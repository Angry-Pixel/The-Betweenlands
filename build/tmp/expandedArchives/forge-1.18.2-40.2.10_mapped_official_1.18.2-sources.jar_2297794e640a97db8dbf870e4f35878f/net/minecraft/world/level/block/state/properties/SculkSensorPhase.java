package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum SculkSensorPhase implements StringRepresentable {
   INACTIVE("inactive"),
   ACTIVE("active"),
   COOLDOWN("cooldown");

   private final String name;

   private SculkSensorPhase(String p_156050_) {
      this.name = p_156050_;
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }
}