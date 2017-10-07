package net.redstoneore.legacyfactions.integration.conquer.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import ch.njol.skript.Skript;
import ch.njol.yggdrasil.Fields;
import me.andrew28.addons.conquer.api.ConquerFaction;
import me.andrew28.addons.conquer.api.events.ConquerFactionRelationChangeEvent;
import me.andrew28.addons.conquer.api.events.ConquerFactionRelationChangeEvent.Relation;
import net.md_5.bungee.api.chat.BaseComponent;
import net.redstoneore.legacyfactions.Role;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import net.redstoneore.legacyfactions.integration.conquer.ConquerEngine;

public class ConquerFactionImpl extends ConquerFaction {
	
	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	private static transient Map<String, ConquerFactionImpl> factionMap = new ConcurrentHashMap<>();
	
	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static ConquerFactionImpl get(Faction faction) {
		return get(faction.getId());
	}
	
	public static ConquerFactionImpl get(String id) {
		if (FactionColl.get().getFactionById(id) == null) {
			if (factionMap.containsKey(id)) {
				factionMap.remove(id);
			}
			return null;
		}
		
		if (!factionMap.containsKey(id)) {
			factionMap.put(id, new ConquerFactionImpl(id));
		}
		
		return factionMap.get(id);
	}
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	private ConquerFactionImpl(String factionId) {
		this.factionId = factionId;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	private transient String factionId;

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	@Override
	public double getMaximumPower() {
		return FactionColl.get().getFactionById(this.factionId).getPowerMax();
	}

	@Override
	public double getPower() {
		return FactionColl.get().getFactionById(this.factionId).getPower();
	}

	@Override
	public double getPowerBoost() {
		return FactionColl.get().getFactionById(this.factionId).getPowerBoost();
	}

	@Override
	public void addPlayer(OfflinePlayer player) {
		FactionColl.get().getFactionById(this.factionId).memberAdd(FPlayerColl.get(player));
	}

	@Override
	public Date getCreationDate() {
		return new Date(FactionColl.get().getFactionById(this.factionId).getFoundedDate());
	}

	@Override
	public String getDescription() {
		return FactionColl.get().getFactionById(this.factionId).getDescription();
	}

	@Override
	public FactionCommandSender getFactionCommandSender() {
		return new FactionCommandSender() {
			
			@Override
			public void sendMessage(String message) {
				FactionColl.get(factionId).sendMessage(message);
			}

			@Override
			public void sendMessage(String[] messages) {
					FactionColl.get(factionId).sendMessage(Arrays.asList(messages));
			}

			// Don't override to support older versions
			public Spigot spigot() {
				return new Spigot() {
					
					@Override
					public void sendMessage(BaseComponent component) {
						FactionColl.get(factionId).sendMessage(component.toLegacyText());
					}
					
					@Override
					public void sendMessage(BaseComponent... components) {
						for (BaseComponent component : components) {
							FactionColl.get(factionId).sendMessage(component.toLegacyText());
						}
					}
					
				};
			}

		};
	}

	@Override
	public Location getHome() {
		return FactionColl.get().getFactionById(this.factionId).getHome();
	}

	@Override
	public String getIdentifier() {
		return FactionColl.get().getFactionById(this.factionId).getId();
	}

	@Override
	public OfflinePlayer getLeader() {
		return FactionColl.get().getFactionById(this.factionId).getWhereRole(Role.ADMIN).stream()
			.map(fplayer -> fplayer.getPlayer())
			.findFirst()
				.orElse(null);
	}

	@Override
	public String getMotd() {
		Skript.warning(ConquerEngine.NO_MOTD_SUPPORT_WARNING);
		return null;
	}

	@Override
	public String getName() {
		return FactionColl.get().getFactionById(this.factionId).getTag();
	}

	@Override
	public OfflinePlayer[] getPlayers() {
		return FactionColl.get().getFactionById(this.factionId).getFPlayers().stream().map(FPlayer::getPlayer).toArray(OfflinePlayer[]::new);
	}

	@Override
	public Relation getRelationShipTo(ConquerFaction otherFaction) {
		Faction factionsFaction = FactionColl.get().getFactionById(this.factionId);
		Faction factionsFactionTarget = FactionColl.get().getFactionById(otherFaction.getIdentifier());
		ConquerFactionRelationChangeEvent.Relation relation = ConquerFactionRelationChangeEvent.Relation.OTHER;
		switch (factionsFaction.getRelationTo(factionsFactionTarget)) {
			case ALLY:
				relation = ConquerFactionRelationChangeEvent.Relation.ALLY;
				break;
			case TRUCE:
				relation = ConquerFactionRelationChangeEvent.Relation.TRUCE;
				break;
			case NEUTRAL:
				relation = ConquerFactionRelationChangeEvent.Relation.NEUTRAL;
				break;
			case ENEMY:
				relation = ConquerFactionRelationChangeEvent.Relation.ENEMY;
				break;
			case MEMBER:
				relation = ConquerFactionRelationChangeEvent.Relation.MEMBER;
				break;
		}
		return relation;
	}

	@Override
	public void removePlayer(OfflinePlayer player) {
		FactionColl.get().getFactionById(this.factionId).memberRemove(FPlayerColl.get(player));
	}

	@Override
	public Fields serialize() {
		Fields f = new Fields();
		f.putObject("id", this.factionId);
		return f;
	}

	@Override
	public void setCreationDate(Date date) {
		FactionColl.get().getFactionById(this.factionId).setFoundedDate(date.getTime());
	}

	@Override
	public void setDescription(String description) {
		FactionColl.get().getFactionById(this.factionId).setDescription(description);
	}

	@Override
	public void setHome(Location home) {
		FactionColl.get().getFactionById(this.factionId).setHome(home);
	}

	@Override
	public void setIdentifier(String arg0) {
		Skript.warning(ConquerEngine.NO_SETID_SUPPORT_WARNING);
	}

	@Override
	public void setLeader(OfflinePlayer leader) {
		if (FactionColl.get().getFactionById(this.factionId).getOwner() != null) {
			FactionColl.get().getFactionById(this.factionId).getOwner().setRole(Role.NORMAL);
		}
		
		FPlayerColl.get(leader).setRole(Role.ADMIN);
	}

	@Override
	public void setMotd(String arg0) {
		Skript.warning(ConquerEngine.NO_MOTD_SUPPORT_WARNING);
	}

	@Override
	public void setName(String tag) {
		FactionColl.get().getFactionById(this.factionId).setTag(tag);
	}

	@Override
	public void setPowerBoost(Double powerBoost) {
		FactionColl.get().getFactionById(this.factionId).setPowerBoost(powerBoost);
	}

}
