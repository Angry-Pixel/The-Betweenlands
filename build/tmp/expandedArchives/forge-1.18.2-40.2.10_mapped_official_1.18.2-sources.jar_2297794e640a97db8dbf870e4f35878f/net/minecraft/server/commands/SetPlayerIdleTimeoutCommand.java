package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;

public class SetPlayerIdleTimeoutCommand {
   public static void register(CommandDispatcher<CommandSourceStack> p_138635_) {
      p_138635_.register(Commands.literal("setidletimeout").requires((p_138639_) -> {
         return p_138639_.hasPermission(3);
      }).then(Commands.argument("minutes", IntegerArgumentType.integer(0)).executes((p_138637_) -> {
         return setIdleTimeout(p_138637_.getSource(), IntegerArgumentType.getInteger(p_138637_, "minutes"));
      })));
   }

   private static int setIdleTimeout(CommandSourceStack p_138641_, int p_138642_) {
      p_138641_.getServer().setPlayerIdleTimeout(p_138642_);
      p_138641_.sendSuccess(new TranslatableComponent("commands.setidletimeout.success", p_138642_), true);
      return p_138642_;
   }
}