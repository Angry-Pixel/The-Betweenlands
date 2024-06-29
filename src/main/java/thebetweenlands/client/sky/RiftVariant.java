package thebetweenlands.client.sky;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;

public record RiftVariant(ResourceLocation maskTexture, ResourceLocation overlayTexture, @Nullable ResourceLocation altOverlayTexture, float minYaw, float maxYaw, float minPitch, float maxPitch, float minRoll, float maxRoll, float minScale, float maxScale, boolean mirrorU, boolean mirrorV) {
	public static final RiftVariant DEFAULT = new RiftVariant(
		TheBetweenlands.prefix("textures/sky/rifts/sky_rift_mask_1.png"),
		TheBetweenlands.prefix("textures/sky/rifts/sky_rift_overlay_1.png"),
		null,
		0, 360,
		-90, 90,
		0, 360,
		0.5f, 1,
		true, true);
}
