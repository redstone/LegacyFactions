package net.redstoneore.legacyfactions.cli;

import java.io.File;

import picocli.CommandLine;
import picocli.CommandLine.Option;

public class FactionsCLI {

	// -------------------------------------------------- //
	// MAIN
	// -------------------------------------------------- //
	
	private static FactionsCLI instance = null;
	public static void main(String[] args) {
		instance = CommandLine.populateCommand(new FactionsCLI(), args);
		instance.exec();
	}
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private FactionsCLI() { }
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	@Option(names = { "-v", "--verbose" }, description = "Be verbose.")
	private boolean verbose = false;
	
	@Option(names = { "-createmanual", "--createmanual" }, description = "Create config manual pages.")
	private boolean generareHelp = false;
	
	@Option(names = { "-dir", "--dir" }, description = "Create config manual pages.")
	private String saveDirectory = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	public void exec() {
		if (this.generareHelp) {
			this.actionCreateManual();
			return;
		}
		
		CommandLine.usage(instance, System.out);
	}
	
	public void actionCreateManual() {
		System.out.println("Not implemented.");
	}
	
}
