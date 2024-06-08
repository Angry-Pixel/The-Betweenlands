package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.dto.RealmsServer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsSettingsScreen extends RealmsScreen {
   private static final int COMPONENT_WIDTH = 212;
   private static final Component NAME_LABEL = new TranslatableComponent("mco.configure.world.name");
   private static final Component DESCRIPTION_LABEL = new TranslatableComponent("mco.configure.world.description");
   private final RealmsConfigureWorldScreen configureWorldScreen;
   private final RealmsServer serverData;
   private Button doneButton;
   private EditBox descEdit;
   private EditBox nameEdit;

   public RealmsSettingsScreen(RealmsConfigureWorldScreen p_89829_, RealmsServer p_89830_) {
      super(new TranslatableComponent("mco.configure.world.settings.title"));
      this.configureWorldScreen = p_89829_;
      this.serverData = p_89830_;
   }

   public void tick() {
      this.nameEdit.tick();
      this.descEdit.tick();
      this.doneButton.active = !this.nameEdit.getValue().trim().isEmpty();
   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      int i = this.width / 2 - 106;
      this.doneButton = this.addRenderableWidget(new Button(i - 2, row(12), 106, 20, new TranslatableComponent("mco.configure.world.buttons.done"), (p_89847_) -> {
         this.save();
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 2, row(12), 106, 20, CommonComponents.GUI_CANCEL, (p_89845_) -> {
         this.minecraft.setScreen(this.configureWorldScreen);
      }));
      String s = this.serverData.state == RealmsServer.State.OPEN ? "mco.configure.world.buttons.close" : "mco.configure.world.buttons.open";
      Button button = new Button(this.width / 2 - 53, row(0), 106, 20, new TranslatableComponent(s), (p_89842_) -> {
         if (this.serverData.state == RealmsServer.State.OPEN) {
            Component component = new TranslatableComponent("mco.configure.world.close.question.line1");
            Component component1 = new TranslatableComponent("mco.configure.world.close.question.line2");
            this.minecraft.setScreen(new RealmsLongConfirmationScreen((p_167510_) -> {
               if (p_167510_) {
                  this.configureWorldScreen.closeTheWorld(this);
               } else {
                  this.minecraft.setScreen(this);
               }

            }, RealmsLongConfirmationScreen.Type.Info, component, component1, true));
         } else {
            this.configureWorldScreen.openTheWorld(false, this);
         }

      });
      this.addRenderableWidget(button);
      this.nameEdit = new EditBox(this.minecraft.font, i, row(4), 212, 20, (EditBox)null, new TranslatableComponent("mco.configure.world.name"));
      this.nameEdit.setMaxLength(32);
      this.nameEdit.setValue(this.serverData.getName());
      this.addWidget(this.nameEdit);
      this.magicalSpecialHackyFocus(this.nameEdit);
      this.descEdit = new EditBox(this.minecraft.font, i, row(8), 212, 20, (EditBox)null, new TranslatableComponent("mco.configure.world.description"));
      this.descEdit.setMaxLength(32);
      this.descEdit.setValue(this.serverData.getDescription());
      this.addWidget(this.descEdit);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean keyPressed(int p_89833_, int p_89834_, int p_89835_) {
      if (p_89833_ == 256) {
         this.minecraft.setScreen(this.configureWorldScreen);
         return true;
      } else {
         return super.keyPressed(p_89833_, p_89834_, p_89835_);
      }
   }

   public void render(PoseStack p_89837_, int p_89838_, int p_89839_, float p_89840_) {
      this.renderBackground(p_89837_);
      drawCenteredString(p_89837_, this.font, this.title, this.width / 2, 17, 16777215);
      this.font.draw(p_89837_, NAME_LABEL, (float)(this.width / 2 - 106), (float)row(3), 10526880);
      this.font.draw(p_89837_, DESCRIPTION_LABEL, (float)(this.width / 2 - 106), (float)row(7), 10526880);
      this.nameEdit.render(p_89837_, p_89838_, p_89839_, p_89840_);
      this.descEdit.render(p_89837_, p_89838_, p_89839_, p_89840_);
      super.render(p_89837_, p_89838_, p_89839_, p_89840_);
   }

   public void save() {
      this.configureWorldScreen.saveSettings(this.nameEdit.getValue(), this.descEdit.getValue());
   }
}