package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class GameProfileArgument implements ArgumentType<GameProfileArgument.Result> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e");
   public static final SimpleCommandExceptionType ERROR_UNKNOWN_PLAYER = new SimpleCommandExceptionType(new TranslatableComponent("argument.player.unknown"));

   public static Collection<GameProfile> getGameProfiles(CommandContext<CommandSourceStack> p_94591_, String p_94592_) throws CommandSyntaxException {
      return p_94591_.getArgument(p_94592_, GameProfileArgument.Result.class).getNames(p_94591_.getSource());
   }

   public static GameProfileArgument gameProfile() {
      return new GameProfileArgument();
   }

   public GameProfileArgument.Result parse(StringReader p_94586_) throws CommandSyntaxException {
      if (p_94586_.canRead() && p_94586_.peek() == '@') {
         EntitySelectorParser entityselectorparser = new EntitySelectorParser(p_94586_);
         EntitySelector entityselector = entityselectorparser.parse();
         if (entityselector.includesEntities()) {
            throw EntityArgument.ERROR_ONLY_PLAYERS_ALLOWED.create();
         } else {
            return new GameProfileArgument.SelectorResult(entityselector);
         }
      } else {
         int i = p_94586_.getCursor();

         while(p_94586_.canRead() && p_94586_.peek() != ' ') {
            p_94586_.skip();
         }

         String s = p_94586_.getString().substring(i, p_94586_.getCursor());
         return (p_94595_) -> {
            Optional<GameProfile> optional = p_94595_.getServer().getProfileCache().get(s);
            return Collections.singleton(optional.orElseThrow(ERROR_UNKNOWN_PLAYER::create));
         };
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_94598_, SuggestionsBuilder p_94599_) {
      if (p_94598_.getSource() instanceof SharedSuggestionProvider) {
         StringReader stringreader = new StringReader(p_94599_.getInput());
         stringreader.setCursor(p_94599_.getStart());
         EntitySelectorParser entityselectorparser = new EntitySelectorParser(stringreader);

         try {
            entityselectorparser.parse();
         } catch (CommandSyntaxException commandsyntaxexception) {
         }

         return entityselectorparser.fillSuggestions(p_94599_, (p_94589_) -> {
            SharedSuggestionProvider.suggest(((SharedSuggestionProvider)p_94598_.getSource()).getOnlinePlayerNames(), p_94589_);
         });
      } else {
         return Suggestions.empty();
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   @FunctionalInterface
   public interface Result {
      Collection<GameProfile> getNames(CommandSourceStack p_94602_) throws CommandSyntaxException;
   }

   public static class SelectorResult implements GameProfileArgument.Result {
      private final EntitySelector selector;

      public SelectorResult(EntitySelector p_94605_) {
         this.selector = p_94605_;
      }

      public Collection<GameProfile> getNames(CommandSourceStack p_94607_) throws CommandSyntaxException {
         List<ServerPlayer> list = this.selector.findPlayers(p_94607_);
         if (list.isEmpty()) {
            throw EntityArgument.NO_PLAYERS_FOUND.create();
         } else {
            List<GameProfile> list1 = Lists.newArrayList();

            for(ServerPlayer serverplayer : list) {
               list1.add(serverplayer.getGameProfile());
            }

            return list1;
         }
      }
   }
}