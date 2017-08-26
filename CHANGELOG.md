# 1.4.3
* 🐞 Bugfix-ish: Database structure has change, backup before updating!
* 🐞 Bugfix: Fixed a bug with script support generating blank comparison strings
* 🐞 Bugfix: None the less, Placeholders work 100% now
* ⭐️ Feature: New placeholder `factions_player_chattag`
* ⭐️ Feature: Now using Java NIO2 - I/O operations should be quicker
* ⭐️ Feature: JSON file is now lenient (if we can be)
* ⭐️ Feature: New `/f style` command for changing map colours and characters of factions
* ⭐️ Feature: New `/f ban` command to stop players from rejoining a faction
* ⭐️ Feature: New config option: `damageModifierPercentRelationPlayer`
* ⭐️ Feature: New config option: `damageModifierPercentRelationLocationByPlayer`
* ⭐️ Feature: New config option: `damageModifierPercentRelationLocationByMob`
* ⭐️ Feature: New config option: `damageModifierPercentWilderness`
* ⭐️ Feature: New config option: `damageModifierPercentSafezone`
* ⭐️ Feature: New config option: `damageModifierPercentWarzone`
* ⭐️ Feature: New config option: `factionDescriptionLengthMax`
* ⭐️ Feature: New _blankwild placeholders (see docs)
* ⭐️ Feature: New config option: `territoryChangeText`
* ⌨️ API: `Faction#hasForcedMapCharacter` method added
* ⌨️ API: `Faction#setForcedMapCharacter` method added
* ⌨️ API: `Faction#getForcedMapCharacter` method added
* ⌨️ API: `Faction#hasForcedMapColour` method added
* ⌨️ API: `Faction#setForcedMapColour` method added
* ⌨️ API: `Faction#getForcedMapColour` method added
* ⌨️ API: `Faction#ban` method added
* ⌨️ API: `Faction#unban` method added
* ⌨️ API: `Faction#isbanned` method added
* ⌨️ API: Event `EventFactionsBan` added
* ⌨️ API: Event `EventFactionsUnban` added
* ⌨️ API: Event `EventFactionsRoleChanged` added
* ⌨️ API: Event `EventFactionsChangeTerritory` added
* ⌨️ API: New LangBuilder class
* 📚 Docs: Multilingual Script Support wiki page added
* 📚 Docs: Placeholder docs updated
* 📊 Stats: Now storing stats about factions, warps, claims, and expansions! 

# 1.4.2
* 🐞 Bugfix: PlaceholderAPI wasn't working properly, but it works now! 
* 🐞 Bugfix: Factionless Scoreboards were not refreshing
* 🐞 Bugfix: `factions_faction_admin` placeholder would throw an NPE if the faction didn't have a leader/admin
* 🐞 Bugfix: Offline players didn't work too well with kick/join/who/owner - but thats been fixed now
* 🐞 Bugfix: Lots of language fields were missing that have now been added
* ⭐️ Feature: Relational placeholders added!
* ⭐️ Feature: FactionChat has been revamped, and should perform better now.
* ⭐️ Feature: FactionChat now allows you to customise public chat (for small servers) enable `enableChatFormatPublic` and use  `chatFormatPublic`
* ⭐️ Feature: Armorstands are now protected against breakage
* ⭐️ Feature: Faction chat placeholders improved
* ⭐️ Feature: `allowColorCodesInFaction` has been removed and split into `allowColourCodesInFactionTitle` and `allowColourCodesInFactionDescription`
* ⭐️ Feature: `enabledScriptSupport` has been added. It allows you to enable other unicode scripts for use in titles and descriptions.
* ⭐️ Feature: Territory Titles! `territoryTitlesShow` has been added to enable it. Customise using the new conf options `territoryTitlesHeader`, `territoryTitlesFooter`, `territoryTitlesTimeFadeInTicks`, `territoryTitlesTimeStayTicks`, `territoryTitlesTimeFadeOutTicks`
* 🎁 FactionsFly - new expansion!
* 🎁 FactionsFly ⭐️ Feature: New config options `factionsFlyExpansionEnabled`, `factionsFlyNoEnderpearl`, `factionsFlyNoChorusFruit`, `factionsFlyMaxY`, `factionsFlyNoFirstFallDamage`, `factionsFlyTeleportToFloorOnDisable`
* 🎁 FactionsChat - now a expansion!
* 🎁 FactionsChat ⭐️ Feature: New config options `factionsChatExpansionEnabled`, `factionsChatEnableAllianceChat`, `factionsChatEnableTruceChat`, `factionsChatEnableFormatPublicChat`, `factionsChatFormatPublic`, `factionsChatFormatFaction`, `factionsChatFormatAlliance`, `factionsChatFormatTruce`, `factionsChatFormatSpy`, `factionChatChannelUse`, `factionChatChannelGlobal`, `chatTagEnabled`, `chatTagRelationalOverride`, `chatTagPlaceholder`, `chatTagFormatDefault`, `chatTagFormatFactionless`
* 🇨🇳 Languages: Chinese Language added!
* 📚 Docs: Placeholder wiki page updated
* 📚 Docs: Expansions wiki page added
* 📚 Docs: FactionsFly wiki page added
* 📚 Docs: FactionsChat wiki page added
* 📚 Docs: What's the difference? wiki page added
* ⌨️ API: `Faction#sendPlainMessage` method added, to send unformatted messages
* ⌨️ API: `TitleUtil` class added for managing titles
* ⌨️ API: `substanceChars` has been removed and replaced with `englishCharacters`, it has been switched to private
* ⌨️ API: `Locality` class introduced, it is due to be adopted as a replacement for FLocation
* ⌨️ API: JavaDocs Improved
* ⌨️ API: Callback classes added
* ⌨️ API: PlayerMixin updated to include action checks

