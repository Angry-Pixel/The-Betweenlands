package thebetweenlands.common.item.equipment;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.registries.KeyBindRegistry;
import thebetweenlands.util.NBTHelper;

public class ItemRingOfPower extends ItemRing {
	public ItemRingOfPower() {
		this.setMaxDamage(1800);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
		list.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.ring.power.bonus"), 0));
		if (GuiScreen.isShiftKeyDown()) {
			String toolTip = I18n.format("tooltip.ring.power", KeyBindRegistry.RADIAL_MENU.getDisplayName());
			list.addAll(ItemTooltipHandler.splitTooltip(toolTip, 1));
		} else {
			list.add(I18n.format("tooltip.press.shift"));
		}
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, IInventory inventory) { 
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.setBoolean("ringActive", true);
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, IInventory inventory) { 
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.setBoolean("ringActive", false);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTag().getBoolean("ringActive");
	}
}
