package net.minecraft.network.syncher;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;

public class SynchedEntityData {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Object2IntMap<Class<? extends Entity>> ENTITY_ID_POOL = new Object2IntOpenHashMap<>();
   private static final int EOF_MARKER = 255;
   private static final int MAX_ID_VALUE = 254;
   private final Entity entity;
   private final Int2ObjectMap<SynchedEntityData.DataItem<?>> itemsById = new Int2ObjectOpenHashMap<>();
   private final ReadWriteLock lock = new ReentrantReadWriteLock();
   private boolean isEmpty = true;
   private boolean isDirty;

   public SynchedEntityData(Entity p_135351_) {
      this.entity = p_135351_;
   }

   public static <T> EntityDataAccessor<T> defineId(Class<? extends Entity> p_135354_, EntityDataSerializer<T> p_135355_) {
      if (true || LOGGER.isDebugEnabled()) { // Forge: This is very useful for mods that register keys on classes that are not their own
         try {
            Class<?> oclass = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
            if (!oclass.equals(p_135354_)) {
               // Forge: log at warn, mods should not add to classes that they don't own, and only add stacktrace when in debug is enabled as it is mostly not needed and consumes time
               if (LOGGER.isDebugEnabled()) LOGGER.warn("defineId called for: {} from {}", p_135354_, oclass, new RuntimeException());
               else LOGGER.warn("defineId called for: {} from {}", p_135354_, oclass);
            }
         } catch (ClassNotFoundException classnotfoundexception) {
         }
      }

      int j;
      if (ENTITY_ID_POOL.containsKey(p_135354_)) {
         j = ENTITY_ID_POOL.getInt(p_135354_) + 1;
      } else {
         int i = 0;
         Class<?> oclass1 = p_135354_;

         while(oclass1 != Entity.class) {
            oclass1 = oclass1.getSuperclass();
            if (ENTITY_ID_POOL.containsKey(oclass1)) {
               i = ENTITY_ID_POOL.getInt(oclass1) + 1;
               break;
            }
         }

         j = i;
      }

      if (j > 254) {
         throw new IllegalArgumentException("Data value id is too big with " + j + "! (Max is 254)");
      } else {
         ENTITY_ID_POOL.put(p_135354_, j);
         return p_135355_.createAccessor(j);
      }
   }

