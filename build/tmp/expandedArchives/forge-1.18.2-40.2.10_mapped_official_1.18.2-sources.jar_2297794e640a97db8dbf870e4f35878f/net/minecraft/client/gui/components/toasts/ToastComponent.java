package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Arrays;
import java.util.Deque;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ToastComponent extends GuiComponent {
   private static final int VISIBLE_TOASTS = 5;
   final Minecraft minecraft;
   private final ToastComponent.ToastInstance<?>[] visible = new ToastComponent.ToastInstance[5];
   private final Deque<Toast> queued = Queues.newArrayDeque();

   public ToastComponent(Minecraft p_94918_) {
      this.minecraft = p_94918_;
   }

   public void render(PoseStack p_94921_) {
      if (!this.minecraft.options.hideGui) {
         for(int i = 0; i < this.visible.length; ++i) {
            ToastComponent.ToastInstance<?> toastinstance = this.visible[i];
            if (toastinstance != null && toastinstance.render(this.minecraft.getWindow().getGuiScaledWidth(), i, p_94921_)) {
               this.visible[i] = null;
            }

            if (this.visible[i] == null && !this.queued.isEmpty()) {
               this.visible[i] = new ToastComponent.ToastInstance<>(this.queued.removeFirst());
            }
         }

      }
   }

   @Nullable
   public <T extends Toast> T getToast(Class<? extends T> p_94927_, Object p_94928_) {
      for(ToastComponent.ToastInstance<?> toastinstance : this.visible) {
         if (toastinstance != null && p_94927_.isAssignableFrom(toastinstance.getToast().getClass()) && toastinstance.getToast().getToken().equals(p_94928_)) {
            return (T)toastinstance.getToast();
         }
      }

      for(Toast toast : this.queued) {
         if (p_94927_.isAssignableFrom(toast.getClass()) && toast.getToken().equals(p_94928_)) {
            return (T)toast;
         }
      }

      return (T)null;
   }

   public void clear() {
      Arrays.fill(this.visible, (Object)null);
      this.queued.clear();
   }

   public void addToast(Toast p_94923_) {
      this.queued.add(p_94923_);
   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   @OnlyIn(Dist.CLIENT)
   class ToastInstance<T extends Toast> {
      private static final long ANIMATION_TIME = 600L;
      private final T toast;
      private long animationTime = -1L;
      private long visibleTime = -1L;
      private Toast.Visibility visibility = Toast.Visibility.SHOW;

      ToastInstance(T p_94937_) {
         this.toast = p_94937_;
      }

      public T getToast() {
         return this.toast;
      }

      private float getVisibility(long p_94948_) {
         float f = Mth.clamp((float)(p_94948_ - this.animationTime) / 600.0F, 0.0F, 1.0F);
         f *= f;
         return this.visibility == Toast.Visibility.HIDE ? 1.0F - f : f;
      }

      public boolean render(int p_94944_, int p_94945_, PoseStack p_94946_) {
         long i = Util.getMillis();
         if (this.animationTime == -1L) {
            this.animationTime = i;
            this.visibility.playSound(ToastComponent.this.minecraft.getSoundManager());
         }

         if (this.visibility == Toast.Visibility.SHOW && i - this.animationTime <= 600L) {
            this.visibleTime = i;
         }

         PoseStack posestack = RenderSystem.getModelViewStack();
         posestack.pushPose();
         posestack.translate((double)((float)p_94944_ - (float)this.toast.width() * this.getVisibility(i)), (double)(p_94945_ * this.toast.height()), (double)(800 + p_94945_));
         RenderSystem.applyModelViewMatrix();
         Toast.Visibility toast$visibility = this.toast.render(p_94946_, ToastComponent.this, i - this.visibleTime);
         posestack.popPose();
         RenderSystem.applyModelViewMatrix();
         if (toast$visibility != this.visibility) {
            this.animationTime = i - (long)((int)((1.0F - this.getVisibility(i)) * 600.0F));
            this.visibility = toast$visibility;
            this.visibility.playSound(ToastComponent.this.minecraft.getSoundManager());
         }

         return this.visibility == Toast.Visibility.HIDE && i - this.animationTime > 600L;
      }
   }
}