package thebetweenlands.common.block.farming;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistryOld.ICustomItemBlock;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockMiddleFruitBush extends BlockGenericCrop implements ICustomItemBlock {
	public BlockMiddleFruitBush() {
		this.setCreativeTab(null);
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(state.get(AGE) >= 15) {
			if(!world.isRemote()) {
				this.dropBlockAsItem(world, pos, state, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, playerIn.getHeldItem(hand)));
				world.setBlockState(pos, state.with(AGE, 8));
				this.harvestAndUpdateSoil(world, pos, 10);
			}
			return true;
		}
		return false;
	}

	@Override
	public int getCropDrops(IWorldReader world, BlockPos pos, Random rand, int fortune) {
		IBlockState state = world.getBlockState(pos);
		if(state.get(AGE) >= 15) {
			return 1 + rand.nextInt(3 + fortune);
		}
		return 0;
	}

	@Override
	protected float getGrowthChance(World world, BlockPos pos, IBlockState state, Random rand) {
		return 0.25F;
	}

	@Override
	protected IntegerProperty createStageProperty() {
		return IntegerProperty.create("stage", 0, 5);
	}

	@Override
	public ItemStack getSeedDrop(IWorldReader world, BlockPos pos, Random rand) {
		return new ItemStack(ItemRegistry.MIDDLE_FRUIT_BUSH_SEEDS);	
	}

	@Override
	public ItemStack getCropDrop(IWorldReader world, BlockPos pos, Random rand) {
		return this.isDecayed(world, pos) ? ItemStack.EMPTY : new ItemStack(ItemRegistry.MIDDLE_FRUIT);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(ItemRegistry.MIDDLE_FRUIT_BUSH_SEEDS);
	}

	@Override
	public ItemBlock getItemBlock() {
		return null;
	}
}
