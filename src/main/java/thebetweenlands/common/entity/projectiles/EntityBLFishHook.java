package thebetweenlands.common.entity.projectiles;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.item.tools.ItemBLFishingRod;

public class EntityBLFishHook extends Entity {
	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(EntityBLFishHook.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private static final DataParameter<Integer> DATA_HOOKED_ENTITY = EntityDataManager.<Integer>createKey(EntityBLFishHook.class, DataSerializers.VARINT);
    private boolean inGround;
    private int ticksInGround;
    private int ticksInAir;
    private int ticksCatchable;
    private int ticksCaughtDelay;
    private int ticksCatchableDelay;
    private float fishApproachAngle;
    public Entity caughtEntity;
    private EntityBLFishHook.State currentState = State.FLYING;
    private int luck;
    private int lureSpeed;

    static enum State {
        FLYING,
        HOOKED_IN_ENTITY,
        BOBBING;
    }

    @SideOnly(Side.CLIENT)
    public EntityBLFishHook(World world, EntityPlayer player, double x, double y, double z) {
        super(world);
        init(player);
        setPosition(x, y, z);
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
    }

    public EntityBLFishHook(World world, EntityPlayer player) {
        super(world);
        init(player);
        shoot();
        System.out.println("Angler: " + getAngler().getName());
    }
    
    public EntityBLFishHook(World world) {
        super(world);
        setSize(0.25F, 0.25F);
        ignoreFrustumCheck = true;
    }

    private void init(EntityPlayer player) {
        setSize(0.25F, 0.25F);
        ignoreFrustumCheck = true;
        setAnglerId(player.getUniqueID());
     //   getAngler().fishEntity = null;
    }

    @Override
    protected void entityInit() {
    	dataManager.register(DATA_HOOKED_ENTITY, 0);
        dataManager.register(OWNER_UNIQUE_ID, Optional.<UUID>absent());
    }

	@Nullable
    public EntityPlayer getAngler() {
		try {
			UUID uuid = getAnglerId();
			return uuid == null ? null : getEntityWorld().getPlayerEntityByUUID(uuid);
		} catch (IllegalArgumentException e) {
			return null;
		}
    }
    
    @Nullable
	public UUID getAnglerId() {
		return dataManager.get(OWNER_UNIQUE_ID).orNull();
	}

	public void setAnglerId(@Nullable UUID uuid) {
		dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(uuid));
	}

