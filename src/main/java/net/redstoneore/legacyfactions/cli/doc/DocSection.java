package net.redstoneore.legacyfactions.cli.doc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.RUNTIME)
public @interface DocSection {
	
	String name();
	
}
