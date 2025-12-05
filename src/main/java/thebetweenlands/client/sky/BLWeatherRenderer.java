package thebetweenlands.client.sky;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.EnvironmentEventRegistry;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.world.event.SnowfallEvent;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.WorldStorageGetter;

public class BLWeatherRenderer {

	public static final BLWeatherRenderer INSTANCE = new BLWeatherRenderer();

	private static final ResourceLocation RAIN_TEXTURES = TheBetweenlands.prefix("textures/environment/rain.png");
	private static final ResourceLocation SNOW_TEXTURES = ResourceLocation.withDefaultNamespace("textures/environment/snow.png");

	private final float[] rainXCoords = new float[1024];
	private final float[] rainYCoords = new float[1024];

	private long prevRenderUpdateTicks;
	private long renderUpdateTicks;

	private float snowingStrength;
	private float prevSnowingStrength;

	private int rainSoundCounter = 0;

	public BLWeatherRenderer() {
		for (int i = 0; i < 32; ++i) {
			for (int j = 0; j < 32; ++j) {
				float f = (j - 16);
				float f1 = (i - 16);
				float f2 = Mth.sqrt(f * f + f1 * f1);
				this.rainXCoords[i << 5 | j] = -f1 / f2;
				this.rainYCoords[i << 5 | j] = f / f2;
			}
		}
	}

