package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;

public class SoundRegistry {
	// Sound list
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, TheBetweenlands.ID);

	// Ambent loops
	public static final DeferredHolder<SoundEvent, SoundEvent> BETWEENLANDS_AMBIENT_SWAMP_LOOP = register("ambient_swamp");
	public static final DeferredHolder<SoundEvent, SoundEvent> BETWEENLANDS_AMBIENT_WATER_LOOP = register("water_ambience");
	public static final DeferredHolder<SoundEvent, SoundEvent> BETWEENLANDS_AMBIENT_CAVES_LOOP = register("ambient_cave");
	public static final DeferredHolder<SoundEvent, SoundEvent> BETWEENLANDS_AMBIENT_CAVE_SPOOK_LOOP = register("ambient_cave_spook");

	// Ambent effects


	// Blocks
	public static final DeferredHolder<SoundEvent, SoundEvent> BETWEENLANDS_PORTAL = register("portal");

	// Music
	public static final DeferredHolder<SoundEvent, SoundEvent> BETWEENLANDS_MUSIC = register("bl_dimension");

	// Records
	public static final DeferredHolder<SoundEvent, SoundEvent> RECORD_ASTATOS = register("astatos");

	// Entities
	public static final DeferredHolder<SoundEvent, SoundEvent> SWAMP_HAG_DEATH = register("swamp_hag_death");
	public static final DeferredHolder<SoundEvent, SoundEvent> SWAMP_HAG_HURT = register("swamp_hag_hurt");
	public static final DeferredHolder<SoundEvent, SoundEvent> SWAMP_HAG_LIVING = register("swamp_hag_living"); // FIXME Compialation of all sounds (may be unused)
	public static final DeferredHolder<SoundEvent, SoundEvent> SWAMP_HAG_LIVING_1 = register("swamp_hag_living_1");
	public static final DeferredHolder<SoundEvent, SoundEvent> SWAMP_HAG_LIVING_2 = register("swamp_hag_living_2");
	public static final DeferredHolder<SoundEvent, SoundEvent> SWAMP_HAG_LIVING_3 = register("swamp_hag_living_3");
	public static final DeferredHolder<SoundEvent, SoundEvent> SWAMP_HAG_LIVING_4 = register("swamp_hag_living_4");
	public static final DeferredHolder<SoundEvent, SoundEvent> GECKO_DEATH = register("gecko_death");
	public static final DeferredHolder<SoundEvent, SoundEvent> GECKO_HIDE = register("gecko_hide");
	public static final DeferredHolder<SoundEvent, SoundEvent> GECKO_HURT = register("gecko_hurt");
	public static final DeferredHolder<SoundEvent, SoundEvent> GECKO_LIVING = register("gecko_living");
	public static final DeferredHolder<SoundEvent, SoundEvent> WIGHT_DEATH = register("wight_death");
	public static final DeferredHolder<SoundEvent, SoundEvent> WIGHT_ATTACK = register("wight_attack");
	public static final DeferredHolder<SoundEvent, SoundEvent> WIGHT_HURT = register("wight_hurt");
	public static final DeferredHolder<SoundEvent, SoundEvent> WIGHT_LIVING = register("wight_living");

	public static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
		return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(TheBetweenlands.prefix(name)));
	}
}
