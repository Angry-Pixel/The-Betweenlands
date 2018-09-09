package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IBLBoss;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.api.entity.IEntityMusic;
import thebetweenlands.client.audio.EntitySound;
import thebetweenlands.client.audio.FortressBossIdleSound;
import thebetweenlands.common.network.datamanager.CustomDataSerializers;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.sound.BLSoundEvent;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;
import thebetweenlands.util.RotationMatrix;

public class EntityFortressBoss extends EntityMob implements IEntityBL, IBLBoss, IEntityMusic {
	public static final RotationMatrix ROTATION_MATRIX = new RotationMatrix();

	private final BossInfoServer bossInfo = (BossInfoServer)(new BossInfoServer(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS)).setDarkenSky(false);
	protected static final DataParameter<Integer> SHIELD_STATE = EntityDataManager.<Integer>createKey(EntityFortressBoss.class, DataSerializers.VARINT);
	protected static final DataParameter<Float> SHIELD_ROTATION = EntityDataManager.<Float>createKey(EntityFortressBoss.class, DataSerializers.FLOAT);
	protected static final DataParameter<Boolean> FLOATING_STATE = EntityDataManager.<Boolean>createKey(EntityFortressBoss.class, DataSerializers.BOOLEAN);
	protected static final DataParameter<Boolean> GROUND_ATTACK_STATE = EntityDataManager.<Boolean>createKey(EntityFortressBoss.class, DataSerializers.BOOLEAN);
	protected static final DataParameter<Vec3d> ANCHOR = EntityDataManager.<Vec3d>createKey(EntityFortressBoss.class, CustomDataSerializers.VEC3D);
	protected static final DataParameter<Float> ANCHOR_RADIUS = EntityDataManager.<Float>createKey(EntityFortressBoss.class, DataSerializers.FLOAT);
	private static final DataParameter<Optional<UUID>> BOSSINFO_ID = EntityDataManager.createKey(EntityFortressBoss.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	public static final double SHIELD_OFFSET_X = 0.0D;
	public static final double SHIELD_OFFSET_Y = 1D;
	public static final double SHIELD_OFFSET_Z = 0.0D;

	private static final double ICOSAHEDRON_X = 0.525731112119133606D;
	private static final double ICOSAHEDRON_Z = 0.850650808352039932D;
	public static final double[][] ICOSAHEDRON_VERTICES = new double[][] {{-ICOSAHEDRON_X, 0.0, ICOSAHEDRON_Z}, {ICOSAHEDRON_X, 0.0, ICOSAHEDRON_Z}, {-ICOSAHEDRON_X, 0.0, -ICOSAHEDRON_Z}, {ICOSAHEDRON_X, 0.0, -ICOSAHEDRON_Z},
		{0.0, ICOSAHEDRON_Z, ICOSAHEDRON_X}, {0.0, ICOSAHEDRON_Z, -ICOSAHEDRON_X}, {0.0, -ICOSAHEDRON_Z, ICOSAHEDRON_X}, {0.0, -ICOSAHEDRON_Z, -ICOSAHEDRON_X},
		{ICOSAHEDRON_Z, ICOSAHEDRON_X, 0.0}, {-ICOSAHEDRON_Z, ICOSAHEDRON_X, 0.0}, {ICOSAHEDRON_Z, -ICOSAHEDRON_X, 0.0}, {-ICOSAHEDRON_Z, -ICOSAHEDRON_X, 0.0}
	};
	public static final int[][] ICOSAHEDRON_INDICES = new int[][] {
		{0,4,1}, {0,9,4}, {9,5,4}, {4,5,8}, {4,8,1},
		{8,10,1}, {8,3,10},{5,3,8}, {5,2,3}, {2,7,3},
		{7,10,3}, {7,6,10}, {7,11,6}, {11,0,6}, {0,1,6},
		{6,1,10}, {9,0,11}, {9,11,2}, {9,2,5}, {7,2,11}
	};

	private boolean[] activeShields = new boolean[20];

	public int[] shieldAnimationTicks = new int[20];

	public final AxisAlignedBB coreBoundingBox;

	private Vec3d anchor = new Vec3d(0, 0, 0);
	private double anchorRadius;

	private float shieldRotationYaw, shieldRotationPitch, shieldRotationRoll, lastShieldRotationYaw, lastShieldRotationPitch, lastShieldRotationRoll, shieldExplosion, lastShieldExplosion;

	private int groundTicks = 0;

	private int turretTicks = -1;

	private int groundAttackTicks = -1;

	private int turretStreak = -1;
	private int turretStreakTicks = 0;

	private int wightSpawnTicks = -1;

	private int teleportTicks = -1;

	private List<EntityLivingBase> trackedEntities = new ArrayList<EntityLivingBase>();

	private int blockadeSpawnTicks = -1;

	private int deathTicks = 0;

	private ISound currentIdleSound;

	public EntityFortressBoss(World world) {
		super(world);
		float width = 1.9F;
		float height = 1.9F;
		this.setSize(width, height);
		float coreWidth = 1.0F;
		float coreHeight = 1.0F;
		this.coreBoundingBox = new AxisAlignedBB(-coreWidth/2.0F, 0F + height / 4.0F, -coreWidth/2.0F, coreWidth/2.0F, coreHeight + height / 4.0F, coreWidth/2.0F);
		for(int i = 0; i < 20; i++) {
			this.activeShields[i] = true;
		}
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(320.0D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(SHIELD_STATE, 0);
		this.getDataManager().register(SHIELD_ROTATION, 0.0F);
		this.getDataManager().register(FLOATING_STATE, true);
		this.getDataManager().register(GROUND_ATTACK_STATE, false);
		this.getDataManager().register(ANCHOR, Vec3d.ZERO);
		this.getDataManager().register(ANCHOR_RADIUS, 0.0F);
		this.getDataManager().register(BOSSINFO_ID, Optional.absent());
	}

	public float getShieldExplosion(float partialTicks) {
		return this.lastShieldExplosion + (this.shieldExplosion - this.lastShieldExplosion) * partialTicks;
	}

	public void setAnchor(Vec3d anchor, double radius) {
		this.anchor = anchor;
		this.anchorRadius = radius;
	}

	public Vec3d getAnchor() {
		return this.anchor;
	}

	public double getAnchorRadius() {
		return this.anchorRadius;
	}

	private int packShieldData() {
		int packedData = 0;
		for(int i = 0; i <= 19; i++) {
			packedData |= (this.activeShields[i] ? 1 : 0) << i;
		}
		return packedData;
	}

	private void unpackShieldData(int packedData) {
		for(int i = 0; i <= 19; i++) {
			this.activeShields[i] = ((packedData >> i) & 1) == 1 ? true : false;
		}
	}

	public boolean hasShield() {
		for(int i = 0; i <= 19; i++) {
			if(this.activeShields[i])
				return true;
		}
		return false;
	}

	public boolean isShieldActive(int shield) {
		return this.activeShields[shield];
	}

	public void setShieldActive(int shield, boolean active) {
		this.activeShields[shield] = active;
	}

	public int getGroundAttackTicks() {
		return this.groundAttackTicks;
	}

	@SideOnly(Side.CLIENT)
	public float getShieldRotationYaw(float partialTicks) {
		return this.lastShieldRotationYaw + (this.shieldRotationYaw - this.lastShieldRotationYaw) * partialTicks;
	}

	@SideOnly(Side.CLIENT)
	public float getShieldRotationPitch(float partialTicks) {
		return this.lastShieldRotationPitch + (this.shieldRotationPitch - this.lastShieldRotationPitch) * partialTicks;
	}

	@SideOnly(Side.CLIENT)
	public float getShieldRotationRoll(float partialTicks) {
		return this.lastShieldRotationRoll + (this.shieldRotationRoll - this.lastShieldRotationRoll) * partialTicks;
	}

	public int rayTraceShield(Vec3d pos, Vec3d ray, boolean back) {
		int shield = -1;
		double centroidX = 0;
		double centroidY = 0;
		double centroidZ = 0;

		ROTATION_MATRIX.setRotations((float)Math.toRadians(-this.shieldRotationPitch), (float)Math.toRadians(-this.shieldRotationYaw), (float)Math.toRadians(-this.shieldRotationRoll));

		Vec3d centerPos = new Vec3d(this.posX + SHIELD_OFFSET_X, this.posY + SHIELD_OFFSET_Y, this.posZ + SHIELD_OFFSET_Z);

		//Transform position and ray to local space
		pos = ROTATION_MATRIX.transformVec(pos, centerPos);
		ray = ROTATION_MATRIX.transformVec(ray, new Vec3d(0, 0, 0));

		for(int i = 0; i <= 19; i++) {
			if(!this.isShieldActive(i)) {
				continue;
			}
			double v3[] = ICOSAHEDRON_VERTICES[ICOSAHEDRON_INDICES[i][0]];
			double v2[] = ICOSAHEDRON_VERTICES[ICOSAHEDRON_INDICES[i][1]];
			double v1[] = ICOSAHEDRON_VERTICES[ICOSAHEDRON_INDICES[i][2]];
			double centerX = (v1[0]+v2[0]+v3[0])/3;
			double centerY = (v1[1]+v2[1]+v3[1])/3;
			double centerZ = (v1[2]+v2[2]+v3[2])/3;
			double len = Math.sqrt(centerX*centerX + centerY*centerY + centerZ*centerZ);
			double a = len + this.getShieldExplosion(1.0F);
			Vec3d center = new Vec3d(centerX, centerY, centerZ);
			centerX += this.posX + SHIELD_OFFSET_X;
			centerY += this.posY + SHIELD_OFFSET_Y;
			centerZ += this.posZ + SHIELD_OFFSET_Z;
			Vec3d vert1Exploded = new Vec3d(v1[0], v1[1], v1[2]);
			double b = vert1Exploded.dotProduct(center);
			double d = a * Math.tan(b);
			double vertexExplode = Math.sqrt(a*a + d*d) - 1;
			Vec3d v1Normalized = new Vec3d(v1[0], v1[1], v1[2]).normalize();
			Vec3d v2Normalized = new Vec3d(v2[0], v2[1], v2[2]).normalize();
			Vec3d v3Normalized = new Vec3d(v3[0], v3[1], v3[2]).normalize();
			Vec3d vert1 = new Vec3d(v1[0]+v1Normalized.x*vertexExplode, v1[1]+v1Normalized.y*vertexExplode, v1[2]+v1Normalized.z*vertexExplode);
			Vec3d vert2 = new Vec3d(v2[0]+v2Normalized.x*vertexExplode, v2[1]+v2Normalized.y*vertexExplode, v2[2]+v2Normalized.z*vertexExplode);
			Vec3d vert3 = new Vec3d(v3[0]+v3Normalized.x*vertexExplode, v3[1]+v3Normalized.y*vertexExplode, v3[2]+v3Normalized.z*vertexExplode);
			vert1 = vert1.addVector(this.posX + SHIELD_OFFSET_X, this.posY + SHIELD_OFFSET_Y, this.posZ + SHIELD_OFFSET_Z);
			vert2 = vert2.addVector(this.posX + SHIELD_OFFSET_X, this.posY + SHIELD_OFFSET_Y, this.posZ + SHIELD_OFFSET_Z);
			vert3 = vert3.addVector(this.posX + SHIELD_OFFSET_X, this.posY + SHIELD_OFFSET_Y, this.posZ + SHIELD_OFFSET_Z);
			Vec3d normal = vert2.subtract(vert1).crossProduct(vert3.subtract(vert1));

			if(rayTraceTriangle(pos, ray, vert1, vert2, vert3) && (back || normal.normalize().dotProduct(ray.normalize()) < Math.cos(Math.toRadians(90)))) {
				double dx = centerX - pos.x;
				double dy = centerY - pos.y;
				double dz = centerZ - pos.z;
				double pdx = centroidX - pos.x;
				double pdy = centroidY - pos.y;
				double pdz = centroidZ - pos.z;
				if(shield == -1 || (Math.sqrt(dx*dx+dy*dy+dz*dz) < Math.sqrt(pdx*pdx+pdy*pdy+pdz*pdz))) {
					shield = i;
					centroidX = centerX;
					centroidY = centerY;
					centroidZ = centerZ;
				}
			}
		}

		return shield;
	}

	public static boolean rayTraceTriangle(Vec3d pos, Vec3d ray, Vec3d v0, Vec3d v1, Vec3d v2) {
		final double epsilon = 0.00001;
		Vec3d diff1 = v1.subtract(v0);
		Vec3d diff2 = v2.subtract(v0);
		Vec3d rayCross = ray.crossProduct(diff2);
		double angleDifference = diff1.dotProduct(rayCross);
		if (angleDifference > -epsilon && angleDifference < epsilon)
			return false;
		double f = 1.0D / angleDifference;
		Vec3d s = pos.subtract(v0);
		double u = f * (s.dotProduct(rayCross));
		if (u < 0.0 || u > 1.0) {
			return false;
		}
		Vec3d q = s.crossProduct(diff1);
		double v = f * ray.dotProduct(q);
		if (v < 0.0 || u + v > 1.0) {
			return false;
		}
		double t = f * diff2.dotProduct(q);
		if (1 - t > epsilon) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isFloating() {
		return this.getDataManager().get(FLOATING_STATE);
	}

	public void setFloating(boolean floating) {
		this.getDataManager().set(FLOATING_STATE, floating);;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source instanceof EntityDamageSource) {
			if(((EntityDamageSource)source).getTrueSource() instanceof Entity) {
				Entity entity = (Entity) ((EntityDamageSource)source).getTrueSource();
				if(source instanceof EntityDamageSourceIndirect && ((EntityDamageSourceIndirect)source).getTrueSource() != null)
					entity = ((EntityDamageSourceIndirect)source).getTrueSource();
				if(entity == null)
					return false;
				Vec3d ray = entity.getLookVec();
				if(entity instanceof EntityLivingBase == false) {
					ray = new Vec3d(entity.motionX, entity.motionY, entity.motionZ).normalize();
				}
				ray = ray.scale(64.0D);
				Vec3d pos = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight() + (entity instanceof EntityPlayer && ((EntityPlayer)entity).isSneaking() ? -0.08D : 0.0D), entity.posZ);
				if(this.hasShield() && (entity instanceof EntityPlayer == false || !((EntityPlayer)entity).capabilities.isCreativeMode) || entity.isSneaking()) {
					int shieldHit = this.rayTraceShield(pos, ray, false);
					if(shieldHit >= 0) {
						if(!this.world.isRemote && entity.isSneaking() && ((EntityPlayer)entity).capabilities.isCreativeMode) {
							this.setShieldActive(shieldHit, false);
						}
						if(this.world.isRemote) {
							this.shieldAnimationTicks[shieldHit] = 20;
						}
						this.world.playSound(null, this.posX, this.posY, this.posZ, SoundRegistry.FORTRESS_BOSS_NOPE, SoundCategory.HOSTILE, 1, 1);
						double dx = entity.posX - this.posX;
						double dy = entity.posY - this.posY;
						double dz = entity.posZ - this.posZ;
						double len = Math.sqrt(dx*dx+dy*dy+dz*dz);
						entity.motionX = dx / len * 0.8F;
						entity.motionY = dy / len * 0.8F;
						entity.motionZ = dz / len * 0.8F;
						entity.attackEntityFrom(DamageSource.MAGIC, 2);
						return false;
					}
				}
				RayTraceResult result = this.coreBoundingBox.offset(this.posX, this.posY, this.posZ).calculateIntercept(pos, ray.addVector(pos.x, pos.y, pos.z));
				if(result != null) {
					return super.attackEntityFrom(source, damage);
				} else {
					return false;
				}
			}
		}
		if(DamageSource.OUT_OF_WORLD.getDamageType().equals(source.getDamageType())) {
			return super.attackEntityFrom(source, damage);
		}
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("shields", this.packShieldData());
		nbt.setDouble("anchorX", this.anchor.x);
		nbt.setDouble("anchorY", this.anchor.y);
		nbt.setDouble("anchorZ", this.anchor.z);
		nbt.setDouble("anchorRadius", this.anchorRadius);
		nbt.setBoolean("floating", this.isFloating());
		nbt.setInteger("groundTicks", this.groundTicks);
		nbt.setInteger("turretTicks", this.turretTicks);
		nbt.setInteger("groundAttackTicks", this.groundAttackTicks);
		nbt.setInteger("turretStreak", this.turretStreak);
		nbt.setInteger("turretStreakTicks", this.turretStreakTicks);
		nbt.setInteger("wightSpawnTicks", this.wightSpawnTicks);
		nbt.setInteger("teleportTicks", this.teleportTicks);
		nbt.setInteger("blockadeSpawnTicks", this.blockadeSpawnTicks);
		nbt.setInteger("deathTicks", this.deathTicks);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.unpackShieldData(nbt.getInteger("shields"));
		this.anchor = new Vec3d(nbt.getDouble("anchorX"), nbt.getDouble("anchorY"), nbt.getDouble("anchorZ"));
		this.anchorRadius = nbt.getDouble("anchorRadius");
		this.setFloating(nbt.getBoolean("floating"));
		this.groundTicks = nbt.getInteger("groundTicks");
		this.turretTicks = nbt.getInteger("turretTicks");
		this.groundAttackTicks = nbt.getInteger("groundAttackTicks");
		this.turretStreak = nbt.getInteger("turretStreak");
		this.turretStreakTicks = nbt.getInteger("turretStreakTicks");
		this.wightSpawnTicks = nbt.getInteger("wightSpawnTicks");
		this.teleportTicks = nbt.getInteger("teleportTicks");
		this.blockadeSpawnTicks = nbt.getInteger("blockadeSpawnTicks");
		this.deathTicks = nbt.getInteger("deathTicks");
		if(hasCustomName())
			bossInfo.setName(this.getDisplayName());
	}

	@Override
	public void setCustomNameTag(String name) {
		super.setCustomNameTag(name);
		bossInfo.setName(this.getDisplayName());
	}

	@Override
	public void addTrackingPlayer(EntityPlayerMP player) {
		super.addTrackingPlayer(player);
		bossInfo.addPlayer(player);
	}

	@Override
	public void removeTrackingPlayer(EntityPlayerMP player) {
		super.removeTrackingPlayer(player);
		bossInfo.removePlayer(player);
	}

	@Override
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		super.onInitialSpawn(difficulty, livingdata);
		this.anchor = this.getPositionVector();
		this.anchorRadius = 10.0D;
		return livingdata;
	}

