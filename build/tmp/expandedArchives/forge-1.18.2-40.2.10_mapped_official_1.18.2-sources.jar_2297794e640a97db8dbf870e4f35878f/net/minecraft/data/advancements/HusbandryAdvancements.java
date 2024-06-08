package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.BeeNestDestroyedTrigger;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.BredAnimalsTrigger;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.EffectsChangedTrigger;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.FilledBucketTrigger;
import net.minecraft.advancements.critereon.FishingRodHookedTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnBlockTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.PlacedBlockTrigger;
import net.minecraft.advancements.critereon.StartRidingTrigger;
import net.minecraft.advancements.critereon.TameAnimalTrigger;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;

public class HusbandryAdvancements implements Consumer<Consumer<Advancement>> {
   private static final EntityType<?>[] BREEDABLE_ANIMALS = new EntityType[]{EntityType.HORSE, EntityType.DONKEY, EntityType.MULE, EntityType.SHEEP, EntityType.COW, EntityType.MOOSHROOM, EntityType.PIG, EntityType.CHICKEN, EntityType.WOLF, EntityType.OCELOT, EntityType.RABBIT, EntityType.LLAMA, EntityType.CAT, EntityType.PANDA, EntityType.FOX, EntityType.BEE, EntityType.HOGLIN, EntityType.STRIDER, EntityType.GOAT, EntityType.AXOLOTL};
   private static final Item[] FISH = new Item[]{Items.COD, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.SALMON};
   private static final Item[] FISH_BUCKETS = new Item[]{Items.COD_BUCKET, Items.TROPICAL_FISH_BUCKET, Items.PUFFERFISH_BUCKET, Items.SALMON_BUCKET};
   private static final Item[] EDIBLE_ITEMS = new Item[]{Items.APPLE, Items.MUSHROOM_STEW, Items.BREAD, Items.PORKCHOP, Items.COOKED_PORKCHOP, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.COOKED_COD, Items.COOKED_SALMON, Items.COOKIE, Items.MELON_SLICE, Items.BEEF, Items.COOKED_BEEF, Items.CHICKEN, Items.COOKED_CHICKEN, Items.ROTTEN_FLESH, Items.SPIDER_EYE, Items.CARROT, Items.POTATO, Items.BAKED_POTATO, Items.POISONOUS_POTATO, Items.GOLDEN_CARROT, Items.PUMPKIN_PIE, Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.MUTTON, Items.COOKED_MUTTON, Items.CHORUS_FRUIT, Items.BEETROOT, Items.BEETROOT_SOUP, Items.DRIED_KELP, Items.SUSPICIOUS_STEW, Items.SWEET_BERRIES, Items.HONEY_BOTTLE, Items.GLOW_BERRIES};
   private static final Item[] WAX_SCRAPING_TOOLS = new Item[]{Items.WOODEN_AXE, Items.GOLDEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE};

