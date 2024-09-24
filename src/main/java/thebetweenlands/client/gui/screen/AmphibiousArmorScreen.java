package thebetweenlands.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.joml.Matrix4f;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.inventory.AmphibiousArmorMenu;
import thebetweenlands.common.registries.DataComponentRegistry;

public class AmphibiousArmorScreen extends AbstractContainerScreen<AmphibiousArmorMenu> {

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/gui/amphibious_armor.png");
	private static final ResourceLocation PULSE = TheBetweenlands.prefix("textures/gui/amphibious_armor_pulse.png");
	private static final ResourceLocation COVER = TheBetweenlands.prefix("container/amphibious_armor/slot_cover");
	public static final ResourceLocation[] SLOT_PULSE_SPRITES = new ResourceLocation[]{
		TheBetweenlands.prefix("container/amphibious_armor/slot_1_full"),
		TheBetweenlands.prefix("container/amphibious_armor/slot_2_full"),
		TheBetweenlands.prefix("container/amphibious_armor/slot_3_full"),
		TheBetweenlands.prefix("container/amphibious_armor/slot_4_full"),
		TheBetweenlands.prefix("container/amphibious_armor/slot_5_full")
	};
	private static final int ANIMATION_FRAMES_X = 39;
	private static final int ANIMATION_FRAMES_Y = 55;

	private int pulseTimer = 0;
	private int pulseAnimationTicks = 0;

	public AmphibiousArmorScreen(AmphibiousArmorMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		this.imageHeight = 221;
		this.imageWidth = 202;
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		if (this.pulseAnimationTicks >= 0) {
			this.pulseAnimationTicks++;

			if (this.pulseAnimationTicks >= 45) {
				this.pulseAnimationTicks = -1;
			}
		} else {
			this.pulseTimer++;

			if (this.pulseTimer > 5) {
				this.pulseAnimationTicks = 0;
				this.pulseTimer = 0;
			}
		}
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.render(graphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
		graphics.drawString(this.font, this.playerInventoryTitle, 20, this.imageHeight - 94, 4210752, false);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		graphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);
		RenderSystem.enableBlend();
		this.handlePulseAnimation(graphics, partialTick);
		for (int slot = 0; slot < this.getMenu().getNumSlots(); slot++) {
			if (this.getMenu().getSlot(slot).hasItem()) {
				graphics.blitSprite(SLOT_PULSE_SPRITES[slot], i + ANIMATION_FRAMES_X, j + ANIMATION_FRAMES_Y, 124, 57);
			} else if (Screen.hasShiftDown() && this.getMenu().getArmorItem().has(DataComponentRegistry.AMPHIBIOUS_ARMOR_FILTERS)) {
				var filters = this.getMenu().getArmorItem().get(DataComponentRegistry.AMPHIBIOUS_ARMOR_FILTERS);
				if (filters.getSlots() > slot && !filters.getStackInSlot(slot).isEmpty()) {
					Slot renderSlot = this.getMenu().getSlot(slot);
					graphics.pose().pushPose();
					graphics.pose().translate(this.leftPos, this.topPos, 0);
					graphics.renderFakeItem(filters.getStackInSlot(slot), renderSlot.x, renderSlot.y);
					// draw 50% gray rectangle over the item
					graphics.pose().pushPose();
					graphics.pose().translate(0.0D, 0.0D, 200.0D);
					graphics.fill(renderSlot.x + 1, renderSlot.y + 1, renderSlot.x + 15, renderSlot.y + 15, 0x9f8b8b8b);
					RenderSystem.enableBlend();
					graphics.pose().popPose();
					graphics.pose().popPose();
				}
			}
		}
		if (this.getMenu().getNumSlots() < 4) {
			graphics.blitSprite(COVER, i + 67, j + 81, 68, 35);
		}
		RenderSystem.disableBlend();
	}

	private void handlePulseAnimation(GuiGraphics graphics, float partialTick) {
		if (this.pulseAnimationTicks >= 0) {
			float animationCounter = (this.pulseAnimationTicks + partialTick) / 5.0f;
			int currentFrame = Mth.floor(animationCounter);

			float frame1alpha;
			int frame1;

			float frame2alpha;
			int frame2;

			if (currentFrame == 0) {
				frame1 = frame2 = 0;
				frame1alpha = animationCounter;
				frame2alpha = 0;
			} else if (currentFrame >= 7) {
				frame1 = frame2 = 7;
				frame1alpha = 0;
				frame2alpha = Math.max(0, 1.0F - (animationCounter - 7));
			} else {
				frame1 = currentFrame;
				frame1alpha = 1.0F - (animationCounter - currentFrame);
				frame2 = currentFrame + 1;
				frame2alpha = 1.0F - frame1alpha;
			}
			this.renderPulse(graphics, this.leftPos + ANIMATION_FRAMES_X, this.topPos + ANIMATION_FRAMES_Y, frame1 * 124, frame1 * 57, frame1alpha);
			this.renderPulse(graphics, this.leftPos + ANIMATION_FRAMES_X, this.topPos + ANIMATION_FRAMES_Y, frame2 * 124, frame2 * 57, frame2alpha);
		}
	}

	private void renderPulse(GuiGraphics graphics, int x, int y, int uOffset, int vOffset, float alpha) {
		float minU = (uOffset + 0.0F) / 124.0F;
		float maxU = (uOffset + 124.0F) / 124.0F;
		float minV = (vOffset + 0.0F) / 456.0F;
		float maxV = (vOffset + 57.0F) / 456.0F;

		RenderSystem.setShaderTexture(0, PULSE);
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		Matrix4f matrix4f = graphics.pose().last().pose();
		BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		bufferbuilder.addVertex(matrix4f, x, y, 0).setUv(minU, minV).setColor(1.0F, 1.0F, 1.0F, alpha);
		bufferbuilder.addVertex(matrix4f, x, y + 57, 0).setUv(minU, maxV).setColor(1.0F, 1.0F, 1.0F, alpha);
		bufferbuilder.addVertex(matrix4f, x + 124, y + 57, 0).setUv(maxU, maxV).setColor(1.0F, 1.0F, 1.0F, alpha);
		bufferbuilder.addVertex(matrix4f, x + 124, y, 0).setUv(maxU, minV).setColor(1.0F, 1.0F, 1.0F, alpha);
		BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
	}
}
