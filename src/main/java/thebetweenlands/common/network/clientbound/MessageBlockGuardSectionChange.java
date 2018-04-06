package thebetweenlands.common.network.clientbound;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.world.storage.BetweenlandsLocalStorage;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationGuarded;
import thebetweenlands.common.world.storage.location.guard.BlockLocationGuard.GuardChunkSection;

public class MessageBlockGuardSectionChange extends MessageBase {
	private String id;
	private BlockPos pos;
	private byte[] data;

	public MessageBlockGuardSectionChange() {}

	public MessageBlockGuardSectionChange(BetweenlandsLocalStorage storage, BlockPos pos, @Nullable GuardChunkSection section) {
		this.id = storage.getID().getStringID();
		this.pos = pos;
		if(section != null && section.getBlockRefCount() > 0) {
			this.data = new byte[512];
			section.writeData(this.data);
		} else {
			this.data = null;
		}
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.id = buf.readString(256);
		this.pos = BlockPos.fromLong(buf.readLong());
		if(!buf.readBoolean()) {
			this.data = buf.readByteArray(512);
			if(this.data.length != 512) {
				throw new RuntimeException("Invalid block guard data");
			}
		} else {
			this.data = null;
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeString(this.id);
		buf.writeLong(this.pos.toLong());
		buf.writeBoolean(this.data == null);
		if(this.data != null) {
			buf.writeByteArray(this.data);
		}
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
					GuardChunkSection section = location.getGuard().getSection(this.pos);
					if(this.data != null) {
						//Make sure chunk and section are loaded and not null
						location.getGuard().setGuarded(world, this.pos, true);
						section = location.getGuard().getSection(this.pos);
						if(section != null) {
							section.loadData(this.data);
						}
					} else if(section != null) {
						section.clear();
					}
				}
			}
		}
	}
}