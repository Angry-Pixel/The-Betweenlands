package net.minecraft.data.models.blockstates;

import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;

public class VariantProperties {
   public static final VariantProperty<VariantProperties.Rotation> X_ROT = new VariantProperty<>("x", (p_125529_) -> {
      return new JsonPrimitive(p_125529_.value);
   });
   public static final VariantProperty<VariantProperties.Rotation> Y_ROT = new VariantProperty<>("y", (p_125525_) -> {
      return new JsonPrimitive(p_125525_.value);
   });
   public static final VariantProperty<ResourceLocation> MODEL = new VariantProperty<>("model", (p_125527_) -> {
      return new JsonPrimitive(p_125527_.toString());
   });
   public static final VariantProperty<Boolean> UV_LOCK = new VariantProperty<>("uvlock", JsonPrimitive::new);
   public static final VariantProperty<Integer> WEIGHT = new VariantProperty<>("weight", JsonPrimitive::new);

   public static enum Rotation {
      R0(0),
      R90(90),
      R180(180),
      R270(270);

      final int value;

      private Rotation(int p_125540_) {
         this.value = p_125540_;
      }
   }
}