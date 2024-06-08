package thebetweenlands.common.savedata;

import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapFrame;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.network.NetworkDirection;
import thebetweenlands.common.networking.BetweenlandsPacketHandler;
import thebetweenlands.common.networking.packets.ClientboundAmateMapItemDataPacket;
import thebetweenlands.common.registries.DimensionRegistries;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

// Refined vanilla map
public class AmateMapItemSavedData extends MapItemSavedData {

    public static Map<Byte, Integer> AmateMapColors = Maps.newHashMap();

    public AmateMapItemSavedData(int x, int y, boolean tracking, boolean trackingUnlimited, boolean locked) {
        super(x, y, (byte)4, tracking, trackingUnlimited, locked, DimensionRegistries.BETWEENLANDS_DIMENSION_KEY);
    }

    public static AmateMapItemSavedData createFresh(double x, double y, boolean tracking, boolean trackingUnlimited, boolean locked) {
        int scale = 128 * 5;
        int j = Mth.floor((x + 64.0D) / (double)scale);
        int k = Mth.floor((y + 64.0D) / (double)scale);
        int l = j * scale + scale / 2 - 64;
        int i1 = k * scale + scale / 2 - 64;
        return new AmateMapItemSavedData(l, i1, tracking, trackingUnlimited, locked);
    }

    public static AmateMapItemSavedData createForClient(boolean p_164778_) {
        return new AmateMapItemSavedData(0, 0, false, false, p_164778_);
    }

    public static AmateMapItemSavedData load(CompoundTag nbt) {
        int i = nbt.getInt("x");
        int j = nbt.getInt("y");
        boolean flag = !nbt.contains("trackingPosition", 1) || nbt.getBoolean("trackingPosition");
        boolean flag1 = nbt.getBoolean("unlimitedTracking");
        boolean flag2 = nbt.getBoolean("locked");
        AmateMapItemSavedData amatemapsaveddata = new AmateMapItemSavedData(i, j, flag, flag1, flag2);
        byte[] byteArray = nbt.getByteArray("colors");
        if (byteArray.length == 16384) {
            amatemapsaveddata.colors = byteArray;
        }
        return amatemapsaveddata;
    }

    // Optimised save data
    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putInt("x", this.x);
        nbt.putInt("y", this.z);
        nbt.putBoolean("trackingPosition", this.trackingPosition);
        nbt.putBoolean("unlimitedTracking", this.unlimitedTracking);
        nbt.putBoolean("locked", this.locked);
        nbt.putByteArray("colors", this.colors);
        return nbt;
    }

    public void tickCarriedBy(Player p_77919_, ItemStack p_77920_) {
        if (!this.carriedByPlayers.containsKey(p_77919_)) {
            HoldingPlayer mapitemsaveddata$holdingplayer = new HoldingPlayer(p_77919_);
            this.carriedByPlayers.put(p_77919_, mapitemsaveddata$holdingplayer);
            this.carriedBy.add(mapitemsaveddata$holdingplayer);
        }

        if (!p_77919_.getInventory().contains(p_77920_)) {
            this.removeDecoration(p_77919_.getName().getString());
        }

        for(int i = 0; i < this.carriedBy.size(); ++i) {
            MapItemSavedData.HoldingPlayer mapitemsaveddata$holdingplayer1 = this.carriedBy.get(i);
            String s = mapitemsaveddata$holdingplayer1.player.getName().getString();
            if (!mapitemsaveddata$holdingplayer1.player.isRemoved() && (mapitemsaveddata$holdingplayer1.player.getInventory().contains(p_77920_) || p_77920_.isFramed())) {
                if (!p_77920_.isFramed() && mapitemsaveddata$holdingplayer1.player.level.dimension() == this.dimension && this.trackingPosition) {
                    this.addDecoration(MapDecoration.Type.PLAYER, mapitemsaveddata$holdingplayer1.player.level, s, mapitemsaveddata$holdingplayer1.player.getX(), mapitemsaveddata$holdingplayer1.player.getZ(), (double)mapitemsaveddata$holdingplayer1.player.getYRot(), (Component)null);
                }
            } else {
                this.carriedByPlayers.remove(mapitemsaveddata$holdingplayer1.player);
                this.carriedBy.remove(mapitemsaveddata$holdingplayer1);
                this.removeDecoration(s);
            }
        }

        if (p_77920_.isFramed() && this.trackingPosition) {
            ItemFrame itemframe = p_77920_.getFrame();
            BlockPos blockpos = itemframe.getPos();
            MapFrame mapframe1 = this.frameMarkers.get(MapFrame.frameId(blockpos));
            if (mapframe1 != null && itemframe.getId() != mapframe1.getEntityId() && this.frameMarkers.containsKey(mapframe1.getId())) {
                this.removeDecoration("frame-" + mapframe1.getEntityId());
            }

            MapFrame mapframe = new MapFrame(blockpos, itemframe.getDirection().get2DDataValue() * 90, itemframe.getId());
            this.addDecoration(MapDecoration.Type.FRAME, p_77919_.level, "frame-" + itemframe.getId(), (double)blockpos.getX(), (double)blockpos.getZ(), (double)(itemframe.getDirection().get2DDataValue() * 90), (Component)null);
            this.frameMarkers.put(mapframe.getId(), mapframe);
        }

        CompoundTag compoundtag = p_77920_.getTag();
        if (compoundtag != null && compoundtag.contains("Decorations", 9)) {
            ListTag listtag = compoundtag.getList("Decorations", 10);

            for(int j = 0; j < listtag.size(); ++j) {
                CompoundTag compoundtag1 = listtag.getCompound(j);
                if (!this.decorations.containsKey(compoundtag1.getString("id"))) {
                    this.addDecoration(MapDecoration.Type.byIcon(compoundtag1.getByte("type")), p_77919_.level, compoundtag1.getString("id"), compoundtag1.getDouble("x"), compoundtag1.getDouble("z"), compoundtag1.getDouble("rot"), (Component)null);
                }
            }
        }

    }

    // Basically MapItemSavedData.HoldingPlayer nextUpdatePacket sends ClientboundAmateMapItemDataPacket
    public class HoldingPlayer extends MapItemSavedData.HoldingPlayer {

        public HoldingPlayer(Player player) {
            super(player);
        }

        @Override
        @Nullable
        public Packet<?> nextUpdatePacket(int amatemapID) {
            MapPatch patch = null;
            if (this.dirtyData) {
                int i = this.minDirtyX;
                int j = this.minDirtyY;
                int k = this.maxDirtyX + 1 - this.minDirtyX;
                int l = this.maxDirtyY + 1 - this.minDirtyY;
                byte[] abyte = new byte[k * l];

                for(int i1 = 0; i1 < k; ++i1) {
                    for(int j1 = 0; j1 < l; ++j1) {
                        abyte[i1 + j1 * k] = AmateMapItemSavedData.this.colors[i + i1 + (j + j1) * 128];
                    }
                }

                patch = new MapPatch(i, j, k, l, abyte);
            }

            Collection<MapDecoration> collection;
            if (this.dirtyDecorations && this.tick++ % 5 == 0) {
                this.dirtyDecorations = false;
                collection = AmateMapItemSavedData.this.decorations.values();
            } else {
                collection = null;
            }

            return patch == null && collection == null ? null: BetweenlandsPacketHandler.CHANNEL.toVanillaPacket(new ClientboundAmateMapItemDataPacket(amatemapID, false, collection, patch), NetworkDirection.PLAY_TO_CLIENT);
        }
    }
}
