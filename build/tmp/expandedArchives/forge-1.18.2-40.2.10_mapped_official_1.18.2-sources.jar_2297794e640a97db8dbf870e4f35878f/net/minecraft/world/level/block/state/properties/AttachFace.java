package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum AttachFace implements StringRepresentable {
   FLOOR("floor"),
   WALL("wall"),
   CEILING("ceiling");

   private final String name;

   private AttachFace(String p_61311_) {
      this.name = p_61311_;
   }

   public String getSerializedName() {
      return this.name;
   }
}