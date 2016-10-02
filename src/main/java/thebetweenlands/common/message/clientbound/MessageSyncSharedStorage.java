package thebetweenlands.common.message.clientbound;

import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.common.message.BLMessage;
import thebetweenlands.common.world.storage.world.shared.SharedStorage;

public class MessageSyncSharedStorage extends BLMessage {
	private NBTTagCompound nbt;

	public MessageSyncSharedStorage() {}

	public MessageSyncSharedStorage(SharedStorage sharedStorage) {
		this.nbt = new NBTTagCompound();
		sharedStorage.writeToPacketNBT(this.nbt);
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		PacketBuffer packetBuffer = new PacketBuffer(buf);
		try {
			this.nbt = packetBuffer.readNBTTagCompoundFromBuffer();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		PacketBuffer packetBuffer = new PacketBuffer(buf);
		packetBuffer.writeNBTTagCompoundToBuffer(this.nbt);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
		}
		return null;
	}
}