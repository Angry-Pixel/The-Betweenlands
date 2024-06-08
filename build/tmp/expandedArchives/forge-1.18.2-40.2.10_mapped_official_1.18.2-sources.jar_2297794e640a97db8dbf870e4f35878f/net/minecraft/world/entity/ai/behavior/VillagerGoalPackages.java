package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;

public class VillagerGoalPackages {
   private static final float STROLL_SPEED_MODIFIER = 0.4F;

   public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getCorePackage(VillagerProfession p_24586_, float p_24587_) {
      return ImmutableList.of(Pair.of(0, new Swim(0.8F)), Pair.of(0, new InteractWithDoor()), Pair.of(0, new LookAtTargetSink(45, 90)), Pair.of(0, new VillagerPanicTrigger()), Pair.of(0, new WakeUp()), Pair.of(0, new ReactToBell()), Pair.of(0, new SetRaidStatus()), Pair.of(0, new ValidateNearbyPoi(p_24586_.getJobPoiType(), MemoryModuleType.JOB_SITE)), Pair.of(0, new ValidateNearbyPoi(p_24586_.getJobPoiType(), MemoryModuleType.POTENTIAL_JOB_SITE)), Pair.of(1, new MoveToTargetSink()), Pair.of(2, new PoiCompetitorScan(p_24586_)), Pair.of(3, new LookAndFollowTradingPlayerSink(p_24587_)), Pair.of(5, new GoToWantedItem(p_24587_, false, 4)), Pair.of(6, new AcquirePoi(p_24586_.getJobPoiType(), MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, true, Optional.empty())), Pair.of(7, new GoToPotentialJobSite(p_24587_)), Pair.of(8, new YieldJobSite(p_24587_)), Pair.of(10, new AcquirePoi(PoiType.HOME, MemoryModuleType.HOME, false, Optional.of((byte)14))), Pair.of(10, new AcquirePoi(PoiType.MEETING, MemoryModuleType.MEETING_POINT, true, Optional.of((byte)14))), Pair.of(10, new AssignProfessionFromJobSite()), Pair.of(10, new ResetProfession()));
   }

   public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getWorkPackage(VillagerProfession p_24590_, float p_24591_) {
      WorkAtPoi workatpoi;
      if (p_24590_ == VillagerProfession.FARMER) {
         workatpoi = new WorkAtComposter();
      } else {
         workatpoi = new WorkAtPoi();
      }

      return ImmutableList.of(getMinimalLookBehavior(), Pair.of(5, new RunOne<>(ImmutableList.of(Pair.of(workatpoi, 7), Pair.of(new StrollAroundPoi(MemoryModuleType.JOB_SITE, 0.4F, 4), 2), Pair.of(new StrollToPoi(MemoryModuleType.JOB_SITE, 0.4F, 1, 10), 5), Pair.of(new StrollToPoiList(MemoryModuleType.SECONDARY_JOB_SITE, p_24591_, 1, 6, MemoryModuleType.JOB_SITE), 5), Pair.of(new HarvestFarmland(), p_24590_ == VillagerProfession.FARMER ? 2 : 5), Pair.of(new UseBonemeal(), p_24590_ == VillagerProfession.FARMER ? 4 : 7)))), Pair.of(10, new ShowTradesToPlayer(400, 1600)), Pair.of(10, new SetLookAndInteract(EntityType.PLAYER, 4)), Pair.of(2, new SetWalkTargetFromBlockMemory(MemoryModuleType.JOB_SITE, p_24591_, 9, 100, 1200)), Pair.of(3, new GiveGiftToHero(100)), Pair.of(99, new UpdateActivityFromSchedule()));
   }

