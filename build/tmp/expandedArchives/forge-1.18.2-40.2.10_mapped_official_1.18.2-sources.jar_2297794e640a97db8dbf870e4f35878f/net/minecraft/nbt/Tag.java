package net.minecraft.nbt;

import java.io.DataOutput;
import java.io.IOException;

public interface Tag {
   int OBJECT_HEADER = 64;
   int ARRAY_HEADER = 96;
   int OBJECT_REFERENCE = 32;
   int STRING_SIZE = 224;
   byte TAG_END = 0;
   byte TAG_BYTE = 1;
   byte TAG_SHORT = 2;
   byte TAG_INT = 3;
   byte TAG_LONG = 4;
   byte TAG_FLOAT = 5;
   byte TAG_DOUBLE = 6;
   byte TAG_BYTE_ARRAY = 7;
   byte TAG_STRING = 8;
   byte TAG_LIST = 9;
   byte TAG_COMPOUND = 10;
   byte TAG_INT_ARRAY = 11;
   byte TAG_LONG_ARRAY = 12;
   byte TAG_ANY_NUMERIC = 99;
   int MAX_DEPTH = 512;

   void write(DataOutput p_129329_) throws IOException;

   String toString();

   byte getId();

   TagType<?> getType();

   Tag copy();

   default String getAsString() {
      return (new StringTagVisitor()).visit(this);
   }

   void accept(TagVisitor p_178208_);

   StreamTagVisitor.ValueResult accept(StreamTagVisitor p_197572_);

   default void acceptAsRoot(StreamTagVisitor p_197574_) {
      StreamTagVisitor.ValueResult streamtagvisitor$valueresult = p_197574_.visitRootEntry(this.getType());
      if (streamtagvisitor$valueresult == StreamTagVisitor.ValueResult.CONTINUE) {
         this.accept(p_197574_);
      }

   }
}