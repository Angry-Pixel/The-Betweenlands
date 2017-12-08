package thebetweenlands.client.render.sky;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.IRenderHandler;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class BLSnowRenderer extends IRenderHandler {
	public static final BLSnowRenderer INSTANCE = new BLSnowRenderer();

	private static final ResourceLocation SNOW_TEXTURES = new ResourceLocation("textures/environment/snow.png");

	private final float[] rainXCoords = new float[1024];
	private final float[] rainYCoords = new float[1024];
	private final Random random = new Random();

	private long prevRenderUpdateTicks;
	private long renderUpdateTicks;

	private float snowingStrength;
	private float prevSnowingStrength;

	public BLSnowRenderer() {
		for (int i = 0; i < 32; ++i) {
			for (int j = 0; j < 32; ++j) {
				float f = (float)(j - 16);
				float f1 = (float)(i - 16);
				float f2 = MathHelper.sqrt(f * f + f1 * f1);
				this.rainXCoords[i << 5 | j] = -f1 / f2;
				this.rainYCoords[i << 5 | j] = f / f2;
			}
		}
	}

	public void update(World world) {
		this.prevSnowingStrength = this.snowingStrength;
		if(world.provider instanceof WorldProviderBetweenlands) {
			WorldProviderBetweenlands provider = (WorldProviderBetweenlands) world.provider;
			this.snowingStrength = provider.getEnvironmentEventRegistry().snowfall.getSnowingStrength();
		} else {
			this.snowingStrength = 0;
		}
		this.prevRenderUpdateTicks = this.renderUpdateTicks;
		this.renderUpdateTicks += 1 + (int)(this.snowingStrength * 2.3F);
	}

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		EntityRenderer renderer = mc.entityRenderer;

		float snowingStrength = this.prevSnowingStrength + (this.snowingStrength - this.prevSnowingStrength) * partialTicks;

		if (snowingStrength > 0.0F) {
			renderer.enableLightmap();
			Entity entity = mc.getRenderViewEntity();
			int px = MathHelper.floor(entity.posX);
			int py = MathHelper.floor(entity.posY);
			int pz = MathHelper.floor(entity.posZ);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder builder = tessellator.getBuffer();
			GlStateManager.disableCull();
			GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.depthMask(true);
			double interpX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
			double interpY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
			double interpZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
			int interpYFloor = MathHelper.floor(interpY);
			int layers = 5;

			if (mc.gameSettings.fancyGraphics) {
				layers = 10;
			}

			float interpTicks = this.prevRenderUpdateTicks + (this.renderUpdateTicks - this.prevRenderUpdateTicks) * partialTicks;

			builder.setTranslation(-interpX, -interpY, -interpZ);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

			mc.getTextureManager().bindTexture(SNOW_TEXTURES);
			builder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

			for (int layerZ = pz - layers; layerZ <= pz + layers; ++layerZ) {
				for (int layerX = px - layers; layerX <= px + layers; ++layerX) {
					int coordsIndex = (layerZ - pz + 16) * 32 + layerX - px + 16;
					double rainXCoord = (double)this.rainXCoords[coordsIndex] * 0.5D;
					double rainYCoord = (double)this.rainYCoords[coordsIndex] * 0.5D;
					checkPos.setPos(layerX, 0, layerZ);

					int precipationHeight = world.getPrecipitationHeight(checkPos).getY();
					int layerY = py - layers;
					int maxLayerY = py + layers;

					if (layerY < precipationHeight) {
						layerY = precipationHeight;
					}

					if (maxLayerY < precipationHeight) {
						maxLayerY = precipationHeight;
					}

					int maxHeight = precipationHeight;

					if (precipationHeight < interpYFloor) {
						maxHeight = interpYFloor;
					}

					if (layerY != maxLayerY) {
						this.random.setSeed((long)(layerX * layerX * 3121 + layerX * 45238971 ^ layerZ * layerZ * 418711 + layerZ * 13761));

						double uvShiftY = (double)(-(interpTicks % 512) / 512.0F);
						double randUvShiftX = this.random.nextDouble() + (double)interpTicks * 0.01D * (double)((float)this.random.nextGaussian());
						double randUvShiftY = this.random.nextDouble() + (double)(interpTicks * (float)this.random.nextGaussian()) * 0.001D;
						double dx = (double)((float)layerX + 0.5F) - entity.posX;
						double dz = (double)((float)layerZ + 0.5F) - entity.posZ;
						float distance = MathHelper.sqrt(dx * dx + dz * dz) / (float)layers;
						float visibility = MathHelper.clamp(((1.0F - distance * distance) * 0.5F + 0.5F) * snowingStrength / 5.0F, 0, 1);
						checkPos.setPos(layerX, maxHeight, layerZ);
						int lightmap = (world.getCombinedLight(checkPos, 0) * 3 + 15728880) / 4;
						int lightmapX = (lightmap >> 16 & 65535);
						int lightmapY = lightmap & 65535;
						builder.pos((double) layerX - rainXCoord + 0.5D, (double) maxLayerY, (double) layerZ - rainYCoord + 0.5D)
						.tex(0.0D + randUvShiftX, (double) layerY * 0.25D + uvShiftY + randUvShiftY).color(1.0F, 1.0F, 1.0F, visibility)
						.lightmap(lightmapX, lightmapY).endVertex();
						builder.pos((double) layerX + rainXCoord + 0.5D, (double) maxLayerY, (double) layerZ + rainYCoord + 0.5D)
						.tex(1.0D + randUvShiftX, (double) layerY * 0.25D + uvShiftY + randUvShiftY).color(1.0F, 1.0F, 1.0F, visibility)
						.lightmap(lightmapX, lightmapY).endVertex();
						builder.pos((double) layerX + rainXCoord + 0.5D, (double) layerY, (double) layerZ + rainYCoord + 0.5D)
						.tex(1.0D + randUvShiftX, (double) maxLayerY * 0.25D + uvShiftY + randUvShiftY).color(1.0F, 1.0F, 1.0F, visibility)
						.lightmap(lightmapX, lightmapY).endVertex();
						builder.pos((double) layerX - rainXCoord + 0.5D, (double) layerY, (double) layerZ - rainYCoord + 0.5D)
						.tex(0.0D + randUvShiftX, (double) maxLayerY * 0.25D + uvShiftY + randUvShiftY).color(1.0F, 1.0F, 1.0F, visibility)
						.lightmap(lightmapX, lightmapY).endVertex();
					}
				}
			}

			tessellator.draw();

			builder.setTranslation(0.0D, 0.0D, 0.0D);
			GlStateManager.enableCull();
			GlStateManager.disableBlend();
			GlStateManager.alphaFunc(516, 0.1F);
			renderer.disableLightmap();
		}
	}
}
