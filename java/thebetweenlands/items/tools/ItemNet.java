package thebetweenlands.items.tools;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.entities.mobs.EntityFirefly;
import thebetweenlands.entities.mobs.EntityGecko;
import thebetweenlands.items.BLItemRegistry;

/**
 * Created by Bart on 27/11/2015.
 */
public class ItemNet extends Item {

	public ItemNet() {
		this.setUnlocalizedName("thebetweenlands.net");
		this.setTextureName("thebetweenlands:net");
		this.maxStackSize = 1;
		this.setMaxDamage(32);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack itemStack, EntityPlayer player, EntityLivingBase entity) {
		if (entity instanceof EntityFirefly || entity instanceof EntityGecko) {
			ItemStack itemStack1;
			if (entity instanceof EntityFirefly)
				itemStack1 = new ItemStack(BLItemRegistry.fireFly);
			else
				itemStack1 = new ItemStack(BLItemRegistry.gecko);
			if (player.getHeldItem() != null && player.getHeldItem().getItem() == this && !player.worldObj.isRemote) {
				if (((EntityLiving) entity).getCustomNameTag() != null)
					itemStack1.setStackDisplayName(((EntityLiving) entity).getCustomNameTag());
				player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, itemStack1));
				entity.setDead();
				itemStack.damageItem(1, player);
			}
			player.swingItem();
		}
		return true;
	}
}
