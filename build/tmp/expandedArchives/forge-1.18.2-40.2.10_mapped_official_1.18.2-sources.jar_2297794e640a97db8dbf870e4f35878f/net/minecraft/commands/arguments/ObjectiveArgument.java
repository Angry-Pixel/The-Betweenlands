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
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

public class ObjectiveArgument implements ArgumentType<String> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "*", "012");
   private static final DynamicCommandExceptionType ERROR_OBJECTIVE_NOT_FOUND = new DynamicCommandExceptionType((p_101971_) -> {
      return new TranslatableComponent("arguments.objective.notFound", p_101971_);
   });
   private static final DynamicCommandExceptionType ERROR_OBJECTIVE_READ_ONLY = new DynamicCommandExceptionType((p_101969_) -> {
      return new TranslatableComponent("arguments.objective.readonly", p_101969_);
   });

   public static ObjectiveArgument objective() {
      return new ObjectiveArgument();
   }

   public static Objective getObjective(CommandContext<CommandSourceStack> p_101961_, String p_101962_) throws CommandSyntaxException {
      String s = p_101961_.getArgument(p_101962_, String.class);
      Scoreboard scoreboard = p_101961_.getSource().getScoreboard();
      Objective objective = scoreboard.getObjective(s);
      if (objective == null) {
         throw ERROR_OBJECTIVE_NOT_FOUND.create(s);
      } else {
         return objective;
      }
   }

   public static Objective getWritableObjective(CommandContext<CommandSourceStack> p_101966_, String p_101967_) throws CommandSyntaxException {
      Objective objective = getObjective(p_101966_, p_101967_);
      if (objective.getCriteria().isReadOnly()) {
         throw ERROR_OBJECTIVE_READ_ONLY.create(objective.getName());
      } else {
         return objective;
      }
   }

   public String parse(StringReader p_101959_) throws CommandSyntaxException {
      return p_101959_.readUnquotedString();
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_101974_, SuggestionsBuilder p_101975_) {
      S s = p_101974_.getSource();
      if (s instanceof CommandSourceStack) {
         CommandSourceStack commandsourcestack = (CommandSourceStack)s;
         return SharedSuggestionProvider.suggest(commandsourcestack.getScoreboard().getObjectiveNames(), p_101975_);
      } else if (s instanceof SharedSuggestionProvider) {
         SharedSuggestionProvider sharedsuggestionprovider = (SharedSuggestionProvider)s;
         return sharedsuggestionprovider.customSuggestion(p_101974_);
      } else {
         return Suggestions.empty();
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
