package net.minecraft.commands.arguments.coordinates;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public interface Coordinates {
   Vec3 getPosition(CommandSourceStack p_119566_);

   Vec2 getRotation(CommandSourceStack p_119567_);

   default BlockPos getBlockPos(CommandSourceStack p_119569_) {
      return new BlockPos(this.getPosition(p_119569_));
   }

   boolean isXRelative();

   boolean isYRelative();

   boolean isZRelative();
}