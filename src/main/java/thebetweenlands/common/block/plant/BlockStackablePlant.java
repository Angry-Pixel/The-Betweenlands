package thebetweenlands.common.block.plant;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.util.AdvancedStateMap;

public class BlockStackablePlant extends BlockPlant implements IStateMappedBlock {
	protected static final AxisAlignedBB STACKABLE_PLANT_AABB = new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 1D, 0.9D);

	public static final PropertyBool IS_TOP = PropertyBool.create("is_top");
	public static final PropertyBool IS_BOTTOM = PropertyBool.create("is_bottom");
	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);

	protected int maxHeight = 3;
	protected boolean harvestAll = false;

	public BlockStackablePlant() {
		this(false);
	}

	public BlockStackablePlant(boolean harvestAll) {
		super();
		this.harvestAll = harvestAll;
		this.setDefaultState(this.blockState.getBaseState().withProperty(IS_TOP, true).withProperty(IS_BOTTOM, false));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		return this.maxHeight > 1 ? Block.EnumOffsetType.NONE : Block.EnumOffsetType.XZ;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return STACKABLE_PLANT_AABB;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{AGE, IS_TOP, IS_BOTTOM});
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		boolean isTop = !this.isSamePlant(worldIn.getBlockState(pos.up()));
		boolean isBottom = !this.isSamePlant(worldIn.getBlockState(pos.down()));
		return state.withProperty(IS_TOP, isTop).withProperty(IS_BOTTOM, isBottom);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		//Up
		int height;
		for (height = 1; this.isSamePlant(worldIn.getBlockState(pos.up(height))); ++height);
		for (int offset = height; offset > 0; offset--) {
			BlockPos offsetPos = pos.up(offset);
			IBlockState blockState = worldIn.getBlockState(offsetPos);
			boolean canHarvest = player.isCreative() ? false : blockState.getBlock().canHarvestBlock(worldIn, offsetPos, player);
			boolean removed = this.removeBlock(worldIn, offsetPos, player, canHarvest);
			if(removed && canHarvest) {
				ItemStack stack = player.getHeldItemMainhand() == null ? null : player.getHeldItemMainhand().copy();
				blockState.getBlock().harvestBlock(worldIn, player, offsetPos, blockState, worldIn.getTileEntity(offsetPos), stack);
			}
		}
		if(this.harvestAll) {
			//Down
			BlockPos offsetPos;
			for (int offset = 1; this.isSamePlant(worldIn.getBlockState(offsetPos = pos.down(offset))); offset++) {
				IBlockState blockState = worldIn.getBlockState(offsetPos);
				boolean canHarvest = player.isCreative() ? false : blockState.getBlock().canHarvestBlock(worldIn, offsetPos, player);
				boolean removed = this.removeBlock(worldIn, offsetPos, player, canHarvest);
				if(removed && canHarvest) {
					ItemStack stack = player.getHeldItemMainhand() == null ? null : player.getHeldItemMainhand().copy();
					blockState.getBlock().harvestBlock(worldIn, player, offsetPos, blockState, worldIn.getTileEntity(offsetPos), stack);
				}
			}
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	protected boolean removeBlock(World world, BlockPos pos, EntityPlayer player, boolean canHarvest) {
		IBlockState blockState = world.getBlockState(pos);
		boolean removed = blockState.getBlock().removedByPlayer(blockState, world, pos, player, canHarvest);
		if (removed) {
			blockState.getBlock().onBlockDestroyedByPlayer(world, pos, blockState);
		}
		return removed;
	}

	/**
	 * Returns true if the specified block should be considered as the same plant
	 * @param block
	 * @return
	 */
	protected boolean isSamePlant(IBlockState blockState) {
		return blockState.getBlock() == this;
	}

	/**
	 * Sets the maximum height this plant should naturally grow
	 * @param maxHeight
	 * @return
	 */
	public BlockStackablePlant setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
		return this;
	}

	/**
	 * Returns the maximum height this plant should naturally grow
	 * @return
	 */
	public int getMaxHeight() {
		return this.maxHeight;
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		return (this.maxHeight > 1 && this.isSamePlant(state)) || SoilHelper.canSustainPlant(state);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		this.checkAndDropBlock(worldIn, pos, state);

		int height;
		for (height = 1; worldIn.getBlockState(pos.down(height)).getBlock() == this; ++height);

		if (this.canGrowUp(worldIn, pos, state, height)) {
			int currentAge = ((Integer)state.getValue(AGE)).intValue();

			if (currentAge == 15) {
				this.growUp(worldIn, pos);
				worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(0)), 4);
			} else {
				worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(currentAge + 1)), 4);
			}
		}
	}

	/**
	 * Returns whether the plant can grow higher
	 * @param world
	 * @param pos
	 * @param state
	 * @param height
	 * @return
	 */
	protected boolean canGrowUp(World world, BlockPos pos, IBlockState state, int height) {
		return world.isAirBlock(pos.up()) && (this.maxHeight == -1 || height < this.maxHeight);
	}

	/**
	 * Grows the plant one block higher
	 * @param world
	 * @param pos Position of the currently highest block of the plant
	 */
	protected void growUp(World world, BlockPos pos) {
		world.setBlockState(pos.up(), this.getDefaultState());
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		int height;
		for (height = 1; worldIn.getBlockState(pos.down(height)).getBlock() == this; ++height);
		return super.canPlaceBlockAt(worldIn, pos) && (this.maxHeight == -1 || height - 1 < this.maxHeight);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((Integer)state.getValue(AGE)).intValue();
	}

	@Override
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(AGE);
		if(this.maxHeight <= 1) {
			builder.ignore(IS_TOP, IS_BOTTOM);
		}
	}
}
