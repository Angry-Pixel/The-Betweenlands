package thebetweenlands.api.misc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.api.event.UpdateFogEvent;
import thebetweenlands.api.misc.Fog.FogType;
import thebetweenlands.api.misc.Fog.MutableFog;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.BiomeBetweenlands;

public class FogState {
	private Fog targetFog;

	private Fog prevTickFog;
	private Fog currentTickFog;

	/**
	 * Sets the target fog. Set to null for default ambient fog
	 * @param fog
	 */
	public void setTargetFog(@Nullable Fog fog) {
		this.targetFog = fog;
	}

	/**
	 * Returns the target fog
	 * @return
	 */
	@Nonnull
	public Fog getTargetFog() {
		return this.targetFog;
	}

	/**
	 * Sets the current fog immediately
	 * @param fog
	 */
	public void setFog(@Nonnull Fog fog) {
		this.prevTickFog = this.currentTickFog = fog;
	}

	/**
	 * Returns the current interpolated fog
	 * @param partialTicks
	 * @return
	 */
	@Nonnull
	public Fog getFog(float partialTicks) {
		if(this.prevTickFog == null || this.currentTickFog == null) {
			return new Fog(FogType.LINEAR, 1, 0, 0, 0, 1, 0, 0);
		}
		if(partialTicks == 0.0F) {
			return this.prevTickFog;
		} else if(partialTicks == 1.0F) {
			return this.currentTickFog;
		} else {
			FogType type = this.currentTickFog.getFogType();
			float density = this.prevTickFog.getDensity() + (this.currentTickFog.getDensity() - this.prevTickFog.getDensity()) * partialTicks;
			float colorIncrement = this.prevTickFog.getColorIncrement() + (this.currentTickFog.getColorIncrement() - this.prevTickFog.getColorIncrement()) * partialTicks;
			float distanceIncrement = this.prevTickFog.getDistanceIncrementMultiplier() + (this.currentTickFog.getDistanceIncrementMultiplier() - this.prevTickFog.getDistanceIncrementMultiplier()) * partialTicks;
			float densityIncrement = this.prevTickFog.getDensityIncrement() + (this.currentTickFog.getDensityIncrement() - this.prevTickFog.getDensityIncrement()) * partialTicks;
			float red = this.prevTickFog.getRed() + (this.currentTickFog.getRed() - this.prevTickFog.getRed()) * partialTicks;
			float green = this.prevTickFog.getGreen() + (this.currentTickFog.getGreen() - this.prevTickFog.getGreen()) * partialTicks;
			float blue = this.prevTickFog.getBlue() + (this.currentTickFog.getBlue() - this.prevTickFog.getBlue()) * partialTicks;
			float colorMultiplier = this.prevTickFog.getColorMultiplier() + (this.currentTickFog.getColorMultiplier() - this.prevTickFog.getColorMultiplier()) * partialTicks;
			float start = this.prevTickFog.getStart() + (this.currentTickFog.getStart() - this.prevTickFog.getStart()) * partialTicks;
			float end = this.prevTickFog.getEnd() + (this.currentTickFog.getEnd() - this.prevTickFog.getEnd()) * partialTicks;
			return new Fog(type, density, red, green, blue, colorMultiplier, start, end, colorIncrement, distanceIncrement, densityIncrement);
		}
	}

	/**
	 * Returns the current fog
	 * @return
	 */
	@Nonnull
	public Fog getFog() {
		return this.getFog(1);
	}

