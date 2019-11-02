package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelMovingWall;
import thebetweenlands.common.entity.EntityMovingWall;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderMovingWall extends Render<EntityMovingWall> {
	private static final ResourceLocation MODEL_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/moving_wall.png");

	private static final ModelMovingWall MODEL = new ModelMovingWall();

	public RenderMovingWall(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	public void doRender(EntityMovingWall entity, double x, double y, double z, float entityYaw, float partialTicks) {
		this.bindEntityTexture(entity);

		GlStateManager.disableBlend();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.disableCull();

		GlStateManager.pushMatrix();

		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(entity.rotationYaw, 0F, 1F, 0F);
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		GlStateManager.translate(0.0F, -0.501F, 0.0F);

		MODEL.render();

		GlStateManager.popMatrix();

		GlStateManager.enableCull();
		GlStateManager.disableRescaleNormal();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMovingWall entity) {
		return MODEL_TEXTURE;
	}
}
