package thebetweenlands.client.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import thebetweenlands.client.render.entity.RenderFirefly;
import thebetweenlands.client.render.entity.RenderGasCloud;
import thebetweenlands.client.render.particle.entity.ParticleWisp;
import thebetweenlands.client.render.shader.GeometryBuffer;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.postprocessing.WorldShader;
import thebetweenlands.client.render.tile.RenderWisp;
import thebetweenlands.common.block.terrain.BlockWisp;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityGasCloud;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityWisp;
import thebetweenlands.util.MathUtils;
import thebetweenlands.util.RenderUtils;


public class WorldRenderHandler {
	private static final Minecraft MC = Minecraft.getMinecraft();

	public static final List<Pair<Pair<RenderWisp, TileEntityWisp>, Vec3d>> WISP_TILE_LIST = new ArrayList<>();
	public static final List<Pair<Pair<RenderFirefly, EntityFirefly>, Vec3d>> FIREFLIES = new ArrayList<>();
	public static final List<Pair<Pair<RenderGasCloud, EntityGasCloud>, Vec3d>> GAS_CLOUDS = new ArrayList<>();
	public static final List<Pair<Vec3d, Float>> REPELLER_SHIELDS = new ArrayList<>();

	private static float partialTicks;

	private static int sphereDispList = -2;

	@SubscribeEvent
	public static void onRenderTick(RenderTickEvent event) {
		if(event.phase == Phase.START) {
			partialTicks = event.renderTickTime;
		}
	}

	public static float getPartialTicks() {
		return partialTicks;
	}

	@SubscribeEvent
	public static void renderWorld(RenderWorldLastEvent event) {
		double renderViewX = MC.getRenderManager().viewerPosX;
		double renderViewY = MC.getRenderManager().viewerPosY;
		double renderViewZ = MC.getRenderManager().viewerPosZ;

		int parentFboId = -1;
		if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
			parentFboId = RenderUtils.getBoundFramebuffer();
		}
		if(parentFboId == -1) {
			parentFboId = MC.getFramebuffer().framebufferObject;
		}
		
