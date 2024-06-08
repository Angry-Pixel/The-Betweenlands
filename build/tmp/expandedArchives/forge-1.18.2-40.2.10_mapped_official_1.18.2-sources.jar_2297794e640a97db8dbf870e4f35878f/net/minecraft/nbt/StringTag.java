package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class StringTag implements Tag {
   private static final int SELF_SIZE_IN_BITS = 288;
   public static final TagType<StringTag> TYPE = new TagType.VariableSize<StringTag>() {
      public StringTag load(DataInput p_129315_, int p_129316_, NbtAccounter p_129317_) throws IOException {
         p_129317_.accountBits(288L);
         String s = p_129315_.readUTF();
         p_129317_.readUTF(s);
         return StringTag.valueOf(s);
      }

      public StreamTagVisitor.ValueResult parse(DataInput p_197570_, StreamTagVisitor p_197571_) throws IOException {
         return p_197571_.visit(p_197570_.readUTF());
      }

      public void skip(DataInput p_197568_) throws IOException {
         StringTag.skipString(p_197568_);
      }

      public String getName() {
         return "STRING";
      }

      public String getPrettyName() {
         return "TAG_String";
      }

      public boolean isValue() {
         return true;
      }
   };
   private static final StringTag EMPTY = new StringTag("");
   private static final char DOUBLE_QUOTE = '"';
   private static final char SINGLE_QUOTE = '\'';
   private static final char ESCAPE = '\\';
   private static final char NOT_SET = '\u0000';
   private final String data;

   public static void skipString(DataInput p_197564_) throws IOException {
      p_197564_.skipBytes(p_197564_.readUnsignedShort());
   }

   private StringTag(String p_129293_) {
      Objects.requireNonNull(p_129293_, "Null string not allowed");
      this.data = p_129293_;
   }

   public static StringTag valueOf(String p_129298_) {
      return p_129298_.isEmpty() ? EMPTY : new StringTag(p_129298_);
   }

   public void write(DataOutput p_129296_) throws IOException {
      p_129296_.writeUTF(this.data);
   }

   public byte getId() {
      return 8;
   }

   public TagType<StringTag> getType() {
      return TYPE;
   }

   public String toString() {
      return Tag.super.getAsString();
   }

   public StringTag copy() {
      return this;
   }

   public boolean equals(Object p_129308_) {
      if (this == p_129308_) {
         return true;
      } else {
         return p_129308_ instanceof StringTag && Objects.equals(this.data, ((StringTag)p_129308_).data);
      }
   }

   public int hashCode() {
      return this.data.hashCode();
   }

   public String getAsString() {
      return this.data;
   }

   public void accept(TagVisitor p_178154_) {
      p_178154_.visitString(this);
   }

   public static String quoteAndEscape(String p_129304_) {
      StringBuilder stringbuilder = new StringBuilder(" ");
      char c0 = 0;

      for(int i = 0; i < p_129304_.length(); ++i) {
         char c1 = p_129304_.charAt(i);
         if (c1 == '\\') {
            stringbuilder.append('\\');
         } else if (c1 == '"' || c1 == '\'') {
            if (c0 == 0) {
               c0 = (char)(c1 == '"' ? 39 : 34);
            }

            if (c0 == c1) {
               stringbuilder.append('\\');
            }
         }

         stringbuilder.append(c1);
      }

      if (c0 == 0) {
         c0 = '"';
      }

      stringbuilder.setCharAt(0, c0);
      stringbuilder.append(c0);
      return stringbuilder.toString();
   }

   public StreamTagVisitor.ValueResult accept(StreamTagVisitor p_197566_) {
      return p_197566_.visit(this.data);
   }
}
