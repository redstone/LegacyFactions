package net.redstoneore.legacyfactions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.redstoneore.legacyfactions.*;
import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholder;
import net.redstoneore.legacyfactions.placeholder.FactionsPlaceholders;

import java.util.UnknownFormatConversionException;
import java.util.logging.Level;

public class FactionsChatListener implements Listener {

	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static FactionsChatListener i = new FactionsChatListener();
	public static FactionsChatListener get() { return i; }
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	// onFactionChat handles public, faction, alliance, and truce chat
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onFactionChat(AsyncPlayerChatEvent event) {
		FPlayer sender = FPlayerColl.get(event.getPlayer());
		Faction senderFaction = sender.getFaction();
		ChatMode chatMode = sender.getChatMode();
		String rawMessage = event.getMessage();
		
		switch (chatMode) {
		case PUBLIC:
			// If we're formatting public chat, do something here 
			if (!Conf.enableChatFormatPublic) return;
			
			FPlayerColl.rewrap(event.getRecipients()).forEach(receiver -> {
				String chatFormat = Conf.chatFormatPublic.toString();
				
				receiver.sendMessage(this.factionChatPlaceholders(sender, receiver, chatFormat, rawMessage));
			});
			
			event.setCancelled(true);
			return;
		case ALLIANCE:
			String allianceChatFormat = Conf.chatFormatAlliance.toString();
			
			// First send to our online members
			senderFaction.getWhereOnline(true).forEach(receiver -> {
				receiver.sendMessage(this.factionChatPlaceholders(sender, receiver, allianceChatFormat, rawMessage));
			});
			
			FPlayerColl.all(true, receiver -> {
				if (receiver.getFaction().getRelationTo(senderFaction) == Relation.TRUCE) {
					// Now send to allies 
					receiver.sendMessage(this.factionChatPlaceholders(sender, receiver, allianceChatFormat, rawMessage));
				} else {
					// Now send to person spying on chat 
					if (receiver.getFactionId() != senderFaction.getId() && receiver.isSpyingChat()) {
						receiver.sendMessage(this.factionChatPlaceholders(sender, receiver, ChatColor.GRAY + "(fcSpy: alliance) " + ChatColor.RESET + allianceChatFormat, rawMessage));
					}
				}
			});
			
			event.setCancelled(true);
			return;
			
		case FACTION:
			String factionChatFormat = Conf.chatFormatFaction.toString();
			
			// Send to our online members
			senderFaction.getWhereOnline(true).forEach(receiver -> {
				receiver.sendMessage(this.factionChatPlaceholders(sender, receiver, factionChatFormat, rawMessage));
			});
			
			FPlayerColl.all(true, receiver -> {
				// Now send to person spying on chat 
				if (receiver.getFactionId() != senderFaction.getId() && receiver.isSpyingChat()) {
					receiver.sendMessage(this.factionChatPlaceholders(sender, receiver, ChatColor.GRAY + "(fcSpy: faction) " + ChatColor.RESET + factionChatFormat, rawMessage));
				}				
			});
		
			event.setCancelled(true);
			return;
		case TRUCE:
			String truceChatFormat = Conf.chatFormatTruce.toString();
			
			// First send to our online members
			senderFaction.getWhereOnline(true).forEach(receiver -> {
				receiver.sendMessage(this.factionChatPlaceholders(sender, receiver, truceChatFormat, rawMessage));
			});
			
			FPlayerColl.all(true, receiver -> {
				if (receiver.getFaction().getRelationTo(senderFaction) == Relation.TRUCE) {
					// Now send to allies 
					receiver.sendMessage(this.factionChatPlaceholders(sender, receiver, truceChatFormat, rawMessage));
				} else {
					// Now send to person spying on chat 
					if (receiver.getFactionId() != senderFaction.getId() && receiver.isSpyingChat()) {
						receiver.sendMessage(this.factionChatPlaceholders(sender, receiver, ChatColor.GRAY + "(fcSpy: truce) " + ChatColor.RESET + truceChatFormat, rawMessage));
					}
				}
			});
			
			event.setCancelled(true);
			return;
		}
	}

	// this is for handling insertion of the player's faction tag, set at highest priority to give other plugins a chance to modify chat first
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		// Are we to insert the Faction tag into the format?
		// If we are not to insert it - we are done.
		if (!Conf.chatTagEnabled || Conf.chatTagHandledByAnotherPlugin) {
			return;
		}