# 1.4.1
* 🐞 Bugfix: Scoreboard issues 
* ⭐️ Feature: new `/f debug` command
* ⌨️ API: Internal economy API rebuilt, economy issues should be gone
* ⌨️ API: VaultAccount is now a nicer way to use economy! Enjoy! 
* ⌨️ API: `msg` is ambiguous and has been deprecated and replaced with `sendMessage`
* ⌨️ API: More JavaDocs have been added!
* 📚 Docs: Scoreboard wiki has been updated

# 1.4.0
* Improvement: Added coleader, you can disable it by not giving the `/f coleader` permission
* Improvement: Allow server owners to let factions use colour codes in /f title and /f desc
* Improvement: Add ability for server owners to toggle the ability for factions to color the desc and title.
* Improvement: Added truces
* Bugfix: Fix NPE when running /f mod without args (#17)
* Bugfix: Scoreboard bug - the scoreboard could be blank on first join
* Docs: Documentation Created
* Internal code cleanup

# 1.2.1
* API: New API Event: EventFactionsChatModeChange
* API: ChatMode, FLocation and Relation improvements
* Bugfix: Placeholders sometimes didn't work
* Improvement: Placeholders now support HolographicDisplays
* Improvement: chat command can be routed to VentureChat with new conf option factionChatChannelRoute

# 1.2.0
* Feature: Command aliases now configurable in conf.json
* Bugfix: Error message now shows with relation command if we can't find the faction. It used to do nothing.
* Bugfix: Enums are no longer dumb with Gson
* Bugfix: Loads of performance improvements 
* Bugfix: More translations added 
* Bugfix: Fixed an upstream bug with invalid/null arrays breaking commands
* Bugfix: Fixed an upstream bug where the console couldn’t list factions
* Bugfix: Fixed deprecated code thats removed in new build of Spigot
* Improvement: Config.yml is no longer used, it is now all in the one configuration file (conf.json)
* Improvement: Improvements to max relations (you will need to redo these in the conf.json file)
* Improvement: Placeholders across major placeholder plugins: https://github.com/redstone/LegacyFactions/wiki/Placeholders
* API: New API Method: Faction#hasMaxRelations
* API: New API Event: EventFactionsWarpUse
* API: New Placeholder API

# 1.1.0
* New API Event: EventFactionsWarpCreate
* New API Event: EventFactionsWarpDelete
* 'top' command now shows buttons when there is no criteria specified. 
* Intgration with MVdW Placeholders API
* Lots of internal improvements
* Fix an upstream language bug in #919 (https://github.com/drtshock/Factions/issues/919)
* Fix an upstream bug for isFactionsCommand
* Fixes a bug with directory not existing
* Adding permission factions.warp.passwords
