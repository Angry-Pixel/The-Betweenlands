package thebetweenlands.common.block.plant;

import net.minecraft.block.SoundType;
import net.minecraft.util.EnumFacing;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockEdgeShroom extends BlockEdgePlant {

    public BlockEdgeShroom() {
        super();
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        setSoundType(SoundType.PLANT);
        setHardness(0.1F);
        setCreativeTab(BLCreativeTabs.BLOCKS);
    }
}