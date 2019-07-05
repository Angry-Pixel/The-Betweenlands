package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelLargeSludgeWorm;
import thebetweenlands.common.entity.mobs.EntityLargeSludgeWorm;

@SideOnly(Side.CLIENT)
public class RenderLargeSludgeWorm extends RenderSludgeWorm<EntityLargeSludgeWorm> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/large_sludge_worm.png");

	private final ModelLargeSludgeWorm model;

	private int renderPass = 0;

	public RenderLargeSludgeWorm(RenderManager manager) {
		super(manager, new ModelLargeSludgeWorm(), 0.0F);
		this.model = (ModelLargeSludgeWorm) this.mainModel;
	}

	@Override
	public void doRender(EntityLargeSludgeWorm entity, double x, double y, double z, float yaw, float partialTicks) {
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();

		for(int i = 0; i < 4; i++) {
			this.renderPass = i;
			super.doRender(entity, x, y, z, yaw, partialTicks);
		}
	}

	@Override
	protected void renderHeadPartModel(EntityLargeSludgeWorm entity, int frame, float wibbleStrength,
			float partialTicks) {
		bindTexture(TEXTURE);

		GlStateManager.enableCull();

		this.prePass();
		model.renderHead(entity, frame, wibbleStrength, partialTicks, this.renderPass == 1);
		this.postPass();
	}

	@Override
	protected void renderBodyPartModel(EntityLargeSludgeWorm entity, int frame, float wibbleStrength,
			float partialTicks) {
		bindTexture(TEXTURE);

		GlStateManager.enableCull();

		this.prePass();
		model.renderBody(entity, frame, wibbleStrength, partialTicks, this.renderPass == 1);
		this.postPass();
	}

	@Override
	protected void renderTailPartModel(EntityLargeSludgeWorm entity, int frame, float wibbleStrength,
			float partialTicks) {
		bindTexture(TEXTURE);

		GlStateManager.enableCull();

		this.prePass();
		model.renderTail(entity, frame, wibbleStrength, partialTicks, this.renderPass == 1);
		this.postPass();
	}

	protected void prePass() {
		switch(this.renderPass) {
		case 0:
			GlStateManager.enableCull();
			GlStateManager.depthMask(false);
			GlStateManager.cullFace(CullFace.FRONT);
			break;
		case 2:
			GlStateManager.enableCull();
			GlStateManager.depthMask(false);
			GlStateManager.cullFace(CullFace.BACK);
			break;
		case 3:
			GlStateManager.enableCull();
			GlStateManager.depthMask(true);
			GlStateManager.colorMask(false, false, false, false);
			break;
		default:
			GlStateManager.disableCull();
			GlStateManager.cullFace(CullFace.BACK);
			break;
		}
	}

	protected void postPass() {
		GlStateManager.enableCull();
		GlStateManager.depthMask(true);
		
		switch(this.renderPass) {
		case 0:
			GlStateManager.cullFace(CullFace.BACK);
			break;
		case 2:
			GlStateManager.cullFace(CullFace.BACK);
			break;
		case 3:
			GlStateManager.colorMask(true, true, true, true);
			break;
		default:
			break;
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLargeSludgeWorm entity) {
		return TEXTURE;
	}

}