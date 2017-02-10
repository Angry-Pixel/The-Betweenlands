package thebetweenlands.common.item.equipment;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.capability.IPuppeteerCapability;
import thebetweenlands.client.event.handler.ItemTooltipHandler;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.KeyBindRegistry;
import thebetweenlands.util.NBTHelper;

public class ItemRingOfRecruitment extends ItemRing {
	public ItemRingOfRecruitment() {
		this.setMaxDamage(450);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advancedTooltips) {
		list.add(I18n.format("tooltip.ring.recruitment.bonus"));
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			String toolTip = I18n.format("tooltip.ring.recruitment", KeyBindRegistry.RADIAL_MENU.getDisplayName());
			list.addAll(ItemTooltipHandler.splitTooltip(toolTip, 1));
		} else {
			list.add(I18n.format("tooltip.press.shift"));
		}
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, IInventory inventory) {
		if(!entity.worldObj.isRemote && entity instanceof EntityPlayer) {
			int tickRate = 80;

			if(entity.hasCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null)) {
				IPuppeteerCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_PUPPETEER, null);
				int puppets = cap.getPuppets().size();
				NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
				if(puppets == 0) {
					tickRate = 0;
					nbt.setBoolean("ringActive", false);
				} else {
					tickRate = (int) Math.max(1, 30 - Math.pow(puppets, 0.5D) * 14);
					nbt.setBoolean("ringActive", true);
				}
			}


			if(tickRate > 0 && entity.ticksExisted % tickRate == 0) {
				this.removeXp((EntityPlayer)entity, 1);
			}
		}
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) { 
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.setBoolean("ringActive", false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean("ringActive");
	}

	public static boolean isRingActive(Entity entity) {
		if(entity instanceof EntityPlayer && ((EntityPlayer) entity).experienceTotal <= 0) {
			return false;
		}

		if(entity.hasCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null)) {
			IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
			IInventory inv = cap.getInventory(EnumEquipmentInventory.RING);

			boolean hasRing = false;

			for(int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if(stack != null && stack.getItem() == ItemRegistry.RING_OF_RECRUITMENT) {
					hasRing = true;
					break;
				}
			}

			return hasRing;
		}
		return false;
	}
}
