package net.minecraft.world.level.gameevent;

import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;

public class GameEventListenerRegistrar {
   private final GameEventListener listener;
   @Nullable
   private SectionPos sectionPos;

   public GameEventListenerRegistrar(GameEventListener p_157853_) {
      this.listener = p_157853_;
   }

   public void onListenerRemoved(Level p_157855_) {
      this.ifEventDispatcherExists(p_157855_, this.sectionPos, (p_157867_) -> {
         p_157867_.unregister(this.listener);
      });
   }

   public void onListenerMove(Level p_157863_) {
      Optional<BlockPos> optional = this.listener.getListenerSource().getPosition(p_157863_);
      if (optional.isPresent()) {
         long i = SectionPos.blockToSection(optional.get().asLong());
         if (this.sectionPos == null || this.sectionPos.asLong() != i) {
            SectionPos sectionpos = this.sectionPos;
            this.sectionPos = SectionPos.of(i);
            this.ifEventDispatcherExists(p_157863_, sectionpos, (p_157865_) -> {
               p_157865_.unregister(this.listener);
            });
            this.ifEventDispatcherExists(p_157863_, this.sectionPos, (p_157861_) -> {
               p_157861_.register(this.listener);
            });
         }
      }

   }

   private void ifEventDispatcherExists(Level p_157857_, @Nullable SectionPos p_157858_, Consumer<GameEventDispatcher> p_157859_) {
      if (p_157858_ != null) {
         ChunkAccess chunkaccess = p_157857_.getChunk(p_157858_.x(), p_157858_.z(), ChunkStatus.FULL, false);
         if (chunkaccess != null) {
            p_157859_.accept(chunkaccess.getEventDispatcher(p_157858_.y()));
         }

      }
   }
}