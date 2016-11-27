package thebetweenlands.common.event;

import net.minecraftforge.event.AttachCapabilitiesEvent;
import thebetweenlands.common.world.storage.world.global.WorldDataBase;

public class AttachWorldCapabilitiesEvent extends AttachCapabilitiesEvent<WorldDataBase<?>> {
	private final WorldDataBase<?> storage;

	@SuppressWarnings("unchecked")
	public AttachWorldCapabilitiesEvent(WorldDataBase<?> storage) {
		//pls, generics, why are you doing this to me
		super((Class<WorldDataBase<?>>)(Class<?>)WorldDataBase.class, storage);
		this.storage = storage;
	}

	public WorldDataBase<?> getStorage() {
		return this.storage;
	}
}
