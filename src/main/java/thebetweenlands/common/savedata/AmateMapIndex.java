package thebetweenlands.common.savedata;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.maps.MapIndex;

public class AmateMapIndex extends SavedData {
    public static final String FILE_NAME = "amatemapidcounts";

    private final Object2IntMap<String> usedIds = new Object2IntOpenHashMap<>();

    public AmateMapIndex(){
        this.usedIds.defaultReturnValue(-1);
    }

    public static AmateMapIndex load(CompoundTag p_164763_) {
        AmateMapIndex mapindex = new AmateMapIndex();

        for(String s : p_164763_.getAllKeys()) {
            if (p_164763_.contains(s, 99)) {
                mapindex.usedIds.put(s, p_164763_.getInt(s));
            }
        }

        return mapindex;
    }

    public CompoundTag save(CompoundTag p_77884_) {
        for(Object2IntMap.Entry<String> entry : this.usedIds.object2IntEntrySet()) {
            p_77884_.putInt(entry.getKey(), entry.getIntValue());
        }

        return p_77884_;
    }

    public int getFreeAuxValueForAmateMap() {
        int out = this.usedIds.getInt("amatemap") + 1;
        this.usedIds.put("amatemap", out);
        this.setDirty();
        return out;
    }
}
