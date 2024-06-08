package net.minecraft.util.datafix.schemas;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.References;

public class V1451_6 extends NamespacedSchema {
   public static final String SPECIAL_OBJECTIVE_MARKER = "_special";
   protected static final HookFunction UNPACK_OBJECTIVE_ID = new HookFunction() {
      public <T> T apply(DynamicOps<T> p_181096_, T p_181097_) {
         Dynamic<T> dynamic = new Dynamic<>(p_181096_, p_181097_);
         return DataFixUtils.orElse(dynamic.get("CriteriaName").asString().get().left().map((p_181094_) -> {
            int i = p_181094_.indexOf(58);
            if (i < 0) {
               return Pair.of("_special", p_181094_);
            } else {
               try {
                  ResourceLocation resourcelocation = ResourceLocation.of(p_181094_.substring(0, i), '.');
                  ResourceLocation resourcelocation1 = ResourceLocation.of(p_181094_.substring(i + 1), '.');
                  return Pair.of(resourcelocation.toString(), resourcelocation1.toString());
               } catch (Exception exception) {
                  return Pair.of("_special", p_181094_);
               }
            }
         }).map((p_181092_) -> {
            return dynamic.set("CriteriaType", dynamic.createMap(ImmutableMap.of(dynamic.createString("type"), dynamic.createString(p_181092_.getFirst()), dynamic.createString("id"), dynamic.createString(p_181092_.getSecond()))));
         }), dynamic).getValue();
      }
   };
   protected static final HookFunction REPACK_OBJECTIVE_ID = new HookFunction() {
      private String packWithDot(String p_181103_) {
         ResourceLocation resourcelocation = ResourceLocation.tryParse(p_181103_);
         return resourcelocation != null ? resourcelocation.getNamespace() + "." + resourcelocation.getPath() : p_181103_;
      }

      public <T> T apply(DynamicOps<T> p_181105_, T p_181106_) {
         Dynamic<T> dynamic = new Dynamic<>(p_181105_, p_181106_);
         Optional<Dynamic<T>> optional = dynamic.get("CriteriaType").get().get().left().flatMap((p_181109_) -> {
            Optional<String> optional1 = p_181109_.get("type").asString().get().left();
            Optional<String> optional2 = p_181109_.get("id").asString().get().left();
            if (optional1.isPresent() && optional2.isPresent()) {
               String s = optional1.get();
               return s.equals("_special") ? Optional.of(dynamic.createString(optional2.get())) : Optional.of(p_181109_.createString(this.packWithDot(s) + ":" + this.packWithDot(optional2.get())));
            } else {
               return Optional.empty();
            }
         });
         return DataFixUtils.orElse(optional.map((p_181101_) -> {
            return dynamic.set("CriteriaName", p_181101_).remove("CriteriaType");
         }), dynamic).getValue();
      }
   };

   public V1451_6(int p_17532_, Schema p_17533_) {
      super(p_17532_, p_17533_);
   }

   public void registerTypes(Schema p_17540_, Map<String, Supplier<TypeTemplate>> p_17541_, Map<String, Supplier<TypeTemplate>> p_17542_) {
      super.registerTypes(p_17540_, p_17541_, p_17542_);
      Supplier<TypeTemplate> supplier = () -> {
         return DSL.compoundList(References.ITEM_NAME.in(p_17540_), DSL.constType(DSL.intType()));
      };
      p_17540_.registerType(false, References.STATS, () -> {
         return DSL.optionalFields("stats", DSL.optionalFields("minecraft:mined", DSL.compoundList(References.BLOCK_NAME.in(p_17540_), DSL.constType(DSL.intType())), "minecraft:crafted", supplier.get(), "minecraft:used", supplier.get(), "minecraft:broken", supplier.get(), "minecraft:picked_up", supplier.get(), DSL.optionalFields("minecraft:dropped", supplier.get(), "minecraft:killed", DSL.compoundList(References.ENTITY_NAME.in(p_17540_), DSL.constType(DSL.intType())), "minecraft:killed_by", DSL.compoundList(References.ENTITY_NAME.in(p_17540_), DSL.constType(DSL.intType())), "minecraft:custom", DSL.compoundList(DSL.constType(namespacedString()), DSL.constType(DSL.intType())))));
      });
      Map<String, Supplier<TypeTemplate>> map = createCriterionTypes(p_17540_);
      p_17540_.registerType(false, References.OBJECTIVE, () -> {
         return DSL.hook(DSL.optionalFields("CriteriaType", DSL.taggedChoiceLazy("type", DSL.string(), map)), UNPACK_OBJECTIVE_ID, REPACK_OBJECTIVE_ID);
      });
   }

   protected static Map<String, Supplier<TypeTemplate>> createCriterionTypes(Schema p_181078_) {
      Supplier<TypeTemplate> supplier = () -> {
         return DSL.optionalFields("id", References.ITEM_NAME.in(p_181078_));
      };
      Supplier<TypeTemplate> supplier1 = () -> {
         return DSL.optionalFields("id", References.BLOCK_NAME.in(p_181078_));
      };
      Supplier<TypeTemplate> supplier2 = () -> {
         return DSL.optionalFields("id", References.ENTITY_NAME.in(p_181078_));
      };
      Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
      map.put("minecraft:mined", supplier1);
      map.put("minecraft:crafted", supplier);
      map.put("minecraft:used", supplier);
      map.put("minecraft:broken", supplier);
      map.put("minecraft:picked_up", supplier);
      map.put("minecraft:dropped", supplier);
      map.put("minecraft:killed", supplier2);
      map.put("minecraft:killed_by", supplier2);
      map.put("minecraft:custom", () -> {
         return DSL.optionalFields("id", DSL.constType(namespacedString()));
      });
      map.put("_special", () -> {
         return DSL.optionalFields("id", DSL.constType(DSL.string()));
      });
      return map;
   }
}