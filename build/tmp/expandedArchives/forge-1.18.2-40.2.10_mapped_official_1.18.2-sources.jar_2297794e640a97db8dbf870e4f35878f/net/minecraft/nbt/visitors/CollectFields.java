package net.minecraft.nbt.visitors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.TagType;

public class CollectFields extends CollectToTag {
   private int fieldsToGetCount;
   private final Set<TagType<?>> wantedTypes;
   private final Deque<FieldTree> stack = new ArrayDeque<>();

   public CollectFields(FieldSelector... p_202496_) {
      this.fieldsToGetCount = p_202496_.length;
      Builder<TagType<?>> builder = ImmutableSet.builder();
      FieldTree fieldtree = FieldTree.createRoot();

      for(FieldSelector fieldselector : p_202496_) {
         fieldtree.addEntry(fieldselector);
         builder.add(fieldselector.type());
      }

      this.stack.push(fieldtree);
      builder.add(CompoundTag.TYPE);
      this.wantedTypes = builder.build();
   }

   public StreamTagVisitor.ValueResult visitRootEntry(TagType<?> p_197614_) {
      return p_197614_ != CompoundTag.TYPE ? StreamTagVisitor.ValueResult.HALT : super.visitRootEntry(p_197614_);
   }

   public StreamTagVisitor.EntryResult visitEntry(TagType<?> p_197608_) {
      FieldTree fieldtree = this.stack.element();
      if (this.depth() > fieldtree.depth()) {
         return super.visitEntry(p_197608_);
      } else if (this.fieldsToGetCount <= 0) {
         return StreamTagVisitor.EntryResult.HALT;
      } else {
         return !this.wantedTypes.contains(p_197608_) ? StreamTagVisitor.EntryResult.SKIP : super.visitEntry(p_197608_);
      }
   }

   public StreamTagVisitor.EntryResult visitEntry(TagType<?> p_197610_, String p_197611_) {
      FieldTree fieldtree = this.stack.element();
      if (this.depth() > fieldtree.depth()) {
         return super.visitEntry(p_197610_, p_197611_);
      } else if (fieldtree.selectedFields().remove(p_197611_, p_197610_)) {
         --this.fieldsToGetCount;
         return super.visitEntry(p_197610_, p_197611_);
      } else {
         if (p_197610_ == CompoundTag.TYPE) {
            FieldTree fieldtree1 = fieldtree.fieldsToRecurse().get(p_197611_);
            if (fieldtree1 != null) {
               this.stack.push(fieldtree1);
               return super.visitEntry(p_197610_, p_197611_);
            }
         }

         return StreamTagVisitor.EntryResult.SKIP;
      }
   }

   public StreamTagVisitor.ValueResult visitContainerEnd() {
      if (this.depth() == this.stack.element().depth()) {
         this.stack.pop();
      }

      return super.visitContainerEnd();
   }

   public int getMissingFieldCount() {
      return this.fieldsToGetCount;
   }
}