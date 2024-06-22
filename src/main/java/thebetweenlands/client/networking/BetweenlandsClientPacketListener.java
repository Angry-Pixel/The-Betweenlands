package thebetweenlands.client.networking;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.ClientTelemetryManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class BetweenlandsClientPacketListener extends ClientPacketListener implements BetweenlandsClientGamePacketListener {

	public BetweenlandsClientPacketListener(Minecraft p_194193_, Screen p_194194_, Connection p_194195_, GameProfile p_194196_, ClientTelemetryManager p_194197_) {
		super(p_194193_, p_194194_, p_194195_, p_194196_, p_194197_);
	}

	@Override
	public void handleAmateMapItemData(ClientGamePacketListener listener) {

	}
}
