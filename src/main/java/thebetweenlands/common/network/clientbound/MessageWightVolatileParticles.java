package thebetweenlands.common.network.clientbound;

import javax.xml.ws.handler.MessageContext;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.common.network.MessageEntity;

public class MessageWightVolatileParticles extends MessageEntity {
	public MessageWightVolatileParticles() { }

	public MessageWightVolatileParticles(Entity entity) {
		this.addEntity(entity);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		if(ctx.side == Dist.CLIENT) {
			this.handle();
		}

		return null;
	}

	@OnlyIn(Dist.CLIENT)
	private void handle() {
		Entity entity = this.getEntity(0);
		if(entity != null) {
			for (int i = 0; i < 80; i++) {
				double px = entity.posX + entity.world.rand.nextFloat() * 0.7F;
				double py = entity.posY + entity.world.rand.nextFloat() * 2.2F;
				double pz = entity.posZ + entity.world.rand.nextFloat() * 0.7F;
				Vec3d vec = new Vec3d(px, py, pz).subtract(new Vec3d(entity.posX + 0.35F, entity.posY + 1.1F, entity.posZ + 0.35F)).normalize();
				BLParticles.SWAMP_SMOKE.spawn(entity.world, px, py, pz, ParticleFactory.ParticleArgs.get().withMotion(vec.x * 0.25F, vec.y * 0.25F, vec.z * 0.25F));
			}
		}
	}
}
