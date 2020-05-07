package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelDreadfulMummy;
import thebetweenlands.common.entity.mobs.EntityDreadfulMummy;

@SideOnly(Side.CLIENT)
public class RenderDreadfulMummy extends RenderLiving<EntityDreadfulMummy> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/dreadful_mummy.png");

	public RenderDreadfulMummy(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelDreadfulMummy(), 0.7F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityDreadfulMummy entity) {
		return TEXTURE;
	}

	@Override
	public void doRender(EntityDreadfulMummy entity, double x, double y, double z, float entityYaw, float partialTicks) {
		this.shadowSize = Math.max(0, entity.getSpawningProgress(partialTicks) * 1 - 0.3f);
		super.doRender(entity, x, y + entity.getInterpolatedYOffsetProgress(partialTicks), z, entityYaw, partialTicks);
	}
}