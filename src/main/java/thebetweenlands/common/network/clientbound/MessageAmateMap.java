package thebetweenlands.common.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.common.item.misc.ItemAmateMap;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.world.storage.AmateMapData;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MessageAmateMap extends MessageBase {
    private int mapId;
    private byte[] locationData;
    private SPacketMaps inner;

    public MessageAmateMap() {}

    public MessageAmateMap(int mapId, AmateMapData mapData, SPacketMaps inner) {
        this.mapId = mapId;
        this.locationData = mapData.serializeLocations();
        this.inner = inner;
    }

    @Override
    public void serialize(PacketBuffer buf) {
        ByteBufUtils.writeVarInt(buf, mapId, 5);
        buf.writeByteArray(locationData);

        try {
            inner.writePacketData(buf);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't write inner SPacketMaps", e);
        }
    }

    @Override
    public void deserialize(PacketBuffer buf) {
        mapId = ByteBufUtils.readVarInt(buf, 5);
        locationData = buf.readByteArray();

        inner = new SPacketMaps();
        try {
            inner.readPacketData(buf);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read inner SPacketMaps", e);
        }
    }

    @Override
    public IMessage process(MessageContext ctx) {
        MapItemRenderer mapItemRenderer = Minecraft.getMinecraft().entityRenderer.getMapItemRenderer();
        AmateMapData mapData = ItemAmateMap.loadMapData(mapId, Minecraft.getMinecraft().world);

        if (mapData == null) {
            String s = ItemAmateMap.STR_ID + "_" + mapId;
            mapData = new AmateMapData(s);

            if (mapItemRenderer.getMapInstanceIfExists(s) != null) {
                MapData mapdata1 = mapItemRenderer.getData(mapItemRenderer.getMapInstanceIfExists(s));

                if (mapdata1 instanceof AmateMapData) {
                    mapData = (AmateMapData) mapdata1;
                }
            }

            Minecraft.getMinecraft().world.setData(s, mapData);
        }

        inner.setMapdataTo(mapData);
        mapData.deserializeLocations(locationData);

        Map<String, MapDecoration> vanilla = mapData.mapDecorations;
        mapData.mapDecorations = new LinkedHashMap<>();

        for (AmateMapData.BLMapDecoration decor : mapData.blDecorations) {
            mapData.mapDecorations.put(decor.toString(), decor);
        }

        mapData.mapDecorations.putAll(vanilla);
        mapData.updateMapTexture();

        return null;
    }
}
