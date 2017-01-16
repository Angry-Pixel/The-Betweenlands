package thebetweenlands.common.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;
import thebetweenlands.common.world.storage.world.shared.SharedStorage;

public class MessageRemoveSharedStorage extends MessageBase {
	private String id;

	public MessageRemoveSharedStorage() {}

	public MessageRemoveSharedStorage(String id) {
		this.id = id;
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		PacketBuffer packetBuffer = new PacketBuffer(buf);
		this.id = packetBuffer.readStringFromBuffer(128);
	}

	@Override
	public void serialize(PacketBuffer buf) {
		PacketBuffer packetBuffer = new PacketBuffer(buf);
		packetBuffer.writeString(this.id);
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
		SharedStorage loadedStorage = worldStorage.getSharedStorage(this.id);
		if(loadedStorage != null) {
			worldStorage.removeSharedStorage(loadedStorage);
		}
	}
}