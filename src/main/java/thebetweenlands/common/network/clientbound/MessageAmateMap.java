package thebetweenlands.common.network.clientbound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.item.misc.ItemAmateMap;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.world.storage.AmateMapData;

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
        if(ctx.side == Side.CLIENT) {
        	this.handle();
        }

        return null;
    }
    
    @SideOnly(Side.CLIENT)
    private void handle() {
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

        List<AmateMapData.BLMapDecoration> decorations = new ArrayList<>(mapData.decorations.values());
        decorations.addAll(mapData.decorations.values());
        Collections.sort(decorations);
        
        for (AmateMapData.BLMapDecoration decor : decorations) {
            mapData.mapDecorations.put(decor.toString(), decor);
        }
        
        mapData.mapDecorations.putAll(vanilla);
        mapData.updateMapTexture();
    }
}
