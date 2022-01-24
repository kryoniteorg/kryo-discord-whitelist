<div align="center">
  <a href="https://github.com/kryoniteorg/kryo-discord-whitelist">
    <img src="https://raw.githubusercontent.com/kryoniteorg/.github/main/assets/kryonite_logo.svg" alt="Kryonite logo" width="80" height="80">
  </a>
</div>

<h1 align="center">kryo-discord-bot</h1>
<div align="center">
    A Discord bot to whitelist players on a <a href="https://github.com/PaperMC/Velocity">Velocity</a> with multi-proxy support.
    <br />
    <br />
    <a href="https://github.com/kryoniteorg/kryo-discord-whitelist/issues/new?assignees=&labels=bug&template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/kryoniteorg/kryo-discord-whitelist/issues/new?assignees=&labels=feature&template=feature_request.md">Request Feature</a>
    <br />
    <br />
    <img alt="Quality Gate Status" src="https://sonarcloud.io/api/project_badges/measure?project=kryoniteorg_kryo-discord-whitelist&metric=alert_status">
    <img alt="Coverage" src="https://sonarcloud.io/api/project_badges/measure?project=kryoniteorg_kryo-discord-whitelist&metric=coverage">
    <img alt="Maintainability Rating" src="https://sonarcloud.io/api/project_badges/measure?project=kryoniteorg_kryo-discord-whitelist&metric=sqale_rating">
    <img alt="Reliability Rating" src="https://sonarcloud.io/api/project_badges/measure?project=kryoniteorg_kryo-discord-whitelist&metric=reliability_rating">
    <img alt="Security Rating" src="https://sonarcloud.io/api/project_badges/measure?project=kryoniteorg_kryo-discord-whitelist&metric=security_rating">
    <br />
    <br />
</div>


## About the project

kryo-discord-whitelist is a whitelist where Discord users can add themselves by writing their username into a specific channel.

## Prerequisites
kryo-discord-whitelist needs the following services to be installed and configured to function properly:

- [MariaDB](https://mariadb.org/) database
- [RabbitMQ](https://www.rabbitmq.com/) message broker

It is not recommended using the root user of the [MariaDB](https://mariadb.org/) server for kryo-discord-whitelist. Please create an extra database with an extra user that is limited to that database.

## Setup
The installation and usage of kryo-discord-whitelist needs to separate modules, the Discord bot itself and a [Velocity](https://github.com/PaperMC/Velocity) plugin. At the moment you need to build all JAR files yourself because we do not distribute prebuilt JARs.  

### Discord bot
The Discord bot needs access to the Discord API, therefore a token is needed. To get a token a Discord bot needs to be created in the Discord developer portal.

1. Goto the [Discord Developer Portal](https://discord.com/developers/applications) and create a new Application.
2. Click on "Bot" in the sidemenu and add a new bot.
3. Click "Copy" next to the profile picture of the bot to copy the token.

With the generated token we can now start the discord bot. The discord bot needs two environment variables or Java start parameters to start.

| Environment variable | Start parameter     | Description                                              |
|----------------------|---------------------|----------------------------------------------------------|
| TOKEN                | -DTOKEN             | Discord Bot token                                        |
| CONNECTION_STRING    | -DCONNECTION_STRING | Connection String for connecting to the MariaDB database |

A startup command could look like the following:
```bash
java -Xms128M -Xmx512M -DTOKEN=DISCORD_TOKEN -DCONNECTION_STRING=jdbc:mariadb://localhost:3306/database?user=user&password=password -JAR kryo-discord-whitelist-bot.JAR
```

### Velocity plugin
To install the [Velocity](https://github.com/PaperMC/Velocity) plugin just copy the JAR-file into the plugin directory.

Furthermore, the [Velocity](https://github.com/PaperMC/Velocity) plugin needs some environment variables or start parameters too.

| Environment variable | Start parameter     | Description                                              |
|----------------------|---------------------|----------------------------------------------------------|
| CONNECTION_STRING    | -DCONNECTION_STRING | Connection String for connecting to the MariaDB database |
| RABBITMQ_ADDRESS     | -DRABBITMQ_ADDRESS  | Address and port of the RabbitMQ message broker          |
| RABBITMQ_USERNAME    | -DRABBITMQ_USERNAME | Username of the RabbitMQ message broker                  |
| RABBITMQ_PASSWORD    | -DRABBITMQ_PASSWORD | Password of the RabbitMQ message broker                  |

A startup command of the [Velocity](https://github.com/PaperMC/Velocity) could look like the following:
```bash
java -Xms128M -Xmx1024M -XX:+UseG1GC -XX:G1HeapRegionSize=4M -XX:+UnlockExperimentalVMOptions -XX:+ParallelRefProcEnabled -XX:+AlwaysPreTouch -XX:MaxInlineLevel=15 -DCONNECTION_STRING=jdbc:mariadb://127.0.0.1:3306/database?user=user&password=password -DRABBITMQ_ADDRESS=127.0.0.1:5672 -DRABBITMQ_USERNAME=guest -DRABBITMQ_PASSWORD=guest -JAR velocity.JAR
```

## Velocity Commands
| Command                        | Permission  | Description                                                                                                                                                                          |
|--------------------------------|-------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `/whitelist add <username>`    | `whitelist` | Add a player manually to the whitelist.<br/><br/>Manually added players do not have to verify themselves via Discord.                                                                |
| `/whitelist remove <username>` | `whitelist` | Remove a player from the whitelist.<br/><br/>If the player is online during the command execution the player gets kicked. This is also applicable if the player is on another proxy. |


## Development

### Building
kryo-discord-whitelist is built with [Gradle](https://gradle.org/). We recommend using the included wrapper script (`./gradlew`) to ensure you use the same [Gradle](https://gradle.org/) version as we do.

To build production-ready JAR files it is sufficient to run `./gradlew shadowJAR`.
You can find the JAR files in `./module-name/build/libs/*-all.JAR`.

### Testing
kryo-discord-whitelist uses JUnit 5 and Mockito for testing. 

To run all tests just execute `./gradlew test`
