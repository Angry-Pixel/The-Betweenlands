package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteTag extends NumericTag {
   private static final int SELF_SIZE_IN_BITS = 72;
   public static final TagType<ByteTag> TYPE = new TagType.StaticSize<ByteTag>() {
      public ByteTag load(DataInput p_128292_, int p_128293_, NbtAccounter p_128294_) throws IOException {
         p_128294_.accountBits(72L);
         return ByteTag.valueOf(p_128292_.readByte());
      }

      public StreamTagVisitor.ValueResult parse(DataInput p_197438_, StreamTagVisitor p_197439_) throws IOException {
         return p_197439_.visit(p_197438_.readByte());
      }

      public int size() {
         return 1;
      }

      public String getName() {
         return "BYTE";
      }

      public String getPrettyName() {
         return "TAG_Byte";
      }

      public boolean isValue() {
         return true;
      }
   };
   public static final ByteTag ZERO = valueOf((byte)0);
   public static final ByteTag ONE = valueOf((byte)1);
   private final byte data;

   ByteTag(byte p_128261_) {
      this.data = p_128261_;
   }

   public static ByteTag valueOf(byte p_128267_) {
      return ByteTag.Cache.cache[128 + p_128267_];
   }

   public static ByteTag valueOf(boolean p_128274_) {
      return p_128274_ ? ONE : ZERO;
   }

   public void write(DataOutput p_128269_) throws IOException {
      p_128269_.writeByte(this.data);
   }

   public byte getId() {
      return 1;
   }

   public TagType<ByteTag> getType() {
      return TYPE;
   }

   public ByteTag copy() {
      return this;
   }

   public boolean equals(Object p_128280_) {
      if (this == p_128280_) {
         return true;
      } else {
         return p_128280_ instanceof ByteTag && this.data == ((ByteTag)p_128280_).data;
      }
   }

   public int hashCode() {
      return this.data;
   }

   public void accept(TagVisitor p_177842_) {
      p_177842_.visitByte(this);
   }

   public long getAsLong() {
      return (long)this.data;
   }

   public int getAsInt() {
      return this.data;
   }

   public short getAsShort() {
      return (short)this.data;
   }

   public byte getAsByte() {
      return this.data;
   }

   public double getAsDouble() {
      return (double)this.data;
   }

   public float getAsFloat() {
      return (float)this.data;
   }

   public Number getAsNumber() {
      return this.data;
   }

   public StreamTagVisitor.ValueResult accept(StreamTagVisitor p_197436_) {
      return p_197436_.visit(this.data);
   }

   static class Cache {
      static final ByteTag[] cache = new ByteTag[256];

      private Cache() {
      }

      static {
         for(int i = 0; i < cache.length; ++i) {
            cache[i] = new ByteTag((byte)(i - 128));
         }

      }
   }
}