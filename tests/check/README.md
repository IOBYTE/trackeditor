# Track checker tests

`run.sh path/to/trackeditor.jar` runs the checker on every folder in `cases` and
compares its output and exit code with the case's `expected.txt`.
`run.sh path/to/trackeditor.jar --update` writes the expected output again, after a
message is reworded or a check is added: review the changes before committing them.

A case has `args.txt` (the checker's options and tracks, on one line), the track's
folder, and `expected.txt`. The tracks are one small oval and copies of it with one
thing wrong each. `gen.py` writes them (`python3 gen.py`): add a case to its `CASES`
list, run it, then run `run.sh ... --update`.
