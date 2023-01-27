package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelBigPuffshroom;
import thebetweenlands.common.entity.EntityBigPuffshroom;

@SideOnly(Side.CLIENT)
public class RenderBigPuffshroom extends RenderLiving<EntityBigPuffshroom> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/puffshroom.png");
	private static final  ModelBigPuffshroom MODEL = new ModelBigPuffshroom();

	public RenderBigPuffshroom(RenderManager manager) {
		super(manager, MODEL, 0.5F);
	}

	@Override
	public void doRender(EntityBigPuffshroom entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		if (entity.animation_1 != 0) {
			bindTexture(TEXTURE);
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y + 3F, z);
			GlStateManager.scale(-3, -3, 3);
			MODEL.render(entity, partialTicks);
			GlStateManager.popMatrix();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBigPuffshroom entity) {
		return TEXTURE;
	}
}