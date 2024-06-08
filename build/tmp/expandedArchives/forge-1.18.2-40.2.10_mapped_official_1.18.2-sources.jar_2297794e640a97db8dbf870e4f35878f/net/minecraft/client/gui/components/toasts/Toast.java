package net.minecraft.client.gui.components.toasts;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface Toast {
   ResourceLocation TEXTURE = new ResourceLocation("textures/gui/toasts.png");
   Object NO_TOKEN = new Object();

   Toast.Visibility render(PoseStack p_94896_, ToastComponent p_94897_, long p_94898_);

   default Object getToken() {
      return NO_TOKEN;
   }

   default int width() {
      return 160;
   }

   default int height() {
      return 32;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Visibility {
      SHOW(SoundEvents.UI_TOAST_IN),
      HIDE(SoundEvents.UI_TOAST_OUT);

      private final SoundEvent soundEvent;

      private Visibility(SoundEvent p_94908_) {
         this.soundEvent = p_94908_;
      }

      public void playSound(SoundManager p_94910_) {
         p_94910_.play(SimpleSoundInstance.forUI(this.soundEvent, 1.0F, 1.0F));
      }
   }
}