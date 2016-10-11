package thebetweenlands.common.message.clientbound;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.message.BLMessage;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;
import thebetweenlands.common.world.storage.world.shared.SharedStorage;

public class MessageRemoveSharedStorage extends BLMessage {
	private UUID uuid;

	public MessageRemoveSharedStorage() {}

	public MessageRemoveSharedStorage(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		PacketBuffer packetBuffer = new PacketBuffer(buf);
		this.uuid = packetBuffer.readUuid();
	}

	@Override
	public void serialize(PacketBuffer buf) {
		PacketBuffer packetBuffer = new PacketBuffer(buf);
		packetBuffer.writeUuid(this.uuid);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			this.handle();
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handle() {
		World world = Minecraft.getMinecraft().theWorld;
		BetweenlandsWorldData worldStorage = BetweenlandsWorldData.forWorld(world);
		SharedStorage loadedStorage = worldStorage.getSharedStorage(this.uuid.toString());
		if(loadedStorage != null) {
			worldStorage.removeSharedStorage(loadedStorage);
		}
	}
}