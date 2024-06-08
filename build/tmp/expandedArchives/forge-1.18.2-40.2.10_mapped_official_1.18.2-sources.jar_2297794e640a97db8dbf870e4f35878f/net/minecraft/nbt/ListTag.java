package net.minecraft.nbt;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ListTag extends CollectionTag<Tag> {
   private static final int SELF_SIZE_IN_BITS = 296;
   public static final TagType<ListTag> TYPE = new TagType.VariableSize<ListTag>() {
      public ListTag load(DataInput p_128792_, int p_128793_, NbtAccounter p_128794_) throws IOException {
         p_128794_.accountBits(296L);
         if (p_128793_ > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
         } else {
            byte b0 = p_128792_.readByte();
            int i = p_128792_.readInt();
            if (b0 == 0 && i > 0) {
               throw new RuntimeException("Missing type on ListTag");
            } else {
               p_128794_.accountBits(32L * (long)i);
               TagType<?> tagtype = TagTypes.getType(b0);
               List<Tag> list = Lists.newArrayListWithCapacity(i);

               for(int j = 0; j < i; ++j) {
                  list.add(tagtype.load(p_128792_, p_128793_ + 1, p_128794_));
               }

               return new ListTag(list, b0);
            }
         }
      }

      public StreamTagVisitor.ValueResult parse(DataInput p_197491_, StreamTagVisitor p_197492_) throws IOException {
         TagType<?> tagtype = TagTypes.getType(p_197491_.readByte());
         int i = p_197491_.readInt();
         switch(p_197492_.visitList(tagtype, i)) {
         case HALT:
            return StreamTagVisitor.ValueResult.HALT;
         case BREAK:
            tagtype.skip(p_197491_, i);
            return p_197492_.visitContainerEnd();
         default:
            int j = 0;

            while(true) {
               label45: {
                  if (j < i) {
                     switch(p_197492_.visitElement(tagtype, j)) {
                     case HALT:
                        return StreamTagVisitor.ValueResult.HALT;
                     case BREAK:
                        tagtype.skip(p_197491_);
                        break;
                     case SKIP:
                        tagtype.skip(p_197491_);
                        break label45;
                     default:
                        switch(tagtype.parse(p_197491_, p_197492_)) {
                        case HALT:
                           return StreamTagVisitor.ValueResult.HALT;
                        case BREAK:
                           break;
                        default:
                           break label45;
                        }
                     }
                  }

                  int k = i - 1 - j;
                  if (k > 0) {
                     tagtype.skip(p_197491_, k);
                  }

                  return p_197492_.visitContainerEnd();
               }

               ++j;
            }
         }
      }

      public void skip(DataInput p_197489_) throws IOException {
         TagType<?> tagtype = TagTypes.getType(p_197489_.readByte());
         int i = p_197489_.readInt();
         tagtype.skip(p_197489_, i);
      }

      public String getName() {
         return "LIST";
      }

      public String getPrettyName() {
         return "TAG_List";
      }
   };
   private final List<Tag> list;
   private byte type;

   ListTag(List<Tag> p_128721_, byte p_128722_) {
      this.list = p_128721_;
      this.type = p_128722_;
   }

   public ListTag() {
      this(Lists.newArrayList(), (byte)0);
   }

   public void write(DataOutput p_128734_) throws IOException {
      if (this.list.isEmpty()) {
         this.type = 0;
      } else {
         this.type = this.list.get(0).getId();
      }

      p_128734_.writeByte(this.type);
      p_128734_.writeInt(this.list.size());

      for(Tag tag : this.list) {
         tag.write(p_128734_);
      }

   }

   public byte getId() {
      return 9;
   }

   public TagType<ListTag> getType() {
      return TYPE;
   }

   public String toString() {
      return this.getAsString();
   }

   private void updateTypeAfterRemove() {
      if (this.list.isEmpty()) {
         this.type = 0;
      }

   }

   public Tag remove(int p_128751_) {
      Tag tag = this.list.remove(p_128751_);
      this.updateTypeAfterRemove();
      return tag;
   }

   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   public CompoundTag getCompound(int p_128729_) {
      if (p_128729_ >= 0 && p_128729_ < this.list.size()) {
         Tag tag = this.list.get(p_128729_);
         if (tag.getId() == 10) {
            return (CompoundTag)tag;
         }
      }

      return new CompoundTag();
   }

   public ListTag getList(int p_128745_) {
      if (p_128745_ >= 0 && p_128745_ < this.list.size()) {
         Tag tag = this.list.get(p_128745_);
         if (tag.getId() == 9) {
            return (ListTag)tag;
         }
      }

      return new ListTag();
   }

   public short getShort(int p_128758_) {
      if (p_128758_ >= 0 && p_128758_ < this.list.size()) {
         Tag tag = this.list.get(p_128758_);
         if (tag.getId() == 2) {
            return ((ShortTag)tag).getAsShort();
         }
      }

      return 0;
   }

   public int getInt(int p_128764_) {
      if (p_128764_ >= 0 && p_128764_ < this.list.size()) {
         Tag tag = this.list.get(p_128764_);
         if (tag.getId() == 3) {
            return ((IntTag)tag).getAsInt();
         }
      }

      return 0;
   }

   public int[] getIntArray(int p_128768_) {
      if (p_128768_ >= 0 && p_128768_ < this.list.size()) {
         Tag tag = this.list.get(p_128768_);
         if (tag.getId() == 11) {
            return ((IntArrayTag)tag).getAsIntArray();
         }
      }

      return new int[0];
   }

   public long[] getLongArray(int p_177992_) {
      if (p_177992_ >= 0 && p_177992_ < this.list.size()) {
         Tag tag = this.list.get(p_177992_);
         if (tag.getId() == 11) {
            return ((LongArrayTag)tag).getAsLongArray();
         }
      }

      return new long[0];
   }

   public double getDouble(int p_128773_) {
      if (p_128773_ >= 0 && p_128773_ < this.list.size()) {
         Tag tag = this.list.get(p_128773_);
         if (tag.getId() == 6) {
            return ((DoubleTag)tag).getAsDouble();
         }
      }

      return 0.0D;
   }

   public float getFloat(int p_128776_) {
      if (p_128776_ >= 0 && p_128776_ < this.list.size()) {
         Tag tag = this.list.get(p_128776_);
         if (tag.getId() == 5) {
            return ((FloatTag)tag).getAsFloat();
         }
      }

      return 0.0F;
   }

   public String getString(int p_128779_) {
      if (p_128779_ >= 0 && p_128779_ < this.list.size()) {
         Tag tag = this.list.get(p_128779_);
         return tag.getId() == 8 ? tag.getAsString() : tag.toString();
      } else {
         return "";
      }
   }

   public int size() {
      return this.list.size();
   }

   public Tag get(int p_128781_) {
      return this.list.get(p_128781_);
   }

   public Tag set(int p_128760_, Tag p_128761_) {
      Tag tag = this.get(p_128760_);
      if (!this.setTag(p_128760_, p_128761_)) {
         throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", p_128761_.getId(), this.type));
      } else {
         return tag;
      }
   }

   public void add(int p_128753_, Tag p_128754_) {
      if (!this.addTag(p_128753_, p_128754_)) {
         throw new UnsupportedOperationException(String.format("Trying to add tag of type %d to list of %d", p_128754_.getId(), this.type));
      }
   }

   public boolean setTag(int p_128731_, Tag p_128732_) {
      if (this.updateType(p_128732_)) {
         this.list.set(p_128731_, p_128732_);
         return true;
      } else {
         return false;
      }
   }

   public boolean addTag(int p_128747_, Tag p_128748_) {
      if (this.updateType(p_128748_)) {
         this.list.add(p_128747_, p_128748_);
         return true;
      } else {
         return false;
      }
   }

   private boolean updateType(Tag p_128739_) {
      if (p_128739_.getId() == 0) {
         return false;
      } else if (this.type == 0) {
         this.type = p_128739_.getId();
         return true;
      } else {
         return this.type == p_128739_.getId();
      }
   }

   public ListTag copy() {
      Iterable<Tag> iterable = (Iterable<Tag>)(TagTypes.getType(this.type).isValue() ? this.list : Iterables.transform(this.list, Tag::copy));
      List<Tag> list = Lists.newArrayList(iterable);
      return new ListTag(list, this.type);
   }

   public boolean equals(Object p_128766_) {
      if (this == p_128766_) {
         return true;
      } else {
         return p_128766_ instanceof ListTag && Objects.equals(this.list, ((ListTag)p_128766_).list);
      }
   }

   public int hashCode() {
      return this.list.hashCode();
   }

   public void accept(TagVisitor p_177990_) {
      p_177990_.visitList(this);
   }

   public byte getElementType() {
      return this.type;
   }

   public void clear() {
      this.list.clear();
      this.type = 0;
   }

   public StreamTagVisitor.ValueResult accept(StreamTagVisitor p_197487_) {
      switch(p_197487_.visitList(TagTypes.getType(this.type), this.list.size())) {
      case HALT:
         return StreamTagVisitor.ValueResult.HALT;
      case BREAK:
         return p_197487_.visitContainerEnd();
      default:
         int i = 0;

         while(i < this.list.size()) {
            Tag tag = this.list.get(i);
            switch(p_197487_.visitElement(tag.getType(), i)) {
            case HALT:
               return StreamTagVisitor.ValueResult.HALT;
            case BREAK:
               return p_197487_.visitContainerEnd();
            default:
               switch(tag.accept(p_197487_)) {
               case HALT:
                  return StreamTagVisitor.ValueResult.HALT;
               case BREAK:
                  return p_197487_.visitContainerEnd();
               }
            case SKIP:
               ++i;
            }
         }

         return p_197487_.visitContainerEnd();
      }
   }
}