package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelTinySludgeWorm;
import thebetweenlands.common.entity.mobs.EntityTinySludgeWorm;

@SideOnly(Side.CLIENT)
public class RenderTinySludgeWorm extends RenderSludgeWorm<EntityTinySludgeWorm> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/tiny_sludge_worm.png");

	private final ModelTinySludgeWorm model;

	public RenderTinySludgeWorm(RenderManager manager) {
		super(manager, new ModelTinySludgeWorm(), 0.0F);
		this.model = (ModelTinySludgeWorm) this.mainModel;
	}

	@Override
	protected void renderHeadPartModel(EntityTinySludgeWorm entity, int frame, float wibbleStrength,
			float partialTicks) {
		bindTexture(TEXTURE);
		model.renderHead(entity, frame, wibbleStrength / 2, partialTicks);
	}

	@Override
	protected void renderBodyPartModel(EntityTinySludgeWorm entity, int frame, float wibbleStrength,
			float partialTicks) {
		bindTexture(TEXTURE);
		model.renderBody(entity, frame, wibbleStrength / 2, partialTicks);
	}

	@Override
	protected void renderTailPartModel(EntityTinySludgeWorm entity, int frame, float wibbleStrength,
			float partialTicks) {
		bindTexture(TEXTURE);
		model.renderTail(entity, frame, wibbleStrength / 2, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityTinySludgeWorm entity) {
		return TEXTURE;
	}

}