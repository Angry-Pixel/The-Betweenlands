package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerPlayer;

public class TellRawCommand {
   public static void register(CommandDispatcher<CommandSourceStack> p_139064_) {
      p_139064_.register(Commands.literal("tellraw").requires((p_139068_) -> {
         return p_139068_.hasPermission(2);
      }).then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("message", ComponentArgument.textComponent()).executes((p_139066_) -> {
         int i = 0;

         for(ServerPlayer serverplayer : EntityArgument.getPlayers(p_139066_, "targets")) {
            serverplayer.sendMessage(ComponentUtils.updateForEntity(p_139066_.getSource(), ComponentArgument.getComponent(p_139066_, "message"), serverplayer, 0), Util.NIL_UUID);
            ++i;
         }

         return i;
      }))));
   }
}