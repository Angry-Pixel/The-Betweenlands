package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.stream.Stream;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class WorldGenSettingsHeightAndBiomeFix extends DataFix {
   private static final String NAME = "WorldGenSettingsHeightAndBiomeFix";
   public static final String WAS_PREVIOUSLY_INCREASED_KEY = "has_increased_height_already";

   public WorldGenSettingsHeightAndBiomeFix(Schema p_185174_) {
      super(p_185174_, true);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.WORLD_GEN_SETTINGS);
      OpticFinder<?> opticfinder = type.findField("dimensions");
      Type<?> type1 = this.getOutputSchema().getType(References.WORLD_GEN_SETTINGS);
      Type<?> type2 = type1.findFieldType("dimensions");
      return this.fixTypeEverywhereTyped("WorldGenSettingsHeightAndBiomeFix", type, type1, (p_185179_) -> {
         OptionalDynamic<?> optionaldynamic = p_185179_.get(DSL.remainderFinder()).get("has_increased_height_already");
         boolean flag = optionaldynamic.result().isEmpty();
         boolean flag1 = optionaldynamic.asBoolean(true);
         return p_185179_.update(DSL.remainderFinder(), (p_185205_) -> {
            return p_185205_.remove("has_increased_height_already");
         }).updateTyped(opticfinder, type2, (p_185190_) -> {
            Dynamic<?> dynamic = p_185190_.write().result().orElseThrow(() -> {
               return new IllegalStateException("Malformed WorldGenSettings.dimensions");
            });
            dynamic = dynamic.update("minecraft:overworld", (p_185194_) -> {
               return p_185194_.update("generator", (p_185201_) -> {
                  String s = p_185201_.get("type").asString("");
                  if ("minecraft:noise".equals(s)) {
                     MutableBoolean mutableboolean = new MutableBoolean();
                     p_185201_ = p_185201_.update("biome_source", (p_185185_) -> {
                        String s1 = p_185185_.get("type").asString("");
                        if ("minecraft:vanilla_layered".equals(s1) || flag && "minecraft:multi_noise".equals(s1)) {
                           if (p_185185_.get("large_biomes").asBoolean(false)) {
                              mutableboolean.setTrue();
                           }

                           return p_185185_.createMap(ImmutableMap.of(p_185185_.createString("preset"), p_185185_.createString("minecraft:overworld"), p_185185_.createString("type"), p_185185_.createString("minecraft:multi_noise")));
                        } else {
                           return p_185185_;
                        }
                     });
                     return mutableboolean.booleanValue() ? p_185201_.update("settings", (p_185203_) -> {
                        return "minecraft:overworld".equals(p_185203_.asString("")) ? p_185203_.createString("minecraft:large_biomes") : p_185203_;
                     }) : p_185201_;
                  } else if ("minecraft:flat".equals(s)) {
                     return flag1 ? p_185201_ : p_185201_.update("settings", (p_185197_) -> {
                        return p_185197_.update("layers", WorldGenSettingsHeightAndBiomeFix::updateLayers);
                     });
                  } else {
                     return p_185201_;
                  }
               });
            });
            return type2.readTyped(dynamic).result().orElseThrow(() -> {
               return new IllegalStateException("WorldGenSettingsHeightAndBiomeFix failed.");
            }).getFirst();
         });
      });
   }

   private static Dynamic<?> updateLayers(Dynamic<?> p_185181_) {
      Dynamic<?> dynamic = p_185181_.createMap(ImmutableMap.of(p_185181_.createString("height"), p_185181_.createInt(64), p_185181_.createString("block"), p_185181_.createString("minecraft:air")));
      return p_185181_.createList(Stream.concat(Stream.of(dynamic), p_185181_.asStream()));
   }
}