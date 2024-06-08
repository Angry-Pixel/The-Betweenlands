package net.minecraft.nbt.visitors;

import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.TagType;

public interface SkipAll extends StreamTagVisitor {
   SkipAll INSTANCE = new SkipAll() {
   };

   default StreamTagVisitor.ValueResult visitEnd() {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(String p_197729_) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(byte p_197719_) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(short p_197739_) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(int p_197725_) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(long p_197727_) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(float p_197723_) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(double p_197721_) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(byte[] p_197741_) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(int[] p_197743_) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visit(long[] p_197745_) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visitList(TagType<?> p_197733_, int p_197734_) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.EntryResult visitElement(TagType<?> p_197750_, int p_197751_) {
      return StreamTagVisitor.EntryResult.SKIP;
   }

   default StreamTagVisitor.EntryResult visitEntry(TagType<?> p_197731_) {
      return StreamTagVisitor.EntryResult.SKIP;
   }

   default StreamTagVisitor.EntryResult visitEntry(TagType<?> p_197736_, String p_197737_) {
      return StreamTagVisitor.EntryResult.SKIP;
   }

   default StreamTagVisitor.ValueResult visitContainerEnd() {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   default StreamTagVisitor.ValueResult visitRootEntry(TagType<?> p_197748_) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }
}