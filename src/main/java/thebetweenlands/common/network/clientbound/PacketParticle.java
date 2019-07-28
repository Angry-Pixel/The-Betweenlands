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
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.TheBetweenlands;

public class PacketParticle implements IMessage, IMessageHandler<PacketParticle, IMessage> {
	//TODO Delete this
	
	
	public static enum ParticleType {
		SPORE_JET,
		SLUDGE_JET,
		FLAME,
		BEAM,
		BEAM_YELLOW,
		GOOP_SPLAT, 
		SPLODE_SHROOM;
		static final ParticleType[] values = values();
	}

	public byte particleType;
	public float posX;
	public float posY;
	public float posZ;
	public float scale;
	private static Random rand = new Random();
	private static int counter = 0;

	public PacketParticle() {
	}

	public PacketParticle(ParticleType particleType, float posX, float posY, float posZ, float scale) {
		this.particleType = (byte) particleType.ordinal();
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.scale = scale;
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeByte(particleType);
		buffer.writeFloat(posX).writeFloat(posY).writeFloat(posZ).writeFloat(scale);
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		particleType = buffer.readByte();
		posX = buffer.readFloat();
		posY = buffer.readFloat();
		posZ = buffer.readFloat();
		scale = buffer.readFloat();
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
						BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d0, d1, d2, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
						BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d0, d1, d4, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
						BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d3, d1, d2, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
						BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d3, d1, d4, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
						BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d0, d1, d2, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
						BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d5, d6, d7, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
						BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, d0, d1, d2, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(105F, 70F, 40F, 1F).withScale(1.5F).withData(100)));
					}
					break;
				case SLUDGE_JET:
					for (double yy = message.posY; yy < message.posY + 2.5D; yy += 0.5D) {
						double d0 = message.posX - 0.075F;
						double d1 = yy;
						double d2 = message.posZ - 0.075F;
						double d3 = message.posX + 0.075F;
						double d4 = message.posZ + 0.075F;
						double d5 = message.posX;
						double d6 = yy + 0.25F;
						double d7 = message.posZ;

						BLParticles.TAR_BEAST_DRIP.spawn(world, d0, d1, d4, ParticleArgs.get().withMotion(4f * yy * (rand.nextFloat() - 0.5f), 4f * yy, 4f * yy * (rand.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
						BLParticles.TAR_BEAST_DRIP.spawn(world, d3, d1, d2, ParticleArgs.get().withMotion(4f * yy * (rand.nextFloat() - 0.5f), 4f * yy, 4f * yy * (rand.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
						BLParticles.TAR_BEAST_DRIP.spawn(world, d3, d1, d4, ParticleArgs.get().withMotion(4f * yy * (rand.nextFloat() - 0.5f), 4f * yy, 4f * yy * (rand.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
						BLParticles.TAR_BEAST_DRIP.spawn(world, d0, d1, d2, ParticleArgs.get().withMotion(4f * yy * (rand.nextFloat() - 0.5f), 4f * yy, 4f * yy * (rand.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
						BLParticles.TAR_BEAST_DRIP.spawn(world, d5, d6, d7, ParticleArgs.get().withMotion(4f * yy * (rand.nextFloat() - 0.5f), 4f * yy, 4f * yy * (rand.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
						BLParticles.TAR_BEAST_DRIP.spawn(world, d0, d1, d2, ParticleArgs.get().withMotion(4f * yy * (rand.nextFloat() - 0.5f), 4f * yy, 4f * yy * (rand.nextFloat() - 0.5f)).withScale(2.5F).withData(100)).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
					}
					break;
				case FLAME:
						TheBetweenlands.proxy.spawnCustomParticle("flame", world, message.posX, message.posY, message.posZ, 0.0D, 0.00D, 0.0D);
					break;
				case BEAM:
					  counter += rand.nextInt(3);
					    if (counter % (Minecraft.getMinecraft().gameSettings.particleSetting == 0 ? 1 : 2 * Minecraft.getMinecraft().gameSettings.particleSetting) == 0);
					    BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, message.posX, message.posY, message.posZ, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(57F, 255F, 56F, 1F).withScale(0.5F + message.scale).withData(100)));
				break;
				case BEAM_YELLOW:
					  counter += rand.nextInt(3);
					    if (counter % (Minecraft.getMinecraft().gameSettings.particleSetting == 0 ? 1 : 2 * Minecraft.getMinecraft().gameSettings.particleSetting) == 0);
					    BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, message.posX, message.posY, message.posZ, ParticleArgs.get().withMotion(0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f), 0.0125f * (rand.nextFloat() - 0.5f)).withColor(255F, 102F, 0F, 1F).withScale(0.5F + message.scale).withData(100)));
				break;
				case GOOP_SPLAT:
					for (int count = 0; count <= 200; ++count)
						TheBetweenlands.proxy.spawnCustomParticle("slime", world, message.posX + (world.rand.nextDouble() - 0.5D) , message.posY + world.rand.nextDouble(), message.posZ + (world.rand.nextDouble() - 0.5D), 0, 0, 0);
					break;
				case SPLODE_SHROOM:
					for (int count = 0; count <= 200; ++count)
						TheBetweenlands.proxy.spawnCustomParticle("splode_shroom", world, message.posX + (world.rand.nextDouble() - 0.5D) , message.posY + world.rand.nextDouble(), message.posZ + (world.rand.nextDouble() - 0.5D), 0, 0, 0);
					break;
				default:
			}
		}
		return null;
	}
	
}