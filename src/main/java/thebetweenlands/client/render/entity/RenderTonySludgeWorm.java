package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelTonySludgeWorm;
import thebetweenlands.common.entity.mobs.EntityTonySludgeWorm;

@SideOnly(Side.CLIENT)
public class RenderTonySludgeWorm extends RenderSludgeWorm<EntityTonySludgeWorm> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/tony_sludge_worm.png");

	private final ModelTonySludgeWorm model;

	public RenderTonySludgeWorm(RenderManager manager) {
		super(manager, new ModelTonySludgeWorm(), 0.0F);
		this.model = (ModelTonySludgeWorm) this.mainModel;
	}

	@Override
	protected void renderHeadPartModel(EntityTonySludgeWorm entity, int frame, float wibbleStrength,
			float partialTicks) {
		bindTexture(TEXTURE);
		model.renderHead(entity, frame, wibbleStrength / 2, partialTicks);
	}

	@Override
	protected void renderBodyPartModel(EntityTonySludgeWorm entity, int frame, float wibbleStrength,
			float partialTicks) {
		bindTexture(TEXTURE);
		model.renderBody(entity, frame, wibbleStrength / 2, partialTicks);
	}

	@Override
	protected void renderTailPartModel(EntityTonySludgeWorm entity, int frame, float wibbleStrength,
			float partialTicks) {
		bindTexture(TEXTURE);
		model.renderTail(entity, frame, wibbleStrength / 2, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityTonySludgeWorm entity) {
		return TEXTURE;
	}

}