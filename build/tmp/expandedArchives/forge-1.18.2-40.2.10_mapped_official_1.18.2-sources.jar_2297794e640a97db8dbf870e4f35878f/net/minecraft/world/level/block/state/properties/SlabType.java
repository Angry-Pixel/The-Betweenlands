package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum SlabType implements StringRepresentable {
   TOP("top"),
   BOTTOM("bottom"),
   DOUBLE("double");

   private final String name;

   private SlabType(String p_61775_) {
      this.name = p_61775_;
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }
}