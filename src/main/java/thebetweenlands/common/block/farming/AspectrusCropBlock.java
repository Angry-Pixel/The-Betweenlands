package thebetweenlands.common.block.farming;

import net.minecraft.world.level.ItemLike;
import thebetweenlands.common.registries.ItemRegistry;

public class AspectrusCropBlock extends DecayableCropBlock {

    public AspectrusCropBlock(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxHeight() {
        return 3;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ItemRegistry.ASPECTRUS_SEEDS;
    }
    
}
