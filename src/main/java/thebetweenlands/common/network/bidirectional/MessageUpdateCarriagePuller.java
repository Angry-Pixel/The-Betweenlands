package thebetweenlands.common.network.bidirectional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.common.entity.EntityWeedwoodDraeton;
import thebetweenlands.common.entity.EntityWeedwoodDraeton.Puller;
import thebetweenlands.common.network.MessageEntity;

public class MessageUpdateCarriagePuller extends MessageEntity {
	public static class Position {
		public int id;
		public float x, y, z, mx, my, mz;

		private Position(Puller puller) {
			this.id = puller.id;
			this.x = (float) (puller.x - puller.carriage.posX);
			this.y = (float) (puller.y - puller.carriage.posY);
			this.z = (float) (puller.z - puller.carriage.posZ);
			this.mx = (float) puller.motionX;
			this.my = (float) puller.motionY;
			this.mz = (float) puller.motionZ;
		}

		private Position(int id, float x, float y, float z, float mx, float my, float mz) {
			this.id = id;
			this.x = x;
			this.y = y;
			this.z = z;
			this.mx = mx;
			this.my = my;
			this.mz = mz;
		}
	}

	private Position position;
	private Action action;

	public static enum Action {
		ADD, REMOVE, UPDATE
	}

	public MessageUpdateCarriagePuller() {

	}

	public MessageUpdateCarriagePuller(EntityWeedwoodDraeton carriage, Puller puller, Action action) {
		this.addEntity(carriage);
		this.position = new Position(puller);
		this.action = action;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		super.serialize(buf);

		buf.writeVarInt(this.action.ordinal());

		buf.writeVarInt(this.position.id);
		buf.writeFloat(this.position.x);
		buf.writeFloat(this.position.y);
		buf.writeFloat(this.position.z);
		buf.writeFloat(this.position.mx);
		buf.writeFloat(this.position.my);
		buf.writeFloat(this.position.mz);
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		super.deserialize(buf);

		this.action = Action.values()[buf.readVarInt()];

		this.position = new Position(buf.readVarInt(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		if(ctx.side == Side.SERVER) {
			if(this.action == Action.UPDATE) {
				EntityPlayer player = ctx.getServerHandler().player;

				Entity entity = this.getEntity(0);
				if(entity instanceof EntityWeedwoodDraeton) {
					EntityWeedwoodDraeton carriage = (EntityWeedwoodDraeton) entity;

					if(carriage.getControllingPassenger() == player) {
						Puller puller = carriage.getPullerById(this.position.id);

						if(puller != null) {
							Vec3d pos = new Vec3d(this.position.x, this.position.y, this.position.z);

							//Make sure position is in valid range since it is client controlled
							if(pos.length() > carriage.getMaxTetherLength()) {
								pos = pos.normalize().scale(carriage.getMaxTetherLength());
							}

							//Make sure motion is in valid range
							float speed = (float) Math.sqrt(this.position.mx * this.position.mx + this.position.my * this.position.my + this.position.mz * this.position.mz);
							float maxSpeed = carriage.getMaxPullerSpeed();
							if(speed > maxSpeed) {
								this.position.mx *= 1.0f / speed * maxSpeed;
								this.position.my *= 1.0f / speed * maxSpeed;
								this.position.mz *= 1.0f / speed * maxSpeed;
							}

							carriage.setPacketRelativePullerPosition(puller, this.position.x, this.position.y, this.position.z, this.position.mx, this.position.my, this.position.mz);
						}
					}
				}
			}
		} else {
			Entity entity = this.getEntity(0);
			if(entity instanceof EntityWeedwoodDraeton) {
				EntityWeedwoodDraeton carriage = (EntityWeedwoodDraeton) entity;

				Puller puller;
				if(this.action == Action.ADD) {
					puller = carriage.addPuller(this.position);
				} else if(this.action == Action.REMOVE) {
					carriage.removePullerById(this.position.id);
					puller = null;
				} else {
					puller = carriage.getPullerById(this.position.id);

					//fallback if adding failed somehow
					if(puller == null) {
						puller = carriage.addPuller(this.position);
					}
				}

				if(puller != null) {
					carriage.setPacketRelativePullerPosition(puller, this.position.x, this.position.y, this.position.z, this.position.mx, this.position.my, this.position.mz);
				}
			}
		}

		return null;
	}
}
