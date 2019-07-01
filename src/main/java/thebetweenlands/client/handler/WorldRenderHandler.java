package thebetweenlands.client.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import thebetweenlands.api.capability.IEntityCustomCollisionsCapability;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.client.render.entity.RenderGasCloud;
import thebetweenlands.client.render.entity.RenderPlayerColored;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.shader.GeometryBuffer;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.postprocessing.WorldShader;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationSludgeWormDungeon;
import thebetweenlands.util.RenderUtils;


public class WorldRenderHandler {
	private static final Minecraft MC = Minecraft.getMinecraft();

	public static final List<Pair<Vec3d, Float>> REPELLER_SHIELDS = new ArrayList<>();

	private static float partialTicks;

	private static int sphereDispList = -2;

	private static RenderPlayerColored renderPlayerSmallArmsColored;
	private static RenderPlayerColored renderPlayerNormalArmsColored;

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
	public static void onClientTick(ClientTickEvent event) {
		if(event.phase == TickEvent.Phase.END && !Minecraft.getMinecraft().isGamePaused() && Minecraft.getMinecraft().world != null) {
			BatchedParticleRenderer.INSTANCE.update();
		}
	}

	@SubscribeEvent
	public static void renderWorld(RenderWorldLastEvent event) {
		Framebuffer mainFramebuffer = MC.getFramebuffer();

		int parentFboId = -1;
		if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
			parentFboId = RenderUtils.getBoundFramebuffer();
		}
		if(parentFboId == -1) {
			parentFboId = mainFramebuffer.framebufferObject;
		}

		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();

		//// Repeller shields ////
		if(sphereDispList == -2) {
			sphereDispList = GL11.glGenLists(1);
			GL11.glNewList(sphereDispList, GL11.GL_COMPILE);
			new Sphere().draw(1.0F, 30, 30);
			GL11.glEndList();
		}
		if(ShaderHelper.INSTANCE.isWorldShaderActive() && sphereDispList >= 0) {
			WorldShader shader = ShaderHelper.INSTANCE.getWorldShader();
			if(shader != null) {
				GeometryBuffer gBuffer = shader.getRepellerShieldBuffer();
				gBuffer.updateGeometryBuffer(mainFramebuffer.framebufferWidth, mainFramebuffer.framebufferHeight);
				if(gBuffer != null && gBuffer.isInitialized()) {
					gBuffer.bind();
					gBuffer.clear(0.0F, 0.0F, 0.0F, 1.0F);

					GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);

					if(!REPELLER_SHIELDS.isEmpty()) {
						GlStateManager.depthMask(true);
						GlStateManager.disableTexture2D();
						GlStateManager.disableBlend();
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
						GlStateManager.color(1, 1, 1, 1);
					}

					World world = MC.world;

					if(world != null) {
						for(EntityPlayer player : MC.world.playerEntities) {
							if(player instanceof AbstractClientPlayer) {
								AbstractClientPlayer clientPlayer = (AbstractClientPlayer) player;

								IEntityCustomCollisionsCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_ENTITY_CUSTOM_BLOCK_COLLISIONS, null);

								if(cap != null) {
									double obstructionDistance = cap.getObstructionDistance();

									if(obstructionDistance < 0) {
										ShaderHelper.INSTANCE.require();

										float alpha = Math.min((float)-obstructionDistance * 2, 1);

										renderPlayerGlow(clientPlayer, alpha, event.getPartialTicks());
									}
								}
							}
						}
					}

					GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

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

		if(MC.getRenderViewEntity() != null) {
			BatchedParticleRenderer.INSTANCE.renderAll(MC.getRenderViewEntity(), event.getPartialTicks());
		}

		if(ShaderHelper.INSTANCE.isWorldShaderActive() && (!DefaultParticleBatches.HEAT_HAZE_PARTICLE_ATLAS.isEmpty() || !DefaultParticleBatches.HEAT_HAZE_BLOCK_ATLAS.isEmpty())) {
			ShaderHelper.INSTANCE.require();
		}

		//Gas clouds/Heat haze
		if(ShaderHelper.INSTANCE.isWorldShaderActive() && MC.getRenderViewEntity() != null) {
			GeometryBuffer fbo = ShaderHelper.INSTANCE.getWorldShader().getGasParticleBuffer();
			if(fbo != null) {
				fbo.updateGeometryBuffer(mainFramebuffer.framebufferWidth, mainFramebuffer.framebufferHeight);
				fbo.clear(0, 0, 0, 0, 1);

				if(!DefaultParticleBatches.GAS_CLOUDS.isEmpty()) {
					MC.getTextureManager().bindTexture(RenderGasCloud.TEXTURE);

					BatchedParticleRenderer.INSTANCE.renderBatch(DefaultParticleBatches.GAS_CLOUDS, MC.getRenderViewEntity(), event.getPartialTicks());
				}

				if(!DefaultParticleBatches.HEAT_HAZE_PARTICLE_ATLAS.isEmpty()) {
					BatchedParticleRenderer.INSTANCE.renderBatch(DefaultParticleBatches.HEAT_HAZE_PARTICLE_ATLAS, MC.getRenderViewEntity(), event.getPartialTicks());
				}

				if(!DefaultParticleBatches.HEAT_HAZE_BLOCK_ATLAS.isEmpty()) {
					BatchedParticleRenderer.INSTANCE.renderBatch(DefaultParticleBatches.HEAT_HAZE_BLOCK_ATLAS, MC.getRenderViewEntity(), event.getPartialTicks());
				}

				//Update gas particles depth buffer
				fbo.updateDepthBuffer();

				OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, parentFboId);
			}
		}

		MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		//Sludge worm dungeon ground fog
		if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
			WorldShader shader = ShaderHelper.INSTANCE.getWorldShader();
			if(shader != null) {
				BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(Minecraft.getMinecraft().world);

				for (ILocalStorage sharedStorage : worldStorage.getLocalStorageHandler().getLoadedStorages()) {
					if (sharedStorage instanceof LocationSludgeWormDungeon) {
						if(((LocationSludgeWormDungeon) sharedStorage).addGroundFogVolumesToShader(shader)) {
							ShaderHelper.INSTANCE.require();
						}
					}
				}
			}
		}
	}

	private static void renderPlayerGlow(AbstractClientPlayer player, float strength, float partialTicks) {
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

		//From MC.getRenderManager().renderEntityStatic(player, partialTicks, false);

		if (player.ticksExisted == 0)
		{
			player.lastTickPosX = player.posX;
			player.lastTickPosY = player.posY;
			player.lastTickPosZ = player.posZ;
		}

		double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
		double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
		double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;
		float f = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;

		GlStateManager.color(1, 1, 1, 1);

		boolean isSmallArms = "slim".equals(player.getSkinType());

		RenderPlayerColored playerRenderer;

		if(isSmallArms) {
			if(renderPlayerSmallArmsColored == null) {
				renderPlayerSmallArmsColored = new RenderPlayerColored(MC.getRenderManager(), true);
			}
			playerRenderer = renderPlayerSmallArmsColored;
		} else {
			if(renderPlayerNormalArmsColored == null) {
				renderPlayerNormalArmsColored = new RenderPlayerColored(MC.getRenderManager(), false);
			}
			playerRenderer = renderPlayerNormalArmsColored;
		}

		//Set alpha < 0.99 so that shader inverts inBack check
		playerRenderer.setColor(strength, strength, strength, 0.98F);

		GlStateManager.disableLighting();

		//Polygon offset is necessary for inBack check in shader
		GlStateManager.enablePolygonOffset();
		GlStateManager.doPolygonOffset(0, -5);

		playerRenderer.doRender(player, d0 - MC.getRenderManager().renderPosX, d1 - MC.getRenderManager().renderPosY, d2 - MC.getRenderManager().renderPosZ, f, partialTicks);

		GlStateManager.disablePolygonOffset();

		GlStateManager.disableLighting();
	}
}
