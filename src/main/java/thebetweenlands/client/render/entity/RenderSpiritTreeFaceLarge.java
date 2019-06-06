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
import thebetweenlands.client.render.model.entity.ModelSpiritTreeFaceLarge;
import thebetweenlands.common.entity.mobs.EntitySpiritTreeFaceLarge;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderSpiritTreeFaceLarge extends RenderWallFace<EntitySpiritTreeFaceLarge> {
	protected static final ResourceLocation[] DESTROY_STAGES = new ResourceLocation[] {new ResourceLocation("textures/blocks/destroy_stage_0.png"), new ResourceLocation("textures/blocks/destroy_stage_1.png"), new ResourceLocation("textures/blocks/destroy_stage_2.png"), new ResourceLocation("textures/blocks/destroy_stage_3.png"), new ResourceLocation("textures/blocks/destroy_stage_4.png"), new ResourceLocation("textures/blocks/destroy_stage_5.png"), new ResourceLocation("textures/blocks/destroy_stage_6.png"), new ResourceLocation("textures/blocks/destroy_stage_7.png"), new ResourceLocation("textures/blocks/destroy_stage_8.png"), new ResourceLocation("textures/blocks/destroy_stage_9.png")};

	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/spirit_tree_face_large.png");

	private final ModelSpiritTreeFaceLarge model = new ModelSpiritTreeFaceLarge();

	protected final LayerOverlay<EntitySpiritTreeFaceLarge> glow;

	public RenderSpiritTreeFaceLarge(RenderManager renderManager) {
		super(renderManager, new ModelSpiritTreeFaceLarge(), 0);
		this.addLayer(new LayerOverlay<EntitySpiritTreeFaceLarge>(this) {
			@Override
			protected ResourceLocation getTexture(EntitySpiritTreeFaceLarge entity, int index) {
				int damage = MathHelper.ceil((1.0F - entity.getHealth() / entity.getMaxHealth()) * 10.0F);
				if(damage > 0 && damage <= 10) {
					return DESTROY_STAGES[damage - 1];
				}
				return null;
			}

			@Override
			protected void renderOverlay(EntitySpiritTreeFaceLarge entity, ModelBase model, float limbSwing,
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
				GlStateManager.scale(8f, 8f, 0);
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
		this.addLayer(this.glow = new LayerOverlay<>(this, new ResourceLocation(ModInfo.ID, "textures/entity/spirit_tree_face_large_glow.png")).setGlow(true));
	}

	@Override
	protected void preRenderCallback(EntitySpiritTreeFaceLarge entity, float partialTicks) {
		super.preRenderCallback(entity, partialTicks);

		this.glow.setAlpha(entity.getGlow(partialTicks));

		float wispStrengthModifier = entity.getWispStrengthModifier();
		if(wispStrengthModifier < 1.0F) {
			float colors = Math.max((wispStrengthModifier - 0.5F) / 0.5F, 0.0F) * 0.4F + 0.6F;
			this.glow.setColor((float)Math.pow(colors, 1.2F), colors, (float)Math.pow(colors, 1.2F));
		} else if(wispStrengthModifier > 1.0F) {
			float redness = Math.min((wispStrengthModifier - 1.0F) / 2.0F, 1.0F) * 0.8F + 0.2F;
			this.glow.setColor(1, 1 - redness, 1 - redness);
		} else {
			this.glow.setColor(1, 1, 1);
		}

		float scale = 0.8F + entity.getHalfMovementProgress(partialTicks) * entity.getHalfMovementProgress(partialTicks) * 0.2F;
		GlStateManager.scale(scale, scale, scale);

		if(entity.isAnchored()) {
			GlStateManager.translate(0, 0.15D, -0.7D);
		} else {
			GlStateManager.translate(0, 0.15D, 0.9D);
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySpiritTreeFaceLarge entity) {
		return TEXTURE;
	}
}
