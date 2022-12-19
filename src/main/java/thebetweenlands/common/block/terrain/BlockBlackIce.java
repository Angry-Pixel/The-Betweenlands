package thebetweenlands.common.block.terrain;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockBreakable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.event.EventWinter;

public class BlockBlackIce extends BlockBreakable {
	public BlockBlackIce() {
		super(Material.ICE, false);
		this.setHardness(0.5F);
		this.setLightOpacity(3);
		this.setSoundType(SoundType.GLASS);
		this.slipperiness = 0.98F;
		this.setTickRandomly(true);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
		player.addStat(StatList.getBlockStats(this));
		player.addExhaustion(0.005F);

		if (this.canSilkHarvest(worldIn, pos, state, player) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
			java.util.List<ItemStack> items = new java.util.ArrayList<ItemStack>();
			items.add(this.getSilkTouchDrop(state));

			net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player);
			for (ItemStack is : items) {
				spawnAsEntity(worldIn, pos, is);
			}
		} else {
			int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
			harvesters.set(player);
			this.dropBlockAsItem(worldIn, pos, state, i);
			harvesters.set(null);
			Material material = worldIn.getBlockState(pos.down()).getMaterial();

			if (material.blocksMovement() || material.isLiquid()) {
				worldIn.setBlockState(pos, BlockRegistry.SWAMP_WATER.getDefaultState());
			}
		}
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (!EventWinter.isFroooosty(worldIn) || (worldIn.getLightFor(EnumSkyBlock.BLOCK, pos) > 11 - this.getDefaultState().getLightOpacity())) {
			this.turnIntoWater(worldIn, pos);
		}
	}

	protected void turnIntoWater(World worldIn, BlockPos pos) {
		worldIn.setBlockState(pos, BlockRegistry.SWAMP_WATER.getDefaultState());
		worldIn.neighborChanged(pos, BlockRegistry.SWAMP_WATER, pos);
	}

	@Override
	public EnumPushReaction getPushReaction(IBlockState state) {
		return EnumPushReaction.NORMAL;
	}
}