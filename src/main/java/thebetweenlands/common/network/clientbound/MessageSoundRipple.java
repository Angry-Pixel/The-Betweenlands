package thebetweenlands.common.network.clientbound;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
		if(ctx.side == Side.CLIENT) {
			this.addEffect();
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void addEffect() {
		World world = Minecraft.getMinecraft().world;
		if(world != null) {
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.UNBATCHED, BLParticles.SOUND_RIPPLE.spawn(world, this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, ParticleArgs.get().withData(this.delay)));
		}
	}
}
