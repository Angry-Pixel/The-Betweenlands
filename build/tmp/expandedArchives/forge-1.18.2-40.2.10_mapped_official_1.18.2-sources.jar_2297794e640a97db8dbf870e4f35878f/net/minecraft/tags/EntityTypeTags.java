package net.minecraft.tags;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public final class EntityTypeTags {
   public static final TagKey<EntityType<?>> SKELETONS = create("skeletons");
   public static final TagKey<EntityType<?>> RAIDERS = create("raiders");
   public static final TagKey<EntityType<?>> BEEHIVE_INHABITORS = create("beehive_inhabitors");
   public static final TagKey<EntityType<?>> ARROWS = create("arrows");
   public static final TagKey<EntityType<?>> IMPACT_PROJECTILES = create("impact_projectiles");
   public static final TagKey<EntityType<?>> POWDER_SNOW_WALKABLE_MOBS = create("powder_snow_walkable_mobs");
   public static final TagKey<EntityType<?>> AXOLOTL_ALWAYS_HOSTILES = create("axolotl_always_hostiles");
   public static final TagKey<EntityType<?>> AXOLOTL_HUNT_TARGETS = create("axolotl_hunt_targets");
   public static final TagKey<EntityType<?>> FREEZE_IMMUNE_ENTITY_TYPES = create("freeze_immune_entity_types");
   public static final TagKey<EntityType<?>> FREEZE_HURTS_EXTRA_TYPES = create("freeze_hurts_extra_types");

   private EntityTypeTags() {
   }

   private static TagKey<EntityType<?>> create(String p_203849_) {
      return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(p_203849_));
   }
}