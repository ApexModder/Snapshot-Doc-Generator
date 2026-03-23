#!/usr/bin/env sh

# Option                    Description
# ------                    -----------
# -?, -h, --help            Shows the help menu
# --all                     Generate all known versions
# -g, --generator <String>  Generator type to run (default: generic)
# -l, --list                List all known versions
# -o, --out <Path>          Directory to store generated files (default: out)
# -v, --version <String>    Version id to process
# --versions <File>         Custom versions.json file

# Generator: [ generic, json, neoforge, forgecraft ]
# ReleaseType: [ RELEASE, RELEASE_CANDIDATE, PRE_RELEASE, SNAPSHOT ]

# -g json --all | dump all known versions to json
# --versions './path/to/my/versions.json' | specify custom file to parse version metadata from
java -jar ./build/libs/snapshotgen-9.9.999-all.jar --versions ./src/main/resources/versions.json "$@"