	public boolean isOwner(EntityPlayer playerIn) {
		return playerIn == getAngler();
	}

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (DATA_HOOKED_ENTITY.equals(key)) {
            int entityId = dataManager.get(DATA_HOOKED_ENTITY);
            caughtEntity = entityId > 0 ? world.getEntityByID(entityId - 1) : null;
        }
        super.notifyDataManagerChange(key);
    }

    private void shoot()
    {
        float f = getAngler().prevRotationPitch + (getAngler().rotationPitch - getAngler().prevRotationPitch);
        float f1 = getAngler().prevRotationYaw + (getAngler().rotationYaw - getAngler().prevRotationYaw);
        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        double d0 = getAngler().prevPosX + (getAngler().posX - getAngler().prevPosX) - (double)f3 * 0.3D;
        double d1 = getAngler().prevPosY + (getAngler().posY - getAngler().prevPosY) + (double)getAngler().getEyeHeight();
        double d2 = getAngler().prevPosZ + (getAngler().posZ - getAngler().prevPosZ) - (double)f2 * 0.3D;
        setLocationAndAngles(d0, d1, d2, f1, f);
        motionX = (double)(-f3);
        motionY = (double)MathHelper.clamp(-(f5 / f4), -5.0F, 5.0F);
        motionZ = (double)(-f2);
        float f6 = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX *= 0.6D / (double)f6 + 0.5D + rand.nextGaussian() * 0.0045D;
        motionY *= 0.6D / (double)f6 + 0.5D + rand.nextGaussian() * 0.0045D;
        motionZ *= 0.6D / (double)f6 + 0.5D + rand.nextGaussian() * 0.0045D;
        float f7 = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
        rotationYaw = (float)(MathHelper.atan2(motionX, motionZ) * (180D / Math.PI));
        rotationPitch = (float)(MathHelper.atan2(motionY, (double)f7) * (180D / Math.PI));
        prevRotationYaw = rotationYaw;
        prevRotationPitch = rotationPitch;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double distance)
    {
        double d0 = 64.0D;
        return distance < 4096.0D;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (getAngler() == null)
        {
            setDead();
        }
        else if (world.isRemote || !shouldStopFishing())
        {
            if (inGround)
            {
                ++ticksInGround;

                if (ticksInGround >= 1200)
                {
                    setDead();
                    return;
                }
            }

            float f = 0.0F;
            BlockPos blockpos = new BlockPos(this);
            IBlockState iblockstate = world.getBlockState(blockpos);

            if (iblockstate.getMaterial() == Material.WATER)
            {
                f = BlockLiquid.getBlockLiquidHeight(iblockstate, world, blockpos);
            }

            if (currentState == EntityBLFishHook.State.FLYING)
            {
                if (caughtEntity != null)
                {
                    motionX = 0.0D;
                    motionY = 0.0D;
                    motionZ = 0.0D;
                    currentState = EntityBLFishHook.State.HOOKED_IN_ENTITY;
                    return;
                }

                if (f > 0.0F)
                {
                    motionX *= 0.3D;
                    motionY *= 0.2D;
                    motionZ *= 0.3D;
                    currentState = EntityBLFishHook.State.BOBBING;
                    return;
                }

                if (!world.isRemote)
                {
                    checkCollision();
                }

                if (!inGround && !onGround && !collidedHorizontally)
                {
                    ++ticksInAir;
                }
                else
                {
                    ticksInAir = 0;
                    motionX = 0.0D;
                    motionY = 0.0D;
                    motionZ = 0.0D;
                }
            }
            else
            {
                if (currentState == EntityBLFishHook.State.HOOKED_IN_ENTITY)
                {
                    if (caughtEntity != null)
                    {
                        if (caughtEntity.isDead)
                        {
                            caughtEntity = null;
                            currentState = EntityBLFishHook.State.FLYING;
                        }
                        else
                        {
                            posX = caughtEntity.posX;
                            double d2 = (double)caughtEntity.height;
                            posY = caughtEntity.getEntityBoundingBox().minY + d2 * 0.8D;
                            posZ = caughtEntity.posZ;
                            setPosition(posX, posY, posZ);
                        }
                    }

                    return;
                }

                if (currentState == EntityBLFishHook.State.BOBBING)
                {
                    motionX *= 0.9D;
                    motionZ *= 0.9D;
                    double d0 = posY + motionY - (double)blockpos.getY() - (double)f;

                    if (Math.abs(d0) < 0.01D)
                    {
                        d0 += Math.signum(d0) * 0.1D;
                    }

                    motionY -= d0 * (double)rand.nextFloat() * 0.2D;

                    if (!world.isRemote && f > 0.0F)
                    {
                        catchingFish(blockpos);
                    }
                }
            }

            if (iblockstate.getMaterial() != Material.WATER)
            {
                motionY -= 0.03D;
            }

            move(MoverType.SELF, motionX, motionY, motionZ);
            updateRotation();
            double d1 = 0.92D;
            motionX *= 0.92D;
            motionY *= 0.92D;
            motionZ *= 0.92D;
            setPosition(posX, posY, posZ);
        }
    }

    private boolean shouldStopFishing()
    {
        ItemStack itemstack = getAngler().getHeldItemMainhand();
        ItemStack itemstack1 = getAngler().getHeldItemOffhand();
        boolean flag = itemstack.getItem() instanceof ItemBLFishingRod;
        boolean flag1 = itemstack1.getItem() instanceof ItemBLFishingRod;

        if (!getAngler().isDead && getAngler().isEntityAlive() && (flag || flag1) && getDistanceSq(getAngler()) <= 1024.0D)
        {
            return false;
        }
        else
        {
            setDead();
            return true;
        }
    }

    private void updateRotation()
    {
        float f = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
        rotationYaw = (float)(MathHelper.atan2(motionX, motionZ) * (180D / Math.PI));

        for (rotationPitch = (float)(MathHelper.atan2(motionY, (double)f) * (180D / Math.PI)); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F)
        {
            ;
        }

        while (rotationPitch - prevRotationPitch >= 180.0F)
        {
            prevRotationPitch += 360.0F;
        }

        while (rotationYaw - prevRotationYaw < -180.0F)
        {
            prevRotationYaw -= 360.0F;
        }

        while (rotationYaw - prevRotationYaw >= 180.0F)
        {
            prevRotationYaw += 360.0F;
        }

        rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
        rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
    }

    private void checkCollision()
    {
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        Vec3d vec3d1 = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
        RayTraceResult raytraceresult = world.rayTraceBlocks(vec3d, vec3d1, false, true, false);
        vec3d = new Vec3d(posX, posY, posZ);
        vec3d1 = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);

        if (raytraceresult != null)
        {
            vec3d1 = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
        }

        Entity entity = null;
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(motionX, motionY, motionZ).grow(1.0D));
        double d0 = 0.0D;

        for (Entity entity1 : list)
        {
            if (canBeHooked(entity1) && (entity1 != getAngler() || ticksInAir >= 5))
            {
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.30000001192092896D);
                RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);

                if (raytraceresult1 != null)
                {
                    double d1 = vec3d.squareDistanceTo(raytraceresult1.hitVec);

                    if (d1 < d0 || d0 == 0.0D)
                    {
                        entity = entity1;
                        d0 = d1;
                    }
                }
            }
        }

        if (entity != null)
        {
            raytraceresult = new RayTraceResult(entity);
        }

        if (raytraceresult != null && raytraceresult.typeOfHit != RayTraceResult.Type.MISS)
        {
            if (raytraceresult.typeOfHit == RayTraceResult.Type.ENTITY)
            {
                caughtEntity = raytraceresult.entityHit;
                setHookedEntity();
            }
            else
            {
                inGround = true;
            }
        }
    }

    private void setHookedEntity()
    {
        getDataManager().set(DATA_HOOKED_ENTITY, Integer.valueOf(caughtEntity.getEntityId() + 1));
    }

    private void catchingFish(BlockPos p_190621_1_)
    {
        WorldServer worldserver = (WorldServer)world;
        int i = 1;
        BlockPos blockpos = p_190621_1_.up();

        if (rand.nextFloat() < 0.25F && world.isRainingAt(blockpos))
        {
            ++i;
        }

        if (rand.nextFloat() < 0.5F && !world.canSeeSky(blockpos))
        {
            --i;
        }

        if (ticksCatchable > 0)
        {
            --ticksCatchable;

            if (ticksCatchable <= 0)
            {
                ticksCaughtDelay = 0;
                ticksCatchableDelay = 0;
            }
            else
            {
                motionY -= 0.2D * (double)rand.nextFloat() * (double)rand.nextFloat();
            }
        }
        else if (ticksCatchableDelay > 0)
        {
            ticksCatchableDelay -= i;

            if (ticksCatchableDelay > 0)
            {
                fishApproachAngle = (float)((double)fishApproachAngle + rand.nextGaussian() * 4.0D);
                float f = fishApproachAngle * 0.017453292F;
                float f1 = MathHelper.sin(f);
                float f2 = MathHelper.cos(f);
                double d0 = posX + (double)(f1 * (float)ticksCatchableDelay * 0.1F);
                double d1 = (double)((float)MathHelper.floor(getEntityBoundingBox().minY) + 1.0F);
                double d2 = posZ + (double)(f2 * (float)ticksCatchableDelay * 0.1F);
                IBlockState state = worldserver.getBlockState(new BlockPos(d0, d1 - 1.0D, d2));

                if (state.getMaterial() == Material.WATER)
                {
                    if (rand.nextFloat() < 0.15F)
                    {
                        worldserver.spawnParticle(EnumParticleTypes.WATER_BUBBLE, d0, d1 - 0.10000000149011612D, d2, 1, (double)f1, 0.1D, (double)f2, 0.0D);
                    }

                    float f3 = f1 * 0.04F;
                    float f4 = f2 * 0.04F;
                    worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, d0, d1, d2, 0, (double)f4, 0.01D, (double)(-f3), 1.0D);
                    worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, d0, d1, d2, 0, (double)(-f4), 0.01D, (double)f3, 1.0D);
                }
            }
            else
            {
                motionY = (double)(-0.4F * MathHelper.nextFloat(rand, 0.6F, 1.0F));
                playSound(SoundEvents.ENTITY_BOBBER_SPLASH, 0.25F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
                double d3 = getEntityBoundingBox().minY + 0.5D;
                worldserver.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX, d3, posZ, (int)(1.0F + width * 20.0F), (double)width, 0.0D, (double)width, 0.20000000298023224D);
                worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, posX, d3, posZ, (int)(1.0F + width * 20.0F), (double)width, 0.0D, (double)width, 0.20000000298023224D);
                ticksCatchable = MathHelper.getInt(rand, 20, 40);
            }
        }
        else if (ticksCaughtDelay > 0)
        {
            ticksCaughtDelay -= i;
            float f5 = 0.15F;

            if (ticksCaughtDelay < 20)
            {
                f5 = (float)((double)f5 + (double)(20 - ticksCaughtDelay) * 0.05D);
            }
            else if (ticksCaughtDelay < 40)
            {
                f5 = (float)((double)f5 + (double)(40 - ticksCaughtDelay) * 0.02D);
            }
            else if (ticksCaughtDelay < 60)
            {
                f5 = (float)((double)f5 + (double)(60 - ticksCaughtDelay) * 0.01D);
            }

            if (rand.nextFloat() < f5)
            {
                float f6 = MathHelper.nextFloat(rand, 0.0F, 360.0F) * 0.017453292F;
                float f7 = MathHelper.nextFloat(rand, 25.0F, 60.0F);
                double d4 = posX + (double)(MathHelper.sin(f6) * f7 * 0.1F);
                double d5 = (double)((float)MathHelper.floor(getEntityBoundingBox().minY) + 1.0F);
                double d6 = posZ + (double)(MathHelper.cos(f6) * f7 * 0.1F);
                IBlockState state = worldserver.getBlockState(new BlockPos((int) d4, (int) d5 - 1, (int) d6));

                if (state.getMaterial() == Material.WATER)
                {
                    worldserver.spawnParticle(EnumParticleTypes.WATER_SPLASH, d4, d5, d6, 2 + rand.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D);
                }
            }

            if (ticksCaughtDelay <= 0)
            {
                fishApproachAngle = MathHelper.nextFloat(rand, 0.0F, 360.0F);
                ticksCatchableDelay = MathHelper.getInt(rand, 20, 80);
            }
        }
        else
        {
            ticksCaughtDelay = MathHelper.getInt(rand, 100, 600);
            ticksCaughtDelay -= lureSpeed * 20 * 5;
        }
    }

    protected boolean canBeHooked(Entity p_189739_1_)
    {
        return p_189739_1_.canBeCollidedWith() || p_189739_1_ instanceof EntityItem;
    }

    public int handleHookRetraction()
    {
        if (!world.isRemote && getAngler() != null)
        {
            int i = 0;

            //net.minecraftforge.event.entity.player.ItemFishedEvent event = null;
            if (caughtEntity != null)
            {
                bringInHookedEntity();
                world.setEntityState(this, (byte)31);
                i = caughtEntity instanceof EntityItem ? 3 : 5;
            }
            else if (ticksCatchable > 0)
            {
              //  LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer)world);
             //   lootcontext$builder.withLuck((float)luck + getAngler().getLuck()).withPlayer(getAngler()).withLootedEntity(this); // Forge: add player & looted entity to LootContext
             //   List<ItemStack> result = world.getLootTableManager().getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING).generateLootForPools(rand, lootcontext$builder.build());
               // event = new net.minecraftforge.event.entity.player.ItemFishedEvent(result, inGround ? 2 : 1, this);
             //   net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
              //  if (event.isCanceled())
             //   {
               //    setDead();
               //     return event.getRodDamage();
              //  }

               // for (ItemStack itemstack : result)
               // {
                   // EntityItem entityitem = new EntityItem(world, posX, posY, posZ, itemstack);
                    double d0 = getAngler().posX - posX;
                    double d1 = getAngler().posY - posY;
                    double d2 = getAngler().posZ - posZ;
                    double d3 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                    double d4 = 0.1D;
                    caughtEntity.motionX = d0 * 0.1D;
                    caughtEntity.motionY = d1 * 0.1D + (double)MathHelper.sqrt(d3) * 0.08D;
                    caughtEntity.motionZ = d2 * 0.1D;
                  //  world.spawnEntity(entityitem);
                    getAngler().world.spawnEntity(new EntityXPOrb(getAngler().world, getAngler().posX, getAngler().posY + 0.5D, getAngler().posZ + 0.5D, rand.nextInt(6) + 1));
                   // Item item = itemstack.getItem();

                    //if (item == Items.FISH || item == Items.COOKED_FISH)
                   // {
                        getAngler().addStat(StatList.FISH_CAUGHT, 1);
                 //   }
               // }

                i = 1;
            }

            if (inGround)
            {
                i = 2;
            }

            setDead();
            return  i;
        }
        else
        {
            return 0;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void handleStatusUpdate(byte id)
    {
        if (id == 31 && world.isRemote && caughtEntity instanceof EntityPlayer && ((EntityPlayer)caughtEntity).isUser())
        {
            bringInHookedEntity();
        }

        super.handleStatusUpdate(id);
    }

    protected void bringInHookedEntity()
    {
        if (getAngler() != null)
        {
            double d0 = getAngler().posX - posX;
            double d1 = getAngler().posY - posY;
            double d2 = getAngler().posZ - posZ;
            double d3 = 0.1D;
            caughtEntity.motionX += d0 * 0.1D;
            caughtEntity.motionY += d1 * 0.1D;
            caughtEntity.motionZ += d2 * 0.1D;
        }
    }

    @Override
    protected boolean canTriggerWalking()
    {
        return false;
    }

    @Override
    public void setDead()
    {
        super.setDead();

    //    if (getAngler() != null)
     //   {
      //      getAngler().fishEntity = null;
      //  }
    }

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		if (getAnglerId() == null)
			nbt.setString("OwnerUUID", "");
		else
			nbt.setString("OwnerUUID", getAnglerId().toString());

	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		String s;
		if (nbt.hasKey("OwnerUUID", 8))
			s = nbt.getString("OwnerUUID");
		else {
			String s1 = nbt.getString("Owner");
			s = PreYggdrasilConverter.convertMobOwnerIfNeeded(getServer(), s1);
		}
		if (!s.isEmpty()) {
			try {
				setAnglerId(UUID.fromString(s));
			} catch (Throwable e) {}
		}
	}
}