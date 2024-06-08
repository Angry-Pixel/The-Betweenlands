package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LockIconButton extends Button {
   private boolean locked;

   public LockIconButton(int p_94299_, int p_94300_, Button.OnPress p_94301_) {
      super(p_94299_, p_94300_, 20, 20, new TranslatableComponent("narrator.button.difficulty_lock"), p_94301_);
   }

   protected MutableComponent createNarrationMessage() {
      return CommonComponents.joinForNarration(super.createNarrationMessage(), this.isLocked() ? new TranslatableComponent("narrator.button.difficulty_lock.locked") : new TranslatableComponent("narrator.button.difficulty_lock.unlocked"));
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean p_94310_) {
      this.locked = p_94310_;
   }

   public void renderButton(PoseStack p_94304_, int p_94305_, int p_94306_, float p_94307_) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, Button.WIDGETS_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      LockIconButton.Icon lockiconbutton$icon;
      if (!this.active) {
         lockiconbutton$icon = this.locked ? LockIconButton.Icon.LOCKED_DISABLED : LockIconButton.Icon.UNLOCKED_DISABLED;
      } else if (this.isHoveredOrFocused()) {
         lockiconbutton$icon = this.locked ? LockIconButton.Icon.LOCKED_HOVER : LockIconButton.Icon.UNLOCKED_HOVER;
      } else {
         lockiconbutton$icon = this.locked ? LockIconButton.Icon.LOCKED : LockIconButton.Icon.UNLOCKED;
      }

      this.blit(p_94304_, this.x, this.y, lockiconbutton$icon.getX(), lockiconbutton$icon.getY(), this.width, this.height);
   }

   @OnlyIn(Dist.CLIENT)
   static enum Icon {
      LOCKED(0, 146),
      LOCKED_HOVER(0, 166),
      LOCKED_DISABLED(0, 186),
      UNLOCKED(20, 146),
      UNLOCKED_HOVER(20, 166),
      UNLOCKED_DISABLED(20, 186);

      private final int x;
      private final int y;

      private Icon(int p_94324_, int p_94325_) {
         this.x = p_94324_;
         this.y = p_94325_;
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }
   }
}