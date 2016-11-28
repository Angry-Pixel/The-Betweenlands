package thebetweenlands.common.message.clientbound;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.common.entity.mobs.EntityDarkDruid;
import thebetweenlands.common.message.BLMessage;

public class MessageDruidTeleportParticles extends BLMessage {
	public MessageDruidTeleportParticles() {
	}

	public double x, y, z;

	public MessageDruidTeleportParticles(EntityDarkDruid druid) {
		this.x = druid.posX;
		this.y = druid.posY;
		this.z = druid.posZ;
	}

	@Override
	public void deserialize(PacketBuffer buffer) {
		this.x = buffer.readDouble();
		this.y = buffer.readDouble();
		this.z = buffer.readDouble();
	}

	@Override
	public void serialize(PacketBuffer buffer) {
		buffer.writeDouble(this.x);
		buffer.writeDouble(this.y);
		buffer.writeDouble(this.z);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		World world = FMLClientHandler.instance().getWorldClient();
		if (world != null && world.isRemote) {
			for (int a = 0; a < 360; a += 4) {
				double rad = a * Math.PI / 180D;
				BLParticles.SMOKE.spawn(world, this.x - MathHelper.sin((float) rad) * 0.25D, this.y, this.z + MathHelper.cos((float) rad) * 0.25D, ParticleFactory.ParticleArgs.get().withMotion(-MathHelper.sin((float) rad) * 0.1D, 0.01D, MathHelper.cos((float) rad) * 0.1));
				BLParticles.SMOKE.spawn(world, this.x - MathHelper.sin((float) rad) * 0.25D, this.y + 0.5D, this.z + MathHelper.cos((float) rad) * 0.25D, ParticleFactory.ParticleArgs.get().withMotion(-MathHelper.sin((float) rad) * 0.1D, 0.01D, MathHelper.cos((float) rad) * 0.1));
				BLParticles.SMOKE.spawn(world, this.x - MathHelper.sin((float) rad) * 0.25D, this.y + 1D, this.z + MathHelper.cos((float) rad) * 0.25D, ParticleFactory.ParticleArgs.get().withMotion(-MathHelper.sin((float) rad) * 0.1D, 0.01D, MathHelper.cos((float) rad) * 0.1));
			}
		}
		return null;
	}
}
