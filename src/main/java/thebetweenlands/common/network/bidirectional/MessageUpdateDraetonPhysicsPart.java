package thebetweenlands.common.network.bidirectional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.common.entity.draeton.DraetonPhysicsPart;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.network.MessageEntity;

public class MessageUpdateDraetonPhysicsPart extends MessageEntity {
	public static class Position {
		public int id, slot;
		public DraetonPhysicsPart.Type type;
		public float x, y, z, mx, my, mz;

		private Position(DraetonPhysicsPart part) {
			this.id = part.id;
			this.slot = part.slot;
			this.type = part.type;
			this.x = (float) (part.x - part.carriage.posX);
			this.y = (float) (part.y - part.carriage.posY);
			this.z = (float) (part.z - part.carriage.posZ);
			this.mx = (float) part.motionX;
			this.my = (float) part.motionY;
			this.mz = (float) part.motionZ;
		}

		private Position(int id, int slot, DraetonPhysicsPart.Type type, float x, float y, float z, float mx, float my, float mz) {
			this.id = id;
			this.slot = slot;
			this.type = type;
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

	public MessageUpdateDraetonPhysicsPart() {

	}

	public MessageUpdateDraetonPhysicsPart(EntityDraeton carriage, DraetonPhysicsPart part, Action action) {
		this.addEntity(carriage);
		this.position = new Position(part);
		this.action = action;
	}

	@Override
	public void serialize(PacketBuffer buf) {
		super.serialize(buf);

		buf.writeVarInt(this.action.ordinal());

		buf.writeVarInt(this.position.id);
		buf.writeVarInt(this.position.slot);
		buf.writeVarInt(this.position.type.ordinal());
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
				buf.readVarInt(), buf.readVarInt(),
				DraetonPhysicsPart.Type.values()[buf.readVarInt()],
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
						DraetonPhysicsPart part = carriage.getPhysicsPartById(this.position.id);

						if(part != null) {
							//Make sure position is in valid range since it is client controlled
							float dist = (float) Math.sqrt(this.position.x * this.position.x + this.position.y * this.position.y + this.position.z * this.position.z);
							float maxDist = carriage.getMaxTetherLength(part);
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

							carriage.setPacketRelativePartPosition(part, this.position.x, this.position.y, this.position.z, this.position.mx, this.position.my, this.position.mz);
						}
					}
				}
			}
		} else {
			Entity entity = this.getEntity(0);
			if(entity instanceof EntityDraeton) {
				this.processClient((EntityDraeton) entity);
			}
		}

		return null;
	}
	
	public void processClient(EntityDraeton carriage) {
		if(this.action == Action.ADD) {
			carriage.addPhysicsPart(this.position);
		} else if(this.action == Action.REMOVE) {
			carriage.removePhysicsPartById(this.position.id);
		} else {
			DraetonPhysicsPart part = carriage.getPhysicsPartById(this.position.id);

			//fallback if adding failed somehow
			if(part == null) {
				part = carriage.addPhysicsPart(this.position);
			} else {
				carriage.setPacketRelativePartPosition(part, this.position.x, this.position.y, this.position.z, this.position.mx, this.position.my, this.position.mz);
			}
		}
	}
}
