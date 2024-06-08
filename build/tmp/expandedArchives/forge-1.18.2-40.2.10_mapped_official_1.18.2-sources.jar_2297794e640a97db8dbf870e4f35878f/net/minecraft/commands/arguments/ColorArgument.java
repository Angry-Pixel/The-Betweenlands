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
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;

public class ColorArgument implements ArgumentType<ChatFormatting> {
   private static final Collection<String> EXAMPLES = Arrays.asList("red", "green");
   public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((p_85470_) -> {
      return new TranslatableComponent("argument.color.invalid", p_85470_);
   });

   private ColorArgument() {
   }

   public static ColorArgument color() {
      return new ColorArgument();
   }

   public static ChatFormatting getColor(CommandContext<CommandSourceStack> p_85467_, String p_85468_) {
      return p_85467_.getArgument(p_85468_, ChatFormatting.class);
   }

   public ChatFormatting parse(StringReader p_85465_) throws CommandSyntaxException {
      String s = p_85465_.readUnquotedString();
      ChatFormatting chatformatting = ChatFormatting.getByName(s);
      if (chatformatting != null && !chatformatting.isFormat()) {
         return chatformatting;
      } else {
         throw ERROR_INVALID_VALUE.create(s);
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_85473_, SuggestionsBuilder p_85474_) {
      return SharedSuggestionProvider.suggest(ChatFormatting.getNames(true, false), p_85474_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}