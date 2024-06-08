package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;

public class EntityUUIDFix extends AbstractUUIDFix {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Set<String> ABSTRACT_HORSES = Sets.newHashSet();
   private static final Set<String> TAMEABLE_ANIMALS = Sets.newHashSet();
   private static final Set<String> ANIMALS = Sets.newHashSet();
   private static final Set<String> MOBS = Sets.newHashSet();
   private static final Set<String> LIVING_ENTITIES = Sets.newHashSet();
   private static final Set<String> PROJECTILES = Sets.newHashSet();

   public EntityUUIDFix(Schema p_15723_) {
      super(p_15723_, References.ENTITY);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityUUIDFixes", this.getInputSchema().getType(this.typeReference), (p_15725_) -> {
         p_15725_ = p_15725_.update(DSL.remainderFinder(), EntityUUIDFix::updateEntityUUID);

         for(String s : ABSTRACT_HORSES) {
            p_15725_ = this.updateNamedChoice(p_15725_, s, EntityUUIDFix::updateAnimalOwner);
         }

         for(String s1 : TAMEABLE_ANIMALS) {
            p_15725_ = this.updateNamedChoice(p_15725_, s1, EntityUUIDFix::updateAnimalOwner);
         }

         for(String s2 : ANIMALS) {
            p_15725_ = this.updateNamedChoice(p_15725_, s2, EntityUUIDFix::updateAnimal);
         }

         for(String s3 : MOBS) {
            p_15725_ = this.updateNamedChoice(p_15725_, s3, EntityUUIDFix::updateMob);
         }

         for(String s4 : LIVING_ENTITIES) {
            p_15725_ = this.updateNamedChoice(p_15725_, s4, EntityUUIDFix::updateLivingEntity);
         }

         for(String s5 : PROJECTILES) {
            p_15725_ = this.updateNamedChoice(p_15725_, s5, EntityUUIDFix::updateProjectile);
         }

         p_15725_ = this.updateNamedChoice(p_15725_, "minecraft:bee", EntityUUIDFix::updateHurtBy);
         p_15725_ = this.updateNamedChoice(p_15725_, "minecraft:zombified_piglin", EntityUUIDFix::updateHurtBy);
         p_15725_ = this.updateNamedChoice(p_15725_, "minecraft:fox", EntityUUIDFix::updateFox);
         p_15725_ = this.updateNamedChoice(p_15725_, "minecraft:item", EntityUUIDFix::updateItem);
         p_15725_ = this.updateNamedChoice(p_15725_, "minecraft:shulker_bullet", EntityUUIDFix::updateShulkerBullet);
         p_15725_ = this.updateNamedChoice(p_15725_, "minecraft:area_effect_cloud", EntityUUIDFix::updateAreaEffectCloud);
         p_15725_ = this.updateNamedChoice(p_15725_, "minecraft:zombie_villager", EntityUUIDFix::updateZombieVillager);
         p_15725_ = this.updateNamedChoice(p_15725_, "minecraft:evoker_fangs", EntityUUIDFix::updateEvokerFangs);
         return this.updateNamedChoice(p_15725_, "minecraft:piglin", EntityUUIDFix::updatePiglin);
      });
   }

   private static Dynamic<?> updatePiglin(Dynamic<?> p_15740_) {
      return p_15740_.update("Brain", (p_15781_) -> {
         return p_15781_.update("memories", (p_145345_) -> {
            return p_145345_.update("minecraft:angry_at", (p_145347_) -> {
               return replaceUUIDString(p_145347_, "value", "value").orElseGet(() -> {
                  LOGGER.warn("angry_at has no value.");
                  return p_145347_;
               });
            });
         });
      });
   }

   private static Dynamic<?> updateEvokerFangs(Dynamic<?> p_15745_) {
      return replaceUUIDLeastMost(p_15745_, "OwnerUUID", "Owner").orElse(p_15745_);
   }

   private static Dynamic<?> updateZombieVillager(Dynamic<?> p_15750_) {
      return replaceUUIDLeastMost(p_15750_, "ConversionPlayer", "ConversionPlayer").orElse(p_15750_);
   }

   private static Dynamic<?> updateAreaEffectCloud(Dynamic<?> p_15752_) {
      return replaceUUIDLeastMost(p_15752_, "OwnerUUID", "Owner").orElse(p_15752_);
   }

   private static Dynamic<?> updateShulkerBullet(Dynamic<?> p_15754_) {
      p_15754_ = replaceUUIDMLTag(p_15754_, "Owner", "Owner").orElse(p_15754_);
      return replaceUUIDMLTag(p_15754_, "Target", "Target").orElse(p_15754_);
   }

   private static Dynamic<?> updateItem(Dynamic<?> p_15756_) {
      p_15756_ = replaceUUIDMLTag(p_15756_, "Owner", "Owner").orElse(p_15756_);
      return replaceUUIDMLTag(p_15756_, "Thrower", "Thrower").orElse(p_15756_);
   }

   private static Dynamic<?> updateFox(Dynamic<?> p_15758_) {
      Optional<Dynamic<?>> optional = p_15758_.get("TrustedUUIDs").result().map((p_15748_) -> {
         return p_15758_.createList(p_15748_.asStream().map((p_145341_) -> {
            return createUUIDFromML(p_145341_).orElseGet(() -> {
               LOGGER.warn("Trusted contained invalid data.");
               return p_145341_;
            });
         }));
      });
      return DataFixUtils.orElse(optional.map((p_15743_) -> {
         return p_15758_.remove("TrustedUUIDs").set("Trusted", p_15743_);
      }), p_15758_);
   }

   private static Dynamic<?> updateHurtBy(Dynamic<?> p_15760_) {
      return replaceUUIDString(p_15760_, "HurtBy", "HurtBy").orElse(p_15760_);
   }

   private static Dynamic<?> updateAnimalOwner(Dynamic<?> p_15762_) {
      Dynamic<?> dynamic = updateAnimal(p_15762_);
      return replaceUUIDString(dynamic, "OwnerUUID", "Owner").orElse(dynamic);
   }

   private static Dynamic<?> updateAnimal(Dynamic<?> p_15764_) {
      Dynamic<?> dynamic = updateMob(p_15764_);
      return replaceUUIDLeastMost(dynamic, "LoveCause", "LoveCause").orElse(dynamic);
   }

   private static Dynamic<?> updateMob(Dynamic<?> p_15767_) {
      return updateLivingEntity(p_15767_).update("Leash", (p_15775_) -> {
         return replaceUUIDLeastMost(p_15775_, "UUID", "UUID").orElse(p_15775_);
      });
   }

   public static Dynamic<?> updateLivingEntity(Dynamic<?> p_15730_) {
      return p_15730_.update("Attributes", (p_15733_) -> {
         return p_15730_.createList(p_15733_.asStream().map((p_145337_) -> {
            return p_145337_.update("Modifiers", (p_145335_) -> {
               return p_145337_.createList(p_145335_.asStream().map((p_145339_) -> {
                  return replaceUUIDLeastMost(p_145339_, "UUID", "UUID").orElse(p_145339_);
               }));
            });
         }));
      });
   }

   private static Dynamic<?> updateProjectile(Dynamic<?> p_15769_) {
      return DataFixUtils.orElse(p_15769_.get("OwnerUUID").result().map((p_15728_) -> {
         return p_15769_.remove("OwnerUUID").set("Owner", p_15728_);
      }), p_15769_);
   }

   public static Dynamic<?> updateEntityUUID(Dynamic<?> p_15735_) {
      return replaceUUIDLeastMost(p_15735_, "UUID", "UUID").orElse(p_15735_);
   }

   static {
      ABSTRACT_HORSES.add("minecraft:donkey");
      ABSTRACT_HORSES.add("minecraft:horse");
      ABSTRACT_HORSES.add("minecraft:llama");
      ABSTRACT_HORSES.add("minecraft:mule");
      ABSTRACT_HORSES.add("minecraft:skeleton_horse");
      ABSTRACT_HORSES.add("minecraft:trader_llama");
      ABSTRACT_HORSES.add("minecraft:zombie_horse");
      TAMEABLE_ANIMALS.add("minecraft:cat");
      TAMEABLE_ANIMALS.add("minecraft:parrot");
      TAMEABLE_ANIMALS.add("minecraft:wolf");
      ANIMALS.add("minecraft:bee");
      ANIMALS.add("minecraft:chicken");
      ANIMALS.add("minecraft:cow");
      ANIMALS.add("minecraft:fox");
      ANIMALS.add("minecraft:mooshroom");
      ANIMALS.add("minecraft:ocelot");
      ANIMALS.add("minecraft:panda");
      ANIMALS.add("minecraft:pig");
      ANIMALS.add("minecraft:polar_bear");
      ANIMALS.add("minecraft:rabbit");
      ANIMALS.add("minecraft:sheep");
      ANIMALS.add("minecraft:turtle");
      ANIMALS.add("minecraft:hoglin");
      MOBS.add("minecraft:bat");
      MOBS.add("minecraft:blaze");
      MOBS.add("minecraft:cave_spider");
      MOBS.add("minecraft:cod");
      MOBS.add("minecraft:creeper");
      MOBS.add("minecraft:dolphin");
      MOBS.add("minecraft:drowned");
      MOBS.add("minecraft:elder_guardian");
      MOBS.add("minecraft:ender_dragon");
      MOBS.add("minecraft:enderman");
      MOBS.add("minecraft:endermite");
      MOBS.add("minecraft:evoker");
      MOBS.add("minecraft:ghast");
      MOBS.add("minecraft:giant");
      MOBS.add("minecraft:guardian");
      MOBS.add("minecraft:husk");
      MOBS.add("minecraft:illusioner");
      MOBS.add("minecraft:magma_cube");
      MOBS.add("minecraft:pufferfish");
      MOBS.add("minecraft:zombified_piglin");
      MOBS.add("minecraft:salmon");
      MOBS.add("minecraft:shulker");
      MOBS.add("minecraft:silverfish");
      MOBS.add("minecraft:skeleton");
      MOBS.add("minecraft:slime");
      MOBS.add("minecraft:snow_golem");
      MOBS.add("minecraft:spider");
      MOBS.add("minecraft:squid");
      MOBS.add("minecraft:stray");
      MOBS.add("minecraft:tropical_fish");
      MOBS.add("minecraft:vex");
      MOBS.add("minecraft:villager");
      MOBS.add("minecraft:iron_golem");
      MOBS.add("minecraft:vindicator");
      MOBS.add("minecraft:pillager");
      MOBS.add("minecraft:wandering_trader");
      MOBS.add("minecraft:witch");
      MOBS.add("minecraft:wither");
      MOBS.add("minecraft:wither_skeleton");
      MOBS.add("minecraft:zombie");
      MOBS.add("minecraft:zombie_villager");
      MOBS.add("minecraft:phantom");
      MOBS.add("minecraft:ravager");
      MOBS.add("minecraft:piglin");
      LIVING_ENTITIES.add("minecraft:armor_stand");
      PROJECTILES.add("minecraft:arrow");
      PROJECTILES.add("minecraft:dragon_fireball");
      PROJECTILES.add("minecraft:firework_rocket");
      PROJECTILES.add("minecraft:fireball");
      PROJECTILES.add("minecraft:llama_spit");
      PROJECTILES.add("minecraft:small_fireball");
      PROJECTILES.add("minecraft:snowball");
      PROJECTILES.add("minecraft:spectral_arrow");
      PROJECTILES.add("minecraft:egg");
      PROJECTILES.add("minecraft:ender_pearl");
      PROJECTILES.add("minecraft:experience_bottle");
      PROJECTILES.add("minecraft:potion");
      PROJECTILES.add("minecraft:trident");
      PROJECTILES.add("minecraft:wither_skull");
   }
}