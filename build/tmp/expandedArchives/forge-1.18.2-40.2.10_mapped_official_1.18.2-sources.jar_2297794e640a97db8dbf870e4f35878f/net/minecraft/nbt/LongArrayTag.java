package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class LongArrayTag extends CollectionTag<LongTag> {
   private static final int SELF_SIZE_IN_BITS = 192;
   public static final TagType<LongArrayTag> TYPE = new TagType.VariableSize<LongArrayTag>() {
      public LongArrayTag load(DataInput p_128865_, int p_128866_, NbtAccounter p_128867_) throws IOException {
         p_128867_.accountBits(192L);
         int i = p_128865_.readInt();
         p_128867_.accountBits(64L * (long)i);
         long[] along = new long[i];

         for(int j = 0; j < i; ++j) {
            along[j] = p_128865_.readLong();
         }

         return new LongArrayTag(along);
      }

      public StreamTagVisitor.ValueResult parse(DataInput p_197501_, StreamTagVisitor p_197502_) throws IOException {
         int i = p_197501_.readInt();
         long[] along = new long[i];

         for(int j = 0; j < i; ++j) {
            along[j] = p_197501_.readLong();
         }

         return p_197502_.visit(along);
      }

      public void skip(DataInput p_197499_) throws IOException {
         p_197499_.skipBytes(p_197499_.readInt() * 8);
      }

      public String getName() {
         return "LONG[]";
      }

      public String getPrettyName() {
         return "TAG_Long_Array";
      }
   };
   private long[] data;

   public LongArrayTag(long[] p_128808_) {
      this.data = p_128808_;
   }

   public LongArrayTag(LongSet p_128804_) {
      this.data = p_128804_.toLongArray();
   }

   public LongArrayTag(List<Long> p_128806_) {
      this(toArray(p_128806_));
   }

   private static long[] toArray(List<Long> p_128824_) {
      long[] along = new long[p_128824_.size()];

      for(int i = 0; i < p_128824_.size(); ++i) {
         Long olong = p_128824_.get(i);
         along[i] = olong == null ? 0L : olong;
      }

      return along;
   }

   public void write(DataOutput p_128819_) throws IOException {
      p_128819_.writeInt(this.data.length);

      for(long i : this.data) {
         p_128819_.writeLong(i);
      }

   }

   public byte getId() {
      return 12;
   }

   public TagType<LongArrayTag> getType() {
      return TYPE;
   }

   public String toString() {
      return this.getAsString();
   }

   public LongArrayTag copy() {
      long[] along = new long[this.data.length];
      System.arraycopy(this.data, 0, along, 0, this.data.length);
      return new LongArrayTag(along);
   }

   public boolean equals(Object p_128850_) {
      if (this == p_128850_) {
         return true;
      } else {
         return p_128850_ instanceof LongArrayTag && Arrays.equals(this.data, ((LongArrayTag)p_128850_).data);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public void accept(TagVisitor p_177995_) {
      p_177995_.visitLongArray(this);
   }

   public long[] getAsLongArray() {
      return this.data;
   }

   public int size() {
      return this.data.length;
   }

   public LongTag get(int p_128811_) {
      return LongTag.valueOf(this.data[p_128811_]);
   }

   public LongTag set(int p_128813_, LongTag p_128814_) {
      long i = this.data[p_128813_];
      this.data[p_128813_] = p_128814_.getAsLong();
      return LongTag.valueOf(i);
   }

   public void add(int p_128832_, LongTag p_128833_) {
      this.data = ArrayUtils.add(this.data, p_128832_, p_128833_.getAsLong());
   }

   public boolean setTag(int p_128816_, Tag p_128817_) {
      if (p_128817_ instanceof NumericTag) {
         this.data[p_128816_] = ((NumericTag)p_128817_).getAsLong();
         return true;
      } else {
         return false;
      }
   }

   public boolean addTag(int p_128835_, Tag p_128836_) {
      if (p_128836_ instanceof NumericTag) {
         this.data = ArrayUtils.add(this.data, p_128835_, ((NumericTag)p_128836_).getAsLong());
         return true;
      } else {
         return false;
      }
   }

   public LongTag remove(int p_128830_) {
      long i = this.data[p_128830_];
      this.data = ArrayUtils.remove(this.data, p_128830_);
      return LongTag.valueOf(i);
   }

   public byte getElementType() {
      return 4;
   }

   public void clear() {
      this.data = new long[0];
   }

   public StreamTagVisitor.ValueResult accept(StreamTagVisitor p_197497_) {
      return p_197497_.visit(this.data);
   }
}