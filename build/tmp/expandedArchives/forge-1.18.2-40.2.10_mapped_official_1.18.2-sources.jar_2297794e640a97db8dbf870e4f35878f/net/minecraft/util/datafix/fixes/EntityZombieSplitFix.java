package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class EntityZombieSplitFix extends SimpleEntityRenameFix {
   public EntityZombieSplitFix(Schema p_15798_, boolean p_15799_) {
      super("EntityZombieSplitFix", p_15798_, p_15799_);
   }

   protected Pair<String, Dynamic<?>> getNewNameAndTag(String p_15801_, Dynamic<?> p_15802_) {
      if (Objects.equals("Zombie", p_15801_)) {
         String s = "Zombie";
         int i = p_15802_.get("ZombieType").asInt(0);
         switch(i) {
         case 0:
         default:
            break;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
            s = "ZombieVillager";
            p_15802_ = p_15802_.set("Profession", p_15802_.createInt(i - 1));
            break;
         case 6:
            s = "Husk";
         }

         p_15802_ = p_15802_.remove("ZombieType");
         return Pair.of(s, p_15802_);
      } else {
         return Pair.of(p_15801_, p_15802_);
      }
   }
}