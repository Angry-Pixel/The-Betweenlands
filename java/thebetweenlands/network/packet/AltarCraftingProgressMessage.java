package thebetweenlands.network.packet;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class AltarCraftingProgressMessage implements IMessage {

	public int posX, posY, posZ, craftingProgress;

	public AltarCraftingProgressMessage() {
	}

	public AltarCraftingProgressMessage(int x, int y, int z, int progress) {
		posX = x;
		posY = y;
		posZ = z;
		craftingProgress = progress;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(posX);
		buf.writeInt(posY);
		buf.writeInt(posZ);
		buf.writeInt(craftingProgress);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		posX = buf.readInt();
		posY = buf.readInt();
		posZ = buf.readInt();
		craftingProgress = buf.readInt();
	}
}
