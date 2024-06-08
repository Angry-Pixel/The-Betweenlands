package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;

public class GameModeCommand {
   public static final int PERMISSION_LEVEL = 2;

   public static void register(CommandDispatcher<CommandSourceStack> p_137730_) {
      LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder = Commands.literal("gamemode").requires((p_137736_) -> {
         return p_137736_.hasPermission(2);
      });

      for(GameType gametype : GameType.values()) {
         literalargumentbuilder.then(Commands.literal(gametype.getName()).executes((p_137743_) -> {
            return setMode(p_137743_, Collections.singleton(p_137743_.getSource().getPlayerOrException()), gametype);
         }).then(Commands.argument("target", EntityArgument.players()).executes((p_137728_) -> {
            return setMode(p_137728_, EntityArgument.getPlayers(p_137728_, "target"), gametype);
         })));
      }

      p_137730_.register(literalargumentbuilder);
   }

   private static void logGamemodeChange(CommandSourceStack p_137738_, ServerPlayer p_137739_, GameType p_137740_) {
      Component component = new TranslatableComponent("gameMode." + p_137740_.getName());
      if (p_137738_.getEntity() == p_137739_) {
         p_137738_.sendSuccess(new TranslatableComponent("commands.gamemode.success.self", component), true);
      } else {
         if (p_137738_.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
            p_137739_.sendMessage(new TranslatableComponent("gameMode.changed", component), Util.NIL_UUID);
         }

         p_137738_.sendSuccess(new TranslatableComponent("commands.gamemode.success.other", p_137739_.getDisplayName(), component), true);
      }

   }

   private static int setMode(CommandContext<CommandSourceStack> p_137732_, Collection<ServerPlayer> p_137733_, GameType p_137734_) {
      int i = 0;

      for(ServerPlayer serverplayer : p_137733_) {
         if (serverplayer.setGameMode(p_137734_)) {
            logGamemodeChange(p_137732_.getSource(), serverplayer, p_137734_);
            ++i;
         }
      }

      return i;
   }
}