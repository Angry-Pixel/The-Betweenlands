package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class SeedCommand {
   public static void register(CommandDispatcher<CommandSourceStack> p_138590_, boolean p_138591_) {
      p_138590_.register(Commands.literal("seed").requires((p_138596_) -> {
         return !p_138591_ || p_138596_.hasPermission(2);
      }).executes((p_138593_) -> {
         long i = p_138593_.getSource().getLevel().getSeed();
         Component component = ComponentUtils.wrapInSquareBrackets((new TextComponent(String.valueOf(i))).withStyle((p_180514_) -> {
            return p_180514_.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(i))).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.copy.click"))).withInsertion(String.valueOf(i));
         }));
         p_138593_.getSource().sendSuccess(new TranslatableComponent("commands.seed.success", component), false);
         return (int)i;
      }));
   }
}