   public void accept(Consumer<Advancement> p_123998_) {
      Advancement advancement = Advancement.Builder.advancement().display(Blocks.HAY_BLOCK, new TranslatableComponent("advancements.husbandry.root.title"), new TranslatableComponent("advancements.husbandry.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/husbandry.png"), FrameType.TASK, false, false, false).addCriterion("consumed_item", ConsumeItemTrigger.TriggerInstance.usedItem()).save(p_123998_, "husbandry/root");
      Advancement advancement1 = Advancement.Builder.advancement().parent(advancement).display(Items.WHEAT, new TranslatableComponent("advancements.husbandry.plant_seed.title"), new TranslatableComponent("advancements.husbandry.plant_seed.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("wheat", PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.WHEAT)).addCriterion("pumpkin_stem", PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.PUMPKIN_STEM)).addCriterion("melon_stem", PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.MELON_STEM)).addCriterion("beetroots", PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.BEETROOTS)).addCriterion("nether_wart", PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.NETHER_WART)).save(p_123998_, "husbandry/plant_seed");
      Advancement advancement2 = Advancement.Builder.advancement().parent(advancement).display(Items.WHEAT, new TranslatableComponent("advancements.husbandry.breed_an_animal.title"), new TranslatableComponent("advancements.husbandry.breed_an_animal.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("bred", BredAnimalsTrigger.TriggerInstance.bredAnimals()).save(p_123998_, "husbandry/breed_an_animal");
      this.addFood(Advancement.Builder.advancement()).parent(advancement1).display(Items.APPLE, new TranslatableComponent("advancements.husbandry.balanced_diet.title"), new TranslatableComponent("advancements.husbandry.balanced_diet.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).save(p_123998_, "husbandry/balanced_diet");
      Advancement.Builder.advancement().parent(advancement1).display(Items.NETHERITE_HOE, new TranslatableComponent("advancements.husbandry.netherite_hoe.title"), new TranslatableComponent("advancements.husbandry.netherite_hoe.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).addCriterion("netherite_hoe", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_HOE)).save(p_123998_, "husbandry/obtain_netherite_hoe");
      Advancement advancement3 = Advancement.Builder.advancement().parent(advancement).display(Items.LEAD, new TranslatableComponent("advancements.husbandry.tame_an_animal.title"), new TranslatableComponent("advancements.husbandry.tame_an_animal.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("tamed_animal", TameAnimalTrigger.TriggerInstance.tamedAnimal()).save(p_123998_, "husbandry/tame_an_animal");
      this.addBreedable(Advancement.Builder.advancement()).parent(advancement2).display(Items.GOLDEN_CARROT, new TranslatableComponent("advancements.husbandry.breed_all_animals.title"), new TranslatableComponent("advancements.husbandry.breed_all_animals.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).save(p_123998_, "husbandry/bred_all_animals");
      Advancement advancement4 = this.addFish(Advancement.Builder.advancement()).parent(advancement).requirements(RequirementsStrategy.OR).display(Items.FISHING_ROD, new TranslatableComponent("advancements.husbandry.fishy_business.title"), new TranslatableComponent("advancements.husbandry.fishy_business.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(p_123998_, "husbandry/fishy_business");
      Advancement advancement5 = this.addFishBuckets(Advancement.Builder.advancement()).parent(advancement4).requirements(RequirementsStrategy.OR).display(Items.PUFFERFISH_BUCKET, new TranslatableComponent("advancements.husbandry.tactical_fishing.title"), new TranslatableComponent("advancements.husbandry.tactical_fishing.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(p_123998_, "husbandry/tactical_fishing");
      Advancement advancement6 = Advancement.Builder.advancement().parent(advancement5).requirements(RequirementsStrategy.OR).addCriterion(Registry.ITEM.getKey(Items.AXOLOTL_BUCKET).getPath(), FilledBucketTrigger.TriggerInstance.filledBucket(ItemPredicate.Builder.item().of(Items.AXOLOTL_BUCKET).build())).display(Items.AXOLOTL_BUCKET, new TranslatableComponent("advancements.husbandry.axolotl_in_a_bucket.title"), new TranslatableComponent("advancements.husbandry.axolotl_in_a_bucket.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(p_123998_, "husbandry/axolotl_in_a_bucket");
      Advancement.Builder.advancement().parent(advancement6).addCriterion("kill_axolotl_target", EffectsChangedTrigger.TriggerInstance.gotEffectsFrom(EntityPredicate.Builder.entity().of(EntityType.AXOLOTL).build())).display(Items.TROPICAL_FISH_BUCKET, new TranslatableComponent("advancements.husbandry.kill_axolotl_target.title"), new TranslatableComponent("advancements.husbandry.kill_axolotl_target.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(p_123998_, "husbandry/kill_axolotl_target");
      this.addCatVariants(Advancement.Builder.advancement()).parent(advancement3).display(Items.COD, new TranslatableComponent("advancements.husbandry.complete_catalogue.title"), new TranslatableComponent("advancements.husbandry.complete_catalogue.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).save(p_123998_, "husbandry/complete_catalogue");
      Advancement advancement7 = Advancement.Builder.advancement().parent(advancement).addCriterion("safely_harvest_honey", ItemUsedOnBlockTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockTags.BEEHIVES).build()).setSmokey(true), ItemPredicate.Builder.item().of(Items.GLASS_BOTTLE))).display(Items.HONEY_BOTTLE, new TranslatableComponent("advancements.husbandry.safely_harvest_honey.title"), new TranslatableComponent("advancements.husbandry.safely_harvest_honey.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(p_123998_, "husbandry/safely_harvest_honey");
      Advancement advancement8 = Advancement.Builder.advancement().parent(advancement7).display(Items.HONEYCOMB, new TranslatableComponent("advancements.husbandry.wax_on.title"), new TranslatableComponent("advancements.husbandry.wax_on.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("wax_on", ItemUsedOnBlockTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(HoneycombItem.WAXABLES.get().keySet()).build()), ItemPredicate.Builder.item().of(Items.HONEYCOMB))).save(p_123998_, "husbandry/wax_on");
      Advancement.Builder.advancement().parent(advancement8).display(Items.STONE_AXE, new TranslatableComponent("advancements.husbandry.wax_off.title"), new TranslatableComponent("advancements.husbandry.wax_off.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("wax_off", ItemUsedOnBlockTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(HoneycombItem.WAX_OFF_BY_BLOCK.get().keySet()).build()), ItemPredicate.Builder.item().of(WAX_SCRAPING_TOOLS))).save(p_123998_, "husbandry/wax_off");
      Advancement.Builder.advancement().parent(advancement).addCriterion("silk_touch_nest", BeeNestDestroyedTrigger.TriggerInstance.destroyedBeeNest(Blocks.BEE_NEST, ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))), MinMaxBounds.Ints.exactly(3))).display(Blocks.BEE_NEST, new TranslatableComponent("advancements.husbandry.silk_touch_nest.title"), new TranslatableComponent("advancements.husbandry.silk_touch_nest.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(p_123998_, "husbandry/silk_touch_nest");
      Advancement.Builder.advancement().parent(advancement).display(Items.OAK_BOAT, new TranslatableComponent("advancements.husbandry.ride_a_boat_with_a_goat.title"), new TranslatableComponent("advancements.husbandry.ride_a_boat_with_a_goat.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("ride_a_boat_with_a_goat", StartRidingTrigger.TriggerInstance.playerStartsRiding(EntityPredicate.Builder.entity().vehicle(EntityPredicate.Builder.entity().of(EntityType.BOAT).passenger(EntityPredicate.Builder.entity().of(EntityType.GOAT).build()).build()))).save(p_123998_, "husbandry/ride_a_boat_with_a_goat");
      Advancement.Builder.advancement().parent(advancement).display(Items.GLOW_INK_SAC, new TranslatableComponent("advancements.husbandry.make_a_sign_glow.title"), new TranslatableComponent("advancements.husbandry.make_a_sign_glow.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("make_a_sign_glow", ItemUsedOnBlockTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockTags.SIGNS).build()), ItemPredicate.Builder.item().of(Items.GLOW_INK_SAC))).save(p_123998_, "husbandry/make_a_sign_glow");
   }

   private Advancement.Builder addFood(Advancement.Builder p_124000_) {
      for(Item item : EDIBLE_ITEMS) {
         p_124000_.addCriterion(Registry.ITEM.getKey(item).getPath(), ConsumeItemTrigger.TriggerInstance.usedItem(item));
      }

      return p_124000_;
   }

   private Advancement.Builder addBreedable(Advancement.Builder p_124008_) {
      for(EntityType<?> entitytype : BREEDABLE_ANIMALS) {
         p_124008_.addCriterion(EntityType.getKey(entitytype).toString(), BredAnimalsTrigger.TriggerInstance.bredAnimals(EntityPredicate.Builder.entity().of(entitytype)));
      }

      p_124008_.addCriterion(EntityType.getKey(EntityType.TURTLE).toString(), BredAnimalsTrigger.TriggerInstance.bredAnimals(EntityPredicate.Builder.entity().of(EntityType.TURTLE).build(), EntityPredicate.Builder.entity().of(EntityType.TURTLE).build(), EntityPredicate.ANY));
      return p_124008_;
   }

   private Advancement.Builder addFishBuckets(Advancement.Builder p_124010_) {
      for(Item item : FISH_BUCKETS) {
         p_124010_.addCriterion(Registry.ITEM.getKey(item).getPath(), FilledBucketTrigger.TriggerInstance.filledBucket(ItemPredicate.Builder.item().of(item).build()));
      }

      return p_124010_;
   }

   private Advancement.Builder addFish(Advancement.Builder p_124012_) {
      for(Item item : FISH) {
         p_124012_.addCriterion(Registry.ITEM.getKey(item).getPath(), FishingRodHookedTrigger.TriggerInstance.fishedItem(ItemPredicate.ANY, EntityPredicate.ANY, ItemPredicate.Builder.item().of(item).build()));
      }

      return p_124012_;
   }

   private Advancement.Builder addCatVariants(Advancement.Builder p_124014_) {
      Cat.TEXTURE_BY_TYPE.forEach((p_124003_, p_124004_) -> {
         p_124014_.addCriterion(p_124004_.getPath(), TameAnimalTrigger.TriggerInstance.tamedAnimal(EntityPredicate.Builder.entity().of(p_124004_).build()));
      });
      return p_124014_;
   }
}