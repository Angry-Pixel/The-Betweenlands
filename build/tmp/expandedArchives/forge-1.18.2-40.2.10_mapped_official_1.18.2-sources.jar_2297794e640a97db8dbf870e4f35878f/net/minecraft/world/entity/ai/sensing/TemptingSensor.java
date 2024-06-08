package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class TemptingSensor extends Sensor<PathfinderMob> {
   public static final int TEMPTATION_RANGE = 10;
   private static final TargetingConditions TEMPT_TARGETING = TargetingConditions.forNonCombat().range(10.0D).ignoreLineOfSight();
   private final Ingredient temptations;

   public TemptingSensor(Ingredient p_148325_) {
      this.temptations = p_148325_;
   }

   protected void doTick(ServerLevel p_148331_, PathfinderMob p_148332_) {
      Brain<?> brain = p_148332_.getBrain();
      List<Player> list = p_148331_.players().stream().filter(EntitySelector.NO_SPECTATORS).filter((p_148342_) -> {
         return TEMPT_TARGETING.test(p_148332_, p_148342_);
      }).filter((p_148335_) -> {
         return p_148332_.closerThan(p_148335_, 10.0D);
      }).filter(this::playerHoldingTemptation).sorted(Comparator.comparingDouble(p_148332_::distanceToSqr)).collect(Collectors.toList());
      if (!list.isEmpty()) {
         Player player = list.get(0);
         brain.setMemory(MemoryModuleType.TEMPTING_PLAYER, player);
      } else {
         brain.eraseMemory(MemoryModuleType.TEMPTING_PLAYER);
      }

   }

   private boolean playerHoldingTemptation(Player p_148337_) {
      return this.isTemptation(p_148337_.getMainHandItem()) || this.isTemptation(p_148337_.getOffhandItem());
   }

   private boolean isTemptation(ItemStack p_148339_) {
      return this.temptations.test(p_148339_);
   }

   public Set<MemoryModuleType<?>> requires() {
      return ImmutableSet.of(MemoryModuleType.TEMPTING_PLAYER);
   }
}