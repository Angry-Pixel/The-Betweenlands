package net.minecraft.realms;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsLabel implements Widget {
   private final Component text;
   private final int x;
   private final int y;
   private final int color;

   public RealmsLabel(Component p_120736_, int p_120737_, int p_120738_, int p_120739_) {
      this.text = p_120736_;
      this.x = p_120737_;
      this.y = p_120738_;
      this.color = p_120739_;
   }

   public void render(PoseStack p_175036_, int p_175037_, int p_175038_, float p_175039_) {
      GuiComponent.drawCenteredString(p_175036_, Minecraft.getInstance().font, this.text, this.x, this.y, this.color);
   }

   public Component getText() {
      return this.text;
   }
}