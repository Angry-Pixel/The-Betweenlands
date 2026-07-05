package thebetweenlands.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
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
import thebetweenlands.api.entity.NonDismountable;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.multipart.LivingHangerMultipart;
import thebetweenlands.common.registries.SoundRegistry;

public class LivingHanger extends Monster implements BLEntity, NonDismountable {
	private final int hangerSegments = 10;
	private final double segmentLength = 0.5D;
	private final double attackRange = 8.0D;
	private final double grabDistance = 1.25D; // Distance to grab will need to testy
	private Player targetPlayer = null;
	private AABB renderBoundingBox;
	
	private static final EntityDataAccessor<Integer> CURRENT_SEGMENT = SynchedEntityData.defineId(LivingHanger.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> MOVEMENT_TICKS = SynchedEntityData.defineId(LivingHanger.class, EntityDataSerializers.INT);
	private final int TICKS_RIDING_PER_SEGMENT = 10; // can be tweaked but half a second seems good atm
	public final LivingHangerMultipart[] parts;
	private final Vec3[] jointPositions;
	private final Vec3[] prevJointPositions;
	private Vec3 anchorPos;

	public LivingHanger(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		parts = new LivingHangerMultipart[hangerSegments];
		jointPositions = new Vec3[hangerSegments + 1];
		prevJointPositions = new Vec3[hangerSegments + 1];

		for (int i = 0; i < hangerSegments; i++)
			parts[i] = new LivingHangerMultipart(this, 0.5F, 0.5F);

		setId(ENTITY_COUNTER.getAndAdd(parts.length + 1) + 1);
		renderBoundingBox = getBoundingBox();
		
		Vec3 initialAnchor = position();
		for (int i = 0; i <= hangerSegments; i++) {
			jointPositions[i] = initialAnchor.subtract(0, i * segmentLength, 0);
			prevJointPositions[i] = initialAnchor.subtract(0, i * segmentLength, 0);
		}
	}

	public Vec3[] getJointPositions() {
		return jointPositions;
	}

	public Vec3[] getPrevJointPositions() {
		return prevJointPositions;
	}

	@Override
	public void setId(int id) {
		super.setId(id);
		for (int i = 0; i < parts.length; i++)
			parts[i].setId(id + i + 1);
	}

	@Override
	public PartEntity<?>[] getParts() {
		return parts;
	}

	@Override
	public boolean isMultipartEntity() {
		return true;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
	    super.defineSynchedData(builder);
	    builder.define(CURRENT_SEGMENT, 9);
	    builder.define(MOVEMENT_TICKS, 0);
	}
	
	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MOVEMENT_SPEED, 0.08D)
			.add(Attributes.ATTACK_DAMAGE, 2.5D);
	}

	@Override
	public void tick() {
	    anchorPos = position();
	    setPos(anchorPos.x, anchorPos.y, anchorPos.z);
	    setDeltaMovement(Vec3.ZERO);

	    for (int i = 0; i <= hangerSegments; i++) {
	        if (jointPositions[i] != null) {
	            prevJointPositions[i] = jointPositions[i];
	        }
	    }

	    super.tick();

	    if (!level().isClientSide()) {
	    	//maybe move this block to an AI later?
	        grabPlayer();
	        checkTargeting();
	    } else {
	        if (targetPlayer == null || !targetPlayer.isAlive() || targetPlayer.isSpectator())
	            targetPlayer = level().getNearestPlayer(getX(), getY(), getZ(), attackRange, true);
	    }

	    if (targetPlayer != null) {
	        double distanceSqr = distanceToSqr(targetPlayer);
	        if (distanceSqr > (attackRange * attackRange))
	            targetPlayer = null; 
	    }

	    setPiecePos();
	    movePiecePos();

	    renderBoundingBox = getBoundingBox();
 
	    for (LivingHangerMultipart part : parts)
	        renderBoundingBox = renderBoundingBox.minmax(part.getBoundingBox());
	    
	    if (!level().isClientSide() && hasPassenger(passenger -> passenger instanceof Player)) {
	        int currentSegment = getSegmentRiderAttachedTo();
	        int currentTicks = getSegmentRiderAttachedMoveTicks();

	        if (currentSegment > 2) {
	            currentTicks++;

	            if (currentTicks >= TICKS_RIDING_PER_SEGMENT) {
	                currentTicks = 0;
	                currentSegment--; 
	                playSound(SoundRegistry.GECKO_HIDE.get(), 0.75F, getRandom().nextFloat() * 0.3F - 0.9F);
	            }

	            setSegmentRiderAttachedTo(currentSegment);
	            setSegmentRiderAttachedMoveTicks(currentTicks);
	        } else if (currentSegment == 2)
	            suffocatePLayer();
	    }
	}
	
	private void suffocatePLayer() {
	    if (level().isClientSide())
	    	return;

	    Entity passenger = getFirstPassenger();
	    if (passenger instanceof Player player) {
	        if (!player.isAlive()) {
	        	player.stopRiding();
	        	targetPlayer = null;
	        	resetSegmentData();
	            return;
	        }
	        //temp - I don't want the vanilla hurt sounds and we need a choking sound from Compost.
	        if (tickCount % 20 == 0) {
	        	player.hurt(damageSources().inWall(), 2.0F);
	            level().playSound(null, player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), SoundRegistry.BL_FISHING_ROD_CREAK.get(), SoundSource.PLAYERS, 0.75F, 0.25F);
	            playSound(SoundRegistry.GECKO_HIDE.get(), 0.25F, getRandom().nextFloat() * 0.3F - 0.9F);
	        }
	    }
	}

	public void setPiecePos() {
	    if (jointPositions == null)
	    	return;

	    jointPositions[0] = anchorPos;

	    for (int i = 1; i <= hangerSegments; i++) {
	        if (jointPositions[i] == null || Double.isNaN(jointPositions[i].x) || jointPositions[i].distanceToSqr(anchorPos) > 400.0)
	            jointPositions[i] = anchorPos.subtract(0, i * segmentLength, 0);

	        jointPositions[i] = jointPositions[i].add(0, -0.02, 0);

	        if (targetPlayer != null) {
	            Vec3 targetPoint = targetPlayer.position().add(0, targetPlayer.getBbHeight() / 2.0, 0);
	            double searchSpeed = 0.20;    // speed
	            double waveFrequency = 1.2;   // wibble frequency
	            double maxWaveRadius = 0.65;  // swing
	            double phaseShift = i * waveFrequency;
	            double angle = (tickCount * searchSpeed) - phaseShift;
	            double segmentScale = (double) i / hangerSegments;
	            double currentRadius = maxWaveRadius * segmentScale;
	            double offsetX = Math.cos(angle) * currentRadius;
	            double offsetZ = Math.sin(angle) * currentRadius;

	            // wibble
	            targetPoint = targetPoint.add(offsetX, 0.0, offsetZ);

	            Vec3 pullDirection = targetPoint.subtract(jointPositions[i]);

	            if (pullDirection.lengthSqr() > 0.001) {
	                double segmentInfluence = (double) i / hangerSegments; 
	                double pullStrength = 0.22 * segmentInfluence; 
	                jointPositions[i] = jointPositions[i].add(pullDirection.normalize().scale(pullStrength));
	            }
	        } else {
	            // Go sleepy bye-byes when player out of range
	            Vec3 restingPos = anchorPos.subtract(0, i * segmentLength, 0);
	            double returnSpeed = 0.15; 
	            jointPositions[i] = jointPositions[i].lerp(restingPos, returnSpeed);
	        }
	    }

	    for (int x = 0; x < 4; x++) {
	        for (int i = 1; i <= hangerSegments; i++) {
	            Vec3 partPrev = jointPositions[i - 1];
	            Vec3 part = jointPositions[i];

	            Vec3 delta = part.subtract(partPrev);
	            double currentDist = delta.length();

	            if (currentDist > segmentLength && currentDist > 0) {
	                Vec3 dir = delta.scale(1.0 / currentDist);
	                jointPositions[i] = partPrev.add(dir.scale(segmentLength));
	            }
	        }
	    }
	}

	public void movePiecePos() {
	    for (int i = 0; i < hangerSegments; i++) {
	        Vec3 topJoint = jointPositions[i];
	        Vec3 bottomJoint = jointPositions[i + 1];

	        if (topJoint == null || bottomJoint == null)
	        	continue;

	        Vec3 midPoint = topJoint.add(bottomJoint).scale(0.5);
	        LivingHangerMultipart part = parts[i];
	        double targetY = midPoint.y - (part.getBbHeight() / 2.0);

	        part.xo = part.getX();
	        part.yo = part.getY();
	        part.zo = part.getZ();
	        part.xOld = part.getX();
	        part.yOld = part.getY();
	        part.zOld = part.getZ();

	        part.setPos(midPoint.x, targetY, midPoint.z); 
	    }
	}

	@Override
	public AABB getBoundingBoxForCulling() {
		return renderBoundingBox;
	}

	private void grabPlayer() {
		if (targetPlayer == null || !targetPlayer.isAlive() || targetPlayer.isSpectator()) { // || // targetPlayer.isCreative())
			targetPlayer = level().getNearestPlayer(getX(), getY(), getZ(), attackRange, true);
		}

		if (targetPlayer != null) {
			Vec3 tipPos = jointPositions[hangerSegments];
			Vec3 playerPos = targetPlayer.position().add(0, targetPlayer.getBbHeight() / 2.0, 0);
			double distanceToPlayer = tipPos.distanceTo(playerPos);

			if (distanceToPlayer <= grabDistance && !targetPlayer.isPassenger())
				targetPlayer.startRiding(this, true);
		}
	}
	
	public void resetSegmentData() {
		if (getSegmentRiderAttachedTo() != 9)
			setSegmentRiderAttachedTo(9);
		if (getSegmentRiderAttachedMoveTicks() != 0)
			setSegmentRiderAttachedMoveTicks(0);
	}

	private void checkTargeting() {
	    if (!hasPassenger(entity -> entity instanceof Player)) {
	        targetPlayer = null;
	        resetSegmentData();
	    }
	}

	@Override
	protected void addPassenger(Entity passenger) {
	    super.addPassenger(passenger);
	    if (!level().isClientSide() && passenger instanceof Player)
	        resetSegmentData();;
	}

	@Override
	protected void removePassenger(Entity passenger) {
	    super.removePassenger(passenger);
	    if (!level().isClientSide() && passenger instanceof Player)
	    	resetSegmentData();
	}

	@Override
	protected void positionRider(Entity rider, MoveFunction moveFunction) {
	    if (!hasPassenger(rider) || jointPositions == null)
	    	return;

	    int currentIdx = getSegmentRiderAttachedTo();
	    int currentTicks = getSegmentRiderAttachedMoveTicks();
	    int nextIdx = Math.max(2, currentIdx - 1);
	    if (currentIdx >= jointPositions.length || nextIdx >= jointPositions.length)
	    	return;
	    Vec3 currentPos = jointPositions[currentIdx];
	    Vec3 nextPos = jointPositions[nextIdx];

	    if (currentPos == null || nextPos == null)
	    	return;

	    double progressRatio = (double) currentTicks / (double) TICKS_RIDING_PER_SEGMENT;
	    double interpolatedX = net.minecraft.util.Mth.lerp(progressRatio, currentPos.x, nextPos.x);
	    double interpolatedY = net.minecraft.util.Mth.lerp(progressRatio, currentPos.y, nextPos.y);
	    double interpolatedZ = net.minecraft.util.Mth.lerp(progressRatio, currentPos.z, nextPos.z);
	    double targetY = interpolatedY - (rider.getBbHeight() / 2.0);

	    rider.setDeltaMovement(Vec3.ZERO);
	    moveFunction.accept(rider, interpolatedX, targetY, interpolatedZ);
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
		for (LivingHangerMultipart part : parts)
			part.setRemoved(reason);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.contains("anchor_x"))
			anchorPos = new Vec3(tag.getDouble("anchor_x"), tag.getDouble("anchor_y"), tag.getDouble("anchor_z"));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		if (anchorPos != null) {
			tag.putDouble("anchor_x", anchorPos.x);
			tag.putDouble("anchor_y", anchorPos.y);
			tag.putDouble("anchor_z", anchorPos.z);
		}
	}

	// can be set to any part(s) - dunno if we want this either
	public boolean hurtSegment(LivingHangerMultipart part, DamageSource source, float dmg) {
		damageHanger(source, dmg * 0.75F);
		return true;
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.FELL_OUT_OF_WORLD))
			return damageHanger(source, amount);
		else if (source.is(DamageTypes.IN_WALL))
			return false;
		return damageHanger(source, amount);
	}

	protected boolean damageHanger(DamageSource source, float amount) {
		return super.hurt(source, amount);
	}
	
	public void setSegmentRiderAttachedTo(int segment) {
		getEntityData().set(CURRENT_SEGMENT, segment);
	}

	public int getSegmentRiderAttachedTo() {
		return getEntityData().get(CURRENT_SEGMENT);
	}

	public void setSegmentRiderAttachedMoveTicks(int count) {
		getEntityData().set(MOVEMENT_TICKS, count);
	}

	public int getSegmentRiderAttachedMoveTicks() {
		return getEntityData().get(MOVEMENT_TICKS);
	}

	@Override
	public boolean isUnmountBlocked(Player rider) {
		return !level().isClientSide() && !rider.isCreative();
	}
}
