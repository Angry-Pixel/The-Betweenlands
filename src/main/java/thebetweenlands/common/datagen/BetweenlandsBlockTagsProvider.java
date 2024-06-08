package thebetweenlands.common.datagen;

import net.minecraft.core.Holder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;

public class BetweenlandsBlockTagsProvider extends BlockTagsProvider {

    public BetweenlandsBlockTagsProvider(DataGenerator datagen, @Nullable ExistingFileHelper existingFileHelper) {
        super(datagen, TheBetweenlands.ID, existingFileHelper);
    }

    protected void addTags() {
        this.tag(BlockTags.DIRT).add(BlockRegistry.SWAMP_DIRT.get(), BlockRegistry.SWAMP_GRASS.get(), BlockRegistry.DEAD_SWAMP_GRASS.get(), BlockRegistry.MUD.get());
    }
}
