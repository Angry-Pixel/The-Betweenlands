package net.minecraft.nbt;

public interface StreamTagVisitor {
   StreamTagVisitor.ValueResult visitEnd();

   StreamTagVisitor.ValueResult visit(String p_197525_);

   StreamTagVisitor.ValueResult visit(byte p_197520_);

   StreamTagVisitor.ValueResult visit(short p_197531_);

   StreamTagVisitor.ValueResult visit(int p_197523_);

   StreamTagVisitor.ValueResult visit(long p_197524_);

   StreamTagVisitor.ValueResult visit(float p_197522_);

   StreamTagVisitor.ValueResult visit(double p_197521_);

   StreamTagVisitor.ValueResult visit(byte[] p_197532_);

   StreamTagVisitor.ValueResult visit(int[] p_197533_);

   StreamTagVisitor.ValueResult visit(long[] p_197534_);

   StreamTagVisitor.ValueResult visitList(TagType<?> p_197527_, int p_197528_);

   StreamTagVisitor.EntryResult visitEntry(TagType<?> p_197526_);

   StreamTagVisitor.EntryResult visitEntry(TagType<?> p_197529_, String p_197530_);

   StreamTagVisitor.EntryResult visitElement(TagType<?> p_197536_, int p_197537_);

   StreamTagVisitor.ValueResult visitContainerEnd();

   StreamTagVisitor.ValueResult visitRootEntry(TagType<?> p_197535_);

   public static enum EntryResult {
      ENTER,
      SKIP,
      BREAK,
      HALT;
   }

   public static enum ValueResult {
      CONTINUE,
      BREAK,
      HALT;
   }
}