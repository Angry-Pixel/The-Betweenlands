package thebetweenlands.common.entity.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.item.tools.bow.EnumArrowType;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.config.ConfigHandler;

import java.util.List;
import java.util.UUID;

public class EntityBLArrow extends EntityArrow {

    private static final DataParameter<String> DW_TYPE = EntityDataManager.<String>createKey(EntityArrow.class, DataSerializers.STRING);
    private static final DataParameter<String> DW_UUID = EntityDataManager.<String>createKey(EntityArrow.class, DataSerializers.STRING);
    private boolean checkedShooter = false;
    private boolean inGround = false;
    private int inGroundTicks = 0;
    public BlockPos lastLight = BlockPos.ORIGIN;


    public EntityBLArrow(World worldIn) {
        super(worldIn);
    }

    public EntityBLArrow(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityBLArrow(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
        this.dataManager.set(DW_UUID, shooter.getUniqueID().toString());
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.dataManager.register(DW_TYPE, "");
        this.dataManager.register(DW_UUID, "");
    }



    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("inGroundTicks", this.inGroundTicks);
        nbt.setString("arrowType", this.getArrowType().getName());
        nbt.setString("shooter", this.getDataManager().get(DW_UUID));
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.inGroundTicks = nbt.getInteger("inGroundTicks");
        this.setType(nbt.getString("arrowType"));
        this.dataManager.set(DW_UUID, "shooter");
    }

    @Override
    public void onEntityUpdate() {
        if(this.shootingEntity == null && !this.checkedShooter) {
            try {
                UUID uuid = UUID.fromString(this.dataManager.get(DW_UUID));
                if(uuid != null) {
                    this.shootingEntity = this.worldObj.getPlayerEntityByUUID(uuid);
                }
            } catch(Exception ignored) { } finally {
                this.checkedShooter = true;
            }
        }
        if(this.inGround) {
            this.inGroundTicks++;
        }
        if(!this.worldObj.isRemote && !this.inGround) {
            RayTraceResult collision = getCollision(this);
            if(collision != null && collision.typeOfHit == RayTraceResult.Type.ENTITY && collision.entityHit instanceof EntityLivingBase) {
                EntityLivingBase hitEntity = (EntityLivingBase) collision.entityHit;
                switch(this.getArrowType()) {
                    case ANGLER_POISON:
                        hitEntity.addPotionEffect(new PotionEffect(MobEffects.POISON, 200, 2));
                        break;
                    case OCTINE:
                        if(hitEntity.isBurning()) {
                            hitEntity.setFire(9);
                        } else {
                            hitEntity.setFire(5);
                        }
                        break;
                    case BASILISK:
                        hitEntity.addPotionEffect(ElixirEffectRegistry.EFFECT_PETRIFY.createEffect(100, 1));
                        break;
                    default:
                }
            } else if(collision != null && collision.typeOfHit == RayTraceResult.Type.BLOCK) {
                this.inGround = true;
            }
        }

        //TODO light up the octine arrows?
        if (getArrowType().equals(EnumArrowType.OCTINE)){
            if (this.lastLight != this.getPosition()) {
                if (ConfigHandler.fireflyLighting && !ShaderHelper.INSTANCE.isWorldShaderActive()) {
                    this.switchOff();
                    this.lightUp(this.worldObj, this.getPosition());
                }
            }
        }
        super.onEntityUpdate();
    }
    @SideOnly(Side.CLIENT)
    private void switchOff() {
        this.worldObj.checkLightFor(EnumSkyBlock.BLOCK, this.lastLight);
        this.worldObj.checkLightFor(EnumSkyBlock.BLOCK, this.getPosition());
    }

