package thebetweenlands.common.entity.mobs;

import java.util.Random;

import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.common.entity.ai.EntityAIFlyRandomly;
import thebetweenlands.common.entity.movement.FlightMoveHelper;

public class EntityFirefly extends EntityFlying implements IMob, IEntityBL {
	protected double aboveLayer = 6.0D;

	protected int glowTicks = 0;
	protected int prevGlowTicks = 0;

	private static final DataParameter<Boolean> GLOW = EntityDataManager.<Boolean>createKey(EntityBlaze.class, DataSerializers.BOOLEAN);

	public EntityFirefly(World world) {
		super(world);
		this.setSize(0.6F, 0.6F);
		this.ignoreFrustumCheck = true;
		this.moveHelper = new FlightMoveHelper(this);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(5, new EntityAIFlyRandomly<EntityFirefly>(this) {
			@Override
			protected double getTargetY(Random rand, double distanceMultiplier) {
				int worldHeight = MathHelper.floor_double(EntityFirefly.this.aboveLayer);

				PooledMutableBlockPos checkPos = PooledMutableBlockPos.retain();

				for(int yo = 0; yo < MathHelper.ceiling_double_int(EntityFirefly.this.aboveLayer); yo++) {
					checkPos.setPos(this.entity.posX, this.entity.posY - yo, this.entity.posZ);

					if(!this.entity.getEntityWorld().isBlockLoaded(checkPos))
						return this.entity.posY;

					if(!this.entity.getEntityWorld().isAirBlock(checkPos)) {
						worldHeight = checkPos.getY();
						break;
					}
				}

				checkPos.release();

				if(this.entity.posY > worldHeight + EntityFirefly.this.aboveLayer) {
					return this.entity.posY + (-rand.nextFloat() * 2.0F) * 16.0F * distanceMultiplier;
				} else {
					float rndFloat = rand.nextFloat() * 2.0F - 1.0F;
					if(rndFloat > 0.0D) {
						double maxRange = worldHeight + EntityFirefly.this.aboveLayer - this.entity.posY;
						return this.entity.posY + (-rand.nextFloat() * 2.0F) * maxRange * distanceMultiplier;
					} else {
						return this.entity.posY + (rand.nextFloat() * 2.0F - 1.0F) * 16.0F * distanceMultiplier;
					}
				}
			}

			@Override
			protected double getFlightSpeed() {
				return 0.035D;
			}
		});
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(GLOW, true);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (this.isInWater()) {
			this.moveHelper.setMoveTo(this.posX, this.posY + 1.0D, this.posZ, 0.1D);
		}

		this.renderYawOffset = this.rotationYaw = -((float) Math.atan2(this.motionX, this.motionZ)) * 180.0F / (float) Math.PI;

		if(!this.isGlowActive() && this.rand.nextInt(400) == 0) {
			this.setGlow(true);
		} else if(this.isGlowActive() && this.rand.nextInt(1200) == 0) {
			this.setGlow(false);
		}

		this.prevGlowTicks = this.glowTicks;
		if(this.isGlowActive() && this.glowTicks < 20) {
			this.glowTicks++;
		} else if(!this.isGlowActive() && this.glowTicks > 0) {
			this.glowTicks--;
		}
	}

	@Override
	protected boolean canDespawn() {
		return true;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("glowing", this.isGlowActive());

		super.writeEntityToNBT(nbt);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		this.setGlow(nbt.getBoolean("glowing"));

		super.readEntityFromNBT(nbt);
	}

	public boolean isGlowActive() {
		return this.getDataManager().get(GLOW);
	}

	public void setGlow(boolean glowing) {
		this.getDataManager().set(GLOW, glowing);
	}

	public float getGlowTicks(float partialTicks) {
		return this.prevGlowTicks + (this.glowTicks - this.prevGlowTicks) * partialTicks;
	}
}
