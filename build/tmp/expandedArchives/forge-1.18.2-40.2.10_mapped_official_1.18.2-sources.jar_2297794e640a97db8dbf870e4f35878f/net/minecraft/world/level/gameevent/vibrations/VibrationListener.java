package net.minecraft.world.level.gameevent.vibrations;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.GameEventTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class VibrationListener implements GameEventListener {
   protected final PositionSource listenerSource;
   protected final int listenerRange;
   protected final VibrationListener.VibrationListenerConfig config;
   protected Optional<GameEvent> receivingEvent = Optional.empty();
   protected int receivingDistance;
   protected int travelTimeInTicks = 0;

   public VibrationListener(PositionSource p_157894_, int p_157895_, VibrationListener.VibrationListenerConfig p_157896_) {
      this.listenerSource = p_157894_;
      this.listenerRange = p_157895_;
      this.config = p_157896_;
   }

   public void tick(Level p_157899_) {
      if (this.receivingEvent.isPresent()) {
         --this.travelTimeInTicks;
         if (this.travelTimeInTicks <= 0) {
            this.travelTimeInTicks = 0;
            this.config.onSignalReceive(p_157899_, this, this.receivingEvent.get(), this.receivingDistance);
            this.receivingEvent = Optional.empty();
         }
      }

   }

   public PositionSource getListenerSource() {
      return this.listenerSource;
   }

   public int getListenerRadius() {
      return this.listenerRange;
   }

   public boolean handleGameEvent(Level p_157901_, GameEvent p_157902_, @Nullable Entity p_157903_, BlockPos p_157904_) {
      if (!this.isValidVibration(p_157902_, p_157903_)) {
         return false;
      } else {
         Optional<BlockPos> optional = this.listenerSource.getPosition(p_157901_);
         if (!optional.isPresent()) {
            return false;
         } else {
            BlockPos blockpos = optional.get();
            if (!this.config.shouldListen(p_157901_, this, p_157904_, p_157902_, p_157903_)) {
               return false;
            } else if (this.isOccluded(p_157901_, p_157904_, blockpos)) {
               return false;
            } else {
               this.sendSignal(p_157901_, p_157902_, p_157904_, blockpos);
               return true;
            }
         }
      }
   }

   private boolean isValidVibration(GameEvent p_157917_, @Nullable Entity p_157918_) {
      if (this.receivingEvent.isPresent()) {
         return false;
      } else if (!p_157917_.is(GameEventTags.VIBRATIONS)) {
         return false;
      } else {
         if (p_157918_ != null) {
            if (p_157917_.is(GameEventTags.IGNORE_VIBRATIONS_SNEAKING) && p_157918_.isSteppingCarefully()) {
               return false;
            }

            if (p_157918_.occludesVibrations()) {
               return false;
            }
         }

         return p_157918_ == null || !p_157918_.isSpectator();
      }
   }

   private void sendSignal(Level p_157906_, GameEvent p_157907_, BlockPos p_157908_, BlockPos p_157909_) {
      this.receivingEvent = Optional.of(p_157907_);
      if (p_157906_ instanceof ServerLevel) {
         this.receivingDistance = Mth.floor(Math.sqrt(p_157908_.distSqr(p_157909_)));
         this.travelTimeInTicks = this.receivingDistance;
         ((ServerLevel)p_157906_).sendVibrationParticle(new VibrationPath(p_157908_, this.listenerSource, this.travelTimeInTicks));
      }

   }

   private boolean isOccluded(Level p_157911_, BlockPos p_157912_, BlockPos p_157913_) {
      return p_157911_.isBlockInLine(new ClipBlockStateContext(Vec3.atCenterOf(p_157912_), Vec3.atCenterOf(p_157913_), (p_157915_) -> {
         return p_157915_.is(BlockTags.OCCLUDES_VIBRATION_SIGNALS);
      })).getType() == HitResult.Type.BLOCK;
   }

   public interface VibrationListenerConfig {
      boolean shouldListen(Level p_157924_, GameEventListener p_157925_, BlockPos p_157926_, GameEvent p_157927_, @Nullable Entity p_157928_);

      void onSignalReceive(Level p_157920_, GameEventListener p_157921_, GameEvent p_157922_, int p_157923_);
   }
}