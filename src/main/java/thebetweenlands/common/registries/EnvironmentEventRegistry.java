package thebetweenlands.common.registries;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.event.*;

public class EnvironmentEventRegistry {

	public static final DeferredRegister<IEnvironmentEvent> EVENTS = DeferredRegister.create(BLRegistries.Keys.ENVIRONMENT_EVENTS, TheBetweenlands.ID);

	public static final DeferredHolder<IEnvironmentEvent, AuroraEvent> AURORAS = EVENTS.register("auroras", AuroraEvent::new);
	public static final DeferredHolder<IEnvironmentEvent, BloodSkyEvent> BLOOD_SKY = EVENTS.register("blood_sky", BloodSkyEvent::new);
	public static final DeferredHolder<IEnvironmentEvent, DenseFogEvent> DENSE_FOG = EVENTS.register("dense_fog", DenseFogEvent::new);
	public static final DeferredHolder<IEnvironmentEvent, HeavyRainEvent> HEAVY_RAIN = EVENTS.register("heavy_rain", HeavyRainEvent::new);
	public static final DeferredHolder<IEnvironmentEvent, RiftEvent> RIFT = EVENTS.register("rift", RiftEvent::new);
	public static final DeferredHolder<IEnvironmentEvent, SnowfallEvent> SNOWFALL = EVENTS.register("snowfall", SnowfallEvent::new);
	public static final DeferredHolder<IEnvironmentEvent, SpoopyEvent> SPOOPY = EVENTS.register("spoopy", SpoopyEvent::new);
	public static final DeferredHolder<IEnvironmentEvent, ThunderstormEvent> THUNDERSTORM = EVENTS.register("thunderstorm", ThunderstormEvent::new);
	public static final DeferredHolder<IEnvironmentEvent, WinterEvent> WINTER = EVENTS.register("winter", WinterEvent::new);

}
