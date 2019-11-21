package thebetweenlands.common.item.equipment;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.registries.KeyBindRegistry;
import thebetweenlands.util.NBTHelper;

public class ItemRingOfPower extends ItemRing {
	public static final UUID POWER_SPEED_MODIFIER_ATTRIBUTE_UUID = UUID.fromString("ac457979-c0c4-4782-bfc3-53f995b21a4b");

	public ItemRingOfPower() {
		this.setMaxDamage(1800);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.bl.ring.power.bonus"), 0));
		if (GuiScreen.isShiftKeyDown()) {
			String toolTip = I18n.format("tooltip.bl.ring.power", KeyBindRegistry.RADIAL_MENU.getDisplayName());
			list.addAll(ItemTooltipHandler.splitTooltip(toolTip, 1));
		} else {
			list.add(I18n.format("tooltip.bl.press.shift"));
		}
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, IInventory inventory) { 
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.setBoolean("ringActive", true);

		if(entity instanceof EntityLivingBase) {
			IAttributeInstance speedAttrib = ((EntityLivingBase) entity).getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

			if(speedAttrib != null) {
				speedAttrib.applyModifier(new AttributeModifier(POWER_SPEED_MODIFIER_ATTRIBUTE_UUID, "Ring of power speed modifier", 0.2D, 2));
			}
		}
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) { 
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.setBoolean("ringActive", false);

		if(entity instanceof EntityLivingBase) {
			IAttributeInstance speedAttrib = ((EntityLivingBase) entity).getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

			if(speedAttrib != null) {
				speedAttrib.removeModifier(POWER_SPEED_MODIFIER_ATTRIBUTE_UUID);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().getBoolean("ringActive");
	}
}
