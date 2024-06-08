package net.minecraft.gametest.framework;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TextComponent;

public class TestClassNameArgument implements ArgumentType<String> {
   private static final Collection<String> EXAMPLES = Arrays.asList("techtests", "mobtests");

   public String parse(StringReader p_127919_) throws CommandSyntaxException {
      String s = p_127919_.readUnquotedString();
      if (GameTestRegistry.isTestClass(s)) {
         return s;
      } else {
         Message message = new TextComponent("No such test class: " + s);
         throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
      }
   }

   public static TestClassNameArgument testClassName() {
      return new TestClassNameArgument();
   }

   public static String getTestClassName(CommandContext<CommandSourceStack> p_127921_, String p_127922_) {
      return p_127921_.getArgument(p_127922_, String.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_127925_, SuggestionsBuilder p_127926_) {
      return SharedSuggestionProvider.suggest(GameTestRegistry.getAllTestClassNames().stream(), p_127926_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}