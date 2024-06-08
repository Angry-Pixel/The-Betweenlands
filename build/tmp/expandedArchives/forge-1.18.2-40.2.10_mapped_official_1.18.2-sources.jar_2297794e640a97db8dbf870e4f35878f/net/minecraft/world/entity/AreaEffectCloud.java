package net.minecraft.world.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import org.slf4j.Logger;

public class AreaEffectCloud extends Entity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int TIME_BETWEEN_APPLICATIONS = 5;
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_WAITING = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<ParticleOptions> DATA_PARTICLE = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.PARTICLE);
   private static final float MAX_RADIUS = 32.0F;
   private Potion potion = Potions.EMPTY;
   private final List<MobEffectInstance> effects = Lists.newArrayList();
   private final Map<Entity, Integer> victims = Maps.newHashMap();
   private int duration = 600;
   private int waitTime = 20;
   private int reapplicationDelay = 20;
   private boolean fixedColor;
   private int durationOnUse;
   private float radiusOnUse;
   private float radiusPerTick;
   @Nullable
   private LivingEntity owner;
   @Nullable
   private UUID ownerUUID;

   public AreaEffectCloud(EntityType<? extends AreaEffectCloud> p_19704_, Level p_19705_) {
      super(p_19704_, p_19705_);
      this.noPhysics = true;
      this.setRadius(3.0F);
   }

   public AreaEffectCloud(Level p_19707_, double p_19708_, double p_19709_, double p_19710_) {
      this(EntityType.AREA_EFFECT_CLOUD, p_19707_);
      this.setPos(p_19708_, p_19709_, p_19710_);
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_COLOR, 0);
      this.getEntityData().define(DATA_RADIUS, 0.5F);
      this.getEntityData().define(DATA_WAITING, false);
      this.getEntityData().define(DATA_PARTICLE, ParticleTypes.ENTITY_EFFECT);
   }

   public void setRadius(float p_19713_) {
      if (!this.level.isClientSide) {
         this.getEntityData().set(DATA_RADIUS, Mth.clamp(p_19713_, 0.0F, 32.0F));
      }

   }

   public void refreshDimensions() {
      double d0 = this.getX();
      double d1 = this.getY();
      double d2 = this.getZ();
      super.refreshDimensions();
      this.setPos(d0, d1, d2);
   }

   public float getRadius() {
      return this.getEntityData().get(DATA_RADIUS);
   }

   public void setPotion(Potion p_19723_) {
      this.potion = p_19723_;
      if (!this.fixedColor) {
         this.updateColor();
      }

   }

   private void updateColor() {
      if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
         this.getEntityData().set(DATA_COLOR, 0);
      } else {
         this.getEntityData().set(DATA_COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
      }

   }

   public void addEffect(MobEffectInstance p_19717_) {
      this.effects.add(p_19717_);
      if (!this.fixedColor) {
         this.updateColor();
      }

   }

   public int getColor() {
      return this.getEntityData().get(DATA_COLOR);
   }

   public void setFixedColor(int p_19715_) {
      this.fixedColor = true;
      this.getEntityData().set(DATA_COLOR, p_19715_);
   }

   public ParticleOptions getParticle() {
      return this.getEntityData().get(DATA_PARTICLE);
   }

   public void setParticle(ParticleOptions p_19725_) {
      this.getEntityData().set(DATA_PARTICLE, p_19725_);
   }

   protected void setWaiting(boolean p_19731_) {
      this.getEntityData().set(DATA_WAITING, p_19731_);
   }

   public boolean isWaiting() {
      return this.getEntityData().get(DATA_WAITING);
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int p_19735_) {
      this.duration = p_19735_;
   }

   public void tick() {
      super.tick();
      boolean flag = this.isWaiting();
      float f = this.getRadius();
      if (this.level.isClientSide) {
         if (flag && this.random.nextBoolean()) {
            return;
         }

         ParticleOptions particleoptions = this.getParticle();
         int i;
         float f1;
         if (flag) {
            i = 2;
            f1 = 0.2F;
         } else {
            i = Mth.ceil((float)Math.PI * f * f);
            f1 = f;
         }

         for(int j = 0; j < i; ++j) {
            float f2 = this.random.nextFloat() * ((float)Math.PI * 2F);
            float f3 = Mth.sqrt(this.random.nextFloat()) * f1;
            double d0 = this.getX() + (double)(Mth.cos(f2) * f3);
            double d2 = this.getY();
            double d4 = this.getZ() + (double)(Mth.sin(f2) * f3);
            double d5;
            double d6;
            double d7;
            if (particleoptions.getType() != ParticleTypes.ENTITY_EFFECT) {
               if (flag) {
                  d5 = 0.0D;
                  d6 = 0.0D;
                  d7 = 0.0D;
               } else {
                  d5 = (0.5D - this.random.nextDouble()) * 0.15D;
                  d6 = (double)0.01F;
                  d7 = (0.5D - this.random.nextDouble()) * 0.15D;
               }
            } else {
               int k = flag && this.random.nextBoolean() ? 16777215 : this.getColor();
               d5 = (double)((float)(k >> 16 & 255) / 255.0F);
               d6 = (double)((float)(k >> 8 & 255) / 255.0F);
               d7 = (double)((float)(k & 255) / 255.0F);
            }

            this.level.addAlwaysVisibleParticle(particleoptions, d0, d2, d4, d5, d6, d7);
         }
      } else {
         if (this.tickCount >= this.waitTime + this.duration) {
            this.discard();
            return;
         }

         boolean flag1 = this.tickCount < this.waitTime;
         if (flag != flag1) {
            this.setWaiting(flag1);
         }

         if (flag1) {
            return;
         }

         if (this.radiusPerTick != 0.0F) {
            f += this.radiusPerTick;
            if (f < 0.5F) {
               this.discard();
               return;
            }

            this.setRadius(f);
         }

         if (this.tickCount % 5 == 0) {
            this.victims.entrySet().removeIf((p_146784_) -> {
               return this.tickCount >= p_146784_.getValue();
            });
            List<MobEffectInstance> list = Lists.newArrayList();

            for(MobEffectInstance mobeffectinstance : this.potion.getEffects()) {
               list.add(new MobEffectInstance(mobeffectinstance.getEffect(), mobeffectinstance.getDuration() / 4, mobeffectinstance.getAmplifier(), mobeffectinstance.isAmbient(), mobeffectinstance.isVisible()));
            }

            list.addAll(this.effects);
            if (list.isEmpty()) {
               this.victims.clear();
            } else {
               List<LivingEntity> list1 = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
               if (!list1.isEmpty()) {
                  for(LivingEntity livingentity : list1) {
                     if (!this.victims.containsKey(livingentity) && livingentity.isAffectedByPotions()) {
                        double d8 = livingentity.getX() - this.getX();
                        double d1 = livingentity.getZ() - this.getZ();
                        double d3 = d8 * d8 + d1 * d1;
                        if (d3 <= (double)(f * f)) {
                           this.victims.put(livingentity, this.tickCount + this.reapplicationDelay);

                           for(MobEffectInstance mobeffectinstance1 : list) {
                              if (mobeffectinstance1.getEffect().isInstantenous()) {
                                 mobeffectinstance1.getEffect().applyInstantenousEffect(this, this.getOwner(), livingentity, mobeffectinstance1.getAmplifier(), 0.5D);
                              } else {
                                 livingentity.addEffect(new MobEffectInstance(mobeffectinstance1), this);
                              }
                           }

                           if (this.radiusOnUse != 0.0F) {
                              f += this.radiusOnUse;
                              if (f < 0.5F) {
                                 this.discard();
                                 return;
                              }

                              this.setRadius(f);
                           }

                           if (this.durationOnUse != 0) {
                              this.duration += this.durationOnUse;
                              if (this.duration <= 0) {
                                 this.discard();
                                 return;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public float getRadiusOnUse() {
      return this.radiusOnUse;
   }

   public void setRadiusOnUse(float p_19733_) {
      this.radiusOnUse = p_19733_;
   }

   public float getRadiusPerTick() {
      return this.radiusPerTick;
   }

   public void setRadiusPerTick(float p_19739_) {
      this.radiusPerTick = p_19739_;
   }

   public int getDurationOnUse() {
      return this.durationOnUse;
   }

   public void setDurationOnUse(int p_146786_) {
      this.durationOnUse = p_146786_;
   }

   public int getWaitTime() {
      return this.waitTime;
   }

   public void setWaitTime(int p_19741_) {
      this.waitTime = p_19741_;
   }

   public void setOwner(@Nullable LivingEntity p_19719_) {
      this.owner = p_19719_;
      this.ownerUUID = p_19719_ == null ? null : p_19719_.getUUID();
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.owner == null && this.ownerUUID != null && this.level instanceof ServerLevel) {
         Entity entity = ((ServerLevel)this.level).getEntity(this.ownerUUID);
         if (entity instanceof LivingEntity) {
            this.owner = (LivingEntity)entity;
         }
      }

      return this.owner;
   }

   protected void readAdditionalSaveData(CompoundTag p_19727_) {
      this.tickCount = p_19727_.getInt("Age");
      this.duration = p_19727_.getInt("Duration");
      this.waitTime = p_19727_.getInt("WaitTime");
      this.reapplicationDelay = p_19727_.getInt("ReapplicationDelay");
      this.durationOnUse = p_19727_.getInt("DurationOnUse");
      this.radiusOnUse = p_19727_.getFloat("RadiusOnUse");
      this.radiusPerTick = p_19727_.getFloat("RadiusPerTick");
      this.setRadius(p_19727_.getFloat("Radius"));
      if (p_19727_.hasUUID("Owner")) {
         this.ownerUUID = p_19727_.getUUID("Owner");
      }

      if (p_19727_.contains("Particle", 8)) {
         try {
            this.setParticle(ParticleArgument.readParticle(new StringReader(p_19727_.getString("Particle"))));
         } catch (CommandSyntaxException commandsyntaxexception) {
            LOGGER.warn("Couldn't load custom particle {}", p_19727_.getString("Particle"), commandsyntaxexception);
         }
      }

      if (p_19727_.contains("Color", 99)) {
         this.setFixedColor(p_19727_.getInt("Color"));
      }

      if (p_19727_.contains("Potion", 8)) {
         this.setPotion(PotionUtils.getPotion(p_19727_));
      }

      if (p_19727_.contains("Effects", 9)) {
         ListTag listtag = p_19727_.getList("Effects", 10);
         this.effects.clear();

         for(int i = 0; i < listtag.size(); ++i) {
            MobEffectInstance mobeffectinstance = MobEffectInstance.load(listtag.getCompound(i));
            if (mobeffectinstance != null) {
               this.addEffect(mobeffectinstance);
            }
         }
      }

   }

   protected void addAdditionalSaveData(CompoundTag p_19737_) {
      p_19737_.putInt("Age", this.tickCount);
      p_19737_.putInt("Duration", this.duration);
      p_19737_.putInt("WaitTime", this.waitTime);
      p_19737_.putInt("ReapplicationDelay", this.reapplicationDelay);
      p_19737_.putInt("DurationOnUse", this.durationOnUse);
      p_19737_.putFloat("RadiusOnUse", this.radiusOnUse);
      p_19737_.putFloat("RadiusPerTick", this.radiusPerTick);
      p_19737_.putFloat("Radius", this.getRadius());
      p_19737_.putString("Particle", this.getParticle().writeToString());
      if (this.ownerUUID != null) {
         p_19737_.putUUID("Owner", this.ownerUUID);
      }

      if (this.fixedColor) {
         p_19737_.putInt("Color", this.getColor());
      }

      if (this.potion != Potions.EMPTY) {
         p_19737_.putString("Potion", Registry.POTION.getKey(this.potion).toString());
      }

      if (!this.effects.isEmpty()) {
         ListTag listtag = new ListTag();

         for(MobEffectInstance mobeffectinstance : this.effects) {
            listtag.add(mobeffectinstance.save(new CompoundTag()));
         }

         p_19737_.put("Effects", listtag);
      }

   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> p_19729_) {
      if (DATA_RADIUS.equals(p_19729_)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(p_19729_);
   }

   public Potion getPotion() {
      return this.potion;
   }

   public PushReaction getPistonPushReaction() {
      return PushReaction.IGNORE;
   }

   public Packet<?> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }

   public EntityDimensions getDimensions(Pose p_19721_) {
      return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
   }
}