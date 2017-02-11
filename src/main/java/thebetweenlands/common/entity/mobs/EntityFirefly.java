package thebetweenlands.common.entity.mobs;

import java.util.Random;

import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.EntityAIFlyRandomly;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.registries.LootTableRegistry;

public class EntityFirefly extends EntityFlying implements IEntityBL {
	public static final IAttribute GLOW_STRENGTH_ATTRIB = (new RangedAttribute(null, "bl.fireflyGlowStrength", 1, 0, 8)).setDescription("Firefly glow strength").setShouldWatch(true);
	public static final IAttribute GLOW_START_CHANCE = (new RangedAttribute(null, "bl.fireflyGlowStartChance", 0.0025D, 0, 1)).setDescription("Firefly glow start chance per tick");
	public static final IAttribute GLOW_STOP_CHANCE = (new RangedAttribute(null, "bl.fireflyGlowStopChance", 0.00083D, 0, 1)).setDescription("Firefly glow stop chance per tick");

	private static final DataParameter<Float> GLOW_STRENGTH = EntityDataManager.<Float>createKey(EntityFirefly.class, DataSerializers.FLOAT);

	protected double aboveLayer = 6.0D;

	protected int glowTicks = 0;
	protected int prevGlowTicks = 0;

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
		this.getAttributeMap().registerAttribute(GLOW_STRENGTH_ATTRIB);
		this.getAttributeMap().registerAttribute(GLOW_START_CHANCE);
		this.getAttributeMap().registerAttribute(GLOW_STOP_CHANCE);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(GLOW_STRENGTH, (float)GLOW_STRENGTH_ATTRIB.getDefaultValue());
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.FIREFLY;
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

		this.prevGlowTicks = this.glowTicks;

		if(this.isEntityAlive()) {
			if(!this.worldObj.isRemote) {
				if(!this.isGlowActive() && this.rand.nextDouble() < this.getEntityAttribute(GLOW_START_CHANCE).getAttributeValue()) {
					this.setGlowStrength(this.getEntityAttribute(GLOW_STRENGTH_ATTRIB).getAttributeValue());
				} else if(this.isGlowActive() && this.rand.nextDouble() < this.getEntityAttribute(GLOW_STOP_CHANCE).getAttributeValue()) {
					this.setGlowStrength(0);
				}
			}

			if(this.isGlowActive() && this.glowTicks < 20) {
				this.glowTicks++;
			} else if(!this.isGlowActive() && this.glowTicks > 0) {
				this.glowTicks--;
			}
		} else {
			this.setGlowStrength(0);
			this.glowTicks = 0;
		}
	}

	@Override
	protected boolean canDespawn() {
		return true;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setDouble("glowStrength", this.getGlowStrength());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.setGlowStrength(nbt.getDouble("glowStrength"));
	}

	/**
	 * Returns whether the glow is active
	 * @return
	 */
	public boolean isGlowActive() {
		return this.getGlowStrength() > 0;
	}

	/**
	 * Returns the glow strength
	 * @return
	 */
	public double getGlowStrength() {
		return this.getDataManager().get(GLOW_STRENGTH);
	}

	/**
	 * Sets the glow strength
	 * @param strength
	 */
	public void setGlowStrength(double strength) {
		this.getDataManager().set(GLOW_STRENGTH, (float)strength);
	}

	/**
	 * Returns the interpolated glow ticks
	 * @param partialTicks
	 * @return
	 */
	public float getGlowTicks(float partialTicks) {
		return this.prevGlowTicks + (this.glowTicks - this.prevGlowTicks) * partialTicks;
	}
}
