package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class DefaultGameModeCommands {
   public static void register(CommandDispatcher<CommandSourceStack> p_136927_) {
      LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder = Commands.literal("defaultgamemode").requires((p_136929_) -> {
         return p_136929_.hasPermission(2);
      });

      for(GameType gametype : GameType.values()) {
         literalargumentbuilder.then(Commands.literal(gametype.getName()).executes((p_136925_) -> {
            return setMode(p_136925_.getSource(), gametype);
         }));
      }

      p_136927_.register(literalargumentbuilder);
   }

   private static int setMode(CommandSourceStack p_136931_, GameType p_136932_) {
      int i = 0;
      MinecraftServer minecraftserver = p_136931_.getServer();
      minecraftserver.setDefaultGameType(p_136932_);
      GameType gametype = minecraftserver.getForcedGameType();
      if (gametype != null) {
         for(ServerPlayer serverplayer : minecraftserver.getPlayerList().getPlayers()) {
            if (serverplayer.setGameMode(gametype)) {
               ++i;
            }
         }
      }

      p_136931_.sendSuccess(new TranslatableComponent("commands.defaultgamemode.success", p_136932_.getLongDisplayName()), true);
      return i;
   }
}