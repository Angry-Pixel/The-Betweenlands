package net.minecraft.commands.arguments;

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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.scores.Score;

public class OperationArgument implements ArgumentType<OperationArgument.Operation> {
   private static final Collection<String> EXAMPLES = Arrays.asList("=", ">", "<");
   private static final SimpleCommandExceptionType ERROR_INVALID_OPERATION = new SimpleCommandExceptionType(new TranslatableComponent("arguments.operation.invalid"));
   private static final SimpleCommandExceptionType ERROR_DIVIDE_BY_ZERO = new SimpleCommandExceptionType(new TranslatableComponent("arguments.operation.div0"));

   public static OperationArgument operation() {
      return new OperationArgument();
   }

   public static OperationArgument.Operation getOperation(CommandContext<CommandSourceStack> p_103276_, String p_103277_) {
      return p_103276_.getArgument(p_103277_, OperationArgument.Operation.class);
   }

   public OperationArgument.Operation parse(StringReader p_103274_) throws CommandSyntaxException {
      if (!p_103274_.canRead()) {
         throw ERROR_INVALID_OPERATION.create();
      } else {
         int i = p_103274_.getCursor();

         while(p_103274_.canRead() && p_103274_.peek() != ' ') {
            p_103274_.skip();
         }

         return getOperation(p_103274_.getString().substring(i, p_103274_.getCursor()));
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_103302_, SuggestionsBuilder p_103303_) {
      return SharedSuggestionProvider.suggest(new String[]{"=", "+=", "-=", "*=", "/=", "%=", "<", ">", "><"}, p_103303_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   private static OperationArgument.Operation getOperation(String p_103282_) throws CommandSyntaxException {
      return (p_103282_.equals("><") ? (p_103279_, p_103280_) -> {
         int i = p_103279_.getScore();
         p_103279_.setScore(p_103280_.getScore());
         p_103280_.setScore(i);
      } : getSimpleOperation(p_103282_));
   }

   private static OperationArgument.SimpleOperation getSimpleOperation(String p_103287_) throws CommandSyntaxException {
      switch(p_103287_) {
      case "=":
         return (p_103298_, p_103299_) -> {
            return p_103299_;
         };
      case "+=":
         return (p_103295_, p_103296_) -> {
            return p_103295_ + p_103296_;
         };
      case "-=":
         return (p_103292_, p_103293_) -> {
            return p_103292_ - p_103293_;
         };
      case "*=":
         return (p_103289_, p_103290_) -> {
            return p_103289_ * p_103290_;
         };
      case "/=":
         return (p_103284_, p_103285_) -> {
            if (p_103285_ == 0) {
               throw ERROR_DIVIDE_BY_ZERO.create();
            } else {
               return Mth.intFloorDiv(p_103284_, p_103285_);
            }
         };
      case "%=":
         return (p_103271_, p_103272_) -> {
            if (p_103272_ == 0) {
               throw ERROR_DIVIDE_BY_ZERO.create();
            } else {
               return Mth.positiveModulo(p_103271_, p_103272_);
            }
         };
      case "<":
         return Math::min;
      case ">":
         return Math::max;
      default:
         throw ERROR_INVALID_OPERATION.create();
      }
   }

   @FunctionalInterface
   public interface Operation {
      void apply(Score p_103306_, Score p_103307_) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface SimpleOperation extends OperationArgument.Operation {
      int apply(int p_103309_, int p_103310_) throws CommandSyntaxException;

      default void apply(Score p_103312_, Score p_103313_) throws CommandSyntaxException {
         p_103312_.setScore(this.apply(p_103312_.getScore(), p_103313_.getScore()));
      }
   }
}