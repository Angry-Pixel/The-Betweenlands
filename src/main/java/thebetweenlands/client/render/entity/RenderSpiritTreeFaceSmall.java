package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.model.entity.ModelSpiritTreeFaceSmall1;
import thebetweenlands.client.render.model.entity.ModelSpiritTreeFaceSmall2;
import thebetweenlands.common.entity.mobs.EntitySpiritTreeFaceSmall;
import thebetweenlands.common.lib.ModInfo;

@OnlyIn(Dist.CLIENT)
public class RenderSpiritTreeFaceSmall extends RenderWallFace<EntitySpiritTreeFaceSmall> {
	protected static final ResourceLocation[] DESTROY_STAGES = new ResourceLocation[] {new ResourceLocation("textures/blocks/destroy_stage_0.png"), new ResourceLocation("textures/blocks/destroy_stage_1.png"), new ResourceLocation("textures/blocks/destroy_stage_2.png"), new ResourceLocation("textures/blocks/destroy_stage_3.png"), new ResourceLocation("textures/blocks/destroy_stage_4.png"), new ResourceLocation("textures/blocks/destroy_stage_5.png"), new ResourceLocation("textures/blocks/destroy_stage_6.png"), new ResourceLocation("textures/blocks/destroy_stage_7.png"), new ResourceLocation("textures/blocks/destroy_stage_8.png"), new ResourceLocation("textures/blocks/destroy_stage_9.png")};

	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/spirit_tree_face_small.png");

	private final ModelSpiritTreeFaceSmall1 model1 = new ModelSpiritTreeFaceSmall1();
	private final ModelSpiritTreeFaceSmall2 model2 = new ModelSpiritTreeFaceSmall2();

	protected final LayerOverlay<EntitySpiritTreeFaceSmall> glow;

	public RenderSpiritTreeFaceSmall(RenderManager renderManager) {
		super(renderManager, new ModelSpiritTreeFaceSmall1(), 0);
		this.addLayer(new LayerOverlay<EntitySpiritTreeFaceSmall>(this) {
			@Override
			protected ResourceLocation getTexture(EntitySpiritTreeFaceSmall entity, int index) {
				int damage = MathHelper.ceil((1.0F - entity.getHealth() / entity.getMaxHealth()) * 10.0F);
				if(damage > 0 && damage <= 10) {
					return DESTROY_STAGES[damage - 1];
				}
				return null;
			}

			@Override
			protected void renderOverlay(EntitySpiritTreeFaceSmall entity, ModelBase model, float limbSwing,
					float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch,
					float scale) {
				this.preRenderDamagedBlocks();
				super.renderOverlay(entity, model, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
				this.postRenderDamagedBlocks();
			}

			private void preRenderDamagedBlocks() {
				GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.enableBlend();
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.5F);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
				GlStateManager.enableAlphaTest();

				GlStateManager.matrixMode(GL11.GL_TEXTURE);
				GlStateManager.pushMatrix();
				GlStateManager.loadIdentity();
				GlStateManager.scalef(4f, 4f, 0);
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			}

			private void postRenderDamagedBlocks() {
				GlStateManager.disableAlphaTest();
				GlStateManager.enableAlphaTest();
				GlStateManager.depthMask(true);
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

				GlStateManager.matrixMode(GL11.GL_TEXTURE);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			}
		});
		this.addLayer(this.glow = new LayerOverlay<>(this, new ResourceLocation(ModInfo.ID, "textures/entity/spirit_tree_face_small_glow.png")).setGlow(true));
	}

	@Override
	protected void preRenderCallback(EntitySpiritTreeFaceSmall entity, float partialTicks) {
		super.preRenderCallback(entity, partialTicks);

		this.glow.setAlpha(entity.getGlow(partialTicks));

		float scale = 0.8F + entity.getHalfMovementProgress(partialTicks) * entity.getHalfMovementProgress(partialTicks) * 0.2F;
		GlStateManager.scalef(scale, scale, scale);

		int variant = entity.getVariant();
		if(variant == 0) {
			this.mainModel = this.model1;
			GlStateManager.translatef(0, 1, -0.25F);
		} else {
			this.mainModel = this.model2;
			GlStateManager.translatef(0, 0, -0.74F);
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySpiritTreeFaceSmall entity) {
		return TEXTURE;
	}
}
