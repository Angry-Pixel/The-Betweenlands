package thebetweenlands.items;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraftforge.common.IShearable;
import thebetweenlands.creativetabs.ModCreativeTabs;

public class ItemSickle extends Item {
	public ItemSickle() {
		this.setUnlocalizedName("thebetweenlands.sickle");
		this.setTextureName("thebetweenlands:strictlyHerblore/tools/sickle");
		this.setMaxStackSize(1);
		this.setMaxDamage(500);
		ModCreativeTabs.herbLore.setTab(this);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
		if (player.worldObj.isRemote || player.capabilities.isCreativeMode) {
			return false;
		}
		Block block = player.worldObj.getBlock(x, y, z);
		if (block instanceof IShearable) {
			IShearable target = (IShearable)block;
			if (target.isShearable(itemstack, player.worldObj, x, y, z)) {
				ArrayList<ItemStack> drops = target.onSheared(itemstack, player.worldObj, x, y, z, EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemstack));
				Random rand = new Random();

				for(ItemStack stack : drops) {
					float f = 0.7F;
					double d  = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					double d1 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					double d2 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					EntityItem entityitem = new EntityItem(player.worldObj, (double)x + d, (double)y + d1, (double)z + d2, stack);
					entityitem.delayBeforeCanPickup = 10;
					player.worldObj.spawnEntityInWorld(entityitem);
				}

				itemstack.damageItem(1, player);
				player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(block)], 1);
			}
		}
		return false;
	}
}
