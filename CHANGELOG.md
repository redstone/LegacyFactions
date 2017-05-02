# 1.1.1
* Improvements to max relations (you will need to redo these in the conf.json file)
* New API Method: Faction#hasMaxRelations
* Error message now shows with relation command if we can't find the faction. It used to do nothing.

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
