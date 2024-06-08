package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsConfirmScreen extends RealmsScreen {
   protected BooleanConsumer callback;
   private final Component title1;
   private final Component title2;

   public RealmsConfirmScreen(BooleanConsumer p_88550_, Component p_88551_, Component p_88552_) {
      super(NarratorChatListener.NO_TITLE);
      this.callback = p_88550_;
      this.title1 = p_88551_;
      this.title2 = p_88552_;
   }

   public void init() {
      this.addRenderableWidget(new Button(this.width / 2 - 105, row(9), 100, 20, CommonComponents.GUI_YES, (p_88562_) -> {
         this.callback.accept(true);
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 5, row(9), 100, 20, CommonComponents.GUI_NO, (p_88559_) -> {
         this.callback.accept(false);
      }));
   }

   public void render(PoseStack p_88554_, int p_88555_, int p_88556_, float p_88557_) {
      this.renderBackground(p_88554_);
      drawCenteredString(p_88554_, this.font, this.title1, this.width / 2, row(3), 16777215);
      drawCenteredString(p_88554_, this.font, this.title2, this.width / 2, row(5), 16777215);
      super.render(p_88554_, p_88555_, p_88556_, p_88557_);
   }
}