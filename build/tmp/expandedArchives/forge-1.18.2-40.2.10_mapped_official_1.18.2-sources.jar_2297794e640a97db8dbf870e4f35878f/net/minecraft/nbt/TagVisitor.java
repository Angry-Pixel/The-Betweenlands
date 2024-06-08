package net.minecraft.nbt;

public interface TagVisitor {
   void visitString(StringTag p_178228_);

   void visitByte(ByteTag p_178217_);

   void visitShort(ShortTag p_178227_);

   void visitInt(IntTag p_178223_);

   void visitLong(LongTag p_178226_);

   void visitFloat(FloatTag p_178221_);

   void visitDouble(DoubleTag p_178219_);

   void visitByteArray(ByteArrayTag p_178216_);

   void visitIntArray(IntArrayTag p_178222_);

   void visitLongArray(LongArrayTag p_178225_);

   void visitList(ListTag p_178224_);

   void visitCompound(CompoundTag p_178218_);

   void visitEnd(EndTag p_178220_);
}