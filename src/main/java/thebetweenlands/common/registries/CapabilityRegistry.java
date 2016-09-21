package thebetweenlands.common.registries;

import thebetweenlands.common.entity.capability.DecayEntityCapability;
import thebetweenlands.common.entity.capability.base.EntityCapabilityHandler;

public class CapabilityRegistry {
	private CapabilityRegistry() { }

	public static void preInit() {
		//EntityCapabilityHandler.registerEntityCapability(new DecayEntityCapability());

		EntityCapabilityHandler.registerCapabilities();
	}
}