	public void tickRain(Level level) {
		this.prevRenderUpdateTicks = this.renderUpdateTicks;
		this.renderUpdateTicks++;

		Minecraft mc = Minecraft.getInstance();

		float intensity = level.getRainLevel(1.0F) / (Minecraft.useFancyGraphics() ? 1.0F : 2.0F);
		if (intensity > 0.0F) {
			RandomSource random = RandomSource.create(this.renderUpdateTicks * 312987231L);
			BlockPos cameraPos = mc.getCameraEntity().blockPosition();
			BlockPos below = null;
			int count = (int)(100.0F * intensity * intensity) / (mc.options.particles().get() == ParticleStatus.DECREASED ? 2 : 1);

			for (int i = 0; i < count; i++) {
				int xOffs = random.nextInt(21) - 10;
				int zOffs = random.nextInt(21) - 10;
				BlockPos skySightPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, cameraPos.offset(xOffs, 0, zOffs));
				if (skySightPos.getY() > level.getMinBuildHeight() && skySightPos.getY() <= cameraPos.getY() + 10 && skySightPos.getY() >= cameraPos.getY() - 10) {
					Biome biome = level.getBiome(skySightPos).value();
					if (biome.getPrecipitationAt(skySightPos) == Biome.Precipitation.RAIN) {
						below = skySightPos.below();
						if (mc.options.particles().get() == ParticleStatus.MINIMAL) {
							break;
						}

						BlockState blockstate = level.getBlockState(below);
						VoxelShape voxelshape = blockstate.getCollisionShape(level, below);

						if (!voxelshape.isEmpty()) {
							AABB blockAABB = voxelshape.bounds();
							float rangeX = (float) (blockAABB.maxX - blockAABB.minX);
							float rangeZ = (float) (blockAABB.maxZ - blockAABB.minZ);

							float size = (0.025F + random.nextFloat() * 0.4F);

							if (size * 2 < rangeX && size * 2 < rangeZ) {
								double rx = random.nextDouble() * (rangeX - size * 2) + size + blockAABB.minX;
								double rz = random.nextDouble() * (rangeZ - size * 2) + size + blockAABB.minZ;
								FluidState fluidstate = level.getFluidState(below);
								if (!fluidstate.is(FluidTags.LAVA) && !blockstate.is(Blocks.MAGMA_BLOCK) && !CampfireBlock.isLitCampfire(blockstate)) {
									double samplePos1 = random.nextDouble();
									double samplePos2 = random.nextDouble();
									double blockTop = voxelshape.max(Direction.Axis.Y, samplePos1, samplePos2);
									double fluidHeight = fluidstate.getHeight(level, below);
									double height = Math.max(blockTop, fluidHeight);
									int waterColor = BiomeColors.getAverageWaterColor(level, below);

									for (int particleCounter = 0; particleCounter < 4 + random.nextInt(6); particleCounter++) {
										TheBetweenlands.createParticle(ParticleRegistry.RAIN.get(), level, (double) below.getX() + rx, (double) ((float) below.getY() + 0.1F) + height, (double) below.getZ() + rz, ParticleFactory.ParticleArgs.get().withColor(1.0F, waterColor));
									}

									TheBetweenlands.createParticle(ParticleRegistry.WATER_RIPPLE.get(), level, (double) below.getX() + rx, (double) ((float) below.getY() + 0.1F) + height, (double) below.getZ() + rz, ParticleFactory.ParticleArgs.get().withScale(size * 10).withColor(1.0F, waterColor));
								}
							}
						}
					}
				}
			}

			if (below != null && random.nextInt(3) < this.rainSoundCounter++) {
				this.rainSoundCounter = 0;
				if (below.getY() > cameraPos.getY() + 1 && level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, cameraPos).getY() > Mth.floor((float)cameraPos.getY())) {
					level.playLocalSound(below, SoundEvents.WEATHER_RAIN_ABOVE, SoundSource.WEATHER, 0.1F, 0.5F, false);
				} else {
					level.playLocalSound(below, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.2F, 1.0F, false);
				}
			}
		}
	}

	public void tickSnow(Level level) {
		this.prevSnowingStrength = this.snowingStrength;
		BetweenlandsWorldStorage storage = WorldStorageGetter.getNullable(level);
		if (storage != null) {
			this.snowingStrength = EnvironmentEventRegistry.SNOWFALL.get().getSnowingStrength();
		} else {
			this.snowingStrength = 0;
		}
		this.prevRenderUpdateTicks = this.renderUpdateTicks;
		this.renderUpdateTicks += 1 + (int) (this.snowingStrength * 2.3F);
	}

	public void render(boolean snowing, Level level, LightTexture lightTexture, float partialTick, double camX, double camY, double camZ) {
		float intensity = snowing ? Mth.lerp(partialTick, this.prevSnowingStrength, this.snowingStrength) : level.getRainLevel(partialTick);
		if (intensity > 0.0F) {
			lightTexture.turnOnLightLayer();
			int px = Mth.floor(camX);
			int py = Mth.floor(camY);
			int pz = Mth.floor(camZ);
			Tesselator tesselator = Tesselator.getInstance();
			BufferBuilder bufferbuilder = null;
			RenderSystem.disableCull();
			RenderSystem.enableBlend();
			RenderSystem.enableDepthTest();
			int layers = 5;
			if (Minecraft.useFancyGraphics()) {
				layers = 10;
			}

			RenderSystem.depthMask(Minecraft.useShaderTransparency());
			int drawLayer = -1;
			float interpTicks = Mth.lerp(partialTick, this.prevRenderUpdateTicks, this.renderUpdateTicks);
			RenderSystem.setShader(GameRenderer::getParticleShader);
			BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

			for (int layerZ = pz - layers; layerZ <= pz + layers; layerZ++) {
				for (int layerX = px - layers; layerX <= px + layers; layerX++) {
					int coordsIndex = (layerZ - pz + 16) * 32 + layerX - px + 16;
					float rainXCoord = this.rainXCoords[coordsIndex] * 0.5F;
					float rainYCoord = this.rainYCoords[coordsIndex] * 0.5F;
					mutable.set(layerX, camY, layerZ);
					Biome biome = level.getBiome(mutable).value();
					if (biome.hasPrecipitation()) {
						int height = level.getHeight(Heightmap.Types.MOTION_BLOCKING, layerX, layerZ);
						int layerY = py - layers;
						int maxLayerY = py + layers;
						if (layerY < height) {
							layerY = height;
						}

						if (maxLayerY < height) {
							maxLayerY = height;
						}

						int drawHeight = Math.max(height, py);

						if (layerY != maxLayerY) {
							RandomSource random = RandomSource.create(layerX * layerX * 3121L + layerX * 45238971L ^ layerZ * layerZ * 418711L + layerZ * 13761L);
							mutable.set(layerX, layerY, layerZ);
							if (!snowing) {
								if (drawLayer != 0) {
									if (drawLayer >= 0) {
										BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
									}

									drawLayer = 0;
									RenderSystem.setShaderTexture(0, RAIN_TEXTURES);
									bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
								}

								long adjustedTicks = this.renderUpdateTicks & 131071L;
								int shift = layerX * layerX * 3121 + layerX * 45238971 + layerZ * layerZ * 418711 + layerZ * 13761 & 0xFF;
								float offset = 3.0F + random.nextFloat();
								float uvShiftY = -((adjustedTicks + shift) + partialTick) / 32.0F * offset;
								float f4 = uvShiftY % 32.0F;
								float d2 = (float) (layerX + 0.5F - camX);
								float d3 = (float) (layerZ + 0.5F - camZ);
								float f6 = (float) (Math.sqrt(d2 * d2 + d3 * d3) / layers);
								float visibility = ((1.0F - f6 * f6) * 0.5F + 0.5F) * intensity;
								mutable.set(layerX, drawHeight, layerZ);
								int waterColor = BiomeColors.getAverageWaterColor(level, mutable);
								float r = (waterColor >> 16 & 255) / 255.0F * 1.5F;
								float g = (waterColor >> 8 & 255) / 255.0F * 1.5F;
								float b = (waterColor & 255) / 255.0F * 1.5F + 0.1F;
								int light = LevelRenderer.getLightColor(level, mutable);
								bufferbuilder.addVertex((float) (layerX - camX - rainXCoord + 0.5F), (float) (maxLayerY - camY), (float) (layerZ - camZ - rainYCoord + 0.5F))
									.setUv(0.0F, layerY * 0.25F + f4)
									.setColor(r, g, b, visibility)
									.setLight(light);
								bufferbuilder.addVertex((float) (layerX - camX + rainXCoord + 0.5F), (float) (maxLayerY - camY), (float) (layerZ - camZ + rainYCoord + 0.5F))
									.setUv(1.0F, layerY * 0.25F + f4)
									.setColor(r, g, b, visibility)
									.setLight(light);
								bufferbuilder.addVertex((float) (layerX - camX + rainXCoord + 0.5F), (float) (layerY - camY), (float) (layerZ - camZ + rainYCoord + 0.5F))
									.setUv(1.0F, maxLayerY * 0.25F + f4)
									.setColor(r, g, b, visibility)
									.setLight(light);
								bufferbuilder.addVertex((float) (layerX - camX - rainXCoord + 0.5F), (float) (layerY - camY), (float) (layerZ - camZ - rainYCoord + 0.5F))
									.setUv(0.0F, maxLayerY * 0.25F + f4)
									.setColor(r, g, b, visibility)
									.setLight(light);
							} else {
								if (drawLayer != 1) {
									if (drawLayer == 0) {
										BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
									}

									drawLayer = 1;
									RenderSystem.setShaderTexture(0, SNOW_TEXTURES);
									bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
								}

								float uvShiftY = -(((int)interpTicks & 512) + partialTick) / 512.0F;
								float randUvShiftX = (float) (random.nextFloat() + interpTicks * 0.01F * random.nextGaussian());
								float randUvShiftY = (float) (random.nextFloat() + (interpTicks * random.nextGaussian()) * 0.001F);
								float dx = (float) (layerX + 0.5F - camX);
								float dy = (float) (layerZ + 0.5F - camZ);
								float distance = (float) (Math.sqrt(dx * dx + dy * dy) / layers);
								float visibility = Mth.clamp(((1.0F - distance * distance) * 0.5F + 0.5F) * intensity / 5.0F, 0, 1);
								mutable.set(layerX, drawHeight, layerZ);
								int j4 = LevelRenderer.getLightColor(level, mutable);
								int k4 = j4 >> 16 & 65535;
								int l4 = j4 & 65535;
								int l3 = (k4 * 3 + 240) / 4;
								int i4 = (l4 * 3 + 240) / 4;
								bufferbuilder.addVertex((float) (layerX - camX - rainXCoord + 0.5F), (float) (maxLayerY - camY), (float) (layerZ - camZ - rainYCoord + 0.5F))
									.setUv(0.0F + randUvShiftX, layerY * 0.25F + uvShiftY + randUvShiftY)
									.setColor(1.0F, 1.0F, 1.0F, visibility)
									.setUv2(i4, l3);
								bufferbuilder.addVertex((float) (layerX - camX + rainXCoord + 0.5F), (float) (maxLayerY - camY), (float) (layerZ - camZ + rainYCoord + 0.5F))
									.setUv(1.0F + randUvShiftX, layerY * 0.25F + uvShiftY + randUvShiftY)
									.setColor(1.0F, 1.0F, 1.0F, visibility)
									.setUv2(i4, l3);
								bufferbuilder.addVertex((float) (layerX - camX + rainXCoord + 0.5F), (float) (layerY - camY), (float) (layerZ - camZ + rainYCoord + 0.5F))
									.setUv(1.0F + randUvShiftX, maxLayerY * 0.25F + uvShiftY + randUvShiftY)
									.setColor(1.0F, 1.0F, 1.0F, visibility)
									.setUv2(i4, l3);
								bufferbuilder.addVertex((float) (layerX - camX - rainXCoord + 0.5F), (float) (layerY - camY), (float) (layerZ - camZ - rainYCoord + 0.5F))
									.setUv(0.0F + randUvShiftX, maxLayerY * 0.25F + uvShiftY + randUvShiftY)
									.setColor(1.0F, 1.0F, 1.0F, visibility)
									.setUv2(i4, l3);
							}
						}
					}
				}
			}

			if (drawLayer >= 0) {
				BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
			}

			RenderSystem.enableCull();
			RenderSystem.disableBlend();
			lightTexture.turnOffLightLayer();
		}
	}
}
