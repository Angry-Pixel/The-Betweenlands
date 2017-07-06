package thebetweenlands.common.block.farming;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockMiddleFruitBush extends BlockGenericCrop implements ICustomItemBlock {
	public BlockMiddleFruitBush() {
		this.setCreativeTab(null);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(state.getValue(AGE) >= 15) {
			if(!world.isRemote) {
				ItemStack heldItem = player.getHeldItem(hand);
				this.dropBlockAsItem(world, pos, state, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, heldItem));
				world.setBlockState(pos, state.withProperty(AGE, 8));
				this.consumeCompost(world, pos, 10);
			}
			return true;
		}
		return false;
	}

	@Override
	public int getCropDrops(IBlockAccess world, BlockPos pos, Random rand, int fortune) {
		IBlockState state = world.getBlockState(pos);
		if(state.getValue(AGE) >= 15) {
			return 1 + rand.nextInt(3 + fortune);
		}
		return 0;
	}

	@Override
	protected float getGrowthChance(World world, BlockPos pos, IBlockState state, Random rand) {
		return 0.25F;
	}

	@Override
	protected PropertyInteger createStageProperty() {
		return PropertyInteger.create("stage", 0, 5);
	}

	@Override
	public ItemStack getSeedDrop(IBlockAccess world, BlockPos pos, Random rand) {
		return new ItemStack(ItemRegistry.MIDDLE_FRUIT_BUSH_SEEDS);	
	}

	@Override
	public ItemStack getCropDrop(IBlockAccess world, BlockPos pos, Random rand) {
		return this.isDecayed(world, pos) ? null : new ItemStack(ItemRegistry.MIDDLE_FRUIT);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@Nullable
	public Item getRenderedItem() {
		return ItemRegistry.MIDDLE_FRUIT_BUSH_SEEDS;
	}
}
