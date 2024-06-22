package thebetweenlands.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.concurrent.CompletableFuture;

public class BetweenlandsBlockTagsProvider extends BlockTagsProvider {

	public BetweenlandsBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, provider, TheBetweenlands.ID, existingFileHelper);
	}

	protected void addTags(HolderLookup.Provider provider) {
		this.tag(BlockTags.DIRT).add(BlockRegistry.SWAMP_DIRT.get(), BlockRegistry.SWAMP_GRASS.get(), BlockRegistry.DEAD_SWAMP_GRASS.get(), BlockRegistry.MUD.get());
	}
}
