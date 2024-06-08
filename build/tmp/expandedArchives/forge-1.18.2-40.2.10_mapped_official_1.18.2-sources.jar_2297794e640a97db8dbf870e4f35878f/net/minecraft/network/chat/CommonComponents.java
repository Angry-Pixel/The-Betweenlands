package net.minecraft.network.chat;

import java.util.Arrays;
import java.util.Collection;

public class CommonComponents {
   public static final Component OPTION_ON = new TranslatableComponent("options.on");
   public static final Component OPTION_OFF = new TranslatableComponent("options.off");
   public static final Component GUI_DONE = new TranslatableComponent("gui.done");
   public static final Component GUI_CANCEL = new TranslatableComponent("gui.cancel");
   public static final Component GUI_YES = new TranslatableComponent("gui.yes");
   public static final Component GUI_NO = new TranslatableComponent("gui.no");
   public static final Component GUI_PROCEED = new TranslatableComponent("gui.proceed");
   public static final Component GUI_BACK = new TranslatableComponent("gui.back");
   public static final Component CONNECT_FAILED = new TranslatableComponent("connect.failed");
   public static final Component NEW_LINE = new TextComponent("\n");
   public static final Component NARRATION_SEPARATOR = new TextComponent(". ");

   public static Component optionStatus(boolean p_130667_) {
      return p_130667_ ? OPTION_ON : OPTION_OFF;
   }

   public static MutableComponent optionStatus(Component p_130664_, boolean p_130665_) {
      return new TranslatableComponent(p_130665_ ? "options.on.composed" : "options.off.composed", p_130664_);
   }

   public static MutableComponent optionNameValue(Component p_178394_, Component p_178395_) {
      return new TranslatableComponent("options.generic_value", p_178394_, p_178395_);
   }

   public static MutableComponent joinForNarration(Component p_178399_, Component p_178400_) {
      return (new TextComponent("")).append(p_178399_).append(NARRATION_SEPARATOR).append(p_178400_);
   }

   public static Component joinLines(Component... p_178397_) {
      return joinLines(Arrays.asList(p_178397_));
   }

   public static Component joinLines(Collection<? extends Component> p_178392_) {
      return ComponentUtils.formatList(p_178392_, NEW_LINE);
   }
}