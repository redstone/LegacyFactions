package net.redstoneore.legacyfactions.expansion;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.redstoneore.legacyfactions.expansion.chat.FactionsChat;
import net.redstoneore.legacyfactions.expansion.fly.FactionsFly;

public class FactionsExpansions {
	
	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //
	
	private static List<FactionsExpansion> internalExpansions = Lists.newArrayList(
		FactionsFly.get(),
		FactionsChat.get()
	);
	
	private static List<FactionsExpansion> externalExpansions = new ArrayList<>();
	
	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	/**
	 * Add an external expansion, must call {@link FactionsExpansions#sync} after
	 * @param expansion {@link FactionsExpansion} to add
	 * @return true if added
	 */
	public static boolean add(FactionsExpansion expansion) {
		return externalExpansions.add(expansion);
	}
	
	/**
	 * Removes an external expansion, must call {@link FactionsExpansion#disable} after
	 * @param expansion {@link FactionsExpansion} to remove
	 * @return true if added
	 */
	public static boolean remove(FactionsExpansion expansion) {
		return externalExpansions.remove(expansion);
	}
	
	/**
	 * Synchronise expansions according to the result of their {@link FactionsExpansion#shouldEnable}
	 */
	public static void sync() {
		internalExpansions.forEach(expansion -> {
			if (expansion.shouldEnable()) {
				if (!expansion.isEnabled()) {
					expansion.enable();
				}
			} else {
				if (expansion.isEnabled()) {
					expansion.disable();
				}
			}
		});
	}
	
}
