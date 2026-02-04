package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.SmallSpiritTreeFaceModel;
import thebetweenlands.client.renderer.entity.layers.AlphaGlowLayer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.spirit_tree.AbstractSmallSpritTreeFace;

public class SmallSpiritTreeFaceRenderer<T extends AbstractSmallSpritTreeFace> extends WallFaceRenderer<T, SmallSpiritTreeFaceModel<T>> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/small_spirit_tree_face.png");
	public static final ResourceLocation GLOW_TEXTURE = TheBetweenlands.prefix("textures/entity/small_spirit_tree_face_glow.png");
	private final AlphaGlowLayer<T, SmallSpiritTreeFaceModel<T>> layer;
	private final SmallSpiritTreeFaceModel<T> face1;
	private final SmallSpiritTreeFaceModel<T> face2;


	public SmallSpiritTreeFaceRenderer(EntityRendererProvider.Context context) {
		super(context, new SmallSpiritTreeFaceModel<>(context.bakeLayer(BLModelLayers.SMALL_SPIRIT_TREE_FACE_1)), 0.0F);
		this.face1 = this.getModel();
		this.face2 = new SmallSpiritTreeFaceModel<>(context.bakeLayer(BLModelLayers.SMALL_SPIRIT_TREE_FACE_2));
		this.addLayer(this.layer = new AlphaGlowLayer<>(this, GLOW_TEXTURE));
	}

	@Override
	protected void renderModel(T entity, PoseStack stack, MultiBufferSource buffer, int packedLight, int overlay, int color, RenderState state) {
		packedLight = LevelRenderer.getLightColor(entity.level(), entity.blockPosition().relative(entity.getFacing()));
		RenderType type = this.getRenderType(entity, state.visible(), state.translucent(), state.glowing());
		if (type != null) {
			this.getModel().renderToBuffer(stack, buffer.getBuffer(type), packedLight, overlay, color);
			int damage = Mth.ceil((1.0F - entity.getHealth() / entity.getMaxHealth()) * 10.0F);
			this.renderBreakingOverlay(this.getModel(), damage, stack, packedLight, overlay);
		}
	}

	@Override
	protected void scale(T entity, PoseStack stack, float partialTick) {
		super.scale(entity, stack, partialTick);

		this.layer.setAlpha(entity.getGlow(partialTick));

		float scale = 0.8F + entity.getHalfMovementProgress(partialTick) * entity.getHalfMovementProgress(partialTick) * 0.2F;
		stack.scale(scale, scale, scale);

		if (entity.getVariant() == 0) {
			this.model = this.face1;
		} else {
			this.model = this.face2;
		}
		if (entity.isAnchored()) {
			stack.translate(0, 1, -0.195D);
		} else {
			stack.translate(0, 1, 0.45D);
		}
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return TEXTURE;
	}
}
