package thebetweenlands.common.entity.projectiles;


import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.api.event.SplashPotionEvent;
import thebetweenlands.common.block.terrain.BlockDentrothyst.EnumDentrothyst;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityElixir extends EntityThrowable {
    private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(EntityElixir.class, DataSerializers.ITEM_STACK);

    public EntityElixir(World world) {
        super(EntityRegistry.ELIXIR, world);
    }

    public EntityElixir(World world, EntityLivingBase thrower, ItemStack elixir) {
        super(EntityRegistry.ELIXIR, thrower, world);
        this.setItem(elixir.copy());
    }

    @Override
	protected void registerData() {
        this.getDataManager().register(ITEM, ItemStack.EMPTY);
    }

    public ItemStack getElixirStack() {
        return this.getDataManager().get(ITEM);
    }

    public void setItem(ItemStack stack) {
        this.getDataManager().set(ITEM, stack);
    }

    @Override
    protected float getGravityVelocity() {
        return 0.05F;
    }
    
    @Override
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote()) {
            AxisAlignedBB hitBB = this.getBoundingBox().grow(4.0D, 2.0D, 4.0D);
            List<EntityLivingBase> hitEntities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, hitBB);
            if (!hitEntities.isEmpty()) {
                Iterator<EntityLivingBase> hitEntitiesIT = hitEntities.iterator();
                while (hitEntitiesIT.hasNext()) {
                    EntityLivingBase affectedEntity = hitEntitiesIT.next();
                    double entityDst = this.getDistanceSq(affectedEntity);
                    if (entityDst < 16.0D) {
                        double modifier = 1.0D - Math.sqrt(entityDst) / 4.0D;
                        if (affectedEntity == result.entity) {
                            modifier = 1.0D;
                        }
                        PotionEffect effect = ItemRegistry.ELIXIR.createPotionEffect(getElixirStack(), modifier);
                        SplashPotionEvent event = new SplashPotionEvent(this, affectedEntity, effect, effect.getPotion().isInstant());
                        MinecraftForge.EVENT_BUS.post(event);
                        if(!event.isCanceled()) {
                        	affectedEntity.addPotionEffect(effect);
                        }
                    }
                }
            }
            this.world.playEvent(2002, new BlockPos(this), ItemRegistry.ELIXIR.getColorMultiplier(getElixirStack(), 0));
            this.entityDropItem(new ItemStack(ItemRegistry.ELIXIR.getDentrothystType(getElixirStack()) == EnumDentrothyst.GREEN ? ItemRegistry.DENTROTHYST_SHARD_GREEN : ItemRegistry.DENTROTHYST_SHARD_ORANGE,
            		this.world.rand.nextInt(2) + 2), this.height / 2);
            if(this.world.rand.nextInt(2) == 0) this.entityDropItem(EnumItemMisc.RUBBER_BALL.create(1), this.height / 2);
            this.remove();
        }
    }

    @Override
    public void readAdditional(NBTTagCompound nbt) {
        super.readAdditional(nbt);
    	ItemStack itemstack = ItemStack.read(nbt.getCompound("elixir"));

        if (itemstack.isEmpty()) {
            this.remove();
        } else {
            this.setItem(itemstack);
        }
    }

    @Override
    public void writeAdditional(NBTTagCompound nbt) {
        super.writeAdditional(nbt);
        ItemStack stack = getElixirStack();
        if (!stack.isEmpty()) {
            nbt.setTag("elixir", stack.write(new NBTTagCompound()));
        }
    }
}
