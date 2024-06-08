package net.minecraft.world.level.entity;

import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.world.phys.AABB;

public class LevelEntityGetterAdapter<T extends EntityAccess> implements LevelEntityGetter<T> {
   private final EntityLookup<T> visibleEntities;
   private final EntitySectionStorage<T> sectionStorage;

   public LevelEntityGetterAdapter(EntityLookup<T> p_156943_, EntitySectionStorage<T> p_156944_) {
      this.visibleEntities = p_156943_;
      this.sectionStorage = p_156944_;
   }

   @Nullable
   public T get(int p_156947_) {
      return this.visibleEntities.getEntity(p_156947_);
   }

   @Nullable
   public T get(UUID p_156959_) {
      return this.visibleEntities.getEntity(p_156959_);
   }

   public Iterable<T> getAll() {
      return this.visibleEntities.getAllEntities();
   }

   public <U extends T> void get(EntityTypeTest<T, U> p_156953_, Consumer<U> p_156954_) {
      this.visibleEntities.getEntities(p_156953_, p_156954_);
   }

   public void get(AABB p_156956_, Consumer<T> p_156957_) {
      this.sectionStorage.getEntities(p_156956_, p_156957_);
   }

   public <U extends T> void get(EntityTypeTest<T, U> p_156949_, AABB p_156950_, Consumer<U> p_156951_) {
      this.sectionStorage.getEntities(p_156949_, p_156950_, p_156951_);
   }
}