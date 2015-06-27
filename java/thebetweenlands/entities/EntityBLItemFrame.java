package thebetweenlands.entities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import thebetweenlands.items.BLItemRegistry;

/**
 * Created by Bart on 27-6-2015.
 */
public class EntityBLItemFrame extends EntityItemFrame {

    private float itemDropChance = 1.0F;

    public EntityBLItemFrame(World world) {
        super(world);
    }

    public EntityBLItemFrame(World world, int x, int y, int z, int direction) {
        super(world, x, y, z, direction);
        this.setDirection(direction);
    }

    @Override
    protected void entityInit() {
        this.getDataWatcher().addObjectByDataType(2, 5);
        this.getDataWatcher().addObject(3, Byte.valueOf((byte) 0));
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float v) {
        if (this.isEntityInvulnerable()) {
            return false;
        } else if (this.getDisplayedItem() != null) {
            if (!this.worldObj.isRemote) {
                this.func_146065_b(damageSource.getEntity(), false);
                this.setDisplayedItem((ItemStack) null);
            }

            return true;
        } else {
            return super.attackEntityFrom(damageSource, v);
        }
    }

    @Override
    public int getWidthPixels() {
        return 9;
    }

    @Override
    public int getHeightPixels() {
        return 9;
    }

    @Override
    public void onBroken(Entity entity) {
        this.func_146065_b(entity, true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double p_70112_1_) {
        double d1 = 16.0D;
        d1 *= 64.0D * this.renderDistanceWeight;
        return p_70112_1_ < d1 * d1;
    }

    @Override
    public void func_146065_b(Entity entity, boolean p_146065_2_) {
        ItemStack itemstack = this.getDisplayedItem();

        if (entity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entity;

            if (entityplayer.capabilities.isCreativeMode) {
                this.removeFrameFromMap(itemstack);
                return;
            }
        }

        if (p_146065_2_) {
            this.entityDropItem(new ItemStack(BLItemRegistry.itemFrame), 0.0F);
        }


        if (itemstack != null && this.rand.nextFloat() < itemDropChance) {
            itemstack = itemstack.copy();
            this.removeFrameFromMap(itemstack);
            this.entityDropItem(itemstack, 0.0F);
        }
    }

    public ItemStack getDisplayedItem()
    {
        return this.getDataWatcher().getWatchableObjectItemStack(2);
    }

    public void setDisplayedItem(ItemStack itemStack)
    {
        if (itemStack != null)
        {
            itemStack = itemStack.copy();
            itemStack.stackSize = 1;
            itemStack.setItemFrame(this);
        }

        this.getDataWatcher().updateObject(2, itemStack);
        this.getDataWatcher().setObjectWatched(2);
    }

    public int getRotation()
    {
        return this.getDataWatcher().getWatchableObjectByte(3);
    }

    public void setItemRotation(int i) {
        this.getDataWatcher().updateObject(3, Byte.valueOf((byte)(i % 4)));
    }

    private void removeFrameFromMap(ItemStack itemStack) {
        if (itemStack != null) {
            if (itemStack.getItem() == Items.filled_map) {
                MapData mapdata = ((ItemMap) itemStack.getItem()).getMapData(itemStack, this.worldObj);
                mapdata.playersVisibleOnMap.remove("frame-" + this.getEntityId());
            }

            itemStack.setItemFrame((EntityBLItemFrame) null);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        if (this.getDisplayedItem() != null) {
            p_70014_1_.setTag("Item", this.getDisplayedItem().writeToNBT(new NBTTagCompound()));
            p_70014_1_.setByte("ItemRotation", (byte) this.getRotation());
            p_70014_1_.setFloat("ItemDropChance", this.itemDropChance);
        }

        super.writeEntityToNBT(p_70014_1_);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        NBTTagCompound nbttagcompound1 = nbt.getCompoundTag("Item");

        if (nbttagcompound1 != null && !nbttagcompound1.hasNoTags()) {
            this.setDisplayedItem(ItemStack.loadItemStackFromNBT(nbttagcompound1));
            this.setItemRotation(nbt.getByte("ItemRotation"));

            if (nbt.hasKey("ItemDropChance", 99)) {
                this.itemDropChance = nbt.getFloat("ItemDropChance");
            }
        }

        super.readEntityFromNBT(nbt);
    }

    @Override
    public boolean interactFirst(EntityPlayer p_130002_1_) {
        if (this.getDisplayedItem() == null) {
            ItemStack itemstack = p_130002_1_.getHeldItem();

            if (itemstack != null && !this.worldObj.isRemote) {
                this.setDisplayedItem(itemstack);

                if (!p_130002_1_.capabilities.isCreativeMode && --itemstack.stackSize <= 0) {
                    p_130002_1_.inventory.setInventorySlotContents(p_130002_1_.inventory.currentItem, (ItemStack) null);
                }
            }
        } else if (!this.worldObj.isRemote) {
            this.setItemRotation(this.getRotation() + 1);
        }

        return true;
    }
}
