package net.minecraft.commands.arguments.blocks;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockStateParser {
   public static final SimpleCommandExceptionType ERROR_NO_TAGS_ALLOWED = new SimpleCommandExceptionType(new TranslatableComponent("argument.block.tag.disallowed"));
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_BLOCK = new DynamicCommandExceptionType((p_116790_) -> {
      return new TranslatableComponent("argument.block.id.invalid", p_116790_);
   });
   public static final Dynamic2CommandExceptionType ERROR_UNKNOWN_PROPERTY = new Dynamic2CommandExceptionType((p_116820_, p_116821_) -> {
      return new TranslatableComponent("argument.block.property.unknown", p_116820_, p_116821_);
   });
   public static final Dynamic2CommandExceptionType ERROR_DUPLICATE_PROPERTY = new Dynamic2CommandExceptionType((p_116813_, p_116814_) -> {
      return new TranslatableComponent("argument.block.property.duplicate", p_116814_, p_116813_);
   });
   public static final Dynamic3CommandExceptionType ERROR_INVALID_VALUE = new Dynamic3CommandExceptionType((p_116795_, p_116796_, p_116797_) -> {
      return new TranslatableComponent("argument.block.property.invalid", p_116795_, p_116797_, p_116796_);
   });
   public static final Dynamic2CommandExceptionType ERROR_EXPECTED_VALUE = new Dynamic2CommandExceptionType((p_116792_, p_116793_) -> {
      return new TranslatableComponent("argument.block.property.novalue", p_116792_, p_116793_);
   });
   public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_PROPERTIES = new SimpleCommandExceptionType(new TranslatableComponent("argument.block.property.unclosed"));
   private static final char SYNTAX_START_PROPERTIES = '[';
   private static final char SYNTAX_START_NBT = '{';
   private static final char SYNTAX_END_PROPERTIES = ']';
   private static final char SYNTAX_EQUALS = '=';
   private static final char SYNTAX_PROPERTY_SEPARATOR = ',';
   private static final char SYNTAX_TAG = '#';
   private static final BiFunction<SuggestionsBuilder, Registry<Block>, CompletableFuture<Suggestions>> SUGGEST_NOTHING = (p_205646_, p_205647_) -> {
      return p_205646_.buildFuture();
   };
   private final StringReader reader;
   private final boolean forTesting;
   private final Map<Property<?>, Comparable<?>> properties = Maps.newHashMap();
   private final Map<String, String> vagueProperties = Maps.newHashMap();
   private ResourceLocation id = new ResourceLocation("");
   private StateDefinition<Block, BlockState> definition;
   private BlockState state;
   @Nullable
   private CompoundTag nbt;
   @Nullable
   private TagKey<Block> tag;
   private int tagCursor;
   private BiFunction<SuggestionsBuilder, Registry<Block>, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;

   public BlockStateParser(StringReader p_116762_, boolean p_116763_) {
      this.reader = p_116762_;
      this.forTesting = p_116763_;
   }

   public Map<Property<?>, Comparable<?>> getProperties() {
      return this.properties;
   }

   @Nullable
   public BlockState getState() {
      return this.state;
   }

   @Nullable
   public CompoundTag getNbt() {
      return this.nbt;
   }

   @Nullable
   public TagKey<Block> getTag() {
      return this.tag;
   }

   public BlockStateParser parse(boolean p_116807_) throws CommandSyntaxException {
      this.suggestions = this::suggestBlockIdOrTag;
      if (this.reader.canRead() && this.reader.peek() == '#') {
         this.readTag();
         this.suggestions = this::suggestOpenVaguePropertiesOrNbt;
         if (this.reader.canRead() && this.reader.peek() == '[') {
            this.readVagueProperties();
            this.suggestions = this::suggestOpenNbt;
         }
      } else {
         this.readBlock();
         this.suggestions = this::suggestOpenPropertiesOrNbt;
         if (this.reader.canRead() && this.reader.peek() == '[') {
            this.readProperties();
            this.suggestions = this::suggestOpenNbt;
         }
      }

      if (p_116807_ && this.reader.canRead() && this.reader.peek() == '{') {
         this.suggestions = SUGGEST_NOTHING;
         this.readNbt();
      }

      return this;
   }

   private CompletableFuture<Suggestions> suggestPropertyNameOrEnd(SuggestionsBuilder p_205612_, Registry<Block> p_205613_) {
      if (p_205612_.getRemaining().isEmpty()) {
         p_205612_.suggest(String.valueOf(']'));
      }

      return this.suggestPropertyName(p_205612_, p_205613_);
   }

   private CompletableFuture<Suggestions> suggestVaguePropertyNameOrEnd(SuggestionsBuilder p_205615_, Registry<Block> p_205616_) {
      if (p_205615_.getRemaining().isEmpty()) {
         p_205615_.suggest(String.valueOf(']'));
      }

      return this.suggestVaguePropertyName(p_205615_, p_205616_);
   }

   private CompletableFuture<Suggestions> suggestPropertyName(SuggestionsBuilder p_205619_, Registry<Block> p_205620_) {
      String s = p_205619_.getRemaining().toLowerCase(Locale.ROOT);

      for(Property<?> property : this.state.getProperties()) {
         if (!this.properties.containsKey(property) && property.getName().startsWith(s)) {
            p_205619_.suggest(property.getName() + "=");
         }
      }

      return p_205619_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestVaguePropertyName(SuggestionsBuilder p_205622_, Registry<Block> p_205623_) {
      String s = p_205622_.getRemaining().toLowerCase(Locale.ROOT);
      if (this.tag != null) {
         for(Holder<Block> holder : p_205623_.getTagOrEmpty(this.tag)) {
            for(Property<?> property : holder.value().getStateDefinition().getProperties()) {
               if (!this.vagueProperties.containsKey(property.getName()) && property.getName().startsWith(s)) {
                  p_205622_.suggest(property.getName() + "=");
               }
            }
         }
      }

      return p_205622_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOpenNbt(SuggestionsBuilder p_205625_, Registry<Block> p_205626_) {
      if (p_205625_.getRemaining().isEmpty() && this.hasBlockEntity(p_205626_)) {
         p_205625_.suggest(String.valueOf('{'));
      }

      return p_205625_.buildFuture();
   }

   private boolean hasBlockEntity(Registry<Block> p_205606_) {
      if (this.state != null) {
         return this.state.hasBlockEntity();
      } else {
         if (this.tag != null) {
            for(Holder<Block> holder : p_205606_.getTagOrEmpty(this.tag)) {
               if (holder.value().defaultBlockState().hasBlockEntity()) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private CompletableFuture<Suggestions> suggestEquals(SuggestionsBuilder p_205628_, Registry<Block> p_205629_) {
      if (p_205628_.getRemaining().isEmpty()) {
         p_205628_.suggest(String.valueOf('='));
      }

      return p_205628_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestNextPropertyOrEnd(SuggestionsBuilder p_205631_, Registry<Block> p_205632_) {
      if (p_205631_.getRemaining().isEmpty()) {
         p_205631_.suggest(String.valueOf(']'));
      }

      if (p_205631_.getRemaining().isEmpty() && this.properties.size() < this.state.getProperties().size()) {
         p_205631_.suggest(String.valueOf(','));
      }

      return p_205631_.buildFuture();
   }

   private static <T extends Comparable<T>> SuggestionsBuilder addSuggestions(SuggestionsBuilder p_116787_, Property<T> p_116788_) {
      for(T t : p_116788_.getPossibleValues()) {
         if (t instanceof Integer) {
            p_116787_.suggest((Integer)t);
         } else {
            p_116787_.suggest(p_116788_.getName(t));
         }
      }

      return p_116787_;
   }

   private CompletableFuture<Suggestions> suggestVaguePropertyValue(SuggestionsBuilder p_205598_, Registry<Block> p_205599_, String p_205600_) {
      boolean flag = false;
      if (this.tag != null) {
         for(Holder<Block> holder : p_205599_.getTagOrEmpty(this.tag)) {
            Block block = holder.value();
            Property<?> property = block.getStateDefinition().getProperty(p_205600_);
            if (property != null) {
               addSuggestions(p_205598_, property);
            }

            if (!flag) {
               for(Property<?> property1 : block.getStateDefinition().getProperties()) {
                  if (!this.vagueProperties.containsKey(property1.getName())) {
                     flag = true;
                     break;
                  }
               }
            }
         }
      }

      if (flag) {
         p_205598_.suggest(String.valueOf(','));
      }

      p_205598_.suggest(String.valueOf(']'));
      return p_205598_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOpenVaguePropertiesOrNbt(SuggestionsBuilder p_205634_, Registry<Block> p_205635_) {
      if (p_205634_.getRemaining().isEmpty() && this.tag != null) {
         boolean flag = false;
         boolean flag1 = false;

         for(Holder<Block> holder : p_205635_.getTagOrEmpty(this.tag)) {
            Block block = holder.value();
            flag |= !block.getStateDefinition().getProperties().isEmpty();
            flag1 |= block.defaultBlockState().hasBlockEntity();
            if (flag && flag1) {
               break;
            }
         }

         if (flag) {
            p_205634_.suggest(String.valueOf('['));
         }

         if (flag1) {
            p_205634_.suggest(String.valueOf('{'));
         }
      }

      return this.suggestTag(p_205634_, p_205635_);
   }

   private CompletableFuture<Suggestions> suggestOpenPropertiesOrNbt(SuggestionsBuilder p_205637_, Registry<Block> p_205638_) {
      if (p_205637_.getRemaining().isEmpty()) {
         if (!this.state.getBlock().getStateDefinition().getProperties().isEmpty()) {
            p_205637_.suggest(String.valueOf('['));
         }

         if (this.state.hasBlockEntity()) {
            p_205637_.suggest(String.valueOf('{'));
         }
      }

      return p_205637_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder p_205640_, Registry<Block> p_205641_) {
      return SharedSuggestionProvider.suggestResource(p_205641_.getTagNames().map(TagKey::location), p_205640_.createOffset(this.tagCursor).add(p_205640_));
   }

   private CompletableFuture<Suggestions> suggestBlockIdOrTag(SuggestionsBuilder p_205643_, Registry<Block> p_205644_) {
      if (this.forTesting) {
         SharedSuggestionProvider.suggestResource(p_205644_.getTagNames().map(TagKey::location), p_205643_, String.valueOf('#'));
      }

      SharedSuggestionProvider.suggestResource(p_205644_.keySet(), p_205643_);
      return p_205643_.buildFuture();
   }

   public void readBlock() throws CommandSyntaxException {
      int i = this.reader.getCursor();
      this.id = ResourceLocation.read(this.reader);
      Block block = Registry.BLOCK.getOptional(this.id).orElseThrow(() -> {
         this.reader.setCursor(i);
         return ERROR_UNKNOWN_BLOCK.createWithContext(this.reader, this.id.toString());
      });
      this.definition = block.getStateDefinition();
      this.state = block.defaultBlockState();
   }

   public void readTag() throws CommandSyntaxException {
      if (!this.forTesting) {
         throw ERROR_NO_TAGS_ALLOWED.create();
      } else {
         this.suggestions = this::suggestTag;
         this.reader.expect('#');
         this.tagCursor = this.reader.getCursor();
         this.tag = TagKey.create(Registry.BLOCK_REGISTRY, ResourceLocation.read(this.reader));
      }
   }

   public void readProperties() throws CommandSyntaxException {
      this.reader.skip();
      this.suggestions = this::suggestPropertyNameOrEnd;
      this.reader.skipWhitespace();

      while(true) {
         if (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            int i = this.reader.getCursor();
            String s = this.reader.readString();
            Property<?> property = this.definition.getProperty(s);
            if (property == null) {
               this.reader.setCursor(i);
               throw ERROR_UNKNOWN_PROPERTY.createWithContext(this.reader, this.id.toString(), s);
            }

            if (this.properties.containsKey(property)) {
               this.reader.setCursor(i);
               throw ERROR_DUPLICATE_PROPERTY.createWithContext(this.reader, this.id.toString(), s);
            }

            this.reader.skipWhitespace();
            this.suggestions = this::suggestEquals;
            if (!this.reader.canRead() || this.reader.peek() != '=') {
               throw ERROR_EXPECTED_VALUE.createWithContext(this.reader, this.id.toString(), s);
            }

            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = (p_205603_, p_205604_) -> {
               return addSuggestions(p_205603_, property).buildFuture();
            };
            int j = this.reader.getCursor();
            this.setValue(property, this.reader.readString(), j);
            this.suggestions = this::suggestNextPropertyOrEnd;
            this.reader.skipWhitespace();
            if (!this.reader.canRead()) {
               continue;
            }

            if (this.reader.peek() == ',') {
               this.reader.skip();
               this.suggestions = this::suggestPropertyName;
               continue;
            }

            if (this.reader.peek() != ']') {
               throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
            }
         }

         if (this.reader.canRead()) {
            this.reader.skip();
            return;
         }

         throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
      }
   }

   public void readVagueProperties() throws CommandSyntaxException {
      this.reader.skip();
      this.suggestions = this::suggestVaguePropertyNameOrEnd;
      int i = -1;
      this.reader.skipWhitespace();

      while(true) {
         if (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            int j = this.reader.getCursor();
            String s = this.reader.readString();
            if (this.vagueProperties.containsKey(s)) {
               this.reader.setCursor(j);
               throw ERROR_DUPLICATE_PROPERTY.createWithContext(this.reader, this.id.toString(), s);
            }

            this.reader.skipWhitespace();
            if (!this.reader.canRead() || this.reader.peek() != '=') {
               this.reader.setCursor(j);
               throw ERROR_EXPECTED_VALUE.createWithContext(this.reader, this.id.toString(), s);
            }

            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = (p_205609_, p_205610_) -> {
               return this.suggestVaguePropertyValue(p_205609_, p_205610_, s);
            };
            i = this.reader.getCursor();
            String s1 = this.reader.readString();
            this.vagueProperties.put(s, s1);
            this.reader.skipWhitespace();
            if (!this.reader.canRead()) {
               continue;
            }

            i = -1;
            if (this.reader.peek() == ',') {
               this.reader.skip();
               this.suggestions = this::suggestVaguePropertyName;
               continue;
            }

            if (this.reader.peek() != ']') {
               throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
            }
         }

         if (this.reader.canRead()) {
            this.reader.skip();
            return;
         }

         if (i >= 0) {
            this.reader.setCursor(i);
         }

         throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
      }
   }

   public void readNbt() throws CommandSyntaxException {
      this.nbt = (new TagParser(this.reader)).readStruct();
   }

   private <T extends Comparable<T>> void setValue(Property<T> p_116776_, String p_116777_, int p_116778_) throws CommandSyntaxException {
      Optional<T> optional = p_116776_.getValue(p_116777_);
      if (optional.isPresent()) {
         this.state = this.state.setValue(p_116776_, optional.get());
         this.properties.put(p_116776_, optional.get());
      } else {
         this.reader.setCursor(p_116778_);
         throw ERROR_INVALID_VALUE.createWithContext(this.reader, this.id.toString(), p_116776_.getName(), p_116777_);
      }
   }

   public static String serialize(BlockState p_116770_) {
      StringBuilder stringbuilder = new StringBuilder(Registry.BLOCK.getKey(p_116770_.getBlock()).toString());
      if (!p_116770_.getProperties().isEmpty()) {
         stringbuilder.append('[');
         boolean flag = false;

         for(Entry<Property<?>, Comparable<?>> entry : p_116770_.getValues().entrySet()) {
            if (flag) {
               stringbuilder.append(',');
            }

            appendProperty(stringbuilder, entry.getKey(), entry.getValue());
            flag = true;
         }

         stringbuilder.append(']');
      }

      return stringbuilder.toString();
   }

   private static <T extends Comparable<T>> void appendProperty(StringBuilder p_116803_, Property<T> p_116804_, Comparable<?> p_116805_) {
      p_116803_.append(p_116804_.getName());
      p_116803_.append('=');
      p_116803_.append(p_116804_.getName((T)p_116805_));
   }

   public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder p_205595_, Registry<Block> p_205596_) {
      return this.suggestions.apply(p_205595_.createOffset(this.reader.getCursor()), p_205596_);
   }

   public Map<String, String> getVagueProperties() {
      return this.vagueProperties;
   }
}