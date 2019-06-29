package thebetweenlands.common.handler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.BossType;
import thebetweenlands.api.entity.IBLBoss;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.util.Stencil;

public class BossHandler<T extends Entity & IBLBoss> {
	private BossHandler() { }

	private static class Listener implements IWorldEventListener {
		@Override
		public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState,
				int flags) { }

		@Override
		public void notifyLightSet(BlockPos pos) { }

		@Override
		public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) { }

		@Override
		public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x,
				double y, double z, float volume, float pitch) { }

		@Override
		public void playRecord(SoundEvent soundIn, BlockPos pos) { }

		@Override
		public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord,
				double xSpeed, double ySpeed, double zSpeed, int... parameters) { }

		@Override
		public void spawnParticle(int id, boolean ignoreRange, boolean p_190570_3_, double x, double y, double z,
				double xSpeed, double ySpeed, double zSpeed, int... parameters) { }

		@Override
		public void broadcastSound(int soundID, BlockPos pos, int data) { }

		@Override
		public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) { }

		@Override
		public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) { }


		@Override
		public void onEntityAdded(Entity entityIn) {
			if(entityIn instanceof IBLBoss) {
				BOSS_ENTITIES.add((IBLBoss) entityIn);
			}
		}

		@Override
		public void onEntityRemoved(Entity entityIn) {
			if(entityIn instanceof IBLBoss) {
				BOSS_ENTITIES.remove((IBLBoss) entityIn);
			}
		}
	}

	private static final ResourceLocation BOSS_BAR_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/boss_health_bar.png");
	private static final ResourceLocation MINIBOSS_BAR_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/miniboss_health_bar.png");
	private static final Listener LISTENER = new Listener();

	public static final Set<IBLBoss> BOSS_ENTITIES = new HashSet<>();


	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event) {
		event.getWorld().addEventListener(LISTENER);
	}

	@SubscribeEvent
	public static void onWorldUnload(WorldEvent.Unload event) {
		event.getWorld().removeEventListener(LISTENER);

		Iterator<IBLBoss> it = BOSS_ENTITIES.iterator();
		while(it.hasNext()) {
			Entity entity = (Entity) it.next();
			if(entity.getEntityWorld() == event.getWorld()) {
				it.remove();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onBossBarRender(RenderGameOverlayEvent.BossInfo event) {
		Minecraft mc = Minecraft.getMinecraft();

		IBLBoss boss = null;

		for(IBLBoss candidate : BOSS_ENTITIES) {
			if(event.getBossInfo().getUniqueId().equals(candidate.getBossInfoUuid())) {
				boss = candidate;
				break;
			}
		}

		if(boss != null) {
			event.setCanceled(true);

			if(boss.getBossType() == BossType.NORMAL_BOSS) {
				BossInfoClient info = event.getBossInfo();
				float percent = info.getPercent();
				ITextComponent name = info.getName();

				int texWidth = 256;
				int texHeight = 32/2;
				event.setIncrement(texHeight + 2);
				double renderWidth = 250;
				double renderHeight = (double)texHeight / (double)texWidth * renderWidth;
				double renderHealth  = (renderWidth - 16.0F / texWidth * renderWidth - (renderWidth - 16.0F / texWidth * renderWidth) * percent);

				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				mc.getTextureManager().bindTexture(BOSS_BAR_TEXTURE);
				//Old rendering code
				GlStateManager.enableBlend();

				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GlStateManager.pushMatrix();
				GlStateManager.translate(event.getResolution().getScaledWidth() / 2 - renderWidth / 2.0D, event.getY() - 2, 0);
				GlStateManager.glBegin(GL11.GL_QUADS);
				//Background
				GlStateManager.glTexCoord2f(0, 0);
				GL11.glVertex2d(0, 0);
				GlStateManager.glTexCoord2f(0, 0.5F);
				GL11.glVertex2d(0, renderHeight);
				GlStateManager.glTexCoord2f(1, 0.5F);
				GL11.glVertex2d(renderWidth, renderHeight);
				GlStateManager.glTexCoord2f(1, 0);
				GL11.glVertex2d(renderWidth, 0);
				//Foreground
				if (percent > 0) {
					GlStateManager.glTexCoord2f(0, 0.5F);
					GL11.glVertex2d(0, 0);
					GlStateManager.glTexCoord2f(0, 1.0F);
					GL11.glVertex2d(0, renderHeight);
					GlStateManager.glTexCoord2f(16.0F / texWidth + (1.0F - 16.0F / texWidth) * percent, 1.0F);
					GL11.glVertex2d(renderWidth - renderHealth, renderHeight);
					GlStateManager.glTexCoord2f(16.0F / texWidth + (1.0F - 16.0F / texWidth) * percent, 0.5F);
					GL11.glVertex2d(renderWidth - renderHealth, 0);
				}
				GlStateManager.glEnd();
				GlStateManager.popMatrix();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				int strWidth = TheBetweenlands.proxy.getCustomFontRenderer().getStringWidth(name.getFormattedText());
				TheBetweenlands.proxy.getCustomFontRenderer().drawString(name.getFormattedText(), event.getResolution().getScaledWidth() / 2 - strWidth / 2, event.getY() + 1, 0xFFFFFFFF);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onRenderLiving(RenderLivingEvent.Specials.Post<EntityLivingBase> event) {
		EntityLivingBase entity = event.getEntity();

		if(entity instanceof IBLBoss) {
			IBLBoss boss = (IBLBoss) entity;
			if(boss.getBossType() == BossType.MINI_BOSS) {
				BossInfo info = Minecraft.getMinecraft().ingameGUI.getBossOverlay().mapBossInfos.get(boss.getBossInfoUuid());
				if(info != null) {
					RenderManager renderManager = event.getRenderer().getRenderManager();
					TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
					Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();

					Tessellator tessellator = Tessellator.getInstance();

					float viewerYaw = renderManager.playerViewY;
					float viewerPitch = renderManager.playerViewX;
					boolean isThirdPersonFrontal = renderManager.options.thirdPersonView == 2;

					Vec3d offset = boss.getMiniBossTagOffset(event.getPartialRenderTick());

					double x = event.getX() + offset.x;
					double y = event.getY() + offset.y;
					double z = event.getZ() + offset.z;

					double emptyPercentage = 1.0D - info.getPercent();

					textureManager.bindTexture(MINIBOSS_BAR_TEXTURE);

					GlStateManager.pushMatrix();
					GlStateManager.translate(x, y, z);
					GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
					GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate((float)(isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
					GlStateManager.disableLighting();
					GlStateManager.enableBlend();
					GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
					GlStateManager.enableTexture2D();
					GlStateManager.enableAlpha();
					GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
					GlStateManager.depthMask(false);

					/*boolean useStencil = false;
					int stencilBit = MinecraftForgeClient.reserveStencilBit();
					int stencilMask = 1 << stencilBit;

					if(stencilBit >= 0) {
						useStencil = fbo.isStencilEnabled() ? true : fbo.enableStencil();
					}*/

					double width = boss.getMiniBossTagSize(event.getPartialRenderTick());
					double height = width;

					try(Stencil stencil = Stencil.reserve(fbo)) {
						if(stencil.valid()) {
							GL11.glEnable(GL11.GL_STENCIL_TEST);
							
							stencil.clear(false);
							
							stencil.func(GL11.GL_ALWAYS, true);
							stencil.op(GL11.GL_REPLACE);
							
							GlStateManager.colorMask(false, false, false, false);
							GlStateManager.alphaFunc(GL11.GL_GREATER, 0.5F);

							renderTagQuad(tessellator, -width, -height - (height - 0.2D) * emptyPercentage, width, height - height * emptyPercentage, 0, 0.5D, 0.5D, 1);

							GlStateManager.colorMask(true, true, true, true);
							GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
							
							stencil.func(GL11.GL_EQUAL, true);
							stencil.op(GL11.GL_KEEP);
						}

						renderTag(tessellator, fbo, textureManager, width, height, emptyPercentage, stencil);

						GlStateManager.depthMask(true);
						GlStateManager.colorMask(false, false, false, false);

						renderTag(tessellator, fbo, textureManager, width, height, emptyPercentage, stencil);
						
						GL11.glDisable(GL11.GL_STENCIL_TEST);
					}
					
					GlStateManager.colorMask(true, true, true, true);
					GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
					GlStateManager.enableLighting();
					GlStateManager.disableBlend();
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					GlStateManager.popMatrix();
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private static void renderTag(Tessellator tessellator, Framebuffer fbo, TextureManager textureManager, double width, double height, double emptyPercentage, Stencil stencil) {
		GL11.glDisable(GL11.GL_STENCIL_TEST);

		renderTagQuad(tessellator, -width, -height, width, height, 0, 0, 0.5D, 0.5D);

		if(stencil.valid()) {
			GL11.glEnable(GL11.GL_STENCIL_TEST);
		}

		renderTagQuad(tessellator, -width, -height, width, height, 0.5D, 0, 1, 0.5D);

		if(stencil.valid()) {
			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}
	}

	@SideOnly(Side.CLIENT)
	private static void renderTagQuad(Tessellator tessellator, double minX, double minY, double maxX, double maxY, double minU, double minV, double maxU, double maxV) {
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		buffer.pos(minX, minY, 0.0D).tex(minU, maxV).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		buffer.pos(minX, maxY, 0.0D).tex(minU, minV).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		buffer.pos(maxX, maxY, 0.0D).tex(maxU, minV).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		buffer.pos(maxX, minY, 0.0D).tex(maxU, maxV).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		tessellator.draw();
	}
}
