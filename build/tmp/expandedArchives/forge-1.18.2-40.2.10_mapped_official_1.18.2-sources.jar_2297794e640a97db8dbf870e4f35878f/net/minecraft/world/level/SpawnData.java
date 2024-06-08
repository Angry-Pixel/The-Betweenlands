package net.minecraft.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.random.SimpleWeightedRandomList;

public record SpawnData(CompoundTag entityToSpawn, Optional<SpawnData.CustomSpawnRules> customSpawnRules) {
   public static final Codec<SpawnData> CODEC = RecordCodecBuilder.create((p_186571_) -> {
      return p_186571_.group(CompoundTag.CODEC.fieldOf("entity").forGetter((p_186576_) -> {
         return p_186576_.entityToSpawn;
      }), SpawnData.CustomSpawnRules.CODEC.optionalFieldOf("custom_spawn_rules").forGetter((p_186569_) -> {
         return p_186569_.customSpawnRules;
      })).apply(p_186571_, SpawnData::new);
   });
   public static final Codec<SimpleWeightedRandomList<SpawnData>> LIST_CODEC = SimpleWeightedRandomList.wrappedCodecAllowingEmpty(CODEC);
   public static final String DEFAULT_TYPE = "minecraft:pig";

   public SpawnData() {
      this(Util.make(new CompoundTag(), (p_186573_) -> {
         p_186573_.putString("id", "minecraft:pig");
      }), Optional.empty());
   }

   public SpawnData {
      ResourceLocation resourcelocation = ResourceLocation.tryParse(entityToSpawn.getString("id"));
      entityToSpawn.putString("id", resourcelocation != null ? resourcelocation.toString() : "minecraft:pig");
   }

   public CompoundTag getEntityToSpawn() {
      return this.entityToSpawn;
   }

   public Optional<SpawnData.CustomSpawnRules> getCustomSpawnRules() {
      return this.customSpawnRules;
   }

   public static record CustomSpawnRules(InclusiveRange<Integer> blockLightLimit, InclusiveRange<Integer> skyLightLimit) {
      private static final InclusiveRange<Integer> LIGHT_RANGE = new InclusiveRange<>(0, 15);
      public static final Codec<SpawnData.CustomSpawnRules> CODEC = RecordCodecBuilder.create((p_186597_) -> {
         return p_186597_.group(InclusiveRange.INT.optionalFieldOf("block_light_limit", LIGHT_RANGE).flatXmap(SpawnData.CustomSpawnRules::checkLightBoundaries, SpawnData.CustomSpawnRules::checkLightBoundaries).forGetter((p_186600_) -> {
            return p_186600_.blockLightLimit;
         }), InclusiveRange.INT.optionalFieldOf("sky_light_limit", LIGHT_RANGE).flatXmap(SpawnData.CustomSpawnRules::checkLightBoundaries, SpawnData.CustomSpawnRules::checkLightBoundaries).forGetter((p_186595_) -> {
            return p_186595_.skyLightLimit;
         })).apply(p_186597_, SpawnData.CustomSpawnRules::new);
      });

      private static DataResult<InclusiveRange<Integer>> checkLightBoundaries(InclusiveRange<Integer> p_186593_) {
         return !LIGHT_RANGE.contains(p_186593_) ? DataResult.error("Light values must be withing range " + LIGHT_RANGE) : DataResult.success(p_186593_);
      }
   }
}