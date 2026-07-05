package thebetweenlands.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.multipart.LivingHangerMultipart;

public class LivingHanger extends Monster implements BLEntity {
	private final int hangerSegments = 10;
	private final double segmentLength = 0.5D;
	private final double attackRange = 8.0D;
	private final double grabDistance = 1.25D; // Distance to grab will need to testy
	private Player targetPlayer = null;
	private AABB renderBoundingBox;

	public final LivingHangerMultipart[] parts;

	private final Vec3[] jointPositions;
	private final Vec3[] prevJointPositions;
	private Vec3 anchorPos;

	public LivingHanger(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.parts = new LivingHangerMultipart[hangerSegments];
		this.jointPositions = new Vec3[hangerSegments + 1];
		this.prevJointPositions = new Vec3[hangerSegments + 1];

		for (int i = 0; i < hangerSegments; i++)
			this.parts[i] = new LivingHangerMultipart(this, 0.5F, 0.5F);

		this.setId(ENTITY_COUNTER.getAndAdd(this.parts.length + 1) + 1);
		this.renderBoundingBox = this.getBoundingBox();
		
		Vec3 initialAnchor = this.position();
		for (int i = 0; i <= hangerSegments; i++) {
			this.jointPositions[i] = initialAnchor.subtract(0, i * segmentLength, 0);
			this.prevJointPositions[i] = initialAnchor.subtract(0, i * segmentLength, 0);
		}
	}

	public Vec3[] getJointPositions() {
		return this.jointPositions;
	}

	public Vec3[] getPrevJointPositions() {
		return this.prevJointPositions;
	}

	@Override
	public void setId(int id) {
		super.setId(id);
		for (int i = 0; i < this.parts.length; i++) {
			this.parts[i].setId(id + i + 1);
		}
	}

	@Override
	public PartEntity<?>[] getParts() {
		return this.parts;
	}

