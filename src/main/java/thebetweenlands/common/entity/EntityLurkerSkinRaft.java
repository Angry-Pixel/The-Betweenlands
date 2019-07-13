package thebetweenlands.common.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityLurkerSkinRaft extends EntityBoat {
	private ItemStack shield = ItemStack.EMPTY;
	private boolean updating = false;

	public EntityLurkerSkinRaft(World worldIn) {
		super(worldIn);
		this.setSize(1.25F, 0.25F);
	}

	public EntityLurkerSkinRaft(World worldIn, double x, double y, double z, ItemStack shield) {
		super(worldIn, x, y,z);
		this.setSize(1.25F, 0.25F);
		this.shield = shield.copy();
	}

	@Override
	public Item getItemBoat() {
		return ItemRegistry.LURKER_SKIN_SHIELD;
	}

	protected ItemStack getBoatDrop() {
		if(!this.shield.isEmpty()) {
			return this.shield.copy();
		}
		return new ItemStack(this.getItemBoat());
	}

	@Override
	public EntityItem entityDropItem(ItemStack stack, float offsetY) {
		if(stack.getItem() == this.getItemBoat()) {
			return super.entityDropItem(this.getBoatDrop(), offsetY);
		}
		return null;
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
		//Don't break raft
		this.fallDistance = 0;

		super.updateFallState(y, onGroundIn, state, pos);
	}

	@Override
	protected void removePassenger(Entity passenger) {
		if(!this.world.isRemote && this.isEntityAlive() && this.getPassengers().indexOf(passenger) == 0) {
			ItemStack drop = this.getBoatDrop();

			boolean itemReturned = false;

			if(passenger instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) passenger;

				if(player.getHeldItem(EnumHand.OFF_HAND).isEmpty()) {
					player.setHeldItem(EnumHand.OFF_HAND, drop);
					itemReturned = true;
				} else if(player.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
					player.setHeldItem(EnumHand.MAIN_HAND, drop);
					itemReturned = true;
				} else {
					itemReturned = player.inventory.addItemStackToInventory(drop);
				}
			}

			if(itemReturned) {
				this.setDead();
			}
		}

		super.removePassenger(passenger);
	}

	@Override
	public void onUpdate() {
		this.updating = true;
		super.onUpdate();
		this.updating = false;
	}

	@Override
	public void move(MoverType type, double x, double y, double z) {
		if(this.updating) {
			x *= 0.4D;
			z *= 0.4D;
		}
		super.move(type, x, y, z);
	}

	@Override
	public void updatePassenger(Entity passenger) {
		super.updatePassenger(passenger);

		if(this.isPassenger(passenger)) {
			float offset = -0.2F;
			float yOffset = (float)((this.isDead ? 0.01D : this.getMountedYOffset()) + passenger.getYOffset());

			if(this.getPassengers().size() > 1) {
				int i = this.getPassengers().indexOf(passenger);

				if(i == 0) {
					offset = 0.3F;
				} else {
					offset = -0.4F;
				}

				if (passenger instanceof EntityAnimal) {
					offset = (float)((double)offset + 0.2D);
				}
			}

			Vec3d offsetPos = (new Vec3d((double)offset, 0.0D, 0.0D)).rotateYaw(-this.rotationYaw * 0.017453292F - ((float)Math.PI / 2F));
			passenger.setPosition(this.posX + offsetPos.x, this.posY + (double)yOffset, this.posZ + offsetPos.z);
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);

		if(!this.shield.isEmpty()) {
			NBTTagCompound shieldNbt = new NBTTagCompound();
			this.shield.writeToNBT(shieldNbt);
			nbt.setTag("shield", shieldNbt);
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);

		this.shield = ItemStack.EMPTY;
		if(nbt.hasKey("shield", Constants.NBT.TAG_COMPOUND)) {
			NBTTagCompound shieldNbt = nbt.getCompoundTag("shield");
			this.shield = new ItemStack(shieldNbt);
		}
	}
}
