# Snapshot-Doc-Gen

Very basic application used to generate my Minecraft Snapshot/Release Discord annoucement messages.

No published builds are currently available, project must be cloned and built from source.

`gradlew :build`

To generate a NeoForge snapshots thread message execute the following command

`./run.sh -g neoforge -v {VERSION}`

Replace `{VERSION}` with the version you wish to generate

Metadata for said version must exist in `src/main/resources/versions.json`