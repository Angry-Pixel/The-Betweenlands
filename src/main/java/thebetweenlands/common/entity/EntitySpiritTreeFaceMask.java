package thebetweenlands.common.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class EntitySpiritTreeFaceMask extends EntityHanging implements IEntityAdditionalSpawnData {
	public static enum Type {
		LARGE, SMALL;
	}

	private Type type;

	public EntitySpiritTreeFaceMask(World world) {
		super(EntityRegistry.SPIRIT_TREE_FACE_MASK, world);
	}

	public EntitySpiritTreeFaceMask(World world, BlockPos pos, EnumFacing facing, Type type) {
		super(EntityRegistry.SPIRIT_TREE_FACE_MASK, world, pos);
		this.type = type;
		this.updateFacingWithBoundingBox(facing);
	}

	public Type getType() {
		return this.type;
	}

	@Override
	public void writeAdditional(NBTTagCompound nbt) {
		super.writeAdditional(nbt);
		nbt.setInt("type", this.type.ordinal());
	}

	@Override
	public void readAdditional(NBTTagCompound nbt) {
		super.readAdditional(nbt);
		this.type = Type.values()[nbt.getInt("type")];
	}

	@Override
	public int getWidthPixels() {
		return this.type == Type.LARGE ? 32 : 16;
	}

	@Override
	public int getHeightPixels() {
		return this.type == Type.LARGE ? 32 : 16;
	}

	@Override
	public void onBroken(@Nullable Entity brokenEntity) {
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			this.playSound(SoundEvents.BLOCK_WOOD_BREAK, 1.0F, 1.0F);

			if (brokenEntity instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer)brokenEntity;

				if (entityplayer.abilities.isCreativeMode) {
					return;
				}
			}

			this.entityDropItem(new ItemStack(this.type == Type.LARGE ? ItemRegistry.SPIRIT_TREE_FACE_LARGE_MASK : ItemRegistry.SPIRIT_TREE_FACE_SMALL_MASK), 0.0F);
		}
	}

	@Override
	public void playPlaceSound() {
		this.playSound(SoundEvents.BLOCK_WOOD_PLACE, 1.0F, 1.0F);
	}

	@Override
	public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch)  {
		this.setPosition(x, y, z);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
		BlockPos blockpos = this.hangingPosition.add(x - this.posX, y - this.posY, z - this.posZ);
		this.setPosition((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
	}

	@Override
	public void writeSpawnData(PacketBuffer buf) {
		buf.writeInt(this.type.ordinal());
		buf.writeLong(this.hangingPosition.toLong());
		buf.writeBoolean(this.facingDirection != null);
		if(this.facingDirection != null) {
			buf.writeInt(this.facingDirection.getIndex());
		}
	}

	@Override
	public void readSpawnData(PacketBuffer buf) {
		this.type = Type.values()[buf.readInt()];
		this.hangingPosition = BlockPos.fromLong(buf.readLong());
		if(buf.readBoolean()) {
			this.facingDirection = EnumFacing.byIndex(buf.readInt());
			this.updateFacingWithBoundingBox(this.facingDirection);
		}
	}
}