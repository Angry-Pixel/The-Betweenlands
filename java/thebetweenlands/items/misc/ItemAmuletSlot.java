package thebetweenlands.items.misc;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesCircleGem;
import thebetweenlands.items.equipment.ItemAmulet;

public class ItemAmuletSlot extends Item {
	public ItemAmuletSlot() {
		this.setUnlocalizedName("thebetweenlands.amuletSlot");
		this.setTextureName("thebetweenlands:amuletSlot");

		this.setMaxStackSize(1);
		this.setMaxDamage(30);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(player.isSneaking() && player.capabilities.isCreativeMode) {
			if(!world.isRemote)
				removeAmuletSlot(player);
			player.swingItem();
		} else {
			addAmuletSlot(player, stack, player);
		}
		return stack;
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity) {
		if(player.capabilities.isCreativeMode || (!ItemAmulet.SUPPORTED_ENTITIES.isEmpty() && ItemAmulet.SUPPORTED_ENTITIES.contains(entity.getClass()))) {
			if(player.isSneaking() && player.capabilities.isCreativeMode) {
				if(!player.worldObj.isRemote)
					removeAmuletSlot(entity);
				player.swingItem();
			} else {
				addAmuletSlot(player, itemstack, entity);
			}
		}
		return true;
	}

	public static boolean addAmuletSlot(EntityPlayer player, ItemStack stack, EntityLivingBase entity) {
		EntityPropertiesCircleGem property = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesCircleGem.class);
		if(property != null) {
			if(player.capabilities.isCreativeMode || property.getAmuletSlots() < EntityPropertiesCircleGem.MAX_AMULET_SLOTS) {
				if(!player.worldObj.isRemote) {
					property.addAmuletSlot();
					if(!player.capabilities.isCreativeMode) {
						if(entity instanceof EntityPlayer) {
							stack.damageItem(5, player);
						} else {
							stack.damageItem(2, player);
						}
					}
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

	public static void removeAmuletSlot(EntityLivingBase entity) {
		EntityPropertiesCircleGem property = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesCircleGem.class);
		if(property != null) {
			property.removeAmuletSlot();
		}
	}
}
