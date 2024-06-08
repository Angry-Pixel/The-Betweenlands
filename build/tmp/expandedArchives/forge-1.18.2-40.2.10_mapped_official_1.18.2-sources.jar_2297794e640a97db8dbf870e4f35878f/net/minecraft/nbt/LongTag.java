package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LongTag extends NumericTag {
   private static final int SELF_SIZE_IN_BITS = 128;
   public static final TagType<LongTag> TYPE = new TagType.StaticSize<LongTag>() {
      public LongTag load(DataInput p_128906_, int p_128907_, NbtAccounter p_128908_) throws IOException {
         p_128908_.accountBits(128L);
         return LongTag.valueOf(p_128906_.readLong());
      }

      public StreamTagVisitor.ValueResult parse(DataInput p_197506_, StreamTagVisitor p_197507_) throws IOException {
         return p_197507_.visit(p_197506_.readLong());
      }

      public int size() {
         return 8;
      }

      public String getName() {
         return "LONG";
      }

      public String getPrettyName() {
         return "TAG_Long";
      }

      public boolean isValue() {
         return true;
      }
   };
   private final long data;

   LongTag(long p_128877_) {
      this.data = p_128877_;
   }

   public static LongTag valueOf(long p_128883_) {
      return p_128883_ >= -128L && p_128883_ <= 1024L ? LongTag.Cache.cache[(int)p_128883_ - -128] : new LongTag(p_128883_);
   }

   public void write(DataOutput p_128885_) throws IOException {
      p_128885_.writeLong(this.data);
   }

   public byte getId() {
      return 4;
   }

   public TagType<LongTag> getType() {
      return TYPE;
   }

   public LongTag copy() {
      return this;
   }

   public boolean equals(Object p_128894_) {
      if (this == p_128894_) {
         return true;
      } else {
         return p_128894_ instanceof LongTag && this.data == ((LongTag)p_128894_).data;
      }
   }

   public int hashCode() {
      return (int)(this.data ^ this.data >>> 32);
   }

   public void accept(TagVisitor p_177998_) {
      p_177998_.visitLong(this);
   }

   public long getAsLong() {
      return this.data;
   }

   public int getAsInt() {
      return (int)(this.data & -1L);
   }

   public short getAsShort() {
      return (short)((int)(this.data & 65535L));
   }

   public byte getAsByte() {
      return (byte)((int)(this.data & 255L));
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

   public StreamTagVisitor.ValueResult accept(StreamTagVisitor p_197504_) {
      return p_197504_.visit(this.data);
   }

   static class Cache {
      private static final int HIGH = 1024;
      private static final int LOW = -128;
      static final LongTag[] cache = new LongTag[1153];

      private Cache() {
      }

      static {
         for(int i = 0; i < cache.length; ++i) {
            cache[i] = new LongTag((long)(-128 + i));
         }

      }
   }
}