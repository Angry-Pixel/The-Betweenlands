package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;

public interface TagType<T extends Tag> {
   T load(DataInput p_129379_, int p_129380_, NbtAccounter p_129381_) throws IOException;

   StreamTagVisitor.ValueResult parse(DataInput p_197578_, StreamTagVisitor p_197579_) throws IOException;

   default void parseRoot(DataInput p_197581_, StreamTagVisitor p_197582_) throws IOException {
      switch(p_197582_.visitRootEntry(this)) {
      case CONTINUE:
         this.parse(p_197581_, p_197582_);
      case HALT:
      default:
         break;
      case BREAK:
         this.skip(p_197581_);
      }

   }

   void skip(DataInput p_197576_, int p_197577_) throws IOException;

   void skip(DataInput p_197575_) throws IOException;

   default boolean isValue() {
      return false;
   }

   String getName();

   String getPrettyName();

   static TagType<EndTag> createInvalid(final int p_129378_) {
      return new TagType<EndTag>() {
         private IOException createException() {
            return new IOException("Invalid tag id: " + p_129378_);
         }

         public EndTag load(DataInput p_129387_, int p_129388_, NbtAccounter p_129389_) throws IOException {
            throw this.createException();
         }

         public StreamTagVisitor.ValueResult parse(DataInput p_197589_, StreamTagVisitor p_197590_) throws IOException {
            throw this.createException();
         }

         public void skip(DataInput p_197586_, int p_197587_) throws IOException {
            throw this.createException();
         }

         public void skip(DataInput p_197584_) throws IOException {
            throw this.createException();
         }

         public String getName() {
            return "INVALID[" + p_129378_ + "]";
         }

         public String getPrettyName() {
            return "UNKNOWN_" + p_129378_;
         }
      };
   }

   public interface StaticSize<T extends Tag> extends TagType<T> {
      default void skip(DataInput p_197595_) throws IOException {
         p_197595_.skipBytes(this.size());
      }

      default void skip(DataInput p_197597_, int p_197598_) throws IOException {
         p_197597_.skipBytes(this.size() * p_197598_);
      }

      int size();
   }

   public interface VariableSize<T extends Tag> extends TagType<T> {
      default void skip(DataInput p_197600_, int p_197601_) throws IOException {
         for(int i = 0; i < p_197601_; ++i) {
            this.skip(p_197600_);
         }

      }
   }
}