package thebetweenlands.common.item.equipment;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.registries.KeyBindRegistry;
import thebetweenlands.util.NBTHelper;

import javax.annotation.Nullable;

public class ItemRingOfPower extends ItemRing {
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
}