    @SideOnly(Side.CLIENT)
    private void lightUp(World world, BlockPos pos) {
        world.setLightFor(EnumSkyBlock.BLOCK, pos, 4);
        for (int offsetX = -1; offsetX < 2; offsetX++) {
            for (int offsetY = -1; offsetY < 2; offsetY++) {
                for (int offsetZ = -1; offsetZ < 2; offsetZ++) {
                    BlockPos offset = pos.add(offsetX, offsetY, offsetZ);
                    if (!offset.equals(this.lastLight) || this.isDead) {
                        world.checkLightFor(EnumSkyBlock.BLOCK, this.lastLight.add(offsetX, offsetY, offsetZ));
                        this.lastLight = pos;
                    }
                }
            }
        }
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        if (!this.worldObj.isRemote && this.inGround && this.inGroundTicks > 20 && this.arrowShake <= 0) {
            switchOff();
            boolean canPickUp = this.pickupStatus == EntityArrow.PickupStatus.ALLOWED || this.pickupStatus == EntityArrow.PickupStatus.CREATIVE_ONLY && player.capabilities.isCreativeMode;
            if (canPickUp && !this.pickUp(player)) {
                canPickUp = false;
            }
            if (canPickUp) {
                playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                player.onItemPickup(this, 1);
                setDead();
            }
        }
    }

    private boolean pickUp(EntityPlayer player) {
        switch(this.getArrowType()) {
            case ANGLER_POISON:
                return player.inventory.addItemStackToInventory(new ItemStack(ItemRegistry.POISONED_ANGLER_TOOTH_ARROW, 1));
            case OCTINE:
                return player.inventory.addItemStackToInventory(new ItemStack(ItemRegistry.OCTINE_ARROW, 1));
            case BASILISK:
                return player.inventory.addItemStackToInventory(new ItemStack(ItemRegistry.BASILISK_ARROW, 1));
            case DEFAULT:
            default:
                return player.inventory.addItemStackToInventory(new ItemStack(ItemRegistry.ANGLER_TOOTH_ARROW, 1));
        }
    }

    private static RayTraceResult getCollision(EntityArrow ea) {
        Vec3d start = new Vec3d(ea.posX, ea.posY, ea.posZ);
        Vec3d dest = new Vec3d(ea.posX + ea.motionX, ea.posY + ea.motionY, ea.posZ + ea.motionZ);
        RayTraceResult hit = ea.worldObj.rayTraceBlocks(start, dest, false, true, false);
        start = new Vec3d(ea.posX, ea.posY, ea.posZ);
        dest = new Vec3d(ea.posX + ea.motionX, ea.posY + ea.motionY, ea.posZ + ea.motionZ);
        if (hit != null) {
            dest = new Vec3d(hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord);
        }
        Entity collidedEntity = null;
        List entityList = ea.worldObj.getEntitiesWithinAABBExcludingEntity(ea, ea.getEntityBoundingBox().addCoord(ea.motionX, ea.motionY, ea.motionZ).expand(1.05D, 1.05D, 1.05D));
        double lastDistance = 0.0D;
        for (Object anEntityList : entityList) {
            Entity currentEntity = (Entity) anEntityList;
            if (currentEntity.canBeCollidedWith() && (currentEntity != ea.shootingEntity || ea.ticksExisted > 5)) {
                AxisAlignedBB entityBoundingBox = currentEntity.getEntityBoundingBox().expand((double) 0.35F, (double) 0.35F, (double) 0.35F);
                RayTraceResult collision = entityBoundingBox.calculateIntercept(start, dest);
                if (collision != null) {
                    double currentDistance = start.distanceTo(collision.hitVec);

                    if (currentDistance < lastDistance || lastDistance == 0.0D) {
                        collidedEntity = currentEntity;
                        lastDistance = currentDistance;
                    }
                }
            }
        }
        if (collidedEntity != null) {
            hit = new RayTraceResult(collidedEntity);
        }
        return hit;
    }

    public void setType(String type) {
        dataManager.set(DW_TYPE, type);
    }

    public EnumArrowType getArrowType(){
        return EnumArrowType.getEnumFromString(dataManager.get(DW_TYPE));
    }

    @Override
    protected ItemStack getArrowStack() {
        return null;
    }
}
