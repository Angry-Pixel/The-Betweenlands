package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import thebetweenlands.common.entity.mobs.EntitySporeJet;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class TileEntityPuffshroom extends TileEntity implements ITickable {

	public int animationTicks, prevAnimationTicks, cooldown;
	public boolean active, prevActive;

	@Override
	public void update() {
		prevAnimationTicks = animationTicks;
		prevActive = active;
		if (!getWorld().isRemote && cooldown <= 0)
			findEnemyToAttack();
		if (active) {
			if (getWorld().isRemote && getWorld().isAirBlock(getPos().up())) {
				if (animationTicks < 3) {
					double px = getPos().getX() + 0.5D;
					double py = getPos().getY() + 1.0625D;
					double pz = getPos().getZ() + 0.5D;
					for (int i = 0, amount = 20 + getWorld().rand.nextInt(10); i < amount; i++) {
						double ox = getWorld().rand.nextDouble() * 0.1F - 0.05F;
						double oz = getWorld().rand.nextDouble() * 0.1F - 0.05F;
						double motionX = getWorld().rand.nextDouble() * 0.2F - 0.1F;
						double motionY = getWorld().rand.nextDouble() * 0.1F + 0.075F;
						double motionZ = getWorld().rand.nextDouble() * 0.2F - 0.1F;
						world.spawnParticle(EnumParticleTypes.BLOCK_DUST, px + ox, py, pz + oz, motionX, motionY, motionZ, Block.getStateId(BlockRegistry.MUD_TILES.getDefaultState()));
					}
				}
			}
			if (!getWorld().isRemote && getWorld().isAirBlock(getPos().up())) {
				if (animationTicks == 1) 
					getWorld().playSound((EntityPlayer) null, getPos().getX() + 0.5D, getPos().getY() + 1D, getPos().getZ() + 0.5D, SoundRegistry.PUFF_SHROOM, SoundCategory.BLOCKS, 0.5F, 0.95F + getWorld().rand.nextFloat() * 0.2F);
				if (animationTicks == 12) {
					EntitySporeJet jet = new EntitySporeJet(getWorld());
					jet.setPosition(getPos().getX() + 0.5D, getPos().getY() + 1D, getPos().getZ() + 0.5D);
					getWorld().spawnEntity(jet);
				}
			}
			if (animationTicks <= 16)
				animationTicks++;
			if (animationTicks >= 16)
				if (!getWorld().isRemote)
					setActive(false);
		}
		if (!active) {
			if (animationTicks >= 1)
				animationTicks--;
			if (cooldown > 0)
				cooldown--;
		}
		if (prevActive != active)
			markForUpdate();
	}
	
	public void markForUpdate() {
        IBlockState state = this.getWorld().getBlockState(this.getPos());
        this.getWorld().notifyBlockUpdate(this.getPos(), state, state, 3);
    }

	@SuppressWarnings("unchecked")
	protected Entity findEnemyToAttack() {
		List<EntityLivingBase> list = getWorld().getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(getPos()).grow(2D, 2D, 2D));
			for (int i = 0; i < list.size(); i++) {
				Entity entity = list.get(i);
				if (entity != null)
					if (entity instanceof EntityPlayer)
						if (!active && animationTicks == 0) {
							setActive(true);
							cooldown = 120;
						}
			}
		return null;
	}

	public void setActive(boolean isActive) {
		active = isActive;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("active", active);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
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