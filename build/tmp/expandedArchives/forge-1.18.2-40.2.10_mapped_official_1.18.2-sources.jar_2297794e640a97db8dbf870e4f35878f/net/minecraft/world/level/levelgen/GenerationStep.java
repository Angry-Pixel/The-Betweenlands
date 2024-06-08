package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.StringRepresentable;

public class GenerationStep {
   public static enum Carving implements StringRepresentable {
      AIR("air"),
      LIQUID("liquid");

      public static final Codec<GenerationStep.Carving> CODEC = StringRepresentable.fromEnum(GenerationStep.Carving::values, GenerationStep.Carving::byName);
      private static final Map<String, GenerationStep.Carving> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(GenerationStep.Carving::getName, (p_64205_) -> {
         return p_64205_;
      }));
      private final String name;

      private Carving(String p_64202_) {
         this.name = p_64202_;
      }

      public String getName() {
         return this.name;
      }

      @Nullable
      public static GenerationStep.Carving byName(String p_64207_) {
         return BY_NAME.get(p_64207_);
      }

      public String getSerializedName() {
         return this.name;
      }
   }

   public static enum Decoration {
      RAW_GENERATION,
      LAKES,
      LOCAL_MODIFICATIONS,
      UNDERGROUND_STRUCTURES,
      SURFACE_STRUCTURES,
      STRONGHOLDS,
      UNDERGROUND_ORES,
      UNDERGROUND_DECORATION,
      FLUID_SPRINGS,
      VEGETAL_DECORATION,
      TOP_LAYER_MODIFICATION;
   }
}