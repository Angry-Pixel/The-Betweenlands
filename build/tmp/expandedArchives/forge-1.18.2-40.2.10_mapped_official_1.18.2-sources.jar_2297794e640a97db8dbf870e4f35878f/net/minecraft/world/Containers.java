package net.minecraft.world;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Containers {
   private static final Random RANDOM = new Random();

   public static void dropContents(Level p_19003_, BlockPos p_19004_, Container p_19005_) {
      dropContents(p_19003_, (double)p_19004_.getX(), (double)p_19004_.getY(), (double)p_19004_.getZ(), p_19005_);
   }

   public static void dropContents(Level p_18999_, Entity p_19000_, Container p_19001_) {
      dropContents(p_18999_, p_19000_.getX(), p_19000_.getY(), p_19000_.getZ(), p_19001_);
   }

   private static void dropContents(Level p_18987_, double p_18988_, double p_18989_, double p_18990_, Container p_18991_) {
      for(int i = 0; i < p_18991_.getContainerSize(); ++i) {
         dropItemStack(p_18987_, p_18988_, p_18989_, p_18990_, p_18991_.getItem(i));
      }

   }

   public static void dropContents(Level p_19011_, BlockPos p_19012_, NonNullList<ItemStack> p_19013_) {
      p_19013_.forEach((p_19009_) -> {
         dropItemStack(p_19011_, (double)p_19012_.getX(), (double)p_19012_.getY(), (double)p_19012_.getZ(), p_19009_);
      });
   }

   public static void dropItemStack(Level p_18993_, double p_18994_, double p_18995_, double p_18996_, ItemStack p_18997_) {
      double d0 = (double)EntityType.ITEM.getWidth();
      double d1 = 1.0D - d0;
      double d2 = d0 / 2.0D;
      double d3 = Math.floor(p_18994_) + RANDOM.nextDouble() * d1 + d2;
      double d4 = Math.floor(p_18995_) + RANDOM.nextDouble() * d1;
      double d5 = Math.floor(p_18996_) + RANDOM.nextDouble() * d1 + d2;

      while(!p_18997_.isEmpty()) {
         ItemEntity itementity = new ItemEntity(p_18993_, d3, d4, d5, p_18997_.split(RANDOM.nextInt(21) + 10));
         float f = 0.05F;
         itementity.setDeltaMovement(RANDOM.nextGaussian() * (double)0.05F, RANDOM.nextGaussian() * (double)0.05F + (double)0.2F, RANDOM.nextGaussian() * (double)0.05F);
         p_18993_.addFreshEntity(itementity);
      }

   }
}