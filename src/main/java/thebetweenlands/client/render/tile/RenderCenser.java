package thebetweenlands.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.api.recipes.ICenserRecipe;
import thebetweenlands.api.recipes.ICenserRecipe.EffectColorType;
import thebetweenlands.client.render.model.tile.ModelCenser;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.postprocessing.GroundFog.GroundFogVolume;
import thebetweenlands.common.block.container.BlockCenser;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.tile.TileEntityCenser;
import thebetweenlands.util.RenderUtils;
import thebetweenlands.util.StatePropertyHelper;

public class RenderCenser extends TileEntitySpecialRenderer<TileEntityCenser> {
	private static final ModelCenser MODEL = new ModelCenser();
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/censer.png");

	public static final ResourceLocation CENSER_FOG_PATH = new ResourceLocation(ModInfo.ID, "tiles/censer_fog");

	@Override
	public boolean isGlobalRenderer(TileEntityCenser te) {
		return true;
	}

	private void renderFogFlow(TextureAtlasSprite fogSprite, float animationTicks, int skyLight, int blockLight, int color, float alpha, boolean flatSlope) {
		float r = ((color >> 16) & 0xFF) / 255f;
		float g = ((color >> 8) & 0xFF) / 255f;
		float b = ((color >> 0) & 0xFF) / 255f;
		float a = ((color >> 24) & 0xFF) / 255f * alpha;

		float minU = fogSprite.getMinU();
		float minV = fogSprite.getMinV();
		float maxU = fogSprite.getMaxU();
		float maxV = fogSprite.getMaxV();

		//texture is 3px wide
		maxU = minU + (maxU - minU) * (3.0f / 16.0f);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

		float halfWidth = 0.13f / 2.0f;

		float zOff = 0.0f;
		float prevZOff = zOff;
		float vOff = minV;

		float onePx = 1 / 16.0f;
		float oneTx = (maxV - minV) / 16.0f;

		float stripY = 0.0f;
		float prevStripY = stripY;

		for(int i = 0; i < 16; i++) {
			float slopeIndex = i + 1;

			float slope = 0.001f;

			if(flatSlope && (i == 3 || i == 4)) {
				slope = 0.00019f;
			}

			stripY -= slope + Math.min(slopeIndex * slopeIndex * slopeIndex * slope, onePx);
			zOff += onePx / ((i + 1) * slope / 0.001f);

			float wavyZOff = zOff + (float)Math.sin((i - (animationTicks) * 0.1f) * 1.1f) *  0.015f;

			buffer.pos(-halfWidth, prevStripY, prevZOff).color(r, g, b, a).tex(minU, vOff + oneTx).lightmap(skyLight, blockLight).endVertex();
			buffer.pos(-halfWidth, stripY, wavyZOff).color(r, g, b, a).tex(minU, vOff).lightmap(skyLight, blockLight).endVertex();
			buffer.pos(halfWidth, stripY, wavyZOff).color(r, g, b, a).tex(maxU, vOff).lightmap(skyLight, blockLight).endVertex();
			buffer.pos(halfWidth, prevStripY, prevZOff).color(r, g, b, a).tex(maxU, vOff + oneTx).lightmap(skyLight, blockLight).endVertex();

			vOff += oneTx;

			prevStripY = stripY;
			prevZOff = wavyZOff;
		}

		tessellator.draw();
	}

	@Override
	public void render(TileEntityCenser te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		bindTexture(TEXTURE);

		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5F, y + 1.5F, z + 0.5F);
		GlStateManager.scale(1F, -1F, -1F);
		GlStateManager.rotate(StatePropertyHelper.getStatePropertySafely(te, BlockCenser.class, BlockCenser.FACING, EnumFacing.NORTH).getHorizontalAngle(), 0.0F, 1F, 0F);
		GlStateManager.disableCull();

		MODEL.render(null, 0, 0, 0, 0, 0, 0.0625F);

		GlStateManager.enableCull();
		GlStateManager.popMatrix();

		if(te != null && te.getFuelTicks() > 0) {
			if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
				ShaderHelper.INSTANCE.require();

				float strength = te.getDungeonFogStrength(partialTicks);

				float fogBrightness = 0.85F;
				float inScattering = 0.025F * strength;
				float extinction = 2.5F + 5.0F * (1 - strength);

				AxisAlignedBB fogArea = te.getFogRenderArea();

				ShaderHelper.INSTANCE.getWorldShader().addGroundFogVolume(new GroundFogVolume(new Vec3d(fogArea.minX, fogArea.minY, fogArea.minZ), new Vec3d(fogArea.maxX - fogArea.minX, fogArea.maxY - fogArea.minY, fogArea.maxZ - fogArea.minZ), inScattering, extinction, fogBrightness, fogBrightness, fogBrightness));
			}

