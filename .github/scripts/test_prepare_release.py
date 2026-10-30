import importlib.util
import os
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

spec = importlib.util.spec_from_file_location("prepare_release", Path(__file__).with_name("prepare-release.py"))
release = importlib.util.module_from_spec(spec)
spec.loader.exec_module(release)


def pom(version, child=False):
    parent = "<parent><version>{}</version></parent>".format(
        version) if child else "<modules><module>dao</module></modules>"
    return '<project xmlns="http://maven.apache.org/POM/4.0.0"><version>{}</version>{}</project>'.format(version,
                                                                                                         parent)


class PrepareReleaseTest(unittest.TestCase):
    def setUp(self):
        self.directory = tempfile.TemporaryDirectory()
        self.previous_directory = Path.cwd()
        os.chdir(self.directory.name)
        Path("dao").mkdir()
        self.write_version("3.0.5")
        self.event = {"ref": "refs/heads/master", "before": "a" * 40}

    def tearDown(self):
        os.chdir(self.previous_directory)
        self.directory.cleanup()

    def write_version(self, version):
        Path("pom.xml").write_text(pom(version))
        Path("dao/pom.xml").write_text(pom(version, child=True))

    def prepare(self, previous="3.0.4", tags="", event_name="push"):
        def fake_git(*args):
            if args[0] == "show":
                self.assertEqual(args[1], self.event["before"] + ":pom.xml")
                return pom(previous)
            self.assertEqual(args, ("tag", "--list"))
            return tags

        with patch.object(release, "git", side_effect=fake_git):
            return release.prepare(event_name, self.event)

    def test_bump_creates_release_candidate(self):
        self.assertEqual(self.prepare()[:2], (True, "v3.0.5"))

    def test_numeric_comparison(self):
        self.write_version("3.0.10")
        self.assertTrue(self.prepare(previous="3.0.9")[0])

    def test_no_release_for_same_or_lower_version(self):
        for previous in ("3.0.5", "3.0.6", "4.0.0"):
            with self.subTest(previous=previous):
                self.assertFalse(self.prepare(previous=previous)[0])

    def test_snapshot_to_final_is_an_increase(self):
        self.assertTrue(self.prepare(previous="3.0.5-SNAPSHOT")[0])

    def test_snapshots_and_prereleases_do_not_auto_publish(self):
        for version in ("3.0.6-SNAPSHOT", "3.0.6-rc1"):
            self.write_version(version)
            self.assertFalse(self.prepare()[0])

    def test_existing_version_tags_prevent_republishing(self):
        for tag in ("v3.0.5", "3.0.5"):
            self.assertFalse(self.prepare(tags=tag)[0])

    def test_first_branch_push_does_not_publish(self):
        self.event["before"] = "0" * 40
        self.assertFalse(self.prepare()[0])

    def test_uncomparable_previous_version_fails(self):
        with self.assertRaisesRegex(ValueError, "Cannot compare"):
            self.prepare(previous="legacy")

    def test_misaligned_module_fails(self):
        Path("dao/pom.xml").write_text(pom("3.0.4", child=True))
        with self.assertRaisesRegex(ValueError, "must match"):
            self.prepare()

    def test_explicit_tag_still_publishes(self):
        self.event["ref"] = "refs/tags/v3.0.5"
        self.assertEqual(self.prepare()[:2], (True, "v3.0.5"))

    def test_wrong_tag_fails(self):
        self.event["ref"] = "refs/tags/v3.0.6"
        with self.assertRaisesRegex(ValueError, "must match"):
            self.prepare()

    def test_manual_release_and_prerelease(self):
        self.event["release"] = {"tag_name": "3.0.5", "prerelease": False}
        self.assertEqual(self.prepare(event_name="release")[:2], (True, "3.0.5"))
        self.event["release"]["prerelease"] = True
        self.assertFalse(self.prepare(event_name="release")[0])


if __name__ == "__main__":
    unittest.main()
