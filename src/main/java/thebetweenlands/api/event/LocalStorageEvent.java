package thebetweenlands.api.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import thebetweenlands.api.storage.ILocalStorage;

public class LocalStorageEvent extends Event {
	private final ILocalStorage storage;
	
	public LocalStorageEvent(ILocalStorage storage) {
		this.storage = storage;
	}
	
	public ILocalStorage getStorage() {
		return this.storage;
	}
	
	public static class Added extends LocalStorageEvent {
		public Added(ILocalStorage storage) {
			super(storage);
		}
	}
	
	public static class Removed extends LocalStorageEvent {
		public Removed(ILocalStorage storage) {
			super(storage);
		}
	}
	
	public static class Loaded extends LocalStorageEvent {
		public Loaded(ILocalStorage storage) {
			super(storage);
		}
	}
	
	public static class Unloaded extends LocalStorageEvent {
		public Unloaded(ILocalStorage storage) {
			super(storage);
		}
	}
}
