package thebetweenlands.items.tools;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import thebetweenlands.creativetabs.ModCreativeTabs;

public class ItemSyrmoriteShears extends Item {
	public ItemSyrmoriteShears() {
		this.setUnlocalizedName("thebetweenlands.syrmoriteShears");
		this.setTextureName("thebetweenlands:syrmoriteShears");
		this.setMaxStackSize(1);
		this.setMaxDamage(500);
		ModCreativeTabs.gears.setTab(this);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
		if (player.worldObj.isRemote || player.capabilities.isCreativeMode) {
			return false;
		}
		Block block = player.worldObj.getBlock(x, y, z);
		if (block instanceof ISyrmoriteShearable) {
			ISyrmoriteShearable target = (ISyrmoriteShearable)block;
			if(target.isSyrmoriteShearable(itemstack, player.worldObj, x, y, z)) {
				Random rand = new Random();
				ItemStack specialDrop = target.getSyrmoriteShearableSpecialDrops(block, x, y, z, player.worldObj.getBlockMetadata(x, y, z));
				if(specialDrop != null) {
					float f = 0.7F;
					double d  = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					double d1 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					double d2 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					EntityItem entityitem = new EntityItem(player.worldObj, (double)x + d, (double)y + d1, (double)z + d2, specialDrop);
					entityitem.delayBeforeCanPickup = 10;
					player.worldObj.spawnEntityInWorld(entityitem);
				} else {
					float f = 0.7F;
					double d  = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					double d1 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					double d2 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
					EntityItem entityitem = new EntityItem(player.worldObj, (double)x + d, (double)y + d1, (double)z + d2, new ItemStack(Item.getItemFromBlock(block)));
					entityitem.delayBeforeCanPickup = 10;
					player.worldObj.spawnEntityInWorld(entityitem);
				}
				itemstack.damageItem(1, player);
				player.worldObj.setBlock(x, y, z, Blocks.air);
				player.addStat(StatList.mineBlockStatArray[Block.getIdFromBlock(block)], 1);
				return true;
			}
		}
		return false;
	}
}