	/**
	 * Returns the biome fog at the specified position
	 * @param world
	 * @param position
	 * @param farPlaneDistance
	 * @param mode
	 * @return
	 */
	public Fog getBiomeFog(World world, Vec3d position, float farPlaneDistance, int mode) {
		MutableFog defaultBiomeFog = new MutableFog().setType(FogType.LINEAR).setDensity(0.0F).setColorIncrement(0.001F).setDistanceIncrementMultiplier(1.0F)
				.setRed(0.5F).setGreen(0.5F).setBlue(0.5F).setStart(farPlaneDistance).setEnd(farPlaneDistance).setColorMultiplier(1);

		Biome biome = world.getBiome(new BlockPos(position));

		if(biome instanceof BiomeBetweenlands) {
			BiomeBetweenlands biomeBl = (BiomeBetweenlands) biome;
			int[] fogColor = biomeBl.getFogRGB();
			defaultBiomeFog.setRed(fogColor[0] / 255.0F).setGreen(fogColor[1] / 255.0F).setBlue(fogColor[2] / 255.0F);
			defaultBiomeFog.setStart(biomeBl.getFogStart(farPlaneDistance, mode)).setEnd(biomeBl.getFogEnd(farPlaneDistance, mode));
		}

		return defaultBiomeFog.toImmutable();
	}

	/**
	 * Returns min(144, start)
	 * @param start
	 * @return
	 */
	public float getFixedFogStart(float start) {
		return Math.min(144.0F, start);
	}

	/**
	 * Returns min(192, end)
	 * @param end
	 * @return
	 */
	public float getFixedFogEnd(float end) {
		return Math.min(192.0F, end);
	}

	/**
	 * Returns fog reduction for the specified distance
	 * @param end
	 * @return
	 */
	public float getLowDistanceFogReduction(float end) {
		return end > 128 ? 0.0F : (1.0F - end / 128.0F);
	}

	/**
	 * Returns the ambient fog using the specified biome fog and position
	 * @param biomeFog
	 * @param world
	 * @param position
	 * @return
	 */
	public Fog getAmbientFog(Fog biomeFog, World world, Vec3d position) {
		MutableFog defaultAmbientFog = new MutableFog(biomeFog);

		//Reduced fog for those players with really low view distance
		float lowViewDistanceFogReduction = this.getLowDistanceFogReduction(defaultAmbientFog.getEnd());

		//Use the same values regardless the view distance for fairness
		float fixedFogStart = this.getFixedFogStart(defaultAmbientFog.getStart());
		float fixedFogEnd = this.getFixedFogEnd(defaultAmbientFog.getEnd());

		if(position.y < WorldProviderBetweenlands.CAVE_START) {
			float fogColorMultiplier = ((float)(WorldProviderBetweenlands.CAVE_START - position.y) / WorldProviderBetweenlands.CAVE_START);
			fogColorMultiplier = 1.0F - fogColorMultiplier;
			fogColorMultiplier *= Math.pow(fogColorMultiplier, 8.5);
			fogColorMultiplier = fogColorMultiplier * 0.95F + 0.05F;
			if(position.y <= WorldProviderBetweenlands.PITSTONE_HEIGHT) {
				float targettedMultiplier = 0.3F;
				if(fogColorMultiplier < targettedMultiplier) {
					fogColorMultiplier += Math.pow(((targettedMultiplier - fogColorMultiplier) / WorldProviderBetweenlands.PITSTONE_HEIGHT * (WorldProviderBetweenlands.PITSTONE_HEIGHT - position.y)), 0.85F);
				}
			}
			fogColorMultiplier = MathHelper.clamp(fogColorMultiplier, 0.1F, 1);
			fogColorMultiplier = Math.min(fogColorMultiplier, 1.0F);
			fogColorMultiplier = fogColorMultiplier + (1.0F - fogColorMultiplier) * (float)Math.pow(lowViewDistanceFogReduction, 2.25D);
			defaultAmbientFog.setStart(fixedFogStart * fogColorMultiplier);
			defaultAmbientFog.setEnd(fixedFogEnd * fogColorMultiplier);
			defaultAmbientFog.setColorMultiplier(fogColorMultiplier);
			defaultAmbientFog.setDistanceIncrementMultiplier(2.0F);
		}

		return defaultAmbientFog.toImmutable();
	}

	/**
	 * Returns the ambient fog at the specified position
	 * @param world
	 * @param position
	 * @param farPlaneDistance
	 * @param mode
	 * @return
	 */
	public Fog getAmbientFog(World world, Vec3d position, float farPlaneDistance, int mode) {
		return this.getAmbientFog(this.getBiomeFog(world, position, farPlaneDistance, mode), world, position);
	}

