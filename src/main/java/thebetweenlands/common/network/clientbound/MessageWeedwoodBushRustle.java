package thebetweenlands.common.network.clientbound;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.network.MessageBase;

public class MessageWeedwoodBushRustle extends MessageBase {
	private BlockPos pos;
	private float strength;

	public MessageWeedwoodBushRustle() {
	}

	public MessageWeedwoodBushRustle(BlockPos pos, float strength) {
		this.pos = pos;
		this.strength = strength;
	}

	@Override
	public void serialize(PacketBuffer buffer) {
		buffer.writeLong(pos.toLong());
		buffer.writeFloat(strength);
	}

	@Override
	public void deserialize(PacketBuffer buffer) {
		pos = BlockPos.fromLong(buffer.readLong());
		strength = buffer.readFloat();
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if (ctx.side.isClient()) {
			this.handle();
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handle() {
		World world = FMLClientHandler.instance().getWorldClient();
		if(world != null) {
			int leafCount = (int) (47 * this.getStrength()) + 1;
			float x = this.getPos().getX() + 0.5F;
			float y = this.getPos().getY() + 0.5F;
			float z = this.getPos().getZ() + 0.5F;
			while (leafCount-- > 0) {
				float dx = world.rand.nextFloat() * 2 - 1;
				float dy = world.rand.nextFloat() * 2 - 0.5F;
				float dz = world.rand.nextFloat() * 2 - 1;
				float mag = 0.01F + world.rand.nextFloat() * 0.07F;
				BLParticles.WEEDWOOD_LEAF.spawn(world, x, y, z, ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag));
			}
		}
	}

	public BlockPos getPos() {
		return pos;
	}

	public float getStrength() {
		return strength;
	}
}
