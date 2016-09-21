package thebetweenlands.common.entity.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class EntityCapabilities {
	@CapabilityInject(IDecayCapability.class)
	public static final Capability<IDecayCapability> DECAY_CAPABILITY = null;
}
