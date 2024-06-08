package net.minecraft.world.level.block.piston;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

public class PistonMath {
   public static AABB getMovementArea(AABB p_60329_, Direction p_60330_, double p_60331_) {
      double d0 = p_60331_ * (double)p_60330_.getAxisDirection().getStep();
      double d1 = Math.min(d0, 0.0D);
      double d2 = Math.max(d0, 0.0D);
      switch(p_60330_) {
      case WEST:
         return new AABB(p_60329_.minX + d1, p_60329_.minY, p_60329_.minZ, p_60329_.minX + d2, p_60329_.maxY, p_60329_.maxZ);
      case EAST:
         return new AABB(p_60329_.maxX + d1, p_60329_.minY, p_60329_.minZ, p_60329_.maxX + d2, p_60329_.maxY, p_60329_.maxZ);
      case DOWN:
         return new AABB(p_60329_.minX, p_60329_.minY + d1, p_60329_.minZ, p_60329_.maxX, p_60329_.minY + d2, p_60329_.maxZ);
      case UP:
      default:
         return new AABB(p_60329_.minX, p_60329_.maxY + d1, p_60329_.minZ, p_60329_.maxX, p_60329_.maxY + d2, p_60329_.maxZ);
      case NORTH:
         return new AABB(p_60329_.minX, p_60329_.minY, p_60329_.minZ + d1, p_60329_.maxX, p_60329_.maxY, p_60329_.minZ + d2);
      case SOUTH:
         return new AABB(p_60329_.minX, p_60329_.minY, p_60329_.maxZ + d1, p_60329_.maxX, p_60329_.maxY, p_60329_.maxZ + d2);
      }
   }
}