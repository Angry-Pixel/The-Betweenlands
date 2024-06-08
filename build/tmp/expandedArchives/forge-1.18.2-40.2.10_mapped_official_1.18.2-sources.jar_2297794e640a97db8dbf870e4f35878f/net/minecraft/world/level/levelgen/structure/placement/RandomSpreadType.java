package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.RandomSource;

public enum RandomSpreadType implements StringRepresentable {
   LINEAR("linear"),
   TRIANGULAR("triangular");

   private static final RandomSpreadType[] VALUES = values();
   public static final Codec<RandomSpreadType> CODEC = StringRepresentable.fromEnum(() -> {
      return VALUES;
   }, RandomSpreadType::byName);
   private final String id;

   private RandomSpreadType(String p_205022_) {
      this.id = p_205022_;
   }

   public static RandomSpreadType byName(String p_205028_) {
      for(RandomSpreadType randomspreadtype : VALUES) {
         if (randomspreadtype.getSerializedName().equals(p_205028_)) {
            return randomspreadtype;
         }
      }

      throw new IllegalArgumentException("Unknown Random Spread type: " + p_205028_);
   }

   public String getSerializedName() {
      return this.id;
   }

   public int evaluate(RandomSource p_205025_, int p_205026_) {
      int i;
      switch(this) {
      case LINEAR:
         i = p_205025_.nextInt(p_205026_);
         break;
      case TRIANGULAR:
         i = (p_205025_.nextInt(p_205026_) + p_205025_.nextInt(p_205026_)) / 2;
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return i;
   }
}