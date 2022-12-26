package thebetweenlands.common.entity;

import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.common.item.herblore.ItemAspectVial;
import thebetweenlands.common.item.misc.ItemGlowingGoop;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityBLItemFrame extends EntityItemFrame implements IEntityAdditionalSpawnData {
    private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntityBLItemFrame.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> INVISIBLE = EntityDataManager.createKey(EntityBLItemFrame.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_GLOWING = EntityDataManager.createKey(EntityBLItemFrame.class, DataSerializers.BOOLEAN);
    private static final String TAG_COLOR = "DyeColor";
    protected static final Predicate<Entity> IS_HANGING_ENTITY = entity -> entity instanceof EntityHanging;

    private float itemDropChance = 1.0F;
    public EnumFacing realFacingDirection;


    public EntityBLItemFrame(World worldIn) {
        super(worldIn);
    }

    public EntityBLItemFrame(World worldIn, BlockPos pos, EnumFacing facing, int color) {
        super(worldIn, pos, facing);
        dataManager.set(COLOR, color);
        dataManager.set(INVISIBLE, false);
        dataManager.set(IS_GLOWING, false);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(COLOR, 0);
        dataManager.register(INVISIBLE, false);
        dataManager.register(IS_GLOWING, false);
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

    @Override
    public boolean onValidSurface() {
        if(this.realFacingDirection.getAxis() == EnumFacing.Axis.Y) {
            if(!this.world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty()) {
                return false;
            } else {
                BlockPos blockpos = this.hangingPosition.offset(this.realFacingDirection.getOpposite());
                IBlockState iblockstate = this.world.getBlockState(blockpos);
                if(!iblockstate.isSideSolid(this.world, blockpos, this.realFacingDirection))
                    if(!iblockstate.getMaterial().isSolid() && !BlockRedstoneDiode.isDiode(iblockstate))
                        return false;

                return this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox(), IS_HANGING_ENTITY::test).isEmpty();
            }
        } else
            return super.onValidSurface();
    }

    @Override
    public EntityItem entityDropItem(ItemStack stack, float offsetY) {
        EntityItem entityitem = new EntityItem(this.world, this.posX + (this.realFacingDirection.getXOffset() * 0.25F), this.posY + offsetY + (this.realFacingDirection.getYOffset() * 0.25F), this.posZ + (this.realFacingDirection.getZOffset() * 0.25F), stack);
        entityitem.setDefaultPickupDelay();
        if (realFacingDirection == EnumFacing.DOWN)
            entityitem.motionY = -Math.abs(entityitem.motionY);
        this.world.spawnEntity(entityitem);
        return entityitem;
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
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger(TAG_COLOR, getColor());
        compound.setBoolean("IS_INVISIBLE", dataManager.get(INVISIBLE));
        compound.setBoolean("IS_GLOWING", dataManager.get(IS_GLOWING));
        compound.setByte("REAL_FACING_DIRECTION", (byte)this.realFacingDirection.getIndex());
        super.writeEntityToNBT(compound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        dataManager.set(COLOR, compound.getInteger(TAG_COLOR));
        dataManager.set(INVISIBLE, compound.getBoolean("IS_INVISIBLE"));
        dataManager.set(IS_GLOWING, compound.getBoolean("IS_GLOWING"));
        super.readEntityFromNBT(compound);

        this.updateFacingWithBoundingBox(EnumFacing.byIndex(compound.getByte("REAL_FACING_DIRECTION")));
    }

    @Override
    public void writeSpawnData(ByteBuf buf) {
        buf.writeLong(this.hangingPosition.toLong());
        buf.writeShort(this.realFacingDirection.getIndex());
    }

    @Override
    public void readSpawnData(ByteBuf buf) {
        this.hangingPosition = BlockPos.fromLong(buf.readLong());
        updateFacingWithBoundingBox(EnumFacing.byIndex(buf.readShort()));
    }

    @Override
    protected void updateFacingWithBoundingBox(EnumFacing facingDirectionIn) {
        Validate.notNull(facingDirectionIn);
        this.realFacingDirection = facingDirectionIn;
        this.facingDirection = realFacingDirection.getAxis() == EnumFacing.Axis.Y ? EnumFacing.SOUTH : realFacingDirection;
        this.rotationYaw = realFacingDirection.getAxis() == EnumFacing.Axis.Y ? 0 : (this.realFacingDirection.getHorizontalIndex() * 90);
        this.rotationPitch = realFacingDirection.getAxis() == EnumFacing.Axis.Y ? (realFacingDirection == EnumFacing.UP ? -90.0F : 90.0F) : 0F;
        this.prevRotationYaw = this.rotationYaw;
        this.updateBoundingBox();
    }

    @Override
    protected void updateBoundingBox() {
        if(this.realFacingDirection == null)
            return;

        if(this.realFacingDirection.getAxis() == EnumFacing.Axis.Y) {
            double d0 = this.hangingPosition.getX() + 0.5D;
            double d1 = this.hangingPosition.getY() + 0.5D;
            double d2 = this.hangingPosition.getZ() + 0.5D;
            d1 = d1 - this.realFacingDirection.getYOffset() * 0.46875D;

            double d6 = this.getHeightPixels();
            double d7 = -this.realFacingDirection.getYOffset();
            double d8 = this.getHeightPixels();

            d6 = d6 / 32.0D;
            d7 = d7 / 32.0D;
            d8 = d8 / 32.0D;

            this.posX = d0;
            this.posY = d1 - d7;
            this.posZ = d2;
            this.height = 1.0F / 16.0F;
            this.setEntityBoundingBox(new AxisAlignedBB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8));
        } else
            super.updateBoundingBox();
    }

    public int getColor() {
        return dataManager.get(COLOR);
    }


    public void SetVisibility(boolean isInvisible, EntityPlayer player) {
    	if(isInvisible)
    		AdvancementCriterionRegistry.ITEM_FRAME_INVISIBLE.trigger((EntityPlayerMP) player);
        dataManager.set(INVISIBLE, isInvisible);
    }


    public boolean IsFrameInvisible() {
        return dataManager.get(INVISIBLE);
    }

    public boolean IsFrameGlowing() {
        return dataManager.get(IS_GLOWING);
    }

    public void SetGlowing(boolean isGlowing) {
        dataManager.set(IS_GLOWING, isGlowing);
    }


    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        ItemStack itemstack = player.getHeldItem(hand);

        if (!this.world.isRemote)
        {
            if(player.isSneaking()) {
                if (itemstack.getItem() instanceof ItemAspectVial && !this.IsFrameInvisible()) {
                    ItemAspectContainer aspectVial = ItemAspectContainer.fromItem(itemstack);
                    List<Aspect> aspectList = aspectVial.getAspects(player);

                    for (Aspect aspect : aspectList) {
                        if (aspect.type == AspectRegistry.FREIWYNN && aspect.amount >= 100) {
                            aspectVial.drain(AspectRegistry.FREIWYNN, 100);
                            this.SetVisibility(true, player);
                            return true;
                        }
                    }
                } else if(itemstack.getItem() instanceof ItemGlowingGoop && !isGlowing()) {
                    itemstack.shrink(1);
                    SetGlowing(true);
                    return true;
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
