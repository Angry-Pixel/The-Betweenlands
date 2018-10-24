package thebetweenlands.common.network.clientbound;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.network.PacketBuffer;
import net.minecraft.world.GameRules;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.registries.GameruleRegistry;

public class MessageSyncGameRules extends MessageBase {
	private Map<String, String> values = new HashMap<>();

	public MessageSyncGameRules() { }

	public MessageSyncGameRules(Collection<String> gameRules) {
		for(String gameRule : gameRules) {
			this.values.put(gameRule, GameruleRegistry.getGameRuleStringValue(gameRule));
		}
	}

	@Override
	public void serialize(PacketBuffer buf) throws IOException {
		buf.writeVarInt(this.values.size());
		for(Entry<String, String> entry : this.values.entrySet()) {
			buf.writeString(entry.getKey());
			buf.writeString(entry.getValue());
		}
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		int entries = buf.readVarInt();
		for(int i = 0; i < entries; i++) {
			this.values.put(buf.readString(256), buf.readString(256));
		}
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Side.CLIENT) {
			GameRules gameRules = GameruleRegistry.getGameRules();

			if(gameRules != null) {
				for(Entry<String, String> entry : this.values.entrySet()) {
					gameRules.setOrCreateGameRule(entry.getKey(), entry.getValue());
				}
			}
		}

		return null;
	}
}
