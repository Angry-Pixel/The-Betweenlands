package net.minecraft.nbt.visitors;

import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.TagType;

public class SkipFields extends CollectToTag {
   private final Deque<FieldTree> stack = new ArrayDeque<>();

   public SkipFields(FieldSelector... p_202549_) {
      FieldTree fieldtree = FieldTree.createRoot();

      for(FieldSelector fieldselector : p_202549_) {
         fieldtree.addEntry(fieldselector);
      }

      this.stack.push(fieldtree);
   }

   public StreamTagVisitor.EntryResult visitEntry(TagType<?> p_202551_, String p_202552_) {
      FieldTree fieldtree = this.stack.element();
      if (fieldtree.isSelected(p_202551_, p_202552_)) {
         return StreamTagVisitor.EntryResult.SKIP;
      } else {
         if (p_202551_ == CompoundTag.TYPE) {
            FieldTree fieldtree1 = fieldtree.fieldsToRecurse().get(p_202552_);
            if (fieldtree1 != null) {
               this.stack.push(fieldtree1);
            }
         }

         return super.visitEntry(p_202551_, p_202552_);
      }
   }

   public StreamTagVisitor.ValueResult visitContainerEnd() {
      if (this.depth() == this.stack.element().depth()) {
         this.stack.pop();
      }

      return super.visitContainerEnd();
   }
}