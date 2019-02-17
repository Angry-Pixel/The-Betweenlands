package thebetweenlands.common.network.clientbound;

import java.io.IOException;

import javax.xml.ws.handler.MessageContext;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationGuarded;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class MessageBlockGuardData extends MessageBase {
	private String id;
	private NBTTagCompound data;

	public MessageBlockGuardData() {}

	public MessageBlockGuardData(LocationStorage location) {
		this.id = location.getID().getStringID();
		this.data = location.writeGuardNBT(new NBTTagCompound());
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.id = buf.readString(256);
		try {
			this.data = buf.readCompoundTag();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeString(this.id);
		buf.writeCompoundTag(this.data);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Dist.CLIENT) {
			this.handle();
		}
		return null;
	}

	@OnlyIn(Dist.CLIENT)
	private void handle() {
		World world = Minecraft.getInstance().world;
		if(world != null) {
			BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
			ILocalStorage storage = worldStorage.getLocalStorageHandler().getLocalStorage(StorageID.fromString(this.id));
			if(storage != null && storage instanceof LocationGuarded) {
				LocationGuarded location = (LocationGuarded) storage;
				location.readGuardNBT(this.data);
			}
		}
	}
}