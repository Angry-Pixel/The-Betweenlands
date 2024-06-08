package net.minecraft.world.entity.animal.horse;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class SkeletonTrapGoal extends Goal {
   private final SkeletonHorse horse;

   public SkeletonTrapGoal(SkeletonHorse p_30927_) {
      this.horse = p_30927_;
   }

   public boolean canUse() {
      return this.horse.level.hasNearbyAlivePlayer(this.horse.getX(), this.horse.getY(), this.horse.getZ(), 10.0D);
   }

   public void tick() {
      ServerLevel serverlevel = (ServerLevel)this.horse.level;
      // Forge: Trigger the trap in a tick task to avoid crashes when mods add goals to skeleton horses
      // (MC-206338/Forge PR #7509)
      serverlevel.getServer().tell(new net.minecraft.server.TickTask(serverlevel.getServer().getTickCount(), () -> {
      if (!this.horse.isAlive()) return;
      DifficultyInstance difficultyinstance = serverlevel.getCurrentDifficultyAt(this.horse.blockPosition());
      this.horse.setTrap(false);
      this.horse.setTamed(true);
      this.horse.setAge(0);
      LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(serverlevel);
      lightningbolt.moveTo(this.horse.getX(), this.horse.getY(), this.horse.getZ());
      lightningbolt.setVisualOnly(true);
      serverlevel.addFreshEntity(lightningbolt);
      Skeleton skeleton = this.createSkeleton(difficultyinstance, this.horse);
      skeleton.startRiding(this.horse);
      serverlevel.addFreshEntityWithPassengers(skeleton);

      for(int i = 0; i < 3; ++i) {
         AbstractHorse abstracthorse = this.createHorse(difficultyinstance);
         Skeleton skeleton1 = this.createSkeleton(difficultyinstance, abstracthorse);
         skeleton1.startRiding(abstracthorse);
         abstracthorse.push(this.horse.getRandom().nextGaussian() * 0.5D, 0.0D, this.horse.getRandom().nextGaussian() * 0.5D);
         serverlevel.addFreshEntityWithPassengers(abstracthorse);
      }
      }));
   }

   private AbstractHorse createHorse(DifficultyInstance p_30930_) {
      SkeletonHorse skeletonhorse = EntityType.SKELETON_HORSE.create(this.horse.level);
      skeletonhorse.finalizeSpawn((ServerLevel)this.horse.level, p_30930_, MobSpawnType.TRIGGERED, (SpawnGroupData)null, (CompoundTag)null);
      skeletonhorse.setPos(this.horse.getX(), this.horse.getY(), this.horse.getZ());
      skeletonhorse.invulnerableTime = 60;
      skeletonhorse.setPersistenceRequired();
      skeletonhorse.setTamed(true);
      skeletonhorse.setAge(0);
      return skeletonhorse;
   }

   private Skeleton createSkeleton(DifficultyInstance p_30932_, AbstractHorse p_30933_) {
      Skeleton skeleton = EntityType.SKELETON.create(p_30933_.level);
      skeleton.finalizeSpawn((ServerLevel)p_30933_.level, p_30932_, MobSpawnType.TRIGGERED, (SpawnGroupData)null, (CompoundTag)null);
      skeleton.setPos(p_30933_.getX(), p_30933_.getY(), p_30933_.getZ());
      skeleton.invulnerableTime = 60;
      skeleton.setPersistenceRequired();
      if (skeleton.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
         skeleton.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
      }

      skeleton.setItemSlot(EquipmentSlot.MAINHAND, EnchantmentHelper.enchantItem(skeleton.getRandom(), this.disenchant(skeleton.getMainHandItem()), (int)(5.0F + p_30932_.getSpecialMultiplier() * (float)skeleton.getRandom().nextInt(18)), false));
      skeleton.setItemSlot(EquipmentSlot.HEAD, EnchantmentHelper.enchantItem(skeleton.getRandom(), this.disenchant(skeleton.getItemBySlot(EquipmentSlot.HEAD)), (int)(5.0F + p_30932_.getSpecialMultiplier() * (float)skeleton.getRandom().nextInt(18)), false));
      return skeleton;
   }

   private ItemStack disenchant(ItemStack p_30935_) {
      p_30935_.removeTagKey("Enchantments");
      return p_30935_;
   }
}
