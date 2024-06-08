package net.minecraft.server.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.players.UserBanList;

public class PardonCommand {
   private static final SimpleCommandExceptionType ERROR_NOT_BANNED = new SimpleCommandExceptionType(new TranslatableComponent("commands.pardon.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> p_138094_) {
      p_138094_.register(Commands.literal("pardon").requires((p_138101_) -> {
         return p_138101_.hasPermission(3);
      }).then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_138098_, p_138099_) -> {
         return SharedSuggestionProvider.suggest(p_138098_.getSource().getServer().getPlayerList().getBans().getUserList(), p_138099_);
      }).executes((p_138096_) -> {
         return pardonPlayers(p_138096_.getSource(), GameProfileArgument.getGameProfiles(p_138096_, "targets"));
      })));
   }

   private static int pardonPlayers(CommandSourceStack p_138103_, Collection<GameProfile> p_138104_) throws CommandSyntaxException {
      UserBanList userbanlist = p_138103_.getServer().getPlayerList().getBans();
      int i = 0;

      for(GameProfile gameprofile : p_138104_) {
         if (userbanlist.isBanned(gameprofile)) {
            userbanlist.remove(gameprofile);
            ++i;
            p_138103_.sendSuccess(new TranslatableComponent("commands.pardon.success", ComponentUtils.getDisplayName(gameprofile)), true);
         }
      }

      if (i == 0) {
         throw ERROR_NOT_BANNED.create();
      } else {
         return i;
      }
   }
}