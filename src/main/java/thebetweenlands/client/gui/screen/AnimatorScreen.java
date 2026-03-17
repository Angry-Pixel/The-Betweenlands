package thebetweenlands.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.AnimatorBlockEntity;
import thebetweenlands.common.inventory.AnimatorMenu;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.RenderUtils;

public class AnimatorScreen extends AbstractContainerScreen<AnimatorMenu> {

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/gui/animator.png");
	private static final ResourceLocation PROGRESS_BAR = TheBetweenlands.prefix("container/animator/progress_bar");
	private static final ResourceLocation SMELT_PROGRESS = TheBetweenlands.prefix("container/animator/progress");
	private int updateTicks = 0;

	public AnimatorScreen(AnimatorMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		this.updateTicks++;
	}

	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {

	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.render(graphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		graphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);

		// Life crystal bar
		if (this.getMenu().getSlot(1).hasItem()) {
			int currentLife = this.getMenu().getCrystalLife();
			int maxLife = this.getMenu().getCrystalMaxLife();
			if(currentLife > 0 && maxLife > 0) {
				int renderMaxLife = Math.max(maxLife, AnimatorBlockEntity.DEFAULT_MAX_LIFE);
				long scaledCrystalLife = (long)currentLife * 128L / renderMaxLife;
				// Life power bar
				int lifeCrystalCount = (int) (42 - scaledCrystalLife / 3.0F);
				graphics.blitSprite(PROGRESS_BAR, 6, 40, 0, lifeCrystalCount, this.leftPos + 39, this.topPos + 8 + lifeCrystalCount, 6, 40 - lifeCrystalCount);

				int lifeToDrain = this.getMenu().getCrystalLifeToDrain();
				if (lifeToDrain > 0) {
					// Life crystal drain bar
					int requiredLifeCrystal = (int) ((long)lifeToDrain * 128L / renderMaxLife) / 3;
					RenderSystem.enableBlend();
					RenderSystem.setShaderColor(1.0F, 0.1F, 0.1F, 0.35F + (float) (Math.cos((this.updateTicks + partialTick) / 10.0F) + 1.0F) / 2.0F * 0.65F);
					graphics.blitSprite(PROGRESS_BAR, 6, 40, 0, lifeCrystalCount, this.leftPos + 39, this.topPos + 8 + lifeCrystalCount, 6, requiredLifeCrystal);
					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					RenderSystem.disableBlend();
				}
			}
		} else {
			graphics.pose().pushPose();
			graphics.pose().translate(this.leftPos, this.topPos, 0);
			RenderUtils.drawGhostItemAtSlot(graphics, new ItemStack(ItemRegistry.LIFE_CRYSTAL.get()), this.getMenu().getSlot(1));
			graphics.pose().popPose();
		}

		// Fuel bar
		if (this.getMenu().getSlot(2).hasItem()) {
			float fuelBurnPercentage = this.getMenu().getFuelBurnPercentage(partialTick);
			int fuelBurnProgress = Mth.floor(fuelBurnPercentage * 42);
			graphics.blitSprite(PROGRESS_BAR, 6, 40, 0, fuelBurnProgress, this.leftPos + 129, this.topPos + 8 + fuelBurnProgress, 6, 40 - fuelBurnProgress);

			float relTotalProgress = this.getMenu().getTotalBurnProgress(partialTick);

			if (relTotalProgress <= 0.66D) {
				int barWidth = (int) (relTotalProgress / 0.66D * 32);
				graphics.blitSprite(SMELT_PROGRESS, 72, 18, 0, 16, this.leftPos + 51, this.topPos + 65, barWidth, 2);
				graphics.blitSprite(SMELT_PROGRESS, 72, 18, 72 - barWidth, 16, this.leftPos + 123 - barWidth, this.topPos + 65, barWidth, 2);
			}
			if (relTotalProgress > 0.66D && relTotalProgress <= 1.0D) {
				int barHeight = (int) ((relTotalProgress - 0.66D) / 0.4D * 19);
				graphics.blitSprite(SMELT_PROGRESS, 72, 18, 0, 16 - barHeight, this.leftPos + 51, this.topPos + 65 - barHeight, 72, 2 + barHeight);
			}
		} else {
			graphics.pose().pushPose();
			graphics.pose().translate(this.leftPos, this.topPos, 0);
			RenderUtils.drawGhostItemAtSlot(graphics, new ItemStack(ItemRegistry.SULFUR.get()), this.getMenu().getSlot(2));
			graphics.pose().popPose();
		}
	}
}
