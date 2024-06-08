package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PageButton extends Button {
   private final boolean isForward;
   private final boolean playTurnSound;

   public PageButton(int p_99225_, int p_99226_, boolean p_99227_, Button.OnPress p_99228_, boolean p_99229_) {
      super(p_99225_, p_99226_, 23, 13, TextComponent.EMPTY, p_99228_);
      this.isForward = p_99227_;
      this.playTurnSound = p_99229_;
   }

   public void renderButton(PoseStack p_99233_, int p_99234_, int p_99235_, float p_99236_) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, BookViewScreen.BOOK_LOCATION);
      int i = 0;
      int j = 192;
      if (this.isHoveredOrFocused()) {
         i += 23;
      }

      if (!this.isForward) {
         j += 13;
      }

      this.blit(p_99233_, this.x, this.y, i, j, 23, 13);
   }

   public void playDownSound(SoundManager p_99231_) {
      if (this.playTurnSound) {
         p_99231_.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
      }

   }
}