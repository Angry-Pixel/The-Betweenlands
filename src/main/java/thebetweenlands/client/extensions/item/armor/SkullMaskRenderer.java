package thebetweenlands.client.extensions.item.armor;

import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;

public class SkullMaskRenderer extends MaskRenderer {
	public static final SkullMaskRenderer INSTANCE = new SkullMaskRenderer();

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/misc/skull_mask.png");
	private static final ResourceLocation LEFT_TEXTURE = TheBetweenlands.prefix("textures/misc/skull_mask_side_left.png");
	private static final ResourceLocation RIGHT_TEXTURE = TheBetweenlands.prefix("textures/misc/skull_mask_side_right.png");

	@Override
	public ResourceLocation getOverlayTexture() {
		return TEXTURE;
	}

	@Override
	public ResourceLocation getSideOverlayTexture(boolean left) {
		return left ? LEFT_TEXTURE : RIGHT_TEXTURE;
	}
}
