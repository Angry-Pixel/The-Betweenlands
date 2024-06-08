package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsClientOutdatedScreen extends RealmsScreen {
   private static final Component OUTDATED_TITLE = new TranslatableComponent("mco.client.outdated.title");
   private static final Component[] OUTDATED_MESSAGES = new Component[]{new TranslatableComponent("mco.client.outdated.msg.line1"), new TranslatableComponent("mco.client.outdated.msg.line2")};
   private static final Component INCOMPATIBLE_TITLE = new TranslatableComponent("mco.client.incompatible.title");
   private static final Component[] INCOMPATIBLE_MESSAGES = new Component[]{new TranslatableComponent("mco.client.incompatible.msg.line1"), new TranslatableComponent("mco.client.incompatible.msg.line2"), new TranslatableComponent("mco.client.incompatible.msg.line3")};
   private final Screen lastScreen;
   private final boolean outdated;

   public RealmsClientOutdatedScreen(Screen p_88366_, boolean p_88367_) {
      super(p_88367_ ? OUTDATED_TITLE : INCOMPATIBLE_TITLE);
      this.lastScreen = p_88366_;
      this.outdated = p_88367_;
   }

   public void init() {
      this.addRenderableWidget(new Button(this.width / 2 - 100, row(12), 200, 20, CommonComponents.GUI_BACK, (p_88378_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void render(PoseStack p_88373_, int p_88374_, int p_88375_, float p_88376_) {
      this.renderBackground(p_88373_);
      drawCenteredString(p_88373_, this.font, this.title, this.width / 2, row(3), 16711680);
      Component[] acomponent = this.outdated ? INCOMPATIBLE_MESSAGES : OUTDATED_MESSAGES;

      for(int i = 0; i < acomponent.length; ++i) {
         drawCenteredString(p_88373_, this.font, acomponent[i], this.width / 2, row(5) + i * 12, 16777215);
      }

      super.render(p_88373_, p_88374_, p_88375_, p_88376_);
   }

   public boolean keyPressed(int p_88369_, int p_88370_, int p_88371_) {
      if (p_88369_ != 257 && p_88369_ != 335 && p_88369_ != 256) {
         return super.keyPressed(p_88369_, p_88370_, p_88371_);
      } else {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      }
   }
}