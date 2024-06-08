package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum BedPart implements StringRepresentable {
   HEAD("head"),
   FOOT("foot");

   private final String name;

   private BedPart(String p_61339_) {
      this.name = p_61339_;
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }
}