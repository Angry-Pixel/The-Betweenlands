package net.minecraft.world.level.portal;

import net.minecraft.world.phys.Vec3;

public class PortalInfo {
   public final Vec3 pos;
   public final Vec3 speed;
   public final float yRot;
   public final float xRot;

   public PortalInfo(Vec3 p_77681_, Vec3 p_77682_, float p_77683_, float p_77684_) {
      this.pos = p_77681_;
      this.speed = p_77682_;
      this.yRot = p_77683_;
      this.xRot = p_77684_;
   }
}