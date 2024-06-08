package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BeehiveBlockEntity extends BlockEntity {
   public static final String TAG_FLOWER_POS = "FlowerPos";
   public static final String MIN_OCCUPATION_TICKS = "MinOccupationTicks";
   public static final String ENTITY_DATA = "EntityData";
   public static final String TICKS_IN_HIVE = "TicksInHive";
   public static final String HAS_NECTAR = "HasNectar";
   public static final String BEES = "Bees";
   private static final List<String> IGNORED_BEE_TAGS = Arrays.asList("Air", "ArmorDropChances", "ArmorItems", "Brain", "CanPickUpLoot", "DeathTime", "FallDistance", "FallFlying", "Fire", "HandDropChances", "HandItems", "HurtByTimestamp", "HurtTime", "LeftHanded", "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation", "CannotEnterHiveTicks", "TicksSincePollination", "CropsGrownSincePollination", "HivePos", "Passengers", "Leash", "UUID");
   public static final int MAX_OCCUPANTS = 3;
   private static final int MIN_TICKS_BEFORE_REENTERING_HIVE = 400;
   private static final int MIN_OCCUPATION_TICKS_NECTAR = 2400;
   public static final int MIN_OCCUPATION_TICKS_NECTARLESS = 600;
   private final List<BeehiveBlockEntity.BeeData> stored = Lists.newArrayList();
   @Nullable
   private BlockPos savedFlowerPos;

   public BeehiveBlockEntity(BlockPos p_155134_, BlockState p_155135_) {
      super(BlockEntityType.BEEHIVE, p_155134_, p_155135_);
   }

   public void setChanged() {
      if (this.isFireNearby()) {
         this.emptyAllLivingFromHive((Player)null, this.level.getBlockState(this.getBlockPos()), BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
      }

      super.setChanged();
   }

   public boolean isFireNearby() {
      if (this.level == null) {
         return false;
      } else {
         for(BlockPos blockpos : BlockPos.betweenClosed(this.worldPosition.offset(-1, -1, -1), this.worldPosition.offset(1, 1, 1))) {
            if (this.level.getBlockState(blockpos).getBlock() instanceof FireBlock) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isEmpty() {
      return this.stored.isEmpty();
   }

   public boolean isFull() {
      return this.stored.size() == 3;
   }

   public void emptyAllLivingFromHive(@Nullable Player p_58749_, BlockState p_58750_, BeehiveBlockEntity.BeeReleaseStatus p_58751_) {
      List<Entity> list = this.releaseAllOccupants(p_58750_, p_58751_);
      if (p_58749_ != null) {
         for(Entity entity : list) {
            if (entity instanceof Bee) {
               Bee bee = (Bee)entity;
               if (p_58749_.position().distanceToSqr(entity.position()) <= 16.0D) {
                  if (!this.isSedated()) {
                     bee.setTarget(p_58749_);
                  } else {
                     bee.setStayOutOfHiveCountdown(400);
                  }
               }
            }
         }
      }

   }

   private List<Entity> releaseAllOccupants(BlockState p_58760_, BeehiveBlockEntity.BeeReleaseStatus p_58761_) {
      List<Entity> list = Lists.newArrayList();
      this.stored.removeIf((p_58766_) -> {
         return releaseOccupant(this.level, this.worldPosition, p_58760_, p_58766_, list, p_58761_, this.savedFlowerPos);
      });
      if (!list.isEmpty()) {
         super.setChanged();
      }

      return list;
   }

   public void addOccupant(Entity p_58742_, boolean p_58743_) {
      this.addOccupantWithPresetTicks(p_58742_, p_58743_, 0);
   }

   @VisibleForDebug
   public int getOccupantCount() {
      return this.stored.size();
   }

   public static int getHoneyLevel(BlockState p_58753_) {
      return p_58753_.getValue(BeehiveBlock.HONEY_LEVEL);
   }

   @VisibleForDebug
   public boolean isSedated() {
      return CampfireBlock.isSmokeyPos(this.level, this.getBlockPos());
   }

   public void addOccupantWithPresetTicks(Entity p_58745_, boolean p_58746_, int p_58747_) {
      if (this.stored.size() < 3) {
         p_58745_.stopRiding();
         p_58745_.ejectPassengers();
         CompoundTag compoundtag = new CompoundTag();
         p_58745_.save(compoundtag);
         this.storeBee(compoundtag, p_58747_, p_58746_);
         if (this.level != null) {
            if (p_58745_ instanceof Bee) {
               Bee bee = (Bee)p_58745_;
               if (bee.hasSavedFlowerPos() && (!this.hasSavedFlowerPos() || this.level.random.nextBoolean())) {
                  this.savedFlowerPos = bee.getSavedFlowerPos();
               }
            }

            BlockPos blockpos = this.getBlockPos();
            this.level.playSound((Player)null, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
         }

         p_58745_.discard();
         super.setChanged();
      }
   }

   public void storeBee(CompoundTag p_155158_, int p_155159_, boolean p_155160_) {
      this.stored.add(new BeehiveBlockEntity.BeeData(p_155158_, p_155159_, p_155160_ ? 2400 : 600));
   }

   private static boolean releaseOccupant(Level p_155137_, BlockPos p_155138_, BlockState p_155139_, BeehiveBlockEntity.BeeData p_155140_, @Nullable List<Entity> p_155141_, BeehiveBlockEntity.BeeReleaseStatus p_155142_, @Nullable BlockPos p_155143_) {
      if ((p_155137_.isNight() || p_155137_.isRaining()) && p_155142_ != BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY) {
         return false;
      } else {
         CompoundTag compoundtag = p_155140_.entityData.copy();
         removeIgnoredBeeTags(compoundtag);
         compoundtag.put("HivePos", NbtUtils.writeBlockPos(p_155138_));
         compoundtag.putBoolean("NoGravity", true);
         Direction direction = p_155139_.getValue(BeehiveBlock.FACING);
         BlockPos blockpos = p_155138_.relative(direction);
         boolean flag = !p_155137_.getBlockState(blockpos).getCollisionShape(p_155137_, blockpos).isEmpty();
         if (flag && p_155142_ != BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY) {
            return false;
         } else {
            Entity entity = EntityType.loadEntityRecursive(compoundtag, p_155137_, (p_58740_) -> {
               return p_58740_;
            });
            if (entity != null) {
               if (!entity.getType().is(EntityTypeTags.BEEHIVE_INHABITORS)) {
                  return false;
               } else {
                  if (entity instanceof Bee) {
                     Bee bee = (Bee)entity;
                     if (p_155143_ != null && !bee.hasSavedFlowerPos() && p_155137_.random.nextFloat() < 0.9F) {
                        bee.setSavedFlowerPos(p_155143_);
                     }

                     if (p_155142_ == BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED) {
                        bee.dropOffNectar();
                        if (p_155139_.is(BlockTags.BEEHIVES, (p_202037_) -> {
                           return p_202037_.hasProperty(BeehiveBlock.HONEY_LEVEL);
                        })) {
                           int i = getHoneyLevel(p_155139_);
                           if (i < 5) {
                              int j = p_155137_.random.nextInt(100) == 0 ? 2 : 1;
                              if (i + j > 5) {
                                 --j;
                              }

                              p_155137_.setBlockAndUpdate(p_155138_, p_155139_.setValue(BeehiveBlock.HONEY_LEVEL, Integer.valueOf(i + j)));
                           }
                        }
                     }

                     setBeeReleaseData(p_155140_.ticksInHive, bee);
                     if (p_155141_ != null) {
                        p_155141_.add(bee);
                     }

                     float f = entity.getBbWidth();
                     double d3 = flag ? 0.0D : 0.55D + (double)(f / 2.0F);
                     double d0 = (double)p_155138_.getX() + 0.5D + d3 * (double)direction.getStepX();
                     double d1 = (double)p_155138_.getY() + 0.5D - (double)(entity.getBbHeight() / 2.0F);
                     double d2 = (double)p_155138_.getZ() + 0.5D + d3 * (double)direction.getStepZ();
                     entity.moveTo(d0, d1, d2, entity.getYRot(), entity.getXRot());
                  }

                  p_155137_.playSound((Player)null, p_155138_, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                  return p_155137_.addFreshEntity(entity);
               }
            } else {
               return false;
            }
         }
      }
   }

   static void removeIgnoredBeeTags(CompoundTag p_155162_) {
      for(String s : IGNORED_BEE_TAGS) {
         p_155162_.remove(s);
      }

   }

   private static void setBeeReleaseData(int p_58737_, Bee p_58738_) {
      int i = p_58738_.getAge();
      if (i < 0) {
         p_58738_.setAge(Math.min(0, i + p_58737_));
      } else if (i > 0) {
         p_58738_.setAge(Math.max(0, i - p_58737_));
      }

      p_58738_.setInLoveTime(Math.max(0, p_58738_.getInLoveTime() - p_58737_));
   }

   private boolean hasSavedFlowerPos() {
      return this.savedFlowerPos != null;
   }

   private static void tickOccupants(Level p_155150_, BlockPos p_155151_, BlockState p_155152_, List<BeehiveBlockEntity.BeeData> p_155153_, @Nullable BlockPos p_155154_) {
      boolean flag = false;

      BeehiveBlockEntity.BeeData beehiveblockentity$beedata;
      for(Iterator<BeehiveBlockEntity.BeeData> iterator = p_155153_.iterator(); iterator.hasNext(); ++beehiveblockentity$beedata.ticksInHive) {
         beehiveblockentity$beedata = iterator.next();
         if (beehiveblockentity$beedata.ticksInHive > beehiveblockentity$beedata.minOccupationTicks) {
            BeehiveBlockEntity.BeeReleaseStatus beehiveblockentity$beereleasestatus = beehiveblockentity$beedata.entityData.getBoolean("HasNectar") ? BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED : BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED;
            if (releaseOccupant(p_155150_, p_155151_, p_155152_, beehiveblockentity$beedata, (List<Entity>)null, beehiveblockentity$beereleasestatus, p_155154_)) {
               flag = true;
               iterator.remove();
            }
         }
      }

      if (flag) {
         setChanged(p_155150_, p_155151_, p_155152_);
      }

   }

   public static void serverTick(Level p_155145_, BlockPos p_155146_, BlockState p_155147_, BeehiveBlockEntity p_155148_) {
      tickOccupants(p_155145_, p_155146_, p_155147_, p_155148_.stored, p_155148_.savedFlowerPos);
      if (!p_155148_.stored.isEmpty() && p_155145_.getRandom().nextDouble() < 0.005D) {
         double d0 = (double)p_155146_.getX() + 0.5D;
         double d1 = (double)p_155146_.getY();
         double d2 = (double)p_155146_.getZ() + 0.5D;
         p_155145_.playSound((Player)null, d0, d1, d2, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

      DebugPackets.sendHiveInfo(p_155145_, p_155146_, p_155147_, p_155148_);
   }

   public void load(CompoundTag p_155156_) {
      super.load(p_155156_);
      this.stored.clear();
      ListTag listtag = p_155156_.getList("Bees", 10);

      for(int i = 0; i < listtag.size(); ++i) {
         CompoundTag compoundtag = listtag.getCompound(i);
         BeehiveBlockEntity.BeeData beehiveblockentity$beedata = new BeehiveBlockEntity.BeeData(compoundtag.getCompound("EntityData"), compoundtag.getInt("TicksInHive"), compoundtag.getInt("MinOccupationTicks"));
         this.stored.add(beehiveblockentity$beedata);
      }

      this.savedFlowerPos = null;
      if (p_155156_.contains("FlowerPos")) {
         this.savedFlowerPos = NbtUtils.readBlockPos(p_155156_.getCompound("FlowerPos"));
      }

   }

   protected void saveAdditional(CompoundTag p_187467_) {
      super.saveAdditional(p_187467_);
      p_187467_.put("Bees", this.writeBees());
      if (this.hasSavedFlowerPos()) {
         p_187467_.put("FlowerPos", NbtUtils.writeBlockPos(this.savedFlowerPos));
      }

   }

   public ListTag writeBees() {
      ListTag listtag = new ListTag();

      for(BeehiveBlockEntity.BeeData beehiveblockentity$beedata : this.stored) {
         CompoundTag compoundtag = beehiveblockentity$beedata.entityData.copy();
         compoundtag.remove("UUID");
         CompoundTag compoundtag1 = new CompoundTag();
         compoundtag1.put("EntityData", compoundtag);
         compoundtag1.putInt("TicksInHive", beehiveblockentity$beedata.ticksInHive);
         compoundtag1.putInt("MinOccupationTicks", beehiveblockentity$beedata.minOccupationTicks);
         listtag.add(compoundtag1);
      }

      return listtag;
   }

   static class BeeData {
      final CompoundTag entityData;
      int ticksInHive;
      final int minOccupationTicks;

      BeeData(CompoundTag p_58786_, int p_58787_, int p_58788_) {
         BeehiveBlockEntity.removeIgnoredBeeTags(p_58786_);
         this.entityData = p_58786_;
         this.ticksInHive = p_58787_;
         this.minOccupationTicks = p_58788_;
      }
   }

   public static enum BeeReleaseStatus {
      HONEY_DELIVERED,
      BEE_RELEASED,
      EMERGENCY;
   }
}