package net.minecraft.world.level.block.entity;

import net.minecraft.world.Container;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface Hopper extends Container {
   VoxelShape INSIDE = Block.box(2.0D, 11.0D, 2.0D, 14.0D, 16.0D, 14.0D);
   VoxelShape ABOVE = Block.box(0.0D, 16.0D, 0.0D, 16.0D, 32.0D, 16.0D);
   VoxelShape SUCK = Shapes.or(INSIDE, ABOVE);

   default VoxelShape getSuckShape() {
      return SUCK;
   }

   double getLevelX();

   double getLevelY();

   double getLevelZ();
}