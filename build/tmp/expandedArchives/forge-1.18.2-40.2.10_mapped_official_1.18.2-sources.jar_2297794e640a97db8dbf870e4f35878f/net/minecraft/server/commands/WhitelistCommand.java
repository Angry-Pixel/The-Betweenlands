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
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;

public class WhitelistCommand {
   private static final SimpleCommandExceptionType ERROR_ALREADY_ENABLED = new SimpleCommandExceptionType(new TranslatableComponent("commands.whitelist.alreadyOn"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_DISABLED = new SimpleCommandExceptionType(new TranslatableComponent("commands.whitelist.alreadyOff"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_WHITELISTED = new SimpleCommandExceptionType(new TranslatableComponent("commands.whitelist.add.failed"));
   private static final SimpleCommandExceptionType ERROR_NOT_WHITELISTED = new SimpleCommandExceptionType(new TranslatableComponent("commands.whitelist.remove.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> p_139202_) {
      p_139202_.register(Commands.literal("whitelist").requires((p_139234_) -> {
         return p_139234_.hasPermission(3);
      }).then(Commands.literal("on").executes((p_139236_) -> {
         return enableWhitelist(p_139236_.getSource());
      })).then(Commands.literal("off").executes((p_139232_) -> {
         return disableWhitelist(p_139232_.getSource());
      })).then(Commands.literal("list").executes((p_139228_) -> {
         return showList(p_139228_.getSource());
      })).then(Commands.literal("add").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_139216_, p_139217_) -> {
         PlayerList playerlist = p_139216_.getSource().getServer().getPlayerList();
         return SharedSuggestionProvider.suggest(playerlist.getPlayers().stream().filter((p_142794_) -> {
            return !playerlist.getWhiteList().isWhiteListed(p_142794_.getGameProfile());
         }).map((p_142791_) -> {
            return p_142791_.getGameProfile().getName();
         }), p_139217_);
      }).executes((p_139224_) -> {
         return addPlayers(p_139224_.getSource(), GameProfileArgument.getGameProfiles(p_139224_, "targets"));
      }))).then(Commands.literal("remove").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((p_139206_, p_139207_) -> {
         return SharedSuggestionProvider.suggest(p_139206_.getSource().getServer().getPlayerList().getWhiteListNames(), p_139207_);
      }).executes((p_139214_) -> {
         return removePlayers(p_139214_.getSource(), GameProfileArgument.getGameProfiles(p_139214_, "targets"));
      }))).then(Commands.literal("reload").executes((p_139204_) -> {
         return reload(p_139204_.getSource());
      })));
   }

   private static int reload(CommandSourceStack p_139209_) {
      p_139209_.getServer().getPlayerList().reloadWhiteList();
      p_139209_.sendSuccess(new TranslatableComponent("commands.whitelist.reloaded"), true);
      p_139209_.getServer().kickUnlistedPlayers(p_139209_);
      return 1;
   }

   private static int addPlayers(CommandSourceStack p_139211_, Collection<GameProfile> p_139212_) throws CommandSyntaxException {
      UserWhiteList userwhitelist = p_139211_.getServer().getPlayerList().getWhiteList();
      int i = 0;

      for(GameProfile gameprofile : p_139212_) {
         if (!userwhitelist.isWhiteListed(gameprofile)) {
            UserWhiteListEntry userwhitelistentry = new UserWhiteListEntry(gameprofile);
            userwhitelist.add(userwhitelistentry);
            p_139211_.sendSuccess(new TranslatableComponent("commands.whitelist.add.success", ComponentUtils.getDisplayName(gameprofile)), true);
            ++i;
         }
      }

      if (i == 0) {
         throw ERROR_ALREADY_WHITELISTED.create();
      } else {
         return i;
      }
   }

   private static int removePlayers(CommandSourceStack p_139221_, Collection<GameProfile> p_139222_) throws CommandSyntaxException {
      UserWhiteList userwhitelist = p_139221_.getServer().getPlayerList().getWhiteList();
      int i = 0;

      for(GameProfile gameprofile : p_139222_) {
         if (userwhitelist.isWhiteListed(gameprofile)) {
            UserWhiteListEntry userwhitelistentry = new UserWhiteListEntry(gameprofile);
            userwhitelist.remove(userwhitelistentry);
            p_139221_.sendSuccess(new TranslatableComponent("commands.whitelist.remove.success", ComponentUtils.getDisplayName(gameprofile)), true);
            ++i;
         }
      }

      if (i == 0) {
         throw ERROR_NOT_WHITELISTED.create();
      } else {
         p_139221_.getServer().kickUnlistedPlayers(p_139221_);
         return i;
      }
   }

   private static int enableWhitelist(CommandSourceStack p_139219_) throws CommandSyntaxException {
      PlayerList playerlist = p_139219_.getServer().getPlayerList();
      if (playerlist.isUsingWhitelist()) {
         throw ERROR_ALREADY_ENABLED.create();
      } else {
         playerlist.setUsingWhiteList(true);
         p_139219_.sendSuccess(new TranslatableComponent("commands.whitelist.enabled"), true);
         p_139219_.getServer().kickUnlistedPlayers(p_139219_);
         return 1;
      }
   }

   private static int disableWhitelist(CommandSourceStack p_139226_) throws CommandSyntaxException {
      PlayerList playerlist = p_139226_.getServer().getPlayerList();
      if (!playerlist.isUsingWhitelist()) {
         throw ERROR_ALREADY_DISABLED.create();
      } else {
         playerlist.setUsingWhiteList(false);
         p_139226_.sendSuccess(new TranslatableComponent("commands.whitelist.disabled"), true);
         return 1;
      }
   }

   private static int showList(CommandSourceStack p_139230_) {
      String[] astring = p_139230_.getServer().getPlayerList().getWhiteListNames();
      if (astring.length == 0) {
         p_139230_.sendSuccess(new TranslatableComponent("commands.whitelist.none"), false);
      } else {
         p_139230_.sendSuccess(new TranslatableComponent("commands.whitelist.list", astring.length, String.join(", ", astring)), false);
      }

      return astring.length;
   }
}