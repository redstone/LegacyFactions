package net.redstoneore.legacyfactions.cmd;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.*;
import net.redstoneore.legacyfactions.entity.Board;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.integration.essentials.EssentialsEngine;
import net.redstoneore.legacyfactions.task.SpiralTask;

public class CmdFactionsStuck extends FCommand {

    public CmdFactionsStuck() {
        super();

        this.aliases.add("stuck");
        this.aliases.add("halp!"); // halp! c:

        this.permission = Permission.STUCK.node;
        this.disableOnLock = true;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        final Player player = fme.getPlayer();
        final Location sentAt = player.getLocation();
        final FLocation chunk = fme.getLastStoodAt();
        final long delay = Conf.stuckDelay;
        final int radius = Conf.stuckRadius;

        if (Factions.get().getStuckMap().containsKey(player.getUniqueId())) {
            long wait = Factions.get().getTimers().get(player.getUniqueId()) - System.currentTimeMillis();
            String time = DurationFormatUtils.formatDuration(wait, Lang.COMMAND_STUCK_TIMEFORMAT.toString(), true);
            msg(Lang.COMMAND_STUCK_EXISTS, time);
        } else {

            // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
            if (!payForCommand(Conf.econCostStuck, Lang.COMMAND_STUCK_TOSTUCK.format(fme.getName()), Lang.COMMAND_STUCK_FORSTUCK.format(fme.getName()))) {
                return;
            }

            
            final int id = Bukkit.getScheduler().runTaskLater(Factions.get(), new Runnable() {

                @Override
                public void run() {
                    if (!Factions.get().getStuckMap().containsKey(player.getUniqueId())) {
                        return;
                    }

                    // check for world difference or radius exceeding
                    final World world = chunk.getWorld();
                    if (world.getUID() != player.getWorld().getUID() || sentAt.distance(player.getLocation()) > radius) {
                        msg(Lang.COMMAND_STUCK_OUTSIDE.format(radius));
                        Factions.get().getTimers().remove(player.getUniqueId());
                        Factions.get().getStuckMap().remove(player.getUniqueId());
                        return;
                    }

                    final Board board = Board.get();
                    // spiral task to find nearest wilderness chunk
                    new SpiralTask(new FLocation(me), radius * 2) {

                        @Override
                        public boolean work() {
                            FLocation chunk = currentFLocation();
                            Faction faction = board.getFactionAt(chunk);
                            if (faction.isWilderness()) {
                                int cx = FLocation.chunkToBlock((int) chunk.getX());
                                int cz = FLocation.chunkToBlock((int) chunk.getZ());
                                int y = world.getHighestBlockYAt(cx, cz);
                                Location tp = new Location(world, cx, y, cz);
                                msg(Lang.COMMAND_STUCK_TELEPORT, tp.getBlockX(), tp.getBlockY(), tp.getBlockZ());
                                Factions.get().getTimers().remove(player.getUniqueId());
                                Factions.get().getStuckMap().remove(player.getUniqueId());
                                if (!EssentialsEngine.handleTeleport(player, tp)) {
                                    player.teleport(tp);
                                    Factions.get().debug("/f stuck used regular teleport, not essentials!");
                                }
                                this.stop();
                                return false;
                            }
                            return true;
                        }
                    };
                }
            }, delay * 20).getTaskId();

            Factions.get().getTimers().put(player.getUniqueId(), System.currentTimeMillis() + (delay * 1000));
            long wait = Factions.get().getTimers().get(player.getUniqueId()) - System.currentTimeMillis();
            String time = DurationFormatUtils.formatDuration(wait, Lang.COMMAND_STUCK_TIMEFORMAT.toString(), true);
            msg(Lang.COMMAND_STUCK_START, time);
            Factions.get().getStuckMap().put(player.getUniqueId(), id);
        }
    }

    @Override
    public String getUsageTranslation() {
        return Lang.COMMAND_STUCK_DESCRIPTION.toString();
    }
}
