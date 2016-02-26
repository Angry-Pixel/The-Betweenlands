package thebetweenlands.items.misc;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesCircleGem;

public class ItemAmuletSlot extends Item {
	public ItemAmuletSlot() {
		this.setUnlocalizedName("thebetweenlands.amuletSlot");

		this.setMaxStackSize(1);
		this.setMaxDamage(16);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		addAmuletSlot(player, stack, player);
		return stack;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity) {
		if(!ItemAmulet.supportedEntities.isEmpty() && ItemAmulet.supportedEntities.contains(entity.getClass())) {
			addAmuletSlot(player, itemstack, entity);
		}
		return true;
	}

	public static boolean addAmuletSlot(EntityPlayer player, ItemStack stack, EntityLivingBase entity) {
		EntityPropertiesCircleGem property = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesCircleGem.class);
		if(property != null) {
			if(property.getAmuletSlots() < EntityPropertiesCircleGem.MAX_AMULET_SLOTS) {
				if(!player.worldObj.isRemote) {
					property.addAmuletSlot();
					if(!player.capabilities.isCreativeMode) 
						stack.damageItem(1, entity);
					player.addChatMessage(new ChatComponentTranslation("chat.amulet.slot.added"));
				}
				player.swingItem();
				return true;
			} else if(!player.worldObj.isRemote) {
				player.addChatMessage(new ChatComponentTranslation("chat.amulet.slot.full"));
			}
		}
		return false;
	}
}
