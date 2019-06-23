package thebetweenlands.common.entity;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.shader.ShaderHelper;

public class EntityDecayPitTarget extends Entity implements IEntityMultiPartPitTarget {
	public float animationTicks = 0;
	public float animationTicksPrev = 0;
	public int animationTicksChain = 0;
	public int animationTicksChainPrev = 0;
	public EntityDecayPitTargetPart[] shield_array;
	public EntityDecayPitTargetPart shield_1;
	public EntityDecayPitTargetPart shield_2;
	public EntityDecayPitTargetPart shield_3;
	public EntityDecayPitTargetPart shield_4;
	public EntityDecayPitTargetPart shield_5;
	public EntityDecayPitTargetPart shield_6;
	public EntityDecayPitTargetPart shield_7;
	public EntityDecayPitTargetPart shield_8;
	public EntityDecayPitTargetPart shield_9;
	public EntityDecayPitTargetPart shield_10;
	public EntityDecayPitTargetPart shield_11;
	public EntityDecayPitTargetPart shield_12;
	public EntityDecayPitTargetPart shield_13;
	public EntityDecayPitTargetPart shield_14;
	public EntityDecayPitTargetPart shield_15;
	public EntityDecayPitTargetPart shield_16;
	public EntityDecayPitTargetPart target;
	public EntityDecayPitTargetPart bottom;
	public EntityDecayPitTargetPart chain_1;
	public EntityDecayPitTargetPart chain_2;
	public EntityDecayPitTargetPart chain_3;
	public EntityDecayPitTargetPart chain_4;
	
