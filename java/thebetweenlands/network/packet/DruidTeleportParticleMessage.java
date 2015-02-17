package thebetweenlands.network.packet;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class DruidTeleportParticleMessage implements IMessage {

	public float posX, posY, posZ;

	public DruidTeleportParticleMessage() {
	}

	public DruidTeleportParticleMessage(float x, float y, float z) {
		posX = x;
		posY = y;
		posZ = z;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeFloat(posX);
		buf.writeFloat(posY);
		buf.writeFloat(posZ);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		posX = buf.readFloat();
		posY = buf.readFloat();
		posZ = buf.readFloat();
	}
}
