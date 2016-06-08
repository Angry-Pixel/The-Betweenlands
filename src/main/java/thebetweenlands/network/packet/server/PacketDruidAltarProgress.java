package thebetweenlands.network.packet.server;

import io.netty.buffer.ByteBuf;
import thebetweenlands.common.tile.TileEntityDruidAltar;
import thebetweenlands.network.base.Packet;

public class PacketDruidAltarProgress extends Packet {
    public int x, y, z, progress;

    public PacketDruidAltarProgress() {
    }

    public PacketDruidAltarProgress(TileEntityDruidAltar tile) {
        this.x = tile.getPos().getX();
        this.y = tile.getPos().getY();
        this.z = tile.getPos().getZ();
        this.progress = tile.craftingProgress;
    }

    public PacketDruidAltarProgress(TileEntityDruidAltar tile, int progress) {
        this.x = tile.getPos().getX();
        this.y = tile.getPos().getY();
        this.z = tile.getPos().getZ();
        this.progress = progress;
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
        this.progress = buffer.readInt();
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeInt(this.x);
        buffer.writeInt(this.y);
        buffer.writeInt(this.z);
        buffer.writeInt(this.progress);
    }
}
