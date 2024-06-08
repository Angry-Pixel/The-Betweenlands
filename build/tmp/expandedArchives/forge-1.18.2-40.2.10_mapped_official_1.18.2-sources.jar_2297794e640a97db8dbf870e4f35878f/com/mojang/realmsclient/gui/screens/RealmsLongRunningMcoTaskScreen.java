package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.ErrorCallback;
import com.mojang.realmsclient.util.task.LongRunningTask;
import java.time.Duration;
import javax.annotation.Nullable;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RepeatedNarrator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsLongRunningMcoTaskScreen extends RealmsScreen implements ErrorCallback {
   private static final RepeatedNarrator REPEATED_NARRATOR = new RepeatedNarrator(Duration.ofSeconds(5L));
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Screen lastScreen;
   private volatile Component title = TextComponent.EMPTY;
   @Nullable
   private volatile Component errorMessage;
   private volatile boolean aborted;
   private int animTicks;
   private final LongRunningTask task;
   private final int buttonLength = 212;
   private Button cancelOrBackButton;
   public static final String[] SYMBOLS = new String[]{"\u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583", "_ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584", "_ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585", "_ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586", "_ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587", "_ _ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588", "_ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587", "_ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586", "_ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585", "_ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584", "\u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583", "\u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _", "\u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _", "\u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _", "\u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _", "\u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _ _", "\u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _", "\u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _", "\u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _", "\u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _"};

   public RealmsLongRunningMcoTaskScreen(Screen p_88777_, LongRunningTask p_88778_) {
      super(NarratorChatListener.NO_TITLE);
      this.lastScreen = p_88777_;
      this.task = p_88778_;
      p_88778_.setScreen(this);
      Thread thread = new Thread(p_88778_, "Realms-long-running-task");
      thread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
      thread.start();
   }

   public void tick() {
      super.tick();
      REPEATED_NARRATOR.narrate(this.title);
      ++this.animTicks;
      this.task.tick();
   }

   public boolean keyPressed(int p_88781_, int p_88782_, int p_88783_) {
      if (p_88781_ == 256) {
         this.cancelOrBackButtonClicked();
         return true;
      } else {
         return super.keyPressed(p_88781_, p_88782_, p_88783_);
      }
   }

   public void init() {
      this.task.init();
      this.cancelOrBackButton = this.addRenderableWidget(new Button(this.width / 2 - 106, row(12), 212, 20, CommonComponents.GUI_CANCEL, (p_88795_) -> {
         this.cancelOrBackButtonClicked();
      }));
   }

   private void cancelOrBackButtonClicked() {
      this.aborted = true;
      this.task.abortTask();
      this.minecraft.setScreen(this.lastScreen);
   }

   public void render(PoseStack p_88785_, int p_88786_, int p_88787_, float p_88788_) {
      this.renderBackground(p_88785_);
      drawCenteredString(p_88785_, this.font, this.title, this.width / 2, row(3), 16777215);
      Component component = this.errorMessage;
      if (component == null) {
         drawCenteredString(p_88785_, this.font, SYMBOLS[this.animTicks % SYMBOLS.length], this.width / 2, row(8), 8421504);
      } else {
         drawCenteredString(p_88785_, this.font, component, this.width / 2, row(8), 16711680);
      }

      super.render(p_88785_, p_88786_, p_88787_, p_88788_);
   }

   public void error(Component p_88792_) {
      this.errorMessage = p_88792_;
      NarratorChatListener.INSTANCE.sayNow(p_88792_);
      this.minecraft.execute(() -> {
         this.removeWidget(this.cancelOrBackButton);
         this.cancelOrBackButton = this.addRenderableWidget(new Button(this.width / 2 - 106, this.height / 4 + 120 + 12, 200, 20, CommonComponents.GUI_BACK, (p_88790_) -> {
            this.cancelOrBackButtonClicked();
         }));
      });
   }

   public void setTitle(Component p_88797_) {
      this.title = p_88797_;
   }

   public boolean aborted() {
      return this.aborted;
   }
}