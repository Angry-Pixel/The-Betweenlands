package thebetweenlands.api.storage;

import java.io.Closeable;

/**
 * A temporary handle for an {@link ILocalStorage}.
 * This handle will keep the local storage loaded until it is closed.
 * Handle must be closed after no longer being used.
 * After closing the location will be saved and unloaded if not
 * referenced anywhere else.
 */
public interface ILocalStorageHandle extends Closeable, AutoCloseable {
	public ILocalStorage get();
	
	@Override
	public void close(); //doesn't throw exceptions
}
