package net.minecraft.world.level.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

public class EntityTickList {
   private Int2ObjectMap<Entity> active = new Int2ObjectLinkedOpenHashMap<>();
   private Int2ObjectMap<Entity> passive = new Int2ObjectLinkedOpenHashMap<>();
   @Nullable
   private Int2ObjectMap<Entity> iterated;

   private void ensureActiveIsNotIterated() {
      if (this.iterated == this.active) {
         this.passive.clear();

         for(Entry<Entity> entry : Int2ObjectMaps.fastIterable(this.active)) {
            this.passive.put(entry.getIntKey(), entry.getValue());
         }

         Int2ObjectMap<Entity> int2objectmap = this.active;
         this.active = this.passive;
         this.passive = int2objectmap;
      }

   }

   public void add(Entity p_156909_) {
      this.ensureActiveIsNotIterated();
      this.active.put(p_156909_.getId(), p_156909_);
   }

   public void remove(Entity p_156913_) {
      this.ensureActiveIsNotIterated();
      this.active.remove(p_156913_.getId());
   }

   public boolean contains(Entity p_156915_) {
      return this.active.containsKey(p_156915_.getId());
   }

   public void forEach(Consumer<Entity> p_156911_) {
      if (this.iterated != null) {
         throw new UnsupportedOperationException("Only one concurrent iteration supported");
      } else {
         this.iterated = this.active;

         try {
            for(Entity entity : this.active.values()) {
               p_156911_.accept(entity);
            }
         } finally {
            this.iterated = null;
         }

      }
   }
}