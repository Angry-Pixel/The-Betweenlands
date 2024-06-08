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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.phys.Vec3;

public class Vec3Argument implements ArgumentType<Coordinates> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5");
   public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos3d.incomplete"));
   public static final SimpleCommandExceptionType ERROR_MIXED_TYPE = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos.mixed"));
   private final boolean centerCorrect;

   public Vec3Argument(boolean p_120840_) {
      this.centerCorrect = p_120840_;
   }

   public static Vec3Argument vec3() {
      return new Vec3Argument(true);
   }

   public static Vec3Argument vec3(boolean p_120848_) {
      return new Vec3Argument(p_120848_);
   }

   public static Vec3 getVec3(CommandContext<CommandSourceStack> p_120845_, String p_120846_) {
      return p_120845_.getArgument(p_120846_, Coordinates.class).getPosition(p_120845_.getSource());
   }

   public static Coordinates getCoordinates(CommandContext<CommandSourceStack> p_120850_, String p_120851_) {
      return p_120850_.getArgument(p_120851_, Coordinates.class);
   }

   public Coordinates parse(StringReader p_120843_) throws CommandSyntaxException {
      return (Coordinates)(p_120843_.canRead() && p_120843_.peek() == '^' ? LocalCoordinates.parse(p_120843_) : WorldCoordinates.parseDouble(p_120843_, this.centerCorrect));
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_120854_, SuggestionsBuilder p_120855_) {
      if (!(p_120854_.getSource() instanceof SharedSuggestionProvider)) {
         return Suggestions.empty();
      } else {
         String s = p_120855_.getRemaining();
         Collection<SharedSuggestionProvider.TextCoordinates> collection;
         if (!s.isEmpty() && s.charAt(0) == '^') {
            collection = Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_LOCAL);
         } else {
            collection = ((SharedSuggestionProvider)p_120854_.getSource()).getAbsoluteCoordinates();
         }

         return SharedSuggestionProvider.suggestCoordinates(s, collection, p_120855_, Commands.createValidator(this::parse));
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}