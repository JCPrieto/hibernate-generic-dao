#!/usr/bin/env bash

set -euo pipefail

LOG_FILE="${1:-build.log}"
OUTPUT_DIR="${2:-baseline-metrics}"
BUILD_STATUS="${3:-UNKNOWN}"
BUILD_DURATION_SECONDS="${4:-0}"

mkdir -p "${OUTPUT_DIR}"

CLEAN_LOG="${OUTPUT_DIR}/build-clean.log"
if [[ -f "${LOG_FILE}" ]]; then
  sed -r 's/\x1B\[[0-9;]*[mK]//g' "${LOG_FILE}" > "${CLEAN_LOG}"
else
  : > "${CLEAN_LOG}"
fi

warn_count=$(grep -c "\\[WARNING\\]" "${CLEAN_LOG}" || true)
error_count=$(grep -c "\\[ERROR\\]" "${CLEAN_LOG}" || true)
total_time=$(grep "Total time:" "${CLEAN_LOG}" | tail -1 | sed -E 's/.*Total time:[[:space:]]*//' || true)
finished_at=$(grep "Finished at:" "${CLEAN_LOG}" | tail -1 | sed -E 's/.*Finished at:[[:space:]]*//' || true)

tests_run=0
tests_failures=0
tests_errors=0
tests_skipped=0

while IFS= read -r line; do
  run=$(echo "${line}" | sed -E 's/.*Tests run: ([0-9]+), Failures: ([0-9]+), Errors: ([0-9]+), Skipped: ([0-9]+).*/\1/' || echo 0)
  failures=$(echo "${line}" | sed -E 's/.*Tests run: ([0-9]+), Failures: ([0-9]+), Errors: ([0-9]+), Skipped: ([0-9]+).*/\2/' || echo 0)
  errors=$(echo "${line}" | sed -E 's/.*Tests run: ([0-9]+), Failures: ([0-9]+), Errors: ([0-9]+), Skipped: ([0-9]+).*/\3/' || echo 0)
  skipped=$(echo "${line}" | sed -E 's/.*Tests run: ([0-9]+), Failures: ([0-9]+), Errors: ([0-9]+), Skipped: ([0-9]+).*/\4/' || echo 0)
  tests_run=$((tests_run + run))
  tests_failures=$((tests_failures + failures))
  tests_errors=$((tests_errors + errors))
  tests_skipped=$((tests_skipped + skipped))
done < <(grep '^Tests run:' "${CLEAN_LOG}" || true)

cat > "${OUTPUT_DIR}/baseline-metrics.json" <<EOF
{
  "build_status": "${BUILD_STATUS}",
  "build_duration_seconds": ${BUILD_DURATION_SECONDS},
  "maven_total_time": "${total_time}",
  "finished_at": "${finished_at}",
  "warnings": ${warn_count},
  "errors": ${error_count},
  "tests": {
    "run": ${tests_run},
    "failures": ${tests_failures},
    "errors": ${tests_errors},
    "skipped": ${tests_skipped}
  }
}
EOF

cat > "${OUTPUT_DIR}/baseline-summary.md" <<EOF
# Baseline tecnico CI

- Estado build: ${BUILD_STATUS}
- Duracion (s): ${BUILD_DURATION_SECONDS}
- Maven total time: ${total_time}
- Finished at: ${finished_at}
- Warnings: ${warn_count}
- Errors: ${error_count}
- Tests run: ${tests_run}
- Test failures: ${tests_failures}
- Test errors: ${tests_errors}
- Tests skipped: ${tests_skipped}
EOF
