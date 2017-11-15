package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.EntityAIFlyingWander;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.registries.LootTableRegistry;

public class EntityFirefly extends EntityFlyingCreature implements IEntityBL {
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
		setPathPriority(PathNodeType.WATER, -8F);
		setPathPriority(PathNodeType.BLOCKED, -8.0F);
		setPathPriority(PathNodeType.OPEN, 8.0F);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAIFlyingWander(this, 0.6D));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.05D);
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
			this.moveHelper.setMoveTo(this.posX, this.posY + 1.0D, this.posZ, 1.0D);
		}

		if(getEntityWorld().getBlockState(getPosition().down()).isSideSolid(getEntityWorld(), getPosition().down(), EnumFacing.UP))
			getMoveHelper().setMoveTo(this.posX, this.posY + 1, this.posZ, 0.32D);

		this.renderYawOffset = this.rotationYaw = -((float) Math.atan2(this.motionX, this.motionZ)) * 180.0F / (float) Math.PI;

		this.prevGlowTicks = this.glowTicks;

		if(this.isEntityAlive()) {
			if(!this.world.isRemote) {
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
