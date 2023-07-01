package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelPuffshroomBuilder;
import thebetweenlands.common.entity.EntityPuffshroomBuilder;

@SideOnly(Side.CLIENT)
public class RenderPuffshroomBuilder extends RenderLiving<EntityPuffshroomBuilder> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/puffshroom_builder.png");
	private static final  ModelPuffshroomBuilder MODEL = new ModelPuffshroomBuilder();

	public RenderPuffshroomBuilder(RenderManager manager) {
		super(manager, MODEL, 0.5F);
	}

	@Override
	public void doRender(EntityPuffshroomBuilder entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		float smoothedTicks = entity.prev_renderTicks + (entity.renderTicks - entity.prev_renderTicks)* partialTicks;
		float flap = MathHelper.sin((smoothedTicks) * 0.25F) * 0.125F;
		bindTexture(TEXTURE);
		if (entity.getIsMiddle()) {
			float scale = 2F / 1024F;
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y + 1.5F + scale * entity.getGrowthCount() * 1.5F, z);
			GlStateManager.scale(-1 - scale * entity.getGrowthCount() + flap, -1- scale * entity.getGrowthCount(), 1 + scale * entity.getGrowthCount() - flap);
			GlStateManager.rotate(entityYaw, 0, 1, 0);
			MODEL.renderSpore(0.0625F);
			GlStateManager.popMatrix();
		}
		else {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y + 1.5F, z);
			GlStateManager.scale(-1, -1, 1);
			GlStateManager.rotate(entityYaw, 0, 1, 0);
			MODEL.renderTendril(0.0625F);
			GlStateManager.popMatrix();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityPuffshroomBuilder entity) {
		return TEXTURE;
	}
}