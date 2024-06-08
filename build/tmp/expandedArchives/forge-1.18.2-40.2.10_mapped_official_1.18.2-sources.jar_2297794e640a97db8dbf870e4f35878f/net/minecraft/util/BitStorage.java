package net.minecraft.util;

import java.util.function.IntConsumer;

public interface BitStorage {
   int getAndSet(int p_13517_, int p_13518_);

   void set(int p_13525_, int p_13526_);

   int get(int p_13515_);

   long[] getRaw();

   int getSize();

   int getBits();

   void getAll(IntConsumer p_13520_);

   void unpack(int[] p_198162_);

   BitStorage copy();
}