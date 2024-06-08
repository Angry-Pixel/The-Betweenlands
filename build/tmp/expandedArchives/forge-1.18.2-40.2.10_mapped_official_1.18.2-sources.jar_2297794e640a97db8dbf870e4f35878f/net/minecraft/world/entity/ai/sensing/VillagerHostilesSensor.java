package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class VillagerHostilesSensor extends NearestVisibleLivingEntitySensor {
   private static final ImmutableMap<EntityType<?>, Float> ACCEPTABLE_DISTANCE_FROM_HOSTILES = ImmutableMap.<EntityType<?>, Float>builder().put(EntityType.DROWNED, 8.0F).put(EntityType.EVOKER, 12.0F).put(EntityType.HUSK, 8.0F).put(EntityType.ILLUSIONER, 12.0F).put(EntityType.PILLAGER, 15.0F).put(EntityType.RAVAGER, 12.0F).put(EntityType.VEX, 8.0F).put(EntityType.VINDICATOR, 10.0F).put(EntityType.ZOGLIN, 10.0F).put(EntityType.ZOMBIE, 8.0F).put(EntityType.ZOMBIE_VILLAGER, 8.0F).build();

   protected boolean isMatchingEntity(LivingEntity p_148344_, LivingEntity p_148345_) {
      return this.isHostile(p_148345_) && this.isClose(p_148344_, p_148345_);
   }

   private boolean isClose(LivingEntity p_26861_, LivingEntity p_26862_) {
      float f = ACCEPTABLE_DISTANCE_FROM_HOSTILES.get(p_26862_.getType());
      return p_26862_.distanceToSqr(p_26861_) <= (double)(f * f);
   }

   protected MemoryModuleType<LivingEntity> getMemory() {
      return MemoryModuleType.NEAREST_HOSTILE;
   }

   private boolean isHostile(LivingEntity p_26868_) {
      return ACCEPTABLE_DISTANCE_FROM_HOSTILES.containsKey(p_26868_.getType());
   }
}