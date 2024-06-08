package net.minecraft.world.level.entity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class EntityLookup<T extends EntityAccess> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Int2ObjectMap<T> byId = new Int2ObjectLinkedOpenHashMap<>();
   private final Map<UUID, T> byUuid = Maps.newHashMap();

   public <U extends T> void getEntities(EntityTypeTest<T, U> p_156817_, Consumer<U> p_156818_) {
      for(T t : this.byId.values()) {
         U u = (U)((EntityAccess)p_156817_.tryCast(t));
         if (u != null) {
            p_156818_.accept(u);
         }
      }

   }

   public Iterable<T> getAllEntities() {
      return Iterables.unmodifiableIterable(this.byId.values());
   }

   public void add(T p_156815_) {
      UUID uuid = p_156815_.getUUID();
      if (this.byUuid.containsKey(uuid)) {
         LOGGER.warn("Duplicate entity UUID {}: {}", uuid, p_156815_);
      } else {
         this.byUuid.put(uuid, p_156815_);
         this.byId.put(p_156815_.getId(), p_156815_);
      }
   }

   public void remove(T p_156823_) {
      this.byUuid.remove(p_156823_.getUUID());
      this.byId.remove(p_156823_.getId());
   }

   @Nullable
   public T getEntity(int p_156813_) {
      return this.byId.get(p_156813_);
   }

   @Nullable
   public T getEntity(UUID p_156820_) {
      return this.byUuid.get(p_156820_);
   }

   public int count() {
      return this.byUuid.size();
   }
}