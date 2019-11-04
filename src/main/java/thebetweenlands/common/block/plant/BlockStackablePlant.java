package thebetweenlands.common.block.plant;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
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
	protected boolean resetAge = true;

	protected final ThreadLocal<Boolean> harvesting = new ThreadLocal<>();

	public BlockStackablePlant() {
		this.setDefaultState(this.blockState.getBaseState().withProperty(IS_TOP, true).withProperty(IS_BOTTOM, false));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		return this.maxHeight != 1 ? Block.EnumOffsetType.NONE : Block.EnumOffsetType.XZ;
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
	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			this.removePlant(worldIn, pos, null, false);
		}
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		this.onBlockHarvested(world, pos, state, player);
		boolean removed = false;
		Boolean harvesting = this.harvesting.get(); //I'm sorry
		if(harvesting == null || !harvesting) {
			this.harvesting.set(true);
			int height;
			for (height = 1; this.isSamePlant(world.getBlockState(pos.up(height))); ++height);
			for (int offset = height - 1; (this.harvestAll && this.isSamePlant(world.getBlockState(pos.up(offset)))) || (!this.harvestAll && offset >= 0); offset--) {
				if(offset != 0) {
					BlockPos offsetPos = pos.up(offset);
					IBlockState blockState = world.getBlockState(offsetPos);
					boolean canHarvest = player.isCreative() ? false : blockState.getBlock().canHarvestBlock(world, offsetPos, player);
					boolean otherRemoved = this.removeOtherBlockAsPlayer(world, offsetPos, player, canHarvest);
					if(otherRemoved && canHarvest) {
						ItemStack stack = player.getHeldItemMainhand() == null ? null : player.getHeldItemMainhand().copy();
						blockState.getBlock().harvestBlock(world, player, offsetPos, blockState, world.getTileEntity(offsetPos), stack);
					}
				} else {
					removed = this.removePlant(world, pos, null, false);
				}
			}
			this.harvesting.set(false);
		}
		if(removed) {
			return true;
		}
		return this.removePlant(world, pos, player, willHarvest);
	}

	protected boolean removeOtherBlockAsPlayer(World world, BlockPos pos, EntityPlayer player, boolean canHarvest) {
		IBlockState blockState = world.getBlockState(pos);
		boolean removed = blockState.getBlock().removedByPlayer(blockState, world, pos, player, canHarvest);
		if (removed) {
			blockState.getBlock().onPlayerDestroy(world, pos, blockState);
		}
		return removed;
	}

	/**
	 * Removes the plant. Usually sets the block to air
	 * @param world
	 * @param pos
	 * @param player
	 * @param canHarvest
	 * @return
	 */
	protected boolean removePlant(World world, BlockPos pos, @Nullable EntityPlayer player, boolean canHarvest) {
		return world.setBlockState(pos, Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
	}

	/**
	 * Returns true if the specified block should be considered as the same plant
	 * @param blockState
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
		return ((this.maxHeight == -1 || this.maxHeight > 1) && this.isSamePlant(state)) || SoilHelper.canSustainPlant(state);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		this.checkAndDropBlock(worldIn, pos, state);

		if(this.canGrow(worldIn, pos, state)) {
			if(ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextFloat() <= this.getGrowthChance(worldIn, pos, state, rand))) {
				int currentAge = ((Integer)state.getValue(AGE)).intValue();

				worldIn.setBlockState(pos, state.withProperty(AGE, Math.min(currentAge + this.getGrowthSpeed(worldIn, pos, state, rand), 15)));
				
				if (currentAge >= 15) {
					int height;
					for (height = 1; this.isSamePlant(worldIn.getBlockState(pos.down(height))); ++height);

					if (this.canGrowUp(worldIn, pos, state, height)) {
						this.growUp(worldIn, pos);
					}

					worldIn.setBlockState(pos, state.withProperty(AGE, this.resetAge ? 0 : 15));
				}

				ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
			}
		}
	}
	
	/**
	 * Returns by how many steps it should age per growth chance
	 * @param world
	 * @param pos
	 * @param state
	 * @param rand
	 * @return
	 */
	protected int getGrowthSpeed(World world, BlockPos pos, IBlockState state, Random rand) {
		return 1;
	}

	/**
	 * Returns the growth chance
	 * @param world
	 * @param pos
	 * @param state
	 * @param rand
	 * @return
	 */
	protected float getGrowthChance(World world, BlockPos pos, IBlockState state, Random rand) {
		return 0.5F;
	}

	/**
	 * Returns whether the plant can grow
	 * @param world
	 * @param pos
	 * @param state
	 * @return
	 */
	protected boolean canGrow(World world, BlockPos pos, IBlockState state) {
		return true;
	}

	/**
	 * Returns whether the plant can grow a block higher
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
		for (height = 1; this.isSamePlant(worldIn.getBlockState(pos.down(height))); ++height);
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
	@SideOnly(Side.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(AGE);
		if(this.maxHeight == 1) {
			builder.ignore(IS_TOP, IS_BOTTOM);
		}
	}
}
