package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum RedstoneSide implements StringRepresentable {
   UP("up"),
   SIDE("side"),
   NONE("none");

   private final String name;

   private RedstoneSide(String p_61759_) {
      this.name = p_61759_;
   }

   public String toString() {
      return this.getSerializedName();
   }

   public String getSerializedName() {
      return this.name;
   }

   public boolean isConnected() {
      return this != NONE;
   }
}