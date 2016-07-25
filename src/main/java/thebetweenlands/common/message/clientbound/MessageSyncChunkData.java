package thebetweenlands.common.message.clientbound;

import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.common.message.BLMessage;
import thebetweenlands.common.world.storage.chunk.BetweenlandsChunkData;
import thebetweenlands.common.world.storage.chunk.ChunkDataBase;

public class MessageSyncChunkData extends BLMessage {
    private int chunkX, chunkZ;
    private String name;
    private NBTTagCompound nbt;

    public MessageSyncChunkData() {}

    public MessageSyncChunkData(BetweenlandsChunkData data) {
        this.chunkX = data.getChunk().xPosition;
        this.chunkZ = data.getChunk().zPosition;
        this.name = data.name;
        NBTTagCompound nbtData = data.readData();
        this.nbt = nbtData != null ? nbtData : new NBTTagCompound();
    }

    @Override
    public void deserialize(PacketBuffer buf) {
        this.chunkX = buf.readInt();
        this.chunkZ = buf.readInt();
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        try {
            this.name = packetBuffer.readStringFromBuffer(128);
            this.nbt = packetBuffer.readNBTTagCompoundFromBuffer();
        } catch (IOException e) {
        }
    }

    @Override
    public void serialize(PacketBuffer buf) {
        buf.writeInt(this.chunkX);
        buf.writeInt(this.chunkZ);
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.writeString(this.name);
        packetBuffer.writeNBTTagCompoundToBuffer(this.nbt);
    }

    @Override
    public IMessage process(MessageContext ctx) {
        synchronized (BetweenlandsChunkData.CHUNK_DATA_HANDLER) {
            World world = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld();
            ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
            NBTTagCompound currentNBT = ChunkDataBase.getNBTCache(chunkPos, world);
            if (currentNBT == null)
                currentNBT = new NBTTagCompound();
            currentNBT.setTag(name, nbt);
            ChunkDataBase.addNBTCache(chunkPos, world, currentNBT);
            ChunkDataBase data = ChunkDataBase.getDataCache(chunkPos, world, BetweenlandsChunkData.class);
            if (data != null) {
                data.writeData(nbt);
                data.load();
            }
        }
        return null;
    }
}