package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConfirmScreen extends Screen {
   private static final int LABEL_Y = 90;
   private final Component title2;
   private MultiLineLabel message = MultiLineLabel.EMPTY;
   protected Component yesButton;
   protected Component noButton;
   private int delayTicker;
   protected final BooleanConsumer callback;
   private final List<Button> exitButtons = Lists.newArrayList();

   public ConfirmScreen(BooleanConsumer p_95654_, Component p_95655_, Component p_95656_) {
      this(p_95654_, p_95655_, p_95656_, CommonComponents.GUI_YES, CommonComponents.GUI_NO);
   }

   public ConfirmScreen(BooleanConsumer p_95658_, Component p_95659_, Component p_95660_, Component p_95661_, Component p_95662_) {
      super(p_95659_);
      this.callback = p_95658_;
      this.title2 = p_95660_;
      this.yesButton = p_95661_;
      this.noButton = p_95662_;
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), this.title2);
   }

   protected void init() {
      super.init();
      this.message = MultiLineLabel.create(this.font, this.title2, this.width - 50);
      int i = this.message.getLineCount() * 9;
      int j = Mth.clamp(90 + i + 12, this.height / 6 + 96, this.height - 24);
      this.exitButtons.clear();
      this.addButtons(j);
   }

   protected void addButtons(int p_169252_) {
      this.addExitButton(new Button(this.width / 2 - 155, p_169252_, 150, 20, this.yesButton, (p_169259_) -> {
         this.callback.accept(true);
      }));
      this.addExitButton(new Button(this.width / 2 - 155 + 160, p_169252_, 150, 20, this.noButton, (p_169257_) -> {
         this.callback.accept(false);
      }));
   }

   protected void addExitButton(Button p_169254_) {
      this.exitButtons.add(this.addRenderableWidget(p_169254_));
   }

   public void render(PoseStack p_95670_, int p_95671_, int p_95672_, float p_95673_) {
      this.renderBackground(p_95670_);
      drawCenteredString(p_95670_, this.font, this.title, this.width / 2, 70, 16777215);
      this.message.renderCentered(p_95670_, this.width / 2, 90);
      super.render(p_95670_, p_95671_, p_95672_, p_95673_);
   }

   public void setDelay(int p_95664_) {
      this.delayTicker = p_95664_;

      for(Button button : this.exitButtons) {
         button.active = false;
      }

   }

   public void tick() {
      super.tick();
      if (--this.delayTicker == 0) {
         for(Button button : this.exitButtons) {
            button.active = true;
         }
      }

   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(int p_95666_, int p_95667_, int p_95668_) {
      if (p_95666_ == 256) {
         this.callback.accept(false);
         return true;
      } else {
         return super.keyPressed(p_95666_, p_95667_, p_95668_);
      }
   }
}