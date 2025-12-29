package thebetweenlands.client.gui.overlay;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.DataComponentRegistry;

public class CorrosiveBootsOverlay implements IItemDecorator {
    private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/item/corrosive_boots_overlay.png");

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
    		if (stack.has(DataComponentRegistry.CORROSIVE.get()))
				if (stack.get(DataComponentRegistry.CORROSIVE.get()))
					guiGraphics.blitSprite(TEXTURE, xOffset, yOffset, 16, 16);
		return true;
    }
}
