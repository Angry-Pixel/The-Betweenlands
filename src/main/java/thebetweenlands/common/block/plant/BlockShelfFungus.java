package thebetweenlands.common.block.plant;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.common.block.BasicBlock;

public class BlockShelfFungus extends BasicBlock {
	public static final PropertyBool IS_TOP = PropertyBool.create("is_top");

	public BlockShelfFungus() {
		super(Material.WOOD);
		this.setSoundType2(SoundType.CLOTH).setHardness(0.2F);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { IS_TOP });
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state.withProperty(IS_TOP, worldIn.getBlockState(pos.up()).getBlock() != this);
	}
}
