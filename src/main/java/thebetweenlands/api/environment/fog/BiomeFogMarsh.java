package thebetweenlands.api.environment.fog;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.util.FogGenerator;

public class BiomeFogMarsh extends BiomeFogBetweenlands {
	private FogGenerator fogGenerator;
	private float fogRangeInterpolateStart = 0.0F;
	private float fogRangeInterpolateEnd = 0.0F;

	public BiomeFogMarsh() {
		super();
	}

	@Override
	public void updateFog() {
		if(fogGenerator == null) { //|| fogGenerator.getSeed() != mc.level.getSeed()) {
				fogGenerator = new FogGenerator(0);
		}
		float[] range = this.fogGenerator.getFogRange(0.0F, 1.0F);
		this.fogRangeInterpolateStart = range[0];
		this.fogRangeInterpolateEnd = range[1];
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public float getFogStart(float farPlaneDistance, int mode) {
		float fogStart = Math.min(10, super.getFogStart(farPlaneDistance, mode));

		Entity viewEntity = Minecraft.getInstance().getCameraEntity();
		if (viewEntity == null || viewEntity.yo <= TheBetweenlands.CAVE_START)
			return fogStart;

		float fogEnd = super.getFogEnd(farPlaneDistance, mode);

		return fogStart + (fogEnd - fogStart) * this.fogRangeInterpolateStart;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public float getFogEnd(float farPlaneDistance, int mode) {
		float fogEnd = super.getFogEnd(farPlaneDistance, mode);

		Entity viewEntity = Minecraft.getInstance().getCameraEntity();
		if (viewEntity == null || viewEntity.yo <= TheBetweenlands.CAVE_START)
			return fogEnd;

		float fogStart = Math.min(10, super.getFogStart(farPlaneDistance, mode));

		return fogStart + ((fogEnd - fogStart) * this.fogRangeInterpolateEnd + 16.0F);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public float[] getFogRGB() {
		Entity viewEntity = Minecraft.getInstance().getCameraEntity();

		if (viewEntity == null || viewEntity.yo <= TheBetweenlands.CAVE_START)
			return super.getFogRGB();

		float[] targetFogColor = super.getFogRGB().clone();
		float fogBrightness = 110.0F - this.fogRangeInterpolateEnd * 110.0F;

		if (fogBrightness < 0) {
			fogBrightness = 0.0f;
		} else if (fogBrightness > 110) {
			fogBrightness = 110;
		}
		for (int i = 0; i < 3; i++) {
			float diff = 1.0F - targetFogColor[i];
			targetFogColor[i] = targetFogColor[i] + (diff * fogBrightness/ 255.0F);
		}

		return targetFogColor;
	}
}
