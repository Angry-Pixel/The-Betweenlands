package net.minecraft.commands.arguments.item;

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
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemPredicateArgument implements ArgumentType<ItemPredicateArgument.Result> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo=bar}");
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((p_121047_) -> {
      return new TranslatableComponent("arguments.item.tag.unknown", p_121047_);
   });

   public static ItemPredicateArgument itemPredicate() {
      return new ItemPredicateArgument();
   }

   public ItemPredicateArgument.Result parse(StringReader p_121039_) throws CommandSyntaxException {
      ItemParser itemparser = (new ItemParser(p_121039_, true)).parse();
      if (itemparser.getItem() != null) {
         ItemPredicateArgument.ItemPredicate itempredicateargument$itempredicate = new ItemPredicateArgument.ItemPredicate(itemparser.getItem(), itemparser.getNbt());
         return (p_121045_) -> {
            return itempredicateargument$itempredicate;
         };
      } else {
         TagKey<Item> tagkey = itemparser.getTag();
         return (p_205684_) -> {
            if (!Registry.ITEM.isKnownTagName(tagkey)) {
               throw ERROR_UNKNOWN_TAG.create(tagkey);
            } else {
               return new ItemPredicateArgument.TagPredicate(tagkey, itemparser.getNbt());
            }
         };
      }
   }

   public static Predicate<ItemStack> getItemPredicate(CommandContext<CommandSourceStack> p_121041_, String p_121042_) throws CommandSyntaxException {
      return p_121041_.getArgument(p_121042_, ItemPredicateArgument.Result.class).create(p_121041_);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_121054_, SuggestionsBuilder p_121055_) {
      StringReader stringreader = new StringReader(p_121055_.getInput());
      stringreader.setCursor(p_121055_.getStart());
      ItemParser itemparser = new ItemParser(stringreader, true);

      try {
         itemparser.parse();
      } catch (CommandSyntaxException commandsyntaxexception) {
      }

      return itemparser.fillSuggestions(p_121055_, Registry.ITEM);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static class ItemPredicate implements Predicate<ItemStack> {
      private final Item item;
      @Nullable
      private final CompoundTag nbt;

      public ItemPredicate(Item p_121061_, @Nullable CompoundTag p_121062_) {
         this.item = p_121061_;
         this.nbt = p_121062_;
      }

      public boolean test(ItemStack p_121064_) {
         return p_121064_.is(this.item) && NbtUtils.compareNbt(this.nbt, p_121064_.getTag(), true);
      }
   }

   public interface Result {
      Predicate<ItemStack> create(CommandContext<CommandSourceStack> p_121068_) throws CommandSyntaxException;
   }

   static class TagPredicate implements Predicate<ItemStack> {
      private final TagKey<Item> tag;
      @Nullable
      private final CompoundTag nbt;

      public TagPredicate(TagKey<Item> p_205686_, @Nullable CompoundTag p_205687_) {
         this.tag = p_205686_;
         this.nbt = p_205687_;
      }

      public boolean test(ItemStack p_121075_) {
         return p_121075_.is(this.tag) && NbtUtils.compareNbt(this.nbt, p_121075_.getTag(), true);
      }
   }
}