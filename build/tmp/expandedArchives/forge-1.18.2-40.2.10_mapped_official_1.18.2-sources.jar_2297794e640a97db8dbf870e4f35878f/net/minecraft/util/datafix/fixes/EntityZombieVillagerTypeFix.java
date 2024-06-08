package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Random;

public class EntityZombieVillagerTypeFix extends NamedEntityFix {
   private static final int PROFESSION_MAX = 6;
   private static final Random RANDOM = new Random();

   public EntityZombieVillagerTypeFix(Schema p_15806_, boolean p_15807_) {
      super(p_15806_, p_15807_, "EntityZombieVillagerTypeFix", References.ENTITY, "Zombie");
   }

   public Dynamic<?> fixTag(Dynamic<?> p_15813_) {
      if (p_15813_.get("IsVillager").asBoolean(false)) {
         if (!p_15813_.get("ZombieType").result().isPresent()) {
            int i = this.getVillagerProfession(p_15813_.get("VillagerProfession").asInt(-1));
            if (i == -1) {
               i = this.getVillagerProfession(RANDOM.nextInt(6));
            }

            p_15813_ = p_15813_.set("ZombieType", p_15813_.createInt(i));
         }

         p_15813_ = p_15813_.remove("IsVillager");
      }

      return p_15813_;
   }

   private int getVillagerProfession(int p_15809_) {
      return p_15809_ >= 0 && p_15809_ < 6 ? p_15809_ : -1;
   }

   protected Typed<?> fix(Typed<?> p_15811_) {
      return p_15811_.update(DSL.remainderFinder(), this::fixTag);
   }
}