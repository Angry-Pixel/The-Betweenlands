package net.minecraft.world.entity.monster.piglin;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public abstract class AbstractPiglin extends Monster {
   protected static final EntityDataAccessor<Boolean> DATA_IMMUNE_TO_ZOMBIFICATION = SynchedEntityData.defineId(AbstractPiglin.class, EntityDataSerializers.BOOLEAN);
   protected static final int CONVERSION_TIME = 300;
   protected int timeInOverworld;

   public AbstractPiglin(EntityType<? extends AbstractPiglin> p_34652_, Level p_34653_) {
      super(p_34652_, p_34653_);
      this.setCanPickUpLoot(true);
      this.applyOpenDoorsAbility();
      this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 16.0F);
      this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
   }

   private void applyOpenDoorsAbility() {
      if (GoalUtils.hasGroundPathNavigation(this)) {
         ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
      }

   }

   protected abstract boolean canHunt();

   public void setImmuneToZombification(boolean p_34671_) {
      this.getEntityData().set(DATA_IMMUNE_TO_ZOMBIFICATION, p_34671_);
   }

   protected boolean isImmuneToZombification() {
      return this.getEntityData().get(DATA_IMMUNE_TO_ZOMBIFICATION);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_IMMUNE_TO_ZOMBIFICATION, false);
   }

   public void addAdditionalSaveData(CompoundTag p_34661_) {
      super.addAdditionalSaveData(p_34661_);
      if (this.isImmuneToZombification()) {
         p_34661_.putBoolean("IsImmuneToZombification", true);
      }

      p_34661_.putInt("TimeInOverworld", this.timeInOverworld);
   }

   public double getMyRidingOffset() {
      return this.isBaby() ? -0.05D : -0.45D;
   }

   public void readAdditionalSaveData(CompoundTag p_34659_) {
      super.readAdditionalSaveData(p_34659_);
      this.setImmuneToZombification(p_34659_.getBoolean("IsImmuneToZombification"));
      this.timeInOverworld = p_34659_.getInt("TimeInOverworld");
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      if (this.isConverting()) {
         ++this.timeInOverworld;
      } else {
         this.timeInOverworld = 0;
         this.timeInOverworld = 0;
      }

      if (this.timeInOverworld > 300 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, EntityType.ZOMBIFIED_PIGLIN, (timer) -> this.timeInOverworld = timer)) {
         this.playConvertedSound();
         this.finishConversion((ServerLevel)this.level);
      }

   }

   public boolean isConverting() {
      return !this.level.dimensionType().piglinSafe() && !this.isImmuneToZombification() && !this.isNoAi();
   }

   protected void finishConversion(ServerLevel p_34663_) {
      ZombifiedPiglin zombifiedpiglin = this.convertTo(EntityType.ZOMBIFIED_PIGLIN, true);
      if (zombifiedpiglin != null) {
         zombifiedpiglin.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
         net.minecraftforge.event.ForgeEventFactory.onLivingConvert(this, zombifiedpiglin);
      }

   }

   public boolean isAdult() {
      return !this.isBaby();
   }

   public abstract PiglinArmPose getArmPose();

   @Nullable
   public LivingEntity getTarget() {
      return this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse((LivingEntity)null);
   }

   protected boolean isHoldingMeleeWeapon() {
      return this.getMainHandItem().getItem() instanceof TieredItem;
   }

   public void playAmbientSound() {
      if (PiglinAi.isIdle(this)) {
         super.playAmbientSound();
      }

   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   protected abstract void playConvertedSound();
}
