# Periscope - RE203

Client of the network project 2024-2025.

## Create an artifact

Create a java artifact in `./build/libs/` named `periscope-1.0-SNAPSHOT.jar`.

```shell
./gradlew build
```

## Run with artifact

Run the java artifact

```shell
java -jar ./build/libs/periscope-1.0-SNAPSHOT.jar
```

## Run without artifact

Run the command

```shell
./gradlew run
```

In case you have an `Executing` printed at the end of the logs, run :

```shell
./gradlew run --console=plain
```
