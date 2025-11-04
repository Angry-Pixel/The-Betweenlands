package thebetweenlands.common.datagen.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.EntityRegistry;

import java.util.concurrent.CompletableFuture;

public class BLEntityTagProvider extends EntityTypeTagsProvider {

	public static final TagKey<EntityType<?>> WEEDWOOD_BUSH_PASSABLE = tag("weedwood_bush_passable");
	public static final TagKey<EntityType<?>> WIGHTS_BANE_INSTAKILLS = tag("wights_bane_instakills");
	public static final TagKey<EntityType<?>> HAG_HACKER_INSTAKILLS = tag("hag_hacker_instakills");
	public static final TagKey<EntityType<?>> CRITTER_CRUNCHER_INSTAKILLS = tag("critter_cruncher_instakills");
	public static final TagKey<EntityType<?>> SLUDGE_SLICER_INSTAKILLS = tag("sludge_slicer_instakills");
	public static final TagKey<EntityType<?>> LEECH_ATTACKS = tag("leech_attacks");
	public static final TagKey<EntityType<?>> FISHING_SPEAR_ONE_SHOTS = tag("fishing_spear_one_shots");

	public static final TagKey<EntityType<?>> BYPASSES_REPELLER = tag("bypasses_repeller");
	public static final TagKey<EntityType<?>> SPIKE_TRAP_IMMUNE = tag("spike_trap_immune");
//	public static final TagKey<EntityType<?>> TRIGGER_SPIKE_TRAPS = tah("trigger_spike_traps");

	public static final TagKey<EntityType<?>> TAR_BEING = tag("tar_being"); // creature type tag for tar-based things, I guess
	public static final TagKey<EntityType<?>> CAN_BREATHE_UNDER_TAR = tag("can_breathe_under_tar");
	public static final TagKey<EntityType<?>> IMMUNE_TO_TAR_SLOWDOWN = tag("immune_to_tar_slowdown");

	public static final TagKey<EntityType<?>> IMMUNE_TO_RUBBER_SLOWDOWN = tag("immune_to_rubber_slowdown");

	public BLEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, provider, TheBetweenlands.ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(WEEDWOOD_BUSH_PASSABLE).add(EntityType.PLAYER, EntityRegistry.GECKO.get());
		this.tag(WIGHTS_BANE_INSTAKILLS).add(EntityRegistry.WIGHT.get());
		this.tag(HAG_HACKER_INSTAKILLS).add(EntityRegistry.SWAMP_HAG.get());
		this.tag(CRITTER_CRUNCHER_INSTAKILLS).add(EntityRegistry.DRAGONFLY.get(), EntityRegistry.FIREFLY.get(), EntityRegistry.BLOOD_SNAIL.get(), EntityRegistry.MIRE_SNAIL.get(), EntityRegistry.SPORELING.get(), EntityRegistry.LEECH.get(), EntityRegistry.TERMITE.get(), EntityRegistry.INFESTATION.get(), EntityRegistry.CHIROMAW.get());
		this.tag(SLUDGE_SLICER_INSTAKILLS).add(EntityRegistry.SLUDGE.get(), EntityRegistry.SMOL_SLUDGE.get());
		this.tag(LEECH_ATTACKS).add(EntityRegistry.SWAMP_HAG.get(), EntityType.PLAYER);
		this.tag(FISHING_SPEAR_ONE_SHOTS).add(EntityRegistry.ANADIA.get(), EntityType.COD, EntityType.SALMON, EntityType.PUFFERFISH, EntityType.TROPICAL_FISH);
		this.tag(Tags.EntityTypes.BOSSES).add(EntityRegistry.DREADFUL_PEAT_MUMMY.get(), EntityRegistry.PRIMORDIAL_MALEVOLENCE.get()); // TODO bosses
		this.tag(EntityTypeTags.ARROWS).add(EntityRegistry.ANGLER_TOOTH_ARROW.get(), EntityRegistry.BASILISK_ARROW.get(), EntityRegistry.OCTINE_ARROW.get(), EntityRegistry.POISON_ANGLER_TOOTH_ARROW.get(), EntityRegistry.SHOCK_ARROW.get(), EntityRegistry.SLUDGE_WORM_ARROW.get());
		this.tag(EntityTypeTags.CAN_BREATHE_UNDER_WATER).add(EntityRegistry.BUBBLER_CRAB.get(), EntityRegistry.SILT_CRAB.get(), EntityRegistry.ANADIA.get(), EntityRegistry.DREADFUL_PEAT_MUMMY.get(), EntityRegistry.EMBERLING.get(), EntityRegistry.EMBERLING_SHAMAN.get(), EntityRegistry.JELLYFISH.get(), EntityRegistry.LURKER.get(), EntityRegistry.FRESHWATER_URCHIN.get(), EntityRegistry.CAVE_FISH.get(), EntityRegistry.FROG.get(), EntityRegistry.OLM.get());
		this.tag(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(EntityRegistry.STALKER.get(), EntityRegistry.DREADFUL_PEAT_MUMMY.get(), EntityRegistry.DRAGONFLY.get(), EntityRegistry.FIREFLY.get(), EntityRegistry.SPORELING.get(), EntityRegistry.CHIROMAW.get(), EntityRegistry.CHIROMAW_MATRIARCH.get(), EntityRegistry.CHIROMAW_GREEBLING_RIDER.get(), EntityRegistry.TAME_CHIROMAW.get(), EntityRegistry.SHALLOWBREATH.get());
		this.tag(EntityTypeTags.UNDEAD).add(EntityRegistry.SWAMP_HAG.get(), EntityRegistry.WIGHT.get(), EntityRegistry.PEAT_MUMMY.get(), EntityRegistry.DREADFUL_PEAT_MUMMY.get(), EntityRegistry.MUMMY_ARM.get());
		this.tag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).add(EntityRegistry.DREADFUL_PEAT_MUMMY.get());
		this.tag(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES).add(EntityRegistry.EMBERLING.get(), EntityRegistry.EMBERLING_SHAMAN.get(), EntityRegistry.PYRAD.get());
		this.tag(EntityTypeTags.AQUATIC).add(EntityRegistry.BUBBLER_CRAB.get(), EntityRegistry.SILT_CRAB.get(), EntityRegistry.ANADIA.get(), EntityRegistry.JELLYFISH.get(), EntityRegistry.LURKER.get(), EntityRegistry.FRESHWATER_URCHIN.get(), EntityRegistry.CAVE_FISH.get(), EntityRegistry.FROG.get(), EntityRegistry.OLM.get());
		this.tag(EntityTypeTags.ARTHROPOD).add(EntityRegistry.DRAGONFLY.get(), EntityRegistry.TERMITE.get(), EntityRegistry.BUBBLER_CRAB.get(), EntityRegistry.SILT_CRAB.get(), EntityRegistry.INFESTATION.get());
		this.tag(TAR_BEING).add(EntityRegistry.TAR_BEAST.get(), EntityRegistry.TARMINION.get());

