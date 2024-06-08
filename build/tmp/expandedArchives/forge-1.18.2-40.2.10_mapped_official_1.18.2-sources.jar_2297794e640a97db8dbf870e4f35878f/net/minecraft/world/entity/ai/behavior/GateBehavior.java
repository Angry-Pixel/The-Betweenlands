package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class GateBehavior<E extends LivingEntity> extends Behavior<E> {
   private final Set<MemoryModuleType<?>> exitErasedMemories;
   private final GateBehavior.OrderPolicy orderPolicy;
   private final GateBehavior.RunningPolicy runningPolicy;
   private final ShufflingList<Behavior<? super E>> behaviors = new ShufflingList<>();

   public GateBehavior(Map<MemoryModuleType<?>, MemoryStatus> p_22873_, Set<MemoryModuleType<?>> p_22874_, GateBehavior.OrderPolicy p_22875_, GateBehavior.RunningPolicy p_22876_, List<Pair<Behavior<? super E>, Integer>> p_22877_) {
      super(p_22873_);
      this.exitErasedMemories = p_22874_;
      this.orderPolicy = p_22875_;
      this.runningPolicy = p_22876_;
      p_22877_.forEach((p_22892_) -> {
         this.behaviors.add(p_22892_.getFirst(), p_22892_.getSecond());
      });
   }

   protected boolean canStillUse(ServerLevel p_22894_, E p_22895_, long p_22896_) {
      return this.behaviors.stream().filter((p_22920_) -> {
         return p_22920_.getStatus() == Behavior.Status.RUNNING;
      }).anyMatch((p_22912_) -> {
         return p_22912_.canStillUse(p_22894_, p_22895_, p_22896_);
      });
   }

   protected boolean timedOut(long p_22879_) {
      return false;
   }

   protected void start(ServerLevel p_22881_, E p_22882_, long p_22883_) {
      this.orderPolicy.apply(this.behaviors);
      this.runningPolicy.apply(this.behaviors.stream(), p_22881_, p_22882_, p_22883_);
   }

   protected void tick(ServerLevel p_22916_, E p_22917_, long p_22918_) {
      this.behaviors.stream().filter((p_22914_) -> {
         return p_22914_.getStatus() == Behavior.Status.RUNNING;
      }).forEach((p_22901_) -> {
         p_22901_.tickOrStop(p_22916_, p_22917_, p_22918_);
      });
   }

   protected void stop(ServerLevel p_22905_, E p_22906_, long p_22907_) {
      this.behaviors.stream().filter((p_22903_) -> {
         return p_22903_.getStatus() == Behavior.Status.RUNNING;
      }).forEach((p_22888_) -> {
         p_22888_.doStop(p_22905_, p_22906_, p_22907_);
      });
      this.exitErasedMemories.forEach(p_22906_.getBrain()::eraseMemory);
   }

   public String toString() {
      Set<? extends Behavior<? super E>> set = this.behaviors.stream().filter((p_22890_) -> {
         return p_22890_.getStatus() == Behavior.Status.RUNNING;
      }).collect(Collectors.toSet());
      return "(" + this.getClass().getSimpleName() + "): " + set;
   }

   public static enum OrderPolicy {
      ORDERED((p_147530_) -> {
      }),
      SHUFFLED(ShufflingList::shuffle);

      private final Consumer<ShufflingList<?>> consumer;

      private OrderPolicy(Consumer<ShufflingList<?>> p_22930_) {
         this.consumer = p_22930_;
      }

      public void apply(ShufflingList<?> p_147528_) {
         this.consumer.accept(p_147528_);
      }
   }

   public static enum RunningPolicy {
      RUN_ONE {
         public <E extends LivingEntity> void apply(Stream<Behavior<? super E>> p_147537_, ServerLevel p_147538_, E p_147539_, long p_147540_) {
            p_147537_.filter((p_22965_) -> {
               return p_22965_.getStatus() == Behavior.Status.STOPPED;
            }).filter((p_22963_) -> {
               return p_22963_.tryStart(p_147538_, p_147539_, p_147540_);
            }).findFirst();
         }
      },
      TRY_ALL {
         public <E extends LivingEntity> void apply(Stream<Behavior<? super E>> p_147542_, ServerLevel p_147543_, E p_147544_, long p_147545_) {
            p_147542_.filter((p_22980_) -> {
               return p_22980_.getStatus() == Behavior.Status.STOPPED;
            }).forEach((p_22978_) -> {
               p_22978_.tryStart(p_147543_, p_147544_, p_147545_);
            });
         }
      };

      public abstract <E extends LivingEntity> void apply(Stream<Behavior<? super E>> p_147532_, ServerLevel p_147533_, E p_147534_, long p_147535_);
   }
}