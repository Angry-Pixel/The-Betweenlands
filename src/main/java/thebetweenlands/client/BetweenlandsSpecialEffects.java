package thebetweenlands.client;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.sky.BLWeatherRenderer;
import thebetweenlands.common.registries.EnvironmentEventRegistry;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.WorldStorageGetter;

import javax.annotation.Nullable;

public class BetweenlandsSpecialEffects extends DimensionSpecialEffects {

	public BetweenlandsSpecialEffects() {
		super(192, true, SkyType.NORMAL, false, false);
	}

	//TODO
	@Override
	public Vec3 getBrightnessDependentFogColor(Vec3 color, float light) {
		return new Vec3(0.753f, 0.847f, 1.0f).scale(light);
	}

	@Nullable
	@Override
	public float[] getSunriseColor(float timeOfDay, float partialTicks) {
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

	@Override
	public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
		boolean snowing = false;
		BetweenlandsWorldStorage storage = WorldStorageGetter.getNullable(level);
		if (storage != null) {
			snowing = storage.getEnvironmentEventRegistry().isEventActive(EnvironmentEventRegistry.SNOWFALL.getId());
		}
		BLWeatherRenderer.INSTANCE.render(snowing, level, lightTexture, partialTick, camX, camY, camZ);
		return true;
	}

	@Override
	public boolean tickRain(ClientLevel level, int ticks, Camera camera) {
		return true;
	}
}
