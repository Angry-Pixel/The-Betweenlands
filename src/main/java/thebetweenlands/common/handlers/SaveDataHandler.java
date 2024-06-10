package thebetweenlands.common.handlers;

import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapIndex;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.savedata.AmateMapIndex;
import thebetweenlands.common.savedata.AmateMapItemSavedData;

import java.util.Map;

// Handler saving and loading nbt data from world file
public class SaveDataHandler {

    public static final Map<String, AmateMapItemSavedData> amateMapData = Maps.newHashMap();

    public static AmateMapItemSavedData getAmateMapData(Level level, String p_46650_) {
        if (level.isClientSide()) {
            return amateMapData.get(p_46650_);
        }
        return level.getServer().overworld().getDataStorage().get(AmateMapItemSavedData::load, p_46650_);
    }

    public static int getFreeAmateMapId(Level level) {
        if (LogicalSide.CLIENT == EffectiveSide.get()) {
            return 0;
        }
        return level.getServer().overworld().getDataStorage().computeIfAbsent(AmateMapIndex::load, AmateMapIndex::new, "amatemapidcounts").getFreeAuxValueForAmateMap();
    }

    public static void setAmateMapData(Level level, String p_143305_, AmateMapItemSavedData p_143306_) {
        if (LogicalSide.CLIENT == EffectiveSide.get()) {
            amateMapData.put(p_143305_, p_143306_);
            return;
        }
        level.getServer().overworld().getDataStorage().set(p_143305_, p_143306_);
    }
}
