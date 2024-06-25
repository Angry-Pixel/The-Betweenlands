package thebetweenlands.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.items.AmateMapItem;
import thebetweenlands.common.world.storage.AmateMapData;

public class AmateMapPacket implements CustomPacketPayload {

	public static final Type<AmateMapPacket> TYPE = new Type<>(TheBetweenlands.prefix("amate_map"));

	public static final StreamCodec<RegistryFriendlyByteBuf, AmateMapPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.byteArray(256),
		p -> p.featureData,
		ClientboundMapItemDataPacket.STREAM_CODEC, p -> p.inner,
		AmateMapPacket::new
	);

	private final byte[] featureData;
	private final ClientboundMapItemDataPacket inner;

	public AmateMapPacket(AmateMapData mapData, ClientboundMapItemDataPacket inner) {
		this(mapData.serializeLocations(), inner);
	}

	public AmateMapPacket(byte[] mapData, ClientboundMapItemDataPacket inner) {
		this.featureData = mapData;
		this.inner = inner;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@SuppressWarnings("Convert2Lambda")
	public static void handle(AmateMapPacket message, IPayloadContext ctx) {
		//ensure this is only done on clients as this uses client only code
		if (ctx.flow().isClientbound()) {
			ctx.enqueueWork(new Runnable() {
				@Override
				public void run() {
					Level level = ctx.player().level();
					// [VanillaCopy] ClientPlayNetHandler#handleMaps with our own mapdatas
					MapRenderer mapitemrenderer = Minecraft.getInstance().gameRenderer.getMapRenderer();
					String s = AmateMapItem.getMapName(message.inner.mapId().id());
					AmateMapData mapdata = AmateMapData.getMapData(level, s);
					if (mapdata == null) {
						mapdata = new AmateMapData(0, 0, false, false, message.inner.locked());
						AmateMapData.registerMapData(level, mapdata, s);
					}

					message.inner.applyToMap(mapdata);
					mapdata.deserializeLocations(message.featureData);
					mapitemrenderer.update(message.inner.mapId(), mapdata);
				}
			});
		}
	}
}
