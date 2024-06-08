package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OutOfMemoryScreen extends Screen {
   public OutOfMemoryScreen() {
      super(new TextComponent("Out of memory!"));
   }

   protected void init() {
      this.addRenderableWidget(new Button(this.width / 2 - 155, this.height / 4 + 120 + 12, 150, 20, new TranslatableComponent("gui.toTitle"), (p_96304_) -> {
         this.minecraft.setScreen(new TitleScreen());
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 155 + 160, this.height / 4 + 120 + 12, 150, 20, new TranslatableComponent("menu.quit"), (p_96300_) -> {
         this.minecraft.stop();
      }));
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void render(PoseStack p_96295_, int p_96296_, int p_96297_, float p_96298_) {
      this.renderBackground(p_96295_);
      drawCenteredString(p_96295_, this.font, this.title, this.width / 2, this.height / 4 - 60 + 20, 16777215);
      drawString(p_96295_, this.font, "Minecraft has run out of memory.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 0, 10526880);
      drawString(p_96295_, this.font, "This could be caused by a bug in the game or by the", this.width / 2 - 140, this.height / 4 - 60 + 60 + 18, 10526880);
      drawString(p_96295_, this.font, "Java Virtual Machine not being allocated enough", this.width / 2 - 140, this.height / 4 - 60 + 60 + 27, 10526880);
      drawString(p_96295_, this.font, "memory.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 36, 10526880);
      drawString(p_96295_, this.font, "To prevent level corruption, the current game has quit.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 54, 10526880);
      drawString(p_96295_, this.font, "We've tried to free up enough memory to let you go back to", this.width / 2 - 140, this.height / 4 - 60 + 60 + 63, 10526880);
      drawString(p_96295_, this.font, "the main menu and back to playing, but this may not have worked.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 72, 10526880);
      drawString(p_96295_, this.font, "Please restart the game if you see this message again.", this.width / 2 - 140, this.height / 4 - 60 + 60 + 81, 10526880);
      super.render(p_96295_, p_96296_, p_96297_, p_96298_);
   }
}