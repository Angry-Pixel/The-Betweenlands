package thebetweenlands.common.network.bidirectional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.entity.draeton.EntityDraeton.Puller;
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

	public MessageUpdateCarriagePuller(EntityDraeton carriage, Puller puller, Action action) {
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

	private float getFloatOrDefault(float x, float def) {
		if(Float.isFinite(x)) {
			return x;
		}
		return def;
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		super.deserialize(buf);

		this.action = Action.values()[buf.readVarInt()];

		this.position = new Position(
				buf.readVarInt(),
				this.getFloatOrDefault(buf.readFloat(), 0), this.getFloatOrDefault(buf.readFloat(), 0), this.getFloatOrDefault(buf.readFloat(), 0),
				this.getFloatOrDefault(buf.readFloat(), 0), this.getFloatOrDefault(buf.readFloat(), 0), this.getFloatOrDefault(buf.readFloat(), 0)
				);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		//Check for invalid ID
		if(this.position.id < 0) {
			return null;
		}

		if(ctx.side == Side.SERVER) {
			if(this.action == Action.UPDATE) {
				EntityPlayer player = ctx.getServerHandler().player;

				Entity entity = this.getEntity(0);
				if(entity instanceof EntityDraeton) {
					EntityDraeton carriage = (EntityDraeton) entity;

					if(carriage.getControllingPassenger() == player) {
						Puller puller = carriage.getPullerById(this.position.id);

						if(puller != null) {
							//Make sure position is in valid range since it is client controlled
							float dist = (float) Math.sqrt(this.position.x * this.position.x + this.position.y * this.position.y + this.position.z * this.position.z);
							float maxDist = carriage.getMaxTetherLength();
							if(dist > maxDist) {
								this.position.x *= 1.0f / dist * maxDist;
								this.position.y *= 1.0f / dist * maxDist;
								this.position.z *= 1.0f / dist * maxDist;
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
			if(entity instanceof EntityDraeton) {
				EntityDraeton carriage = (EntityDraeton) entity;

				if(this.action == Action.ADD) {
					carriage.addPuller(this.position);
				} else if(this.action == Action.REMOVE) {
					carriage.removePullerById(this.position.id);
				} else {
					Puller puller = carriage.getPullerById(this.position.id);

					//fallback if adding failed somehow
					if(puller == null) {
						puller = carriage.addPuller(this.position);
					} else {
						carriage.setPacketRelativePullerPosition(puller, this.position.x, this.position.y, this.position.z, this.position.mx, this.position.my, this.position.mz);
					}
				}
			}
		}

		return null;
	}
}
