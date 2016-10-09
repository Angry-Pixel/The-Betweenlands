package thebetweenlands.common.event;

import net.minecraftforge.event.AttachCapabilitiesEvent;
import thebetweenlands.common.world.storage.world.global.WorldDataBase;

public class AttachWorldCapabilitiesEvent extends AttachCapabilitiesEvent {
	private final WorldDataBase<?> storage;

	public AttachWorldCapabilitiesEvent(WorldDataBase<?> storage) {
		super(storage);
		this.storage = storage;
	}

	public WorldDataBase<?> getStorage() {
		return this.storage;
	}
}
