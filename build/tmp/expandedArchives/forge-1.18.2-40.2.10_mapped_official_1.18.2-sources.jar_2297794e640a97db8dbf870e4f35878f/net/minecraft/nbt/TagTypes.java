package net.minecraft.nbt;

public class TagTypes {
   private static final TagType<?>[] TYPES = new TagType[]{EndTag.TYPE, ByteTag.TYPE, ShortTag.TYPE, IntTag.TYPE, LongTag.TYPE, FloatTag.TYPE, DoubleTag.TYPE, ByteArrayTag.TYPE, StringTag.TYPE, ListTag.TYPE, CompoundTag.TYPE, IntArrayTag.TYPE, LongArrayTag.TYPE};

   public static TagType<?> getType(int p_129398_) {
      return p_129398_ >= 0 && p_129398_ < TYPES.length ? TYPES[p_129398_] : TagType.createInvalid(p_129398_);
   }
}