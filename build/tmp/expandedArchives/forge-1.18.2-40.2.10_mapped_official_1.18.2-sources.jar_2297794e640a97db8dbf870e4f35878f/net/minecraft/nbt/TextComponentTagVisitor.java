package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import org.slf4j.Logger;

public class TextComponentTagVisitor implements TagVisitor {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int INLINE_LIST_THRESHOLD = 8;
   private static final ByteCollection INLINE_ELEMENT_TYPES = new ByteOpenHashSet(Arrays.asList((byte)1, (byte)2, (byte)3, (byte)4, (byte)5, (byte)6));
   private static final ChatFormatting SYNTAX_HIGHLIGHTING_KEY = ChatFormatting.AQUA;
   private static final ChatFormatting SYNTAX_HIGHLIGHTING_STRING = ChatFormatting.GREEN;
   private static final ChatFormatting SYNTAX_HIGHLIGHTING_NUMBER = ChatFormatting.GOLD;
   private static final ChatFormatting SYNTAX_HIGHLIGHTING_NUMBER_TYPE = ChatFormatting.RED;
   private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");
   private static final String NAME_VALUE_SEPARATOR = String.valueOf(':');
   private static final String ELEMENT_SEPARATOR = String.valueOf(',');
   private static final String LIST_OPEN = "[";
   private static final String LIST_CLOSE = "]";
   private static final String LIST_TYPE_SEPARATOR = ";";
   private static final String ELEMENT_SPACING = " ";
   private static final String STRUCT_OPEN = "{";
   private static final String STRUCT_CLOSE = "}";
   private static final String NEWLINE = "\n";
   private final String indentation;
   private final int depth;
   private Component result = TextComponent.EMPTY;

   public TextComponentTagVisitor(String p_178251_, int p_178252_) {
      this.indentation = p_178251_;
      this.depth = p_178252_;
   }

   public Component visit(Tag p_178282_) {
      p_178282_.accept(this);
      return this.result;
   }

   public void visitString(StringTag p_178280_) {
      String s = StringTag.quoteAndEscape(p_178280_.getAsString());
      String s1 = s.substring(0, 1);
      Component component = (new TextComponent(s.substring(1, s.length() - 1))).withStyle(SYNTAX_HIGHLIGHTING_STRING);
      this.result = (new TextComponent(s1)).append(component).append(s1);
   }

