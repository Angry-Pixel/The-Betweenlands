package thebetweenlands.common.block.plant;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockVenusFlyTrap extends BlockGenericPlant {
	public static final PropertyBool BLOOMING = PropertyBool.create("blooming");

	public BlockVenusFlyTrap() {
		super();
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(BLOOMING, false));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {FACING, BLOOMING});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		int facing = (meta >> 1) & 4;
		boolean blooming = (meta & 1) == 1;
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(facing)).withProperty(BLOOMING, blooming);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = ((EnumFacing)state.getValue(FACING)).getHorizontalIndex() << 1;
		if(state.getValue(BLOOMING))
			meta |= 1;
		return meta;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.XZ;
	}
}
