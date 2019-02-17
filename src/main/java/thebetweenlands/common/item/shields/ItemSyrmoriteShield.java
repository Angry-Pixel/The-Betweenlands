package thebetweenlands.common.item.shields;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.item.BLMaterialRegistry;

public class ItemSyrmoriteShield extends ItemSwatShield {
	public ItemSyrmoriteShield() {
		super(BLMaterialRegistry.TOOL_SYRMORITE);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.syrmorite_shield", Minecraft.getInstance().gameSettings.keyBindSneak.getDisplayName(), Minecraft.getInstance().gameSettings.keyBindUseItem.getDisplayName()), 0));
	}
}
