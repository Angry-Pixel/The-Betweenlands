package net.minecraft.world.level;

import com.mojang.logging.LogUtils;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public abstract class BaseSpawner {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int EVENT_SPAWN = 1;
   private int spawnDelay = 20;
   private SimpleWeightedRandomList<SpawnData> spawnPotentials = SimpleWeightedRandomList.empty();
   private SpawnData nextSpawnData = new SpawnData();
   private double spin;
   private double oSpin;
   private int minSpawnDelay = 200;
   private int maxSpawnDelay = 800;
   private int spawnCount = 4;
   @Nullable
   private Entity displayEntity;
   private int maxNearbyEntities = 6;
   private int requiredPlayerRange = 16;
   private int spawnRange = 4;
   private final Random random = new Random();

   public void setEntityId(EntityType<?> p_45463_) {
      this.nextSpawnData.getEntityToSpawn().putString("id", Registry.ENTITY_TYPE.getKey(p_45463_).toString());
   }

   private boolean isNearPlayer(Level p_151344_, BlockPos p_151345_) {
      return p_151344_.hasNearbyAlivePlayer((double)p_151345_.getX() + 0.5D, (double)p_151345_.getY() + 0.5D, (double)p_151345_.getZ() + 0.5D, (double)this.requiredPlayerRange);
   }

   public void clientTick(Level p_151320_, BlockPos p_151321_) {
      if (!this.isNearPlayer(p_151320_, p_151321_)) {
         this.oSpin = this.spin;
      } else {
         double d0 = (double)p_151321_.getX() + p_151320_.random.nextDouble();
         double d1 = (double)p_151321_.getY() + p_151320_.random.nextDouble();
         double d2 = (double)p_151321_.getZ() + p_151320_.random.nextDouble();
         p_151320_.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
         p_151320_.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
         if (this.spawnDelay > 0) {
            --this.spawnDelay;
         }

         this.oSpin = this.spin;
         this.spin = (this.spin + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0D;
      }

   }

   public void serverTick(ServerLevel p_151312_, BlockPos p_151313_) {
      if (this.isNearPlayer(p_151312_, p_151313_)) {
         if (this.spawnDelay == -1) {
            this.delay(p_151312_, p_151313_);
         }

         if (this.spawnDelay > 0) {
            --this.spawnDelay;
         } else {
            boolean flag = false;

            for(int i = 0; i < this.spawnCount; ++i) {
               CompoundTag compoundtag = this.nextSpawnData.getEntityToSpawn();
               Optional<EntityType<?>> optional = EntityType.by(compoundtag);
               if (optional.isEmpty()) {
                  this.delay(p_151312_, p_151313_);
                  return;
               }

               ListTag listtag = compoundtag.getList("Pos", 6);
               int j = listtag.size();
               double d0 = j >= 1 ? listtag.getDouble(0) : (double)p_151313_.getX() + (p_151312_.random.nextDouble() - p_151312_.random.nextDouble()) * (double)this.spawnRange + 0.5D;
               double d1 = j >= 2 ? listtag.getDouble(1) : (double)(p_151313_.getY() + p_151312_.random.nextInt(3) - 1);
               double d2 = j >= 3 ? listtag.getDouble(2) : (double)p_151313_.getZ() + (p_151312_.random.nextDouble() - p_151312_.random.nextDouble()) * (double)this.spawnRange + 0.5D;
               if (p_151312_.noCollision(optional.get().getAABB(d0, d1, d2))) {
                  BlockPos blockpos = new BlockPos(d0, d1, d2);
                  if (this.nextSpawnData.getCustomSpawnRules().isPresent()) {
                     if (!optional.get().getCategory().isFriendly() && p_151312_.getDifficulty() == Difficulty.PEACEFUL) {
                        continue;
                     }

                     SpawnData.CustomSpawnRules spawndata$customspawnrules = this.nextSpawnData.getCustomSpawnRules().get();
                     if (!spawndata$customspawnrules.blockLightLimit().isValueInRange(p_151312_.getBrightness(LightLayer.BLOCK, blockpos)) || !spawndata$customspawnrules.skyLightLimit().isValueInRange(p_151312_.getBrightness(LightLayer.SKY, blockpos))) {
                        continue;
                     }
                  } else if (!SpawnPlacements.checkSpawnRules(optional.get(), p_151312_, MobSpawnType.SPAWNER, blockpos, p_151312_.getRandom())) {
                     continue;
                  }

                  Entity entity = EntityType.loadEntityRecursive(compoundtag, p_151312_, (p_151310_) -> {
                     p_151310_.moveTo(d0, d1, d2, p_151310_.getYRot(), p_151310_.getXRot());
                     return p_151310_;
                  });
                  if (entity == null) {
                     this.delay(p_151312_, p_151313_);
                     return;
                  }

                  int k = p_151312_.getEntitiesOfClass(entity.getClass(), (new AABB((double)p_151313_.getX(), (double)p_151313_.getY(), (double)p_151313_.getZ(), (double)(p_151313_.getX() + 1), (double)(p_151313_.getY() + 1), (double)(p_151313_.getZ() + 1))).inflate((double)this.spawnRange)).size();
                  if (k >= this.maxNearbyEntities) {
                     this.delay(p_151312_, p_151313_);
                     return;
                  }

                  entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), p_151312_.random.nextFloat() * 360.0F, 0.0F);
                  if (entity instanceof Mob) {
                     Mob mob = (Mob)entity;
                     net.minecraftforge.eventbus.api.Event.Result res = net.minecraftforge.event.ForgeEventFactory.canEntitySpawn(mob, p_151312_, (float)entity.getX(), (float)entity.getY(), (float)entity.getZ(), this, MobSpawnType.SPAWNER);
                     if (res == net.minecraftforge.eventbus.api.Event.Result.DENY) continue;
                     if (res == net.minecraftforge.eventbus.api.Event.Result.DEFAULT)
                     if (this.nextSpawnData.getCustomSpawnRules().isEmpty() && !mob.checkSpawnRules(p_151312_, MobSpawnType.SPAWNER) || !mob.checkSpawnObstruction(p_151312_)) {
                        continue;
                     }

                     if (this.nextSpawnData.getEntityToSpawn().size() == 1 && this.nextSpawnData.getEntityToSpawn().contains("id", 8)) {
                        if (!net.minecraftforge.event.ForgeEventFactory.doSpecialSpawn(mob, (LevelAccessor)p_151312_, (float)entity.getX(), (float)entity.getY(), (float)entity.getZ(), this, MobSpawnType.SPAWNER))
                        ((Mob)entity).finalizeSpawn(p_151312_, p_151312_.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.SPAWNER, (SpawnGroupData)null, (CompoundTag)null);
                     }
                  }

                  if (!p_151312_.tryAddFreshEntityWithPassengers(entity)) {
                     this.delay(p_151312_, p_151313_);
                     return;
                  }

                  p_151312_.levelEvent(2004, p_151313_, 0);
                  if (entity instanceof Mob) {
                     ((Mob)entity).spawnAnim();
                  }

                  flag = true;
               }
            }

            if (flag) {
               this.delay(p_151312_, p_151313_);
            }

         }
      }
   }

   private void delay(Level p_151351_, BlockPos p_151352_) {
      if (this.maxSpawnDelay <= this.minSpawnDelay) {
         this.spawnDelay = this.minSpawnDelay;
      } else {
         this.spawnDelay = this.minSpawnDelay + this.random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
      }

      this.spawnPotentials.getRandom(this.random).ifPresent((p_186386_) -> {
         this.setNextSpawnData(p_151351_, p_151352_, p_186386_.getData());
      });
      this.broadcastEvent(p_151351_, p_151352_, 1);
   }

   public void load(@Nullable Level p_151329_, BlockPos p_151330_, CompoundTag p_151331_) {
      this.spawnDelay = p_151331_.getShort("Delay");
      boolean flag = p_151331_.contains("SpawnPotentials", 9);
      boolean flag1 = p_151331_.contains("SpawnData", 10);
      if (!flag) {
         SpawnData spawndata;
         if (flag1) {
            spawndata = SpawnData.CODEC.parse(NbtOps.INSTANCE, p_151331_.getCompound("SpawnData")).resultOrPartial((p_186391_) -> {
               LOGGER.warn("Invalid SpawnData: {}", (Object)p_186391_);
            }).orElseGet(SpawnData::new);
         } else {
            spawndata = new SpawnData();
         }

         this.spawnPotentials = SimpleWeightedRandomList.single(spawndata);
         this.setNextSpawnData(p_151329_, p_151330_, spawndata);
      } else {
         ListTag listtag = p_151331_.getList("SpawnPotentials", 10);
         this.spawnPotentials = SpawnData.LIST_CODEC.parse(NbtOps.INSTANCE, listtag).resultOrPartial((p_186388_) -> {
            LOGGER.warn("Invalid SpawnPotentials list: {}", (Object)p_186388_);
         }).orElseGet(SimpleWeightedRandomList::empty);
         if (flag1) {
            SpawnData spawndata1 = SpawnData.CODEC.parse(NbtOps.INSTANCE, p_151331_.getCompound("SpawnData")).resultOrPartial((p_186380_) -> {
               LOGGER.warn("Invalid SpawnData: {}", (Object)p_186380_);
            }).orElseGet(SpawnData::new);
            this.setNextSpawnData(p_151329_, p_151330_, spawndata1);
         } else {
            this.spawnPotentials.getRandom(this.random).ifPresent((p_186378_) -> {
               this.setNextSpawnData(p_151329_, p_151330_, p_186378_.getData());
            });
         }
      }

      if (p_151331_.contains("MinSpawnDelay", 99)) {
         this.minSpawnDelay = p_151331_.getShort("MinSpawnDelay");
         this.maxSpawnDelay = p_151331_.getShort("MaxSpawnDelay");
         this.spawnCount = p_151331_.getShort("SpawnCount");
      }

      if (p_151331_.contains("MaxNearbyEntities", 99)) {
         this.maxNearbyEntities = p_151331_.getShort("MaxNearbyEntities");
         this.requiredPlayerRange = p_151331_.getShort("RequiredPlayerRange");
      }

      if (p_151331_.contains("SpawnRange", 99)) {
         this.spawnRange = p_151331_.getShort("SpawnRange");
      }

      this.displayEntity = null;
   }

   public CompoundTag save(CompoundTag p_186382_) {
      p_186382_.putShort("Delay", (short)this.spawnDelay);
      p_186382_.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
      p_186382_.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
      p_186382_.putShort("SpawnCount", (short)this.spawnCount);
      p_186382_.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
      p_186382_.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
      p_186382_.putShort("SpawnRange", (short)this.spawnRange);
      p_186382_.put("SpawnData", SpawnData.CODEC.encodeStart(NbtOps.INSTANCE, this.nextSpawnData).result().orElseThrow(() -> {
         return new IllegalStateException("Invalid SpawnData");
      }));
      p_186382_.put("SpawnPotentials", SpawnData.LIST_CODEC.encodeStart(NbtOps.INSTANCE, this.spawnPotentials).result().orElseThrow());
      return p_186382_;
   }

   @Nullable
   public Entity getOrCreateDisplayEntity(Level p_151315_) {
      if (this.displayEntity == null) {
         this.displayEntity = EntityType.loadEntityRecursive(this.nextSpawnData.getEntityToSpawn(), p_151315_, Function.identity());
         if (this.nextSpawnData.getEntityToSpawn().size() == 1 && this.nextSpawnData.getEntityToSpawn().contains("id", 8) && this.displayEntity instanceof Mob) {
         }
      }

      return this.displayEntity;
   }

   public boolean onEventTriggered(Level p_151317_, int p_151318_) {
      if (p_151318_ == 1) {
         if (p_151317_.isClientSide) {
            this.spawnDelay = this.minSpawnDelay;
         }

         return true;
      } else {
         return false;
      }
   }

   public void setNextSpawnData(@Nullable Level p_151325_, BlockPos p_151326_, SpawnData p_151327_) {
      this.nextSpawnData = p_151327_;
   }

   public abstract void broadcastEvent(Level p_151322_, BlockPos p_151323_, int p_151324_);

   public double getSpin() {
      return this.spin;
   }

   public double getoSpin() {
      return this.oSpin;
   }

   @Nullable
   public Entity getSpawnerEntity() {
      return null;
   }

   @Nullable
   public net.minecraft.world.level.block.entity.BlockEntity getSpawnerBlockEntity(){ return null; }
}
