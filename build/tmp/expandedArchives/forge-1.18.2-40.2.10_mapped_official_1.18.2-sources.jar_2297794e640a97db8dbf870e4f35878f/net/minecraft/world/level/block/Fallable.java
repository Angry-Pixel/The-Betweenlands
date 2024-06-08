package net.minecraft.world.level.block;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface Fallable {
   default void onLand(Level p_153220_, BlockPos p_153221_, BlockState p_153222_, BlockState p_153223_, FallingBlockEntity p_153224_) {
   }

   default void onBrokenAfterFall(Level p_153217_, BlockPos p_153218_, FallingBlockEntity p_153219_) {
   }

   default DamageSource getFallDamageSource() {
      return DamageSource.FALLING_BLOCK;
   }

   default Predicate<Entity> getHurtsEntitySelector() {
      return EntitySelector.NO_SPECTATORS;
   }
}