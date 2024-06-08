package net.minecraft.client.gui.screens.multiplayer;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Realms32bitWarningScreen extends WarningScreen {
   private static final Component TITLE = (new TranslatableComponent("title.32bit.deprecation.realms.header")).withStyle(ChatFormatting.BOLD);
   private static final Component CONTENT = new TranslatableComponent("title.32bit.deprecation.realms");
   private static final Component CHECK = new TranslatableComponent("title.32bit.deprecation.realms.check");
   private static final Component NARRATION = TITLE.copy().append("\n").append(CONTENT);

   public Realms32bitWarningScreen(Screen p_210898_) {
      super(TITLE, CONTENT, CHECK, NARRATION, p_210898_);
   }

   protected void initButtons(int p_210900_) {
      this.addRenderableWidget(new Button(this.width / 2 - 75, 100 + p_210900_, 150, 20, CommonComponents.GUI_DONE, (p_210902_) -> {
         if (this.stopShowing.selected()) {
            this.minecraft.options.skipRealms32bitWarning = true;
            this.minecraft.options.save();
         }

         this.minecraft.setScreen(this.previous);
      }));
   }
}