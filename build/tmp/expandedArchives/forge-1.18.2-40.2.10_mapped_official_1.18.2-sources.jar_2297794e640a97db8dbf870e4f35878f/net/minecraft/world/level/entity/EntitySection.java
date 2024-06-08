package net.minecraft.world.level.entity;

import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public class EntitySection<T extends EntityAccess> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ClassInstanceMultiMap<T> storage;
   private Visibility chunkStatus;

   public EntitySection(Class<T> p_156831_, Visibility p_156832_) {
      this.chunkStatus = p_156832_;
      this.storage = new ClassInstanceMultiMap<>(p_156831_);
   }

   public void add(T p_188347_) {
      this.storage.add(p_188347_);
   }

   public boolean remove(T p_188356_) {
      return this.storage.remove(p_188356_);
   }

   public void getEntities(AABB p_188353_, Consumer<T> p_188354_) {
      for(T t : this.storage) {
         if (t.getBoundingBox().intersects(p_188353_)) {
            p_188354_.accept(t);
         }
      }

   }

   public <U extends T> void getEntities(EntityTypeTest<T, U> p_188349_, AABB p_188350_, Consumer<? super U> p_188351_) {
      Collection<? extends T> collection = this.storage.find(p_188349_.getBaseClass());
      if (!collection.isEmpty()) {
         for(T t : collection) {
            U u = (U)((EntityAccess)p_188349_.tryCast(t));
            if (u != null && t.getBoundingBox().intersects(p_188350_)) {
               p_188351_.accept(u);
            }
         }

      }
   }

   public boolean isEmpty() {
      return this.storage.isEmpty();
   }

   public Stream<T> getEntities() {
      return this.storage.stream();
   }

   public Visibility getStatus() {
      return this.chunkStatus;
   }

   public Visibility updateChunkStatus(Visibility p_156839_) {
      Visibility visibility = this.chunkStatus;
      this.chunkStatus = p_156839_;
      return visibility;
   }

   @VisibleForDebug
   public int size() {
      return this.storage.size();
   }
}