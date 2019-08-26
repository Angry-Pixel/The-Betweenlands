package thebetweenlands.common.block.plant;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.ITintedBlock;

public class BlockEdgeMoss extends BlockEdgePlant implements ITintedBlock {

    public BlockEdgeMoss() {
        super();
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        setSoundType(SoundType.PLANT);
        setHardness(0.1F);
    }

    @Override
	public int getColorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
		return worldIn != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos) : ColorizerFoliage.getFoliageColorBasic();
	}
}