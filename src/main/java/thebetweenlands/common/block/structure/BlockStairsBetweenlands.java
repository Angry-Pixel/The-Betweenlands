package thebetweenlands.common.block.structure;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockStairsBetweenlands extends BlockStairs {
    public BlockStairsBetweenlands(IBlockState modelState) {
        super(modelState);
        setLightOpacity(0);
        useNeighborBrightness = true;
        setCreativeTab(BLCreativeTabs.BLOCKS);
    }
}
