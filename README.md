<img src="https://raw.githubusercontent.com/redstone/LegacyFactions/master/media/legacyfactions.png" width="500">

# LegacyFactions
A maintained high-performance version of Factions 1.6.

Keeping the same Factions 1.6 experience with some performance enhancements and improved features - which are of course, all optional!

There has been a **huge** (and forever continuing) internal code cleanup. Lots of deprecated code has been removed and optimised. 

## Cost
It's free! Enjoy.

## New Features and Changes
* /f ahome is now /f home <name> <player/faction>: similar to how the current /f sethome works. The permission to use this is factions.home.any.
* Configuration tidy up, all your orignal settings will stick.
* New help menu with new JSON button support (can be disabled)
* Faction warps have passwords (can be disabled with permission)
* Much better API

## Installation
1. Download the latest release.
2. Put LegacyFactions.jar in the plugins folder.
3. Start your server up.
4. Have fun!

A default config file will be created on the first run.

## Upgrading
### FactionsUUID -> Legacy Factions
1) Turn off your server.
2) Backup your original `plugins/FactionsUUID` folder.
3) Delete the jar file for FactionsUUID.
4) Copy LegacyFactions.jar and start your server.
5) Done!

### Factions 1.6 -> Legacy Factions
1) Turn off your server.
2) Backup your original `plugins/Factions` folder.
3) Delete the jar file for Factions 1.6.
4) Rename the `plugins/Factions` folder to `plugins/LegacyFactions`.
5) Copy LegacyFactions.jar and start your server.
6) Done!

## Why another fork?
LegacyFactions was created as the demand for a maintained version of Factions 1.6 increased.

With many bugs that have been reported to the previous fork, my own contact attempts, and over 100 unanswered issue tickets (for a plugin that requires paid support) it became obvious that the plugin was being neglected. Hence, I have picked up development and already fixed several issues.

## License
For licensing concerns please see [LICENSE.md](LICENSE.md)
