package thebetweenlands.common.message.clientbound;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thebetweenlands.client.particle.BLParticles;
import thebetweenlands.common.message.BLMessage;

public class MessageWeedwoodBushRustle extends BLMessage {
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
			World world = FMLClientHandler.instance().getWorldClient();
			int leafCount = (int) (47 * this.getStrength()) + 1;
			float x = this.getPos().getX() + 0.5F;
			float y = this.getPos().getY() + 0.5F;
			float z = this.getPos().getZ() + 0.5F;
			while (leafCount-- > 0) {
				BLParticles.SPLASH_TAR.spawn(world, x, y, z); //TODO WEEDWOOD_EAF
			}
		}
		return null;
	}

	public BlockPos getPos() {
		return pos;
	}

	public float getStrength() {
		return strength;
	}
}
