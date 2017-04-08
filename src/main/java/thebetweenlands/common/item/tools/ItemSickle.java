package thebetweenlands.common.item.tools;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.block.ISickleHarvestable;
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.api.item.ICorrodible;
import thebetweenlands.client.tab.BLCreativeTabs;

public class ItemSickle extends Item implements ICorrodible {
	public ItemSickle() {
		this.setUnlocalizedName("thebetweenlands.sickle");
		this.setMaxStackSize(1);
		this.setMaxDamage(2500);
		this.setCreativeTab(BLCreativeTabs.GEARS);
		CorrosionHelper.addCorrosionPropertyOverrides(this);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
		boolean shouldDrop = player.worldObj.rand.nextFloat() <= 1.0F * CorrosionHelper.getModifier(itemstack);
		if (player.worldObj.isRemote || player.capabilities.isCreativeMode || !shouldDrop)
			return false;
		Block block = player.worldObj.getBlockState(pos).getBlock();
		if (block instanceof ISickleHarvestable) {
			ISickleHarvestable target = (ISickleHarvestable)block;
			if (target.isHarvestable(itemstack, player.worldObj, pos)) {
				List<ItemStack> drops = target.getHarvestableDrops(itemstack, player.worldObj, pos, EnchantmentHelper.getEnchantmentLevel(Enchantment.getEnchantmentByLocation("fortune"), itemstack));
				if(drops == null || drops.isEmpty())
					return false;
				Random rand = new Random();
				for(ItemStack stack : drops) {
					float offset = 0.7F;
					double rx  = (double)(rand.nextFloat() * offset) + (double)(1.0F - offset) * 0.5D;
					double ry = (double)(rand.nextFloat() * offset) + (double)(1.0F - offset) * 0.5D;
					double rz = (double)(rand.nextFloat() * offset) + (double)(1.0F - offset) * 0.5D;
					EntityItem entityitem = new EntityItem(player.worldObj, (double)pos.getX() + rx, (double)pos.getY() + ry, (double)pos.getZ() + rz, stack);
					entityitem.setDefaultPickupDelay();
					player.worldObj.spawnEntityInWorld(entityitem);
				}
				itemstack.damageItem(1, player);
				block.onBlockHarvested(player.worldObj, pos, player.worldObj.getBlockState(pos), player);
				player.worldObj.setBlockToAir(pos);
				player.addStat(StatList.getBlockStats(block), 1);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return CorrosionHelper.shouldCauseBlockBreakReset(oldStack, newStack);
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return CorrosionHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}
	
	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState state) {
		return CorrosionHelper.getStrVsBlock(super.getStrVsBlock(stack, state), stack, state); 
	}
	
	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity holder, int slot, boolean isHeldItem) {
		CorrosionHelper.updateCorrosion(itemStack, world, holder, slot, isHeldItem);
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> lines, boolean advancedItemTooltips) {
		CorrosionHelper.addCorrosionTooltips(itemStack, player, lines, advancedItemTooltips);
	}
}
