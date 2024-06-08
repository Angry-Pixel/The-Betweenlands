package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.network.chat.TranslatableComponent;

public class TagParser {
   public static final SimpleCommandExceptionType ERROR_TRAILING_DATA = new SimpleCommandExceptionType(new TranslatableComponent("argument.nbt.trailing"));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_KEY = new SimpleCommandExceptionType(new TranslatableComponent("argument.nbt.expected.key"));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_VALUE = new SimpleCommandExceptionType(new TranslatableComponent("argument.nbt.expected.value"));
   public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_LIST = new Dynamic2CommandExceptionType((p_129366_, p_129367_) -> {
      return new TranslatableComponent("argument.nbt.list.mixed", p_129366_, p_129367_);
   });
   public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_ARRAY = new Dynamic2CommandExceptionType((p_129357_, p_129358_) -> {
      return new TranslatableComponent("argument.nbt.array.mixed", p_129357_, p_129358_);
   });
   public static final DynamicCommandExceptionType ERROR_INVALID_ARRAY = new DynamicCommandExceptionType((p_129355_) -> {
      return new TranslatableComponent("argument.nbt.array.invalid", p_129355_);
   });
   public static final char ELEMENT_SEPARATOR = ',';
   public static final char NAME_VALUE_SEPARATOR = ':';
   private static final char LIST_OPEN = '[';
   private static final char LIST_CLOSE = ']';
   private static final char STRUCT_CLOSE = '}';
   private static final char STRUCT_OPEN = '{';
   private static final Pattern DOUBLE_PATTERN_NOSUFFIX = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
   private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
   private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
   private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
   private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
   private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
   private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
   private final StringReader reader;

   public static CompoundTag parseTag(String p_129360_) throws CommandSyntaxException {
      return (new TagParser(new StringReader(p_129360_))).readSingleStruct();
   }

   @VisibleForTesting
   CompoundTag readSingleStruct() throws CommandSyntaxException {
      CompoundTag compoundtag = this.readStruct();
      this.reader.skipWhitespace();
      if (this.reader.canRead()) {
         throw ERROR_TRAILING_DATA.createWithContext(this.reader);
      } else {
         return compoundtag;
      }
   }

   public TagParser(StringReader p_129350_) {
      this.reader = p_129350_;
   }

   protected String readKey() throws CommandSyntaxException {
      this.reader.skipWhitespace();
      if (!this.reader.canRead()) {
         throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
      } else {
         return this.reader.readString();
      }
   }

   protected Tag readTypedValue() throws CommandSyntaxException {
      this.reader.skipWhitespace();
      int i = this.reader.getCursor();
      if (StringReader.isQuotedStringStart(this.reader.peek())) {
         return StringTag.valueOf(this.reader.readQuotedString());
      } else {
         String s = this.reader.readUnquotedString();
         if (s.isEmpty()) {
            this.reader.setCursor(i);
            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
         } else {
            return this.type(s);
         }
      }
   }

   private Tag type(String p_129369_) {
      try {
         if (FLOAT_PATTERN.matcher(p_129369_).matches()) {
            return FloatTag.valueOf(Float.parseFloat(p_129369_.substring(0, p_129369_.length() - 1)));
         }

         if (BYTE_PATTERN.matcher(p_129369_).matches()) {
            return ByteTag.valueOf(Byte.parseByte(p_129369_.substring(0, p_129369_.length() - 1)));
         }

         if (LONG_PATTERN.matcher(p_129369_).matches()) {
            return LongTag.valueOf(Long.parseLong(p_129369_.substring(0, p_129369_.length() - 1)));
         }

         if (SHORT_PATTERN.matcher(p_129369_).matches()) {
            return ShortTag.valueOf(Short.parseShort(p_129369_.substring(0, p_129369_.length() - 1)));
         }

         if (INT_PATTERN.matcher(p_129369_).matches()) {
            return IntTag.valueOf(Integer.parseInt(p_129369_));
         }

         if (DOUBLE_PATTERN.matcher(p_129369_).matches()) {
            return DoubleTag.valueOf(Double.parseDouble(p_129369_.substring(0, p_129369_.length() - 1)));
         }

         if (DOUBLE_PATTERN_NOSUFFIX.matcher(p_129369_).matches()) {
            return DoubleTag.valueOf(Double.parseDouble(p_129369_));
         }

         if ("true".equalsIgnoreCase(p_129369_)) {
            return ByteTag.ONE;
         }

         if ("false".equalsIgnoreCase(p_129369_)) {
            return ByteTag.ZERO;
         }
      } catch (NumberFormatException numberformatexception) {
      }

      return StringTag.valueOf(p_129369_);
   }

