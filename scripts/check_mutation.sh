#!/usr/bin/env bash
set -euo pipefail

REPORT_DIR="core/target/pit-reports"
BASELINE_FILE=".ci/mutation_baseline.txt"
TOL=0.05   # tolerance in percentage points

# find PIT index.html
INDEX_FILE="$(find "$REPORT_DIR" -name index.html -print -quit || true)"
if [[ -z "${INDEX_FILE:-}" || ! -f "$INDEX_FILE" ]]; then
  echo "❌ No PIT report found under $REPORT_DIR/" >&2
  exit 2
fi

# parse "Mutation Score: NN[.N]%" from index.html
# GNU grep with PCRE (-P) is available on ubuntu-latest
CURRENT_STR="$(grep -Po 'Mutation\s*Score[^0-9]+\K[0-9]+(?:\.[0-9]+)?(?=\s*%)' "$INDEX_FILE" | head -n1 || true)"
if [[ -z "$CURRENT_STR" ]]; then
  echo "❌ Could not parse mutation score from $INDEX_FILE" >&2
  exit 2
fi
CURRENT=$(printf "%.2f" "$CURRENT_STR")
echo "Current mutation score: $CURRENT%"

# initialize baseline if missing
if [[ ! -f "$BASELINE_FILE" ]]; then
  echo "No baseline at $BASELINE_FILE; creating one with current score."
  mkdir -p "$(dirname "$BASELINE_FILE")"
  echo "$CURRENT" > "$BASELINE_FILE"
  exit 0
fi

BASELINE_STR="$(tr -d ' \t\r\n' < "$BASELINE_FILE")"
BASELINE=$(printf "%.2f" "${BASELINE_STR:-0}")
echo "Baseline score: $BASELINE%"

# compare with small tolerance
# use awk for floating point math
DROP=$(awk -v cur="$CURRENT" -v base="$BASELINE" 'BEGIN{print base - cur}')
IS_DROP=$(awk -v d="$DROP" -v tol="$TOL" 'BEGIN{print (d > tol) ? 1 : 0}')

if [[ "$IS_DROP" -eq 1 ]]; then
  echo "❌ Mutation score dropped (${BASELINE}% → ${CURRENT}%)." >&2
  exit 1
fi

echo "✅ Mutation score did not drop."
exit 0
