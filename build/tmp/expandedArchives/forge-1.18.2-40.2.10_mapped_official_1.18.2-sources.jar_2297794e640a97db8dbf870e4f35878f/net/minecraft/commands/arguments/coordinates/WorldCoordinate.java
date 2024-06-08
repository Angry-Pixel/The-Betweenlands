package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.network.chat.TranslatableComponent;

public class WorldCoordinate {
   private static final char PREFIX_RELATIVE = '~';
   public static final SimpleCommandExceptionType ERROR_EXPECTED_DOUBLE = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos.missing.double"));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_INT = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos.missing.int"));
   private final boolean relative;
   private final double value;

   public WorldCoordinate(boolean p_120864_, double p_120865_) {
      this.relative = p_120864_;
      this.value = p_120865_;
   }

   public double get(double p_120868_) {
      return this.relative ? this.value + p_120868_ : this.value;
   }

   public static WorldCoordinate parseDouble(StringReader p_120872_, boolean p_120873_) throws CommandSyntaxException {
      if (p_120872_.canRead() && p_120872_.peek() == '^') {
         throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext(p_120872_);
      } else if (!p_120872_.canRead()) {
         throw ERROR_EXPECTED_DOUBLE.createWithContext(p_120872_);
      } else {
         boolean flag = isRelative(p_120872_);
         int i = p_120872_.getCursor();
         double d0 = p_120872_.canRead() && p_120872_.peek() != ' ' ? p_120872_.readDouble() : 0.0D;
         String s = p_120872_.getString().substring(i, p_120872_.getCursor());
         if (flag && s.isEmpty()) {
            return new WorldCoordinate(true, 0.0D);
         } else {
            if (!s.contains(".") && !flag && p_120873_) {
               d0 += 0.5D;
            }

            return new WorldCoordinate(flag, d0);
         }
      }
   }

   public static WorldCoordinate parseInt(StringReader p_120870_) throws CommandSyntaxException {
      if (p_120870_.canRead() && p_120870_.peek() == '^') {
         throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext(p_120870_);
      } else if (!p_120870_.canRead()) {
         throw ERROR_EXPECTED_INT.createWithContext(p_120870_);
      } else {
         boolean flag = isRelative(p_120870_);
         double d0;
         if (p_120870_.canRead() && p_120870_.peek() != ' ') {
            d0 = flag ? p_120870_.readDouble() : (double)p_120870_.readInt();
         } else {
            d0 = 0.0D;
         }

         return new WorldCoordinate(flag, d0);
      }
   }

   public static boolean isRelative(StringReader p_120875_) {
      boolean flag;
      if (p_120875_.peek() == '~') {
         flag = true;
         p_120875_.skip();
      } else {
         flag = false;
      }

      return flag;
   }

   public boolean equals(Object p_120877_) {
      if (this == p_120877_) {
         return true;
      } else if (!(p_120877_ instanceof WorldCoordinate)) {
         return false;
      } else {
         WorldCoordinate worldcoordinate = (WorldCoordinate)p_120877_;
         if (this.relative != worldcoordinate.relative) {
            return false;
         } else {
            return Double.compare(worldcoordinate.value, this.value) == 0;
         }
      }
   }

   public int hashCode() {
      int i = this.relative ? 1 : 0;
      long j = Double.doubleToLongBits(this.value);
      return 31 * i + (int)(j ^ j >>> 32);
   }

   public boolean isRelative() {
      return this.relative;
   }
}