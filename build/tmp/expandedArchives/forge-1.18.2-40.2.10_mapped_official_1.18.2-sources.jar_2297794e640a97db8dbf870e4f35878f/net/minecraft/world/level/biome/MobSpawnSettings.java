package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.slf4j.Logger;

public class MobSpawnSettings {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final float DEFAULT_CREATURE_SPAWN_PROBABILITY = 0.1F;
   public static final WeightedRandomList<MobSpawnSettings.SpawnerData> EMPTY_MOB_LIST = WeightedRandomList.create();
   public static final MobSpawnSettings EMPTY = (new MobSpawnSettings.Builder()).build();
   public static final MapCodec<MobSpawnSettings> CODEC = RecordCodecBuilder.mapCodec((p_187051_) -> {
      return p_187051_.group(Codec.floatRange(0.0F, 0.9999999F).optionalFieldOf("creature_spawn_probability", 0.1F).forGetter((p_187055_) -> {
         return p_187055_.creatureGenerationProbability;
      }), Codec.simpleMap(MobCategory.CODEC, WeightedRandomList.codec(MobSpawnSettings.SpawnerData.CODEC).promotePartial(Util.prefix("Spawn data: ", LOGGER::error)), StringRepresentable.keys(MobCategory.values())).fieldOf("spawners").forGetter((p_187053_) -> {
         return p_187053_.spawners;
      }), Codec.simpleMap(Registry.ENTITY_TYPE.byNameCodec(), MobSpawnSettings.MobSpawnCost.CODEC, Registry.ENTITY_TYPE).fieldOf("spawn_costs").forGetter((p_187049_) -> {
         return p_187049_.mobSpawnCosts;
      })).apply(p_187051_, MobSpawnSettings::new);
   });
   private final float creatureGenerationProbability;
   private final Map<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>> spawners;
   private final Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> mobSpawnCosts;
   private final java.util.Set<MobCategory> typesView;
   private final java.util.Set<EntityType<?>> costView;

   MobSpawnSettings(float p_196689_, Map<MobCategory, WeightedRandomList<MobSpawnSettings.SpawnerData>> p_196690_, Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> p_196691_) {
      this.creatureGenerationProbability = p_196689_;
      this.spawners = ImmutableMap.copyOf(p_196690_);
      this.mobSpawnCosts = ImmutableMap.copyOf(p_196691_);
      this.typesView = java.util.Collections.unmodifiableSet(this.spawners.keySet());
      this.costView = java.util.Collections.unmodifiableSet(this.mobSpawnCosts.keySet());
   }

   public WeightedRandomList<MobSpawnSettings.SpawnerData> getMobs(MobCategory p_151799_) {
      return this.spawners.getOrDefault(p_151799_, EMPTY_MOB_LIST);
   }

   public java.util.Set<MobCategory> getSpawnerTypes() {
       return this.typesView;
   }

   @Nullable
   public MobSpawnSettings.MobSpawnCost getMobSpawnCost(EntityType<?> p_48346_) {
      return this.mobSpawnCosts.get(p_48346_);
   }

   public java.util.Set<EntityType<?>> getEntityTypes() {
       return this.costView;
   }

   public float getCreatureProbability() {
      return this.creatureGenerationProbability;
   }

   public static class Builder {
      protected final Map<MobCategory, List<MobSpawnSettings.SpawnerData>> spawners = Stream.of(MobCategory.values()).collect(ImmutableMap.toImmutableMap((p_48383_) -> {
         return p_48383_;
      }, (p_48375_) -> {
         return Lists.newArrayList();
      }));
      protected final Map<EntityType<?>, MobSpawnSettings.MobSpawnCost> mobSpawnCosts = Maps.newLinkedHashMap();
      protected float creatureGenerationProbability = 0.1F;

      public MobSpawnSettings.Builder addSpawn(MobCategory p_48377_, MobSpawnSettings.SpawnerData p_48378_) {
         this.spawners.get(p_48377_).add(p_48378_);
         return this;
      }

      public MobSpawnSettings.Builder addMobCharge(EntityType<?> p_48371_, double p_48372_, double p_48373_) {
         this.mobSpawnCosts.put(p_48371_, new MobSpawnSettings.MobSpawnCost(p_48373_, p_48372_));
         return this;
      }

      public MobSpawnSettings.Builder creatureGenerationProbability(float p_48369_) {
         this.creatureGenerationProbability = p_48369_;
         return this;
      }

      public MobSpawnSettings build() {
         return new MobSpawnSettings(this.creatureGenerationProbability, this.spawners.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (p_151809_) -> {
            return WeightedRandomList.create(p_151809_.getValue());
         })), ImmutableMap.copyOf(this.mobSpawnCosts));
      }
   }

   public static class MobSpawnCost {
      public static final Codec<MobSpawnSettings.MobSpawnCost> CODEC = RecordCodecBuilder.create((p_48399_) -> {
         return p_48399_.group(Codec.DOUBLE.fieldOf("energy_budget").forGetter((p_151813_) -> {
            return p_151813_.energyBudget;
         }), Codec.DOUBLE.fieldOf("charge").forGetter((p_151811_) -> {
            return p_151811_.charge;
         })).apply(p_48399_, MobSpawnSettings.MobSpawnCost::new);
      });
      private final double energyBudget;
      private final double charge;

      MobSpawnCost(double p_48389_, double p_48390_) {
         this.energyBudget = p_48389_;
         this.charge = p_48390_;
      }

      public double getEnergyBudget() {
         return this.energyBudget;
      }

      public double getCharge() {
         return this.charge;
      }
   }

   public static class SpawnerData extends WeightedEntry.IntrusiveBase {
      public static final Codec<MobSpawnSettings.SpawnerData> CODEC = RecordCodecBuilder.create((p_151822_) -> {
         return p_151822_.group(Registry.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter((p_151826_) -> {
            return p_151826_.type;
         }), Weight.CODEC.fieldOf("weight").forGetter(WeightedEntry.IntrusiveBase::getWeight), Codec.INT.fieldOf("minCount").forGetter((p_151824_) -> {
            return p_151824_.minCount;
         }), Codec.INT.fieldOf("maxCount").forGetter((p_151820_) -> {
            return p_151820_.maxCount;
         })).apply(p_151822_, MobSpawnSettings.SpawnerData::new);
      });
      public final EntityType<?> type;
      public final int minCount;
      public final int maxCount;

      public SpawnerData(EntityType<?> p_48409_, int p_48410_, int p_48411_, int p_48412_) {
         this(p_48409_, Weight.of(p_48410_), p_48411_, p_48412_);
      }

      public SpawnerData(EntityType<?> p_151815_, Weight p_151816_, int p_151817_, int p_151818_) {
         super(p_151816_);
         this.type = p_151815_.getCategory() == MobCategory.MISC ? EntityType.PIG : p_151815_;
         this.minCount = p_151817_;
         this.maxCount = p_151818_;
      }

      public String toString() {
         return EntityType.getKey(this.type) + "*(" + this.minCount + "-" + this.maxCount + "):" + this.getWeight();
      }
   }
}
