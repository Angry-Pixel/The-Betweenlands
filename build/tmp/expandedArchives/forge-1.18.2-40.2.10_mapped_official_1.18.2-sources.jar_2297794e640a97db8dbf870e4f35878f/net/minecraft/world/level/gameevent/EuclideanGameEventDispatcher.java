package net.minecraft.world.level.gameevent;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class EuclideanGameEventDispatcher implements GameEventDispatcher {
   private final List<GameEventListener> listeners = Lists.newArrayList();
   private final Level level;

   public EuclideanGameEventDispatcher(Level p_157753_) {
      this.level = p_157753_;
   }

   public boolean isEmpty() {
      return this.listeners.isEmpty();
   }

   public void register(GameEventListener p_157766_) {
      this.listeners.add(p_157766_);
      DebugPackets.sendGameEventListenerInfo(this.level, p_157766_);
   }

   public void unregister(GameEventListener p_157768_) {
      this.listeners.remove(p_157768_);
   }

   public void post(GameEvent p_157762_, @Nullable Entity p_157763_, BlockPos p_157764_) {
      boolean flag = false;

      for(GameEventListener gameeventlistener : this.listeners) {
         if (this.postToListener(this.level, p_157762_, p_157763_, p_157764_, gameeventlistener)) {
            flag = true;
         }
      }

      if (flag) {
         DebugPackets.sendGameEventInfo(this.level, p_157762_, p_157764_);
      }

   }

   private boolean postToListener(Level p_157756_, GameEvent p_157757_, @Nullable Entity p_157758_, BlockPos p_157759_, GameEventListener p_157760_) {
      Optional<BlockPos> optional = p_157760_.getListenerSource().getPosition(p_157756_);
      if (!optional.isPresent()) {
         return false;
      } else {
         double d0 = optional.get().distSqr(p_157759_);
         int i = p_157760_.getListenerRadius() * p_157760_.getListenerRadius();
         return d0 <= (double)i && p_157760_.handleGameEvent(p_157756_, p_157757_, p_157758_, p_157759_);
      }
   }
}