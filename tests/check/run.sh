#!/usr/bin/env bash
# Runs the track checker on each case and compares its output and exit code with
# what is expected.
#   run.sh path/to/trackeditor.jar            run the cases
#   run.sh path/to/trackeditor.jar --update   write the expected output again
# Each case is a folder in cases: args.txt has the checker's options and tracks,
# expected.txt its output and exit code. gen.py writes the cases' tracks.
set -u
jar=$(cd "$(dirname "$1")" && pwd)/$(basename "$1")
update=${2:-}
cd "$(dirname "$0")/cases" || exit 2
failed=0
for dir in */; do
	name=${dir%/}
	# args.txt: the checker's options and tracks for the case, on one line
	read -r -a args < "$name/args.txt"
	  (cd "$name" && java -jar "$jar" check --no-prefs "${args[@]}" 2>&1; echo "exit $?") | tr -d "\r" | grep -v "^Picked up " > "$name/actual.txt"
	if [ "$update" = "--update" ]; then
		cp "$name/actual.txt" "$name/expected.txt"
		echo "updated $name"
	elif diff -u <(tr -d '\r' < "$name/expected.txt") "$name/actual.txt"; then
		echo "ok      $name"
	else
		echo "FAILED  $name"
		failed=1
	fi
done
exit $failed