	/**
	 * Updates the fog
	 * @param world
	 * @param position
	 * @param farPlaneDistance
	 * @param mode
	 */
	public void update(World world, Vec3d position, float farPlaneDistance, int mode) {
		Fog defaultBiomeFog = this.getBiomeFog(world, position, farPlaneDistance, mode);
		Fog defaultAmbientFog = this.getAmbientFog(defaultBiomeFog, world, position);

		MinecraftForge.EVENT_BUS.post(new UpdateFogEvent(this, defaultBiomeFog, defaultAmbientFog, position, world, farPlaneDistance));

		if(this.currentTickFog == null) {
			this.currentTickFog = defaultAmbientFog;
		}

		this.prevTickFog = this.currentTickFog;

		Fog target = this.targetFog;
		if(target == null) {
			target = defaultAmbientFog;
		}

		float[] fogPositions = new float[]{ this.currentTickFog.getStart(), target.getStart(), this.currentTickFog.getEnd(), target.getEnd() };

		for(int i = 0; i < 2; i++) {
			float fogPosition = fogPositions[i * 2];
			float fogTargetPosition = fogPositions[i * 2 + 1];
			float fogPositionDiff = Math.abs(fogPosition - fogTargetPosition);
			float fogPositionIncr = fogPositionDiff / farPlaneDistance / 2.0f * target.getDistanceIncrementMultiplier();
			if(fogPosition > fogTargetPosition) {
				if(fogPosition - fogTargetPosition > fogPositionIncr) {
					fogPositions[i * 2] -= fogPositionIncr;
				} else {
					fogPositions[i * 2] = fogTargetPosition;
				}
			} else if(fogPosition < fogTargetPosition) {
				if(fogTargetPosition - fogPosition > fogPositionIncr) {
					fogPositions[i * 2] += fogPositionIncr;
				} else {
					fogPositions[i * 2] = fogTargetPosition;
				}
			}
		}

		float[] currentFogColor = new float[]{ this.currentTickFog.getRed(), this.currentTickFog.getGreen(), this.currentTickFog.getBlue() };
		float[] targetFogColor = new float[] { target.getRed() * target.getColorMultiplier(), target.getGreen() * target.getColorMultiplier(), target.getBlue() * target.getColorMultiplier() };

		for(int a = 0; a < 3; a++) {
			if(currentFogColor[a] != targetFogColor[a]) {
				if(currentFogColor[a] < targetFogColor[a]) {
					currentFogColor[a] += target.getColorIncrement();
					if(currentFogColor[a] > targetFogColor[a]) {
						currentFogColor[a] = targetFogColor[a];
					}
				} else if(currentFogColor[a] > targetFogColor[a]) {
					currentFogColor[a] -= target.getColorIncrement();
					if(currentFogColor[a] < targetFogColor[a]) {
						currentFogColor[a] = targetFogColor[a];
					}
				}
			}
		}

		float currentFogDensity = this.currentTickFog.getDensity();
		float targetFogDensity = target.getDensity();

		if(currentFogDensity != targetFogDensity) {
			if(currentFogDensity < targetFogDensity) {
				currentFogDensity += target.getDensityIncrement();
				if(currentFogDensity > targetFogDensity) {
					currentFogDensity = targetFogDensity;
				}
			} else if(currentFogDensity > targetFogDensity) {
				currentFogDensity -= target.getDensityIncrement();
				if(currentFogDensity < targetFogDensity) {
					currentFogDensity = targetFogDensity;
				}
			}
		}

		this.currentTickFog = new Fog(target.getFogType(), currentFogDensity, currentFogColor[0], currentFogColor[1], currentFogColor[2], 1.0F, fogPositions[0], fogPositions[2],
				target.getColorIncrement(), target.getDistanceIncrementMultiplier(), target.getDensityIncrement());
	}
}
