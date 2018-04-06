package thebetweenlands.common.network.clientbound;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.ILocalStorageHandler;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.world.storage.WorldStorageImpl;

public class MessageSyncLocalStorage extends MessageBase {
	private NBTTagCompound nbt;

	public MessageSyncLocalStorage() {}

	public MessageSyncLocalStorage(NBTTagCompound nbt) {
		this.nbt = nbt;
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		try {
			this.nbt = buf.readCompoundTag();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeCompoundTag(this.nbt);
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
		World world = Minecraft.getMinecraft().world;
		if(world != null) {
			IWorldStorage worldStorage = WorldStorageImpl.getCapability(world);
			ILocalStorageHandler storageHandler = worldStorage.getLocalStorageHandler();
			ILocalStorage localStorage = storageHandler.createLocalStorageFromNBT(this.nbt, null, true);
			ILocalStorage loadedStorage = storageHandler.getLocalStorage(localStorage.getID());
			
			if(loadedStorage != null && localStorage.getLinkedChunks().isEmpty()) {
				//Remove storage if all chunks have been unlinked
				storageHandler.removeLocalStorage(loadedStorage);
			} else {
				if(loadedStorage != null && localStorage.getClass() == loadedStorage.getClass()) {
					//Storage already loaded, update NBT only
					loadedStorage.readFromPacketNBT(storageHandler.getLocalStorageDataNBT(this.nbt));
				} else {
					//Remove storage if the class is not matching
					if(loadedStorage != null) {
						storageHandler.removeLocalStorage(loadedStorage);
					}
					storageHandler.addLocalStorage(localStorage);
				}
			}
		}
	}
}