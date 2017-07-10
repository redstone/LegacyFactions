# Building

The requirements for building LegacyFactions are:
* Java 8
* Gradle 3.5

Simply execute the following from terminal:

```
gradle build shadow
```

The project will now be built and can be found in `build/libs`! You will want to use the '-all.jar' file as this has dependencies copied into it.

# Common issues

Here are some common issues you may run into while compiling.

## Unsupported method: HierarchicalEclipseProject.getIdentifier().

This is an issue while importing the project into Eclipse. Specifying the version as 3.0 should solve the issue, simply do your compiling from

## Unsupported major.minor version 52.0

Your version of Java is out of date. We currently compile against Java 8!
