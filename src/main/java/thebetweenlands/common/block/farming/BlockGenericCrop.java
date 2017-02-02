package thebetweenlands.common.block.farming;

import java.util.ArrayList;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.common.block.BlockStateContainerHelper;
import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.block.plant.BlockStackablePlant;
import thebetweenlands.common.tile.TileEntityDugSoil;
import thebetweenlands.util.AdvancedStateMap;

public class BlockGenericCrop extends BlockStackablePlant implements IGrowable {
	public static final PropertyBool DECAYED = PropertyBool.create("decayed");
	public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 3);

	public BlockGenericCrop() {
		this.harvestAll = true;
		this.resetAge = false;
		this.setMaxHeight(1);
		this.setDefaultState(this.getDefaultState().withProperty(DECAYED, false));
	}

	public boolean isDecayed(IBlockAccess world, BlockPos pos) {
		for(int i = 0; i < this.maxHeight + 1; i++) {
			IBlockState blockState = world.getBlockState(pos);
			if(blockState.getBlock() instanceof BlockGenericDugSoil) {
				return blockState.getValue(BlockGenericDugSoil.DECAYED);
			}
			pos = pos.down();
		}
		return false;
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
		player.addStat(StatList.getBlockStats(this));
		player.addExhaustion(0.025F);
		//Dropping logic moved to #onBlockHarvested
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		if(!player.isCreative()) {
			ItemStack stack = player.getHeldItemMainhand() != null ? player.getHeldItemMainhand().copy() : null;
			if (this.canSilkHarvest(worldIn, pos, state, player) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
				java.util.List<ItemStack> items = new java.util.ArrayList<ItemStack>();
				ItemStack itemstack = this.createStackedBlock(state);

				if (itemstack != null) {
					items.add(itemstack);
				}

				net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player);
				for (ItemStack item : items) {
					spawnAsEntity(worldIn, pos, item);
				}
			} else {
				this.harvesters.set(player);
				int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
				this.dropBlockAsItem(worldIn, pos, state, i);
				this.harvesters.set(null);
			}

			//Remove 1 compost after harvesting fully grown crop
			if(state.getValue(AGE) >= 15) {
				this.consumeCompost(worldIn, pos);
			}
		}

		super.onBlockHarvested(worldIn, pos, state, player);
	}

	/**
	 * Tries to consume 1 compost if the block below is dug soil
	 * @param world
	 * @param pos
	 */
	protected void consumeCompost(World world, BlockPos pos) {
		IBlockState stateDown = world.getBlockState(pos.down());
		if(stateDown.getBlock() instanceof BlockGenericDugSoil) {
			TileEntityDugSoil te = BlockGenericDugSoil.getTile(world, pos.down());
			if(te != null && te.isComposted()) {
				te.setCompost(te.getCompost() - 1);
			}
		}
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		Random rand = world instanceof World ? ((World)world).rand : RANDOM;

		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		ItemStack seedDrop = this.getSeedDrop(world, pos, rand);
		ItemStack cropDrop = this.getCropDrop(world, pos, rand);

		if(seedDrop != null) {
			for(int i = 0; i < this.getSeedDrops(world, pos, rand, fortune); i++) {
				ret.add(seedDrop);
			}
		}

		if(cropDrop != null) {
			for(int i = 0; i < this.getCropDrops(world, pos, rand, fortune); i++) {
				ret.add(cropDrop);
			}
		}

		return ret;
	}

	/**
	 * Returns the number of seeds to drop
	 * @param world
	 * @param pos
	 * @param rand
	 * @param fortune
	 * @return
	 */
	public int getSeedDrops(IBlockAccess world, BlockPos pos, Random rand, int fortune) {
		IBlockState state = world.getBlockState(pos);
		return 1 + (state.getValue(AGE) >= 15 ? (rand.nextInt(3) == 0 ? 1 : 0) + fortune : 0);
	}

	/**
	 * Returns the number of crops to drop
	 * @param world
	 * @param pos
	 * @param rand
	 * @param fortune
	 * @return
	 */
	public int getCropDrops(IBlockAccess world, BlockPos pos, Random rand, int fortune) {
		IBlockState state = world.getBlockState(pos);
		if(state.getValue(AGE) >= 15) {
			return 2 + rand.nextInt(3) + fortune;
		}
		return 0;
	}

	/**
	 * Returns the seed item to drop
	 * @param world
	 * @param pos
	 * @param rand
	 * @return
	 */
	public ItemStack getSeedDrop(IBlockAccess world, BlockPos pos, Random rand) {
		return null;	
	}

	/**
	 * Returns the crop item to drop
	 * @param world
	 * @param pos
	 * @param rand
	 * @return
	 */
	public ItemStack getCropDrop(IBlockAccess world, BlockPos pos, Random rand) {
		return null;	
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		state = super.getActualState(state, worldIn, pos);
		return state.withProperty(DECAYED, this.isDecayed(worldIn, pos)).withProperty(STAGE, MathHelper.floor_float(state.getValue(AGE) / 15.0f * 3.0f));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return BlockStateContainerHelper.extendBlockstateContainer(super.createBlockState(), DECAYED, STAGE);
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		return (this.maxHeight > 1 && this.isSamePlant(state)) || SoilHelper.canSustainCrop(state);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState state = worldIn.getBlockState(pos.down());
		return super.canPlaceBlockAt(worldIn, pos) && 
				!(state.getBlock() instanceof BlockGenericDugSoil && state.getValue(BlockGenericDugSoil.DECAYED)) &&
				!(state.getBlock() instanceof BlockGenericCrop && state.getValue(AGE) < 15);
	}

	@Override
	protected boolean canGrow(World world, BlockPos pos, IBlockState state) {
		return !state.getValue(DECAYED);
	}

	@Override
	protected void growUp(World world, BlockPos pos) {
		world.setBlockState(pos.up(), this.getDefaultState().withProperty(DECAYED, world.getBlockState(pos).getValue(DECAYED)));
	}

	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		if(this.isDecayed(worldIn, pos)) {
			return false;
		}
		if(state.getValue(AGE) < 15) {
			return true;
		}
		int height;
		for (height = 1; worldIn.getBlockState(pos.down(height)).getBlock() == this; ++height);
		return this.canGrowUp(worldIn, pos, state, height);
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		int age = state.getValue(AGE) + MathHelper.getRandomIntegerInRange(worldIn.rand, 2, 5);
		if(age > 15) {
			age = 15;
			int height;
			for (height = 1; worldIn.getBlockState(pos.down(height)).getBlock() == this; ++height);
			if(this.canGrowUp(worldIn, pos, state, height)) {
				this.growUp(worldIn, pos);
			}
		}
		worldIn.setBlockState(pos, state.withProperty(AGE, age));
	}

	@Override
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		super.setStateMapper(builder);
		builder.ignore(DECAYED).withPropertySuffixTrue(DECAYED, "decayed");
	}
}
