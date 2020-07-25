package thebetweenlands.common.entity.draeton;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thebetweenlands.api.entity.IPullerEntity;
import thebetweenlands.common.entity.mobs.EntityChiromawTame;

public class EntityPullerChiromaw extends EntityChiromawTame implements IPullerEntity, IEntityAdditionalSpawnData {
	private int carriageId;
	private int pullerId;

	private DraetonPhysicsPart puller;

	public EntityPullerChiromaw(World world) {
		super(world);
	}

	public EntityPullerChiromaw(World world, EntityDraeton carriage, DraetonPhysicsPart puller) {
		super(world);
		this.setPuller(carriage, puller);
	}

	@Override
	public String getName() {
		if (getElectricBoogaloo()) {
			return I18n.translateToLocal("entity.thebetweenlands.chiromaw_tame_lightning.name");
		} else {
			return I18n.translateToLocal("entity.thebetweenlands.chiromaw_tame.name");
		}
	}

	@Override
	public EntityDraeton getCarriage() {
		Entity entity = this.world.getEntityByID(this.carriageId);
		return entity instanceof EntityDraeton ? (EntityDraeton)entity : null;
	}
	
	@Override
	public void setPuller(EntityDraeton carriage, DraetonPhysicsPart puller) {
		this.puller = puller;
		this.pullerId = puller.id;
		this.carriageId = carriage.getEntityId();
	}

	@Override
	public float getPull(float pull) {
		return pull;
	}

	@Override
	public float getCarriageDrag(float drag) {
		return drag;
	}

	@Override
	public float getDrag(float drag) {
		return drag;
	}

	@Override
	public Entity createReleasedEntity() {
		EntityChiromawTame entity = new EntityChiromawTame(this.world);
		entity.readFromNBT(this.writeToNBT(new NBTTagCompound()));
		entity.setNoAI(false);
		entity.setUniqueId(UUID.randomUUID());
		entity.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
		return entity;
	}
	
	@Override
	public void spawnReleasedEntity() {
		if(!this.world.isRemote) {
			this.world.spawnEntity(this.createReleasedEntity());

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
			return this.puller.carriage.isControlling(this.puller);
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
					DraetonPhysicsPart puller = ((EntityDraeton) entity).getPhysicsPartById(this.pullerId);
					if(puller != null) {
						this.puller = puller;
						puller.setEntity(this);
					}
				}
			}
		} else {
			if(this.puller.carriage.isControlling(this.puller)) {
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