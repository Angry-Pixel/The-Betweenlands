package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ItemParser {
   public static final SimpleCommandExceptionType ERROR_NO_TAGS_ALLOWED = new SimpleCommandExceptionType(new TranslatableComponent("argument.item.tag.disallowed"));
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_ITEM = new DynamicCommandExceptionType((p_121013_) -> {
      return new TranslatableComponent("argument.item.id.invalid", p_121013_);
   });
   private static final char SYNTAX_START_NBT = '{';
   private static final char SYNTAX_TAG = '#';
   private static final BiFunction<SuggestionsBuilder, Registry<Item>, CompletableFuture<Suggestions>> SUGGEST_NOTHING = (p_205679_, p_205680_) -> {
      return p_205679_.buildFuture();
   };
   private final StringReader reader;
   private final boolean forTesting;
   private Item item;
   @Nullable
   private CompoundTag nbt;
   @Nullable
   private TagKey<Item> tag;
   private int tagCursor;
   private BiFunction<SuggestionsBuilder, Registry<Item>, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;

   public ItemParser(StringReader p_121004_, boolean p_121005_) {
      this.reader = p_121004_;
      this.forTesting = p_121005_;
   }

   public Item getItem() {
      return this.item;
   }

   @Nullable
   public CompoundTag getNbt() {
      return this.nbt;
   }

   public TagKey<Item> getTag() {
      return this.tag;
   }

   public void readItem() throws CommandSyntaxException {
      int i = this.reader.getCursor();
      ResourceLocation resourcelocation = ResourceLocation.read(this.reader);
      this.item = Registry.ITEM.getOptional(resourcelocation).orElseThrow(() -> {
         this.reader.setCursor(i);
         return ERROR_UNKNOWN_ITEM.createWithContext(this.reader, resourcelocation.toString());
      });
   }

   public void readTag() throws CommandSyntaxException {
      if (!this.forTesting) {
         throw ERROR_NO_TAGS_ALLOWED.create();
      } else {
         this.suggestions = this::suggestTag;
         this.reader.expect('#');
         this.tagCursor = this.reader.getCursor();
         this.tag = TagKey.create(Registry.ITEM_REGISTRY, ResourceLocation.read(this.reader));
      }
   }

   public void readNbt() throws CommandSyntaxException {
      this.nbt = (new TagParser(this.reader)).readStruct();
   }

   public ItemParser parse() throws CommandSyntaxException {
      this.suggestions = this::suggestItemIdOrTag;
      if (this.reader.canRead() && this.reader.peek() == '#') {
         this.readTag();
      } else {
         this.readItem();
         this.suggestions = this::suggestOpenNbt;
      }

      if (this.reader.canRead() && this.reader.peek() == '{') {
         this.suggestions = SUGGEST_NOTHING;
         this.readNbt();
      }

      return this;
   }

   private CompletableFuture<Suggestions> suggestOpenNbt(SuggestionsBuilder p_205669_, Registry<Item> p_205670_) {
      if (p_205669_.getRemaining().isEmpty()) {
         p_205669_.suggest(String.valueOf('{'));
      }

      return p_205669_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder p_205673_, Registry<Item> p_205674_) {
      return SharedSuggestionProvider.suggestResource(p_205674_.getTagNames().map(TagKey::location), p_205673_.createOffset(this.tagCursor));
   }

   private CompletableFuture<Suggestions> suggestItemIdOrTag(SuggestionsBuilder p_205676_, Registry<Item> p_205677_) {
      if (this.forTesting) {
         SharedSuggestionProvider.suggestResource(p_205677_.getTagNames().map(TagKey::location), p_205676_, String.valueOf('#'));
      }

      return SharedSuggestionProvider.suggestResource(Registry.ITEM.keySet(), p_205676_);
   }

   public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder p_205666_, Registry<Item> p_205667_) {
      return this.suggestions.apply(p_205666_.createOffset(this.reader.getCursor()), p_205667_);
   }
}