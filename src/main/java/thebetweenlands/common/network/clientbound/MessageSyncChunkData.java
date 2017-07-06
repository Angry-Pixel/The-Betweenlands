package thebetweenlands.common.network.clientbound;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.network.MessageBase;
import thebetweenlands.common.world.storage.chunk.ChunkDataBase;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;

public class MessageSyncChunkData extends MessageBase {
	private int chunkX, chunkZ;
	private NBTTagCompound nbt;

	public MessageSyncChunkData() {}

	public MessageSyncChunkData(Chunk chunk, NBTTagCompound nbt) {
		this.chunkX = chunk.x;
		this.chunkZ = chunk.z;
		this.nbt = nbt;
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.chunkX = buf.readInt();
		this.chunkZ = buf.readInt();
		PacketBuffer packetBuffer = new PacketBuffer(buf);
		try {
			this.nbt = packetBuffer.readCompoundTag();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeInt(this.chunkX);
		buf.writeInt(this.chunkZ);
		PacketBuffer packetBuffer = new PacketBuffer(buf);
		packetBuffer.writeCompoundTag(this.nbt);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Side.CLIENT) {
			this.updateChunks(this.chunkX, this.chunkZ, this.nbt);
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void updateChunks(int chunkX, int chunkZ, NBTTagCompound nbt) {
		Chunk chunk = Minecraft.getMinecraft().world.getChunkProvider().getLoadedChunk(chunkX, chunkZ);
		if(chunk != null) {
			ChunkDataBase.updateHandlerData(BetweenlandsWorldData.forWorld(Minecraft.getMinecraft().world), chunk, nbt);
		}
	}
}