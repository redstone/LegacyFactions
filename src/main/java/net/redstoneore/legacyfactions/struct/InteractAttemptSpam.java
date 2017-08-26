package net.redstoneore.legacyfactions.struct;

public class InteractAttemptSpam {
	
	public int attempts = 0;
	public long lastAttempt = System.currentTimeMillis();

	// returns the current attempt count
	public int increment() {
		long now = System.currentTimeMillis();
		if (now > this.lastAttempt + 2000) {
			this.attempts = 1;
		} else {
			this.attempts++;
		}
		this.lastAttempt = now;
		return this.attempts;
	}

}
