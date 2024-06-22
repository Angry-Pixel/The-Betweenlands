package thebetweenlands.client.networking;

import net.minecraft.network.protocol.game.ClientGamePacketListener;

public interface BetweenlandsClientGamePacketListener extends ClientGamePacketListener {

	void handleAmateMapItemData(ClientGamePacketListener listener);
}
