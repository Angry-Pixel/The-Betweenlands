package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GenericDirtMessageScreen extends Screen {
   public GenericDirtMessageScreen(Component p_96061_) {
      super(p_96061_);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void render(PoseStack p_96063_, int p_96064_, int p_96065_, float p_96066_) {
      this.renderDirtBackground(0);
      drawCenteredString(p_96063_, this.font, this.title, this.width / 2, 70, 16777215);
      super.render(p_96063_, p_96064_, p_96065_, p_96066_);
   }
}