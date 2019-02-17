package thebetweenlands.common.item.tools;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import thebetweenlands.api.item.IAnimatorRepairable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemNet extends Item implements IAnimatorRepairable {
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
				if (receivedItem.getTag() == null)
					receivedItem.setTagCompound(new NBTTagCompound());
				receivedItem.getTag().setFloat("Health", target.getHealth());
			}
			if (!player.getHeldItem(hand).isEmpty() && player.getHeldItem(hand).getItem() == this && !player.world.isRemote) {
				if (!target.getCustomNameTag().isEmpty()) {
					receivedItem.setStackDisplayName(target.getCustomNameTag());
				}
				player.world.spawnEntity(new EntityItem(player.world, player.posX, player.posY, player.posZ, receivedItem));
				target.setDead();
				stack.damageItem(1, player);
			}
			player.swingArm(hand);
		}
		return true;
	}

	@Override
	public int getMinRepairFuelCost(ItemStack stack) {
		return 2;
	}

	@Override
	public int getFullRepairFuelCost(ItemStack stack) {
		return 8;
	}

	@Override
	public int getMinRepairLifeCost(ItemStack stack) {
		return 4;
	}

	@Override
	public int getFullRepairLifeCost(ItemStack stack) {
		return 12;
	}
}
