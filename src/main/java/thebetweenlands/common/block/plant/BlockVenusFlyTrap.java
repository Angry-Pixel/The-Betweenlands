package thebetweenlands.common.block.plant;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockVenusFlyTrap extends BlockPlant {
	public static final PropertyBool BLOOMING = PropertyBool.create("blooming");

	public BlockVenusFlyTrap() {
		super();
		this.setDefaultState(this.blockState.getBaseState().withProperty(BLOOMING, false));
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		if(rand.nextInt(300) == 0) {
			if(!state.getValue(BLOOMING)) {
				if(rand.nextInt(3) == 0)
					worldIn.setBlockState(pos, this.getDefaultState().withProperty(BLOOMING, true));
			} else {
				worldIn.setBlockState(pos, this.getDefaultState().withProperty(BLOOMING, false));
			}
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {BLOOMING});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		boolean blooming = meta == 1;
		return this.getDefaultState().withProperty(BLOOMING, blooming);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = 0;
		if(state.getValue(BLOOMING))
			meta = 1;
		return meta;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.XZ;
	}
}
