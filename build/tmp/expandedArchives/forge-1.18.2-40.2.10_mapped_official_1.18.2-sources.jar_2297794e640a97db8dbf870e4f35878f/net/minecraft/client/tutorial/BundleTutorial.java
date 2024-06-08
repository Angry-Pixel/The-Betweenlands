package net.minecraft.client.tutorial;

import javax.annotation.Nullable;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BundleTutorial {
   private final Tutorial tutorial;
   private final Options options;
   @Nullable
   private TutorialToast toast;

   public BundleTutorial(Tutorial p_175003_, Options p_175004_) {
      this.tutorial = p_175003_;
      this.options = p_175004_;
   }

   private void showToast() {
      if (this.toast != null) {
         this.tutorial.removeTimedToast(this.toast);
      }

      Component component = new TranslatableComponent("tutorial.bundleInsert.title");
      Component component1 = new TranslatableComponent("tutorial.bundleInsert.description");
      this.toast = new TutorialToast(TutorialToast.Icons.RIGHT_CLICK, component, component1, true);
      this.tutorial.addTimedToast(this.toast, 160);
   }

   private void clearToast() {
      if (this.toast != null) {
         this.tutorial.removeTimedToast(this.toast);
         this.toast = null;
      }

      if (!this.options.hideBundleTutorial) {
         this.options.hideBundleTutorial = true;
         this.options.save();
      }

   }

   public void onInventoryAction(ItemStack p_175007_, ItemStack p_175008_, ClickAction p_175009_) {
      if (!this.options.hideBundleTutorial) {
         if (!p_175007_.isEmpty() && p_175008_.is(Items.BUNDLE)) {
            if (p_175009_ == ClickAction.PRIMARY) {
               this.showToast();
            } else if (p_175009_ == ClickAction.SECONDARY) {
               this.clearToast();
            }
         } else if (p_175007_.is(Items.BUNDLE) && !p_175008_.isEmpty() && p_175009_ == ClickAction.SECONDARY) {
            this.clearToast();
         }

      }
   }
}