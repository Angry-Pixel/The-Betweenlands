package thebetweenlands.common.block;

import net.minecraft.block.BlockLog;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockLogBetweenlands extends BlockLog {

	public BlockLogBetweenlands() {
		setHarvestLevel("axe", 0);
		setCreativeTab(BLCreativeTabs.PLANTS);
		setDefaultState(blockState.getBaseState().withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState state = getDefaultState();
		switch (meta & 12) {
			case 0:
				state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y);
				break;
			case 4:
				state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
				break;
			case 8:
				state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
				break;
			default:
				state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);
		}

		return state;
	}

	@Override
	@SuppressWarnings("incomplete-switch")
	public int getMetaFromState(IBlockState state) {
		int meta = 0;
		switch (state.getValue(LOG_AXIS)) {
			case X:
				meta |= 4;
				break;
			case Z:
				meta |= 8;
				break;
			case NONE:
				meta |= 12;
				break;
		}

		return meta;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { LOG_AXIS });
	}

}