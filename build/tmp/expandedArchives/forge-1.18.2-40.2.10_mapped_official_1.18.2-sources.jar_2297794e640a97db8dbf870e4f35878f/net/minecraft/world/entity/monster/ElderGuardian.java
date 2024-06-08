package net.minecraft.world.entity.monster;

import java.util.List;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class ElderGuardian extends Guardian {
   public static final float ELDER_SIZE_SCALE = EntityType.ELDER_GUARDIAN.getWidth() / EntityType.GUARDIAN.getWidth();

   public ElderGuardian(EntityType<? extends ElderGuardian> p_32460_, Level p_32461_) {
      super(p_32460_, p_32461_);
      this.setPersistenceRequired();
      if (this.randomStrollGoal != null) {
         this.randomStrollGoal.setInterval(400);
      }

   }

   public static AttributeSupplier.Builder createAttributes() {
      return Guardian.createAttributes().add(Attributes.MOVEMENT_SPEED, (double)0.3F).add(Attributes.ATTACK_DAMAGE, 8.0D).add(Attributes.MAX_HEALTH, 80.0D);
   }

   public int getAttackDuration() {
      return 60;
   }

   protected SoundEvent getAmbientSound() {
      return this.isInWaterOrBubble() ? SoundEvents.ELDER_GUARDIAN_AMBIENT : SoundEvents.ELDER_GUARDIAN_AMBIENT_LAND;
   }

   protected SoundEvent getHurtSound(DamageSource p_32468_) {
      return this.isInWaterOrBubble() ? SoundEvents.ELDER_GUARDIAN_HURT : SoundEvents.ELDER_GUARDIAN_HURT_LAND;
   }

   protected SoundEvent getDeathSound() {
      return this.isInWaterOrBubble() ? SoundEvents.ELDER_GUARDIAN_DEATH : SoundEvents.ELDER_GUARDIAN_DEATH_LAND;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ELDER_GUARDIAN_FLOP;
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      int i = 1200;
      if ((this.tickCount + this.getId()) % 1200 == 0) {
         MobEffect mobeffect = MobEffects.DIG_SLOWDOWN;
         List<ServerPlayer> list = ((ServerLevel)this.level).getPlayers((p_32465_) -> {
            return this.distanceToSqr(p_32465_) < 2500.0D && p_32465_.gameMode.isSurvival();
         });
         int j = 2;
         int k = 6000;
         int l = 1200;

         for(ServerPlayer serverplayer : list) {
            if (!serverplayer.hasEffect(mobeffect) || serverplayer.getEffect(mobeffect).getAmplifier() < 2 || serverplayer.getEffect(mobeffect).getDuration() < 1200) {
               serverplayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT, this.isSilent() ? 0.0F : 1.0F));
               serverplayer.addEffect(new MobEffectInstance(mobeffect, 6000, 2), this);
            }
         }
      }

      if (!this.hasRestriction()) {
         this.restrictTo(this.blockPosition(), 16);
      }

   }
}