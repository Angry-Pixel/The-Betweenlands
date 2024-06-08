package net.minecraft.world.level.storage;

import net.minecraft.core.BlockPos;

public interface WritableLevelData extends LevelData {
   void setXSpawn(int p_78651_);

   void setYSpawn(int p_78652_);

   void setZSpawn(int p_78653_);

   void setSpawnAngle(float p_78648_);

   default void setSpawn(BlockPos p_78649_, float p_78650_) {
      this.setXSpawn(p_78649_.getX());
      this.setYSpawn(p_78649_.getY());
      this.setZSpawn(p_78649_.getZ());
      this.setSpawnAngle(p_78650_);
   }
}