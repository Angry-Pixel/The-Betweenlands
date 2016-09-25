package thebetweenlands.common.item.tools;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemNet extends Item {
	public ItemNet() {
		this.maxStackSize = 1;
		this.setMaxDamage(32);
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
		if (target instanceof EntityFirefly || target instanceof EntityGecko) {
			ItemStack receivedItem;
			if (target instanceof EntityFirefly) {
				receivedItem = new ItemStack(ItemRegistry.FIREFLY);
			} else {
				receivedItem = new ItemStack(ItemRegistry.GECKO);
			}
			if (player.getHeldItem(hand) != null && player.getHeldItem(hand).getItem() == this && !player.worldObj.isRemote) {
				if (target.getCustomNameTag() != null) {
					receivedItem.setStackDisplayName(target.getCustomNameTag());
				}
				player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, receivedItem));
				target.setDead();
				stack.damageItem(1, player);
			}
			player.swingArm(hand);
		}
		return true;
	}
}
