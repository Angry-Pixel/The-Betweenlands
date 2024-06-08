package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class TeamArgument implements ArgumentType<String> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "123");
   private static final DynamicCommandExceptionType ERROR_TEAM_NOT_FOUND = new DynamicCommandExceptionType((p_112095_) -> {
      return new TranslatableComponent("team.notFound", p_112095_);
   });

   public static TeamArgument team() {
      return new TeamArgument();
   }

   public static PlayerTeam getTeam(CommandContext<CommandSourceStack> p_112092_, String p_112093_) throws CommandSyntaxException {
      String s = p_112092_.getArgument(p_112093_, String.class);
      Scoreboard scoreboard = p_112092_.getSource().getScoreboard();
      PlayerTeam playerteam = scoreboard.getPlayerTeam(s);
      if (playerteam == null) {
         throw ERROR_TEAM_NOT_FOUND.create(s);
      } else {
         return playerteam;
      }
   }

   public String parse(StringReader p_112090_) throws CommandSyntaxException {
      return p_112090_.readUnquotedString();
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_112098_, SuggestionsBuilder p_112099_) {
      return p_112098_.getSource() instanceof SharedSuggestionProvider ? SharedSuggestionProvider.suggest(((SharedSuggestionProvider)p_112098_.getSource()).getAllTeams(), p_112099_) : Suggestions.empty();
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
