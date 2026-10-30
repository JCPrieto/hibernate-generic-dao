"""Decide whether this event introduces a stable Maven release, without publishing."""
import json
import os
import re
import subprocess
import xml.etree.ElementTree as ET
from pathlib import Path

NS = {"m": "http://maven.apache.org/POM/4.0.0"}


def git(*args):
    return subprocess.check_output(["git"] + list(args), text=True).strip()


def version_of(root):
    return root.findtext("m:version", namespaces=NS)


def stable_version(version):
    if version and re.fullmatch(r"(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)", version):
        return tuple(int(part) for part in version.split("."))
    return None


def prepare(event_name, event):
    root = ET.parse("pom.xml").getroot()
    version = version_of(root)
    if event_name == "push" and event["ref"] == "refs/heads/master":
        current = stable_version(version)
        if current is None:
            return False, "", "POM version is not a stable MAJOR.MINOR.PATCH version"
        before = event["before"]
        if not before.strip("0"):
            return False, "", "Initial branch push has no previous version to compare"
        previous = version_of(ET.fromstring(git("show", before + ":pom.xml")))
        previous_base = stable_version(previous.removesuffix("-SNAPSHOT") if previous else None)
        if previous == version or (previous_base is not None and (
                current < previous_base or (current == previous_base and not previous.endswith("-SNAPSHOT")))):
            return False, "", "Stable version has not increased"
        if previous_base is None:
            raise ValueError("Cannot compare previous POM version: " + str(previous))
        tag = "v" + version
        tags = set(git("tag", "--list").splitlines())
        if tag in tags or version in tags:
            return False, tag, "Version already has a tag"
    else:
        release = event.get("release", {})
        if release.get("prerelease") or release.get("draft"):
            return False, "", "Prereleases and drafts do not publish"
        tag = release.get("tag_name") or event["ref"].removeprefix("refs/tags/")
        if not version or "SNAPSHOT" in version.upper() or tag not in (version, "v" + version):
            raise ValueError("Release tag must match the non-SNAPSHOT POM version")
        if event_name == "push" and (stable_version(version) is None or tag != "v" + version):
            raise ValueError("Automatic releases require a stable vMAJOR.MINOR.PATCH tag")
    for module in root.findall("m:modules/m:module", NS):
        pom = Path(module.text) / "pom.xml"
        child = ET.parse(pom).getroot()
        parent = child.findtext("m:parent/m:version", namespaces=NS)
        child_version = child.findtext("m:version", default=parent, namespaces=NS)
        if parent != version or child_version != version:
            raise ValueError(str(pom) + ": module and parent versions must match " + version)
    return True, tag, "Validated release " + tag


if __name__ == "__main__":
    publish, tag, message = prepare(os.environ["GITHUB_EVENT_NAME"],
                                    json.loads(Path(os.environ["GITHUB_EVENT_PATH"]).read_text()))
    with open(os.environ["GITHUB_OUTPUT"], "a") as output:
        output.write("publish=" + str(publish).lower() + "\ntag=" + tag + "\n")
    print(message)
