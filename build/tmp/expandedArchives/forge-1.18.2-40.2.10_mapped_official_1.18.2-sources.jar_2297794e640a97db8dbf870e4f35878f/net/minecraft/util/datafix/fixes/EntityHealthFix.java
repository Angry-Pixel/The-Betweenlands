package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.Set;

public class EntityHealthFix extends DataFix {
   private static final Set<String> ENTITIES = Sets.newHashSet("ArmorStand", "Bat", "Blaze", "CaveSpider", "Chicken", "Cow", "Creeper", "EnderDragon", "Enderman", "Endermite", "EntityHorse", "Ghast", "Giant", "Guardian", "LavaSlime", "MushroomCow", "Ozelot", "Pig", "PigZombie", "Rabbit", "Sheep", "Shulker", "Silverfish", "Skeleton", "Slime", "SnowMan", "Spider", "Squid", "Villager", "VillagerGolem", "Witch", "WitherBoss", "Wolf", "Zombie");

   public EntityHealthFix(Schema p_15434_, boolean p_15435_) {
      super(p_15434_, p_15435_);
   }

   public Dynamic<?> fixTag(Dynamic<?> p_15439_) {
      Optional<Number> optional = p_15439_.get("HealF").asNumber().result();
      Optional<Number> optional1 = p_15439_.get("Health").asNumber().result();
      float f;
      if (optional.isPresent()) {
         f = optional.get().floatValue();
         p_15439_ = p_15439_.remove("HealF");
      } else {
         if (!optional1.isPresent()) {
            return p_15439_;
         }

         f = optional1.get().floatValue();
      }

      return p_15439_.set("Health", p_15439_.createFloat(f));
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityHealthFix", this.getInputSchema().getType(References.ENTITY), (p_15437_) -> {
         return p_15437_.update(DSL.remainderFinder(), this::fixTag);
      });
   }
}