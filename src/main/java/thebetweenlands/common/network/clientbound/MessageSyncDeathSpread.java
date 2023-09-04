package thebetweenlands.common.network.clientbound;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.network.MessageBase;

public class MessageSyncDeathSpread extends MessageBase {
	public static class SpreadEntry {
		private int blockX;
		private int blockZ;
		private byte biomeId;
		
		public SpreadEntry(int blockX, int blockZ, int biomeId) {
			this.blockX = blockX;
			this.blockZ = blockZ;
			this.biomeId = (byte)biomeId;
		}
	}
	
	private List<SpreadEntry> entries;
	
	public MessageSyncDeathSpread() { }

	public MessageSyncDeathSpread(List<SpreadEntry> entries) {
		this.entries = entries;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		final int size = this.entries.size();
		buf.writeVarInt(size);
		for(SpreadEntry entry : this.entries) {
			buf.writeInt(entry.blockX);
			buf.writeInt(entry.blockZ);
			buf.writeByte(entry.biomeId);
		}
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		final int size = buf.readVarInt();
		this.entries = new ArrayList<SpreadEntry>(size);
		for(int i = 0; i < size; ++i) {
			final int blockX = buf.readInt();
			final int blockZ = buf.readInt();
			final byte biomeId = buf.readByte();
			this.entries.add(new SpreadEntry(blockX, blockZ, biomeId));
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
		Minecraft mc = Minecraft.getMinecraft();
		World world = mc.world;
		if(world != null) {
			MutableBlockPos pos = new MutableBlockPos();
			for(SpreadEntry entry : this.entries) {
				pos.setPos(entry.blockX, 0, entry.blockZ);
				Chunk chunk = world.getChunk(pos);
				final byte[] biomes = chunk.getBiomeArray();
				final int x = entry.blockX & 15;
				final int z = entry.blockZ & 15;
				biomes[z << 4 | x] = entry.biomeId;
			}
		}
	}
}
