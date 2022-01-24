<p style="text-align: center">
  <a href="https://github.com/kryoniteorg/kryo-discord-whitelist">
    <img src="https://raw.githubusercontent.com/kryoniteorg/.github/main/assets/kryonite_logo.svg" alt="Kryonite logo" width="80" height="80">
  </a>
</p>

<h1 style="text-align: center">kryo-discord-bot</h1>
<p style="text-align: center">
    A Discord bot to whitelist players on a Velocity proxy with multi-proxy support.
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
</p>


## About the project

kryo-discord-whitelist is a whitelist where Discord users can add themselves by writing their username into a specific channel.

## Prerequisites
kryo-discord-whitelist needs the following services to be installed and configured to function properly:

- MariaDB database
- RabbitMQ message broker

It is not recommended using the root user of the MariaDB server for kryo-discord-whitelist. Please create an extra database with an extra user that is limited to that database.

## Setup

### Discord bot
The Discord bot needs access to the Discord api, therefore a token is needed. To get a token a Discord bot needs to be created in the Discord developer portal.

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
java -Xms128M -Xmx512M -DTOKEN=DISCORD_TOKEN -DCONNECTION_STRING=jdbc:mariadb://localhost:3306/database?user=user&password=password -jar kryo-discord-whitelist-bot.jar
```

### Velocity plugin
To install the Velocity plugin just copy the jar-file into the plugin directory.

Furthermore, the Velocity plugin needs some environment variables or start parameters too.

| Environment variable | Start parameter     | Description                                              |
|----------------------|---------------------|----------------------------------------------------------|
| CONNECTION_STRING    | -DCONNECTION_STRING | Connection String for connecting to the MariaDB database |
| RABBITMQ_ADDRESS     | -DRABBITMQ_ADDRESS  | Address and port of the RabbitMQ message broker          |
| RABBITMQ_USERNAME    | -DRABBITMQ_USERNAME | Username of the RabbitMQ message broker                  |
| RABBITMQ_PASSWORD    | -DRABBITMQ_PASSWORD | Password of the RabbitMQ message broker                  |

A startup command of the Velocity proxy could look like the following:
```bash
java -Xms128M -Xmx1024M -XX:+UseG1GC -XX:G1HeapRegionSize=4M -XX:+UnlockExperimentalVMOptions -XX:+ParallelRefProcEnabled -XX:+AlwaysPreTouch -XX:MaxInlineLevel=15 -DCONNECTION_STRING=jdbc:mariadb://127.0.0.1:3306/database?user=user&password=password -DRABBITMQ_ADDRESS=127.0.0.1:5672 -DRABBITMQ_USERNAME=guest -DRABBITMQ_PASSWORD=guest -jar velocity.jar
```

## Velocity Commands
| Command                        | Permission  | Description                                                                                                                                                                          |
|--------------------------------|-------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `/whitelist add <username>`    | `whitelist` | Add a player manually to the whitelist.<br/><br/>Manually added players do not have to verify themselves via Discord.                                                                |
| `/whitelist remove <username>` | `whitelist` | Remove a player from the whitelist.<br/><br/>If the player is online during the command execution the player gets kicked. This is also applicable if the player is on another proxy. |
