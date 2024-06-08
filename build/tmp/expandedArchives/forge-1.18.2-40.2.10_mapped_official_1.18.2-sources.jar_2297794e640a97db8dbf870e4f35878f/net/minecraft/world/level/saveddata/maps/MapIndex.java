package net.minecraft.world.level.saveddata.maps;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class MapIndex extends SavedData {
   public static final String FILE_NAME = "idcounts";
   private final Object2IntMap<String> usedAuxIds = new Object2IntOpenHashMap<>();

   public MapIndex() {
      this.usedAuxIds.defaultReturnValue(-1);
   }

   public static MapIndex load(CompoundTag p_164763_) {
      MapIndex mapindex = new MapIndex();

      for(String s : p_164763_.getAllKeys()) {
         if (p_164763_.contains(s, 99)) {
            mapindex.usedAuxIds.put(s, p_164763_.getInt(s));
         }
      }

      return mapindex;
   }

   public CompoundTag save(CompoundTag p_77884_) {
      for(Entry<String> entry : this.usedAuxIds.object2IntEntrySet()) {
         p_77884_.putInt(entry.getKey(), entry.getIntValue());
      }

      return p_77884_;
   }

   public int getFreeAuxValueForMap() {
      int i = this.usedAuxIds.getInt("map") + 1;
      this.usedAuxIds.put("map", i);
      this.setDirty();
      return i;
   }
}