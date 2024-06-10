package thebetweenlands.common.networking.packets;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.client.gui.AmateMapRenderer;
import thebetweenlands.client.gui.MapHanderer;
import thebetweenlands.client.networking.BetweenlandsClientPacketListener;
import thebetweenlands.common.handlers.SaveDataHandler;
import thebetweenlands.common.items.ItemAmateMap;
import thebetweenlands.common.savedata.AmateMapItemSavedData;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ClientboundAmateMapItemDataPacket  {
    public final int amateMapID;
    public ClientboundMapItemDataPacket remove;
    public boolean locked;
    public List<MapDecoration> icons;
    public MapItemSavedData.MapPatch mapPatch;

    public ClientboundAmateMapItemDataPacket(int amateMapID, boolean locked, @Nullable Collection<MapDecoration> icons, @Nullable MapItemSavedData.MapPatch mapPatch) {
        super();
        this.amateMapID = amateMapID;
        this.locked = locked;
        this.icons = icons != null ? Lists.newArrayList(icons) : null;;
        this.mapPatch = mapPatch;
    }

    public ClientboundAmateMapItemDataPacket(FriendlyByteBuf buff) {
        this.amateMapID = buff.readVarInt();
        this.locked = buff.readBoolean();
        this.icons = null;
        this.mapPatch = null;

        if (buff.readBoolean()) {
            this.icons = buff.readList((decoration) -> {
                MapDecoration.Type type = decoration.readEnum(MapDecoration.Type.class);
                return new MapDecoration(type, buff.readByte(), buff.readByte(), (byte)(buff.readByte() & 15), buff.readBoolean() ? buff.readComponent() : null); // TODO: setup: incorrect to remove red squiglle
            });
        }

        int i = buff.readUnsignedByte();
        if (i > 0) {
            int j = buff.readUnsignedByte();
            int k = buff.readUnsignedByte();
            int l = buff.readUnsignedByte();
            byte[] abyte = buff.readByteArray();
            this.mapPatch = new MapItemSavedData.MapPatch(k, l, i, j, abyte);
        } else {
            this.mapPatch = null;
        }
    }

    public void encode(FriendlyByteBuf buff) {
        buff.writeVarInt(this.amateMapID);
        buff.writeBoolean(this.locked);
        if (this.icons != null) {
            buff.writeBoolean(true);
            buff.writeCollection(this.icons, (p_178978_, p_178979_) -> {
                p_178978_.writeEnum(p_178979_.getType());
                p_178978_.writeByte(p_178979_.getX());
                p_178978_.writeByte(p_178979_.getY());
                p_178978_.writeByte(p_178979_.getRot() & 15);
                if (p_178979_.getName() != null) {
                    p_178978_.writeBoolean(true);
                    p_178978_.writeComponent(p_178979_.getName());
                } else {
                    p_178978_.writeBoolean(false);
                }

            });
        } else {
            buff.writeBoolean(false);
        }

        if (this.mapPatch != null) {
            buff.writeByte(this.mapPatch.width);
            buff.writeByte(this.mapPatch.height);
            buff.writeByte(this.mapPatch.startX);
            buff.writeByte(this.mapPatch.startY);
            buff.writeByteArray(this.mapPatch.mapColors);
        } else {
            buff.writeByte(0);
        }
    }

    // Handle
    public boolean handle(Supplier<NetworkEvent.Context> context) {
        final AtomicBoolean result = new AtomicBoolean(false);
        // Pass a runnable to be run on main thread
        context.get().enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;

            AmateMapRenderer maprenderer = TheBetweenlands.amateMapRenderer;
            String s = ItemAmateMap.makeKey(this.amateMapID);
            AmateMapItemSavedData mapitemsaveddata = SaveDataHandler.getAmateMapData(level, s);
            if (mapitemsaveddata == null) {
                mapitemsaveddata = AmateMapItemSavedData.createForClient(this.locked);
                SaveDataHandler.setAmateMapData(level, s, mapitemsaveddata);
            }
            this.applyToMap(mapitemsaveddata);
            maprenderer.update(this.amateMapID, mapitemsaveddata);
            result.set(true);
        });
        context.get().setPacketHandled(true);
        return result.get();
    }

    public void applyToMap(AmateMapItemSavedData p_132438_) {
        if (this.icons != null) {
            p_132438_.addClientSideDecorations(this.icons);
        }

        if (this.mapPatch != null) {
            for(int i = 0; i < this.mapPatch.width; ++i) {
                for(int j = 0; j < this.mapPatch.height; ++j) {
                    p_132438_.setColor(this.mapPatch.startX + i, this.mapPatch.startY + j, this.mapPatch.mapColors[i + j * this.mapPatch.width]);
                }
            }
        }

    }
}
