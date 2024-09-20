package thebetweenlands.common.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.misc.AmateMapItem;
import thebetweenlands.common.world.storage.AmateMapData;

public record AmateMapPacket(ClientboundMapItemDataPacket inner) implements CustomPacketPayload {

	public static final Type<AmateMapPacket> TYPE = new Type<>(TheBetweenlands.prefix("amate_map"));

	public static final StreamCodec<RegistryFriendlyByteBuf, AmateMapPacket> STREAM_CODEC = StreamCodec.composite(ClientboundMapItemDataPacket.STREAM_CODEC, p -> p.inner, AmateMapPacket::new);

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
					// [VanillaCopy] ClientPacketListener#handleMapItemData with our own mapdatas
					MapRenderer mapitemrenderer = Minecraft.getInstance().gameRenderer.getMapRenderer();
					String s = AmateMapItem.getMapName(message.inner.mapId().id());
					AmateMapData mapdata = AmateMapData.getMapData(level, s);
					if (mapdata == null) {
						mapdata = new AmateMapData(0, 0, false, false, message.inner.locked());
						AmateMapData.registerMapData(level, mapdata, s);
					}

					message.inner.applyToMap(mapdata);
					mapitemrenderer.update(message.inner.mapId(), mapdata);
				}
			});
		}
	}
}
