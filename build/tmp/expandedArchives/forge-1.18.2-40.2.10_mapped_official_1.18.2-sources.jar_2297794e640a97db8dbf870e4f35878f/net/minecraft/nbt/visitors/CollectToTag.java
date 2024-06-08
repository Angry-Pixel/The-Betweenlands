package net.minecraft.nbt.visitors;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;

public class CollectToTag implements StreamTagVisitor {
   private String lastId = "";
   @Nullable
   private Tag rootTag;
   private final Deque<Consumer<Tag>> consumerStack = new ArrayDeque<>();

   @Nullable
   public Tag getResult() {
      return this.rootTag;
   }

   protected int depth() {
      return this.consumerStack.size();
   }

   private void appendEntry(Tag p_197683_) {
      this.consumerStack.getLast().accept(p_197683_);
   }

   public StreamTagVisitor.ValueResult visitEnd() {
      this.appendEntry(EndTag.INSTANCE);
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(String p_197678_) {
      this.appendEntry(StringTag.valueOf(p_197678_));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(byte p_197668_) {
      this.appendEntry(ByteTag.valueOf(p_197668_));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(short p_197693_) {
      this.appendEntry(ShortTag.valueOf(p_197693_));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(int p_197674_) {
      this.appendEntry(IntTag.valueOf(p_197674_));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(long p_197676_) {
      this.appendEntry(LongTag.valueOf(p_197676_));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(float p_197672_) {
      this.appendEntry(FloatTag.valueOf(p_197672_));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(double p_197670_) {
      this.appendEntry(DoubleTag.valueOf(p_197670_));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(byte[] p_197695_) {
      this.appendEntry(new ByteArrayTag(p_197695_));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(int[] p_197697_) {
      this.appendEntry(new IntArrayTag(p_197697_));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visit(long[] p_197699_) {
      this.appendEntry(new LongArrayTag(p_197699_));
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visitList(TagType<?> p_197687_, int p_197688_) {
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.EntryResult visitElement(TagType<?> p_197709_, int p_197710_) {
      this.enterContainerIfNeeded(p_197709_);
      return StreamTagVisitor.EntryResult.ENTER;
   }

   public StreamTagVisitor.EntryResult visitEntry(TagType<?> p_197685_) {
      return StreamTagVisitor.EntryResult.ENTER;
   }

   public StreamTagVisitor.EntryResult visitEntry(TagType<?> p_197690_, String p_197691_) {
      this.lastId = p_197691_;
      this.enterContainerIfNeeded(p_197690_);
      return StreamTagVisitor.EntryResult.ENTER;
   }

   private void enterContainerIfNeeded(TagType<?> p_197712_) {
      if (p_197712_ == ListTag.TYPE) {
         ListTag listtag = new ListTag();
         this.appendEntry(listtag);
         this.consumerStack.addLast(listtag::add);
      } else if (p_197712_ == CompoundTag.TYPE) {
         CompoundTag compoundtag = new CompoundTag();
         this.appendEntry(compoundtag);
         this.consumerStack.addLast((p_197703_) -> {
            compoundtag.put(this.lastId, p_197703_);
         });
      }

   }

   public StreamTagVisitor.ValueResult visitContainerEnd() {
      this.consumerStack.removeLast();
      return StreamTagVisitor.ValueResult.CONTINUE;
   }

   public StreamTagVisitor.ValueResult visitRootEntry(TagType<?> p_197707_) {
      if (p_197707_ == ListTag.TYPE) {
         ListTag listtag = new ListTag();
         this.rootTag = listtag;
         this.consumerStack.addLast(listtag::add);
      } else if (p_197707_ == CompoundTag.TYPE) {
         CompoundTag compoundtag = new CompoundTag();
         this.rootTag = compoundtag;
         this.consumerStack.addLast((p_197681_) -> {
            compoundtag.put(this.lastId, p_197681_);
         });
      } else {
         this.consumerStack.addLast((p_197705_) -> {
            this.rootTag = p_197705_;
         });
      }

      return StreamTagVisitor.ValueResult.CONTINUE;
   }
}