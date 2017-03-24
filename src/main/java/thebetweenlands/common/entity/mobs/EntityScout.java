package thebetweenlands.common.entity.mobs;

import com.google.common.base.Optional;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import thebetweenlands.common.entity.ai.EntityAIFlyRandomly;
import thebetweenlands.common.entity.movement.FlightMoveHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EntityScout extends EntityFlying {
    protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(EntityTameable.class, DataSerializers.OPTIONAL_UNIQUE_ID);

    public EntityScout(World worldIn) {
        super(worldIn);
        this.setSize(0.6F, 0.6F);
        this.moveHelper = new FlightMoveHelper(this);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(OWNER_UNIQUE_ID, Optional.<UUID>absent());
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(5, new EntityAIFlyRandomly<EntityScout>(this) {
            @Override
            protected double getTargetY(Random rand, double distanceMultiplier) {
                if (EntityScout.this.getOwner() != null) {
                    return EntityScout.this.getOwner().posY + 4;
                }
                return EntityScout.this.posY;
            }

            @Override
            protected double getTargetX(Random rand, double distanceMultiplier) {
                if (EntityScout.this.getOwner() != null) {
                    return EntityScout.this.getOwner().posX + (rand.nextFloat() * 2.0F - 1.0F) * 16.0F * 15d;
                }
                return EntityScout.this.posX;
            }

            @Override
            protected double getTargetZ(Random rand, double distanceMultiplier) {
                if (EntityScout.this.getOwner() != null) {
                    return EntityScout.this.getOwner().posZ + (rand.nextFloat() * 2.0F - 1.0F) * 16.0F * 15d;
                }
                return EntityScout.this.posZ;
            }

            @Override
            protected double getFlightSpeed() {
                return 0.1D;
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
            AxisAlignedBB bound = new AxisAlignedBB(this.posX - 10, this.posY - 10, this.posZ - 10, this.posX + 10, this.posY + 10, this.posZ + 10);
            List<EntityLiving> entities = worldObj.getEntitiesWithinAABB(EntityLiving.class, bound);
            for (EntityLiving e : entities)
                if (!(e instanceof EntityScout))
                    e.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 500, 0));
        } else {
            this.setDead();
        }
    }
}
