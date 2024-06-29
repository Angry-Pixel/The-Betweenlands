package thebetweenlands.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import javax.annotation.Nullable;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.concurrent.CompletableFuture;

public class BetweenlandsBlockTagsProvider extends BlockTagsProvider {

	public static final TagKey<Block> BETWEENSTONE_ORE_REPLACEABLE = tag("betweenstone_ore_replaceable");
	public static final TagKey<Block> PITSTONE_ORE_REPLACEABLE = tag("pitstone_ore_replaceable");

	public BetweenlandsBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, provider, TheBetweenlands.ID, existingFileHelper);
	}

	protected void addTags(HolderLookup.Provider provider) {
		this.tag(BlockTags.DIRT).add(BlockRegistry.SWAMP_DIRT.get(), BlockRegistry.SWAMP_GRASS.get(), BlockRegistry.DEAD_SWAMP_GRASS.get(), BlockRegistry.MUD.get());
	}

	public static TagKey<Block> tag(String tagName) {
		return BlockTags.create(TheBetweenlands.prefix(tagName));
	}

	public static TagKey<Block> commonTag(String tagName) {
		return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", tagName));
	}
}
