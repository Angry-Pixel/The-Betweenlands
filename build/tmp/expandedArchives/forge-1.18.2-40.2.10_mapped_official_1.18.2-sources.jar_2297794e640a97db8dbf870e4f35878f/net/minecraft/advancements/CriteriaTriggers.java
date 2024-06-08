package net.minecraft.advancements;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.BeeNestDestroyedTrigger;
import net.minecraft.advancements.critereon.BredAnimalsTrigger;
import net.minecraft.advancements.critereon.BrewedPotionTrigger;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.advancements.critereon.ChanneledLightningTrigger;
import net.minecraft.advancements.critereon.ConstructBeaconTrigger;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.CuredZombieVillagerTrigger;
import net.minecraft.advancements.critereon.DistanceTrigger;
import net.minecraft.advancements.critereon.EffectsChangedTrigger;
import net.minecraft.advancements.critereon.EnchantedItemTrigger;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.EntityHurtPlayerTrigger;
import net.minecraft.advancements.critereon.FilledBucketTrigger;
import net.minecraft.advancements.critereon.FishingRodHookedTrigger;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemDurabilityTrigger;
import net.minecraft.advancements.critereon.ItemPickedUpByEntityTrigger;
import net.minecraft.advancements.critereon.ItemUsedOnBlockTrigger;
import net.minecraft.advancements.critereon.KilledByCrossbowTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.LevitationTrigger;
import net.minecraft.advancements.critereon.LightningStrikeTrigger;
import net.minecraft.advancements.critereon.LocationTrigger;
import net.minecraft.advancements.critereon.LootTableTrigger;
import net.minecraft.advancements.critereon.PlacedBlockTrigger;
import net.minecraft.advancements.critereon.PlayerHurtEntityTrigger;
import net.minecraft.advancements.critereon.PlayerInteractTrigger;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.advancements.critereon.ShotCrossbowTrigger;
import net.minecraft.advancements.critereon.SlideDownBlockTrigger;
import net.minecraft.advancements.critereon.StartRidingTrigger;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.advancements.critereon.TameAnimalTrigger;
import net.minecraft.advancements.critereon.TargetBlockTrigger;
import net.minecraft.advancements.critereon.TickTrigger;
import net.minecraft.advancements.critereon.TradeTrigger;
import net.minecraft.advancements.critereon.UsedEnderEyeTrigger;
import net.minecraft.advancements.critereon.UsedTotemTrigger;
import net.minecraft.advancements.critereon.UsingItemTrigger;
import net.minecraft.resources.ResourceLocation;