	private static final DataParameter<Boolean> IS_RAISING = EntityDataManager.createKey(EntityDecayPitTarget.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_MOVING = EntityDataManager.createKey(EntityDecayPitTarget.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_SLOW = EntityDataManager.createKey(EntityDecayPitTarget.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> LENGTH = EntityDataManager.createKey(EntityDecayPitTarget.class, DataSerializers.VARINT);
	private static final DataParameter<Float> PROGRESS = EntityDataManager.createKey(EntityDecayPitTarget.class, DataSerializers.FLOAT);

	public EntityDecayPitTarget(World world) {
		super(world);
		setSize(5F, 13F);
		shield_array = new EntityDecayPitTargetPart[] {
				shield_1 = new EntityDecayPitTargetPart(this, "part1", 1F, 1F),
				shield_2 = new EntityDecayPitTargetPart(this, "part2", 1F, 1F),
				shield_3 = new EntityDecayPitTargetPart(this, "part3", 1F, 1F),
				shield_4 = new EntityDecayPitTargetPart(this, "part4", 1F, 1F),
				shield_5 = new EntityDecayPitTargetPart(this, "part5", 1F, 1F),
				shield_6 = new EntityDecayPitTargetPart(this, "part6", 1F, 1F),
				shield_7 = new EntityDecayPitTargetPart(this, "part7", 1F, 1F),
				shield_8 = new EntityDecayPitTargetPart(this, "part8", 1F, 1F),
				shield_9 = new EntityDecayPitTargetPart(this, "part9", 1F, 1F),
				shield_10 = new EntityDecayPitTargetPart(this, "part10", 1F, 1F),
				shield_11 = new EntityDecayPitTargetPart(this, "part11", 1F, 1F),
				shield_12 = new EntityDecayPitTargetPart(this, "part12", 1F, 1F),
				shield_13 = new EntityDecayPitTargetPart(this, "part13", 1F, 1F),
				shield_14 = new EntityDecayPitTargetPart(this, "part16", 1F, 1F),
				shield_15 = new EntityDecayPitTargetPart(this, "part15", 1F, 1F),
				shield_16 = new EntityDecayPitTargetPart(this, "part16", 1F, 1F),
				target = new EntityDecayPitTargetPart(this, "target", 2.1875F, 2F),
				bottom = new EntityDecayPitTargetPart(this, "bottom", 3F, 1F),
				chain_1 = new EntityDecayPitTargetPart(this, "chain_1", 0.625F, 2F),
				chain_2 = new EntityDecayPitTargetPart(this, "chain_2", 0.625F, 2F),
				chain_3 = new EntityDecayPitTargetPart(this, "chain_3", 0.625F, 2F),
				chain_4 = new EntityDecayPitTargetPart(this, "chain_4", 0.625F, 2F)
				};
	}

	@Override
	protected void entityInit() {
		dataManager.register(IS_RAISING, false);
		dataManager.register(IS_MOVING, false);
		dataManager.register(IS_SLOW, true);
		dataManager.register(PROGRESS, 2F);
		dataManager.register(LENGTH, 2);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		animationTicksPrev = animationTicks;
		animationTicksChainPrev = animationTicksChain;
		animationTicks += 1F;
		if (animationTicks >= 359F)
			animationTicks = animationTicksPrev = 0;

		for(EntityDecayPitTargetPart part :shield_array) {
			part.prevPosX = part.lastTickPosX = part.posX;
			part.prevPosY = part.lastTickPosY = part.posY;
			part.prevPosZ = part.lastTickPosZ = part.posZ;
		}

		setNewShieldHitboxPos(animationTicks, shield_1);
		setNewShieldHitboxPos(-animationTicks + 22.5F, shield_2);
		setNewShieldHitboxPos(animationTicks + 45F, shield_3);
		setNewShieldHitboxPos(-animationTicks + 67.5F, shield_4);
		setNewShieldHitboxPos(animationTicks + 90F, shield_5);
		setNewShieldHitboxPos(-animationTicks + 112.5F, shield_6);
		setNewShieldHitboxPos(animationTicks + 135F, shield_7);
		setNewShieldHitboxPos(-animationTicks + 157F, shield_8);

		setNewShieldHitboxPos(animationTicks + 180F, shield_9);
		setNewShieldHitboxPos(-animationTicks + 202.5F, shield_10);
		setNewShieldHitboxPos(animationTicks + 225F, shield_11);
		setNewShieldHitboxPos(-animationTicks + 247.5F, shield_12);
		setNewShieldHitboxPos(animationTicks + 270F, shield_13);
		setNewShieldHitboxPos(-animationTicks + 292.5F, shield_14);
		setNewShieldHitboxPos(animationTicks + 315F, shield_15);
		setNewShieldHitboxPos(-animationTicks + 337.5F, shield_16);

		target.setPosition(posX, posY + height - getProgress() + 1.25, posZ);
		bottom.setPosition(posX, posY + height - getProgress(), posZ);
		chain_1.setPosition(posX, posY + height - getProgress(), posZ - 1D);
		chain_2.setPosition(posX, posY + height - getProgress(), posZ + 1D);
		chain_3.setPosition(posX + 1D, posY + height - getProgress(), posZ);
		chain_4.setPosition(posX - 1D, posY + height - getProgress(), posZ);
		target.setPosition(posX, chain_1.posY - 2D, posZ);
		bottom.setPosition(posX, chain_1.posY - 5D, posZ);

		if (isMoving()) {
			if (isSlow())
				animationTicksChain++;
			else
				animationTicksChain += 8;

			if (!isRaising())
				setProgress(getProgress() + 0.0078125F * 8F);

			if (isRaising())
				setProgress(getProgress() - 0.0078125F);

			setHangingLength(chain_1, getProgress());
			setHangingLength(chain_2, getProgress());
			setHangingLength(chain_3, getProgress());
			setHangingLength(chain_4, getProgress());
		}

		if (animationTicksChainPrev >= 128) {
			animationTicksChain = animationTicksChainPrev = 0;
			setMoving(false);
		}

		if (!getEntityWorld().isRemote && getProgress() > 2F && getEntityWorld().getTotalWorldTime() % 60 == 0) // upsy-daisy
			moveUp();
	}

	protected void setHangingLength(EntityDecayPitTargetPart chain, float extended) {
		chain.height = extended;
		AxisAlignedBB axisalignedbb = new AxisAlignedBB(chain.posX - chain.width * 0.5D, getEntityBoundingBox().maxY - getProgress(), chain.posZ - chain.width * 0.5D, chain.posX + chain.width * 0.5D, getEntityBoundingBox().maxY, chain.posZ + chain.width * 0.5D);
		chain.setEntityBoundingBox(axisalignedbb);
		chain.onUpdate();
	}

	protected void setNewShieldHitboxPos(float animationTicks, EntityDecayPitTargetPart shield) {
		double a = Math.toRadians(animationTicks);
		double offSetX = -Math.sin(a) * 2.825D;
		double offSetZ = Math.cos(a) * 2.825D;
		float wobble = 0F;
		if (shield == shield_1 || shield == shield_3 || shield == shield_5 || shield == shield_7 || shield == shield_9 || shield == shield_11 || shield == shield_13 || shield == shield_15)
			wobble = MathHelper.sin(animationTicks * 0.14F) * 1.2F;
		else
			wobble = -MathHelper.sin(animationTicks * 0.14F) * 1.2F;
		float squarePoint = Math.signum(wobble);
		if(squarePoint == -1F)
			wobble = 0F;
		shield.setPosition(posX + offSetX, target.posY + target.height / 2.0D - shield.height + wobble, posZ + offSetZ);
		shield.rotationYaw = animationTicks + 180F;
		shield.onUpdate();
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Nullable
	public Entity[] getParts() {
		return shield_array;
	}

	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0;
		motionY = 0;
		motionZ = 0;
	}

	@Override
	public boolean getIsInvulnerable() {
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		return false;
	}

	@Override
	public boolean attackEntityFromPart(EntityDecayPitTargetPart part, DamageSource source, float damage) {
		if (!getEntityWorld().isRemote) {
			if (part != target && part != bottom && part != chain_1 && part != chain_2 && part != chain_3 && part != chain_4) {
				if (source instanceof EntityDamageSourceIndirect) {
					EntityRootGrabber grabber = new EntityRootGrabber(getEntityWorld());
					grabber.setPosition(source.getTrueSource().getPosition().down(), 40);
					getEntityWorld().spawnEntity(grabber);
				}
			}

			if (part == target) {
				moveDown();
				return true;
			}

			if (part == chain_1 || part == chain_2 || part == chain_3 || part == chain_4)
				if (source instanceof EntityDamageSourceIndirect) // may want to remove this line so it 'dinks' on all damage attempts
					getEntityWorld().playSound((EntityPlayer) null, posX, posY, posZ, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.5F, 3F);
		}
		if (getEntityWorld().isRemote)
			if (part == target) {
				shootBeamsAtThings(new Vec3d(0D, -4.5D + getProgress(), -11D));
				shootBeamsAtThings(new Vec3d(11D, -4.5D + getProgress(), 0D));
				shootBeamsAtThings(new Vec3d(0D, -4.5D + getProgress(), 11D));
				shootBeamsAtThings(new Vec3d(-11D, -4.5D + getProgress(), 0D));
			}
		return false;
	}

	private void moveUp() {
		if (getProgress() > 2) {
			for (EntityDecayPitChain chain : getChains()) {
				chain.setRaising(false);
				chain.setMoving(true);
				chain.setSlow(true);

			}
			setRaising(true);
			setMoving(true);
			setSlow(true);
		}
	}

	private void moveDown() {
		if (getProgress() < 7) {
			for (EntityDecayPitChain chain : getChains()) {
				chain.setRaising(true);
				chain.setMoving(true);
				chain.setSlow(false);
			}
			setRaising(false);
			setMoving(true);
			setSlow(false);
		}
	}

	@SideOnly(Side.CLIENT)
	private void shootBeamsAtThings(Vec3d target) {
		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, BLParticles.PUZZLE_BEAM_2.create(world, this.target.posX + target.x, this.target.posY + this.target.height * 0.5D + target.y, this.target.posZ + target.z, ParticleArgs.get().withMotion(0, 0, 0).withColor(255F, 102F, 0F, 1F).withScale(3.5F).withData(30, target.scale(-1))));
		for(int i = 0; i < 2; i++) {
			float offsetLen = this.world.rand.nextFloat();
			Vec3d offset = new Vec3d(target.x * offsetLen + world.rand.nextFloat() * 0.2f - 0.1f, target.y * offsetLen + world.rand.nextFloat() * 0.2f - 0.1f, target.z * offsetLen + world.rand.nextFloat() * 0.2f - 0.1f);
			float vx = (world.rand.nextFloat() * 2f - 1) * 0.0025f;
			float vy = (world.rand.nextFloat() * 2f - 1) * 0.0025f + 0.008f;
			float vz = (world.rand.nextFloat() * 2f - 1) * 0.0025f;
			float scale = 0.5f + world.rand.nextFloat();
			if(ShaderHelper.INSTANCE.canUseShaders() && world.rand.nextBoolean()) {
				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.HEAT_HAZE_BLOCK_ATLAS, BLParticles.SMOOTH_SMOKE.create(world, this.target.posX + offset.x, this.target.posY + this.target.height * 0.5D + offset.y, this.target.posZ + offset.z, ParticleArgs.get().withMotion(vx, vy, vz).withColor(1, 1, 1, 0.2F).withScale(scale * 8).withData(80, true, 0.0F, true)));
			} else {
				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, this.target.posX + offset.x, this.target.posY + this.target.height * 0.5D + offset.y, this.target.posZ + offset.z, ParticleArgs.get().withMotion(vx, vy, vz).withColor(255F, 102F, 0F, 1F).withScale(scale).withData(100)));
			}
		}
	}

	public List<EntityDecayPitChain> getChains() {
		return getWorld().<EntityDecayPitChain>getEntitiesWithinAABB(EntityDecayPitChain.class, getEntityBoundingBox().grow(10D, 0D, 10D));
    }
	
	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if (PROGRESS.equals(key))
			setLength(MathHelper.floor(getProgress()));
		super.notifyDataManagerChange(key);
	}

	public void setLength(int length) {
		dataManager.set(LENGTH, length);
	}

	public int getLength() {
		return dataManager.get(LENGTH);
	}
	
	public void setProgress(float progress) {
		dataManager.set(PROGRESS, progress);
	}

	public float getProgress() {
		return dataManager.get(PROGRESS);
	}

	public void setRaising(boolean raising) {
		dataManager.set(IS_RAISING, raising);
	}

	public boolean isRaising() {
		return dataManager.get(IS_RAISING);
	}

	public void setMoving(boolean moving) {
		dataManager.set(IS_MOVING, moving);
	}
	
	public boolean isMoving() {
		return dataManager.get(IS_MOVING);
	}

	public void setSlow(boolean slow) {
		dataManager.set(IS_SLOW, slow);
	}

	public boolean isSlow() {
		return dataManager.get(IS_SLOW);
	}

	@Override
	public World getWorld() {
		return getEntityWorld();
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setLength(nbt.getInteger("length"));
		setProgress(nbt.getFloat("progress"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("length", getLength());
		nbt.setFloat("progress", getProgress());
	}
}