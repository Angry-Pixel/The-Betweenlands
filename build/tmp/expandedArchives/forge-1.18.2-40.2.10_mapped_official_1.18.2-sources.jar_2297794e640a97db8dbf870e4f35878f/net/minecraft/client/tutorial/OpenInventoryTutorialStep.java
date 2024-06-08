package net.minecraft.client.tutorial;

import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OpenInventoryTutorialStep implements TutorialStepInstance {
   private static final int HINT_DELAY = 600;
   private static final Component TITLE = new TranslatableComponent("tutorial.open_inventory.title");
   private static final Component DESCRIPTION = new TranslatableComponent("tutorial.open_inventory.description", Tutorial.key("inventory"));
   private final Tutorial tutorial;
   private TutorialToast toast;
   private int timeWaiting;

   public OpenInventoryTutorialStep(Tutorial p_120537_) {
      this.tutorial = p_120537_;
   }

   public void tick() {
      ++this.timeWaiting;
      if (!this.tutorial.isSurvival()) {
         this.tutorial.setStep(TutorialSteps.NONE);
      } else {
         if (this.timeWaiting >= 600 && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.RECIPE_BOOK, TITLE, DESCRIPTION, false);
            this.tutorial.getMinecraft().getToasts().addToast(this.toast);
         }

      }
   }

   public void clear() {
      if (this.toast != null) {
         this.toast.hide();
         this.toast = null;
      }

   }

   public void onOpenInventory() {
      this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
   }
}