   public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getPlayPackage(float p_24584_) {
      return ImmutableList.of(Pair.of(0, new MoveToTargetSink(80, 120)), getFullLookBehavior(), Pair.of(5, new PlayTagWithOtherKids()), Pair.of(5, new RunOne<>(ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryStatus.VALUE_ABSENT), ImmutableList.of(Pair.of(InteractWith.of(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, p_24584_, 2), 2), Pair.of(InteractWith.of(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, p_24584_, 2), 1), Pair.of(new VillageBoundRandomStroll(p_24584_), 1), Pair.of(new SetWalkTargetFromLookTarget(p_24584_, 2), 1), Pair.of(new JumpOnBed(p_24584_), 2), Pair.of(new DoNothing(20, 40), 2)))), Pair.of(99, new UpdateActivityFromSchedule()));
   }

   public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getRestPackage(VillagerProfession p_24593_, float p_24594_) {
      return ImmutableList.of(Pair.of(2, new SetWalkTargetFromBlockMemory(MemoryModuleType.HOME, p_24594_, 1, 150, 1200)), Pair.of(3, new ValidateNearbyPoi(PoiType.HOME, MemoryModuleType.HOME)), Pair.of(3, new SleepInBed()), Pair.of(5, new RunOne<>(ImmutableMap.of(MemoryModuleType.HOME, MemoryStatus.VALUE_ABSENT), ImmutableList.of(Pair.of(new SetClosestHomeAsWalkTarget(p_24594_), 1), Pair.of(new InsideBrownianWalk(p_24594_), 4), Pair.of(new GoToClosestVillage(p_24594_, 4), 2), Pair.of(new DoNothing(20, 40), 2)))), getMinimalLookBehavior(), Pair.of(99, new UpdateActivityFromSchedule()));
   }

   public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getMeetPackage(VillagerProfession p_24596_, float p_24597_) {
      return ImmutableList.of(Pair.of(2, new RunOne<>(ImmutableList.of(Pair.of(new StrollAroundPoi(MemoryModuleType.MEETING_POINT, 0.4F, 40), 2), Pair.of(new SocializeAtBell(), 2)))), Pair.of(10, new ShowTradesToPlayer(400, 1600)), Pair.of(10, new SetLookAndInteract(EntityType.PLAYER, 4)), Pair.of(2, new SetWalkTargetFromBlockMemory(MemoryModuleType.MEETING_POINT, p_24597_, 6, 100, 200)), Pair.of(3, new GiveGiftToHero(100)), Pair.of(3, new ValidateNearbyPoi(PoiType.MEETING, MemoryModuleType.MEETING_POINT)), Pair.of(3, new GateBehavior<>(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, ImmutableList.of(Pair.of(new TradeWithVillager(), 1)))), getFullLookBehavior(), Pair.of(99, new UpdateActivityFromSchedule()));
   }

   public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getIdlePackage(VillagerProfession p_24599_, float p_24600_) {
      return ImmutableList.of(Pair.of(2, new RunOne<>(ImmutableList.of(Pair.of(InteractWith.of(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, p_24600_, 2), 2), Pair.of(new InteractWith<>(EntityType.VILLAGER, 8, AgeableMob::canBreed, AgeableMob::canBreed, MemoryModuleType.BREED_TARGET, p_24600_, 2), 1), Pair.of(InteractWith.of(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, p_24600_, 2), 1), Pair.of(new VillageBoundRandomStroll(p_24600_), 1), Pair.of(new SetWalkTargetFromLookTarget(p_24600_, 2), 1), Pair.of(new JumpOnBed(p_24600_), 1), Pair.of(new DoNothing(30, 60), 1)))), Pair.of(3, new GiveGiftToHero(100)), Pair.of(3, new SetLookAndInteract(EntityType.PLAYER, 4)), Pair.of(3, new ShowTradesToPlayer(400, 1600)), Pair.of(3, new GateBehavior<>(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, ImmutableList.of(Pair.of(new TradeWithVillager(), 1)))), Pair.of(3, new GateBehavior<>(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.BREED_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, ImmutableList.of(Pair.of(new VillagerMakeLove(), 1)))), getFullLookBehavior(), Pair.of(99, new UpdateActivityFromSchedule()));
   }

   public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getPanicPackage(VillagerProfession p_24602_, float p_24603_) {
      float f = p_24603_ * 1.5F;
      return ImmutableList.of(Pair.of(0, new VillagerCalmDown()), Pair.of(1, SetWalkTargetAwayFrom.entity(MemoryModuleType.NEAREST_HOSTILE, f, 6, false)), Pair.of(1, SetWalkTargetAwayFrom.entity(MemoryModuleType.HURT_BY_ENTITY, f, 6, false)), Pair.of(3, new VillageBoundRandomStroll(f, 2, 2)), getMinimalLookBehavior());
   }

   public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getPreRaidPackage(VillagerProfession p_24605_, float p_24606_) {
      return ImmutableList.of(Pair.of(0, new RingBell()), Pair.of(0, new RunOne<>(ImmutableList.of(Pair.of(new SetWalkTargetFromBlockMemory(MemoryModuleType.MEETING_POINT, p_24606_ * 1.5F, 2, 150, 200), 6), Pair.of(new VillageBoundRandomStroll(p_24606_ * 1.5F), 2)))), getMinimalLookBehavior(), Pair.of(99, new ResetRaidStatus()));
   }

   public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getRaidPackage(VillagerProfession p_24608_, float p_24609_) {
      return ImmutableList.of(Pair.of(0, new RunOne<>(ImmutableList.of(Pair.of(new GoOutsideToCelebrate(p_24609_), 5), Pair.of(new VictoryStroll(p_24609_ * 1.1F), 2)))), Pair.of(0, new CelebrateVillagersSurvivedRaid(600, 600)), Pair.of(2, new LocateHidingPlaceDuringRaid(24, p_24609_ * 1.4F)), getMinimalLookBehavior(), Pair.of(99, new ResetRaidStatus()));
   }

   public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getHidePackage(VillagerProfession p_24611_, float p_24612_) {
      int i = 2;
      return ImmutableList.of(Pair.of(0, new SetHiddenState(15, 3)), Pair.of(1, new LocateHidingPlace(32, p_24612_ * 1.25F, 2)), getMinimalLookBehavior());
   }

   private static Pair<Integer, Behavior<LivingEntity>> getFullLookBehavior() {
      return Pair.of(5, new RunOne<>(ImmutableList.of(Pair.of(new SetEntityLookTarget(EntityType.CAT, 8.0F), 8), Pair.of(new SetEntityLookTarget(EntityType.VILLAGER, 8.0F), 2), Pair.of(new SetEntityLookTarget(EntityType.PLAYER, 8.0F), 2), Pair.of(new SetEntityLookTarget(MobCategory.CREATURE, 8.0F), 1), Pair.of(new SetEntityLookTarget(MobCategory.WATER_CREATURE, 8.0F), 1), Pair.of(new SetEntityLookTarget(MobCategory.AXOLOTLS, 8.0F), 1), Pair.of(new SetEntityLookTarget(MobCategory.UNDERGROUND_WATER_CREATURE, 8.0F), 1), Pair.of(new SetEntityLookTarget(MobCategory.WATER_AMBIENT, 8.0F), 1), Pair.of(new SetEntityLookTarget(MobCategory.MONSTER, 8.0F), 1), Pair.of(new DoNothing(30, 60), 2))));
   }

   private static Pair<Integer, Behavior<LivingEntity>> getMinimalLookBehavior() {
      return Pair.of(5, new RunOne<>(ImmutableList.of(Pair.of(new SetEntityLookTarget(EntityType.VILLAGER, 8.0F), 2), Pair.of(new SetEntityLookTarget(EntityType.PLAYER, 8.0F), 2), Pair.of(new DoNothing(30, 60), 8))));
   }
}