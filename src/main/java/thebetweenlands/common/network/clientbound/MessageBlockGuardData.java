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
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;
import thebetweenlands.common.world.storage.world.shared.SharedStorage;
import thebetweenlands.common.world.storage.world.shared.location.LocationGuarded;
import thebetweenlands.common.world.storage.world.shared.location.LocationStorage;

public class MessageBlockGuardData extends MessageBase {
	private String id;
	private NBTTagCompound data;

	public MessageBlockGuardData() {}

	public MessageBlockGuardData(LocationStorage location) {
		this.id = location.getID();
		this.data = location.writeGuardNBT(new NBTTagCompound());
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.id = buf.readStringFromBuffer(256);
		try {
			this.data = buf.readNBTTagCompoundFromBuffer();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeString(this.id);
		buf.writeNBTTagCompoundToBuffer(this.data);
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
		World world = Minecraft.getMinecraft().theWorld;
		BetweenlandsWorldData worldStorage = BetweenlandsWorldData.forWorld(world);
		SharedStorage storage = worldStorage.getSharedStorage(this.id);
		if(storage != null && storage instanceof LocationGuarded) {
			LocationGuarded location = (LocationGuarded) storage;
			location.readGuardNBT(this.data);
		}
	}
}