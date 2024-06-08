package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class DimensionArgument implements ArgumentType<ResourceLocation> {
   private static final Collection<String> EXAMPLES = Stream.of(Level.OVERWORLD, Level.NETHER).map((p_88814_) -> {
      return p_88814_.location().toString();
   }).collect(Collectors.toList());
   private static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((p_88812_) -> {
      return new TranslatableComponent("argument.dimension.invalid", p_88812_);
   });

   public ResourceLocation parse(StringReader p_88807_) throws CommandSyntaxException {
      return ResourceLocation.read(p_88807_);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_88817_, SuggestionsBuilder p_88818_) {
      return p_88817_.getSource() instanceof SharedSuggestionProvider ? SharedSuggestionProvider.suggestResource(((SharedSuggestionProvider)p_88817_.getSource()).levels().stream().map(ResourceKey::location), p_88818_) : Suggestions.empty();
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static DimensionArgument dimension() {
      return new DimensionArgument();
   }

   public static ServerLevel getDimension(CommandContext<CommandSourceStack> p_88809_, String p_88810_) throws CommandSyntaxException {
      ResourceLocation resourcelocation = p_88809_.getArgument(p_88810_, ResourceLocation.class);
      ResourceKey<Level> resourcekey = ResourceKey.create(Registry.DIMENSION_REGISTRY, resourcelocation);
      ServerLevel serverlevel = p_88809_.getSource().getServer().getLevel(resourcekey);
      if (serverlevel == null) {
         throw ERROR_INVALID_VALUE.create(resourcelocation);
      } else {
         return serverlevel;
      }
   }
}