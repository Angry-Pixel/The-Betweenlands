package thebetweenlands.common.block.plant;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockVenusFlyTrap extends BlockPlant {
	public static final BooleanProperty BLOOMING = BooleanProperty.create("blooming");

	public BlockVenusFlyTrap() {
		super();
		this.setDefaultState(this.blockState.getBaseState().with(BLOOMING, false));
	}

	@Override
	public void tick(IBlockState state, World worldIn, BlockPos pos, Random rand) {
		super.tick(state, worldIn, pos, rand);
		if(rand.nextInt(300) == 0) {
			if(!state.get(BLOOMING)) {
				if(rand.nextInt(3) == 0)
					worldIn.setBlockState(pos, this.getDefaultState().with(BLOOMING, true));
			} else {
				worldIn.setBlockState(pos, this.getDefaultState().with(BLOOMING, false));
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
		return this.getDefaultState().with(BLOOMING, blooming);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = 0;
		if(state.get(BLOOMING))
			meta = 1;
		return meta;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.XZ;
	}
}
