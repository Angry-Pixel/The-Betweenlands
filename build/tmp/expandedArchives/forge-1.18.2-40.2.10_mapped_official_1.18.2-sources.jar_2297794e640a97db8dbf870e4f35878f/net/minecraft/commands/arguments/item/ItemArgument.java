package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Registry;

public class ItemArgument implements ArgumentType<ItemInput> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "stick{foo=bar}");

   public static ItemArgument item() {
      return new ItemArgument();
   }

   public ItemInput parse(StringReader p_120962_) throws CommandSyntaxException {
      ItemParser itemparser = (new ItemParser(p_120962_, false)).parse();
      return new ItemInput(itemparser.getItem(), itemparser.getNbt());
   }

   public static <S> ItemInput getItem(CommandContext<S> p_120964_, String p_120965_) {
      return p_120964_.getArgument(p_120965_, ItemInput.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_120968_, SuggestionsBuilder p_120969_) {
      StringReader stringreader = new StringReader(p_120969_.getInput());
      stringreader.setCursor(p_120969_.getStart());
      ItemParser itemparser = new ItemParser(stringreader, false);

      try {
         itemparser.parse();
      } catch (CommandSyntaxException commandsyntaxexception) {
      }

      return itemparser.fillSuggestions(p_120969_, Registry.ITEM);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}