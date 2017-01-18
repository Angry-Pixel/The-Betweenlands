package thebetweenlands.common.item.equipment;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemRingOfPower extends ItemRing {
	public ItemRingOfPower() {
		this.setMaxDamage(1800);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advancedTooltips) {
		list.add(I18n.format("ring.power.bonus"));
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			String toolTip = I18n.format("item.thebetweenlands.ringOfPower.tooltip");
			//list.addAll(ItemTooltipHandler.splitTooltip(toolTip, 1));
			list.add(toolTip);
		} else {
			list.add(I18n.format("item.thebetweenlands.press.shift"));
		}
	}

}
