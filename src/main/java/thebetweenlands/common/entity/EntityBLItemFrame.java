package thebetweenlands.common.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thebetweenlands.common.item.EnumBLDyeColor;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class EntityBLItemFrame extends EntityItemFrame implements IEntityAdditionalSpawnData {
    protected static final Predicate<Entity> IS_HANGING_ENTITY = entity -> entity instanceof EntityHanging;

    private EnumBLDyeColor color = EnumBLDyeColor.DULL_LAVENDER;
    private float itemDropChance = 1.0F;

    private static final DataParameter<ItemStack> ITEM = EntityDataManager.<ItemStack>createKey(EntityBLItemFrame.class, DataSerializers.ITEM_STACK);


    public EntityBLItemFrame(World worldIn) {
        super(worldIn);
    }

    public EntityBLItemFrame(World worldIn, BlockPos pos, EnumFacing facing) {
        super(worldIn, pos, facing);

        System.out.println(facing);
        this.updateFacingWithBoundingBox(facing);
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
                this.entityDropItem(new ItemStack(ItemRegistry.ITEM_FRAME, 1, color.getMetadata()), 0.0F);
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
        if (!this.getDisplayedItem().isEmpty())
        {
            compound.setInteger("Color", color.getMetadata());
        }

        super.writeEntityToNBT(compound);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        NBTTagCompound nbttagcompound = compound.getCompoundTag("Item");

        if (!nbttagcompound.isEmpty())
        {
            this.color = EnumBLDyeColor.byMetadata(compound.getInteger("Color"));
        }

        super.readEntityFromNBT(compound);
    }

    @Override
    protected void updateBoundingBox() {
        super.updateBoundingBox();
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
}
