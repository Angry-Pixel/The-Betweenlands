package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class AdvancementList {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<ResourceLocation, Advancement> advancements = Maps.newHashMap();
   private final Set<Advancement> roots = Sets.newLinkedHashSet();
   private final Set<Advancement> tasks = Sets.newLinkedHashSet();
   @Nullable
   private AdvancementList.Listener listener;

   private void remove(Advancement p_139340_) {
      for(Advancement advancement : p_139340_.getChildren()) {
         this.remove(advancement);
      }

      LOGGER.info("Forgot about advancement {}", (Object)p_139340_.getId());
      this.advancements.remove(p_139340_.getId());
      if (p_139340_.getParent() == null) {
         this.roots.remove(p_139340_);
         if (this.listener != null) {
            this.listener.onRemoveAdvancementRoot(p_139340_);
         }
      } else {
         this.tasks.remove(p_139340_);
         if (this.listener != null) {
            this.listener.onRemoveAdvancementTask(p_139340_);
         }
      }

   }

   public void remove(Set<ResourceLocation> p_139336_) {
      for(ResourceLocation resourcelocation : p_139336_) {
         Advancement advancement = this.advancements.get(resourcelocation);
         if (advancement == null) {
            LOGGER.warn("Told to remove advancement {} but I don't know what that is", (Object)resourcelocation);
         } else {
            this.remove(advancement);
         }
      }

   }

   public void add(Map<ResourceLocation, Advancement.Builder> p_139334_) {
      Map<ResourceLocation, Advancement.Builder> map = Maps.newHashMap(p_139334_);

      while(!map.isEmpty()) {
         boolean flag = false;
         Iterator<Entry<ResourceLocation, Advancement.Builder>> iterator = map.entrySet().iterator();

         while(iterator.hasNext()) {
            Entry<ResourceLocation, Advancement.Builder> entry = iterator.next();
            ResourceLocation resourcelocation = entry.getKey();
            Advancement.Builder advancement$builder = entry.getValue();
            if (advancement$builder.canBuild(this.advancements::get)) {
               Advancement advancement = advancement$builder.build(resourcelocation);
               this.advancements.put(resourcelocation, advancement);
               flag = true;
               iterator.remove();
               if (advancement.getParent() == null) {
                  this.roots.add(advancement);
                  if (this.listener != null) {
                     this.listener.onAddAdvancementRoot(advancement);
                  }
               } else {
                  this.tasks.add(advancement);
                  if (this.listener != null) {
                     this.listener.onAddAdvancementTask(advancement);
                  }
               }
            }
         }

         if (!flag) {
            for(Entry<ResourceLocation, Advancement.Builder> entry1 : map.entrySet()) {
               LOGGER.error("Couldn't load advancement {}: {}", entry1.getKey(), entry1.getValue());
            }
            break;
         }
      }

      net.minecraftforge.common.AdvancementLoadFix.buildSortedTrees(this.roots);
      LOGGER.info("Loaded {} advancements", (int)this.advancements.size());
   }

   public void clear() {
      this.advancements.clear();
      this.roots.clear();
      this.tasks.clear();
      if (this.listener != null) {
         this.listener.onAdvancementsCleared();
      }

   }

   public Iterable<Advancement> getRoots() {
      return this.roots;
   }

   public Collection<Advancement> getAllAdvancements() {
      return this.advancements.values();
   }

   @Nullable
   public Advancement get(ResourceLocation p_139338_) {
      return this.advancements.get(p_139338_);
   }

   public void setListener(@Nullable AdvancementList.Listener p_139342_) {
      this.listener = p_139342_;
      if (p_139342_ != null) {
         for(Advancement advancement : this.roots) {
            p_139342_.onAddAdvancementRoot(advancement);
         }

         for(Advancement advancement1 : this.tasks) {
            p_139342_.onAddAdvancementTask(advancement1);
         }
      }

   }

   public interface Listener {
      void onAddAdvancementRoot(Advancement p_139345_);

      void onRemoveAdvancementRoot(Advancement p_139346_);

      void onAddAdvancementTask(Advancement p_139347_);

      void onRemoveAdvancementTask(Advancement p_139348_);

      void onAdvancementsCleared();
   }
}
