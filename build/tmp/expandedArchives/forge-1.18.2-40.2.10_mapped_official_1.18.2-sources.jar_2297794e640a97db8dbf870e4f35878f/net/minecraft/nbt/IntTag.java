package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntTag extends NumericTag {
   private static final int SELF_SIZE_IN_BITS = 96;
   public static final TagType<IntTag> TYPE = new TagType.StaticSize<IntTag>() {
      public IntTag load(DataInput p_128703_, int p_128704_, NbtAccounter p_128705_) throws IOException {
         p_128705_.accountBits(96L);
         return IntTag.valueOf(p_128703_.readInt());
      }

      public StreamTagVisitor.ValueResult parse(DataInput p_197483_, StreamTagVisitor p_197484_) throws IOException {
         return p_197484_.visit(p_197483_.readInt());
      }

      public int size() {
         return 4;
      }

      public String getName() {
         return "INT";
      }

      public String getPrettyName() {
         return "TAG_Int";
      }

      public boolean isValue() {
         return true;
      }
   };
   private final int data;

   IntTag(int p_128674_) {
      this.data = p_128674_;
   }

   public static IntTag valueOf(int p_128680_) {
      return p_128680_ >= -128 && p_128680_ <= 1024 ? IntTag.Cache.cache[p_128680_ - -128] : new IntTag(p_128680_);
   }

   public void write(DataOutput p_128682_) throws IOException {
      p_128682_.writeInt(this.data);
   }

   public byte getId() {
      return 3;
   }

   public TagType<IntTag> getType() {
      return TYPE;
   }

   public IntTag copy() {
      return this;
   }

   public boolean equals(Object p_128691_) {
      if (this == p_128691_) {
         return true;
      } else {
         return p_128691_ instanceof IntTag && this.data == ((IntTag)p_128691_).data;
      }
   }

   public int hashCode() {
      return this.data;
   }

   public void accept(TagVisitor p_177984_) {
      p_177984_.visitInt(this);
   }

   public long getAsLong() {
      return (long)this.data;
   }

   public int getAsInt() {
      return this.data;
   }

   public short getAsShort() {
      return (short)(this.data & '\uffff');
   }

   public byte getAsByte() {
      return (byte)(this.data & 255);
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

   public StreamTagVisitor.ValueResult accept(StreamTagVisitor p_197481_) {
      return p_197481_.visit(this.data);
   }

   static class Cache {
      private static final int HIGH = 1024;
      private static final int LOW = -128;
      static final IntTag[] cache = new IntTag[1153];

      private Cache() {
      }

      static {
         for(int i = 0; i < cache.length; ++i) {
            cache[i] = new IntTag(-128 + i);
         }

      }
   }
}