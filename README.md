# Conflict in Harmony, Backported

Conflict in Harmony is a mod which solves the problem of multiple actions on the same 
key overwriting each other.

## "Supported" Versions

- 1.7.10
- 1.12.2 (eventually)

## Building (1.7.10)

1. Download Python and JDK 8
2. Clone this repository
3. Run `gradlew buildMod` (or `./gradlew buildMod`)
4. Look in build/libs

## Setting Up a Workspace (1.7.10)

1. Download Python and JDK 8
2. Clone this repository
3. Run `gradlew setupWorkspace` (or `./gradlew setupWorkspace`)

## Info:

Possible question: "Why aren't you telling us to run `build` or `setupDecompWorkspace`?"

Answer: Mixin is *weird.* I don't know how to work with the annotation processor and
Mixin doesn't know what obfuscation mapping is being used. As a result, I'm generating
my own mappings by overriding existing mappings to avoid Mixin remapping. Because of that,
I need something that generates the mappings without completely distributing mappings. I
do that with Python since I know it. See [`gen-mappings.py`](gen-mappings.py) for the
way I do it.

Possible question: "Why are you using an outdated version of Mixin?"

Answer: Mixin 0.8 doesn't support such an old version and its ASM.
