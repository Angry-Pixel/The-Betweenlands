package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityJellyfish extends EntityCreature implements IEntityBL, IEntityAdditionalSpawnData {
	protected Vec3d prevOrientationPos = Vec3d.ZERO;
	protected Vec3d orientationPos = Vec3d.ZERO;

	private static final DataParameter<Byte> JELLYFISH_COLOUR = EntityDataManager.createKey(EntityJellyfish.class, DataSerializers.BYTE);
	private static final DataParameter<Float> JELLYFISH_SIZE = EntityDataManager.<Float>createKey(EntityJellyfish.class, DataSerializers.FLOAT);

	protected float lengthScale = 1.0f;

	public EntityJellyfish(World world) {
		super(world);
		setSize(0.8F, 0.8F);
		moveHelper = new EntityJellyfish.JellyfishMoveHelper(this);
		setPathPriority(PathNodeType.WALKABLE, -8.0F);
		setPathPriority(PathNodeType.BLOCKED, -8.0F);
		setPathPriority(PathNodeType.WATER, 16.0F);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAIMoveTowardsRestriction(this, 0.4D));
		tasks.addTask(1, new EntityAIWander(this, 0.5D, 20));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(JELLYFISH_SIZE, 0.5F);
		dataManager.register(JELLYFISH_COLOUR, (byte)0);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12.0D);
	}

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		if(!getEntityWorld().isRemote) {
			setJellyfishSize(0.5f + rand.nextFloat() * 0.75f);
			setJellyfishLength(0.5f + rand.nextFloat() * 0.5f);
			setJellyfishColour((byte)((byte)rand.nextInt(5)));
		}
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	public float getEyeHeight(){
		return this.height * 0.5F;
	}

	public float getJellyfishSize() {
		return dataManager.get(JELLYFISH_SIZE);
	}

	private void setJellyfishSize(float size) {
		dataManager.set(JELLYFISH_SIZE, size);
		setSize(getJellyfishSize() * 0.5F, getJellyfishSize());
		setPosition(posX, posY, posZ);
	}

	public float getJellyfishLength() {
		return this.lengthScale;
	}

	private void setJellyfishLength(float length) {
		this.lengthScale = length;
	}

	public byte getJellyfishColour() {
		return dataManager.get(JELLYFISH_COLOUR);
	}

	public void setJellyfishColour(byte colour) {
		dataManager.set(JELLYFISH_COLOUR, colour);
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if (JELLYFISH_SIZE.equals(key)) {
			setSize(getJellyfishSize(), getJellyfishSize());
			rotationYaw = rotationYawHead;
			renderYawOffset = rotationYawHead;
		}
		if (JELLYFISH_COLOUR.equals(key)) {
			setJellyfishColour(getJellyfishColour());
		}
		super.notifyDataManagerChange(key);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("jellyfishSize", getJellyfishSize());
		nbt.setFloat("jellyfishLength", getJellyfishLength());
		nbt.setByte("jellyfishColour", getJellyfishColour());

	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setJellyfishSize(nbt.getFloat("jellyfishSize"));
		setJellyfishLength(nbt.getFloat("jellyfishLength"));
		setJellyfishColour(nbt.getByte("jellyfishColour"));
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.JELLYFISH_HURT;
	}
	
    @Override
    protected SoundEvent getDeathSound() {
       return SoundRegistry.JELLYFISH_HURT;
    }
	 
	@Override
	protected SoundEvent getSwimSound() {
		return SoundRegistry.JELLYFISH_SWIM;
	}

	@Override
	public void playSound(SoundEvent soundIn, float volume, float pitch) {
		super.playSound(soundIn, soundIn == this.getSwimSound() ? this.getSoundVolume() : volume, pitch);
	}
	
	@Override
	protected float getSoundVolume() {
		return 0.35F;
	}

	@Nullable
	@Override
	protected ResourceLocation getLootTable() {
		return null;//LootTableRegistry.JELLYFISH;
	}

	@Override
	public boolean getCanSpawnHere() {
		return world.getDifficulty() != EnumDifficulty.PEACEFUL && world.getBlockState(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ))).getBlock() == BlockRegistry.SWAMP_WATER;
	}

	public boolean isGrounded() {
		return !isInWater() && world.isAirBlock(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY + 1), MathHelper.floor(posZ))) && world.getBlockState(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY - 1), MathHelper.floor(posZ))).getBlock().isCollidable();
	}

	@Override
	protected PathNavigate createNavigator(World world){
		return new PathNavigateSwimmer(this, world);
	}

	@Override
	public float getBlockPathWeight(BlockPos pos) {
		return world.getBlockState(pos).getMaterial() == Material.WATER ? 10.0F + world.getLightBrightness(pos) - 0.5F : super.getBlockPathWeight(pos);
	}

	@Override
	public void onLivingUpdate() {	
		if (inWater) {
			setAir(300);
		} else if (onGround) {
			if(getEntityWorld().getTotalWorldTime()%20==0)
				damageEntity(DamageSource.DROWN, 0.5F);
		}

		prevRotationPitch = rotationPitch;
		float speedAngle = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
		if(motionX != 0D && motionZ != 0D)
			rotationPitch += (-((float)MathHelper.atan2((double)speedAngle, motionY)) * (180F / (float)Math.PI) - rotationPitch) * 0.1F;

		super.onLivingUpdate();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(this.world.isRemote) {
			this.updateOrientationPos();
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(source instanceof EntityDamageSource && ((EntityDamageSource) source).getTrueSource() instanceof EntityJellyfish) {
			return false;
		}

		return super.attackEntityFrom(source, amount);
	}

	protected void updateOrientationPos() {
		final double length = 0.5D;

		Vec3d tether = this.getPositionVector();

		this.prevOrientationPos = this.orientationPos;

		this.orientationPos = this.orientationPos.add(0, -0.01f, 0);

		if(this.orientationPos.distanceTo(tether) > length) {
			this.orientationPos = tether.add(this.orientationPos.subtract(tether).normalize().scale(length));
		}
	}

	public Vec3d getOrientationPos(float partialTicks) {
		return this.prevOrientationPos.add(this.orientationPos.subtract(this.prevOrientationPos).scale(partialTicks));
	}

	@Override
	public void travel(float strafe, float up, float forward) {
		if (isServerWorld()) {
			if (isInWater()) {
				moveRelative(strafe, up, forward, 0.075F);
				move(MoverType.SELF, motionX, motionY, motionZ);
				motionX *= 0.75D;
				motionY *= 0.75D;
				motionZ *= 0.75D;

				if (getAttackTarget() == null) {
					motionY -= 0.003D;
				}
			} else {
				super.travel(strafe, up, forward);
			}
		} else {
			super.travel(strafe, up, forward);
		}
	}

	@Override
	public boolean isNotColliding() {
		return getEntityWorld().checkNoEntityCollision(getEntityBoundingBox(), this) && getEntityWorld().getCollisionBoxes(this, getEntityBoundingBox()).isEmpty();
	}

	@Override
	public boolean isPushedByWater() {
		return false;
	}

	static class JellyfishMoveHelper extends EntityMoveHelper {
		private final EntityJellyfish jellyfish;

		public JellyfishMoveHelper(EntityJellyfish jellyfishIn) {
			super(jellyfishIn);
			this.jellyfish= jellyfishIn;
		}

		@Override
		public void onUpdateMoveHelper() {
			if (action == EntityMoveHelper.Action.MOVE_TO && !jellyfish.getNavigator().noPath()) {
				double targetX = posX - jellyfish.posX;
				double targetY = posY - jellyfish.posY;
				double targetZ = posZ - jellyfish.posZ;
				double targetDistance = targetX * targetX + targetY * targetY + targetZ * targetZ;
				targetDistance = (double) MathHelper.sqrt(targetDistance);
				targetY = targetY / targetDistance;
				float targetAngle = (float) (MathHelper.atan2(targetZ, targetX) * (180D / Math.PI)) - 90.0F;
				jellyfish.rotationYaw = limitAngle(jellyfish.rotationYaw, targetAngle, 180.0F);
				jellyfish.renderYawOffset = jellyfish.rotationYaw;
				float travelSpeed = (float) (speed * jellyfish.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
				jellyfish.setAIMoveSpeed(jellyfish.getAIMoveSpeed() + (travelSpeed - jellyfish.getAIMoveSpeed()) * 0.125F);
				jellyfish.motionY += (double) jellyfish.getAIMoveSpeed() * targetY * 0.2D;
				EntityLookHelper entitylookhelper = jellyfish.getLookHelper();
				double targetDirectionX = jellyfish.posX + targetX / targetDistance * 2.0D;
				double targetDirectionY = (double) jellyfish.getEyeHeight() + jellyfish.posY + targetY / targetDistance;
				double targetDirectionZ = jellyfish.posZ + targetZ / targetDistance * 2.0D;
				double lookX = entitylookhelper.getLookPosX();
				double lookY = entitylookhelper.getLookPosY();
				double lookZ = entitylookhelper.getLookPosZ();

				if (!entitylookhelper.getIsLooking()) {
					lookX = targetDirectionX;
					lookY = targetDirectionY;
					lookZ = targetDirectionZ;
				}

				jellyfish.getLookHelper().setLookPosition(lookX + (targetDirectionX - lookX) * 0.125D, lookY + (targetDirectionY - lookY) * 0.125D, lookZ + (targetDirectionZ - lookZ) * 0.125D, 10.0F, 40.0F);

			} else {
				jellyfish.setAIMoveSpeed(0.0F);
			}
		}
	}

	@Override
	public void writeSpawnData(ByteBuf buf) {
		buf.writeFloat(this.lengthScale);
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		this.lengthScale = buf.readFloat();
	}
}
