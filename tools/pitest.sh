#!/usr/bin/env bash
# Reusable pitest runner: runs mutation coverage (JDK 11) and prints a compact
# summary plus one line per SURVIVED / NO_COVERAGE mutant.
#
# Usage:
#   tools/pitest.sh                       # full suite
#   tools/pitest.sh 'com.…CashRounding*'  # narrowed targetClasses
#   tools/pitest.sh 'com.…CashRounding*' 'com.…CashRoundingTest*'
set -euo pipefail

export JAVA_HOME="${JAVA_HOME:-$HOME/.local/share/mise/installs/java/temurin-11}"

ARGS=(-o -q org.pitest:pitest-maven:mutationCoverage -DoutputFormats=XML -Dpit.timestampedReports=false)
if [[ $# -ge 1 && -n "$1" ]]; then ARGS+=("-DtargetClasses=$1"); fi
if [[ $# -ge 2 && -n "$2" ]]; then ARGS+=("-DtargetTests=$2"); fi

./mvnw "${ARGS[@]}" || echo "### maven exited non-zero (threshold or failure) ###"

REPORT=$(find target/pit-reports -name mutations.xml | head -n1)
if [[ -z "$REPORT" ]]; then echo "no mutations.xml found"; exit 1; fi

python3 - "$REPORT" <<'PY'
import sys, collections, xml.etree.ElementTree as ET
root = ET.parse(sys.argv[1]).getroot()
muts = root.findall('mutation')
counts = collections.Counter(m.get('status') for m in muts)
total = len(muts)
killed = counts['KILLED'] + counts['TIMED_OUT'] + counts['MEMORY_ERROR']
print(f"total={total} " + " ".join(f"{k}={v}" for k, v in sorted(counts.items())))
print(f"mutation score = {100.0*killed/total:.1f}%" if total else "no mutations")
bad = [m for m in muts if m.get('status') in ('SURVIVED', 'NO_COVERAGE')]
print(f"--- {len(bad)} SURVIVED/NO_COVERAGE ---")
def t(m, n): 
    e = m.find(n); return e.text if e is not None else '?'
for m in sorted(bad, key=lambda m: (t(m,'mutatedClass'), int(t(m,'lineNumber')))):
    print(f"{m.get('status'):11} {t(m,'mutatedClass').split('.')[-1]}.{t(m,'mutatedMethod')}:{t(m,'lineNumber')} "
          f"[{t(m,'mutator').split('.')[-1]}] {t(m,'description')}")
PY
