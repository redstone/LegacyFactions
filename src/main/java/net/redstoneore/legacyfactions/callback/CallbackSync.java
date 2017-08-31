package net.redstoneore.legacyfactions.callback;

@FunctionalInterface
public interface CallbackSync<T, F> {

	T get(F value);
			
}
