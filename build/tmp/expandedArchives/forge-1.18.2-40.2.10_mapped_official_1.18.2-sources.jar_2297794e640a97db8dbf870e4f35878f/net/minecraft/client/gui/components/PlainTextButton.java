package net.minecraft.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlainTextButton extends Button {
   private final Font font;
   private final Component message;
   private final Component underlinedMessage;

   public PlainTextButton(int p_211755_, int p_211756_, int p_211757_, int p_211758_, Component p_211759_, Button.OnPress p_211760_, Font p_211761_) {
      super(p_211755_, p_211756_, p_211757_, p_211758_, p_211759_, p_211760_);
      this.font = p_211761_;
      this.message = p_211759_;
      this.underlinedMessage = ComponentUtils.mergeStyles(p_211759_.copy(), Style.EMPTY.withUnderlined(true));
   }

   public void renderButton(PoseStack p_211763_, int p_211764_, int p_211765_, float p_211766_) {
      Component component = this.isHoveredOrFocused() ? this.underlinedMessage : this.message;
      drawString(p_211763_, this.font, component, this.x, this.y, 16777215 | Mth.ceil(this.alpha * 255.0F) << 24);
   }
}