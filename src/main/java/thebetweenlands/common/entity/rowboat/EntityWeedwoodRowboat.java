package thebetweenlands.common.entity.rowboat;

import java.util.EnumMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.network.serverbound.MessageRow;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.CubicBezier;
import thebetweenlands.util.Mat4d;
import thebetweenlands.util.MathUtils;
import thebetweenlands.util.Matrix;
import thebetweenlands.util.OpenSimplexNoise;
import thebetweenlands.util.Quat;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;

/*
 * Useful links:
 * https://en.wikipedia.org/wiki/Glossary_of_rowing_terms
 * https://en.wikipedia.org/wiki/Glossary_of_nautical_terms
 * https://en.wikipedia.org/wiki/List_of_ship_directions
 * https://en.wikipedia.org/wiki/Anatomy_of_a_rowing_stroke
 */
public class EntityWeedwoodRowboat extends EntityBoat implements IEntityAdditionalSpawnData {
    private static final CubicBezier DEVIATION_DRAG = new CubicBezier(0.9F, 0, 1, 0.6F);

    private static final CubicBezier SPEED_WAVE_POWER = new CubicBezier(0, 1, 0, 1);

    private static final EnumMap<ShipSide, DataParameter<Float>> ROW_PROGRESS = ShipSide.newEnumMap((Class<DataParameter<Float>>) (Class<?>) DataParameter.class, defineId(DataSerializers.FLOAT), defineId(DataSerializers.FLOAT));

    private static final DataParameter<Boolean> IS_TARRED = defineId(DataSerializers.BOOLEAN);

    private static final DataParameter<Boolean> HAS_LANTERN = defineId(DataSerializers.BOOLEAN);

    public static final float OAR_ROTATION_SCALE = -28;

    public static final float ROW_PROGRESS_PERIOD = MathUtils.TAU / Math.abs(OAR_ROTATION_SCALE);

    private static final float OAR_LENGTH = 40F / 16;

    private static final float BLADE_LENGTH = 12F / 16;

    private static final float LOOM_LENGTH = OAR_LENGTH - BLADE_LENGTH;

    private static final float RESTING_ROW_PROGRESS = ROW_PROGRESS_PERIOD * 0.05F;

    private static final int FORCE_SETTLE_DURATION = 10;

    private static final Quat UP = Quat.fromAxisAngle(0, 1, 0, 0);

    private static final OpenSimplexNoise WAVE_RNG = new OpenSimplexNoise(6354); // 1486858338

    private static final EnumMap<ShipSide, SoundEvent> SOUND_ROW = ShipSide.newEnumMap(SoundEvent.class, SoundRegistry.ROWBOAT_ROW_STARBOARD, SoundRegistry.ROWBOAT_ROW_PORT);

    private static final EnumMap<ShipSide, SoundEvent> SOUND_ROW_START = ShipSide.newEnumMap(SoundEvent.class, SoundRegistry.ROWBOAT_ROW_START_STARBOARD, SoundRegistry.ROWBOAT_ROW_START_PORT);

    private EnumMap<ShipSide, OarState> oars = ShipSide.newEnumMap(OarState.class, new OarState(), new OarState());

    public class OarState {
        float rowForce = 0.0F;
        int rowTime = FORCE_SETTLE_DURATION;
        float prevRowProgress = RESTING_ROW_PROGRESS;
        float rowProgress = RESTING_ROW_PROGRESS;
        boolean oarState = false;
        boolean oarInAir = false;
        float prevOarXWavePull;
        float prevOarZWavePull;
        float oarXWavePull;
        float oarZWavePull;
    }

    private float drag;

    private float submergeTicks;

    private int inWaterTicks;

    private float rotationalVelocity;

    private double serverX;

    private double serverY;

    private double serverZ;

    private float boatYaw;

    private float boatPitch;

    private int serverT;

    private boolean prevOarStrokeLeft;

    private boolean prevOarStrokeRight;

    private ShipSide synchronizer = ShipSide.STARBOARD;

    private Quat prevRotation = Quat.fromAxisAngle(0, 1, 0, 0);

    private Quat rotation = new Quat(prevRotation);

    private double prevWaveHeight;

    private double waveHeight;

    private float prevPilotPower;

    private float pilotPower;

    @Nullable
    private Lantern lantern;

    public EntityWeedwoodRowboat(World world) {
        super(world);
        setSize(2, 0.9F);
    }

    public EntityWeedwoodRowboat(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(ROW_PROGRESS.get(ShipSide.STARBOARD), RESTING_ROW_PROGRESS);
        dataManager.register(ROW_PROGRESS.get(ShipSide.PORT), RESTING_ROW_PROGRESS);
        dataManager.register(IS_TARRED, false);
        dataManager.register(HAS_LANTERN, false);
    }

    @Override
    public double getMountedYOffset() {
        return getWaveHeight(1);
    }

    @Override
    public Item getItemBoat() {
        return ItemRegistry.WEEDWOOD_ROWBOAT;
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return getItem();
    }

    public ItemStack getItem() {
        ItemStack stack = new ItemStack(getItemBoat());
        NBTTagCompound attrs = new NBTTagCompound();
        writeEntityToNBT(attrs);
        if (attrs.getSize() > 0) {
            stack.setTagInfo("attributes", attrs);   
        }
        return stack;
    }

    public void setIsTarred(boolean isTarred) {
        dataManager.set(IS_TARRED, isTarred);
    }

