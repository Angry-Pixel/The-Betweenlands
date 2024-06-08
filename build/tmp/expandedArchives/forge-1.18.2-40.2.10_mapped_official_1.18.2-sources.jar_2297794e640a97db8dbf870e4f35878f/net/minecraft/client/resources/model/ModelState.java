package net.minecraft.client.resources.model;

import com.mojang.math.Transformation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ModelState extends net.minecraftforge.client.extensions.IForgeModelState {
   default Transformation getRotation() {
      return Transformation.identity();
   }

   default boolean isUvLocked() {
      return false;
   }
}
