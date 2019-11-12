package thebetweenlands.common.item.shields;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.item.BLMaterialRegistry;

public class ItemSyrmoriteShield extends ItemSwatShield {
	public ItemSyrmoriteShield() {
		super(BLMaterialRegistry.TOOL_SYRMORITE);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.bl.syrmorite_shield", Minecraft.getMinecraft().gameSettings.keyBindSneak.getDisplayName(), Minecraft.getMinecraft().gameSettings.keyBindUseItem.getDisplayName()), 0));
	}
}
