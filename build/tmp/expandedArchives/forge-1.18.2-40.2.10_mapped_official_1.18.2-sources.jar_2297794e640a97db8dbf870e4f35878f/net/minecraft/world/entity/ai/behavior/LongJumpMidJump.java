package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;

public class LongJumpMidJump extends Behavior<Mob> {
   public static final int TIME_OUT_DURATION = 100;
   private final UniformInt timeBetweenLongJumps;
   private SoundEvent landingSound;

   public LongJumpMidJump(UniformInt p_147596_, SoundEvent p_147597_) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryStatus.VALUE_PRESENT), 100);
      this.timeBetweenLongJumps = p_147596_;
      this.landingSound = p_147597_;
   }

   protected boolean canStillUse(ServerLevel p_147603_, Mob p_147604_, long p_147605_) {
      return !p_147604_.isOnGround();
   }

   protected void start(ServerLevel p_147611_, Mob p_147612_, long p_147613_) {
      p_147612_.setDiscardFriction(true);
      p_147612_.setPose(Pose.LONG_JUMPING);
   }

   protected void stop(ServerLevel p_147619_, Mob p_147620_, long p_147621_) {
      if (p_147620_.isOnGround()) {
         p_147620_.setDeltaMovement(p_147620_.getDeltaMovement().scale((double)0.1F));
         p_147619_.playSound((Player)null, p_147620_, this.landingSound, SoundSource.NEUTRAL, 2.0F, 1.0F);
      }

      p_147620_.setDiscardFriction(false);
      p_147620_.setPose(Pose.STANDING);
      p_147620_.getBrain().eraseMemory(MemoryModuleType.LONG_JUMP_MID_JUMP);
      p_147620_.getBrain().setMemory(MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, this.timeBetweenLongJumps.sample(p_147619_.random));
   }
}