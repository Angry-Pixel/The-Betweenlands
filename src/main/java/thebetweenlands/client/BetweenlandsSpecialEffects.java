package thebetweenlands.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import thebetweenlands.api.misc.Fog;
import thebetweenlands.client.handler.FogHandler;
import thebetweenlands.client.sky.BLSkyRenderer;

public class BetweenlandsSpecialEffects extends DimensionSpecialEffects {

	private final BLSkyRenderer skyRenderer;

	public BetweenlandsSpecialEffects() {
		super(50, true, SkyType.NORMAL, false, false);
		this.skyRenderer = new BLSkyRenderer();
	}

	@Override
	public Vec3 getBrightnessDependentFogColor(Vec3 color, float light) {
		if (BLSkyRenderer.drawOverworldSky) {
			return new Vec3(0.75F, 0.84375F, 1.0F).scale(light);
		}
		Fog fog = FogHandler.getFogState().getFog(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
		return new Vec3(fog.getRed(), fog.getGreen(), fog.getBlue());
	}

	/**
	 * Called first thing by LevelRenderer.renderSky if returns true, renderSky exits immediately
	 * We can return false and let default overworld continue to save duplicating it here
	 * @param level
	 * @param ticks
	 * @param partialTick
	 * @param viewMatrix
	 * @param camera
	 * @param projectionMatrix
	 * @param isFoggy
	 * @param setupFog
	 * @return
	 */
	@Override
	public boolean renderSky(ClientLevel level, int ticks, float partialTick, Matrix4f viewMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
		this.skyRenderer.render(level, partialTick, viewMatrix, camera, projectionMatrix, isFoggy, setupFog);
		return true;	// Skip vanilla rendering
	}

	public BLSkyRenderer getSkyRenderer() {
		return this.skyRenderer;
	}

	// When drawing clouds in RiftRender OverWorldRiftSkyRenderer this function is checked: true = no clouds, false = clouds
	// Use BLSkyRenderer sky hack flag
	@Override
	public boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
		return !BLSkyRenderer.drawOverworldSky;
	}

	// Disable sunrise fog effects when not drawing overworld sky
	@Override
	public float[] getSunriseColor(float timeOfDay, float partialTicks) {
		if (!BLSkyRenderer.drawOverworldSky) {
			return null;
		}
		float[] sunriseColors = new float[4];
		float f1 = Mth.cos(timeOfDay * Mth.TWO_PI) - 0.0F;

		if (f1 >= -0.4F && f1 <= 0.4F) {
			float f3 = (f1 + 0.0F) / 0.4F * 0.5F + 0.5F;
			float f4 = 1.0F - (1.0F - Mth.sin(f3 * Mth.PI)) * 0.99F;
			f4 = f4 * f4;
			sunriseColors[0] = f3 * 0.3F + 0.7F;
			sunriseColors[1] = f3 * f3 * 0.7F + 0.2F;
			sunriseColors[2] = f3 * f3 * 0.0F + 0.2F;
			sunriseColors[3] = f4;
			return sunriseColors;
		} else {
			return null;
		}
	}

	@Override
	public boolean isFoggyAt(int x, int y) {
		return false;
	}
}
