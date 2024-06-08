package net.minecraft.client.tutorial;

import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PunchTreeTutorialStepInstance implements TutorialStepInstance {
   private static final int HINT_DELAY = 600;
   private static final Component TITLE = new TranslatableComponent("tutorial.punch_tree.title");
   private static final Component DESCRIPTION = new TranslatableComponent("tutorial.punch_tree.description", Tutorial.key("attack"));
   private final Tutorial tutorial;
   private TutorialToast toast;
   private int timeWaiting;
   private int resetCount;

   public PunchTreeTutorialStepInstance(Tutorial p_120549_) {
      this.tutorial = p_120549_;
   }

   public void tick() {
      ++this.timeWaiting;
      if (!this.tutorial.isSurvival()) {
         this.tutorial.setStep(TutorialSteps.NONE);
      } else {
         if (this.timeWaiting == 1) {
            LocalPlayer localplayer = this.tutorial.getMinecraft().player;
            if (localplayer != null) {
               if (localplayer.getInventory().contains(ItemTags.LOGS)) {
                  this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                  return;
               }

               if (FindTreeTutorialStepInstance.hasPunchedTreesPreviously(localplayer)) {
                  this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                  return;
               }
            }
         }

         if ((this.timeWaiting >= 600 || this.resetCount > 3) && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.TREE, TITLE, DESCRIPTION, true);
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

   public void onDestroyBlock(ClientLevel p_120554_, BlockPos p_120555_, BlockState p_120556_, float p_120557_) {
      boolean flag = p_120556_.is(BlockTags.LOGS);
      if (flag && p_120557_ > 0.0F) {
         if (this.toast != null) {
            this.toast.updateProgress(p_120557_);
         }

         if (p_120557_ >= 1.0F) {
            this.tutorial.setStep(TutorialSteps.OPEN_INVENTORY);
         }
      } else if (this.toast != null) {
         this.toast.updateProgress(0.0F);
      } else if (flag) {
         ++this.resetCount;
      }

   }

   public void onGetItem(ItemStack p_120552_) {
      if (p_120552_.is(ItemTags.LOGS)) {
         this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
      }
   }
}