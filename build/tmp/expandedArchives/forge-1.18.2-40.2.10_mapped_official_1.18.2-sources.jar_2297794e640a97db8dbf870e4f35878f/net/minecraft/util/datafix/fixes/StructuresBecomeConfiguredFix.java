package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;

public class StructuresBecomeConfiguredFix extends DataFix {
   private static final Map<String, StructuresBecomeConfiguredFix.Conversion> CONVERSION_MAP = ImmutableMap.<String, StructuresBecomeConfiguredFix.Conversion>builder().put("mineshaft", StructuresBecomeConfiguredFix.Conversion.biomeMapped(Map.of(List.of("minecraft:badlands", "minecraft:eroded_badlands", "minecraft:wooded_badlands"), "minecraft:mineshaft_mesa"), "minecraft:mineshaft")).put("shipwreck", StructuresBecomeConfiguredFix.Conversion.biomeMapped(Map.of(List.of("minecraft:beach", "minecraft:snowy_beach"), "minecraft:shipwreck_beached"), "minecraft:shipwreck")).put("ocean_ruin", StructuresBecomeConfiguredFix.Conversion.biomeMapped(Map.of(List.of("minecraft:warm_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean"), "minecraft:ocean_ruin_warm"), "minecraft:ocean_ruin_cold")).put("village", StructuresBecomeConfiguredFix.Conversion.biomeMapped(Map.of(List.of("minecraft:desert"), "minecraft:village_desert", List.of("minecraft:savanna"), "minecraft:village_savanna", List.of("minecraft:snowy_plains"), "minecraft:village_snowy", List.of("minecraft:taiga"), "minecraft:village_taiga"), "minecraft:village_plains")).put("ruined_portal", StructuresBecomeConfiguredFix.Conversion.biomeMapped(Map.of(List.of("minecraft:desert"), "minecraft:ruined_portal_desert", List.of("minecraft:badlands", "minecraft:eroded_badlands", "minecraft:wooded_badlands", "minecraft:windswept_hills", "minecraft:windswept_forest", "minecraft:windswept_gravelly_hills", "minecraft:savanna_plateau", "minecraft:windswept_savanna", "minecraft:stony_shore", "minecraft:meadow", "minecraft:frozen_peaks", "minecraft:jagged_peaks", "minecraft:stony_peaks", "minecraft:snowy_slopes"), "minecraft:ruined_portal_mountain", List.of("minecraft:bamboo_jungle", "minecraft:jungle", "minecraft:sparse_jungle"), "minecraft:ruined_portal_jungle", List.of("minecraft:deep_frozen_ocean", "minecraft:deep_cold_ocean", "minecraft:deep_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:frozen_ocean", "minecraft:ocean", "minecraft:cold_ocean", "minecraft:lukewarm_ocean", "minecraft:warm_ocean"), "minecraft:ruined_portal_ocean"), "minecraft:ruined_portal")).put("pillager_outpost", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:pillager_outpost")).put("mansion", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:mansion")).put("jungle_pyramid", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:jungle_pyramid")).put("desert_pyramid", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:desert_pyramid")).put("igloo", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:igloo")).put("swamp_hut", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:swamp_hut")).put("stronghold", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:stronghold")).put("monument", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:monument")).put("fortress", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:fortress")).put("endcity", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:end_city")).put("buried_treasure", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:buried_treasure")).put("nether_fossil", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:nether_fossil")).put("bastion_remnant", StructuresBecomeConfiguredFix.Conversion.trivial("minecraft:bastion_remnant")).build();

   public StructuresBecomeConfiguredFix(Schema p_207679_) {
      super(p_207679_, false);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> type = this.getInputSchema().getType(References.CHUNK);
      Type<?> type1 = this.getInputSchema().getType(References.CHUNK);
      return this.writeFixAndRead("StucturesToConfiguredStructures", type, type1, this::fix);
   }

   private Dynamic<?> fix(Dynamic<?> p_207692_) {
      return p_207692_.update("structures", (p_207728_) -> {
         return p_207728_.update("starts", (p_207734_) -> {
            return this.updateStarts(p_207734_, p_207692_);
         }).update("References", (p_207731_) -> {
            return this.updateReferences(p_207731_, p_207692_);
         });
      });
   }

   private Dynamic<?> updateStarts(Dynamic<?> p_207700_, Dynamic<?> p_207701_) {
      Map<? extends Dynamic<?>, ? extends Dynamic<?>> map = p_207700_.getMapValues().result().get();
      List<Dynamic<?>> list = new ArrayList<>();
      map.forEach((p_207721_, p_207722_) -> {
         if (p_207722_.get("id").asString("INVALID").equals("INVALID")) {
            list.add(p_207721_);
         }

      });

      for(Dynamic<?> dynamic : list) {
         p_207700_ = p_207700_.remove(dynamic.asString(""));
      }

      return p_207700_.updateMapValues((p_207715_) -> {
         return this.updateStart(p_207715_, p_207701_);
      });
   }

   private Pair<Dynamic<?>, Dynamic<?>> updateStart(Pair<Dynamic<?>, Dynamic<?>> p_207685_, Dynamic<?> p_207686_) {
      Dynamic<?> dynamic = this.findUpdatedStructureType(p_207685_, p_207686_);
      return new Pair<>(dynamic, p_207685_.getSecond().set("id", dynamic));
   }

   private Dynamic<?> updateReferences(Dynamic<?> p_207717_, Dynamic<?> p_207718_) {
      Map<? extends Dynamic<?>, ? extends Dynamic<?>> map = p_207717_.getMapValues().result().get();
      List<Dynamic<?>> list = new ArrayList<>();
      map.forEach((p_207704_, p_207705_) -> {
         if (p_207705_.asLongStream().count() == 0L) {
            list.add(p_207704_);
         }

      });

      for(Dynamic<?> dynamic : list) {
         p_207717_ = p_207717_.remove(dynamic.asString(""));
      }

      return p_207717_.updateMapValues((p_207698_) -> {
         return this.updateReference(p_207698_, p_207718_);
      });
   }

   private Pair<Dynamic<?>, Dynamic<?>> updateReference(Pair<Dynamic<?>, Dynamic<?>> p_207711_, Dynamic<?> p_207712_) {
      return p_207711_.mapFirst((p_207690_) -> {
         return this.findUpdatedStructureType(p_207711_, p_207712_);
      });
   }

   private Dynamic<?> findUpdatedStructureType(Pair<Dynamic<?>, Dynamic<?>> p_207724_, Dynamic<?> p_207725_) {
      String s = p_207724_.getFirst().asString("UNKNOWN").toLowerCase(Locale.ROOT);
      StructuresBecomeConfiguredFix.Conversion structuresbecomeconfiguredfix$conversion = CONVERSION_MAP.get(s);
      if (structuresbecomeconfiguredfix$conversion == null) structuresbecomeconfiguredfix$conversion = net.minecraftforge.common.ForgeHooks.getStructureConversion(s); // Forge: hook for mods to register conversions through RegisterStructureConversionsEvent
      if (structuresbecomeconfiguredfix$conversion == null) {
         if (net.minecraftforge.common.ForgeHooks.checkStructureNamespace(s)) return p_207724_.getSecond().createString(s); // Forge: pass-through structure IDs which have a non-"minecraft" namespace
         if (true) return p_207724_.getSecond().createString("unknown." + s); // Forge: Pass-through with "unknown." prefix, so deserializer logs and ignores rather than fixer throwing an exception and dropping chunk data
         throw new IllegalStateException("Found unknown structure: " + s);
      } else {
         Dynamic<?> dynamic = p_207724_.getSecond();
         String s1 = structuresbecomeconfiguredfix$conversion.fallback;
         if (!structuresbecomeconfiguredfix$conversion.biomeMapping().isEmpty()) {
            Optional<String> optional = this.guessConfiguration(p_207725_, structuresbecomeconfiguredfix$conversion);
            if (optional.isPresent()) {
               s1 = optional.get();
            }
         }

         Dynamic<?> dynamic1 = dynamic.createString(s1);
         return dynamic1;
      }
   }

   private Optional<String> guessConfiguration(Dynamic<?> p_207694_, StructuresBecomeConfiguredFix.Conversion p_207695_) {
      Object2IntArrayMap<String> object2intarraymap = new Object2IntArrayMap<>();
      p_207694_.get("sections").asList(Function.identity()).forEach((p_207683_) -> {
         p_207683_.get("biomes").get("palette").asList(Function.identity()).forEach((p_207709_) -> {
            String s = p_207695_.biomeMapping().get(p_207709_.asString(""));
            if (s != null) {
               object2intarraymap.mergeInt(s, 1, Integer::sum);
            }

         });
      });
      return object2intarraymap.object2IntEntrySet().stream().max(Comparator.comparingInt(it.unimi.dsi.fastutil.objects.Object2IntMap.Entry::getIntValue)).map(Entry::getKey);
   }

   public static record Conversion(Map<String, String> biomeMapping, String fallback) {
      public static StructuresBecomeConfiguredFix.Conversion trivial(String p_207747_) {
         return new StructuresBecomeConfiguredFix.Conversion(Map.of(), p_207747_);
      }

      public static StructuresBecomeConfiguredFix.Conversion biomeMapped(Map<List<String>, String> p_207751_, String p_207752_) {
         return new StructuresBecomeConfiguredFix.Conversion(unpack(p_207751_), p_207752_);
      }

      private static Map<String, String> unpack(Map<List<String>, String> p_207749_) {
         Builder<String, String> builder = ImmutableMap.builder();

         for(Entry<List<String>, String> entry : p_207749_.entrySet()) {
            entry.getKey().forEach((p_207745_) -> {
               builder.put(p_207745_, entry.getValue());
            });
         }

         return builder.build();
      }
   }
}
