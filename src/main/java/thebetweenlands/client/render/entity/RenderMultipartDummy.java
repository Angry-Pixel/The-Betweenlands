package thebetweenlands.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.entity.mobs.EntityMultipartDummy;

public class RenderMultipartDummy extends Render<EntityMultipartDummy> {
	public RenderMultipartDummy(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityMultipartDummy entity, double x, double y, double z, float entityYaw, float partialTicks) {
		this.delegateRender(entity, x, y, z, entityYaw, partialTicks, false);
	}

	@Override
	public void renderMultipass(EntityMultipartDummy entity, double x, double y, double z, float entityYaw, float partialTicks) {
		this.delegateRender(entity, x, y, z, entityYaw, partialTicks, true);
	}

	private void delegateRender(EntityMultipartDummy entity, double x, double y, double z, float entityYaw, float partialTicks, boolean isMultipass) {
		MultiPartEntityPart parent = entity.getParent();

		if(parent != null) {
			IEntityMultiPart mainEntityMP = ((MultiPartEntityPart) parent).parent;

			if(mainEntityMP instanceof Entity) {
				Entity mainEntity = (Entity) mainEntityMP;

				Render<Entity> mainEntityRenderer = this.renderManager.getEntityRenderObject(mainEntity);

				if(mainEntityRenderer instanceof IMultipartDummyRendererDelegate && (!isMultipass || mainEntityRenderer.isMultipass())) {
					Entity renderViewEntity = Minecraft.getMinecraft().getRenderViewEntity();

					double rx = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * (double)partialTicks;
					double ry = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * (double)partialTicks;
					double rz = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * (double)partialTicks;

					ICamera camera = new Frustum();
					camera.setPosition(rx, ry, rz);

					if(this.renderManager.shouldRender(mainEntity, camera, rx, ry, rz)) {
						((IMultipartDummyRendererDelegate<?>) mainEntityRenderer).setRenderFromMultipart(entity);

						if(isMultipass) {
							this.renderManager.renderMultipass(mainEntity, partialTicks);
						} else {
							this.renderManager.renderEntityStatic(mainEntity, partialTicks, false);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isMultipass() {
		return true;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMultipartDummy entity) {
		return null;
	}
}
