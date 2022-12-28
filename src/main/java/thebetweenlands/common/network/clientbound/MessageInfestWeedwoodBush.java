package thebetweenlands.common.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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
import thebetweenlands.common.tile.TileEntityGrubHub;

public class MessageInfestWeedwoodBush extends MessageBase {
	private BlockPos tilePos;
	private BlockPos bushPos;

	public MessageInfestWeedwoodBush() { }

	public MessageInfestWeedwoodBush(TileEntityGrubHub tile, BlockPos bushPos) {
		this.tilePos = tile.getPos();
		this.bushPos = bushPos;
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		this.tilePos = buf.readBlockPos();
		this.bushPos = buf.readBlockPos();
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeBlockPos(this.tilePos);
		buf.writeBlockPos(this.bushPos);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Side.CLIENT) {
			this.handle();
		}

		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handle() {
		World world = Minecraft.getMinecraft().world;
		if(world != null) {
			TileEntity te = world.getTileEntity(this.tilePos);

			if(te instanceof TileEntityGrubHub) {
				((TileEntityGrubHub) te).switchTextureCount = 10;

				Vec3d dir = new Vec3d((this.bushPos.getX() + 0.5D) - (this.tilePos.getX() + 0.5D), (this.bushPos.getY() + 1D) - (this.tilePos.getY() + 0.325D), (this.bushPos.getZ() + 0.5D) - (this.tilePos.getZ() + 0.5D));

				for(int i = 0; i < 20 + world.rand.nextInt(5); i++) {
					BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(world, this.tilePos.getX() + 0.5F, this.tilePos.getY() + 0.325F, this.tilePos.getZ() + 0.5F, 
							ParticleArgs.get()
							.withMotion(dir.x * 0.08f, dir.y * 0.08F, dir.z * 0.08F)
							.withScale(0.6f + world.rand.nextFloat() * 5.0F)
							.withColor(1F, 1.0F, 1.0F, 0.05f)
							.withData(80, true, 0.01F, true)));
				}
			}
		}
	}
}
