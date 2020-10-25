package thebetweenlands.common.entity;

import java.util.function.Supplier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityResurrection extends Entity {
	protected static final byte EVENT_RESPAWN = 80;

	private Supplier<Vec3d> positionSupplier;
	private NBTTagCompound entityNbt;
	private int timer;
	private int resurrectionTime;

	private boolean respawning = false;

	public EntityResurrection(World worldIn) {
		super(worldIn);
		this.setSize(0.1f, 0.1f);
	}

	public EntityResurrection(World world, NBTTagCompound entityNbt, Supplier<Vec3d> positionSupplier, int resurrectionTime) {
		this(world);
		this.entityNbt = entityNbt;
		this.positionSupplier = positionSupplier;
		this.resurrectionTime = resurrectionTime;
	}

	@Override
	protected void entityInit() {

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		this.timer = compound.getInteger("respawnTimer");
		this.entityNbt = compound.getCompoundTag("entityNbt");
		this.resurrectionTime = compound.getInteger("resurrectionTime");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setInteger("respawnTimer", this.timer);
		compound.setTag("entityNbt", this.entityNbt);
		compound.setInteger("resurrectionTime", this.resurrectionTime);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(!this.world.isRemote) {
			if(this.positionSupplier != null) {
				Vec3d newPosition = this.positionSupplier.get();

				if(newPosition != null) {
					this.setLocationAndAngles(newPosition.x, newPosition.y, newPosition.z, this.rotationYaw, this.rotationPitch);
				}
			}

			if(this.timer++ > this.resurrectionTime - 20) {
				this.world.setEntityState(this, EVENT_RESPAWN);

				if(this.timer > this.resurrectionTime) {
					if(this.world instanceof WorldServer) {
						Entity entity = EntityList.createEntityFromNBT(this.entityNbt, this.world);

						if(entity != null) {
							if(entity.isNonBoss() && ((WorldServer) this.world).getEntityFromUuid(entity.getUniqueID()) == null) {
								if(entity instanceof EntityLivingBase) {
									EntityLivingBase living = (EntityLivingBase) entity;

									living.setHealth(Math.max(living.getHealth(), living.getMaxHealth() * 0.5f));
								}

								entity.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);

								entity.motionX = entity.motionY = entity.motionZ = 0;

								this.world.spawnEntity(entity);

								this.world.playSound(null, this.posX, this.posY, this.posZ, SoundRegistry.RESURRECTION, SoundCategory.BLOCKS, 1, 1);
							} else {
								entity.setDead();
							}
						}
					}

					this.setDead();
				}
			}
		} else if(this.respawning) {
			this.spawnParticles();
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);

		if(id == EVENT_RESPAWN) {
			this.respawning = true;
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles() {
		for(int i = 0; i < 3; i++) {
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR, 
					BLParticles.SMOOTH_SMOKE.create(this.world, this.posX + (this.world.rand.nextFloat() - 0.5f) * 2, this.posY + 1 + (this.world.rand.nextFloat() - 0.5f) * 2, this.posZ + (this.world.rand.nextFloat() - 0.5f) * 2,
							ParticleArgs.get()
							.withMotion((this.world.rand.nextFloat() - 0.5f) * 0.1f, (this.world.rand.nextFloat() - 0.5f) * 0.1f, (this.world.rand.nextFloat() - 0.5f) * 0.1f)
							.withScale(16)
							.withColor(1, 1, 1, 0.5f)
							.withData(40, true, 0.0F, true)));
		}
	}
}
