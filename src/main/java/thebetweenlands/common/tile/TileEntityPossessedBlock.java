package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.block.structure.BlockPossessedBlock;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.AnimationMathHelper;

public class TileEntityPossessedBlock extends TileEntity implements ITickable {

	public int animationTicks, coolDown;
	public boolean active;
	AnimationMathHelper headShake = new AnimationMathHelper();
	public float moveProgress;

	@Override
	public void update() {
		if (!world.isRemote) {
			findEnemyToAttack();
			if (active) {
				activateBlock();
				if (animationTicks == 0)
					world.playSound(null, getPos(), SoundRegistry.POSSESSED_SCREAM, SoundCategory.BLOCKS, 0.25F, 1.25F - this.world.rand.nextFloat() * 0.5F);
				if (animationTicks <= 24)
					animationTicks++;
				if (animationTicks == 24) {
					setActive(false);
					coolDown = 200;
				}
			}
			if (!active) {
				if (animationTicks >= 1)
					animationTicks--;
				if(coolDown >= 0)
					coolDown--;
			}
			world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
		}
		moveProgress = 1 + headShake.swing(4, 1F, false);
		if (world.isRemote)
			if(!active && animationTicks %8 > 0)
				spawnParticles();
	}

	private void spawnParticles() {
		IBlockState state = getWorld().getBlockState(pos);
		EnumFacing facing = state.getValue(BlockPossessedBlock.FACING);
		float x = 0, z = 0;
		if(facing == EnumFacing.WEST)
			x = -1F;
		if(facing == EnumFacing.EAST)
			x = 1F;
		if(facing == EnumFacing.NORTH)
			z = -1F;
		if(facing == EnumFacing.SOUTH)
			z = 1F;

		float xx = (float) getPos().getX() + 0.5F + x;
		float yy = (float) getPos().getY() + 0.5F;
		float zz = (float) getPos().getZ() + 0.5F + z;
		float randomOffset = world.rand.nextFloat() * 0.6F - 0.3F;
		BLParticles.SMOKE.spawn(world, (double) (xx - randomOffset), (double) (yy + randomOffset), (double) (zz + randomOffset));
		BLParticles.SMOKE.spawn(world, (double) (xx + randomOffset), (double) (yy - randomOffset), (double) (zz + randomOffset));
		BLParticles.SMOKE.spawn(world, (double) (xx + randomOffset), (double) (yy + randomOffset), (double) (zz - randomOffset));
		BLParticles.SMOKE.spawn(world, (double) (xx + randomOffset), (double) (yy - randomOffset), (double) (zz + randomOffset));
	}

	public void setActive(boolean isActive) {
		active = isActive;
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
	}

	@SuppressWarnings("unchecked")
	protected Entity findEnemyToAttack() {
		IBlockState state = getWorld().getBlockState(pos);
		EnumFacing facing = state.getValue(BlockPossessedBlock.FACING);
		float x = 0, z = 0;
		if(facing == EnumFacing.WEST)
			x = -1.25F;
		if(facing == EnumFacing.EAST)
			x = 1.25F;
		if(facing == EnumFacing.NORTH)
			z = -1.25F;
		if(facing == EnumFacing.SOUTH)
			z = 1.25F;
		List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.getX() + x, pos.getY(), pos.getZ() + z, pos.getX() + 1D + x, pos.getY() + 1D, pos.getZ() + 1D + z));
		for (int i = 0; i < list.size(); i++) {
				Entity entity = list.get(i);
				if (entity != null)
					if (entity instanceof EntityPlayer)
						if (!active && animationTicks == 0 && coolDown <= 0)
							setActive(true);
			}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected Entity activateBlock() {
		IBlockState state = getWorld().getBlockState(pos);
		EnumFacing facing = state.getValue(BlockPossessedBlock.FACING);
		float x = 0, z = 0;
		if(facing == EnumFacing.WEST)
			x = -1.25F;
		if(facing == EnumFacing.EAST)
			x = 1.25F;
		if(facing == EnumFacing.NORTH)
			z = -1.25F;
		if(facing == EnumFacing.SOUTH)
			z = 1.25F;
		List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.getX() + x, pos.getY(), pos.getZ() + z, pos.getX() + 1D + x, pos.getY() + 1D, pos.getZ() + 1D + z));
		if (animationTicks == 1)
			for (int i = 0; i < list.size(); i++) {
				Entity entity = list.get(i);
				if (entity != null)
					if (entity instanceof EntityPlayer) {
						int Knockback = 4;
						entity.addVelocity(MathHelper.sin(entity.rotationYaw * 3.141593F / 180.0F) * Knockback * 0.2F, 0.3D, -MathHelper.cos(entity.rotationYaw * 3.141593F / 180.0F) * Knockback * 0.2F);
						((EntityLivingBase) entity).attackEntityFrom(DamageSource.GENERIC, 2);
					}
			}
		return null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("animationTicks", animationTicks);
		nbt.setBoolean("active", active);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		animationTicks = nbt.getInteger("animationTicks");
		active = nbt.getBoolean("active");
	}

	@Override
    public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = new NBTTagCompound();
        return writeToNBT(nbt);
    }

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return new SPacketUpdateTileEntity(pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}
}