			ICenserRecipe<Object> recipe = te.getCurrentRecipe();
			if(recipe != null) {
				if(te.hasWorld()) {
					int effectColor = recipe.getEffectColor(te.getCurrentRecipeContext(), te, EffectColorType.FOG);
					float effectStrength = te.getEffectStrength(partialTicks);

					GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

					GlStateManager.depthMask(false);

					GlStateManager.pushMatrix();
					GlStateManager.translate(x + 0.5F, y + 0.7F, z + 0.5F);

					GlStateManager.rotate(-StatePropertyHelper.getStatePropertySafely(te, BlockCenser.class, BlockCenser.FACING, EnumFacing.NORTH).getHorizontalAngle() - 90, 0.0F, 1F, 0F);

					this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

					TextureAtlasSprite fogSprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(CENSER_FOG_PATH.toString());

					int skyLight = 240;
					int blockLight = 240;

					//top fog strips
					GlStateManager.pushMatrix();
					GlStateManager.translate(0.0f, 0.18f, 0.18f);
					this.renderFogFlow(fogSprite, (RenderUtils.getRenderTickCounter() + partialTicks) * 0.86f, skyLight, blockLight, effectColor, effectStrength, false);
					GlStateManager.popMatrix();

					GlStateManager.pushMatrix();
					GlStateManager.translate(0.03f, 0.18f, 0.15f);
					GlStateManager.rotate(90, 0, 1, 0);
					this.renderFogFlow(fogSprite, (RenderUtils.getRenderTickCounter() + partialTicks) * 0.94f, skyLight, blockLight, effectColor, effectStrength, true);
					GlStateManager.popMatrix();

					GlStateManager.pushMatrix();
					GlStateManager.translate(-0.03f, 0.18f, 0.15f);
					GlStateManager.rotate(-90, 0, 1, 0);
					this.renderFogFlow(fogSprite, (RenderUtils.getRenderTickCounter() + partialTicks) * 0.92f, skyLight, blockLight, effectColor, effectStrength, true);
					GlStateManager.popMatrix();

					//bottom fog strips
					GlStateManager.pushMatrix();
					GlStateManager.translate(0.114f, 0, 0.235f);
					this.renderFogFlow(fogSprite, (RenderUtils.getRenderTickCounter() + partialTicks) * 0.9f, skyLight, blockLight, effectColor, effectStrength, false);
					GlStateManager.popMatrix();

					GlStateManager.pushMatrix();
					GlStateManager.translate(-0.135f, 0, 0.235f);
					this.renderFogFlow(fogSprite, (RenderUtils.getRenderTickCounter() + partialTicks) * 1.1f, skyLight, blockLight, effectColor, effectStrength, false);
					GlStateManager.popMatrix();

					GlStateManager.pushMatrix();
					GlStateManager.rotate(90, 0, 1, 0);
					GlStateManager.translate(0.114f, 0, 0.235f);
					this.renderFogFlow(fogSprite, (RenderUtils.getRenderTickCounter() + partialTicks) * 0.85f, skyLight, blockLight, effectColor, effectStrength, false);
					GlStateManager.popMatrix();

					GlStateManager.pushMatrix();
					GlStateManager.rotate(90, 0, 1, 0);
					GlStateManager.translate(-0.135f, 0, 0.235f);
					this.renderFogFlow(fogSprite, (RenderUtils.getRenderTickCounter() + partialTicks) * 1.15f, skyLight, blockLight, effectColor, effectStrength, false);
					GlStateManager.popMatrix();

					GlStateManager.pushMatrix();
					GlStateManager.rotate(180, 0, 1, 0);
					GlStateManager.translate(0.114f, 0, 0.235f);
					this.renderFogFlow(fogSprite, (RenderUtils.getRenderTickCounter() + partialTicks) * 0.95f, skyLight, blockLight, effectColor, effectStrength, false);
					GlStateManager.popMatrix();

					GlStateManager.pushMatrix();
					GlStateManager.rotate(180, 0, 1, 0);
					GlStateManager.translate(-0.135f, 0, 0.235f);
					this.renderFogFlow(fogSprite, (RenderUtils.getRenderTickCounter() + partialTicks) * 1.08f, skyLight, blockLight, effectColor, effectStrength, false);
					GlStateManager.popMatrix();

					GlStateManager.pushMatrix();
					GlStateManager.rotate(270, 0, 1, 0);
					GlStateManager.translate(0.114f, 0, 0.235f);
					this.renderFogFlow(fogSprite, (RenderUtils.getRenderTickCounter() + partialTicks) * 0.88f, skyLight, blockLight, effectColor, effectStrength, false);
					GlStateManager.popMatrix();

					GlStateManager.pushMatrix();
					GlStateManager.rotate(270, 0, 1, 0);
					GlStateManager.translate(-0.135f, 0, 0.235f);
					this.renderFogFlow(fogSprite, (RenderUtils.getRenderTickCounter() + partialTicks) * 1.05f, skyLight, blockLight, effectColor, effectStrength, false);
					GlStateManager.popMatrix();

					GlStateManager.popMatrix();

					GlStateManager.depthMask(true);
				}

				recipe.render(te.getCurrentRecipeContext(), te, x, y, z, partialTicks);
			}
		}
	}
}
