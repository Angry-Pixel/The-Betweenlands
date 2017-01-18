package thebetweenlands.common.registries;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import thebetweenlands.common.capability.base.EntityCapabilityHandler;
import thebetweenlands.common.capability.circlegem.CircleGemEntityCapability;
import thebetweenlands.common.capability.circlegem.ICircleGemCapability;
import thebetweenlands.common.capability.circlegem.IEntityCircleGemCapability;
import thebetweenlands.common.capability.decay.DecayEntityCapability;
import thebetweenlands.common.capability.decay.IDecayCapability;
import thebetweenlands.common.capability.equipment.EquipmentEntityCapability;
import thebetweenlands.common.capability.equipment.IEquipmentCapability;
import thebetweenlands.common.capability.flight.FlightEntityCapability;
import thebetweenlands.common.capability.flight.IFlightCapability;

public class CapabilityRegistry {
	private CapabilityRegistry() { }

	@CapabilityInject(IDecayCapability.class)
	public static final Capability<IDecayCapability> CAPABILITY_DECAY = null;

	@CapabilityInject(IEntityCircleGemCapability.class)
	public static final Capability<ICircleGemCapability> CAPABILITY_ENTITY_CIRCLE_GEM = null;

	@CapabilityInject(IEquipmentCapability.class)
	public static final Capability<IEquipmentCapability> CAPABILITY_EQUIPMENT = null;

	@CapabilityInject(IFlightCapability.class)
	public static final Capability<IFlightCapability> CAPABILITY_FLIGHT = null;
	
	public static void preInit() {
		EntityCapabilityHandler.registerEntityCapability(new DecayEntityCapability());
		EntityCapabilityHandler.registerEntityCapability(new CircleGemEntityCapability());
		EntityCapabilityHandler.registerEntityCapability(new EquipmentEntityCapability());
		EntityCapabilityHandler.registerEntityCapability(new FlightEntityCapability());
		
		EntityCapabilityHandler.registerCapabilities();
		//ItemCapabilityHandler.registerCapabilities();
	}
}