   public void visitByte(ByteTag p_178258_) {
      Component component = (new TextComponent("b")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      this.result = (new TextComponent(String.valueOf((Object)p_178258_.getAsNumber()))).append(component).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public void visitShort(ShortTag p_178278_) {
      Component component = (new TextComponent("s")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      this.result = (new TextComponent(String.valueOf((Object)p_178278_.getAsNumber()))).append(component).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public void visitInt(IntTag p_178270_) {
      this.result = (new TextComponent(String.valueOf((Object)p_178270_.getAsNumber()))).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public void visitLong(LongTag p_178276_) {
      Component component = (new TextComponent("L")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      this.result = (new TextComponent(String.valueOf((Object)p_178276_.getAsNumber()))).append(component).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public void visitFloat(FloatTag p_178266_) {
      Component component = (new TextComponent("f")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      this.result = (new TextComponent(String.valueOf(p_178266_.getAsFloat()))).append(component).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public void visitDouble(DoubleTag p_178262_) {
      Component component = (new TextComponent("d")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      this.result = (new TextComponent(String.valueOf(p_178262_.getAsDouble()))).append(component).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public void visitByteArray(ByteArrayTag p_178256_) {
      Component component = (new TextComponent("B")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      MutableComponent mutablecomponent = (new TextComponent("[")).append(component).append(";");
      byte[] abyte = p_178256_.getAsByteArray();

      for(int i = 0; i < abyte.length; ++i) {
         MutableComponent mutablecomponent1 = (new TextComponent(String.valueOf((int)abyte[i]))).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
         mutablecomponent.append(" ").append(mutablecomponent1).append(component);
         if (i != abyte.length - 1) {
            mutablecomponent.append(ELEMENT_SEPARATOR);
         }
      }

      mutablecomponent.append("]");
      this.result = mutablecomponent;
   }

   public void visitIntArray(IntArrayTag p_178268_) {
      Component component = (new TextComponent("I")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      MutableComponent mutablecomponent = (new TextComponent("[")).append(component).append(";");
      int[] aint = p_178268_.getAsIntArray();

      for(int i = 0; i < aint.length; ++i) {
         mutablecomponent.append(" ").append((new TextComponent(String.valueOf(aint[i]))).withStyle(SYNTAX_HIGHLIGHTING_NUMBER));
         if (i != aint.length - 1) {
            mutablecomponent.append(ELEMENT_SEPARATOR);
         }
      }

      mutablecomponent.append("]");
      this.result = mutablecomponent;
   }

   public void visitLongArray(LongArrayTag p_178274_) {
      Component component = (new TextComponent("L")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      MutableComponent mutablecomponent = (new TextComponent("[")).append(component).append(";");
      long[] along = p_178274_.getAsLongArray();

      for(int i = 0; i < along.length; ++i) {
         Component component1 = (new TextComponent(String.valueOf(along[i]))).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
         mutablecomponent.append(" ").append(component1).append(component);
         if (i != along.length - 1) {
            mutablecomponent.append(ELEMENT_SEPARATOR);
         }
      }

      mutablecomponent.append("]");
      this.result = mutablecomponent;
   }

   public void visitList(ListTag p_178272_) {
      if (p_178272_.isEmpty()) {
         this.result = new TextComponent("[]");
      } else if (INLINE_ELEMENT_TYPES.contains(p_178272_.getElementType()) && p_178272_.size() <= 8) {
         String s = ELEMENT_SEPARATOR + " ";
         MutableComponent mutablecomponent2 = new TextComponent("[");

         for(int j = 0; j < p_178272_.size(); ++j) {
            if (j != 0) {
               mutablecomponent2.append(s);
            }

            mutablecomponent2.append((new TextComponentTagVisitor(this.indentation, this.depth)).visit(p_178272_.get(j)));
         }

         mutablecomponent2.append("]");
         this.result = mutablecomponent2;
      } else {
         MutableComponent mutablecomponent = new TextComponent("[");
         if (!this.indentation.isEmpty()) {
            mutablecomponent.append("\n");
         }

         for(int i = 0; i < p_178272_.size(); ++i) {
            MutableComponent mutablecomponent1 = new TextComponent(Strings.repeat(this.indentation, this.depth + 1));
            mutablecomponent1.append((new TextComponentTagVisitor(this.indentation, this.depth + 1)).visit(p_178272_.get(i)));
            if (i != p_178272_.size() - 1) {
               mutablecomponent1.append(ELEMENT_SEPARATOR).append(this.indentation.isEmpty() ? " " : "\n");
            }

            mutablecomponent.append(mutablecomponent1);
         }

         if (!this.indentation.isEmpty()) {
            mutablecomponent.append("\n").append(Strings.repeat(this.indentation, this.depth));
         }

         mutablecomponent.append("]");
         this.result = mutablecomponent;
      }
   }

   public void visitCompound(CompoundTag p_178260_) {
      if (p_178260_.isEmpty()) {
         this.result = new TextComponent("{}");
      } else {
         MutableComponent mutablecomponent = new TextComponent("{");
         Collection<String> collection = p_178260_.getAllKeys();
         if (LOGGER.isDebugEnabled()) {
            List<String> list = Lists.newArrayList(p_178260_.getAllKeys());
            Collections.sort(list);
            collection = list;
         }

         if (!this.indentation.isEmpty()) {
            mutablecomponent.append("\n");
         }

         MutableComponent mutablecomponent1;
         for(Iterator<String> iterator = collection.iterator(); iterator.hasNext(); mutablecomponent.append(mutablecomponent1)) {
            String s = iterator.next();
            mutablecomponent1 = (new TextComponent(Strings.repeat(this.indentation, this.depth + 1))).append(handleEscapePretty(s)).append(NAME_VALUE_SEPARATOR).append(" ").append((new TextComponentTagVisitor(this.indentation, this.depth + 1)).visit(p_178260_.get(s)));
            if (iterator.hasNext()) {
               mutablecomponent1.append(ELEMENT_SEPARATOR).append(this.indentation.isEmpty() ? " " : "\n");
            }
         }

         if (!this.indentation.isEmpty()) {
            mutablecomponent.append("\n").append(Strings.repeat(this.indentation, this.depth));
         }

         mutablecomponent.append("}");
         this.result = mutablecomponent;
      }
   }

   protected static Component handleEscapePretty(String p_178254_) {
      if (SIMPLE_VALUE.matcher(p_178254_).matches()) {
         return (new TextComponent(p_178254_)).withStyle(SYNTAX_HIGHLIGHTING_KEY);
      } else {
         String s = StringTag.quoteAndEscape(p_178254_);
         String s1 = s.substring(0, 1);
         Component component = (new TextComponent(s.substring(1, s.length() - 1))).withStyle(SYNTAX_HIGHLIGHTING_KEY);
         return (new TextComponent(s1)).append(component).append(s1);
      }
   }

   public void visitEnd(EndTag p_178264_) {
      this.result = TextComponent.EMPTY;
   }
}