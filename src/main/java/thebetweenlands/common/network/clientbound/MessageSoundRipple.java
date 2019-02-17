package thebetweenlands.common.network.clientbound;

import java.io.IOException;

import javax.xml.ws.handler.MessageContext;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.network.MessageBase;

public class MessageSoundRipple extends MessageBase {
	private BlockPos pos;
	private int delay;

	public MessageSoundRipple() { }

	public MessageSoundRipple(BlockPos pos, int delay) {
		this.pos = pos;
		this.delay = delay;
	}

	@Override
	public void serialize(PacketBuffer buf) throws IOException {
		buf.writeBlockPos(this.pos);
		buf.writeInt(this.delay);
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		this.pos = buf.readBlockPos();
		this.delay = buf.readInt();
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Dist.CLIENT) {
			this.addEffect();
		}
		return null;
	}

	@OnlyIn(Dist.CLIENT)
	private void addEffect() {
		World world = Minecraft.getInstance().world;
		if(world != null) {
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.UNBATCHED, BLParticles.SOUND_RIPPLE.spawn(world, this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, ParticleArgs.get().withData(this.delay)));
		}
	}
}
