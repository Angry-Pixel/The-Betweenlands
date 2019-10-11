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

	public int animation_1 = 0, prev_animation_1 = 0, cooldown = 0;
	public int animation_2 = 0, prev_animation_2 = 0;
	public int animation_3 = 0, prev_animation_3 = 0;
	public int animation_4 = 0, prev_animation_4 = 0;
	public boolean active_1 = false, active_2 = false, active_3 = false, active_4 = false, active_5 = false, pause = true;
	public int renderTicks = 0, prev_renderTicks = 0, pause_count = 30;

	@Override
	public void update() {

		prev_animation_1 = animation_1;
		prev_animation_2 = animation_2;
		prev_animation_3 = animation_3;
		prev_animation_4 = animation_4;
		prev_renderTicks = renderTicks;

		if (!getWorld().isRemote && cooldown <= 0 && getWorld().getTotalWorldTime()%5 == 0)
			findEnemyToAttack();

		if (active_1 || active_5) {
			if (getWorld().isRemote) {
				if (animation_1 < 3) {
					double px = getPos().getX() + 0.5D;
					double py = getPos().getY() + 1.0625D;
					double pz = getPos().getZ() + 0.5D;
					for (int i = 0, amount = 5 + getWorld().rand.nextInt(2); i < amount; i++) {
						double ox = getWorld().rand.nextDouble() * 0.1F - 0.05F;
						double oz = getWorld().rand.nextDouble() * 0.1F - 0.05F;
						double motionX = getWorld().rand.nextDouble() * 0.2F - 0.1F;
						double motionY = getWorld().rand.nextDouble() * 0.1F + 0.075F;
						double motionZ = getWorld().rand.nextDouble() * 0.2F - 0.1F;
						world.spawnParticle(EnumParticleTypes.BLOCK_DUST, px + ox, py, pz + oz, motionX, motionY, motionZ, Block.getStateId(BlockRegistry.MUD_TILES.getDefaultState()));

					}
				}
			}
		}

		if (!getWorld().isRemote) {
			if (active_4) {
				if (animation_4 <= 1)
					getWorld().playSound((EntityPlayer) null, getPos().getX() + 0.5D, getPos().getY() + 1D, getPos().getZ() + 0.5D, SoundRegistry.PUFF_SHROOM, SoundCategory.BLOCKS, 0.5F, 0.95F + getWorld().rand.nextFloat() * 0.2F);
				if (animation_4 == 10) {
					EntitySporeJet jet = new EntitySporeJet(getWorld());
					jet.setPosition(getPos().getX() + 0.5D, getPos().getY() + 1D, getPos().getZ() + 0.5D);
					getWorld().spawnEntity(jet);
				}
			}
		}

		if (active_1) {
			if (animation_1 <= 8)
				animation_1++;
			if (animation_1 > 8) {
				prev_animation_1 = animation_1 = 8;
				active_2 = true;
				active_1 = false;
			}
		}

		if (active_2) {
			if (animation_2 <= 8)
				animation_2++;
			if (animation_2 == 8)
				active_3 = true;
			if (animation_2 > 8) {
				prev_animation_2 = animation_2 = 8;
				active_2 = false;
			}
		}

		if (active_3) {
			if (animation_3 <= 8)
				animation_3++;
			if (animation_3 > 8) {
				prev_animation_3 = animation_3 = 8;
				active_3 = false;
				active_4 = true;
			}
		}

		if (active_4) {
			if (animation_4 <= 12)
				animation_4++;
			if (animation_4 > 12) {
				prev_animation_4 = animation_4 = 12;
				active_4 = false;
			}
		}

		if (pause) {
			if (animation_4 >= 12) {
				if (pause_count > 0)
					pause_count--;
				if (pause_count <= 0) {
					pause = false;
					pause_count = 30;
					active_5 = true;
				}
			}
		}

		if (active_5) {
			prev_animation_4 = animation_4 = 0;
			if (animation_1 >= 0)
				animation_3--;
			if (animation_3 <= 0)
				animation_2--;
			if (animation_2 <= 0)
				animation_1--;
			if (animation_3 <= 0)
				prev_animation_3 = animation_3 = 0;
			if (animation_2 <= 0)
				prev_animation_2 = animation_2 = 0;
			if (animation_1 <= 0) {
				prev_animation_1 = animation_1 = 0;
				active_5 = false;
			}
		}

		if (cooldown >= 0)
			cooldown--;
		if (cooldown < 0)
			cooldown = 0;

		renderTicks++;
	}

	public void markForUpdate() {
        IBlockState state = this.getWorld().getBlockState(this.getPos());
        this.getWorld().notifyBlockUpdate(this.getPos(), state, state, 3);
    }

	protected Entity findEnemyToAttack() {
		if(!active_1 && animation_1 == 0) {
			List<EntityPlayer> list = getWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(getPos()).grow(2D, 2D, 2D));
			for(EntityPlayer player : list) {
				if (!player.isCreative() && !player.isSpectator()) {
					active_1 = true;
					cooldown = 120;
					pause = true;
					markForUpdate();
					return player;
				}
			}
		}
		return null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("active_1", active_1);
		nbt.setBoolean("active_2", active_2);
		nbt.setBoolean("active_3", active_3);
		nbt.setBoolean("active_4", active_4);
		nbt.setBoolean("active_5", active_5);
		nbt.setBoolean("pause", pause);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		active_1 = nbt.getBoolean("active_1");
		active_2 = nbt.getBoolean("active_2");
		active_3 = nbt.getBoolean("active_3");
		active_4 = nbt.getBoolean("active_4");
		active_5 = nbt.getBoolean("active_5");
		pause = nbt.getBoolean("pause");
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
		return new SPacketUpdateTileEntity(getPos(), 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}
}