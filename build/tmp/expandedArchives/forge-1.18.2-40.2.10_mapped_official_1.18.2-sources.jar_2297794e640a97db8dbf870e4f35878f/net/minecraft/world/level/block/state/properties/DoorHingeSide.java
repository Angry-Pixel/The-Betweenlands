package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum DoorHingeSide implements StringRepresentable {
   LEFT,
   RIGHT;

   public String toString() {
      return this.getSerializedName();
   }

   public String getSerializedName() {
      return this == LEFT ? "left" : "right";
   }
}