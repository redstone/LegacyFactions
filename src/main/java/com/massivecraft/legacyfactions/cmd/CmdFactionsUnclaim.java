package com.massivecraft.legacyfactions.cmd;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

import com.massivecraft.legacyfactions.*;
import com.massivecraft.legacyfactions.entity.Board;
import com.massivecraft.legacyfactions.entity.Conf;
import com.massivecraft.legacyfactions.entity.Faction;
import com.massivecraft.legacyfactions.event.EventFactionsLandChange;
import com.massivecraft.legacyfactions.event.EventFactionsLandChange.LandChangeCause;
import com.massivecraft.legacyfactions.integration.vault.VaultEngine;
import com.massivecraft.legacyfactions.task.SpiralTask;

public class CmdFactionsUnclaim extends FCommand {

    public CmdFactionsUnclaim() {
        this.aliases.add("unclaim");
        this.aliases.add("declaim");

        this.optionalArgs.put("radius", "1");
        this.optionalArgs.put("faction", "your");

        this.permission = Permission.UNCLAIM.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        // Read and validate input
        int radius = this.argAsInt(0, 1); // Default to 1
        
        if (radius < 1) {
            msg(TL.COMMAND_CLAIM_INVALIDRADIUS);
            return;
        }

        if (radius < 2) {
            // single chunk
            unClaim(new FLocation(me));
        } else {
            // radius claim
            if (!Permission.CLAIM_RADIUS.has(sender, false)) {
                msg(TL.COMMAND_CLAIM_DENIED);
                return;
            }

            new SpiralTask(new FLocation(me), radius) {
                private int failCount = 0;
                private final int limit = Conf.radiusClaimFailureLimit - 1;

                @Override
                public boolean work() {
                    boolean success = unClaim(this.currentFLocation());
                    if (success) {
                        failCount = 0;
                    } else if (failCount++ >= limit) {
                        this.stop();
                        return false;
                    }

                    return true;
                }
            };
        }
    }

    private boolean unClaim(FLocation target) {
        Faction targetFaction = Board.getInstance().getFactionAt(target);
        if (targetFaction.isSafeZone()) {
            if (Permission.MANAGE_SAFE_ZONE.has(sender)) {
                Board.getInstance().removeAt(target);
                msg(TL.COMMAND_UNCLAIM_SAFEZONE_SUCCESS);

                if (Conf.logLandUnclaims) {
                    Factions.get().log(TL.COMMAND_UNCLAIM_LOG.format(fme.getName(), target.getCoordString(), targetFaction.getTag()));
                }
                return true;
            } else {
                msg(TL.COMMAND_UNCLAIM_SAFEZONE_NOPERM);
                return false;
            }
        } else if (targetFaction.isWarZone()) {
            if (Permission.MANAGE_WAR_ZONE.has(sender)) {
                Board.getInstance().removeAt(target);
                msg(TL.COMMAND_UNCLAIM_WARZONE_SUCCESS);

                if (Conf.logLandUnclaims) {
                    Factions.get().log(TL.COMMAND_UNCLAIM_LOG.format(fme.getName(), target.getCoordString(), targetFaction.getTag()));
                }
                return true;
            } else {
                msg(TL.COMMAND_UNCLAIM_WARZONE_NOPERM);
                return false;
            }
        }

        if (fme.isAdminBypassing()) {
            Map<FLocation, Faction> transactions = new HashMap<FLocation, Faction>();
            
            transactions.put(target, targetFaction);

        	EventFactionsLandChange event = new EventFactionsLandChange(fme, transactions, LandChangeCause.Unclaim);
        	
        	if (event.isCancelled()) return false;
        	
        	for (FLocation location : event.getTransactions().keySet()) {
                Board.getInstance().removeAt(location);
        	}
        	
            targetFaction.msg(TL.COMMAND_UNCLAIM_UNCLAIMED, fme.describeTo(targetFaction, true));
            msg(TL.COMMAND_UNCLAIM_UNCLAIMS);

            if (Conf.logLandUnclaims) {
                Factions.get().log(TL.COMMAND_UNCLAIM_LOG.format(fme.getName(), target.getCoordString(), targetFaction.getTag()));
            }

            return true;
        }

        if (!assertHasFaction()) {
            return false;
        }

        if (!assertMinRole(Role.MODERATOR)) {
            return false;
        }


        if (myFaction != targetFaction) {
            msg(TL.COMMAND_UNCLAIM_WRONGFACTION);
            return false;
        }
        
        Map<FLocation, Faction> transactions = new HashMap<FLocation, Faction>();

        transactions.put(FLocation.valueOf(me.getLocation()), targetFaction);
        
        EventFactionsLandChange event = new EventFactionsLandChange(fme, transactions, LandChangeCause.Unclaim);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        if (VaultEngine.shouldBeUsed()) {
            double refund = VaultEngine.calculateClaimRefund(myFaction.getLandRounded());

            if (Conf.bankEnabled && Conf.bankFactionPaysLandCosts) {
                if (!VaultEngine.modifyMoney(myFaction, refund, TL.COMMAND_UNCLAIM_TOUNCLAIM.toString(), TL.COMMAND_UNCLAIM_FORUNCLAIM.toString())) {
                    return false;
                }
            } else {
                if (!VaultEngine.modifyMoney(fme, refund, TL.COMMAND_UNCLAIM_TOUNCLAIM.toString(), TL.COMMAND_UNCLAIM_FORUNCLAIM.toString())) {
                    return false;
                }
            }
        }

        event.getTransactions().entrySet().stream().forEach(entry -> {
        	FLocation thisLocation = entry.getKey();
        	Faction thisFaction = entry.getValue();
        	
        	Board.getInstance().removeAt(entry.getKey());
        	thisFaction.msg(TL.COMMAND_UNCLAIM_FACTIONUNCLAIMED, fme.describeTo(myFaction, true));
        	

            if (!Conf.logLandUnclaims) return;
            Factions.get().log(TL.COMMAND_UNCLAIM_LOG.format(fme.getName(), thisLocation.getCoordString(), thisFaction.getTag()));
            
        });
        
        return true;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_UNCLAIM_DESCRIPTION;
    }

}
