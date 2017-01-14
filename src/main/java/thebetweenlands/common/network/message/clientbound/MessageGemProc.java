package thebetweenlands.common.network.message.clientbound;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.network.message.MessageEntity;

public class MessageGemProc extends MessageEntity {
	private byte type;

	public MessageGemProc() { }

	public MessageGemProc(Entity entity, byte type) {
		super(entity);
		this.type = type;
	}

	public byte getType() {
		return this.type;
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		super.deserialize(buf);
		this.type = buf.readByte();
	}

	@Override
	public void serialize(PacketBuffer buf) {
		super.serialize(buf);
		buf.writeByte(this.type);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			this.handle();
		}

		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handle() {
		byte type = this.getType();
		Entity entityHit = this.getEntity();
		if(entityHit != null) {
			Random rnd = entityHit.worldObj.rand;
			for(int i = 0; i < 40; i++) {
				double x = entityHit.posX + rnd.nextFloat() * entityHit.width * 2.0F - entityHit.width;
				double y = entityHit.getEntityBoundingBox().minY + rnd.nextFloat() * entityHit.height;
				double z = entityHit.posZ + rnd.nextFloat() * entityHit.width * 2.0F - entityHit.width;
				double dx = x - entityHit.posX;
				double dy = y - entityHit.posY;
				double dz = z - entityHit.posZ;
				double len = Math.sqrt(dx*dx + dy*dy + dz*dz);
				ParticleArgs<?> args = ParticleArgs.get();
				switch(type) {
				case 0:
				default:
					args.withMotion(dx/len, dy/len, dz/len);
					break;
				case 1:
					args.withMotion(-dx/len, -dy/len, -dz/len);
					break;
				}
				BLParticles.GEM_PROC.spawn(entityHit.worldObj, x, y, z, args);
			}
		}
	}
}
