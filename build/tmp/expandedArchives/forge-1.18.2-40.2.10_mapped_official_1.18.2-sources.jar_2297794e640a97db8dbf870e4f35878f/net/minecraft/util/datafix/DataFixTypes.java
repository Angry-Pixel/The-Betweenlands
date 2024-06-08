package net.minecraft.util.datafix;

import com.mojang.datafixers.DSL.TypeReference;
import net.minecraft.util.datafix.fixes.References;

public enum DataFixTypes {
   LEVEL(References.LEVEL),
   PLAYER(References.PLAYER),
   CHUNK(References.CHUNK),
   HOTBAR(References.HOTBAR),
   OPTIONS(References.OPTIONS),
   STRUCTURE(References.STRUCTURE),
   STATS(References.STATS),
   SAVED_DATA(References.SAVED_DATA),
   ADVANCEMENTS(References.ADVANCEMENTS),
   POI_CHUNK(References.POI_CHUNK),
   WORLD_GEN_SETTINGS(References.WORLD_GEN_SETTINGS),
   ENTITY_CHUNK(References.ENTITY_CHUNK);

   private final TypeReference type;

   private DataFixTypes(TypeReference p_14503_) {
      this.type = p_14503_;
   }

   public TypeReference getType() {
      return this.type;
   }
}