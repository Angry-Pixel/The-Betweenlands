package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootContext {
   private final Random random;
   private final float luck;
   private final ServerLevel level;
   private final Function<ResourceLocation, LootTable> lootTables;
   private final Set<LootTable> visitedTables = Sets.newLinkedHashSet();
   private final Function<ResourceLocation, LootItemCondition> conditions;
   private final Set<LootItemCondition> visitedConditions = Sets.newLinkedHashSet();
   private final Map<LootContextParam<?>, Object> params;
   private final Map<ResourceLocation, LootContext.DynamicDrop> dynamicDrops;

   LootContext(Random p_78917_, float p_78918_, ServerLevel p_78919_, Function<ResourceLocation, LootTable> p_78920_, Function<ResourceLocation, LootItemCondition> p_78921_, Map<LootContextParam<?>, Object> p_78922_, Map<ResourceLocation, LootContext.DynamicDrop> p_78923_) {
      this.random = p_78917_;
      this.luck = p_78918_;
      this.level = p_78919_;
      this.lootTables = p_78920_;
      this.conditions = p_78921_;
      this.params = ImmutableMap.copyOf(p_78922_);
      this.dynamicDrops = ImmutableMap.copyOf(p_78923_);
   }

   public boolean hasParam(LootContextParam<?> p_78937_) {
      return this.params.containsKey(p_78937_);
   }

   public <T> T getParam(LootContextParam<T> p_165125_) {
      T t = (T)this.params.get(p_165125_);
      if (t == null) {
         throw new NoSuchElementException(p_165125_.getName().toString());
      } else {
         return t;
      }
   }

   public void addDynamicDrops(ResourceLocation p_78943_, Consumer<ItemStack> p_78944_) {
      LootContext.DynamicDrop lootcontext$dynamicdrop = this.dynamicDrops.get(p_78943_);
      if (lootcontext$dynamicdrop != null) {
         lootcontext$dynamicdrop.add(this, p_78944_);
      }

   }

   @Nullable
   public <T> T getParamOrNull(LootContextParam<T> p_78954_) {
      return (T)this.params.get(p_78954_);
   }

   public boolean addVisitedTable(LootTable p_78935_) {
      return this.visitedTables.add(p_78935_);
   }

   public void removeVisitedTable(LootTable p_78947_) {
      this.visitedTables.remove(p_78947_);
   }

   public boolean addVisitedCondition(LootItemCondition p_78939_) {
      return this.visitedConditions.add(p_78939_);
   }

   public void removeVisitedCondition(LootItemCondition p_78949_) {
      this.visitedConditions.remove(p_78949_);
   }

   public LootTable getLootTable(ResourceLocation p_78941_) {
      return this.lootTables.apply(p_78941_);
   }

   public LootItemCondition getCondition(ResourceLocation p_78951_) {
      return this.conditions.apply(p_78951_);
   }

   public Random getRandom() {
      return this.random;
   }

   public float getLuck() {
      return this.luck;
   }

   public ServerLevel getLevel() {
      return this.level;
   }

   // ============================== FORGE START ==============================
   public int getLootingModifier() {
      return net.minecraftforge.common.ForgeHooks.getLootingLevel(getParamOrNull(LootContextParams.THIS_ENTITY), getParamOrNull(LootContextParams.KILLER_ENTITY), getParamOrNull(LootContextParams.DAMAGE_SOURCE));
   }

   private ResourceLocation queriedLootTableId;

   private LootContext(Random rand, float luckIn, ServerLevel worldIn, Function<ResourceLocation, LootTable> lootTableManagerIn, Function<ResourceLocation, LootItemCondition> p_i225885_5_, Map<LootContextParam<?>, Object> parametersIn, Map<ResourceLocation, LootContext.DynamicDrop> conditionsIn, ResourceLocation queriedLootTableId) {
      this(rand, luckIn, worldIn, lootTableManagerIn, p_i225885_5_, parametersIn, conditionsIn);
      if (queriedLootTableId != null) this.queriedLootTableId = queriedLootTableId;
   }

   public void setQueriedLootTableId(ResourceLocation queriedLootTableId) {
      if (this.queriedLootTableId == null && queriedLootTableId != null) this.queriedLootTableId = queriedLootTableId;
   }
   public ResourceLocation getQueriedLootTableId() {
      return this.queriedLootTableId == null? net.minecraftforge.common.loot.LootTableIdCondition.UNKNOWN_LOOT_TABLE : this.queriedLootTableId;
   }
   // =============================== FORGE END ===============================

   public static class Builder {
      private final ServerLevel level;
      private final Map<LootContextParam<?>, Object> params = Maps.newIdentityHashMap();
      private final Map<ResourceLocation, LootContext.DynamicDrop> dynamicDrops = Maps.newHashMap();
      private Random random;
      private float luck;
      private ResourceLocation queriedLootTableId; // Forge: correctly pass around loot table ID with copy constructor

      public Builder(ServerLevel p_78961_) {
         this.level = p_78961_;
      }

      public Builder(LootContext context) {
         this.level = context.level;
         this.params.putAll(context.params);
         this.dynamicDrops.putAll(context.dynamicDrops);
         this.random = context.random;
         this.luck = context.luck;
         this.queriedLootTableId = context.queriedLootTableId;
      }

      public LootContext.Builder withRandom(Random p_78978_) {
         this.random = p_78978_;
         return this;
      }

      public LootContext.Builder withOptionalRandomSeed(long p_78966_) {
         if (p_78966_ != 0L) {
            this.random = new Random(p_78966_);
         }

         return this;
      }

      public LootContext.Builder withOptionalRandomSeed(long p_78968_, Random p_78969_) {
         if (p_78968_ == 0L) {
            this.random = p_78969_;
         } else {
            this.random = new Random(p_78968_);
         }

         return this;
      }

      public LootContext.Builder withLuck(float p_78964_) {
         this.luck = p_78964_;
         return this;
      }

      public <T> LootContext.Builder withParameter(LootContextParam<T> p_78973_, T p_78974_) {
         this.params.put(p_78973_, p_78974_);
         return this;
      }

      public <T> LootContext.Builder withOptionalParameter(LootContextParam<T> p_78985_, @Nullable T p_78986_) {
         if (p_78986_ == null) {
            this.params.remove(p_78985_);
         } else {
            this.params.put(p_78985_, p_78986_);
         }

         return this;
      }

      public LootContext.Builder withDynamicDrop(ResourceLocation p_78980_, LootContext.DynamicDrop p_78981_) {
         LootContext.DynamicDrop lootcontext$dynamicdrop = this.dynamicDrops.put(p_78980_, p_78981_);
         if (lootcontext$dynamicdrop != null) {
            throw new IllegalStateException("Duplicated dynamic drop '" + this.dynamicDrops + "'");
         } else {
            return this;
         }
      }

      public ServerLevel getLevel() {
         return this.level;
      }

      public <T> T getParameter(LootContextParam<T> p_78971_) {
         T t = (T)this.params.get(p_78971_);
         if (t == null) {
            throw new IllegalArgumentException("No parameter " + p_78971_);
         } else {
            return t;
         }
      }

      @Nullable
      public <T> T getOptionalParameter(LootContextParam<T> p_78983_) {
         return (T)this.params.get(p_78983_);
      }

      public LootContext create(LootContextParamSet p_78976_) {
         Set<LootContextParam<?>> set = Sets.difference(this.params.keySet(), p_78976_.getAllowed());
         if (false && !set.isEmpty()) { // Forge: Allow mods to pass custom loot parameters (not part of the vanilla loot table) to the loot context.
            throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + set);
         } else {
            Set<LootContextParam<?>> set1 = Sets.difference(p_78976_.getRequired(), this.params.keySet());
            if (!set1.isEmpty()) {
               throw new IllegalArgumentException("Missing required parameters: " + set1);
            } else {
               Random random = this.random;
               if (random == null) {
                  random = new Random();
               }

               MinecraftServer minecraftserver = this.level.getServer();
               return new LootContext(random, this.luck, this.level, minecraftserver.getLootTables()::get, minecraftserver.getPredicateManager()::get, this.params, this.dynamicDrops, this.queriedLootTableId);
            }
         }
      }
   }

   @FunctionalInterface
   public interface DynamicDrop {
      void add(LootContext p_78988_, Consumer<ItemStack> p_78989_);
   }

   public static enum EntityTarget {
      THIS("this", LootContextParams.THIS_ENTITY),
      KILLER("killer", LootContextParams.KILLER_ENTITY),
      DIRECT_KILLER("direct_killer", LootContextParams.DIRECT_KILLER_ENTITY),
      KILLER_PLAYER("killer_player", LootContextParams.LAST_DAMAGE_PLAYER);

      final String name;
      private final LootContextParam<? extends Entity> param;

      private EntityTarget(String p_79001_, LootContextParam<? extends Entity> p_79002_) {
         this.name = p_79001_;
         this.param = p_79002_;
      }

      public LootContextParam<? extends Entity> getParam() {
         return this.param;
      }

      // Forge: This method is patched in to expose the same name used in getByName so that ContextNbtProvider#forEntity serializes it properly
      public String getName() {
         return this.name;
      }

      public static LootContext.EntityTarget getByName(String p_79007_) {
         for(LootContext.EntityTarget lootcontext$entitytarget : values()) {
            if (lootcontext$entitytarget.name.equals(p_79007_)) {
               return lootcontext$entitytarget;
            }
         }

         throw new IllegalArgumentException("Invalid entity target " + p_79007_);
      }

      public static class Serializer extends TypeAdapter<LootContext.EntityTarget> {
         public void write(JsonWriter p_79015_, LootContext.EntityTarget p_79016_) throws IOException {
            p_79015_.value(p_79016_.name);
         }

         public LootContext.EntityTarget read(JsonReader p_79013_) throws IOException {
            return LootContext.EntityTarget.getByName(p_79013_.nextString());
         }
      }
   }
}
