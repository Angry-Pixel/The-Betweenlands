package thebetweenlands.common.item.tools;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.corrosion.CorrosionHelper;
import thebetweenlands.common.item.corrosion.ICorrodible;

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
}
