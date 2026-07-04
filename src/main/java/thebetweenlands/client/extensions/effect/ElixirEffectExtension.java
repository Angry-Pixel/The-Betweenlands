package thebetweenlands.client.extensions.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;

public record ElixirEffectExtension(ElixirEffect.ElixirPotionEffect potionEffect) implements IClientMobEffectExtensions {

	@Override
	public boolean isVisibleInInventory(MobEffectInstance instance) {
		return this.potionEffect().getIcon() != null;
	}

	@Override
	public boolean isVisibleInGui(MobEffectInstance instance) {
		return this.potionEffect().getIcon() != null;
	}

	@Override
	public boolean renderInventoryIcon(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics graphics, int x, int y, int blitOffset) {
		if (this.potionEffect().getIcon() != null) {
			RenderSystem.enableBlend();
			graphics.blit(this.potionEffect().getIcon(), x + 1, y + 7, 0, 0, 0, 16, 16, 16, 16);
		}
		return true;
	}

	@Override
	public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics graphics, int x, int y, int blitOffset) {
		return true;
	}

	@Override
	public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics graphics, int x, int y, float z, float alpha) {
		if (this.potionEffect().getIcon() != null) {
			graphics.blit(this.potionEffect().getIcon(), x + 4, y + 4, 0, 0, 0, 16, 16, 16, 16);
		}
		return true;
	}
}
