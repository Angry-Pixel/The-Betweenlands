package thebetweenlands.common.block.misc;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class BlockBurntScrivenerMark extends BlockScrivenerMark {
	public static final PropertyBool LINKED = PropertyBool.create("linked");

	public BlockBurntScrivenerMark() {
		this.setHardness(0.5f);
		this.setDefaultState(this.blockState.getBaseState().withProperty(LINKED, false).withProperty(NORTH_SIDE, false).withProperty(EAST_SIDE, false).withProperty(SOUTH_SIDE, false).withProperty(WEST_SIDE, false));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return this.getConnectedTextureBlockStateContainer(new ExtendedBlockState(this, new IProperty[] { LINKED, NORTH_SIDE, EAST_SIDE, SOUTH_SIDE, WEST_SIDE }, new IUnlistedProperty[0]));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(LINKED) ? 1 : 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(LINKED, meta == 1);
	}
}
