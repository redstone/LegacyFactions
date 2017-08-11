package net.redstoneore.legacyfactions.scoreboards.sidebar;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.redstoneore.legacyfactions.entity.Conf;
import net.redstoneore.legacyfactions.entity.FPlayer;
import net.redstoneore.legacyfactions.scoreboards.FSidebarProvider;

public class FDefaultSidebar extends FSidebarProvider {
	
	@Override
	public String getTitle(FPlayer fplayer) {
		return replaceTags(fplayer, Conf.scoreboardDefaultTitle + "");
	}

	@Override
	public List<String> getLines(FPlayer fplayer) {
		if (fplayer.hasFaction()) {
			return this.getOutput(fplayer, new ArrayList<>(Conf.scoreboardDefault));
		} else {
			if (Conf.scoreboardFactionlessEnabled) {
				return this.getOutput(fplayer, new ArrayList<>(Conf.scoreboardFactionless));
			}
		}
		return this.getOutput(fplayer, new ArrayList<>(Conf.scoreboardDefault)); // no faction, factionless-board disabled
	}
	
	/**
	 * Get the output for these lines
	 * @param fplayer to get output for
	 * @param lines to render
	 * @return
	 */
	public List<String> getOutput(FPlayer fplayer, List<String> lines) {
		
		if (lines == null || lines.isEmpty()) return new ArrayList<>();

		ListIterator<String> linesIterator = lines.listIterator();
		
		while (linesIterator.hasNext()) {
			linesIterator.set(this.replaceTags(fplayer, linesIterator.next()));
		}
		return lines;
	}
	
}
