package thebetweenlands.common.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.common.item.herblore.ItemAspectVial;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class EntityBLItemFrame extends EntityItemFrame implements IEntityAdditionalSpawnData {
    private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntityBLItemFrame.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> INVISIBLE = EntityDataManager.createKey(EntityBLItemFrame.class, DataSerializers.BOOLEAN);
    private static final String TAG_COLOR = "DyeColor";

    private float itemDropChance = 1.0F;


    public EntityBLItemFrame(World worldIn) {
        super(worldIn);
    }

    public EntityBLItemFrame(World worldIn, BlockPos pos, EnumFacing facing, int color) {
        super(worldIn, pos, facing);
        dataManager.set(COLOR, color);
        dataManager.set(INVISIBLE, false);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(COLOR, 0);
        dataManager.register(INVISIBLE, false);
    }

    @Nonnull
    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        ItemStack held = getDisplayedItem();
        if (held.isEmpty())
            return new ItemStack(ItemRegistry.ITEM_FRAME, 1, getColor());
        else
            return held.copy();
    }

    @Override
    public void dropItemOrSelf(@Nullable Entity entityIn, boolean p_146065_2_)
    {
        if (this.world.getGameRules().getBoolean("doEntityDrops"))
        {
            ItemStack itemstack = this.getDisplayedItem();

            if (entityIn instanceof EntityPlayer)
            {
                EntityPlayer entityplayer = (EntityPlayer)entityIn;

                if (entityplayer.capabilities.isCreativeMode)
                {
                    this.removeFrameFromMap(itemstack);
                    return;
                }
            }

            if (p_146065_2_)
            {
                this.entityDropItem(new ItemStack(ItemRegistry.ITEM_FRAME, 1, getColor()), 0.0F);
            }

            if (!itemstack.isEmpty() && this.rand.nextFloat() < this.itemDropChance)
            {
                itemstack = itemstack.copy();
                this.removeFrameFromMap(itemstack);
                this.entityDropItem(itemstack, 0.0F);
            }
        }
    }

    private void removeFrameFromMap(ItemStack stack)
    {
        if (!stack.isEmpty())
        {
            if (stack.getItem() instanceof net.minecraft.item.ItemMap)
            {
                MapData mapdata = ((ItemMap)stack.getItem()).getMapData(stack, this.world);
                mapdata.mapDecorations.remove("frame-" + this.getEntityId());
            }

            stack.setItemFrame((EntityBLItemFrame)null);
            this.setDisplayedItem(ItemStack.EMPTY); //Forge: Fix MC-124833 Pistons duplicating Items.
        }
    }


    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setInteger(TAG_COLOR, getColor());
        compound.setBoolean("IS_INVISIBLE", dataManager.get(INVISIBLE));
        super.writeEntityToNBT(compound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        dataManager.set(COLOR, compound.getInteger(TAG_COLOR));
        dataManager.set(INVISIBLE, compound.getBoolean("IS_INVISIBLE"));
        super.readEntityFromNBT(compound);
    }

    @Override
    public void writeSpawnData(ByteBuf buf) {
        buf.writeLong(this.hangingPosition.toLong());
        buf.writeBoolean(this.facingDirection != null);
        if(this.facingDirection != null) {
            buf.writeInt(this.facingDirection.getIndex());
        }
    }

    @Override
    public void readSpawnData(ByteBuf buf) {
        this.hangingPosition = BlockPos.fromLong(buf.readLong());
        if(buf.readBoolean()) {
            this.facingDirection = EnumFacing.byIndex(buf.readInt());
            this.updateFacingWithBoundingBox(this.facingDirection);
        }
    }

    public int getColor() {
        return dataManager.get(COLOR);
    }


    public void SetVisibility(boolean isInvisible) {
        dataManager.set(INVISIBLE, isInvisible);
    }


    public boolean IsFrameInvisible() {
        return dataManager.get(INVISIBLE);
    }


    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        ItemStack itemstack = player.getHeldItem(hand);

        if (!this.world.isRemote)
        {
            if(player.isSneaking() && itemstack.getItem() instanceof ItemAspectVial && !this.IsFrameInvisible()) {
                ItemAspectContainer aspectVial = ItemAspectContainer.fromItem(itemstack);
                List<Aspect> aspectList = aspectVial.getAspects(player);

                for(Aspect aspect : aspectList) {
                    if(aspect.type == AspectRegistry.FREIWYNN && aspect.amount >= 100) {
                        aspectVial.drain(AspectRegistry.FREIWYNN, 100);
                        this.SetVisibility(true);
                        return true;
                    }
                }
            }

            if (this.getDisplayedItem().isEmpty())
            {
                if (!itemstack.isEmpty())
                {
                    this.setDisplayedItem(itemstack);

                    if (!player.capabilities.isCreativeMode)
                    {
                        itemstack.shrink(1);
                    }
                }
            }
            else
            {
                this.playSound(SoundEvents.ENTITY_ITEMFRAME_ROTATE_ITEM, 1.0F, 1.0F);
                this.setItemRotation(this.getRotation() + 1);
            }
        }

        return true;
    }
}
