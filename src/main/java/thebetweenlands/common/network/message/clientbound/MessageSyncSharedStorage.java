package thebetweenlands.common.network.message.clientbound;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.network.message.BLMessage;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;
import thebetweenlands.common.world.storage.world.shared.SharedStorage;

public class MessageSyncSharedStorage extends BLMessage {
	private NBTTagCompound nbt;

	public MessageSyncSharedStorage() {}

	public MessageSyncSharedStorage(NBTTagCompound nbt) {
		this.nbt = nbt;
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		try {
			this.nbt = buf.readNBTTagCompoundFromBuffer();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeNBTTagCompoundToBuffer(this.nbt);
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
		SharedStorage sharedStorage = SharedStorage.load(worldStorage, this.nbt, null, true);
		SharedStorage loadedStorage = worldStorage.getSharedStorage(sharedStorage.getID());
		if(loadedStorage != null && sharedStorage.getLinkedChunks().isEmpty()) {
			//Remove storage if all chunks have been unlinked
			worldStorage.removeSharedStorage(loadedStorage);
		} else {
			if(loadedStorage != null && sharedStorage.getClass() == loadedStorage.getClass()) {
				//Storage already loaded, update NBT only
				loadedStorage.readFromPacketNBT(sharedStorage.writeToPacketNBT(new NBTTagCompound()));
			} else {
				//Remove storage if the class is not matching
				if(loadedStorage != null) {
					worldStorage.removeSharedStorage(loadedStorage);
				}
				worldStorage.addSharedStorage(sharedStorage);
			}
		}
	}
}