		this.tag(BYPASSES_REPELLER)
			.addTag(Tags.EntityTypes.BOSSES)
			.add(EntityRegistry.WIGHT.get());
		this.tag(SPIKE_TRAP_IMMUNE)
			.addTag(Tags.EntityTypes.BOSSES)
			.add(EntityRegistry.TAR_BEAST.get(), EntityRegistry.WIGHT.get());
//		this.tag(TRIGGER_SPIKE_TRAPS).add(EntityType.PLAYER);

		this.tag(CAN_BREATHE_UNDER_TAR)
			.addTag(EntityTypeTags.UNDEAD) // do we want the undead to be immune to tar?
			.addTag(TAR_BEING);

		this.tag(IMMUNE_TO_TAR_SLOWDOWN)
			.addTag(TAR_BEING);

		this.tag(IMMUNE_TO_RUBBER_SLOWDOWN);

		// TODO Pit of Decay Mechanism
		this.tag(Tags.EntityTypes.CAPTURING_NOT_SUPPORTED).add(
			EntityRegistry.DECAY_PIT_TARGET.get(),
//			EntityRegistry.LIGHTNING_BOLT.get(),
//			EntityRegistry.MOVING_WALL.get(),
			EntityRegistry.SWORD_ENERGY.get(),
			EntityRegistry.SEAT.get(),
			EntityRegistry.WIGHT.get(),
			EntityRegistry.FLAME_JET.get(),
			EntityRegistry.FISH_HOOK.get(),
			EntityRegistry.FISH_VORTEX.get(),
			EntityRegistry.ELECTRIC_SHOCK.get(),
			EntityRegistry.PREDATOR_ARROW_GUIDE.get()
		);

		this.tag(Tags.EntityTypes.TELEPORTING_NOT_SUPPORTED).add(
			EntityRegistry.DECAY_PIT_TARGET.get(),
//			EntityRegistry.MOVING_WALL.get(),
			EntityRegistry.SWORD_ENERGY.get(),
			EntityRegistry.SEAT.get(),
			EntityRegistry.PREDATOR_ARROW_GUIDE.get()
		);

	}

	public static TagKey<EntityType<?>> tag(String path) {
		return TagKey.create(Registries.ENTITY_TYPE, TheBetweenlands.prefix(path));
	}
}
