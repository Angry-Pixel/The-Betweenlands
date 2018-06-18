package thebetweenlands.client.render.sky;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;

public class RiftVariant {
	public static final RiftVariant DEFAULT = new RiftVariant(
			new ResourceLocation(ModInfo.ID, "textures/sky/rifts/sky_rift_mask_1.png"),
			new ResourceLocation(ModInfo.ID, "textures/sky/rifts/sky_rift_overlay_1.png"),
			null,
			0, 360,
			-90, 90,
			0, 360,
			0.5f, 1,
			true, true);

	private final float minYaw, maxYaw, minPitch, maxPitch, minRoll, maxRoll, minScale, maxScale;
	private final boolean mirrorU, mirrorV;
	private final ResourceLocation maskTexture, overlayTexture;
	
	@Nullable
	private final ResourceLocation altOverlayTexture;

	public RiftVariant(ResourceLocation maskTexture, ResourceLocation overlayTexture, ResourceLocation altOverlayTexture, 
			float minYaw, float maxYaw, float minPitch, float maxPitch, float minRoll, float maxRoll, float minScale, float maxScale, boolean mirrorU,
			boolean mirrorV) {
		this.maskTexture = maskTexture;
		this.overlayTexture = overlayTexture;
		this.altOverlayTexture = altOverlayTexture;
		this.minYaw = minYaw;
		this.maxYaw = maxYaw;
		this.minPitch = minPitch;
		this.maxPitch = maxPitch;
		this.minRoll = minRoll;
		this.maxRoll = maxRoll;
		this.minScale = minScale;
		this.maxScale = maxScale;
		this.mirrorU = mirrorU;
		this.mirrorV = mirrorV;
	}

	public ResourceLocation getMaskTexture() {
		return this.maskTexture;
	}

	public ResourceLocation getOverlayTexture() {
		return this.overlayTexture;
	}

	@Nullable
	public ResourceLocation getAltOverlayTexture() {
		return this.altOverlayTexture;
	}

	public float getMinYaw() {
		return this.minYaw;
	}

	public float getMaxYaw() {
		return this.maxYaw;
	}

	public float getMinPitch() {
		return this.minPitch;
	}

	public float getMaxPitch() {
		return this.maxPitch;
	}

	public float getMinRoll() {
		return this.minRoll;
	}

	public float getMaxRoll() {
		return this.maxRoll;
	}
	
	public float getMinScale() {
		return this.minScale;
	}
	
	public float getMaxScale() {
		return this.maxScale;
	}
	
	public boolean getMirrorU() {
		return this.mirrorU;
	}
	
	public boolean getMirrorV() {
		return this.mirrorV;
	}
}
