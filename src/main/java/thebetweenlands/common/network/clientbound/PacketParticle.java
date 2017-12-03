package thebetweenlands.common.network.clientbound;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.TheBetweenlands;

public class PacketParticle implements IMessage, IMessageHandler<PacketParticle, IMessage> {

	public static enum ParticleType {
		SPORE_JET;

		static final ParticleType[] values = values();
	}

	public byte particleType;
	public float posX;
	public float posY;
	public float posZ;

	public PacketParticle() {
	}

	public PacketParticle(ParticleType particleType, float posX, float posY, float posZ) {
		this.particleType = (byte) particleType.ordinal();
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeByte(particleType);
		buffer.writeFloat(posX).writeFloat(posY).writeFloat(posZ);
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		particleType = buffer.readByte();
		posX = buffer.readFloat();
		posY = buffer.readFloat();
		posZ = buffer.readFloat();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(PacketParticle message, MessageContext ctx) {
		World world = FMLClientHandler.instance().getWorldClient();
		if (world == null)
			return null;

		if (world.isRemote) {
			if (message.particleType < 0 || message.particleType >= ParticleType.values.length)
				return null;

			switch (ParticleType.values[message.particleType]) {
				case SPORE_JET:
					for (double yy = message.posY; yy < message.posY + 2D; yy += 0.5D) {
						double d0 = message.posX - 0.075F;
						double d1 = yy;
						double d2 = message.posZ - 0.075F;
						double d3 = message.posX + 0.075F;
						double d4 = message.posZ + 0.075F;
						double d5 = message.posX;
						double d6 = yy + 0.25F;
						double d7 = message.posZ;
						TheBetweenlands.proxy.spawnCustomParticle("spell", world, d0, d1, d2, 0.0D, 0.05D, 0.0D);
						TheBetweenlands.proxy.spawnCustomParticle("spell", world, d0, d1, d4, 0.0D, 0.05D, 0.0D);
						TheBetweenlands.proxy.spawnCustomParticle("spell", world, d3, d1, d2, 0.0D, 0.05D, 0.0D);
						TheBetweenlands.proxy.spawnCustomParticle("spell", world, d3, d1, d4, 0.0D, 0.05D, 0.0D);
						TheBetweenlands.proxy.spawnCustomParticle("spell", world, d5, d6, d7, 0.0D, 0.05D, 0.0D);
					}
					break;
				default:
			}
		}
		return null;
	}
	
}