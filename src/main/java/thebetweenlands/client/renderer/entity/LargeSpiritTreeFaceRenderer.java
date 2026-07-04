package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.LargeSpiritTreeFaceModel;
import thebetweenlands.client.renderer.entity.layers.AlphaGlowLayer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.spirit_tree.LargeSpiritTreeFace;

public class LargeSpiritTreeFaceRenderer extends WallFaceRenderer<LargeSpiritTreeFace, LargeSpiritTreeFaceModel> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/large_spirit_tree_face.png");
	public static final ResourceLocation GLOW_TEXTURE = TheBetweenlands.prefix("textures/entity/large_spirit_tree_face_glow.png");
	private final AlphaGlowLayer<LargeSpiritTreeFace, LargeSpiritTreeFaceModel> layer;

	public LargeSpiritTreeFaceRenderer(EntityRendererProvider.Context context) {
		super(context, new LargeSpiritTreeFaceModel(context.bakeLayer(BLModelLayers.LARGE_SPIRIT_TREE_FACE)), 0.0F);
		this.addLayer(this.layer = new AlphaGlowLayer<>(this, GLOW_TEXTURE));
	}

	@Override
	protected void renderModel(LargeSpiritTreeFace entity, PoseStack stack, MultiBufferSource buffer, int packedLight, int overlay, int color, RenderState state) {
		packedLight = LevelRenderer.getLightColor(entity.level(), entity.blockPosition().relative(entity.getFacing()));
		RenderType type = this.getRenderType(entity, state.visible(), state.translucent(), state.glowing());
		if (type != null) {
			this.getModel().renderToBuffer(stack, buffer.getBuffer(type), packedLight, overlay, color);
			int damage = Mth.ceil((1.0F - entity.getHealth() / entity.getMaxHealth()) * 10.0F);
			this.renderBreakingOverlay(this.getModel(), damage, stack, packedLight, overlay);
		}
	}

	@Override
	protected void scale(LargeSpiritTreeFace entity, PoseStack stack, float partialTick) {
		super.scale(entity, stack, partialTick);

		this.layer.setAlpha(entity.getGlow(partialTick));

		float wispStrengthModifier = entity.getWispStrengthModifier();
		if(wispStrengthModifier < 1.0F) {
			float colors = Math.max((wispStrengthModifier - 0.5F) / 0.5F, 0.0F) * 0.4F + 0.6F;
			this.layer.setColor((float)Math.pow(colors, 1.2F), colors, (float)Math.pow(colors, 1.2F));
		} else if(wispStrengthModifier > 1.0F) {
			float redness = Math.min((wispStrengthModifier - 1.0F) / 2.0F, 1.0F) * 0.8F + 0.2F;
			this.layer.setColor(1, 1 - redness, 1 - redness);
		} else {
			this.layer.setColor(1, 1, 1);
		}

		float scale = 0.8F + entity.getHalfMovementProgress(partialTick) * entity.getHalfMovementProgress(partialTick) * 0.2F;
		stack.scale(scale, scale, scale);

		if(entity.isAnchored()) {
			stack.translate(0, 0.15D, -0.7D);
		} else {
			stack.translate(0, 0.15D, 0.9D);
		}
	}

	@Override
	public ResourceLocation getTextureLocation(LargeSpiritTreeFace entity) {
		return TEXTURE;
	}
}
