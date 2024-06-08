package net.minecraft.client.renderer.block.model;

import com.mojang.math.Vector3f;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockElementRotation {
   public final Vector3f origin;
   public final Direction.Axis axis;
   public final float angle;
   public final boolean rescale;

   public BlockElementRotation(Vector3f p_111383_, Direction.Axis p_111384_, float p_111385_, boolean p_111386_) {
      this.origin = p_111383_;
      this.axis = p_111384_;
      this.angle = p_111385_;
      this.rescale = p_111386_;
   }
}