public class CriteriaTriggers {
   private static final Map<ResourceLocation, CriterionTrigger<?>> CRITERIA = Maps.newHashMap();
   public static final ImpossibleTrigger IMPOSSIBLE = register(new ImpossibleTrigger());
   public static final KilledTrigger PLAYER_KILLED_ENTITY = register(new KilledTrigger(new ResourceLocation("player_killed_entity")));
   public static final KilledTrigger ENTITY_KILLED_PLAYER = register(new KilledTrigger(new ResourceLocation("entity_killed_player")));
   public static final EnterBlockTrigger ENTER_BLOCK = register(new EnterBlockTrigger());
   public static final InventoryChangeTrigger INVENTORY_CHANGED = register(new InventoryChangeTrigger());
   public static final RecipeUnlockedTrigger RECIPE_UNLOCKED = register(new RecipeUnlockedTrigger());
   public static final PlayerHurtEntityTrigger PLAYER_HURT_ENTITY = register(new PlayerHurtEntityTrigger());
   public static final EntityHurtPlayerTrigger ENTITY_HURT_PLAYER = register(new EntityHurtPlayerTrigger());
   public static final EnchantedItemTrigger ENCHANTED_ITEM = register(new EnchantedItemTrigger());
   public static final FilledBucketTrigger FILLED_BUCKET = register(new FilledBucketTrigger());
   public static final BrewedPotionTrigger BREWED_POTION = register(new BrewedPotionTrigger());
   public static final ConstructBeaconTrigger CONSTRUCT_BEACON = register(new ConstructBeaconTrigger());
   public static final UsedEnderEyeTrigger USED_ENDER_EYE = register(new UsedEnderEyeTrigger());
   public static final SummonedEntityTrigger SUMMONED_ENTITY = register(new SummonedEntityTrigger());
   public static final BredAnimalsTrigger BRED_ANIMALS = register(new BredAnimalsTrigger());
   public static final LocationTrigger LOCATION = register(new LocationTrigger(new ResourceLocation("location")));
   public static final LocationTrigger SLEPT_IN_BED = register(new LocationTrigger(new ResourceLocation("slept_in_bed")));
   public static final CuredZombieVillagerTrigger CURED_ZOMBIE_VILLAGER = register(new CuredZombieVillagerTrigger());
   public static final TradeTrigger TRADE = register(new TradeTrigger());
   public static final ItemDurabilityTrigger ITEM_DURABILITY_CHANGED = register(new ItemDurabilityTrigger());
   public static final LevitationTrigger LEVITATION = register(new LevitationTrigger());
   public static final ChangeDimensionTrigger CHANGED_DIMENSION = register(new ChangeDimensionTrigger());
   public static final TickTrigger TICK = register(new TickTrigger());
   public static final TameAnimalTrigger TAME_ANIMAL = register(new TameAnimalTrigger());
   public static final PlacedBlockTrigger PLACED_BLOCK = register(new PlacedBlockTrigger());
   public static final ConsumeItemTrigger CONSUME_ITEM = register(new ConsumeItemTrigger());
   public static final EffectsChangedTrigger EFFECTS_CHANGED = register(new EffectsChangedTrigger());
   public static final UsedTotemTrigger USED_TOTEM = register(new UsedTotemTrigger());
   public static final DistanceTrigger NETHER_TRAVEL = register(new DistanceTrigger(new ResourceLocation("nether_travel")));
   public static final FishingRodHookedTrigger FISHING_ROD_HOOKED = register(new FishingRodHookedTrigger());
   public static final ChanneledLightningTrigger CHANNELED_LIGHTNING = register(new ChanneledLightningTrigger());
   public static final ShotCrossbowTrigger SHOT_CROSSBOW = register(new ShotCrossbowTrigger());
   public static final KilledByCrossbowTrigger KILLED_BY_CROSSBOW = register(new KilledByCrossbowTrigger());
   public static final LocationTrigger RAID_WIN = register(new LocationTrigger(new ResourceLocation("hero_of_the_village")));
   public static final LocationTrigger BAD_OMEN = register(new LocationTrigger(new ResourceLocation("voluntary_exile")));
   public static final SlideDownBlockTrigger HONEY_BLOCK_SLIDE = register(new SlideDownBlockTrigger());
   public static final BeeNestDestroyedTrigger BEE_NEST_DESTROYED = register(new BeeNestDestroyedTrigger());
   public static final TargetBlockTrigger TARGET_BLOCK_HIT = register(new TargetBlockTrigger());
   public static final ItemUsedOnBlockTrigger ITEM_USED_ON_BLOCK = register(new ItemUsedOnBlockTrigger());
   public static final LootTableTrigger GENERATE_LOOT = register(new LootTableTrigger());
   public static final ItemPickedUpByEntityTrigger ITEM_PICKED_UP_BY_ENTITY = register(new ItemPickedUpByEntityTrigger());
   public static final PlayerInteractTrigger PLAYER_INTERACTED_WITH_ENTITY = register(new PlayerInteractTrigger());
   public static final StartRidingTrigger START_RIDING_TRIGGER = register(new StartRidingTrigger());
   public static final LightningStrikeTrigger LIGHTNING_STRIKE = register(new LightningStrikeTrigger());
   public static final UsingItemTrigger USING_ITEM = register(new UsingItemTrigger());
   public static final DistanceTrigger FALL_FROM_HEIGHT = register(new DistanceTrigger(new ResourceLocation("fall_from_height")));
   public static final DistanceTrigger RIDE_ENTITY_IN_LAVA_TRIGGER = register(new DistanceTrigger(new ResourceLocation("ride_entity_in_lava")));

   public static <T extends CriterionTrigger<?>> T register(T p_10596_) {
      if (CRITERIA.containsKey(p_10596_.getId())) {
         throw new IllegalArgumentException("Duplicate criterion id " + p_10596_.getId());
      } else {
         CRITERIA.put(p_10596_.getId(), p_10596_);
         return p_10596_;
      }
   }

   @Nullable
   public static <T extends CriterionTriggerInstance> CriterionTrigger<T> getCriterion(ResourceLocation p_10598_) {
      return (CriterionTrigger<T>)CRITERIA.get(p_10598_);
   }

   public static Iterable<? extends CriterionTrigger<?>> all() {
      return CRITERIA.values();
   }
}