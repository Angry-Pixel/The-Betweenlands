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

	public static final TagKey<EntityType<?>> IGNORES_WEEDWOOD_BUSHES = TagKey.create(Registries.ENTITY_TYPE, TheBetweenlands.prefix("ignores_weedwood_bushes"));
	public static final TagKey<EntityType<?>> WIGHTS_BANE_INSTAKILLS = TagKey.create(Registries.ENTITY_TYPE, TheBetweenlands.prefix("wights_bane_instakills"));
	public static final TagKey<EntityType<?>> HAG_HACKER_INSTAKILLS = TagKey.create(Registries.ENTITY_TYPE, TheBetweenlands.prefix("hag_hacker_instakills"));
	public static final TagKey<EntityType<?>> CRITTER_CRUNCHER_INSTAKILLS = TagKey.create(Registries.ENTITY_TYPE, TheBetweenlands.prefix("critter_cruncher_instakills"));
	public static final TagKey<EntityType<?>> SLUDGE_SLICER_INSTAKILLS = TagKey.create(Registries.ENTITY_TYPE, TheBetweenlands.prefix("sludge_slicer_instakills"));

	public BLEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, provider, TheBetweenlands.ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(IGNORES_WEEDWOOD_BUSHES).add(EntityType.PLAYER, EntityRegistry.GECKO.get());
		this.tag(WIGHTS_BANE_INSTAKILLS).add(EntityRegistry.WIGHT.get());
		this.tag(HAG_HACKER_INSTAKILLS).add(EntityRegistry.SWAMP_HAG.get());
		this.tag(Tags.EntityTypes.BOSSES).add(EntityRegistry.DREADFUL_PEAT_MUMMY.get());
		this.tag(EntityTypeTags.ARROWS).add(EntityRegistry.ANGLER_TOOTH_ARROW.get(), EntityRegistry.BASILISK_ARROW.get(), EntityRegistry.OCTINE_ARROW.get(), EntityRegistry.POISON_ANGLER_TOOTH_ARROW.get(), EntityRegistry.SHOCK_ARROW.get(), EntityRegistry.SLUDGE_WORM_ARROW.get());
		this.tag(EntityTypeTags.CAN_BREATHE_UNDER_WATER).add(EntityRegistry.BUBBLER_CRAB.get(), EntityRegistry.SILT_CRAB.get(), EntityRegistry.ANADIA.get(), EntityRegistry.DREADFUL_PEAT_MUMMY.get(), EntityRegistry.EMBERLING.get(), EntityRegistry.EMBERLING_SHAMAN.get(), EntityRegistry.JELLYFISH.get());
		this.tag(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(EntityRegistry.STALKER.get(), EntityRegistry.DREADFUL_PEAT_MUMMY.get());
		this.tag(EntityTypeTags.UNDEAD).add(EntityRegistry.SWAMP_HAG.get(), EntityRegistry.WIGHT.get(), EntityRegistry.PEAT_MUMMY.get(), EntityRegistry.DREADFUL_PEAT_MUMMY.get(), EntityRegistry.MUMMY_ARM.get());
		this.tag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).add(EntityRegistry.DREADFUL_PEAT_MUMMY.get());
		this.tag(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES).add(EntityRegistry.EMBERLING.get(), EntityRegistry.EMBERLING_SHAMAN.get());
		this.tag(EntityTypeTags.AQUATIC).add(EntityRegistry.BUBBLER_CRAB.get(), EntityRegistry.SILT_CRAB.get(), EntityRegistry.ANADIA.get(), EntityRegistry.JELLYFISH.get());
	}
}
