package thebetweenlands.common.entity.mobs;

import com.google.common.base.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.common.entity.ai.EntityAIMoveToDirect;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class EntityScout extends EntityFlying {
    protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(EntityTameable.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    protected static final DataParameter<BlockPos> LOCATION = EntityDataManager.<BlockPos>createKey(EntityTameable.class, DataSerializers.BLOCK_POS);

    public EntityScout(World worldIn) {
        super(worldIn);
        this.setSize(0.6F, 0.6F);
        this.moveHelper = new FlightMoveHelper(this);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(OWNER_UNIQUE_ID, Optional.<UUID>absent());
        this.dataManager.register(LOCATION, new BlockPos(0, -10, 0));
    }


    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(2, new EntityAIMoveToDirect<EntityScout>(this, 0.25D) {

            @Nullable
            @Override
            protected Vec3d getTarget() {
                if (!EntityScout.this.getLocation().equals(new BlockPos(0, -10, 0))) {
                    return new Vec3d(getLocation().getX(), getLocation().getY(), getLocation().getZ());
                } else if (EntityScout.this.getOwner() != null) {
                    EntityLivingBase player = EntityScout.this.getOwner();
                    return new Vec3d(player.posX, player.posY, player.posZ);
                }
                return null;
            }
        });
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        if (this.getOwnerId() == null) {
            nbt.setString("OwnerUUID", "");
        } else {
            nbt.setString("OwnerUUID", this.getOwnerId().toString());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        String s;
        if (nbt.hasKey("OwnerUUID", 8)) {
            s = nbt.getString("OwnerUUID");
        } else {
            String s1 = nbt.getString("Owner");
            s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
        }

        if (!s.isEmpty()) {
            try {
                this.setOwnerId(UUID.fromString(s));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    public UUID getOwnerId() {
        return (UUID) ((Optional) this.dataManager.get(OWNER_UNIQUE_ID)).orNull();
    }

    public void setOwnerId(@Nullable UUID playerID) {
        this.dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(playerID));
        EntityLivingBase player = getOwner();
        if (player instanceof EntityPlayer)
            for (ItemStack itemStack : player.getArmorInventoryList())
                if (itemStack != null && itemStack.getItem() == ItemRegistry.SCOUT) {
                    NBTTagCompound tagCompound = itemStack.getTagCompound();
                    if (tagCompound == null)
                        tagCompound = new NBTTagCompound();
                    tagCompound.setInteger("entity_id", this.getEntityId());
                    itemStack.setTagCompound(tagCompound);
                    break;
                }
    }

    public void setLocation(BlockPos pos) {
        if (pos == null)
            pos = new BlockPos(0, -10, 0);
        this.dataManager.set(LOCATION, pos);
    }

    public BlockPos getLocation() {
        return this.dataManager.get(LOCATION);
    }

    @Nullable
    public EntityLivingBase getOwner() {
        try {
            UUID uuid = this.getOwnerId();
            return uuid == null ? null : this.worldObj.getPlayerEntityByUUID(uuid);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        if (getOwner() != null) {
            boolean hasOwner = false;
            for (ItemStack itemStack : getOwner().getArmorInventoryList())
                if (itemStack != null && itemStack.getItem() == ItemRegistry.SCOUT) {
                    if ((itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("entity_id") && itemStack.getTagCompound().getInteger("entity_id") == this.getEntityId())) {
                        hasOwner = true;
                    }
                }
            if (!hasOwner)
                this.setDead();

            AxisAlignedBB bound = new AxisAlignedBB(this.posX - 10, this.posY - 10, this.posZ - 10, this.posX + 10, this.posY + 10, this.posZ + 10);
            List<EntityLiving> entities = worldObj.getEntitiesWithinAABB(EntityLiving.class, bound);
            for (EntityLiving e : entities)
                if (!(e instanceof EntityScout))
                    e.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 500, 0));
        } else {
            this.setDead();
        }
    }

    @Override
    protected void collideWithEntity(Entity entityIn) {
    }
}
