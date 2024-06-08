package net.minecraft.commands.arguments.coordinates;

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
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ColumnPos;

public class ColumnPosArgument implements ArgumentType<Coordinates> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "~1 ~-2", "^ ^", "^-1 ^0");
   public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos2d.incomplete"));

   public static ColumnPosArgument columnPos() {
      return new ColumnPosArgument();
   }

   public static ColumnPos getColumnPos(CommandContext<CommandSourceStack> p_118993_, String p_118994_) {
      BlockPos blockpos = p_118993_.getArgument(p_118994_, Coordinates.class).getBlockPos(p_118993_.getSource());
      return new ColumnPos(blockpos.getX(), blockpos.getZ());
   }

   public Coordinates parse(StringReader p_118991_) throws CommandSyntaxException {
      int i = p_118991_.getCursor();
      if (!p_118991_.canRead()) {
         throw ERROR_NOT_COMPLETE.createWithContext(p_118991_);
      } else {
         WorldCoordinate worldcoordinate = WorldCoordinate.parseInt(p_118991_);
         if (p_118991_.canRead() && p_118991_.peek() == ' ') {
            p_118991_.skip();
            WorldCoordinate worldcoordinate1 = WorldCoordinate.parseInt(p_118991_);
            return new WorldCoordinates(worldcoordinate, new WorldCoordinate(true, 0.0D), worldcoordinate1);
         } else {
            p_118991_.setCursor(i);
            throw ERROR_NOT_COMPLETE.createWithContext(p_118991_);
         }
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_118997_, SuggestionsBuilder p_118998_) {
      if (!(p_118997_.getSource() instanceof SharedSuggestionProvider)) {
         return Suggestions.empty();
      } else {
         String s = p_118998_.getRemaining();
         Collection<SharedSuggestionProvider.TextCoordinates> collection;
         if (!s.isEmpty() && s.charAt(0) == '^') {
            collection = Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_LOCAL);
         } else {
            collection = ((SharedSuggestionProvider)p_118997_.getSource()).getRelevantCoordinates();
         }

         return SharedSuggestionProvider.suggest2DCoordinates(s, collection, p_118998_, Commands.createValidator(this::parse));
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}