	@Override
	public void knockBack(Entity entity, float dmg, double x, double z) { 
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();
		bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();

		if (world.isRemote && isEntityAlive()) {
			updateAmbientSounds();
		}
	}

	@SideOnly(Side.CLIENT)
	protected void updateAmbientSounds() {
		if (currentIdleSound != null) {
			if (!Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(currentIdleSound)) {
				currentIdleSound = null;
			}
		}
		if (currentIdleSound == null) {
			currentIdleSound = new FortressBossIdleSound(this);
			Minecraft.getMinecraft().getSoundHandler().playSound(currentIdleSound);
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!world.isRemote)
			dataManager.set(BOSSINFO_ID, Optional.of(bossInfo.getUniqueId()));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		EntityPlayer closestPlayer = this.world.getNearestAttackablePlayer(this, 32.0D, 16.0D);
		if(closestPlayer != null) {
			this.faceEntity(closestPlayer, 360.0F, 360.0F);
			this.setAttackTarget(closestPlayer);
		}

		this.lastShieldRotationYaw = this.shieldRotationYaw;
		this.lastShieldRotationPitch = this.shieldRotationPitch;
		this.lastShieldRotationRoll = this.shieldRotationRoll;

		this.lastShieldExplosion = this.shieldExplosion;
		if(this.isEntityAlive()) {
			this.shieldExplosion = 0.2F;
		} else {
			this.shieldExplosion = 0.2F + (this.deathTicks % 16) / 16.0F * (this.deathTicks / 60.0F);
		}

		float shieldRotation = 0.0F;
		if(this.world.isRemote) {
			shieldRotation = this.getDataManager().get(SHIELD_ROTATION);
			this.anchor = this.getDataManager().get(ANCHOR);
			this.anchorRadius = this.getDataManager().get(ANCHOR_RADIUS);
			this.unpackShieldData(this.getDataManager().get(SHIELD_STATE));
		} else {
			if(this.isEntityAlive()) {
				shieldRotation = this.ticksExisted;
				this.getDataManager().set(SHIELD_ROTATION, shieldRotation + 1);
			}
			this.getDataManager().set(ANCHOR, this.anchor);
			this.getDataManager().set(ANCHOR_RADIUS, (float) this.anchorRadius);
		}
		int activeShields = 0;
		for(int i = 0; i <= 19; i++) {
			if(this.isShieldActive(i))
				activeShields++;
		}
		if(this.isEntityAlive()) {
			this.shieldRotationYaw = shieldRotation * (1.0F + 6.0F / 20.0F * (20-activeShields));
			this.shieldRotationPitch = shieldRotation * (1.4F + 8.0F / 20.0F * (20-activeShields));
			this.shieldRotationRoll = shieldRotation * (1.6F + 10.0F / 20.0F * (20-activeShields));
		} else {
			this.shieldRotationYaw = shieldRotation * 2.0F;
			this.shieldRotationPitch = 0;
			this.shieldRotationRoll = 0;
		}

		if(this.isEntityAlive()) {
			if(!this.world.isRemote) {
				if(this.isFloating() && this.posY < this.anchor.y) {
					this.motionY = 0.1F;
				} else if(!this.isFloating()) {
					this.motionY += -0.1F;
					this.groundTicks++;
					if(this.groundTicks > 180 && this.groundAttackTicks > 20) {
						this.groundTicks = 0;
						this.setFloating(true);
					}
				}

				if(this.isFloating() && (this.getDistance(this.anchor.x, this.posY, this.anchor.z) > this.anchorRadius || Math.abs(this.posY - this.anchor.y) > this.anchorRadius)) {
					this.world.playSound(null, this.posX, this.posY, this.posZ, SoundRegistry.FORTRESS_BOSS_TELEPORT, SoundCategory.HOSTILE, 1.0F, 1.0F);
					this.setPosition(this.anchor.x, this.anchor.y, this.anchor.z);
				}

				//Teleport entities back
				List<EntityLivingBase> currentlyTrackedEntities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(this.anchorRadius*2, 512, this.anchorRadius*2));
				Iterator<EntityLivingBase> it = currentlyTrackedEntities.iterator();
				while(it.hasNext()) {
					EntityLivingBase living = it.next();
					if(living.getDistance(this.anchor.x, living.posY, this.anchor.z) > this.anchorRadius || Math.abs(living.posY - this.anchor.y) > this.anchorRadius)
						it.remove();
				}
				if(!this.trackedEntities.isEmpty()) {
					int tpy = this.world.getHeight(new BlockPos(this.anchor.x, 64, this.anchor.z)).getY();
					if(Math.abs(this.anchor.y - tpy) < this.anchorRadius) {
						for(EntityLivingBase living : this.trackedEntities) {
							if(living != null && living.isEntityAlive() && !currentlyTrackedEntities.contains(living) && (living instanceof EntityPlayer == false || !((EntityPlayer)living).capabilities.isCreativeMode)) {
								if(living instanceof EntityPlayerMP) {
									EntityPlayerMP player = (EntityPlayerMP) living;
									player.dismountRidingEntity();
									player.connection.setPlayerLocation(this.anchor.x, tpy, this.anchor.z, player.rotationYaw, player.rotationPitch);
								} else {
									living.dismountRidingEntity();
									living.setLocationAndAngles(this.anchor.x, tpy, this.anchor.z, living.rotationYaw, living.rotationPitch);
								}
								living.fallDistance = 0.0F;
								living.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 60, 2));
								currentlyTrackedEntities.add(living);
							}
						}
					}
				}
				this.trackedEntities.clear();
				this.trackedEntities.addAll(currentlyTrackedEntities);
			}

			AxisAlignedBB checkArea = this.getEntityBoundingBox().grow(32, 16, 32);
			List<EntityPlayer> players = this.world.getEntitiesWithinAABB(EntityPlayer.class, checkArea);
			if(!players.isEmpty()) {
				if(!this.world.isRemote) {
					this.getDataManager().set(SHIELD_STATE, this.packShieldData());

					if(this.isFloating() && this.posY >= this.anchor.y) {
						AxisAlignedBB checkAABB = this.getEntityBoundingBox().grow(16, 16, 16);
						List<EntityWight> wights = this.world.getEntitiesWithinAABB(EntityWight.class, checkAABB);
						List<EntityFortressBossSpawner> spawners = this.world.getEntitiesWithinAABB(EntityFortressBossSpawner.class, checkAABB);
						if(wights.isEmpty() && spawners.isEmpty()) {
							this.wightSpawnTicks--;
							if(this.wightSpawnTicks <= 0) {
								if(this.wightSpawnTicks == 0) {
									int spawnY = this.world.getHeight(this.getPosition()).getY();
									if(Math.abs(spawnY - this.posY) < this.anchorRadius) {
										EntityFortressBossSpawner spawner = new EntityFortressBossSpawner(this.world, this);
										spawner.setLocationAndAngles(MathHelper.floor(this.posX), spawnY, MathHelper.floor(this.posZ), 0, 0);
										spawner.setOwner(this);
										this.world.spawnEntity(spawner);
									}
								}
								this.wightSpawnTicks = 160 + this.world.rand.nextInt(200);
							}
						}


						List<EntityFortressBossBlockade> blockades = this.world.getEntitiesWithinAABB(EntityFortressBossBlockade.class, checkAABB);
						if(blockades.isEmpty()) {
							this.blockadeSpawnTicks--;
							if(this.blockadeSpawnTicks <= 0) {
								if(this.blockadeSpawnTicks == 0) {
									int spawnY = this.world.getHeight(this.getPosition()).getY();
									if(Math.abs(spawnY - this.posY) < this.anchorRadius) {
										EntityFortressBossBlockade blockade = new EntityFortressBossBlockade(this.world, this);
										blockade.setLocationAndAngles(this.posX, spawnY, this.posZ, 0, 0);
										blockade.setTriangleSize(1.2F + this.world.rand.nextFloat() * 1.6F);
										blockade.setOwner(this);
										blockade.setMaxDespawnTicks(400);
										this.world.spawnEntity(blockade);
									}
								}
								this.blockadeSpawnTicks = 190 + this.world.rand.nextInt(160);
							}
						}

						this.teleportTicks--;
						if(this.teleportTicks <= 0) {
							if(this.teleportTicks == 0) {
								this.world.playSound(null, this.posX, this.posY, this.posZ, SoundRegistry.FORTRESS_BOSS_TELEPORT, SoundCategory.HOSTILE, 1.0F, 1.0F);
								this.setLocationAndAngles(this.anchor.x + (this.world.rand.nextFloat()-0.5F)*2.0F*(this.anchorRadius-1), this.anchor.y, this.anchor.z + (this.world.rand.nextFloat()-0.5F)*2.0F*(this.anchorRadius-1), 0, 0);
							}
							this.teleportTicks = 140 + this.world.rand.nextInt(200);
						}

						if(this.turretStreak <= 0 && this.getHealth() < this.getMaxHealth() / 2.0F){
							if(this.turretStreak == 0) {
								this.turretStreakTicks++;
								int turretFrequency = MathHelper.floor(15.0D - 14.0D / 300.0D * this.turretStreakTicks);
								if(this.turretStreakTicks % turretFrequency == 0) {
									double angle = Math.PI * 2.0D / 150.0D * this.turretStreakTicks;
									for(int d = 0; d < 2; d++) {
										Vec3d dir = new Vec3d(Math.sin(angle) * (d == 0 ? 1 : -1), 0, Math.cos(angle) * (d == 0 ? 1 : -1));
										dir = dir.normalize().scale(this.anchorRadius);
										EntityFortressBossTurret turret = new EntityFortressBossTurret(this.world, this);
										turret.setLocationAndAngles(this.anchor.x + dir.x, this.anchor.y + dir.y, this.anchor.z + dir.z, 0, 0);
										turret.setAttackDelay(turretFrequency);
										this.world.spawnEntity(turret);
										this.world.playSound(null, this.posX, this.posY, this.posZ, SoundRegistry.FORTRESS_BOSS_SUMMON_PROJECTILES, SoundCategory.HOSTILE, 0.25F, 0.3F + 0.7F / 300.0F * this.turretStreakTicks);
									}
								}
								if(this.turretStreakTicks >= 300) {
									double angle = Math.PI * 2.0D / 32.0D;
									for(int i = 0; i < 32; i++) {
										Vec3d dir = new Vec3d(Math.sin(angle * i), 0, Math.cos(angle * i));
										dir = dir.normalize().scale(this.anchorRadius);
										EntityFortressBossTurret turret = new EntityFortressBossTurret(this.world, this);
										turret.setLocationAndAngles(this.anchor.x + dir.x, this.anchor.y + dir.y, this.anchor.z + dir.z, 0, 0);
										turret.setAttackDelay(5 + i / 3);
										this.world.spawnEntity(turret);
									}
									this.world.playSound(null, this.posX, this.posY, this.posZ, SoundRegistry.FORTRESS_BOSS_SUMMON_PROJECTILES, SoundCategory.HOSTILE, 1.0F, 1.0F);
									this.turretStreakTicks = 0;
									this.turretStreak = -1;
								}
							}
							if(this.turretStreak < 0) {
								this.turretStreak = 250 + this.world.rand.nextInt(200);
								this.turretStreakTicks = 0;
							}
						} else {
							if(this.turretTicks <= 0) {
								if(this.turretTicks == 0) {
									double angle = Math.PI * 2.0D / 9;
									for(int i = 0; i < 9; i++) {
										if(this.world.rand.nextInt(3) == 0) {
											Vec3d dir = new Vec3d(Math.sin(angle * i), 0, Math.cos(angle * i));
											dir = dir.normalize().scale(8.0D);
											EntityFortressBossTurret turret = new EntityFortressBossTurret(this.world, this);
											turret.setLocationAndAngles(this.anchor.x + dir.x, this.anchor.y + dir.y, this.anchor.z + dir.z, 0, 0);
											turret.setDeflectable(this.world.rand.nextInt(2) != 0);
											this.world.spawnEntity(turret);
										}
									}
									this.world.playSound(null, this.posX, this.posY, this.posZ, SoundRegistry.FORTRESS_BOSS_SUMMON_PROJECTILES, SoundCategory.HOSTILE, 1.0F, 1.0F);
								}
								this.turretTicks = 100 + this.world.rand.nextInt(200);
							} else {
								this.turretTicks--;
								if(this.turretStreak > 0)
									this.turretStreak--;
							}
						}
					}

					if(!this.isFloating() && this.onGround) {
						if(this.groundAttackTicks <= 0) {
							if(this.groundAttackTicks == 0) {
								double angle = Math.PI * 2.0D / 32;
								for(int i = 0; i < 32; i++) {
									Vec3d dir = new Vec3d(Math.sin(angle * i), 0, Math.cos(angle * i));
									dir = dir.normalize();
									float speed = 0.8F;
									EntityFortressBossProjectile bullet = new EntityFortressBossProjectile(this.world, this);
									bullet.setLocationAndAngles(this.posX, this.posY + 0.5D, this.posZ, 0, 0);
									bullet.shoot(dir.x, dir.y, dir.z, speed, 0.0F);
									this.world.spawnEntity(bullet);
								}
							}
							this.groundAttackTicks = 40 + this.world.rand.nextInt(80);
							this.getDataManager().set(GROUND_ATTACK_STATE, false);
							this.turretStreak = -1;
							this.turretTicks = -1;
						} else {
							this.groundAttackTicks--;
							if(this.groundAttackTicks <= 20) {
								this.getDataManager().set(GROUND_ATTACK_STATE, true);
							}
						}
					} else {
						this.getDataManager().set(GROUND_ATTACK_STATE, false);
					}
				} else {
					for(int i = 0; i <= 19; i++) {
						if(this.shieldAnimationTicks[i] == 0 && this.world.rand.nextInt(50) == 0)
							this.shieldAnimationTicks[i] = 40;
						if(this.shieldAnimationTicks[i] > 0) {
							this.shieldAnimationTicks[i]--;
							if(this.shieldAnimationTicks[i] == 20)
								this.shieldAnimationTicks[i] = 0;
						}
					}
					if(this.getDataManager().get(GROUND_ATTACK_STATE)) {
						if(this.groundAttackTicks < 20)
							this.groundAttackTicks++;
					} else {
						this.groundAttackTicks = 0;
					}
				}
			}
		} else {
			this.setFloating(true);
		}
	}

	@Override
	public void travel(float strafe, float up, float forward) {
		if(this.isFloating()) {
			if (this.isInWater()) {
				this.moveRelative(strafe, up, forward, 0.02F);
				this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
				this.motionX *= 0.800000011920929D;
				this.motionY *= 0.800000011920929D;
				this.motionZ *= 0.800000011920929D;
			} else {
				float friction = 0.91F;

				if (this.onGround) {
					friction = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.91F;
				}

				float groundFriction = 0.16277136F / (friction * friction * friction);
				this.moveRelative(strafe, up, forward, this.onGround ? 0.1F * groundFriction : 0.02F);
				friction = 0.91F;

				if (this.onGround) {
					friction = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.91F;
				}

				this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
				this.motionX *= (double)friction;
				this.motionY *= (double)friction;
				this.motionZ *= (double)friction;
			}

			this.prevLimbSwingAmount = this.limbSwingAmount;
			double dx = this.posX - this.prevPosX;
			double dz = this.posZ - this.prevPosZ;
			float distanceMoved = MathHelper.sqrt(dx * dx + dz * dz) * 4.0F;

			if (distanceMoved > 1.0F) {
				distanceMoved = 1.0F;
			}

			this.limbSwingAmount += (distanceMoved - this.limbSwingAmount) * 0.4F;
			this.limbSwing += this.limbSwingAmount;
		} else {
			this.setJumping(false);
			super.travel(0, 0, 0);
		}
	}

	@Override
	protected void onDeathUpdate() {
		bossInfo.setPercent(0);
		if(this.deathTicks == 0) {
			if(!this.world.isRemote) {
				this.world.playSound(null, this.anchor.x, this.anchor.y, this.anchor.z, SoundRegistry.FORTRESS_BOSS_TELEPORT, SoundCategory.HOSTILE, 1.0F, 1.0F);
				this.setPosition(this.anchor.x, this.anchor.y, this.anchor.z);
				List<Entity> trackedEntities = this.world.getEntitiesWithinAABB(EntityWight.class, this.getEntityBoundingBox().grow(this.anchorRadius*2, 512, this.anchorRadius*2));
				Iterator<Entity> it = trackedEntities.iterator();
				while(it.hasNext()) {
					Entity entity = it.next();
					if(entity.getDistance(this.anchor.x, entity.posY, this.anchor.z) > this.anchorRadius || Math.abs(entity.posY - this.anchor.y) > this.anchorRadius)
						it.remove();
				}
				for(Entity entity : trackedEntities) {
					if(entity instanceof EntityWight || entity instanceof EntityFortressBossSpawner || entity instanceof EntityFortressBossProjectile
							|| entity instanceof EntityFortressBossTurret || entity instanceof EntityFortressBossBlockade) {
						entity.setDead();
					}
				}
			}
		}

		++this.deathTicks;

		this.getDataManager().set(SHIELD_ROTATION, (float)((this.deathTicks/3.0F) * (this.deathTicks/3.0F)));
		for(int i = 0; i <= 19; i++) {
			this.activeShields[i] = i * (130.0F / 19.0F) > this.deathTicks;
		}
		this.getDataManager().set(SHIELD_STATE, this.packShieldData());
		if(!this.world.isRemote) {
			if (this.deathTicks > 100 && this.deathTicks % 5 == 0) {
				int xp = 800;
				while (xp > 0) {
					int dropXP = EntityXPOrb.getXPSplit(xp);
					xp -= dropXP;
					this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY + this.height / 2.0D, this.posZ, dropXP));
				}
			}

			if(this.deathTicks > 130) {
				int xp = 3000;
				while (xp > 0) {
					int dropXP = EntityXPOrb.getXPSplit(xp);
					xp -= dropXP;
					this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY + this.height / 2.0D, this.posZ, dropXP));
				}

				for(int c = 0; c < 4; c++) {
					double yawAngle = Math.PI * 2.0D / 6;
					for(int i = 0; i < 6; i++) {
						Vec3d dir = new Vec3d(Math.sin(yawAngle * i), (c-2)/4.0D*2.0D, Math.cos(yawAngle * i));
						dir = dir.normalize();
						float speed = 0.8F;
						EntityFortressBossProjectile bullet = new EntityFortressBossProjectile(this.world, this);
						bullet.setLocationAndAngles(this.posX, this.posY + 0.5D, this.posZ, 0, 0);
						bullet.shoot(dir.x, dir.y, dir.z, speed, 0.0F);
						this.world.spawnEntity(bullet);
					}
				}

				List<LocationStorage> locations = LocationStorage.getLocations(this.world, this.getPositionVector());
				for(LocationStorage location : locations) {
					if(location.getType() == EnumLocationType.WIGHT_TOWER) {
						if(location.getGuard() != null) {
							location.getGuard().clear(this.world);
							location.setDirty(true, true);
						}
					}
				}

				this.setDead();
			}
		}
	}

	@Nullable
	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.FORTRESS_BOSS;
	}

	@Override
	public boolean isNonBoss() {
		return false;
	}

	@Override
	public boolean isAIDisabled() {
		return false;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.FORTRESS_BOSS_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.FORTRESS_BOSS_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.FORTRESS_BOSS_DEATH;
	}

	@Override
	public void playLivingSound() {
		if(!this.world.isRemote && !this.isSilent()) {
			//TheBetweenlands.networkWrapper.sendToAllAround(new MessagePlayEntityIdle(this, SoundRegistry.FORTRESS_BOSS_LIVING, SoundCategory.HOSTILE, 1, 1), new TargetPoint(this.dimension, this.posX, this.posY, this.posZ, 16.0D));
		}
	}

	@Override
	public BLSoundEvent getMusicFile(EntityPlayer listener) {
		return SoundRegistry.FORTRESS_BOSS_LOOP;
	}

	@Override
	public double getMusicRange(EntityPlayer listener) {
		return 20.0D;
	}

	@Override
	public boolean isMusicActive(EntityPlayer listener) {
		return this.isEntityAlive();
	}

	@Override
	public UUID getBossInfoUuid() {
		return dataManager.get(BOSSINFO_ID).or(new UUID(0, 0));
	}
}