		///// Wisps /////
		GlStateManager.pushMatrix();

		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.004F);

		ITextureObject texture = MC.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		texture.setBlurMipmap(true, false);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexBuffer = tessellator.getBuffer();

		vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

		for (Pair<Pair<RenderWisp, TileEntityWisp>, Vec3d> e : WISP_TILE_LIST) {
			TileEntityWisp te = e.getKey().getValue();
			RenderWisp renderer = e.getKey().getKey();
			Vec3d pos = e.getValue();

			IBlockState blockState = te.getWorld().getBlockState(te.getPos());

			if (blockState.getBlock() == BlockRegistry.WISP) {
				renderer.renderWispParticles(vertexBuffer, te, pos.x, pos.y, pos.z, event.getPartialTicks());

				double rx = pos.x + renderViewX + 0.5D;
				double ry = pos.y + renderViewY + 0.5D;
				double rz = pos.z + renderViewZ + 0.5D;

				float size = 3.0F;
				if (!BlockWisp.canSee(te.getWorld(), te.getPos())) {
					size = (1.0F - MathHelper.sin(MathUtils.PI / 16 * MathHelper.clamp(ParticleWisp.getDistanceToViewer(rx, ry, rz, event.getPartialTicks()), 10, 20))) * 1.2F;
				}

				int colorIndex = blockState.getValue(BlockWisp.COLOR);

				for (int i = 0; i < 2; i++) {
					int color = BlockWisp.COLORS[colorIndex * 2 + i];
					float r = (color >> 16 & 0xFF) / 255F;
					float g = (color >> 8 & 0xFF) / 255F;
					float b = (color & 0xFF) / 255F;
					if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
						ShaderHelper.INSTANCE.require();
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

		//Fireflies
		GlStateManager.pushMatrix();
		for (Pair<Pair<RenderFirefly, EntityFirefly>, Vec3d> e : FIREFLIES) {
			Vec3d pos = e.getValue();
			RenderFirefly renderer = e.getKey().getKey();
			EntityFirefly entity = e.getKey().getValue();
			renderer.renderFireflyGlow(entity, pos.x, pos.y, pos.z, event.getPartialTicks());
		}
		GlStateManager.popMatrix();
		FIREFLIES.clear();

		//Gas clouds
		if(ShaderHelper.INSTANCE.isWorldShaderActive() && !GAS_CLOUDS.isEmpty()) {
			GeometryBuffer fbo = ShaderHelper.INSTANCE.getWorldShader().getGasParticleBuffer();
			if(fbo != null && fbo.isInitialized()) {
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.004F);
				GlStateManager.depthMask(true);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				
				//Render particles to gas fbo
				fbo.bind();
	
				MC.getTextureManager().bindTexture(RenderGasCloud.TEXTURE);
				
				vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
	
				for (Pair<Pair<RenderGasCloud, EntityGasCloud>, Vec3d> e : GAS_CLOUDS) {
					RenderGasCloud renderer = e.getKey().getKey();
					EntityGasCloud entity = e.getKey().getValue();
					
					renderer.renderGasParticles(vertexBuffer, entity, partialTicks);
				}
				
				tessellator.draw();
	
				OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, parentFboId);
				
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
			}
		}
		GAS_CLOUDS.clear();
		
		MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		texture.restoreLastBlurMipmap();

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();

		GlStateManager.popMatrix();
		WISP_TILE_LIST.clear();

		//// Repeller shields ////
		if(sphereDispList == -2) {
			sphereDispList = GL11.glGenLists(1);
			GL11.glNewList(sphereDispList, GL11.GL_COMPILE);
			new Sphere().draw(1.0F, 30, 30);
			GL11.glEndList();
		}
		if(ShaderHelper.INSTANCE.isWorldShaderActive() && sphereDispList >= 0) {
			Framebuffer mainFramebuffer = Minecraft.getMinecraft().getFramebuffer();

			WorldShader shader = ShaderHelper.INSTANCE.getWorldShader();
			if(shader != null) {
				GeometryBuffer gBuffer = shader.getRepellerShieldBuffer();
				gBuffer.updateGeometryBuffer(mainFramebuffer.framebufferWidth, mainFramebuffer.framebufferHeight);
				if(gBuffer != null && gBuffer.isInitialized()) {
					gBuffer.bind();
					gBuffer.clear(1.0F, 0.0F, 0.0F, 1.0F);

					if(!REPELLER_SHIELDS.isEmpty()) {
						GlStateManager.depthMask(true);
						GlStateManager.disableTexture2D();
						GlStateManager.disableBlend();
						GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
						GlStateManager.color(0.0F, 0.4F + (float)(Math.sin(System.nanoTime() / 500000000.0F) + 1.0F) * 0.2F, 0.8F - (float)(Math.cos(System.nanoTime() / 400000000.0F) + 1.0F) * 0.2F, 1.0F);
						GlStateManager.disableCull();

						//Render to G-Buffer 1
						for(Entry<Vec3d, Float> e : REPELLER_SHIELDS) {
							Vec3d pos = e.getKey();
							GlStateManager.pushMatrix();
							GlStateManager.translate(pos.x, pos.y, pos.z);
							GlStateManager.scale(e.getValue(), e.getValue(), e.getValue());
							GL11.glCallList(sphereDispList);
							GlStateManager.popMatrix();
						}

						GlStateManager.enableTexture2D();
						GlStateManager.enableCull();
						GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
						GlStateManager.color(1, 1, 1, 1);
					}

					gBuffer.updateDepthBuffer();

					OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, parentFboId);
				}
			}
		} else if(sphereDispList >= 0 && !REPELLER_SHIELDS.isEmpty()) {
			GlStateManager.depthMask(false);
			GlStateManager.disableCull();
			GlStateManager.disableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.cullFace(CullFace.BACK);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
			GlStateManager.color(0.0F, (0.4F + (float)(Math.sin(System.nanoTime() / 500000000.0F) + 1.0F) * 0.2F) / 3.0F, (0.8F - (float)(Math.cos(System.nanoTime() / 400000000.0F) + 1.0F) * 0.2F) / 3.0F, 0.3F);
			for(Entry<Vec3d, Float> e : REPELLER_SHIELDS) {
				Vec3d pos = e.getKey();
				GlStateManager.pushMatrix();
				GlStateManager.translate(pos.x, pos.y, pos.z);
				GlStateManager.scale(e.getValue(), e.getValue(), e.getValue());
				GL11.glCallList(sphereDispList);
				GlStateManager.popMatrix();
			}
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
			GlStateManager.enableTexture2D();
			GlStateManager.depthMask(true);
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.enableCull();
		}
		REPELLER_SHIELDS.clear();
	}
}
