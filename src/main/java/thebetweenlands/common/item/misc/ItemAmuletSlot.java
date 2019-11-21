package thebetweenlands.common.item.misc;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.equipment.ItemAmulet;
import thebetweenlands.common.registries.CapabilityRegistry;

public class ItemAmuletSlot extends Item {
	public ItemAmuletSlot() {
		this.setMaxStackSize(1);
		this.setMaxDamage(30);
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.EPIC;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(!world.isRemote) {
			if(player.isSneaking() && player.capabilities.isCreativeMode) {
				removeAmuletSlot(player);
			} else {
				addAmuletSlot(player, stack, player);
			}
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
		if(!player.world.isRemote) {
			if(ItemAmulet.canPlayerAddAmulet(player, target) || player.capabilities.isCreativeMode) {
				if(player.isSneaking() && player.capabilities.isCreativeMode) {
					removeAmuletSlot(target);
				} else {
					if(addAmuletSlot(player, stack, target)) {
						player.swingArm(hand);
					}
				}
			}
		}
		return true;
	}

	public static boolean addAmuletSlot(EntityPlayer player, ItemStack stack, EntityLivingBase entity) {
		IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
		if(cap != null) {

			if(cap.getAmuletSlots() < 3 || player.capabilities.isCreativeMode) {
				cap.setAmuletSlots(cap.getAmuletSlots() + 1);

				if(!player.capabilities.isCreativeMode) {
					if(entity instanceof EntityPlayer) {
						stack.damageItem(5, player);
					} else {
						stack.damageItem(2, player);
					}
				}

				player.sendStatusMessage(new TextComponentTranslation("chat.amulet.slot.added"), true);

				return true;
			} else {
				player.sendStatusMessage(new TextComponentTranslation("chat.amulet.slot.full"), true);
			}
		}
		return false;
	}

	public static void removeAmuletSlot(EntityLivingBase entity) {
		IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
		if(cap != null) {
			if(cap.getAmuletSlots() > 1) {
				cap.setAmuletSlots(cap.getAmuletSlots() - 1);
			}
		}
	}
}
