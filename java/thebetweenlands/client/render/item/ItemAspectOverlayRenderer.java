package thebetweenlands.client.render.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class ItemAspectOverlayRenderer implements IItemRenderer {
	@Override
	public boolean handleRenderType(ItemStack itemStack, ItemRenderType type) {
		return !AspectOverlayRenderHelper.shouldIgnoreHook() && type == ItemRenderType.INVENTORY;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack itemStack, Object... data) {
		AspectOverlayRenderHelper.renderOverlay(type, itemStack);
	}
}
