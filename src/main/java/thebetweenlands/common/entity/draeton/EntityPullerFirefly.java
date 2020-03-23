package thebetweenlands.common.entity.draeton;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thebetweenlands.common.entity.draeton.EntityDraeton.IPullerEntity;
import thebetweenlands.common.entity.draeton.EntityDraeton.Puller;
import thebetweenlands.common.entity.mobs.EntityFirefly;

public class EntityPullerFirefly extends EntityFirefly implements IPullerEntity, IEntityAdditionalSpawnData {
	private int carriageId;
	private int pullerId;

	private Puller puller;

	public EntityPullerFirefly(World world) {
		super(world);
	}

	public EntityPullerFirefly(World world, EntityDraeton carriage, Puller puller) {
		super(world);
		this.setPuller(carriage, puller);
	}

	@Override
	public void setPuller(EntityDraeton carriage, Puller puller) {
		this.puller = puller;
		this.pullerId = puller.id;
		this.carriageId = carriage.getEntityId();
	}

	@Override
	public float getPull(float pull) {
		return pull * 0.25f;
	}

	@Override
	public float getCarriageDrag(float drag) {
		return 1 - (1 - drag) * 0.5f;
	}

	@Override
	public float getDrag(float drag) {
		return drag;
	}

	@Override
	public void releaseEntity() {
		if(!this.world.isRemote) {
			EntityFirefly entity = new EntityFirefly(this.world);
			entity.readFromNBT(this.writeToNBT(new NBTTagCompound()));
			entity.setNoAI(false);
			entity.setUniqueId(UUID.randomUUID());
			entity.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
			this.world.spawnEntity(entity);

			this.setDead();
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(source == DamageSource.IN_WALL) {
			return false;
		}
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean writeToNBTOptional(NBTTagCompound compound) {
		//Entity is saved and handled by carriage
		return false;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public boolean isAIDisabled() {
		if(this.puller != null) {
			return this.puller.isControlling();
		}
		return true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(this.puller == null || !this.puller.isActive) {
			if(!this.world.isRemote) {
				//Don't remove immediately if entity is already dying
				if(this.isEntityAlive()) {
					this.setDead();
				}
			} else {
				Entity entity = this.world.getEntityByID(this.carriageId);
				if(entity instanceof EntityDraeton) {
					Puller puller = ((EntityDraeton) entity).getPullerById(this.pullerId);
					if(puller != null) {
						this.puller = puller;
						puller.setEntity(this);
					}
				}
			}
		} else {
			if(this.puller.isControlling()) {
				this.setPositionAndRotation(this.puller.x, this.puller.y, this.puller.z, 0, 0);
				this.rotationYaw = this.rotationYawHead = this.renderYawOffset = (float)Math.toDegrees(Math.atan2(this.puller.motionZ, this.puller.motionX)) - 90;
				this.rotationPitch = (float)Math.toDegrees(-Math.atan2(this.puller.motionY, Math.sqrt(this.puller.motionX * this.puller.motionX + this.puller.motionZ * this.puller.motionZ)));
				this.onGround = false;
			} else {
				this.puller.x = this.posX;
				this.puller.y = this.posY;
				this.puller.z = this.posZ;
			}
		}
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(this.carriageId);
		buffer.writeInt(this.pullerId);
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		this.carriageId = buffer.readInt();
		this.pullerId = buffer.readInt();
	}
}