package net.redstoneore.legacyfactions.callback;

import java.util.Optional;

@FunctionalInterface
public interface Callback<R> {
	
	void then(R result, Optional<Exception> exception);

}
