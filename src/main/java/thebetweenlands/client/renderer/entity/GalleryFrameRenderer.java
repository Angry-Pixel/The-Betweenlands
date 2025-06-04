package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import thebetweenlands.client.handler.gallery.GalleryEntry;
import thebetweenlands.client.handler.gallery.GalleryManager;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.entity.GalleryFrame;

import java.util.List;

public class GalleryFrameRenderer extends EntityRenderer<GalleryFrame> {

	public static final ResourceLocation GALLERY_FRAME_EMPTY_BACKGROUND = TheBetweenlands.prefix("textures/entity/gallery_frame_empty_background.png");

	public GalleryFrameRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(GalleryFrame entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int packedLight) {
		stack.pushPose();
		stack.mulPose(Axis.YP.rotationDegrees(180 - entityYaw));

		GalleryEntry entry = GalleryManager.INSTANCE.getEntries().get(entity.getUrl());

		float relWidth = 1;
		float relHeight = 1;

		if (entry != null) {
			int maxDim = Math.max(entry.getWidth(), entry.getHeight());
			relWidth = (1.0F - (maxDim - entry.getWidth()) / (float) maxDim);
			relHeight = (1.0F - (maxDim - entry.getHeight()) / (float) maxDim);
		}

		float width = relWidth * entity.getFrameType().getSize() / 2.0f;
		float height = relHeight * entity.getFrameType().getSize() / 2.0f;

		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(entity)));

		PoseStack.Pose pose = stack.last();

		consumer.addVertex(pose, width, -height, 0).setUv(0, 1).setColor(-1).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0, 0, 1);
		consumer.addVertex(pose, -width, -height, 0).setUv(1, 1).setColor(-1).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0, 0, 1);
		consumer.addVertex(pose, -width, height, 0).setUv(1, 0).setColor(-1).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0, 0, 1);
		consumer.addVertex(pose, width, height, 0).setUv(0, 0).setColor(-1).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0, 0, 1);

		if (entry == null && !BetweenlandsConfig.onlineGallery) {
			stack.pushPose();

			int size = entity.getFrameType().getSize();

			stack.translate(size / 2.0F - (0.05F * size), 0, -0.03F);
			stack.scale(-1.0F / 128.0F, -1.0F / 128.0F, 1.0F / 128.0F);
			stack.scale(size, size, size);

			List<FormattedCharSequence> notFoundLines = Minecraft.getInstance().font.split(Component.translatable("gui.thebetweenlands.gallery.not_found"), 120);

			int yOff = -6 * notFoundLines.size();

			for (FormattedCharSequence notFoundLine : notFoundLines) {
				Minecraft.getInstance().font.drawInBatch(notFoundLine, 0.0F, (float) yOff, 0xFFFFFFFF, false, stack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, packedLight);
				yOff += 12;
			}

			stack.popPose();
		}
		stack.popPose();
		super.render(entity, entityYaw, partialTick, stack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(GalleryFrame entity) {
		GalleryEntry entry = GalleryManager.INSTANCE.getEntries().get(entity.getUrl());
		return entry != null ? entry.loadTextureAndGetLocation(GALLERY_FRAME_EMPTY_BACKGROUND) : GALLERY_FRAME_EMPTY_BACKGROUND;
	}
}
