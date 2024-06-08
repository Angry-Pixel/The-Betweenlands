package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class GiveGiftToHero extends Behavior<Villager> {
   private static final int THROW_GIFT_AT_DISTANCE = 5;
   private static final int MIN_TIME_BETWEEN_GIFTS = 600;
   private static final int MAX_TIME_BETWEEN_GIFTS = 6600;
   private static final int TIME_TO_DELAY_FOR_HEAD_TO_FINISH_TURNING = 20;
   private static final Map<VillagerProfession, ResourceLocation> GIFTS = Util.make(Maps.newHashMap(), (p_23020_) -> {
      p_23020_.put(VillagerProfession.ARMORER, BuiltInLootTables.ARMORER_GIFT);
      p_23020_.put(VillagerProfession.BUTCHER, BuiltInLootTables.BUTCHER_GIFT);
      p_23020_.put(VillagerProfession.CARTOGRAPHER, BuiltInLootTables.CARTOGRAPHER_GIFT);
      p_23020_.put(VillagerProfession.CLERIC, BuiltInLootTables.CLERIC_GIFT);
      p_23020_.put(VillagerProfession.FARMER, BuiltInLootTables.FARMER_GIFT);
      p_23020_.put(VillagerProfession.FISHERMAN, BuiltInLootTables.FISHERMAN_GIFT);
      p_23020_.put(VillagerProfession.FLETCHER, BuiltInLootTables.FLETCHER_GIFT);
      p_23020_.put(VillagerProfession.LEATHERWORKER, BuiltInLootTables.LEATHERWORKER_GIFT);
      p_23020_.put(VillagerProfession.LIBRARIAN, BuiltInLootTables.LIBRARIAN_GIFT);
      p_23020_.put(VillagerProfession.MASON, BuiltInLootTables.MASON_GIFT);
      p_23020_.put(VillagerProfession.SHEPHERD, BuiltInLootTables.SHEPHERD_GIFT);
      p_23020_.put(VillagerProfession.TOOLSMITH, BuiltInLootTables.TOOLSMITH_GIFT);
      p_23020_.put(VillagerProfession.WEAPONSMITH, BuiltInLootTables.WEAPONSMITH_GIFT);
   });
   private static final float SPEED_MODIFIER = 0.5F;
   private int timeUntilNextGift = 600;
   private boolean giftGivenDuringThisRun;
   private long timeSinceStart;

   public GiveGiftToHero(int p_22992_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryStatus.VALUE_PRESENT), p_22992_);
   }

   protected boolean checkExtraStartConditions(ServerLevel p_23003_, Villager p_23004_) {
      if (!this.isHeroVisible(p_23004_)) {
         return false;
      } else if (this.timeUntilNextGift > 0) {
         --this.timeUntilNextGift;
         return false;
      } else {
         return true;
      }
   }

   protected void start(ServerLevel p_23006_, Villager p_23007_, long p_23008_) {
      this.giftGivenDuringThisRun = false;
      this.timeSinceStart = p_23008_;
      Player player = this.getNearestTargetableHero(p_23007_).get();
      p_23007_.getBrain().setMemory(MemoryModuleType.INTERACTION_TARGET, player);
      BehaviorUtils.lookAtEntity(p_23007_, player);
   }

   protected boolean canStillUse(ServerLevel p_23026_, Villager p_23027_, long p_23028_) {
      return this.isHeroVisible(p_23027_) && !this.giftGivenDuringThisRun;
   }

   protected void tick(ServerLevel p_23036_, Villager p_23037_, long p_23038_) {
      Player player = this.getNearestTargetableHero(p_23037_).get();
      BehaviorUtils.lookAtEntity(p_23037_, player);
      if (this.isWithinThrowingDistance(p_23037_, player)) {
         if (p_23038_ - this.timeSinceStart > 20L) {
            this.throwGift(p_23037_, player);
            this.giftGivenDuringThisRun = true;
         }
      } else {
         BehaviorUtils.setWalkAndLookTargetMemories(p_23037_, player, 0.5F, 5);
      }

   }

   protected void stop(ServerLevel p_23046_, Villager p_23047_, long p_23048_) {
      this.timeUntilNextGift = calculateTimeUntilNextGift(p_23046_);
      p_23047_.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
      p_23047_.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      p_23047_.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
   }

   private void throwGift(Villager p_23012_, LivingEntity p_23013_) {
      for(ItemStack itemstack : this.getItemToThrow(p_23012_)) {
         BehaviorUtils.throwItem(p_23012_, itemstack, p_23013_.position());
      }

   }

   private List<ItemStack> getItemToThrow(Villager p_23010_) {
      if (p_23010_.isBaby()) {
         return ImmutableList.of(new ItemStack(Items.POPPY));
      } else {
         VillagerProfession villagerprofession = p_23010_.getVillagerData().getProfession();
         if (GIFTS.containsKey(villagerprofession)) {
            LootTable loottable = p_23010_.level.getServer().getLootTables().get(GIFTS.get(villagerprofession));
            LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel)p_23010_.level)).withParameter(LootContextParams.ORIGIN, p_23010_.position()).withParameter(LootContextParams.THIS_ENTITY, p_23010_).withRandom(p_23010_.getRandom());
            return loottable.getRandomItems(lootcontext$builder.create(LootContextParamSets.GIFT));
         } else {
            return ImmutableList.of(new ItemStack(Items.WHEAT_SEEDS));
         }
      }
   }

   private boolean isHeroVisible(Villager p_23030_) {
      return this.getNearestTargetableHero(p_23030_).isPresent();
   }

   private Optional<Player> getNearestTargetableHero(Villager p_23040_) {
      return p_23040_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).filter(this::isHero);
   }

   private boolean isHero(Player p_23018_) {
      return p_23018_.hasEffect(MobEffects.HERO_OF_THE_VILLAGE);
   }

   private boolean isWithinThrowingDistance(Villager p_23015_, Player p_23016_) {
      BlockPos blockpos = p_23016_.blockPosition();
      BlockPos blockpos1 = p_23015_.blockPosition();
      return blockpos1.closerThan(blockpos, 5.0D);
   }

   private static int calculateTimeUntilNextGift(ServerLevel p_22994_) {
      return 600 + p_22994_.random.nextInt(6001);
   }
}