package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class WeightedPressurePlateBlock extends BasePressurePlateBlock {
   public static final IntegerProperty POWER = BlockStateProperties.POWER;
   private final int maxWeight;

   public WeightedPressurePlateBlock(int p_58202_, BlockBehaviour.Properties p_58203_) {
      super(p_58203_);
      this.registerDefaultState(this.stateDefinition.any().setValue(POWER, Integer.valueOf(0)));
      this.maxWeight = p_58202_;
   }

   protected int getSignalStrength(Level p_58213_, BlockPos p_58214_) {
      int i = Math.min(p_58213_.getEntitiesOfClass(Entity.class, TOUCH_AABB.move(p_58214_)).size(), this.maxWeight);
      if (i > 0) {
         float f = (float)Math.min(this.maxWeight, i) / (float)this.maxWeight;
         return Mth.ceil(f * 15.0F);
      } else {
         return 0;
      }
   }

   protected void playOnSound(LevelAccessor p_58205_, BlockPos p_58206_) {
      p_58205_.playSound((Player)null, p_58206_, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.90000004F);
   }

   protected void playOffSound(LevelAccessor p_58216_, BlockPos p_58217_) {
      p_58216_.playSound((Player)null, p_58217_, SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.75F);
   }

   protected int getSignalForState(BlockState p_58220_) {
      return p_58220_.getValue(POWER);
   }

   protected BlockState setSignalForState(BlockState p_58208_, int p_58209_) {
      return p_58208_.setValue(POWER, Integer.valueOf(p_58209_));
   }

   protected int getPressedTime() {
      return 10;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_58211_) {
      p_58211_.add(POWER);
   }
}