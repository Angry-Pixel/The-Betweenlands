package thebetweenlands.client.renderer.block;

import org.joml.Matrix4f;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import thebetweenlands.common.block.entity.WeedwoodCraftingTableBlockEntity;

public class WeedwoodCraftingTableRenderer implements BlockEntityRenderer<WeedwoodCraftingTableBlockEntity> {

	private ItemRenderer itemRenderer;
	
	public WeedwoodCraftingTableRenderer(BlockEntityRendererProvider.Context context) {
		itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(WeedwoodCraftingTableBlockEntity table, float partialTicks, PoseStack poseStack, MultiBufferSource source, int light, int overlay) {
		poseStack.pushPose();
		poseStack.translate(0.5D, 0.875D, 0.5D);
		poseStack.scale(0.25F, 0.25F, 0.25F);
//		poseStack.mulPose(new Matrix4f().rotate((float) (Math.PI / 2) * (-table.rotation + 3), 0.0F, 1.0F, 0.0F));
		poseStack.mulPose(Axis.YP.rotationDegrees(90.0F * (-table.rotation + 3)));
		poseStack.translate(-1.5F, -0.0F, -1.0F);
		RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
		
		final Level level = table.getLevel();
		final BlockPos pos = table.getBlockPos().above();
		final int lightValue = LightTexture.pack(level.getBrightness(LightLayer.BLOCK, pos), level.getBrightness(LightLayer.SKY, pos));
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 3; column++) {
				ItemStack stack = table.items.get(column * 3 + row);
				if (!stack.isEmpty()) {
					poseStack.pushPose();
					poseStack.translate(row * 0.75F, 0.0D, column * 0.75F);
					poseStack.translate(0.75F, 0.52F, 0.25F);
					poseStack.scale(0.5F, 0.5F, 0.5F);
//					poseStack.mulPose(new Matrix4f().rotate((float) (-Math.PI / 2), 1.0F, 0.0F, 0.0F));
					poseStack.mulPose(Axis.XN.rotationDegrees(90.0F));
					itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, lightValue, overlay, poseStack, source, table.getLevel(), 0);
					poseStack.popPose();
				}
			}
		}
		
		poseStack.popPose();
	}
}