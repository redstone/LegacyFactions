package net.redstoneore.legacyfactions.integration;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

public class Integrations {

	private static List<Integration> enabledIntegrations = new ArrayList<Integration>();
	
	public static void add(Integration integration) {
		if (integration.isEnabled()) {
			integration.init();
			enabledIntegrations.add(integration);
		}
	}
	
	public static List<Integration> getAll() {
		return Lists.newArrayList(enabledIntegrations);
	}
	
}