   public Tag readValue() throws CommandSyntaxException {
      this.reader.skipWhitespace();
      if (!this.reader.canRead()) {
         throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
      } else {
         char c0 = this.reader.peek();
         if (c0 == '{') {
            return this.readStruct();
         } else {
            return c0 == '[' ? this.readList() : this.readTypedValue();
         }
      }
   }

   protected Tag readList() throws CommandSyntaxException {
      return this.reader.canRead(3) && !StringReader.isQuotedStringStart(this.reader.peek(1)) && this.reader.peek(2) == ';' ? this.readArrayTag() : this.readListTag();
   }

   public CompoundTag readStruct() throws CommandSyntaxException {
      this.expect('{');
      CompoundTag compoundtag = new CompoundTag();
      this.reader.skipWhitespace();

      while(this.reader.canRead() && this.reader.peek() != '}') {
         int i = this.reader.getCursor();
         String s = this.readKey();
         if (s.isEmpty()) {
            this.reader.setCursor(i);
            throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
         }

         this.expect(':');
         compoundtag.put(s, this.readValue());
         if (!this.hasElementSeparator()) {
            break;
         }

         if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_KEY.createWithContext(this.reader);
         }
      }

      this.expect('}');
      return compoundtag;
   }

   private Tag readListTag() throws CommandSyntaxException {
      this.expect('[');
      this.reader.skipWhitespace();
      if (!this.reader.canRead()) {
         throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
      } else {
         ListTag listtag = new ListTag();
         TagType<?> tagtype = null;

         while(this.reader.peek() != ']') {
            int i = this.reader.getCursor();
            Tag tag = this.readValue();
            TagType<?> tagtype1 = tag.getType();
            if (tagtype == null) {
               tagtype = tagtype1;
            } else if (tagtype1 != tagtype) {
               this.reader.setCursor(i);
               throw ERROR_INSERT_MIXED_LIST.createWithContext(this.reader, tagtype1.getPrettyName(), tagtype.getPrettyName());
            }

            listtag.add(tag);
            if (!this.hasElementSeparator()) {
               break;
            }

            if (!this.reader.canRead()) {
               throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
            }
         }

         this.expect(']');
         return listtag;
      }
   }

   private Tag readArrayTag() throws CommandSyntaxException {
      this.expect('[');
      int i = this.reader.getCursor();
      char c0 = this.reader.read();
      this.reader.read();
      this.reader.skipWhitespace();
      if (!this.reader.canRead()) {
         throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
      } else if (c0 == 'B') {
         return new ByteArrayTag(this.readArray(ByteArrayTag.TYPE, ByteTag.TYPE));
      } else if (c0 == 'L') {
         return new LongArrayTag(this.readArray(LongArrayTag.TYPE, LongTag.TYPE));
      } else if (c0 == 'I') {
         return new IntArrayTag(this.readArray(IntArrayTag.TYPE, IntTag.TYPE));
      } else {
         this.reader.setCursor(i);
         throw ERROR_INVALID_ARRAY.createWithContext(this.reader, String.valueOf(c0));
      }
   }

   private <T extends Number> List<T> readArray(TagType<?> p_129362_, TagType<?> p_129363_) throws CommandSyntaxException {
      List<T> list = Lists.newArrayList();

      while(true) {
         if (this.reader.peek() != ']') {
            int i = this.reader.getCursor();
            Tag tag = this.readValue();
            TagType<?> tagtype = tag.getType();
            if (tagtype != p_129363_) {
               this.reader.setCursor(i);
               throw ERROR_INSERT_MIXED_ARRAY.createWithContext(this.reader, tagtype.getPrettyName(), p_129362_.getPrettyName());
            }

            if (p_129363_ == ByteTag.TYPE) {
               list.add((T)(Byte)((NumericTag)tag).getAsByte());
            } else if (p_129363_ == LongTag.TYPE) {
               list.add((T)(Long)((NumericTag)tag).getAsLong());
            } else {
               list.add((T)(Integer)((NumericTag)tag).getAsInt());
            }

            if (this.hasElementSeparator()) {
               if (!this.reader.canRead()) {
                  throw ERROR_EXPECTED_VALUE.createWithContext(this.reader);
               }
               continue;
            }
         }

         this.expect(']');
         return list;
      }
   }

   private boolean hasElementSeparator() {
      this.reader.skipWhitespace();
      if (this.reader.canRead() && this.reader.peek() == ',') {
         this.reader.skip();
         this.reader.skipWhitespace();
         return true;
      } else {
         return false;
      }
   }

   private void expect(char p_129353_) throws CommandSyntaxException {
      this.reader.skipWhitespace();
      this.reader.expect(p_129353_);
   }
}