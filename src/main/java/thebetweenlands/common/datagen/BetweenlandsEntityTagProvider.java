package thebetweenlands.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import javax.annotation.Nullable;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.EntityRegistry;

import java.util.concurrent.CompletableFuture;

public class BetweenlandsEntityTagProvider extends EntityTypeTagsProvider {

	public static final TagKey<EntityType<?>> IGNORES_WEEDWOOD_BUSHES = TagKey.create(Registries.ENTITY_TYPE, TheBetweenlands.prefix("ignores_weedwood_bushes"));

	public BetweenlandsEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, provider, TheBetweenlands.ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(IGNORES_WEEDWOOD_BUSHES).add(EntityType.PLAYER, EntityRegistry.GECKO.get());
		this.tag(EntityTypeTags.CAN_BREATHE_UNDER_WATER).add(EntityRegistry.BUBBLER_CRAB.get(), EntityRegistry.SILT_CRAB.get());
	}
}
