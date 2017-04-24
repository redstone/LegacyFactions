# LegacyFactions
A maintained high-performance version of Factions 1.6.

## Why?
LegacyFactions was created as the demand for a maintained version of Factions 1.6 increased.

With many bugs that have been reported to the previous fork, my own contact attempts, and over 100 unanswered issue tickets (for a plugin that requires paid support) it became obvious that the plugin was being neglected. Hence, I have picked up development and already fixed several issues:

* Inconsistent and restrictive API/Events
* Several performance imporvements  
* Better plugin integration, and more integrations
* Inability to easily upgrade to newer versions of Factions
* Abundance of messy and unnecessary configuration files  

## Minor differences from FactionsUUID
There are a lot of minor differences from FactionsUUID:

* Faster boot and execution times: The source based has been optimised a lot, this was one of the reasons the 1.x base was abandonded
* Tidy config files: we will migrate to the easier-to-read conf.json file, the json file also comes documented 
* /f ahome is now /f home <name> <player/faction>: similar to the current /f sethome. The permission is factions.home.any 

## Major differences
* New help menu with new JSON support
* Faction warps have passwords

## Upgrading
### FactionsUUID -> Legacy Factions
1) Turn off your server
2) Backup your original `plugins/FactionsUUID` folder
3) Delete the jar file for FactionsUUID
4) Copy LegacyFactions.jar and start your server
5) Done

### Factions 1.6 -> Legacy Factions
1) Turn off your server
2) Backup your original `plugins/Factions` folder
3) Delete the jar file for Factions 1.6
4) Rename the `plugins/Faction` folder to `plugins/LegacyFactions`
5) Copy LegacyFactions.jar and start your server
6) Done

## License
For licensing concerns please see [LICENSE.md](LICENSE.md)

## Installation
1. Download the latest release 
2. Put LegacyFactions.jar in the plugins folder.

A default config file will be created on the first run.
