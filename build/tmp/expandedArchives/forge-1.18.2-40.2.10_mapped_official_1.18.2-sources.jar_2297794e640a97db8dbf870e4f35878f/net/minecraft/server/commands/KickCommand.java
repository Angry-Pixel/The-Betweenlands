package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class KickCommand {
   public static void register(CommandDispatcher<CommandSourceStack> p_137796_) {
      p_137796_.register(Commands.literal("kick").requires((p_137800_) -> {
         return p_137800_.hasPermission(3);
      }).then(Commands.argument("targets", EntityArgument.players()).executes((p_137806_) -> {
         return kickPlayers(p_137806_.getSource(), EntityArgument.getPlayers(p_137806_, "targets"), new TranslatableComponent("multiplayer.disconnect.kicked"));
      }).then(Commands.argument("reason", MessageArgument.message()).executes((p_137798_) -> {
         return kickPlayers(p_137798_.getSource(), EntityArgument.getPlayers(p_137798_, "targets"), MessageArgument.getMessage(p_137798_, "reason"));
      }))));
   }

   private static int kickPlayers(CommandSourceStack p_137802_, Collection<ServerPlayer> p_137803_, Component p_137804_) {
      for(ServerPlayer serverplayer : p_137803_) {
         serverplayer.connection.disconnect(p_137804_);
         p_137802_.sendSuccess(new TranslatableComponent("commands.kick.success", serverplayer.getDisplayName(), p_137804_), true);
      }

      return p_137803_.size();
   }
}