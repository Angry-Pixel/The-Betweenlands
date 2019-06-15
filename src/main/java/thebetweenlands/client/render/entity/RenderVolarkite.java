package thebetweenlands.client.render.entity;

import java.nio.FloatBuffer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.client.render.model.entity.ModelVolarkite;
import thebetweenlands.common.entity.EntityVolarkite;

public class RenderVolarkite extends Render<EntityVolarkite> {
	protected static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/volarkite.png");

	protected static final ModelVolarkite MODEL = new ModelVolarkite();

	protected static RenderPlayer renderPlayerSmallArmsVolarkite;
	protected static RenderPlayer renderPlayerNormalArmsVolarkite;

	protected final FloatBuffer brightnessBuffer = GLAllocation.createDirectFloatBuffer(4);

	public RenderVolarkite(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityVolarkite entity, double x, double y, double z, float yaw, float partialTicks) {
		this.bindEntityTexture(entity);

		GlStateManager.enableRescaleNormal();

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableTexture2D();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		GlStateManager.pushMatrix();

		GlStateManager.translate(x, y + 1.35D, z);

		GlStateManager.translate(0, -0.5D, 0);
		GlStateManager.rotate((float)-interpolate(entity.prevRotationYaw, entity.rotationYaw, partialTicks) + 180, 0, 1, 0);
		GlStateManager.rotate((float)-interpolate(entity.prevRotationRoll, entity.rotationRoll, partialTicks), 0, 0, 1);
		GlStateManager.rotate((float)-interpolate(entity.prevRotationPitch, entity.rotationPitch, partialTicks), 1, 0, 0);
		GlStateManager.translate(0, 0.5D, 0);

		GlStateManager.translate(0, 0, 0.14D);

		RenderHelper.enableStandardItemLighting();

		GlStateManager.scale(-1, -1, 1);
		MODEL.render();
		GlStateManager.disableRescaleNormal();

		GlStateManager.popMatrix();
	}

	protected static double interpolate(double prev, double now, double partialTicks) {
		return prev + (now - prev) * partialTicks;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityVolarkite entity) {
		return TEXTURE;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onPlayerRenderPre(RenderPlayerEvent.Pre event) {
		if(event.getEntityPlayer() instanceof AbstractClientPlayer) {
			AbstractClientPlayer player = (AbstractClientPlayer) event.getEntityPlayer();

			Entity ridingEntity = player.getRidingEntity();

			if(ridingEntity instanceof EntityVolarkite && event.getRenderer() instanceof RenderPlayerVolarkite == false) {
				event.setCanceled(true);

				EntityVolarkite kite = (EntityVolarkite) ridingEntity;

				GlStateManager.pushMatrix();

				//Make sure origin is at feet when rotating
				GlStateManager.translate(event.getX(), event.getY(), event.getZ());

				float kiteYaw = (float)interpolate(kite.prevRotationYaw, kite.rotationYaw, event.getPartialRenderTick());

				GlStateManager.rotate(-kiteYaw, 0, 1, 0);
				GlStateManager.translate(0, 1.0D, 0);
				GlStateManager.rotate((float)interpolate(kite.prevRotationRoll, kite.rotationRoll, event.getPartialRenderTick()), 0, 0, 1);
				GlStateManager.rotate((float)interpolate(kite.prevRotationPitch, kite.rotationPitch, event.getPartialRenderTick()), 1, 0, 0);
				GlStateManager.translate(0, -1.0D, 0);
				GlStateManager.rotate(kiteYaw, 0, 1, 0);

				GlStateManager.rotate((float)interpolate(player.prevRenderYawOffset, player.renderYawOffset, event.getPartialRenderTick()) - kiteYaw, 0, 1, 0);

				//Undo previous offset
				GlStateManager.translate(-event.getX(), -event.getY(), -event.getZ());

				boolean isSmallArms = "slim".equals(player.getSkinType());

				RenderPlayer playerRenderer;

				if(isSmallArms) {
					if(renderPlayerSmallArmsVolarkite == null) {
						renderPlayerSmallArmsVolarkite = new RenderPlayerVolarkite(event.getRenderer().getRenderManager(), true);
					}
					playerRenderer = renderPlayerSmallArmsVolarkite;
				} else {
					if(renderPlayerNormalArmsVolarkite == null) {
						renderPlayerNormalArmsVolarkite = new RenderPlayerVolarkite(event.getRenderer().getRenderManager(), false);
					}
					playerRenderer = renderPlayerNormalArmsVolarkite;
				}

				playerRenderer.doRender(player, event.getX(), event.getY(), event.getZ(), (float)interpolate(player.prevRotationYaw, player.rotationYaw, event.getPartialRenderTick()), event.getPartialRenderTick());

				RenderHelper.enableStandardItemLighting();

				GlStateManager.popMatrix();
			}
		}
	}
}
