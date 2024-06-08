package net.minecraft.client.gui.components.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TutorialToast implements Toast {
   public static final int PROGRESS_BAR_WIDTH = 154;
   public static final int PROGRESS_BAR_HEIGHT = 1;
   public static final int PROGRESS_BAR_X = 3;
   public static final int PROGRESS_BAR_Y = 28;
   private final TutorialToast.Icons icon;
   private final Component title;
   @Nullable
   private final Component message;
   private Toast.Visibility visibility = Toast.Visibility.SHOW;
   private long lastProgressTime;
   private float lastProgress;
   private float progress;
   private final boolean progressable;

   public TutorialToast(TutorialToast.Icons p_94958_, Component p_94959_, @Nullable Component p_94960_, boolean p_94961_) {
      this.icon = p_94958_;
      this.title = p_94959_;
      this.message = p_94960_;
      this.progressable = p_94961_;
   }

   public Toast.Visibility render(PoseStack p_94965_, ToastComponent p_94966_, long p_94967_) {
      RenderSystem.setShaderTexture(0, TEXTURE);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      p_94966_.blit(p_94965_, 0, 0, 0, 96, this.width(), this.height());
      this.icon.render(p_94965_, p_94966_, 6, 6);
      if (this.message == null) {
         p_94966_.getMinecraft().font.draw(p_94965_, this.title, 30.0F, 12.0F, -11534256);
      } else {
         p_94966_.getMinecraft().font.draw(p_94965_, this.title, 30.0F, 7.0F, -11534256);
         p_94966_.getMinecraft().font.draw(p_94965_, this.message, 30.0F, 18.0F, -16777216);
      }

      if (this.progressable) {
         GuiComponent.fill(p_94965_, 3, 28, 157, 29, -1);
         float f = Mth.clampedLerp(this.lastProgress, this.progress, (float)(p_94967_ - this.lastProgressTime) / 100.0F);
         int i;
         if (this.progress >= this.lastProgress) {
            i = -16755456;
         } else {
            i = -11206656;
         }

         GuiComponent.fill(p_94965_, 3, 28, (int)(3.0F + 154.0F * f), 29, i);
         this.lastProgress = f;
         this.lastProgressTime = p_94967_;
      }

      return this.visibility;
   }

   public void hide() {
      this.visibility = Toast.Visibility.HIDE;
   }

   public void updateProgress(float p_94963_) {
      this.progress = p_94963_;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Icons {
      MOVEMENT_KEYS(0, 0),
      MOUSE(1, 0),
      TREE(2, 0),
      RECIPE_BOOK(0, 1),
      WOODEN_PLANKS(1, 1),
      SOCIAL_INTERACTIONS(2, 1),
      RIGHT_CLICK(3, 1);

      private final int x;
      private final int y;

      private Icons(int p_94982_, int p_94983_) {
         this.x = p_94982_;
         this.y = p_94983_;
      }

      public void render(PoseStack p_94985_, GuiComponent p_94986_, int p_94987_, int p_94988_) {
         RenderSystem.enableBlend();
         p_94986_.blit(p_94985_, p_94987_, p_94988_, 176 + this.x * 20, this.y * 20, 20, 20);
         RenderSystem.enableBlend();
      }
   }
}