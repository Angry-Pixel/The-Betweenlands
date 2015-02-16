package thebetweenlands.network.packet;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class AltarCraftingProgressMessage
        implements IMessage
{
    public int posX;
    public int posY;
    public int posZ;
    public int craftingProgress;

    public AltarCraftingProgressMessage() {
    }

    public AltarCraftingProgressMessage(int x, int y, int z, int progress) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.craftingProgress = progress;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.posX);
        buf.writeInt(this.posY);
        buf.writeInt(this.posZ);
        buf.writeInt(this.craftingProgress);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.posX = buf.readInt();
        this.posY = buf.readInt();
        this.posZ = buf.readInt();
        this.craftingProgress = buf.readInt();
    }
}
