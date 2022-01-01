rootProject.name = "kryo-discord-whitelist"

include("velocity")
include("bot")
include("common")

project(":velocity").name = "kryo-discord-whitelist-velocity"
project(":bot").name = "kryo-discord-whitelist-bot"
project(":common").name = "kryo-discord-whitelist-common"