   public <T> void define(EntityDataAccessor<T> p_135373_, T p_135374_) {
      int i = p_135373_.getId();
      if (i > 254) {
         throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is 254)");
      } else if (this.itemsById.containsKey(i)) {
         throw new IllegalArgumentException("Duplicate id value for " + i + "!");
      } else if (EntityDataSerializers.getSerializedId(p_135373_.getSerializer()) < 0) {
         throw new IllegalArgumentException("Unregistered serializer " + p_135373_.getSerializer() + " for " + i + "!");
      } else {
         this.createDataItem(p_135373_, p_135374_);
      }
   }

   private <T> void createDataItem(EntityDataAccessor<T> p_135386_, T p_135387_) {
      SynchedEntityData.DataItem<T> dataitem = new SynchedEntityData.DataItem<>(p_135386_, p_135387_);
      this.lock.writeLock().lock();
      this.itemsById.put(p_135386_.getId(), dataitem);
      this.isEmpty = false;
      this.lock.writeLock().unlock();
   }

   private <T> SynchedEntityData.DataItem<T> getItem(EntityDataAccessor<T> p_135380_) {
      this.lock.readLock().lock();

      SynchedEntityData.DataItem<T> dataitem;
      try {
         dataitem = (SynchedEntityData.DataItem<T>)this.itemsById.get(p_135380_.getId());
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Getting synched entity data");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Synched entity data");
         crashreportcategory.setDetail("Data ID", p_135380_);
         throw new ReportedException(crashreport);
      } finally {
         this.lock.readLock().unlock();
      }

      return dataitem;
   }

   public <T> T get(EntityDataAccessor<T> p_135371_) {
      return this.getItem(p_135371_).getValue();
   }

   public <T> void set(EntityDataAccessor<T> p_135382_, T p_135383_) {
      SynchedEntityData.DataItem<T> dataitem = this.getItem(p_135382_);
      if (ObjectUtils.notEqual(p_135383_, dataitem.getValue())) {
         dataitem.setValue(p_135383_);
         this.entity.onSyncedDataUpdated(p_135382_);
         dataitem.setDirty(true);
         this.isDirty = true;
      }

   }

   public boolean isDirty() {
      return this.isDirty;
   }

   public static void pack(@Nullable List<SynchedEntityData.DataItem<?>> p_135359_, FriendlyByteBuf p_135360_) {
      if (p_135359_ != null) {
         for(SynchedEntityData.DataItem<?> dataitem : p_135359_) {
            writeDataItem(p_135360_, dataitem);
         }
      }

      p_135360_.writeByte(255);
   }

   @Nullable
   public List<SynchedEntityData.DataItem<?>> packDirty() {
      List<SynchedEntityData.DataItem<?>> list = null;
      if (this.isDirty) {
         this.lock.readLock().lock();

         for(SynchedEntityData.DataItem<?> dataitem : this.itemsById.values()) {
            if (dataitem.isDirty()) {
               dataitem.setDirty(false);
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(dataitem.copy());
            }
         }

         this.lock.readLock().unlock();
      }

      this.isDirty = false;
      return list;
   }

   @Nullable
   public List<SynchedEntityData.DataItem<?>> getAll() {
      List<SynchedEntityData.DataItem<?>> list = null;
      this.lock.readLock().lock();

      for(SynchedEntityData.DataItem<?> dataitem : this.itemsById.values()) {
         if (list == null) {
            list = Lists.newArrayList();
         }

         list.add(dataitem.copy());
      }

      this.lock.readLock().unlock();
      return list;
   }

   private static <T> void writeDataItem(FriendlyByteBuf p_135368_, SynchedEntityData.DataItem<T> p_135369_) {
      EntityDataAccessor<T> entitydataaccessor = p_135369_.getAccessor();
      int i = EntityDataSerializers.getSerializedId(entitydataaccessor.getSerializer());
      if (i < 0) {
         throw new EncoderException("Unknown serializer type " + entitydataaccessor.getSerializer());
      } else {
         p_135368_.writeByte(entitydataaccessor.getId());
         p_135368_.writeVarInt(i);
         entitydataaccessor.getSerializer().write(p_135368_, p_135369_.getValue());
      }
   }

   @Nullable
   public static List<SynchedEntityData.DataItem<?>> unpack(FriendlyByteBuf p_135362_) {
      List<SynchedEntityData.DataItem<?>> list = null;

      int i;
      while((i = p_135362_.readUnsignedByte()) != 255) {
         if (list == null) {
            list = Lists.newArrayList();
         }

         int j = p_135362_.readVarInt();
         EntityDataSerializer<?> entitydataserializer = EntityDataSerializers.getSerializer(j);
         if (entitydataserializer == null) {
            throw new DecoderException("Unknown serializer type " + j);
         }

         list.add(genericHelper(p_135362_, i, entitydataserializer));
      }

      return list;
   }

   private static <T> SynchedEntityData.DataItem<T> genericHelper(FriendlyByteBuf p_135364_, int p_135365_, EntityDataSerializer<T> p_135366_) {
      return new SynchedEntityData.DataItem<>(p_135366_.createAccessor(p_135365_), p_135366_.read(p_135364_));
   }

   public void assignValues(List<SynchedEntityData.DataItem<?>> p_135357_) {
      this.lock.writeLock().lock();

      try {
         for(SynchedEntityData.DataItem<?> dataitem : p_135357_) {
            SynchedEntityData.DataItem<?> dataitem1 = this.itemsById.get(dataitem.getAccessor().getId());
            if (dataitem1 != null) {
               this.assignValue(dataitem1, dataitem);
               this.entity.onSyncedDataUpdated(dataitem.getAccessor());
            }
         }
      } finally {
         this.lock.writeLock().unlock();
      }

      this.isDirty = true;
   }

   private <T> void assignValue(SynchedEntityData.DataItem<T> p_135376_, SynchedEntityData.DataItem<?> p_135377_) {
      if (!Objects.equals(p_135377_.accessor.getSerializer(), p_135376_.accessor.getSerializer())) {
         throw new IllegalStateException(String.format("Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)", p_135376_.accessor.getId(), this.entity, p_135376_.value, p_135376_.value.getClass(), p_135377_.value, p_135377_.value.getClass()));
      } else {
         p_135376_.setValue((T)p_135377_.getValue());
      }
   }

   public boolean isEmpty() {
      return this.isEmpty;
   }

   public void clearDirty() {
      this.isDirty = false;
      this.lock.readLock().lock();

      for(SynchedEntityData.DataItem<?> dataitem : this.itemsById.values()) {
         dataitem.setDirty(false);
      }

      this.lock.readLock().unlock();
   }

   public static class DataItem<T> {
      final EntityDataAccessor<T> accessor;
      T value;
      private boolean dirty;

      public DataItem(EntityDataAccessor<T> p_135394_, T p_135395_) {
         this.accessor = p_135394_;
         this.value = p_135395_;
         this.dirty = true;
      }

      public EntityDataAccessor<T> getAccessor() {
         return this.accessor;
      }

      public void setValue(T p_135398_) {
         this.value = p_135398_;
      }

      public T getValue() {
         return this.value;
      }

      public boolean isDirty() {
         return this.dirty;
      }

      public void setDirty(boolean p_135402_) {
         this.dirty = p_135402_;
      }

      public SynchedEntityData.DataItem<T> copy() {
         return new SynchedEntityData.DataItem<>(this.accessor, this.accessor.getSerializer().copy(this.value));
      }
   }
}
