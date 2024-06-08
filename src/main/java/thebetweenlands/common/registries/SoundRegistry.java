package thebetweenlands.common.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thebetweenlands.TheBetweenlands;

public class SoundRegistry {
	// Sound list
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TheBetweenlands.ID);
	
	// Ambent loops
	public static final RegistryObject<SoundEvent> BETWEENLANDS_AMBIENT_SWAMP_LOOP = SOUNDS.register("ambient_swamp", () -> new SoundEvent(new ResourceLocation(TheBetweenlands.ID, "ambient_swamp")));
	public static final RegistryObject<SoundEvent> BETWEENLANDS_AMBIENT_WATER_LOOP = SOUNDS.register("water_ambience", () -> new SoundEvent(new ResourceLocation(TheBetweenlands.ID, "water_ambience")));
	public static final RegistryObject<SoundEvent> BETWEENLANDS_AMBIENT_CAVES_LOOP = SOUNDS.register("ambient_cave", () -> new SoundEvent(new ResourceLocation(TheBetweenlands.ID, "ambient_cave")));
	
	// Ambent effects
	
	
	// Blocks
	public static final RegistryObject<SoundEvent> BETWEENLANDS_PORTAL = SOUNDS.register("portal", () -> new SoundEvent(new ResourceLocation(TheBetweenlands.ID, "portal")));
	
	// Music
	public static final RegistryObject<SoundEvent> BETWEENLANDS_MUSIC = SOUNDS.register("bl_dimension", () -> new SoundEvent(new ResourceLocation(TheBetweenlands.ID, "bl_dimension")));

	// Records
	public static final RegistryObject<SoundEvent> RECORD_ASTATOS = SOUNDS.register("astatos", () -> new SoundEvent(new ResourceLocation(TheBetweenlands.ID, "astatos")));

	// Entitys
	public static final RegistryObject<SoundEvent> SWAMP_HAG_DEATH = SOUNDS.register("swamp_hag_death", () -> new SoundEvent(new ResourceLocation(TheBetweenlands.ID, "swamp_hag_death")));
	public static final RegistryObject<SoundEvent> SWAMP_HAG_HURT = SOUNDS.register("swamp_hag_hurt", () -> new SoundEvent(new ResourceLocation(TheBetweenlands.ID, "swamp_hag_hurt")));
	public static final RegistryObject<SoundEvent> SWAMP_HAG_LIVING = SOUNDS.register("swamp_hag_living", () -> new SoundEvent(new ResourceLocation(TheBetweenlands.ID, "swamp_hag_living"))); // Compialation of all sounds (may be unused)
	public static final RegistryObject<SoundEvent> SWAMP_HAG_LIVING_1 = SOUNDS.register("swamp_hag_living_1", () -> new SoundEvent(new ResourceLocation(TheBetweenlands.ID, "swamp_hag_living_1")));
	public static final RegistryObject<SoundEvent> SWAMP_HAG_LIVING_2 = SOUNDS.register("swamp_hag_living_2", () -> new SoundEvent(new ResourceLocation(TheBetweenlands.ID, "swamp_hag_living_2")));
	public static final RegistryObject<SoundEvent> SWAMP_HAG_LIVING_3 = SOUNDS.register("swamp_hag_living_3", () -> new SoundEvent(new ResourceLocation(TheBetweenlands.ID, "swamp_hag_living_3")));
	public static final RegistryObject<SoundEvent> SWAMP_HAG_LIVING_4 = SOUNDS.register("swamp_hag_living_4", () -> new SoundEvent(new ResourceLocation(TheBetweenlands.ID, "swamp_hag_living_4")));
	public static final RegistryObject<SoundEvent> GEKO_DEATH = SOUNDS.register("gecko_death", () -> new SoundEvent(new ResourceLocation(TheBetweenlands.ID, "gecko_death")));
	public static final RegistryObject<SoundEvent> GEKO_HIDE = SOUNDS.register("gecko_hide", () -> new SoundEvent(new ResourceLocation(TheBetweenlands.ID, "gecko_hide")));
	public static final RegistryObject<SoundEvent> GEKO_HURT = SOUNDS.register("gecko_hurt", () -> new SoundEvent(new ResourceLocation(TheBetweenlands.ID, "gecko_hurt")));
	public static final RegistryObject<SoundEvent> GEKO_LIVING = SOUNDS.register("gecko_living", () -> new SoundEvent(new ResourceLocation(TheBetweenlands.ID, "gecko_living")));
	public static final RegistryObject<SoundEvent> WIGHT_DEATH = SOUNDS.register("wight_death", () -> new SoundEvent(new ResourceLocation(TheBetweenlands.ID, "wight_death")));
	public static final RegistryObject<SoundEvent> WIGHT_ATTACK = SOUNDS.register("wight_attack", () -> new SoundEvent(new ResourceLocation(TheBetweenlands.ID, "wight_attack")));
	public static final RegistryObject<SoundEvent> WIGHT_HURT = SOUNDS.register("wight_hurt", () -> new SoundEvent(new ResourceLocation(TheBetweenlands.ID, "wight_hurt")));
	public static final RegistryObject<SoundEvent> WIGHT_LIVING = SOUNDS.register("wight_living", () -> new SoundEvent(new ResourceLocation(TheBetweenlands.ID, "wight_living")));
	
	
	// Register sound events list
	public static void register(IEventBus eventBus) {
		SOUNDS.register(eventBus);
	}
}
