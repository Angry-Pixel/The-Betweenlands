package thebetweenlands.common.network.clientbound;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.storage.IChunkStorage;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.world.storage.WorldStorageImpl;

public class MessageSyncChunkStorage extends MessageBase {
	private NBTTagCompound nbt;
	private ChunkPos pos;

	public MessageSyncChunkStorage() {}

	public MessageSyncChunkStorage(IChunkStorage storage) {
		this.nbt = storage.writeToNBT(new NBTTagCompound(), true);
		this.pos = storage.getChunk().getPos();
	}
	
	public MessageSyncChunkStorage(IChunkStorage storage, NBTTagCompound nbt) {
		this.nbt = nbt;
		this.pos = storage.getChunk().getPos();
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		try {
			this.pos = new ChunkPos(buf.readInt(), buf.readInt());
			this.nbt = buf.readCompoundTag();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeInt(this.pos.x);
		buf.writeInt(this.pos.z);
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
			Chunk chunk = world.getChunkFromChunkCoords(this.pos.x, this.pos.z);
			if(chunk != null) {
				IWorldStorage worldStorage = WorldStorageImpl.getCapability(world);
				IChunkStorage chunkStorage = worldStorage.getChunkStorage(chunk);
				chunkStorage.readFromNBT(this.nbt, true);
			}
		}
	}
}