package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.PurifierBlock;
import thebetweenlands.common.block.entity.PurifierBlockEntity;

public class PurifierRenderer implements BlockEntityRenderer<PurifierBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/purifier.png"));
	private final ModelPart base;
	private final ModelPart fire;

	public PurifierRenderer(BlockEntityRendererProvider.Context context) {
		ModelPart root = context.bakeLayer(BLModelLayers.PURIFIER);
		this.base = root.getChild("base");
		this.fire = root.getChild("fire_plate");
	}

	@Override
	public void render(PurifierBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 1.0F, 0.5F);
		stack.mulPose(Axis.XP.rotationDegrees(180.0F));
		stack.translate(0.0F, 1.0F, 0.0F);
		stack.mulPose(Axis.YP.rotationDegrees(entity.getBlockState().getValue(PurifierBlock.FACING).toYRot()));
		stack.scale(-1.0F, 1.0F, 1.0F);
		stack.pushPose();
		this.base.render(stack, source.getBuffer(TEXTURE), light, overlay);
		if (entity.getBlockState().getValue(PurifierBlock.LIT)) {
			this.fire.render(stack, source.getBuffer(TEXTURE), light, overlay);
		}
		int amount = entity.waterTank.getFluidAmount();
		int capacity = entity.waterTank.getCapacity();
		float size = 1F / capacity * amount;
		if (!entity.getItem(2).isEmpty()) {
			stack.pushPose();
			stack.translate(0.5D, 0.27D, 0.5D);
			stack.mulPose(Axis.XP.rotationDegrees(180.0F));
			int items = entity.getItem(2).getCount() / 4 + 1;
			RandomSource rand = RandomSource.create(entity.getBlockPos().asLong());
			for (int i = 0; i < items; i++) {
				stack.pushPose();
				stack.translate(rand.nextFloat() / 3.0D - 1.0D / 6.0D, -0.25D, rand.nextFloat() / 3.0D - 1.0D / 6.0D);
				stack.mulPose(Axis.XP.rotationDegrees(rand.nextFloat() * 30.0F - 15.0F));
				stack.mulPose(Axis.ZP.rotationDegrees(rand.nextFloat() * 30.0F - 15.0F));
				stack.scale(0.15F, 0.15F, 0.15F);
				stack.mulPose(Axis.XN.rotationDegrees(90));
				stack.mulPose(Axis.ZP.rotationDegrees(rand.nextFloat() * 360.0F));
				Minecraft.getInstance().getItemRenderer().renderStatic(entity.getItem(2), ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
				stack.popPose();
			}
			stack.popPose();
		}

		if (amount >= 100) {
			stack.pushPose();
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.setShaderTexture(0, Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(TheBetweenlands.prefix("textures/fluids/swamp_water_still")).atlasLocation());
			stack.translate(0.0F, 0.35F + size * 0.5F, 0.0F);
			BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
			buffer.addVertex(0.1F, 0.0F, 0.1F).setUv(0.0F, 0.0F).setColor(0.2F, 0.6F, 0.4F, 1.0F);
			buffer.addVertex(0.1F, 0.0F, 0.9F).setUv(0.0F, 1.0F).setColor(0.2F, 0.6F, 0.4F, 1.0F);
			buffer.addVertex(0.9F, 0.0F, 0.9F).setUv(1.0F, 1.0F).setColor(0.2F, 0.6F, 0.4F, 1.0F);
			buffer.addVertex(0.9F, 0.0F, 0.1F).setUv(1.0F, 0.0F).setColor(0.2F, 0.6F, 0.4F, 1.0F);
			BufferUploader.drawWithShader(buffer.buildOrThrow());
			RenderSystem.disableBlend();
			stack.popPose();
		}
		stack.popPose();
		stack.popPose();
	}
}