		Player talkingPlayer = event.getPlayer();
		String msg = event.getMessage();
		String eventFormat = event.getFormat();
		FPlayer me = FPlayerColl.get(talkingPlayer);
		int InsertIndex;

		if (!Conf.chatTagReplaceString.isEmpty() && eventFormat.contains(Conf.chatTagReplaceString)) {
			// we're using the "replace" method of inserting the faction tags
			if (eventFormat.contains("[FACTION_TITLE]")) {
				eventFormat = eventFormat.replace("[FACTION_TITLE]", me.getTitle());
			}
			InsertIndex = eventFormat.indexOf(Conf.chatTagReplaceString);
			eventFormat = eventFormat.replace(Conf.chatTagReplaceString, "");
			Conf.chatTagPadAfter = false;
			Conf.chatTagPadBefore = false;
		} else if (!Conf.chatTagInsertAfterString.isEmpty() && eventFormat.contains(Conf.chatTagInsertAfterString)) {
			// we're using the "insert after string" method
			InsertIndex = eventFormat.indexOf(Conf.chatTagInsertAfterString) + Conf.chatTagInsertAfterString.length();
		} else if (!Conf.chatTagInsertBeforeString.isEmpty() && eventFormat.contains(Conf.chatTagInsertBeforeString)) {
			// we're using the "insert before string" method
			InsertIndex = eventFormat.indexOf(Conf.chatTagInsertBeforeString);
		} else {
			// we'll fall back to using the index place method
			InsertIndex = Conf.chatTagInsertIndex;
			if (InsertIndex > eventFormat.length()) {
				return;
			}
		}

		String formatStart = eventFormat.substring(0, InsertIndex) + ((Conf.chatTagPadBefore && !me.getChatTag().isEmpty()) ? " " : "");
		String formatEnd = ((Conf.chatTagPadAfter && !me.getChatTag().isEmpty()) ? " " : "") + eventFormat.substring(InsertIndex);

		String nonColoredMsgFormat = formatStart + me.getChatTag().trim() + formatEnd;

		// Relation Colored?
		if (Conf.chatTagRelationColored) {
			// We must choke the standard message and send out individual messages to all players
			// Why? Because the relations will differ.
			event.setCancelled(true);

			event.getRecipients().forEach(listeningPlayer -> {
				FPlayer you = FPlayerColl.get(listeningPlayer);
				String yourFormat = formatStart + me.getChatTag(you).trim() + formatEnd;
				try {
					listeningPlayer.sendMessage(String.format(yourFormat, talkingPlayer.getDisplayName(), msg));
				} catch (UnknownFormatConversionException ex) {
					Conf.chatTagInsertIndex = 0;
					Factions.get().error("Critical error in chat message formatting!");
					Factions.get().error("NOTE: This has been automatically fixed right now by setting chatTagInsertIndex to 0.");
					Factions.get().error("For a more proper fix, please read this regarding chat configuration: http://massivecraft.com/plugins/factions/config#Chat_configuration");
					return;
				}
			});
			
			// Write to the log... We will write the non colored message.
			String nonColoredMsg = ChatColor.stripColor(String.format(nonColoredMsgFormat, talkingPlayer.getDisplayName(), msg));
			Bukkit.getLogger().log(Level.INFO, nonColoredMsg);
		} else {
			// No relation color.
			event.setFormat(nonColoredMsgFormat);
		}
	}
	
	private String factionChatPlaceholders(FPlayer sender, FPlayer receiver, String format, String message) {
		format = format.replaceAll("<fc_faction_tag>", sender.getTag());
		format = format.replaceAll("<fc_role>", sender.getRole().toNiceName());
		format = format.replaceAll("<fc_role_prefix>", sender.getRole().getPrefix());
		format = format.replaceAll("<fc_message>", message);
		
		// Relation related
		format = format.replaceAll("<fc_relation>", ChatColor.stripColor(receiver.getFaction().getRelationTo(sender.getFaction()).toNiceName()));
		format = format.replaceAll("<fc_player_relation>", sender.describeTo(receiver));
		
		// Our Placeholders
		for (FactionsPlaceholder placeholder : FactionsPlaceholders.get().getPlaceholders()) {
			format = format.replaceAll("<factions_"+placeholder.placeholder()+">", placeholder.get(sender.getPlayer()));
		}
		
		return format;
	}
	
}
