package thebetweenlands.common.network.clientbound;
import java.util.Random;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.proxy.ClientProxy;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.monkeytest.ParticlePuzzleBeam;

public class PacketParticle implements IMessage, IMessageHandler<PacketParticle, IMessage> {

	public static enum ParticleType {
		SPORE_JET,
		FLAME,
		BEAM;

		static final ParticleType[] values = values();
	}

	public byte particleType;
	public float posX;
	public float posY;
	public float posZ;
	private static Random rand = new Random();
	private static int counter = 0;

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
				case FLAME:
						TheBetweenlands.proxy.spawnCustomParticle("flame", world, message.posX, message.posY, message.posZ, 0.0D, 0.00D, 0.0D);
					break;
				case BEAM:
					  counter += rand.nextInt(3);
					    if (counter % (Minecraft.getMinecraft().gameSettings.particleSetting == 0 ? 1 : 2 * Minecraft.getMinecraft().gameSettings.particleSetting) == 0);
					      ClientProxy.particleRenderer.addParticle(new ParticlePuzzleBeam(world, message.posX, message.posY, message.posZ, 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 57F, 255F, 56F, 1F, 3F, 40));
					//TheBetweenlands.proxy.spawnCustomParticle("flame", world, message.posX, message.posY, message.posZ, 0.0D, 0.00D, 0.0D);
				break;
				default:
			}
		}
		return null;
	}
	
}