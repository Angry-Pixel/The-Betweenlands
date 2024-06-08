package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class IntArrayTag extends CollectionTag<IntTag> {
   private static final int SELF_SIZE_IN_BITS = 192;
   public static final TagType<IntArrayTag> TYPE = new TagType.VariableSize<IntArrayTag>() {
      public IntArrayTag load(DataInput p_128662_, int p_128663_, NbtAccounter p_128664_) throws IOException {
         p_128664_.accountBits(192L);
         int i = p_128662_.readInt();
         p_128664_.accountBits(32L * (long)i);
         int[] aint = new int[i];

         for(int j = 0; j < i; ++j) {
            aint[j] = p_128662_.readInt();
         }

         return new IntArrayTag(aint);
      }

      public StreamTagVisitor.ValueResult parse(DataInput p_197478_, StreamTagVisitor p_197479_) throws IOException {
         int i = p_197478_.readInt();
         int[] aint = new int[i];

         for(int j = 0; j < i; ++j) {
            aint[j] = p_197478_.readInt();
         }

         return p_197479_.visit(aint);
      }

      public void skip(DataInput p_197476_) throws IOException {
         p_197476_.skipBytes(p_197476_.readInt() * 4);
      }

      public String getName() {
         return "INT[]";
      }

      public String getPrettyName() {
         return "TAG_Int_Array";
      }
   };
   private int[] data;

   public IntArrayTag(int[] p_128605_) {
      this.data = p_128605_;
   }

   public IntArrayTag(List<Integer> p_128603_) {
      this(toArray(p_128603_));
   }

   private static int[] toArray(List<Integer> p_128621_) {
      int[] aint = new int[p_128621_.size()];

      for(int i = 0; i < p_128621_.size(); ++i) {
         Integer integer = p_128621_.get(i);
         aint[i] = integer == null ? 0 : integer;
      }

      return aint;
   }

   public void write(DataOutput p_128616_) throws IOException {
      p_128616_.writeInt(this.data.length);

      for(int i : this.data) {
         p_128616_.writeInt(i);
      }

   }

   public byte getId() {
      return 11;
   }

   public TagType<IntArrayTag> getType() {
      return TYPE;
   }

   public String toString() {
      return this.getAsString();
   }

   public IntArrayTag copy() {
      int[] aint = new int[this.data.length];
      System.arraycopy(this.data, 0, aint, 0, this.data.length);
      return new IntArrayTag(aint);
   }

   public boolean equals(Object p_128647_) {
      if (this == p_128647_) {
         return true;
      } else {
         return p_128647_ instanceof IntArrayTag && Arrays.equals(this.data, ((IntArrayTag)p_128647_).data);
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.data);
   }

   public int[] getAsIntArray() {
      return this.data;
   }

   public void accept(TagVisitor p_177869_) {
      p_177869_.visitIntArray(this);
   }

   public int size() {
      return this.data.length;
   }

   public IntTag get(int p_128608_) {
      return IntTag.valueOf(this.data[p_128608_]);
   }

   public IntTag set(int p_128610_, IntTag p_128611_) {
      int i = this.data[p_128610_];
      this.data[p_128610_] = p_128611_.getAsInt();
      return IntTag.valueOf(i);
   }

   public void add(int p_128629_, IntTag p_128630_) {
      this.data = ArrayUtils.add(this.data, p_128629_, p_128630_.getAsInt());
   }

   public boolean setTag(int p_128613_, Tag p_128614_) {
      if (p_128614_ instanceof NumericTag) {
         this.data[p_128613_] = ((NumericTag)p_128614_).getAsInt();
         return true;
      } else {
         return false;
      }
   }

   public boolean addTag(int p_128632_, Tag p_128633_) {
      if (p_128633_ instanceof NumericTag) {
         this.data = ArrayUtils.add(this.data, p_128632_, ((NumericTag)p_128633_).getAsInt());
         return true;
      } else {
         return false;
      }
   }

   public IntTag remove(int p_128627_) {
      int i = this.data[p_128627_];
      this.data = ArrayUtils.remove(this.data, p_128627_);
      return IntTag.valueOf(i);
   }

   public byte getElementType() {
      return 3;
   }

   public void clear() {
      this.data = new int[0];
   }

   public StreamTagVisitor.ValueResult accept(StreamTagVisitor p_197474_) {
      return p_197474_.visit(this.data);
   }
}