    public boolean isTarred() {
        return dataManager.get(IS_TARRED);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateInputs(boolean starboard, boolean port, boolean forward, boolean backward) {
        oars.get(ShipSide.STARBOARD).oarState = starboard;
        oars.get(ShipSide.PORT).oarState = port;
    }

    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int t, boolean teleport) {
        serverX = x;
        serverY = y;
        serverZ = z;
        boatYaw = yaw;
        boatPitch = pitch;
        serverT = 10;
    }

    @Override
    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
        posX = MathHelper.clamp(x, -3E7, 3E7);
        posY = y;
        posZ = MathHelper.clamp(z, -3E7, 3E7);
        // Keep prev for serverside onUpdate
        //if (world.isRemote) {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;   
        //}
        pitch = MathHelper.clamp(pitch, -90, 90);
        rotationYaw = yaw;
        rotationPitch = pitch;
        prevRotationYaw = rotationYaw;
        prevRotationPitch = rotationPitch;
        double delta = prevRotationYaw - yaw;
        if (delta < -180) {
            prevRotationYaw += 360;
        }
        if (delta >= 180) {
            prevRotationYaw -= 360;
        }
        setPosition(posX, posY, posZ);
        setRotation(yaw, pitch);
    }

    public void setOarStates(boolean starboard, boolean port, float progressStarboard, float progressPort) {
        setPaddleState(port, starboard);
        setRowProgress(ShipSide.STARBOARD, progressStarboard);
        setRowProgress(ShipSide.PORT, progressPort);
    }

    @Override
    protected boolean canFitPassenger(Entity passenger) {
        return getPassengers().isEmpty();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (isEntityInvulnerable(source)) {
            return false;
        }
        if (!world.isRemote && !isDead) {
            if (source instanceof EntityDamageSourceIndirect && source.getTrueSource() != null && isPassenger(source.getTrueSource())) {
                return false;
            }
            setForwardDirection(-getForwardDirection());
            setTimeSinceHit(10);
            setDamageTaken(getDamageTaken() + amount * 10);
            markVelocityChanged();
            boolean creative = source.getTrueSource() instanceof EntityPlayer && ((EntityPlayer) source.getTrueSource()).capabilities.isCreativeMode;
            if (creative || getDamageTaken() > 20) {
                if (!creative && world.getGameRules().getBoolean("doEntityDrops")) {
                    entityDropItem(getItem(), 0);
                    
                    if(this.hasLantern()) {
                    	this.entityDropItem(new ItemStack(ItemRegistry.WEEDWOOD_ROWBOAT_UPGRADE_LANTERN), 0);
                    }
                }
                setDead();
            }
        }
        return true;
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (HAS_LANTERN.equals(key)) {
            lantern = hasLantern() ? new Lantern(1.2F, 0.2F) : null;
        }
        super.notifyDataManagerChange(key);
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (EnumItemMisc.TAR_DRIP.isItemOf(stack) && !isTarred()) {
            if (!world.isRemote) {
                setIsTarred(true);
                stack.shrink(1);
                playSound(SoundRegistry.TAR_BEAST_STEP, 0.9F + rand.nextFloat() * 0.1F, 0.6F + rand.nextFloat() * 0.15F);
            }
            player.swingArm(hand);
        } else if (!stack.isEmpty() && stack.getItem() == ItemRegistry.WEEDWOOD_ROWBOAT_UPGRADE_LANTERN) {
            if (!world.isRemote) {
                stack.shrink(1);
                setHasLantern(true);
            }
            player.swingArm(hand);
        } else if (!world.isRemote && !player.isSneaking()) {
            player.startRiding(this);
        }
        return true;
    }

    private void setHasLantern(boolean lantern) {
        dataManager.set(HAS_LANTERN, lantern);
    }

    private boolean hasLantern() {
        return dataManager.get(HAS_LANTERN);
    }

    @Override
    protected void updateFallState(double y, boolean onGround, IBlockState state, BlockPos pos) {
        if (onGround) {
            if (fallDistance > 0) {
                state.getBlock().onFallenUpon(world, pos, this, fallDistance);
            }
            fallDistance = 0;
        } else if (y < 0) {
            fallDistance -= y;
        }
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (world.isRemote && getControllingPassenger() == passenger) {
            TheBetweenlands.proxy.onPilotEnterWeedwoodRowboat(passenger);
        }
    }

    @Override
    protected void removePassenger(Entity passenger) {
        if (world.isRemote && getControllingPassenger() == passenger) {
            TheBetweenlands.proxy.onPilotExitWeedwoodRowboat(this, passenger);
        }
        super.removePassenger(passenger);
    }

    @Override
    public void onUpdate() {
        double pow = 1 - SPEED_WAVE_POWER.eval(MathHelper.sqrt((posX - prevPosX) * (posX - prevPosX) + (posZ - prevPosZ) * (posZ - prevPosZ)));
        if (!world.isRemote) {
            setFlag(6, isGlowing());
        }
        onEntityUpdate();
        if (getTimeSinceHit() > 0) {
            setTimeSinceHit(getTimeSinceHit() - 1);
        }
        if (getDamageTaken() > 0) {
            setDamageTaken(getDamageTaken() - 1);
        }
        tickLerp();
        if (world.isRemote) {
            updateClientOarProgress(ShipSide.STARBOARD);
            updateClientOarProgress(ShipSide.PORT);
        }
        if (canPassengerSteer()) {
            if (getPassengers().size() == 0 || !(getPassengers().get(0) instanceof EntityPlayer)) {
                setPaddleState(false, false);
            }
            applyForces();
        }
        boolean left = getAppropriateOarState(ShipSide.STARBOARD);
        boolean right = getAppropriateOarState(ShipSide.PORT);
        updateRowForce(ShipSide.STARBOARD, left, prevOarStrokeLeft);
        updateRowForce(ShipSide.PORT, right, prevOarStrokeRight);
        updatePilotPull();
        prevOarStrokeLeft = left;
        prevOarStrokeRight = right;
        prevRotation = new Quat(rotation);
        prevWaveHeight = waveHeight;
        OarState oarStarboard = oars.get(ShipSide.STARBOARD);
        OarState oarPort = oars.get(ShipSide.PORT);
        oarStarboard.prevOarXWavePull = oarStarboard.oarXWavePull;
        oarPort.prevOarXWavePull = oarPort.oarXWavePull;
        oarStarboard.prevOarZWavePull = oarStarboard.oarZWavePull;
        oarPort.prevOarZWavePull = oarPort.oarZWavePull;
        if (inWater) {
            hitWaves(pow);
            inWaterTicks++;
        } else {
            rotation.interpolate(UP, 0.175);
            waveHeight -= waveHeight * 0.6F;
            if (waveHeight < 1e-3F) {
                waveHeight = 0;
            }
            inWaterTicks = 0;
        }
        if (canPassengerSteer()) {
            Vec3d motion = null;
            if (world.isRemote) {
                motion = applyRowForce();
            }
            rotationYaw += rotationalVelocity;
            if (world.isRemote) {
                if (motion != null) {
                    updateMotion(motion);
                }
                TheBetweenlands.networkWrapper.sendToServer(new MessageRow(oarStarboard.oarState, oarPort.oarState, oarStarboard.rowProgress, oarPort.rowProgress));
            }
            float rotationLeft = getAppropriateRowProgress(ShipSide.STARBOARD);
            float rotationRight = getAppropriateRowProgress(ShipSide.PORT);
            returnOarToResting(ShipSide.STARBOARD, rotationLeft);
            returnOarToResting(ShipSide.PORT, rotationRight);
            synchronizeOars();
            move(MoverType.SELF, motionX, motionY, motionZ);
        } else {
            motionX = motionY = motionZ = 0;
        }
        doBlockCollisions();
        if (inWater) {
            if (world.isRemote) {
                animateHullWaterInteraction();
                animateOars();   
            } else {
                createSoundFX();
            }
        }
        if (lantern != null && world.isRemote) {
            lantern.tick(getLanternPosition(), rotationYaw);
        }
        if (!world.isRemote) {
            world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().grow(0.2, 0.05, 0.2)).forEach(this::applyEntityCollision);
        }
        rotationYaw = MathHelper.wrapDegrees(rotationYaw);
        prevRotationYaw = MathUtils.adjustAngleForInterpolation(rotationYaw, prevRotationYaw);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0 || pass == 1;
    }

    @Nullable
    public Lantern getLantern() {
        return this.lantern;
    }

    private void hitWaves(double pow) {
        // TODO: custom smooth sync total world time
        double t = world.getTotalWorldTime() * 0.03;
        double roughness = 0.15 * pow * (inWaterTicks < 20 ? inWaterTicks / 20D : 1), scale = 0.5;
        double x = posX, z = posZ;
        Matrix mat = new Matrix();
        mat.rotate(rotation);
        mat.rotate(-rotationYaw * MathUtils.DEG_TO_RAD, 0, 1, 0);
        Vec3d posFront = mat.transform(new Vec3d(0, 0, 0.75));
        Vec3d posStarboard = mat.transform(new Vec3d(0.435, 0, -0.5));
        Vec3d posPort = mat.transform(new Vec3d(-0.435, 0, 0.5));
        double sx0 = posFront.x;
        double sz0 = posFront.z;
        double sx1 = posStarboard.x;
        double sz1 = posStarboard.z;
        double sx2 = posPort.x;
        double sz2 = posPort.z;
        double sy0 = WAVE_RNG.eval((x + sx0) * scale, t, (z + sz0) * scale) * roughness;
        double sy1 = WAVE_RNG.eval((x + sx1) * scale, t, (z + sz1) * scale) * roughness;
        double sy2 = WAVE_RNG.eval((x + sx2) * scale, t, (z + sz2) * scale) * roughness;
        Vec3d s0 = new Vec3d(sx0 * scale, sy0, sz0 * scale);
        Vec3d s1 = new Vec3d(sx1 * scale, sy1, sz1 * scale);
        Vec3d s2 = new Vec3d(sx2 * scale, sy2, sz2 * scale);
        Vec3d normal = s2.subtract(s1).crossProduct(s0.subtract(s1)).normalize();
        Vec3d yAxis = new Vec3d(0, 1, 0);
        double angle = Math.acos(Math.max(Math.min(normal.dotProduct(yAxis), 1), -1));
        Vec3d axis = normal.crossProduct(yAxis).normalize();
        Quat wave = Quat.fromAxisAngle(axis.x, axis.y, axis.z, -angle * 0.4);
        rotation.interpolate(wave, 0.2);
        Vec3d point = new Vec3d(0, roughness, 0);
        waveHeight = point.subtract(normal.scale(point.subtract(s0).dotProduct(normal))).y; 
        pullOarByWave(ShipSide.STARBOARD, normal);
        pullOarByWave(ShipSide.PORT, normal);
        if (!isTarred() && canPassengerSteer()) {
            double wx = normal.x;
            double wz = normal.z;
            double mag = Math.sqrt(wx * wx + wz * wz);
            double strength = mag / Math.min(((1 - normal.y) * 1.8 * roughness), 0.00125);
            if (strength > 0) {
                motionX += wx / strength;
                motionZ += wz / strength;
                double dir = Math.atan2(wz, wx) * MathUtils.RAD_TO_DEG;
                rotationalVelocity += Math.signum(MathUtils.modularDelta(rotationYaw, dir - 90, 360)) * Math.min((1 - normal.y) * 60 * roughness, roughness);   
            }
        }
    }

    private void updatePilotPull() {
        prevPilotPower = pilotPower;
        OarState oarStarboard = oars.get(ShipSide.STARBOARD);
        OarState oarPort = oars.get(ShipSide.PORT);
        int timeStarboard = oarStarboard.rowTime;
        int timePort = oarPort.rowTime;
        if (timeStarboard > 20 && timePort > 20 && getAppropriateOarState(ShipSide.STARBOARD) && getAppropriateOarState(ShipSide.PORT) && getAppropriateRowProgress(ShipSide.STARBOARD) == getAppropriateRowProgress(ShipSide.PORT)) {
            if (pilotPower < 1) {
                pilotPower += 0.2F;
                if (pilotPower > 1) {
                    pilotPower = 1;
                }
            }
        } else if (pilotPower > 0) {
            pilotPower -= 0.16F;
            if (pilotPower < 0) {
                pilotPower = 0;
            }
        }
    }

    private void pullOarByWave(ShipSide side, Vec3d normal) {
        Vec3d oar = getOarVector(side);
        Vec3d of = new Vec3d(oar.x, 0, oar.z);
        Vec3d nf = new Vec3d(normal.x, 0, normal.z);
        float angle = nf.length() < 1e-12 ? 0 : (float) Math.acos(Math.max(Math.min(nf.dotProduct(of) / (nf.length() * of.length()), 1), -1));
        float align = MathUtils.linearTransformf(angle, 0, MathUtils.PI, 1, 0);
        float yaw = (float) Math.atan2(-normal.z, -normal.x) - (rotationYaw - 90) * MathUtils.DEG_TO_RAD;
        float pitch = (float) Math.acos(Math.max(Math.min(normal.dotProduct(of), 1), -1));
        OarState oarSide = oars.get(side);
        float x = oarSide.oarXWavePull;
        oarSide.oarXWavePull = x + (MathHelper.clamp(yaw * align * (float) nf.length() * 2, -0.3F, 0.3F) - x) * 0.7F * (float) nf.length();
        float z = oarSide.oarZWavePull;
        oarSide.oarZWavePull = z + ((pitch - MathUtils.PI / 2) * (1 - align) * (getOarElevation(side) + 1) / 2 - z) * 0.4F;
    }

    private void updateClientOarProgress(ShipSide side) {
        OarState oarSide = oars.get(side);
        oarSide.prevRowProgress = oarSide.rowProgress;
        if (!isUserSteering()) {
            oarSide.rowProgress = getServerRowProgress(side);
        }
    }

    private void returnOarToResting(ShipSide side, float preApplyValue) {
        if (getRowForce(side) == 0) {
            float value = getAppropriateRowProgress(side);
            if (value != RESTING_ROW_PROGRESS) {
                float dist = RESTING_ROW_PROGRESS - value;
                if (dist < 0) {
                    dist += ROW_PROGRESS_PERIOD;
                }
                if (dist < 1e-4 && preApplyValue < RESTING_ROW_PROGRESS) {
                    value = RESTING_ROW_PROGRESS;
                } else {
                    float increment = dist * 0.085F;
                    if (increment > 0.005F) {
                        increment = 0.005F;
                    }
                    value += increment;
                }
            }
            setRowProgress(side, value);
        }
    }

    private void synchronizeOars() {
        if (getRowForce(synchronizer) == 0) {
            return;
        }
        ShipSide desynced = synchronizer.getOpposite();
        if (getRowForce(desynced) == 0) {
            return;
        }
        float target = getAppropriateRowProgress(synchronizer);
        float value = getAppropriateRowProgress(desynced);
        if (Math.abs(target - value) < 1e-6F) {
            return;
        }
        if (target < value) {
            synchronizer = desynced;
            return;
        }
        value += 0.0045F;
        if (value > target) {
            value = target;
        }
        setRowProgress(desynced, value);
    }

    @Override
    public void updatePassenger(Entity passenger) {
        if (isPassenger(passenger)) {
            Matrix mat = new Matrix();
            double pelvis = 0.75;
            mat.translate(0, pelvis - 1.5, 0);
            mat.rotate(rotation);
            mat.translate(0, 1.5, 0);
            mat.rotate(-rotationYaw * MathUtils.DEG_TO_RAD, 0, 1, 0);
            mat.translate(0, getMountedYOffset() - pelvis, 0.2625);
            Vec3d point = mat.transform(Vec3d.ZERO);
            passenger.setPosition(posX + point.x, posY + point.y, posZ + point.z);
            passenger.rotationYaw += rotationalVelocity;
            passenger.setRotationYawHead(passenger.getRotationYawHead() + rotationalVelocity);
            applyYawToEntity(passenger);
        }
    }

    @Override
    protected void applyYawToEntity(Entity entity) {
        entity.setRenderYawOffset(MathHelper.wrapDegrees(rotationYaw - 180));
        float delta = MathHelper.wrapDegrees(entity.rotationYaw - rotationYaw - 180);
        float clamped = MathHelper.clamp(delta, -135, 135);
        entity.prevRotationYaw += clamped - delta;
        entity.rotationYaw = entity.rotationYaw + clamped - delta;
        entity.setRotationYawHead(entity.rotationYaw);
    }

    private void tickLerp() {
        if (serverT <= 0 || isUserSteering()) {
            return;
        }
        double dx = posX - serverX, dy = posY - serverY, dz = posZ - serverZ;
        if (dx * dx + dy * dy + dz * dz > 100) {
            setPosition(serverX, serverY, serverZ);
            setRotation(boatYaw, boatPitch);
            serverT = 0;
            prevPosX = posX;
            prevPosY = posY;
            prevPosZ = posZ;
            prevRotationYaw = rotationYaw;
        } else {
            double x = posX + (serverX - posX) / serverT;
            double y = posY + (serverY - posY) / serverT;
            double z = posZ + (serverZ - posZ) / serverT;
            rotationYaw = rotationYaw + MathHelper.wrapDegrees(boatYaw - rotationYaw) / serverT;
            rotationPitch = rotationPitch + (boatPitch - rotationPitch) / serverT;
            serverT--;
            setPosition(x, y, z);
            setRotation(rotationYaw, rotationPitch);
        }
    }

    private void applyForces() {
        float buoyancy = 0;
        BlockPos pos = new BlockPos(this);
        IBlockState blockAt = world.getBlockState(pos);
        IBlockState blockAbove = world.getBlockState(pos.up());
        if (isWater(blockAt) && !isWater(blockAbove)) {
            float y = (float) pos.getY() + getLiquidHeight(blockAt, world, pos) + height;
            buoyancy = (y - (float) getEntityBoundingBox().minY - 0.55F) / height;
            drag = 0.9875F;
            submergeTicks = 0;
        } else if (isWater(blockAt) && isWater(blockAbove)) {
            buoyancy = 1.25F;
            drag = 0.975F;
            submergeTicks++;
        } else if (blockAt.getMaterial() == Material.AIR) {
            IBlockState blockBellow = world.getBlockState(pos.down());
            if (isWater(blockBellow)) {
                drag = 0.95F;
            } else if (blockBellow.getMaterial().blocksMovement()) {
                drag = 0.35F;
            } else {
                drag = 1;
            }
        }
        float motionRawAngle = (float) Math.atan2(motionZ, motionX);
        float motionAngle = MathHelper.wrapDegrees(motionRawAngle * MathUtils.RAD_TO_DEG + 180);
        float deviation = Math.abs(MathHelper.wrapDegrees(rotationYaw - 90 - motionAngle)) / 180;
        drag *= MathUtils.linearTransformf(DEVIATION_DRAG.eval(deviation), 0, 1, 1, 0.25F);
        motionY -= 0.04;
        motionX *= drag;
        motionZ *= drag;
        rotationalVelocity *= drag * 0.95F;
        if (buoyancy > 0) {
            motionY += buoyancy * 0.06;
            motionY *= 0.75;
        }
    }

    private Vec3d applyRowForce() {
        if (getControllingPassenger() == null || submergeTicks >= 25) {
            return null;
        }
        Vec3d rowForce = new Vec3d(1, 0, 0);
        Vec3d motion = new Vec3d(0, 0, 0);
        Vec3d rotation = new Vec3d(0, 0, 0);
        float leftOarForce = getRowForce(ShipSide.STARBOARD);
        float rightOarForce = getRowForce(ShipSide.PORT);
        float forceFactor = 0.35F;
        if (leftOarForce > 0) {
            updateRowProgress(ShipSide.STARBOARD, leftOarForce * getOarWaterResistance(ShipSide.STARBOARD));
            if (canOarsApplyForce()) {
                leftOarForce *= getOarPeriodicForceApplyment(ShipSide.STARBOARD);
                Vec3d leftLever = new Vec3d(0, 0, leftOarForce);
                motion = motion.add(0, 0, leftOarForce * forceFactor);
                Vec3d cross = rowForce.crossProduct(leftLever);
                rotation = rotation.add(cross.x, cross.y, cross.z);
            }
        }
        if (rightOarForce > 0) {
            updateRowProgress(ShipSide.PORT, rightOarForce * getOarWaterResistance(ShipSide.PORT));
            if (canOarsApplyForce()) {
                rightOarForce *= getOarPeriodicForceApplyment(ShipSide.PORT);
                Vec3d rightLever = new Vec3d(0, 0, rightOarForce);
                motion = motion.add(0, 0, rightOarForce * forceFactor);
                Vec3d cross = new Vec3d(-rowForce.x, -rowForce.y, -rowForce.z).crossProduct(rightLever);
                rotation = rotation.add(cross.x, cross.y, cross.z);
            }
        }
        Vec3d currentMotion = new Vec3d(motionX, 0, motionZ);
        if (currentMotion.length() < 0.1 && rotation.x * rotation.x + rotation.y * rotation.y + rotation.z + rotation.z > 0) {
            motion = motion.scale(0.35);
            rotation = rotation.scale(1.6);
        }
        rotationalVelocity += rotation.y * 10;
        return motion;
    }

    private void updateMotion(Vec3d motion) {
        motion = motion.rotateYaw(-rotationYaw * MathUtils.DEG_TO_RAD);
        motionX += motion.x;
        motionY += motion.y;
        motionZ += motion.z;
        setPaddleState(oars.get(ShipSide.STARBOARD).oarState, oars.get(ShipSide.PORT).oarState);
    }

    private float getOarPeriodicForceApplyment(ShipSide side) {
        return MathUtils.linearTransformf(getOarElevation(side), -1, 1, 0, 2);
    }

    private float getOarWaterResistance(ShipSide side) {
        float weight = MathUtils.linearTransformf(getOarElevation(side), -1, 1, 1, 0.25F);
        float velocity = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
        final float max = 0.5F;
        if (velocity > max) {
            velocity = max;
        }
        float t = velocity / max;
        return weight + (1 - weight) * t;
    }

    private float getOarElevation(ShipSide side) {
        return MathHelper.cos(getAppropriateRowProgress(side) * OAR_ROTATION_SCALE);
    }

    public boolean canOarsApplyForce() {
        return drag <= 1;
    }

    public float getRowForce(ShipSide side) {
        return 0.017F * oars.get(side).rowForce;
    }

    public void updateRowProgress(ShipSide side, float value) {
        setRowProgress(side, getAppropriateRowProgress(side) + value);
    }

    public float getPilotPower(float delta) {
        return prevPilotPower + (pilotPower - prevPilotPower) * delta;
    }

    public void updateRowForce(ShipSide side, boolean oarStroke, boolean prevOarStroke) {
        OarState oarSide = oars.get(side);
        float force = oarSide.rowForce;
        int time = oarSide.rowTime + 1;
        if (oarStroke || time < FORCE_SETTLE_DURATION) {
            if (!prevOarStroke && oarStroke && time >= FORCE_SETTLE_DURATION) {
                force = 1;
                time = 0;
            } else {
                force = Math.max(force - 0.05F, 0.55F);
            }
        } else {
            force = Math.max(force - 0.1F, 0);
        }
        oarSide.rowTime = time;
        oarSide.rowForce = force;
    }

    private void animateHullWaterInteraction() {
        double motionX = posX - prevPosX;
        double motionY = posY - prevPosY;
        double motionZ = posZ - prevPosZ;
        double velocity = Math.sqrt(motionX * motionX + motionZ * motionZ);
        if (velocity > 0.2625) {
            double vecX = Math.cos((rotationYaw - 90) * MathUtils.DEG_TO_RAD);
            double vecZ = Math.sin((rotationYaw - 90) * MathUtils.DEG_TO_RAD);
            for (int p = 0; p < 1 + velocity * 60; p++) {
                double near = rand.nextFloat() * 2 - 1;
                double far = (rand.nextInt(2) * 2 - 1) * 0.7;
                double splashX, splashZ;
                if (rand.nextBoolean()) {
                    splashX = posX - vecX * near * 0.8 + vecZ * far;
                    splashZ = posZ - vecZ * near * 0.8 - vecX * far;
                } else {
                    splashX = posX + vecX + vecZ * near * 0.7;
                    splashZ = posZ + vecZ - vecX * near * 0.7;
                }
                world.spawnParticle(EnumParticleTypes.WATER_SPLASH, splashX, Math.ceil(posY) - 0.125, splashZ, motionX, 0.01, motionZ);
            }
        }
    }

    private void animateOars() {
        double motionX = posX - prevPosX;
        double motionZ = posZ - prevPosZ;
        double motion = Math.sqrt(motionX * motionX + motionZ * motionZ);
        animateOar(ShipSide.STARBOARD, motion);
        animateOar(ShipSide.PORT, motion);
    }

    private void animateOar(ShipSide side, double motion) {
        Vec3d oarlock = getOarlockPosition(side);
        Vec3d oarVector = getOarVector(side);
        Vec3d blade = oarlock.add(oarVector.x * OAR_LENGTH, oarVector.y * OAR_LENGTH, oarVector.z * OAR_LENGTH);
        RayTraceResult raytrace = world.rayTraceBlocks(new Vec3d(oarlock.x, oarlock.y, oarlock.z), blade, true);
        boolean bladeInAir = true;
        float amountOfBladeInAir = BLADE_LENGTH;
        if (raytrace != null && raytrace.typeOfHit == RayTraceResult.Type.BLOCK) {
            if (motion > 0.175) {
                for (int p = 0; p < motion; p++) {
                    float x = MathUtils.linearTransformf(rand.nextFloat(), 0, 1, -0.2F, 0.2F);
                    float y = MathUtils.linearTransformf(rand.nextFloat(), 0, 1, -0.2F, 0.2F);
                    float z = MathUtils.linearTransformf(rand.nextFloat(), 0, 1, -0.2F, 0.2F);
                    world.spawnParticle(EnumParticleTypes.WATER_SPLASH, raytrace.hitVec.x + x, raytrace.hitVec.y + y, raytrace.hitVec.z + z, motionX, 0.01, motionZ);
                }
            }
            float amountInAir = (float) oarlock.distanceTo(raytrace.hitVec);
            if (amountInAir < LOOM_LENGTH) {
                bladeInAir = false;
            } else {
                amountOfBladeInAir = OAR_LENGTH - amountInAir;
            }
        }
        if (bladeInAir && rand.nextFloat() < 0.4F) {
            for (int p = 0, count = (int) (1 + motion * 3); p < count; p++) {
                float point = LOOM_LENGTH + rand.nextFloat() * amountOfBladeInAir;
                float x = (float) (oarVector.x * point + MathUtils.linearTransformf(rand.nextFloat(), 0, 1, -0.1F, 0.1F));
                float y = (float) (oarVector.y * point + MathUtils.linearTransformf(rand.nextFloat(), 0, 1, -0.4F, -0.2F));
                float z = (float) (oarVector.z * point + MathUtils.linearTransformf(rand.nextFloat(), 0, 1, -0.1F, 0.1F));
                world.spawnParticle(EnumParticleTypes.WATER_SPLASH, oarlock.x + x, oarlock.y + y, oarlock.z + z, 0, 1e-8, 0);
            }
        }
    }

    public Vec3d getLanternPosition() {
        Matrix mat = new Matrix();
        mat.translate(posX, posY + waveHeight, posZ);
        return this.getLocalLanternPosition(mat, 1);
    }

    public Vec3d getLocalLanternPosition(float t) {
        return getLocalLanternPosition(new Matrix(), t);
    }

    private Vec3d getLocalLanternPosition(Matrix mat, float t) {
        mat.rotate(getRotation(t));
        mat.rotate(-(prevRotationYaw + (rotationYaw - prevRotationYaw) * t) * MathUtils.DEG_TO_RAD, 0, 1, 0);
        float roll = getRoll(t);
        if (roll != 0) {
            mat.rotate(roll * MathUtils.DEG_TO_RAD, 0, 0, 1);
        }
        mat.scale(-1, -1, 1);
        return mat.transform(new Vec3d(0.0, -0.9922452370881644, 1.6755452654813303));
    }

    private Vec3d getOarlockPosition(ShipSide side) {
        float dir = side == ShipSide.PORT ? 1 : -1;
        Matrix mat = new Matrix();
        mat.translate(posX, posY + waveHeight, posZ);
        mat.rotate(rotation);
        mat.rotate(-rotationYaw * MathUtils.DEG_TO_RAD, 0, 1, 0);
        mat.translate(0.6 * dir, 1.15, -0.2);
        return mat.transform(Vec3d.ZERO);
    }

    private Vec3d getOarVector(ShipSide side) {
        float dir = side == ShipSide.PORT ? -1 : 1;
        float progress = getAppropriateRowProgress(side);
        float yaw = getOarRotationX(side, progress, 1) * dir - (rotationYaw - 90) * MathUtils.DEG_TO_RAD;
        float pitch = getOarRotationZ(side, progress, 1) - MathUtils.PI / 2;
        float cosYaw = MathHelper.cos(-yaw);
        float sinYaw = MathHelper.sin(-yaw);
        float cosPitch = MathHelper.cos(-pitch);
        Mat4d mat = new Mat4d();
        mat.asQuaternion(rotation);
        return mat.transform(new Vec3d(-sinYaw * cosPitch, MathHelper.sin(pitch), cosYaw * cosPitch));
    }

    private void createSoundFX() {
        createOarSoundFX(ShipSide.STARBOARD);
        createOarSoundFX(ShipSide.PORT);
    }

    private void createOarSoundFX(ShipSide side) {
        OarState oarSide = oars.get(side);
        Vec3d oarlock = getOarlockPosition(side);
        Vec3d oarVector = getOarVector(side);
        Vec3d blade = oarlock.add(oarVector.x * OAR_LENGTH, oarVector.y * OAR_LENGTH, oarVector.z * OAR_LENGTH);
        RayTraceResult raytrace = world.rayTraceBlocks(new Vec3d(oarlock.x, oarlock.y, oarlock.z), blade, true);
        boolean bladeInAir = true;
        if (raytrace != null && raytrace.typeOfHit == RayTraceResult.Type.BLOCK) {
            float amountInAir = (float) oarlock.distanceTo(raytrace.hitVec);
            if (amountInAir < LOOM_LENGTH) {
                bladeInAir = false;
                float force = oarSide.rowForce;
                boolean start = force == 1;
                if (oarSide.oarInAir || start) {
                    float volume = force * 0.8F + 0.2F;
                    SoundEvent sound = (start ? SOUND_ROW_START : SOUND_ROW).get(side);
                    world.playSound(null, raytrace.hitVec.x, raytrace.hitVec.y, raytrace.hitVec.z, sound, SoundCategory.NEUTRAL, volume, 0.8F + rand.nextFloat() * 0.3F);
                }
            }
        }
        oarSide.oarInAir = bladeInAir;
    }

    @Override
    public boolean isPushedByWater() {
        return canPassengerSteer();
    }

    @Override
    public boolean handleWaterMovement() {
        double mX = motionX, mZ = motionZ;
        if (world.handleMaterialAcceleration(getEntityBoundingBox(), Material.WATER, this)) {
            if (mX != motionX && mZ != motionZ && canPassengerSteer()) {
                double aX = motionX - mX, aZ = motionZ - mZ;
                double dir = Math.atan2(aZ, aX) * MathUtils.RAD_TO_DEG;
                double speed = Math.sqrt(motionX * motionX + motionZ * motionZ);
                rotationalVelocity += (MathHelper.clamp(MathUtils.modularDelta(rotationYaw, dir - 90, 360) * Math.min(speed * 1.1, 0.3), -12, 12) - rotationalVelocity) * 0.75;   
            }
            if (!inWater) {
                float volume = MathHelper.sqrt(motionX * motionX * 0.2 + motionY * motionY + motionZ * motionZ * 0.2) * 0.2F;
                if (volume > 0.15) {
                    if (volume > 1) {
                        volume = 1;
                    }
                    playSound(getSplashSound(), volume, 1 + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
                    float min = MathHelper.floor(getEntityBoundingBox().minY);
                    for (int i = 0; i < 1 + width * 20; i++) {
                        float x = (rand.nextFloat() * 2 - 1) * width;
                        float z = (rand.nextFloat() * 2 - 1) * width;
                        world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX + x, min + 1, posZ + z, motionX, motionY - rand.nextFloat() * 0.2F, motionZ);
                    }
                    for (int i = 0; i < 1 + width * 20; i++) {
                        float x = (rand.nextFloat() * 2 - 1) * width;
                        float z = (rand.nextFloat() * 2 - 1) * width;
                        world.spawnParticle(EnumParticleTypes.WATER_SPLASH, posX + x, min + 1, posZ + z, motionX, motionY, motionZ);
                    }
                }
            }
            fallDistance = 0;
            inWater = true;
            extinguish();
        } else {
            inWater = false;
        }
        return inWater;
    }

    public void setRowProgress(ShipSide side, float progress) {
        while (progress > ROW_PROGRESS_PERIOD) {
            progress -= ROW_PROGRESS_PERIOD;
        }
        while (progress < 0) {
            progress += ROW_PROGRESS_PERIOD;
        }
        if (isUserSteering()) {
             oars.get(side).rowProgress = progress;
        } else {
            dataManager.set(ROW_PROGRESS.get(side), progress);
        }
    }

    public float getRowProgress(ShipSide side, float delta) {
        OarState oarSide = oars.get(side);
        float prevProgress = oarSide.prevRowProgress;
        float progress = oarSide.rowProgress;
        return delta * (MathUtils.mod(progress - prevProgress + ROW_PROGRESS_PERIOD / 2, ROW_PROGRESS_PERIOD) - ROW_PROGRESS_PERIOD / 2) + prevProgress;
    }

    public float getServerRowProgress(ShipSide side) {
        return dataManager.get(ROW_PROGRESS.get(side));
    }

    public float getAppropriateRowProgress(ShipSide side) {
        return isUserSteering() ? oars.get(side).rowProgress : getServerRowProgress(side);
    }

    public boolean getAppropriateOarState(ShipSide side) {
        return isUserSteering() ? oars.get(side).oarState : getPaddleState(side.ordinal());
    }

    private boolean isUserSteering() {
        Entity entity = getControllingPassenger();
        return entity instanceof EntityPlayer && ((EntityPlayer) entity).isUser();
    }

    public float getOarRotationX(ShipSide side, float theta, float delta) {
        OarState oarSide = oars.get(side);
        return MathHelper.sin(theta * EntityWeedwoodRowboat.OAR_ROTATION_SCALE) * 0.6F + oarSide.prevOarXWavePull + (oarSide.oarXWavePull - oarSide.prevOarXWavePull) * delta;
    }

    public float getOarRotationY(ShipSide side, float theta) {
        float angle = MathUtils.linearTransformf(MathHelper.sin(theta * EntityWeedwoodRowboat.OAR_ROTATION_SCALE + MathUtils.PI / 2), -1, 1, MathUtils.PI / 2, 0);
        if (side == ShipSide.PORT) {
            angle = MathUtils.PI - angle;
        }
        return angle;
    }

    public float getOarRotationZ(ShipSide side, float theta, float delta) {
        OarState oarSide = oars.get(side);
        float angle = MathHelper.cos(theta * EntityWeedwoodRowboat.OAR_ROTATION_SCALE) * 0.45F - MathUtils.PI / 2.5F + oarSide.prevOarZWavePull + (oarSide.oarZWavePull - oarSide.prevOarZWavePull) * delta;
        if (side == ShipSide.PORT) {
            angle = -angle;
        }
        return angle;
    }

    public Quat getRotation(float delta) {
        Quat rot = new Quat(prevRotation);
        rot.interpolate(rotation, delta);
        return rot;
    }

    public float getRoll(float delta) {
        float timeSinceHit = getTimeSinceHit() - delta;
        float damageTaken = Math.max(getDamageTaken() - delta, 0.0F);
        if (timeSinceHit > 0) {
            return MathHelper.sin(timeSinceHit) * timeSinceHit * damageTaken / 10 * getForwardDirection();
        }
        return 0;
    }

    public double getWaveHeight(float delta) {
        return delta == 1 ? waveHeight : prevWaveHeight + (waveHeight - prevWaveHeight) * delta;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setBoolean("isTarred", isTarred());
        compound.setBoolean("hasLantern", hasLantern());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        setIsTarred(compound.getBoolean("isTarred"));
        setHasLantern(compound.getBoolean("hasLantern"));
    }

    @Override
    public void writeSpawnData(ByteBuf buf) {}

    @Override
    public void readSpawnData(ByteBuf buf) {
        prevRotationYaw = rotationYaw;
    }

    public static boolean isTarred(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null) {
            return tag.getCompoundTag("attributes").getBoolean("isTarred");
        }
        return false;
    }

    private static <T> DataParameter<T> defineId(DataSerializer<T> serializer) {
        return EntityDataManager.createKey(EntityWeedwoodRowboat.class, serializer);
    }

    private static boolean isWater(IBlockState state) {
        return state.getMaterial() == Material.WATER;
    }

    private static float getLiquidHeight(IBlockState state, World world, BlockPos pos) {
        Block block = state.getBlock();
        if (block instanceof IFluidBlock) {
            return ((IFluidBlock) block).getFilledPercentage(world, pos);
        }
        if (block instanceof BlockLiquid) {
            return BlockLiquid.getBlockLiquidHeight(state, world, pos);
        }
        return 1;
    }

    // Inherited methods not needed

    @Override
    public float getWaterLevelAbove() {
        return 0.0F;
    }

    @Override
    public float getBoatGlide() {
        return 0.0F;
    }

    @Override
    public void setBoatType(Type boatType) {
    }

    @Override
    public Type getBoatType() {
        return Type.OAK;
    }

    @Override
    public float getRowingTime(int oar, float limbSwing) {
        return 0.0F;
    }

    @SubscribeEvent
    public static void onLivingAttacked(LivingAttackEvent event) {
        Entity ridingEntity = event.getEntityLiving().getRidingEntity();
        DamageSource source = event.getSource();
    	if (ridingEntity instanceof EntityWeedwoodRowboat && source.getTrueSource() != null) {
            Vec3d location = source.getDamageLocation();
            Entity attacker = source.getImmediateSource();
            if (location != null && location.y + (attacker != null ? attacker.getEyeHeight() : 0) < ridingEntity.posY + ridingEntity.height / 2) {
                //Cancel any damage dealt from below the boat
                event.setCanceled(true);
            }
        }
    }
}
