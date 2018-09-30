package thebetweenlands.common.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationGuarded;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class MessageClearBlockGuard extends MessageBase {
	private String id;

	public MessageClearBlockGuard() {}

	public MessageClearBlockGuard(LocationStorage location) {
		this.id = location.getID().getStringID();
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.id = buf.readString(256);
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeString(this.id);
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
			BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);
			ILocalStorage storage = worldStorage.getLocalStorageHandler().getLocalStorage(StorageID.fromString(this.id));
			if(storage != null && storage instanceof LocationGuarded) {
				LocationGuarded location = (LocationGuarded) storage;
				if(location.getGuard() != null) {
					location.getGuard().clear(world);
				}
			}
		}
	}
}