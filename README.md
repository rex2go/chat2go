![](.github/img/header.jpg)

chat2go is a _lightweight_, easy to use plugin aiming at combining essential chat features in one plugin. It is very configurable.

**This version (1.0-BETA) is not finished. Please report any bugs or contact me if you have any suggestions. Discord: https://discord.gg/Pgf7j8JEsc**

![](.github/img/hello.png)

It is highly recommended to use **Vault** besides your permission plugin to ensure prefix and suffix detection works.
**PlaceholderAPI** is supported as well. **DiscordSRV** is fully supported.

Please do not forget to rate :slightly_smiling_face:

![](.github/img/features.jpg)

**Chat Formatting**

* hex color support
* legacy color support
* PlaceholderAPI support
* custom components (customizable clickable messages also known as json elements, json texts)
* dynamic spacing
* group specific formatting

**Chat Administration**

* blocked words list (bad word list)
* decide between censoring and blocking (or nothing)
* domain / link / ip blocking (whitelist available)
* anti spam (spam blocking)
* chat clear
* broadcast
* mute

**Miscellaneous**

* chat can be fully disabled
* all messages are fully customizable
* join and leave messages can be adjusted or disabled
* world chat
* range chat
* msg command
* broadcast command

Example of 1.16+ hex colors:

![](.github/img/hex_color_example.png)

![](.github/img/commands.jpg)

`/msg <player> <message>`

_**Description**: Private message a player_

_**Alias**: /tell_

_**Permission**: chat2go.command.msg_

`/r <message>`

_**Description**: Reply to a private message_

_**Alias**: /reply_

_**Permission**: chat2go.command.msg_

`/chat`

_**Description**: Manage the plugin_

_**Alias**: /chat2go_

_**Permission**: chat2go.command.chat, chat2go.command.chat.reload, chat2go.command.chat.clear, chat2go.command.chat.badword_

`/force <player> <message|command>`

_**Description**: Forces a player_

_**Permission**: chat2go.command.force_

`/mute <player> <duration> [reason]`

_**Description**: Mute a player_

_**Permission**: chat2go.command.mute_

`/unmute <player>`

_**Description**: Unmute a player_

_**Permission**: chat2go.command.mute_

`/ignore <player>`

_**Description**: Ignore a player_

_**Permission**: chat2go.command.ignore_

`/unignore <player>`

_**Description**: Unignore a player_

_**Permission**: chat2go.command.ignore_

`/ignorelist`

_**Description**: List ignored players_

_**Permission**: chat2go.command.ignore_

`/broadcast <message>`

_**Description**: Broadcasts a message_

_**Alias**: /bcast_

_**Permission**: chat2go.command.broadcast_

![](.github/img/permissions.jpg)

* _'chat2go.chat.color'_: allows you to chat with color
* _'chat2go.bypass.filter'_: bypasses a filter
* _'chat2go.bypass.mute'_: bypasses a mute
* _'chat2go.bypass.antispam'_: bypasses anti spam
* _'chat2go.notify.filter'_: filter notification

other permissions are shown above in the commands section

![](.github/img/coming_up.jpg)

* staff chat
* chat replacements
* chat log
* update notify
* chat mentions
* first join message
* message spy command
* countdown command
* slow mode chat
* offline mail
* custom death messages
* automated broadcasts