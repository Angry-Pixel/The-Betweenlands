package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class HayBlock extends RotatedPillarBlock {
   public HayBlock(BlockBehaviour.Properties p_53976_) {
      super(p_53976_);
      this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y));
   }

   public void fallOn(Level p_153362_, BlockState p_153363_, BlockPos p_153364_, Entity p_153365_, float p_153366_) {
      p_153365_.causeFallDamage(p_153366_, 0.2F, DamageSource.FALL);
   }
}