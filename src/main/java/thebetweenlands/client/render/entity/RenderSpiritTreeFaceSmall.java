package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.model.entity.ModelSpiritTreeFaceSmall1;
import thebetweenlands.client.render.model.entity.ModelSpiritTreeFaceSmall2;
import thebetweenlands.common.entity.mobs.EntitySpiritTreeFaceSmallBase;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderSpiritTreeFaceSmall extends RenderWallFace<EntitySpiritTreeFaceSmallBase> {
	protected static final ResourceLocation[] DESTROY_STAGES = new ResourceLocation[] {new ResourceLocation("textures/blocks/destroy_stage_0.png"), new ResourceLocation("textures/blocks/destroy_stage_1.png"), new ResourceLocation("textures/blocks/destroy_stage_2.png"), new ResourceLocation("textures/blocks/destroy_stage_3.png"), new ResourceLocation("textures/blocks/destroy_stage_4.png"), new ResourceLocation("textures/blocks/destroy_stage_5.png"), new ResourceLocation("textures/blocks/destroy_stage_6.png"), new ResourceLocation("textures/blocks/destroy_stage_7.png"), new ResourceLocation("textures/blocks/destroy_stage_8.png"), new ResourceLocation("textures/blocks/destroy_stage_9.png")};

	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/spirit_tree_face_small.png");

	private final ModelSpiritTreeFaceSmall1 model1 = new ModelSpiritTreeFaceSmall1();
	private final ModelSpiritTreeFaceSmall2 model2 = new ModelSpiritTreeFaceSmall2();

	protected final LayerOverlay<EntitySpiritTreeFaceSmallBase> glow;

	public RenderSpiritTreeFaceSmall(RenderManager renderManager) {
		super(renderManager, new ModelSpiritTreeFaceSmall1(), 0);
		this.addLayer(new LayerOverlay<EntitySpiritTreeFaceSmallBase>(this) {
			@Override
			protected ResourceLocation getTexture(EntitySpiritTreeFaceSmallBase entity, int index) {
				int damage = MathHelper.ceil((1.0F - entity.getHealth() / entity.getMaxHealth()) * 10.0F);
				if(damage > 0 && damage <= 10) {
					return DESTROY_STAGES[damage - 1];
				}
				return null;
			}

			@Override
			protected void renderOverlay(EntitySpiritTreeFaceSmallBase entity, ModelBase model, float limbSwing,
					float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch,
					float scale) {
				this.preRenderDamagedBlocks();
				super.renderOverlay(entity, model, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
				this.postRenderDamagedBlocks();
			}

			private void preRenderDamagedBlocks() {
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				GlStateManager.enableBlend();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
				GlStateManager.enableAlpha();

				GlStateManager.matrixMode(GL11.GL_TEXTURE);
				GlStateManager.pushMatrix();
				GlStateManager.loadIdentity();
				GlStateManager.scale(4f, 4f, 0);
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			}

			private void postRenderDamagedBlocks() {
				GlStateManager.disableAlpha();
				GlStateManager.enableAlpha();
				GlStateManager.depthMask(true);
				GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

				GlStateManager.matrixMode(GL11.GL_TEXTURE);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			}
		});
		this.addLayer(this.glow = new LayerOverlay<>(this, new ResourceLocation(ModInfo.ID, "textures/entity/spirit_tree_face_small_glow.png")).setGlow(true));
	}

	@Override
	protected void preRenderCallback(EntitySpiritTreeFaceSmallBase entity, float partialTicks) {
		super.preRenderCallback(entity, partialTicks);

		this.glow.setAlpha(entity.getGlow(partialTicks));

		float scale = 0.8F + entity.getHalfMovementProgress(partialTicks) * entity.getHalfMovementProgress(partialTicks) * 0.2F;
		GlStateManager.scale(scale, scale, scale);

		int variant = entity.getVariant();
		if(variant == 0) {
			this.mainModel = this.model1;
			if(entity.isAnchored()) {
				GlStateManager.translate(0, 1, -0.25D);
			} else {
				GlStateManager.translate(0, 1, 0.45D);
			}
		} else {
			this.mainModel = this.model2;
			if(entity.isAnchored()) {
				GlStateManager.translate(0, 0, -0.74D);
			}
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySpiritTreeFaceSmallBase entity) {
		return TEXTURE;
	}
}
