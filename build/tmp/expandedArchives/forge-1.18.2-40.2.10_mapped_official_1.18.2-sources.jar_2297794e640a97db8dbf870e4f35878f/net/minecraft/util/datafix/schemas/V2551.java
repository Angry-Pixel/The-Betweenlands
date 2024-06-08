package net.minecraft.util.datafix.schemas;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V2551 extends NamespacedSchema {
   public V2551(int p_17944_, Schema p_17945_) {
      super(p_17944_, p_17945_);
   }

   public void registerTypes(Schema p_17959_, Map<String, Supplier<TypeTemplate>> p_17960_, Map<String, Supplier<TypeTemplate>> p_17961_) {
      super.registerTypes(p_17959_, p_17960_, p_17961_);
      p_17959_.registerType(false, References.WORLD_GEN_SETTINGS, () -> {
         return DSL.fields("dimensions", DSL.compoundList(DSL.constType(namespacedString()), DSL.fields("generator", DSL.taggedChoiceLazy("type", DSL.string(), ImmutableMap.of("minecraft:debug", DSL::remainder, "minecraft:flat", () -> {
            return DSL.optionalFields("settings", DSL.optionalFields("biome", References.BIOME.in(p_17959_), "layers", DSL.list(DSL.optionalFields("block", References.BLOCK_NAME.in(p_17959_)))));
         }, "minecraft:noise", () -> {
            return DSL.optionalFields("biome_source", DSL.taggedChoiceLazy("type", DSL.string(), ImmutableMap.of("minecraft:fixed", () -> {
               return DSL.fields("biome", References.BIOME.in(p_17959_));
            }, "minecraft:multi_noise", () -> {
               return DSL.list(DSL.fields("biome", References.BIOME.in(p_17959_)));
            }, "minecraft:checkerboard", () -> {
               return DSL.fields("biomes", DSL.list(References.BIOME.in(p_17959_)));
            }, "minecraft:vanilla_layered", DSL::remainder, "minecraft:the_end", DSL::remainder)), "settings", DSL.or(DSL.constType(DSL.string()), DSL.optionalFields("default_block", References.BLOCK_NAME.in(p_17959_), "default_fluid", References.BLOCK_NAME.in(p_17959_))));
         })))));
      });
   }
}