package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;

public class SculkSensorBlockEntity extends BlockEntity implements VibrationListener.VibrationListenerConfig {
   private final VibrationListener listener;
   private int lastVibrationFrequency;

   public SculkSensorBlockEntity(BlockPos p_155635_, BlockState p_155636_) {
      super(BlockEntityType.SCULK_SENSOR, p_155635_, p_155636_);
      this.listener = new VibrationListener(new BlockPositionSource(this.worldPosition), ((SculkSensorBlock)p_155636_.getBlock()).getListenerRange(), this);
   }

   public void load(CompoundTag p_155649_) {
      super.load(p_155649_);
      this.lastVibrationFrequency = p_155649_.getInt("last_vibration_frequency");
   }

   protected void saveAdditional(CompoundTag p_187511_) {
      super.saveAdditional(p_187511_);
      p_187511_.putInt("last_vibration_frequency", this.lastVibrationFrequency);
   }

   public VibrationListener getListener() {
      return this.listener;
   }

   public int getLastVibrationFrequency() {
      return this.lastVibrationFrequency;
   }

   public boolean shouldListen(Level p_155643_, GameEventListener p_155644_, BlockPos p_155645_, GameEvent p_155646_, @Nullable Entity p_155647_) {
      boolean flag = p_155646_ == GameEvent.BLOCK_DESTROY && p_155645_.equals(this.getBlockPos());
      boolean flag1 = p_155646_ == GameEvent.BLOCK_PLACE && p_155645_.equals(this.getBlockPos());
      return !flag && !flag1 && SculkSensorBlock.canActivate(this.getBlockState());
   }

   public void onSignalReceive(Level p_155638_, GameEventListener p_155639_, GameEvent p_155640_, int p_155641_) {
      BlockState blockstate = this.getBlockState();
      if (!p_155638_.isClientSide() && SculkSensorBlock.canActivate(blockstate)) {
         this.lastVibrationFrequency = SculkSensorBlock.VIBRATION_STRENGTH_FOR_EVENT.getInt(p_155640_);
         SculkSensorBlock.activate(p_155638_, this.worldPosition, blockstate, getRedstoneStrengthForDistance(p_155641_, p_155639_.getListenerRadius()));
      }

   }

   public static int getRedstoneStrengthForDistance(int p_155651_, int p_155652_) {
      double d0 = (double)p_155651_ / (double)p_155652_;
      return Math.max(1, 15 - Mth.floor(d0 * 15.0D));
   }
}