#!/usr/bin/env sh

# Option                                    Description
# ------                                    -----------
# -?, -h, --help                            Shows the help menu
# --all [ReleaseType]                       Generate all known versions (with optional filter)
# -g, --generator [Generator]               Type of generator to run
#                                           [generic, json, neoforge, forgecraft]
# -l, --list [ReleaseType]                  List all known versions (with optional filter)
# -o, --out <Path>                          Directory to store generated files (default './out')
# -v, --version <Id>                        Version id to generate
# --versions <File>                         Custom version(s).json file

# Generator: [ generic, json, neoforge, forgecraft ]
# ReleaseType: [ RELEASE, RELEASE_CANDIDATE, PRE_RELEASE, SNAPSHOT ]

# -g json --all | dump all known versions to json
# --versions './path/to/my/versions.json' | specify custom file to parse version metadata from
java -jar ./build/libs/snapshotgen-9.9.999-all.jar --versions ./src/main/resources/versions.json "$@"
