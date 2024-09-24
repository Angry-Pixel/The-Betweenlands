package thebetweenlands.common.registries;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.environment.EnvironmentEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.event.*;

public class EnvironmentEventRegistry {

	public static final DeferredRegister<EnvironmentEvent> EVENTS = DeferredRegister.create(BLRegistries.Keys.ENVIRONMENT_EVENTS, TheBetweenlands.ID);

	public static final DeferredHolder<EnvironmentEvent, AuroraEvent> AURORAS = EVENTS.register("auroras", AuroraEvent::new);
	public static final DeferredHolder<EnvironmentEvent, BloodSkyEvent> BLOOD_SKY = EVENTS.register("blood_sky", BloodSkyEvent::new);
	public static final DeferredHolder<EnvironmentEvent, DenseFogEvent> DENSE_FOG = EVENTS.register("dense_fog", DenseFogEvent::new);
	public static final DeferredHolder<EnvironmentEvent, HeavyRainEvent> HEAVY_RAIN = EVENTS.register("heavy_rain", HeavyRainEvent::new);
	public static final DeferredHolder<EnvironmentEvent, RiftEvent> RIFT = EVENTS.register("rift", RiftEvent::new);
	public static final DeferredHolder<EnvironmentEvent, SnowfallEvent> SNOWFALL = EVENTS.register("snowfall", SnowfallEvent::new);
	public static final DeferredHolder<EnvironmentEvent, SpoopyEvent> SPOOPY = EVENTS.register("spoopy", SpoopyEvent::new);
	public static final DeferredHolder<EnvironmentEvent, ThunderstormEvent> THUNDERSTORM = EVENTS.register("thunderstorm", ThunderstormEvent::new);
	public static final DeferredHolder<EnvironmentEvent, WinterEvent> WINTER = EVENTS.register("winter", WinterEvent::new);

}
