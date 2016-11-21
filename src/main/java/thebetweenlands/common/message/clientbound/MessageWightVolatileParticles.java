package thebetweenlands.common.message.clientbound;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.particle.BLParticles;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.message.MessageEntity;

public class MessageWightVolatileParticles extends MessageEntity {
	public MessageWightVolatileParticles() { }

	public MessageWightVolatileParticles(Entity entity) {
		super(entity);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		if(ctx.side == Side.CLIENT) {
			this.handle();
		}

		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handle() {
		Entity entity = this.getEntity();
		if(entity != null) {
			for (int i = 0; i < 80; i++) {
				double px = entity.posX + entity.worldObj.rand.nextFloat() * 0.7F;
				double py = entity.posY + entity.worldObj.rand.nextFloat() * 2.2F;
				double pz = entity.posZ + entity.worldObj.rand.nextFloat() * 0.7F;
				Vec3d vec = new Vec3d(px, py, pz).subtract(new Vec3d(entity.posX + 0.35F, entity.posY + 1.1F, entity.posZ + 0.35F)).normalize();
				BLParticles.SWAMP_SMOKE.spawn(entity.worldObj, px, py, pz, ParticleFactory.ParticleArgs.get().withMotion(vec.xCoord * 0.25F, vec.yCoord * 0.25F, vec.zCoord * 0.25F));
			}
		}
	}
}
