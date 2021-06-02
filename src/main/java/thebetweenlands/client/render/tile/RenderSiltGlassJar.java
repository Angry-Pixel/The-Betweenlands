package thebetweenlands.client.render.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.tile.ModelSiltGlassJar;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.common.tile.TileEntitySiltGlassJar;

@SideOnly(Side.CLIENT)
public class RenderSiltGlassJar extends TileEntitySpecialRenderer<TileEntitySiltGlassJar> {
	public static final ResourceLocation WORM_WIGGLE = new ResourceLocation("thebetweenlands:blocks/worm_wiggle");

	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/blocks/silt_glass_jar.png");
	private final ModelSiltGlassJar model = new ModelSiltGlassJar();
	
	@Override
	public void render(TileEntitySiltGlassJar tile, double x, double y, double z, float partialTick, int destroyStage, float alpha) {
		if(tile != null) {
			int wormLevel = tile.getItemCount();
			if (wormLevel >= 1) {
				float height = (0.6875F / 8) * wormLevel;
				
				TextureAtlasSprite wormSprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("thebetweenlands:blocks/worm_wiggle");
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder buffer = tessellator.getBuffer();
		
				GlStateManager.disableLighting();
		        GlStateManager.pushMatrix();
				GlStateManager.translate(x, y, z);
				Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				float xMax, zMax, xMin, zMin, yMin = 0;
				xMax = 1.5F;
				zMax = 1.5F;
				xMin = 0.5F;
				zMin = 0.5F;
				yMin = 0.0625F;
		
				renderCuboid(buffer, xMax, xMin, yMin, height, zMin, zMax, wormSprite);
				tessellator.draw();
				GlStateManager.popMatrix();
				GlStateManager.enableLighting();
			}
		}
		
		bindTexture(TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(1F, -1F, -1F);
		GlStateManager.enableCull();
		GlStateManager.cullFace(CullFace.FRONT);
		model.render();
		GlStateManager.cullFace(CullFace.BACK);
		model.render();
		GlStateManager.popMatrix();
	}

	private void renderCuboid(BufferBuilder buffer, float xMax, float xMin, float yMin, float height, float zMin, float zMax, TextureAtlasSprite textureAtlasSprite) {

		double uMin = (double) textureAtlasSprite.getMinU();
		double uMax = (double) textureAtlasSprite.getMaxU();
		double vMin = (double) textureAtlasSprite.getMinV();
		double vMax = (double) textureAtlasSprite.getMaxV();

		final double vHeight = vMax - vMin;

		// top
		addVertexWithUV(buffer, xMax, height, zMax, uMax, vMin);
		addVertexWithUV(buffer, xMax, height, zMin, uMin, vMin);
		addVertexWithUV(buffer, xMin, height, zMin, uMin, vMax);
		addVertexWithUV(buffer, xMin, height, zMax, uMax, vMax);

		// north
		addVertexWithUV(buffer, xMax, yMin, zMin, uMax, vMin);
		addVertexWithUV(buffer, xMin, yMin, zMin, uMin, vMin);
		addVertexWithUV(buffer, xMin, height, zMin, uMin, vMin + (vHeight * height));
		addVertexWithUV(buffer, xMax, height, zMin, uMax, vMin + (vHeight * height));

		// south
		addVertexWithUV(buffer, xMax, yMin, zMax, uMin, vMin);
		addVertexWithUV(buffer, xMax, height, zMax, uMin, vMin + (vHeight * height));
		addVertexWithUV(buffer, xMin, height, zMax, uMax, vMin + (vHeight * height));
		addVertexWithUV(buffer, xMin, yMin, zMax, uMax, vMin);

		// east
		addVertexWithUV(buffer, xMax, yMin, zMin, uMin, vMin);
		addVertexWithUV(buffer, xMax, height, zMin, uMin, vMin + (vHeight * height));
		addVertexWithUV(buffer, xMax, height, zMax, uMax, vMin + (vHeight * height));
		addVertexWithUV(buffer, xMax, yMin, zMax, uMax, vMin);

		// west
		addVertexWithUV(buffer, xMin, yMin, zMax, uMin, vMin);
		addVertexWithUV(buffer, xMin, height, zMax, uMin, vMin + (vHeight * height));
		addVertexWithUV(buffer, xMin, height, zMin, uMax, vMin + (vHeight * height));
		addVertexWithUV(buffer, xMin, yMin, zMin, uMax, vMin);

	}

	private void addVertexWithUV(BufferBuilder buffer, float x, float y, float z, double u, double v) {
		buffer.pos(x / 2f, y, z / 2f).tex(u, v).endVertex();
	}
}