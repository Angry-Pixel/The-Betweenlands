package thebetweenlands.common.registries;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import thebetweenlands.api.capability.IBlessingCapability;
import thebetweenlands.api.capability.ICircleGemCapability;
import thebetweenlands.api.capability.ICustomStepSoundCapability;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.api.capability.IEntityCircleGemCapability;
import thebetweenlands.api.capability.IEntityCustomCollisionsCapability;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.capability.IFlightCapability;
import thebetweenlands.api.capability.IFoodSicknessCapability;
import thebetweenlands.api.capability.ILastKilledCapability;
import thebetweenlands.api.capability.IPortalCapability;
import thebetweenlands.api.capability.IPuppetCapability;
import thebetweenlands.api.capability.IPuppeteerCapability;
import thebetweenlands.api.capability.ISummoningCapability;
import thebetweenlands.api.capability.ISwarmedCapability;
import thebetweenlands.common.capability.CustomStepSoundCapability;
import thebetweenlands.common.capability.base.EntityCapabilityHandler;
import thebetweenlands.common.capability.base.ItemCapabilityHandler;
import thebetweenlands.common.capability.blessing.BlessingEntityCapability;
import thebetweenlands.common.capability.circlegem.CircleGemEntityCapability;
import thebetweenlands.common.capability.collision.RingOfDispersionEntityCapability;
import thebetweenlands.common.capability.decay.DecayEntityCapability;
import thebetweenlands.common.capability.equipment.EquipmentEntityCapability;
import thebetweenlands.common.capability.flight.FlightEntityCapability;
import thebetweenlands.common.capability.foodsickness.FoodSicknessEntityCapability;
import thebetweenlands.common.capability.item.ILivingWeedwoodShieldCapability;
import thebetweenlands.common.capability.item.LivingWeedwoodShieldItemCapability;
import thebetweenlands.common.capability.lastkilled.LastKilledCapability;
import thebetweenlands.common.capability.playermounts.IPlayerMountsEntityCapability;
import thebetweenlands.common.capability.playermounts.PlayerMountsEntityCapability;
import thebetweenlands.common.capability.portal.PortalEntityCapability;
import thebetweenlands.common.capability.recruitment.EntityPuppetCapability;
import thebetweenlands.common.capability.recruitment.EntityPuppeteerCapability;
import thebetweenlands.common.capability.summoning.EntitySummoningCapability;
import thebetweenlands.common.capability.swarmed.SwarmedCapability;

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

	@CapabilityInject(IPuppetCapability.class)
	public static final Capability<IPuppetCapability> CAPABILITY_PUPPET = null;

	@CapabilityInject(IPuppeteerCapability.class)
	public static final Capability<IPuppeteerCapability> CAPABILITY_PUPPETEER = null;

	@CapabilityInject(ISummoningCapability.class)
	public static final Capability<ISummoningCapability> CAPABILITY_SUMMON = null;

	@CapabilityInject(IPortalCapability.class)
	public static final Capability<IPortalCapability> CAPABILITY_PORTAL = null;

	@CapabilityInject(IFoodSicknessCapability.class)
	public static final Capability<IFoodSicknessCapability> CAPABILITY_FOOD_SICKNESS = null;

	@CapabilityInject(ICustomStepSoundCapability.class)
	public static final Capability<ICustomStepSoundCapability> CAPABILITY_CUSTOM_STEP_SOUND = null;
	
	@CapabilityInject(ILivingWeedwoodShieldCapability.class)
	public static final Capability<ILivingWeedwoodShieldCapability> CAPABILITY_LIVING_WEEDWOOD_SHIELD = null;
	
	@CapabilityInject(IEntityCustomCollisionsCapability.class)
	public static final Capability<IEntityCustomCollisionsCapability> CAPABILITY_ENTITY_CUSTOM_BLOCK_COLLISIONS = null;
	
	@CapabilityInject(IPlayerMountsEntityCapability.class)
	public static final Capability<IPlayerMountsEntityCapability> CAPABILITY_PLAYER_MOUNTS = null;
	
	@CapabilityInject(ILastKilledCapability.class)
	public static final Capability<ILastKilledCapability> CAPABILITY_LAST_KILLED = null;
	
	@CapabilityInject(IBlessingCapability.class)
	public static final Capability<IBlessingCapability> CAPABILITY_BLESSING = null;
	
	@CapabilityInject(ISwarmedCapability.class)
	public static final Capability<ISwarmedCapability> CAPABILITY_SWARMED = null;
		
	public static void preInit() {
		EntityCapabilityHandler.registerEntityCapability(new DecayEntityCapability());
		EntityCapabilityHandler.registerEntityCapability(new CircleGemEntityCapability());
		EntityCapabilityHandler.registerEntityCapability(new EquipmentEntityCapability());
		EntityCapabilityHandler.registerEntityCapability(new FlightEntityCapability());
		EntityCapabilityHandler.registerEntityCapability(new EntityPuppetCapability());
		EntityCapabilityHandler.registerEntityCapability(new EntityPuppeteerCapability());
		EntityCapabilityHandler.registerEntityCapability(new EntitySummoningCapability());
		EntityCapabilityHandler.registerEntityCapability(new PortalEntityCapability());
		EntityCapabilityHandler.registerEntityCapability(new FoodSicknessEntityCapability());
		EntityCapabilityHandler.registerEntityCapability(new CustomStepSoundCapability());
		EntityCapabilityHandler.registerEntityCapability(new RingOfDispersionEntityCapability());
		EntityCapabilityHandler.registerEntityCapability(new PlayerMountsEntityCapability());
		EntityCapabilityHandler.registerEntityCapability(new LastKilledCapability());
		EntityCapabilityHandler.registerEntityCapability(new BlessingEntityCapability());
		EntityCapabilityHandler.registerEntityCapability(new SwarmedCapability());
		
		ItemCapabilityHandler.registerItemCapability(new LivingWeedwoodShieldItemCapability());
		
		EntityCapabilityHandler.registerCapabilities();
		ItemCapabilityHandler.registerCapabilities();
	}
}
