package net.minecraft.client.tutorial;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.Input;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.KeybindComponent;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Tutorial {
   private final Minecraft minecraft;
   @Nullable
   private TutorialStepInstance instance;
   private final List<Tutorial.TimedToast> timedToasts = Lists.newArrayList();
   private final BundleTutorial bundleTutorial;

   public Tutorial(Minecraft p_175022_, Options p_175023_) {
      this.minecraft = p_175022_;
      this.bundleTutorial = new BundleTutorial(this, p_175023_);
   }

   public void onInput(Input p_120587_) {
      if (this.instance != null) {
         this.instance.onInput(p_120587_);
      }

   }

   public void onMouse(double p_120566_, double p_120567_) {
      if (this.instance != null) {
         this.instance.onMouse(p_120566_, p_120567_);
      }

   }

   public void onLookAt(@Nullable ClientLevel p_120579_, @Nullable HitResult p_120580_) {
      if (this.instance != null && p_120580_ != null && p_120579_ != null) {
         this.instance.onLookAt(p_120579_, p_120580_);
      }

   }

   public void onDestroyBlock(ClientLevel p_120582_, BlockPos p_120583_, BlockState p_120584_, float p_120585_) {
      if (this.instance != null) {
         this.instance.onDestroyBlock(p_120582_, p_120583_, p_120584_, p_120585_);
      }

   }

   public void onOpenInventory() {
      if (this.instance != null) {
         this.instance.onOpenInventory();
      }

   }

   public void onGetItem(ItemStack p_120569_) {
      if (this.instance != null) {
         this.instance.onGetItem(p_120569_);
      }

   }

   public void stop() {
      if (this.instance != null) {
         this.instance.clear();
         this.instance = null;
      }
   }

   public void start() {
      if (this.instance != null) {
         this.stop();
      }

      this.instance = this.minecraft.options.tutorialStep.create(this);
   }

   public void addTimedToast(TutorialToast p_120573_, int p_120574_) {
      this.timedToasts.add(new Tutorial.TimedToast(p_120573_, p_120574_));
      this.minecraft.getToasts().addToast(p_120573_);
   }

   public void removeTimedToast(TutorialToast p_120571_) {
      this.timedToasts.removeIf((p_120577_) -> {
         return p_120577_.toast == p_120571_;
      });
      p_120571_.hide();
   }

   public void tick() {
      this.timedToasts.removeIf(Tutorial.TimedToast::updateProgress);
      if (this.instance != null) {
         if (this.minecraft.level != null) {
            this.instance.tick();
         } else {
            this.stop();
         }
      } else if (this.minecraft.level != null) {
         this.start();
      }

   }

   public void setStep(TutorialSteps p_120589_) {
      this.minecraft.options.tutorialStep = p_120589_;
      this.minecraft.options.save();
      if (this.instance != null) {
         this.instance.clear();
         this.instance = p_120589_.create(this);
      }

   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   public boolean isSurvival() {
      if (this.minecraft.gameMode == null) {
         return false;
      } else {
         return this.minecraft.gameMode.getPlayerMode() == GameType.SURVIVAL;
      }
   }

   public static Component key(String p_120593_) {
      return (new KeybindComponent("key." + p_120593_)).withStyle(ChatFormatting.BOLD);
   }

   public void onInventoryAction(ItemStack p_175025_, ItemStack p_175026_, ClickAction p_175027_) {
      this.bundleTutorial.onInventoryAction(p_175025_, p_175026_, p_175027_);
   }

   @OnlyIn(Dist.CLIENT)
   static final class TimedToast {
      final TutorialToast toast;
      private final int durationTicks;
      private int progress;

      TimedToast(TutorialToast p_120603_, int p_120604_) {
         this.toast = p_120603_;
         this.durationTicks = p_120604_;
      }

      private boolean updateProgress() {
         this.toast.updateProgress(Math.min((float)(++this.progress) / (float)this.durationTicks, 1.0F));
         if (this.progress > this.durationTicks) {
            this.toast.hide();
            return true;
         } else {
            return false;
         }
      }
   }
}