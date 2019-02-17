package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.entity.ModelPeatMummy;
import thebetweenlands.common.entity.mobs.EntityPeatMummy;

@OnlyIn(Dist.CLIENT)
public class RenderPeatMummy extends RenderLiving<EntityPeatMummy> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/peat_mummy.png");

	public RenderPeatMummy(RenderManager manager) {
		super(manager, new ModelPeatMummy(), 0.7F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityPeatMummy entity) {
		return TEXTURE;
	}

	@Override
	public void doRender(EntityPeatMummy entity, double x, double y, double z, float yaw, float partialTicks) {
		if(entity.getSpawningProgress() >= 0.1F) {
			super.doRender(entity, x, y + entity.getInterpolatedSpawningOffset(partialTicks), z, yaw, partialTicks);
		}
	}

	@Override
	public void doRenderShadowAndFire(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
		if(((EntityPeatMummy)entity).getSpawningProgress() >= 0.1F) {
			super.doRenderShadowAndFire(entity, x, y, z, yaw, partialTicks);
		}
	}
}