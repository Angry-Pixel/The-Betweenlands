package thebetweenlands.client.event.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.client.particle.entity.ParticleWisp;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.tile.RenderWisp;
import thebetweenlands.common.block.terrain.BlockWisp;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityWisp;
import thebetweenlands.util.MathUtils;


public class WorldRenderHandler {
	public static final List<Pair<Pair<RenderWisp, TileEntityWisp>, Vec3d>> WISP_TILE_LIST = new ArrayList<Pair<Pair<RenderWisp, TileEntityWisp>, Vec3d>>();

	@SubscribeEvent
	public static void renderWorld(RenderWorldLastEvent event) {
		double renderViewX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
		double renderViewY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
		double renderViewZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;

		///// Wisps /////
		GlStateManager.pushMatrix();

		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.004F);

		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		int prevMinFilter = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER);
		int prevMagFilter = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getBuffer();

		vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

		for(Pair<Pair<RenderWisp, TileEntityWisp>, Vec3d> e : WISP_TILE_LIST) {
			TileEntityWisp te = e.getKey().getValue();
			RenderWisp renderer = e.getKey().getKey();
			Vec3d pos = e.getValue();

			IBlockState blockState = te.getWorld().getBlockState(te.getPos());

			if(blockState.getBlock() == BlockRegistry.WISP) {
				renderer.renderWispParticles(vertexBuffer, te, pos.xCoord, pos.yCoord, pos.zCoord, event.getPartialTicks());

				double rx = pos.xCoord + renderViewX + 0.5D;
				double ry = pos.yCoord + renderViewY + 0.5D;
				double rz = pos.zCoord + renderViewZ + 0.5D;

				float size = 3.0F;
				if(!BlockWisp.canSee(te.getWorld())) {
					size = (1.0F - MathHelper.sin(MathUtils.PI / 16 * MathHelper.clamp_float(ParticleWisp.getDistanceToViewer(rx, ry, rz, event.getPartialTicks()), 10, 20))) * 1.2F;
				}

				int colorIndex = blockState.getValue(BlockWisp.COLOR);

				for(int i = 0; i < 2; i++) {
					int color = BlockWisp.COLORS[colorIndex * 2 + i];
					float r = (color >> 16 & 0xFF) / 255F;
					float g = (color >> 8 & 0xFF) / 255F;
					float b = (color & 0xFF) / 255F;
					if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
						ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(rx, ry, rz, 
								i == 0 ? size : size * 0.5F,
										r * (i == 0 ? 3.5F : 1.0F),
										g * (i == 0 ? 3.5F : 1.0F),
										b * (i == 0 ? 3.5F : 1.0F)));
					}
				}
			}
		}

		tessellator.draw();

		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, prevMinFilter);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, prevMagFilter);

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();

		GlStateManager.popMatrix();
		WISP_TILE_LIST.clear();
	}
}
