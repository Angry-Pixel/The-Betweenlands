package thebetweenlands.common.network.clientbound;

import java.io.IOException;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.capability.circlegem.CircleGem;
import thebetweenlands.common.capability.circlegem.CircleGem.CombatType;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.network.MessageEntity;

public class MessageGemProc extends MessageEntity {
	private CircleGem gem;

	public MessageGemProc() { }

	public MessageGemProc(Entity entity, boolean offensive, CircleGemType gem) {
		this.addEntity(entity);
		this.gem = new CircleGem(gem, offensive ? CombatType.OFFENSIVE : CombatType.DEFENSIVE);
	}

	public CircleGem getGem() {
		return this.gem;
	}

	@Override
	public void deserialize(PacketBuffer buf) throws IOException {
		super.deserialize(buf);
		this.gem = CircleGem.readFromNBT(buf.readCompoundTag());
	}

	@Override
	public void serialize(PacketBuffer buf) throws IOException {
		super.serialize(buf);
		buf.writeCompoundTag(this.gem.writeToNBT(new NBTTagCompound()));
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
		CircleGem gem = this.getGem();
		Entity entityHit = this.getEntity(0);
		if(entityHit != null) {
			Random rnd = entityHit.world.rand;
			for(int i = 0; i < 40; i++) {
				double x = entityHit.posX + rnd.nextFloat() * entityHit.width * 2.0F - entityHit.width;
				double y = entityHit.getEntityBoundingBox().minY + rnd.nextFloat() * entityHit.height;
				double z = entityHit.posZ + rnd.nextFloat() * entityHit.width * 2.0F - entityHit.width;
				double dx = x - entityHit.posX;
				double dy = y - (entityHit.posY + entityHit.height / 2.0F);
				double dz = z - entityHit.posZ;
				double len = Math.sqrt(dx*dx + dy*dy + dz*dz);
				ParticleArgs<?> args = ParticleArgs.get();

				switch(gem.getCombatType()) {
				case OFFENSIVE:
				default:
					args.withMotion(dx/len, dy/len, dz/len);
					break;
				case DEFENSIVE:
					args.withMotion(-dx/len, -dy/len, -dz/len);
					break;
				}

				switch(gem.getGemType()) {
				default:
				case AQUA:
					args.withColor(0.35F, 0.35F, 1, 1);
					break;
				case CRIMSON:
					args.withColor(1, 0, 0, 1);
					break;
				case GREEN:
					args.withColor(0.3F, 1.0F, 0.3F, 1.0F);
					break;
				}

				BLParticles.GEM_PROC.spawn(entityHit.world, x, y, z, args);
			}
		}
	}
}
