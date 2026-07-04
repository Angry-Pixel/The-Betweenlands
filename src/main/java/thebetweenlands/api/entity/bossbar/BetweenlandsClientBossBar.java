package thebetweenlands.api.entity.bossbar;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.TheBetweenlands;

import java.util.UUID;

public class BetweenlandsClientBossBar extends LerpingBossEvent {

	private static final ResourceLocation BOSS_BAR_TEXTURE = TheBetweenlands.prefix("textures/gui/overlay/boss_health_bar.png");
	private static final ResourceLocation MINIBOSS_BAR = TheBetweenlands.prefix("textures/gui/overlay/miniboss_health_bar.png");
	private final BetweenlandsBoss.BossType type;

	public BetweenlandsClientBossBar(UUID id, Component name, float progress, BetweenlandsBoss.BossType type) {
		super(id, name, progress, BossBarColor.RED, BossBarOverlay.PROGRESS, false, false, false);
		this.type = type;
	}

	public BetweenlandsBoss.BossType getType() {
		return this.type;
	}

	public void renderBossBar(GuiGraphics graphics, int x, int y) {
		RenderSystem.enableBlend();

		graphics.blit(BOSS_BAR_TEXTURE, x, y, 0, 0, 256, 16, 256, 32);
		int i = Mth.lerpDiscrete(this.getProgress(), 0, 241) + 15;
		if (i > 0) {
			graphics.blit(BOSS_BAR_TEXTURE, x, y, 0, 16, i, 16, 256, 32);
		}

		Component title = this.getName().copy().setStyle(TheBetweenlands.HERBLORE_FONT);
		int l = Minecraft.getInstance().font.width(title);
		int i1 = graphics.guiWidth() / 2 - l / 2;
		int j1 = y + 3;
		graphics.drawString(Minecraft.getInstance().font, title, i1, j1, 16777215, false);

		RenderSystem.disableBlend();
	}

	public void renderMiniBossBar(PoseStack stack, Vec3 offset, float size) {
		Tesselator tesselator = Tesselator.getInstance();
		float viewerYaw = Minecraft.getInstance().gameRenderer.getMainCamera().getYRot();
		float viewerPitch = Minecraft.getInstance().gameRenderer.getMainCamera().getXRot();
		boolean isThirdPersonFrontal = Minecraft.getInstance().options.getCameraType() == CameraType.THIRD_PERSON_FRONT;

		RenderSystem.enableDepthTest();
		stack.pushPose();
		stack.translate(offset.x, offset.y, offset.z);
		stack.mulPose(Axis.YP.rotationDegrees(-viewerYaw));
		stack.mulPose(Axis.XP.rotationDegrees((isThirdPersonFrontal ? -1 : 1) * viewerPitch));

		float emptyProgress = 1.0F - this.getProgress();

		//base
		renderTagQuad(stack.last(), tesselator, -size, -size, size, size, 0.0F, 0.5F, 1.0F, 0.0F);
		//progress
		renderTagQuad(stack.last(), tesselator, -size, -size, size, size - size * (emptyProgress * 2), 0.5F, 1.0F, 1.0F, 0.0F + emptyProgress);
		stack.popPose();
	}

	private static void renderTagQuad(PoseStack.Pose pose, Tesselator tesselator, float minX, float minY, float maxX, float maxY, float minU, float maxU, float minV, float maxV) {
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, MINIBOSS_BAR);
		BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		buffer.addVertex(pose, minX, minY, 0.0F).setUv(minU, minV).setColor(-1);
		buffer.addVertex(pose, minX, maxY, 0.0F).setUv(minU, maxV).setColor(-1);
		buffer.addVertex(pose, maxX, maxY, 0.0F).setUv(maxU, maxV).setColor(-1);
		buffer.addVertex(pose, maxX, minY, 0.0F).setUv(maxU, minV).setColor(-1);
		BufferUploader.drawWithShader(buffer.buildOrThrow());
	}
}
