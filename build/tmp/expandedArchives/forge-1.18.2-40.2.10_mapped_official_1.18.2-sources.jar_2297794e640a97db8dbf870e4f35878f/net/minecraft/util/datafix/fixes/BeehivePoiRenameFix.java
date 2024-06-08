package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;

public class BeehivePoiRenameFix extends PoiTypeRename {
   public BeehivePoiRenameFix(Schema p_14727_) {
      super(p_14727_, false);
   }

   protected String rename(String p_14729_) {
      return p_14729_.equals("minecraft:bee_hive") ? "minecraft:beehive" : p_14729_;
   }
}