	@Override
	public boolean isMultipartEntity() {
		return true;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder); 
	}
	
	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MOVEMENT_SPEED, 0.08D)
			.add(Attributes.ATTACK_DAMAGE, 2.5D);
	}

	@Override
	public void tick() {
	    this.anchorPos = this.position();
	    this.setPos(this.anchorPos.x, this.anchorPos.y, this.anchorPos.z);
	    this.setDeltaMovement(Vec3.ZERO);

	    for (int i = 0; i <= hangerSegments; i++) {
	        if (this.jointPositions[i] != null) {
	            this.prevJointPositions[i] = this.jointPositions[i];
	        }
	    }

	    super.tick();

	    if (!this.level().isClientSide()) {
	    	//maybe move this block to an AI later?
	        grabPlayer();
	        checkTargeting();
	    } else {
	        if (this.targetPlayer == null || !this.targetPlayer.isAlive() || this.targetPlayer.isSpectator())
	            this.targetPlayer = this.level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), attackRange, true);
	    }

	    if (this.targetPlayer != null) {
	        double distanceSqr = this.distanceToSqr(this.targetPlayer);
	        if (distanceSqr > (attackRange * attackRange))
	            this.targetPlayer = null; 
	    }

	    setPiecePos();
	    movePiecePos();

	    this.renderBoundingBox = this.getBoundingBox();
 
	    for (LivingHangerMultipart part : this.parts)
	        this.renderBoundingBox = this.renderBoundingBox.minmax(part.getBoundingBox());
	}

	public void setPiecePos() {
	    if (this.jointPositions == null) return;

	    this.jointPositions[0] = this.anchorPos;
	    
	    for (int i = 1; i <= hangerSegments; i++) {
	        if (this.jointPositions[i] == null || Double.isNaN(this.jointPositions[i].x) || this.jointPositions[i].distanceToSqr(this.anchorPos) > 400.0)
	            this.jointPositions[i] = this.anchorPos.subtract(0, i * segmentLength, 0);

	        this.jointPositions[i] = this.jointPositions[i].add(0, -0.02, 0);

	        if (this.targetPlayer != null) {
	            Vec3 targetPoint = this.targetPlayer.position().add(0, this.targetPlayer.getBbHeight() / 2.0, 0);
	            double searchSpeed = 0.20;    // speed
	            double waveFrequency = 1.2;   // wibble frequency
	            double maxWaveRadius = 0.65;  // swing
	            double phaseShift = i * waveFrequency;
	            double angle = (this.tickCount * searchSpeed) - phaseShift;
	            double segmentScale = (double) i / hangerSegments;
	            double currentRadius = maxWaveRadius * segmentScale;
	            double offsetX = Math.cos(angle) * currentRadius;
	            double offsetZ = Math.sin(angle) * currentRadius;

	            // wibble
	            targetPoint = targetPoint.add(offsetX, 0.0, offsetZ);

	            Vec3 pullDirection = targetPoint.subtract(this.jointPositions[i]);

	            if (pullDirection.lengthSqr() > 0.001) {
	                double segmentInfluence = (double) i / hangerSegments; 
	                double pullStrength = 0.22 * segmentInfluence; 
	                this.jointPositions[i] = this.jointPositions[i].add(pullDirection.normalize().scale(pullStrength));
	            }
	        } else {
	            // Go sleepy bye-byes when player out of range
	            Vec3 restingPos = this.anchorPos.subtract(0, i * segmentLength, 0);
	            double returnSpeed = 0.15; 
	            this.jointPositions[i] = this.jointPositions[i].lerp(restingPos, returnSpeed);
	        }
	    }

	    for (int x = 0; x < 4; x++) {
	        for (int i = 1; i <= hangerSegments; i++) {
	            Vec3 partPrev = this.jointPositions[i - 1];
	            Vec3 part = this.jointPositions[i];

	            Vec3 delta = part.subtract(partPrev);
	            double currentDist = delta.length();

	            if (currentDist > segmentLength && currentDist > 0) {
	                Vec3 dir = delta.scale(1.0 / currentDist);
	                this.jointPositions[i] = partPrev.add(dir.scale(segmentLength));
	            }
	        }
	    }
	}

	public void movePiecePos() {
	    for (int i = 0; i < hangerSegments; i++) {
	        Vec3 topJoint = this.jointPositions[i];
	        Vec3 bottomJoint = this.jointPositions[i + 1];

	        if (topJoint == null || bottomJoint == null)
	        	continue;

	        Vec3 midPoint = topJoint.add(bottomJoint).scale(0.5);
	        LivingHangerMultipart part = this.parts[i];
	        double targetY = midPoint.y - (part.getBbHeight() / 2.0);

	        part.setPos(midPoint.x, targetY, midPoint.z); 
	        part.xo = midPoint.x;
	        part.yo = targetY;
	        part.zo = midPoint.z;
	        part.xOld = midPoint.x;
	        part.yOld = targetY;
	        part.zOld = midPoint.z;
	    }
	}

	@Override
	public AABB getBoundingBoxForCulling() {
		return this.renderBoundingBox;
	}

	private void grabPlayer() {
		if (this.targetPlayer == null || !this.targetPlayer.isAlive() || this.targetPlayer.isSpectator()) { // || // this.targetPlayer.isCreative())
			this.targetPlayer = this.level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), attackRange, true);
		}

		if (this.targetPlayer != null) {
			Vec3 tipPos = this.jointPositions[hangerSegments];
			Vec3 playerPos = this.targetPlayer.position().add(0, this.targetPlayer.getBbHeight() / 2.0, 0);
			double distanceToPlayer = tipPos.distanceTo(playerPos);

			if (distanceToPlayer <= grabDistance && !this.targetPlayer.isPassenger())
				this.targetPlayer.startRiding(this, true);
		}
	}

	private void checkTargeting() {
	    if (!this.hasPassenger(entity -> entity instanceof Player)) {
	        this.targetPlayer = null;
	    }
	}

	@Override
	protected void positionRider(Entity rider, MoveFunction moveFunction) {
		if (!this.hasPassenger(rider) || this.jointPositions == null || this.jointPositions[hangerSegments] == null)
		    return;

		Vec3 tipPosition = this.jointPositions[hangerSegments];
		double targetX = tipPosition.x;
		double targetY = tipPosition.y - (rider.getBbHeight() / 2.0);
		double targetZ = tipPosition.z;
		rider.setDeltaMovement(Vec3.ZERO);
		moveFunction.accept(rider, targetX, targetY, targetZ);
	}

	@Override
	public boolean canRiderInteract() {
		return true;
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	@Override
	public void remove(RemovalReason reason) {
		super.remove(reason);
		for (LivingHangerMultipart part : this.parts)
			part.setRemoved(reason);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.contains("anchor_x"))
			this.anchorPos = new Vec3(tag.getDouble("anchor_x"), tag.getDouble("anchor_y"), tag.getDouble("anchor_z"));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		if (this.anchorPos != null) {
			tag.putDouble("anchor_x", this.anchorPos.x);
			tag.putDouble("anchor_y", this.anchorPos.y);
			tag.putDouble("anchor_z", this.anchorPos.z);
		}
	}

	// can be set to any part(s) - dunno if we want this either
	public boolean hurtSegment(LivingHangerMultipart part, DamageSource source, float dmg) {
		this.damageHanger(source, dmg * 0.75F);
		return true;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.FELL_OUT_OF_WORLD))
			return this.damageHanger(source, amount);
		else if (source.is(DamageTypes.IN_WALL))
			return false;
		return this.damageHanger(source, amount);
	}

	protected boolean damageHanger(DamageSource source, float amount) {
		return super.hurt(source, amount);
	}
}
