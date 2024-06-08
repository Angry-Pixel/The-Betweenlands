package net.minecraft.client.gui.screens;

import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChatOptionsScreen extends SimpleOptionsSubScreen {
   private static final Option[] CHAT_OPTIONS = new Option[]{Option.CHAT_VISIBILITY, Option.CHAT_COLOR, Option.CHAT_LINKS, Option.CHAT_LINKS_PROMPT, Option.CHAT_OPACITY, Option.TEXT_BACKGROUND_OPACITY, Option.CHAT_SCALE, Option.CHAT_LINE_SPACING, Option.CHAT_DELAY, Option.CHAT_WIDTH, Option.CHAT_HEIGHT_FOCUSED, Option.CHAT_HEIGHT_UNFOCUSED, Option.NARRATOR, Option.AUTO_SUGGESTIONS, Option.HIDE_MATCHED_NAMES, Option.REDUCED_DEBUG_INFO};

   public ChatOptionsScreen(Screen p_95571_, Options p_95572_) {
      super(p_95571_, p_95572_, new TranslatableComponent("options.chat.title"), CHAT_OPTIONS);
   }
}