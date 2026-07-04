package thebetweenlands.client.extensions.item.armor;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.joml.Matrix4f;

import javax.annotation.Nullable;

public abstract class MaskRenderer implements IClientItemExtensions {

	public abstract ResourceLocation getOverlayTexture();

	@Nullable
	public abstract ResourceLocation getSideOverlayTexture(boolean left);


	@Override
	public void renderHelmetOverlay(ItemStack stack, Player player, GuiGraphics graphics, DeltaTracker tracker) {
		int width = graphics.guiWidth();
		int height = graphics.guiHeight();

		ResourceLocation leftSide = this.getSideOverlayTexture(true);
		ResourceLocation rightSide = this.getSideOverlayTexture(false);

		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();

		if (leftSide != null && rightSide != null) {
			renderMaskPiece(graphics, this.getOverlayTexture(), width / 2.0F - height / 2.0F, width / 2.0F + height / 2.0F, 0.0F, height, 0.0F, 1.0F);

			float texWidth = (width / 2.0F - height / 2.0F) / height;

			renderMaskPiece(graphics, leftSide, 0.0F, width / 2.0F - height / 2.0F, 0, height, 1.0F - texWidth, 1.0F);
			renderMaskPiece(graphics, rightSide, width / 2.0F + height / 2.0F, width, 0, height, 0.0F, texWidth);
		} else {
			graphics.blit(this.getOverlayTexture(), 0, 0, -90, 0.0F, 0.0F, width, height, width, height);
		}
		RenderSystem.disableBlend();
		RenderSystem.depthMask(true);
		RenderSystem.enableDepthTest();
	}

	private static void renderMaskPiece(GuiGraphics graphics, ResourceLocation atlasLocation, float x1, float x2, float y1, float y2, float u0, float u1) {
		RenderSystem.setShaderTexture(0, atlasLocation);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		Matrix4f matrix4f = graphics.pose().last().pose();
		BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.addVertex(matrix4f, x1, y1, -90).setUv(u0, 0);
		bufferbuilder.addVertex(matrix4f, x1, y2, -90).setUv(u0, 1);
		bufferbuilder.addVertex(matrix4f, x2, y2, -90).setUv(u1, 1);
		bufferbuilder.addVertex(matrix4f, x2, y1, -90).setUv(u1, 0);
		BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
	}
}
