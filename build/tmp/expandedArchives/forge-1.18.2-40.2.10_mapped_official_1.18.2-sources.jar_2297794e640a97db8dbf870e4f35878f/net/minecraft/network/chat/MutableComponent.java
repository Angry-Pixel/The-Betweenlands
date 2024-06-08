package net.minecraft.network.chat;

import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;

public interface MutableComponent extends Component {
   MutableComponent setStyle(Style p_130943_);

   default MutableComponent append(String p_130947_) {
      return this.append(new TextComponent(p_130947_));
   }

   MutableComponent append(Component p_130942_);

   default MutableComponent withStyle(UnaryOperator<Style> p_130939_) {
      this.setStyle(p_130939_.apply(this.getStyle()));
      return this;
   }

   default MutableComponent withStyle(Style p_130949_) {
      this.setStyle(p_130949_.applyTo(this.getStyle()));
      return this;
   }

   default MutableComponent withStyle(ChatFormatting... p_130945_) {
      this.setStyle(this.getStyle().applyFormats(p_130945_));
      return this;
   }

   default MutableComponent withStyle(ChatFormatting p_130941_) {
      this.setStyle(this.getStyle().applyFormat(p_130941_));
      return this;
   }
}