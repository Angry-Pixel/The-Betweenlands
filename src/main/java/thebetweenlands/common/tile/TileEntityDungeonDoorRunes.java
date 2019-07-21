package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityScreenShake;
import thebetweenlands.common.block.structure.BlockDungeonDoorRunes;
import thebetweenlands.common.entity.mobs.EntityBarrishee;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.gen.feature.structure.LightTowerBuildParts;

public class TileEntityDungeonDoorRunes extends TileEntity implements ITickable, IEntityScreenShake {
	private LightTowerBuildParts lightTowerBuild = new LightTowerBuildParts();
	private boolean mimic; // true = Barrishee
	
	public int top_code = -1, mid_code = -1, bottom_code = -1; // set back to -1
	public int top_state = 0, mid_state = 0, bottom_state = 0;
	public int top_state_prev = 0, mid_state_prev = 0, bottom_state_prev = 0;
	public int top_rotate = 0, mid_rotate = 0, bottom_rotate = 0;
	public int lastTickTopRotate = 0, lastTickMidRotate = 0, lastTickBottomRotate = 0;
	public int renderTicks = 0;
	public boolean animate_open = false;
	public boolean animate_open_recess = false;
	public boolean animate_tile_recess = false;
	public boolean break_blocks = false;
	public int slate_1_rotate = 0, slate_2_rotate = 0, slate_3_rotate = 0;
	public int last_tick_slate_1_rotate = 0, last_tick_slate_2_rotate = 0, last_tick_slate_3_rotate = 0;
	public int recess_pos = 0;
	public int last_tick_recess_pos = 0;

	public int tile_1_recess_pos = 0;
	public int last_tick_recess_pos_tile_1 = 0;
	public int tile_2_recess_pos = 0;
	public int last_tick_recess_pos_tile_2 = 0;
	public int tile_3_recess_pos = 0;
	public int last_tick_recess_pos_tile_3 = 0;

	private int prev_shake_timer;
	private int shake_timer;
	private boolean shaking = false;
	private boolean falling_shake = false;

	public boolean hide_slate_1 = false;
	public boolean hide_slate_2 = false;
	public boolean hide_slate_3 = false;
	public boolean hide_lock = false;
	public boolean hide_back_wall = false;
	public boolean is_in_dungeon = false;

	public boolean is_gate_entrance = false;

	private final ItemStack renderStack = new ItemStack(BlockRegistry.MUD_TOWER_BEAM_RELAY.getDefaultState().getBlock());

	private static int SHAKING_TIMER_MAX = 240;

	public TileEntityDungeonDoorRunes(boolean mimic) {
		super();
		this.mimic = mimic;
	}

	public TileEntityDungeonDoorRunes() {
		super();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(getPos()).grow(1D);
	}

	public void sinkingParticles(float ySpikeVel) {
		IBlockState state = getWorld().getBlockState(getPos());
		EnumFacing facing = state.getValue(BlockDungeonDoorRunes.FACING);
		if (facing == EnumFacing.WEST || facing == EnumFacing.EAST) {
			for (int z = -1; z <= 1; z++)
				if (getWorld().isRemote)
					spawnSinkingParticles(getPos().add(0, -1, z), 0F + ySpikeVel);
		}

		if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
			for (int x = -1; x <= 1; x++)
				if (getWorld().isRemote)
					spawnSinkingParticles(getPos().add(x, -1, 0), 0F + ySpikeVel);
		}	
	}

	public void crashingParticles(float ySpikeVel) { // used for damaging entities too atm
		IBlockState state = getWorld().getBlockState(getPos());
		EnumFacing facing = state.getValue(BlockDungeonDoorRunes.FACING);
		AxisAlignedBB hitBox = new AxisAlignedBB(getPos().offset(facing, 2)).grow(1D);
		if (facing == EnumFacing.EAST) {
			for (int x = 1; x <= 3; x++)
				for (int z = -1; z <= 1; z++)
					if (getWorld().isRemote)
						spawnCrashingParticles(getPos().add(x, -1, z), 0F + ySpikeVel);
		}

		if (facing == EnumFacing.WEST) {
			for (int x = -1; x >= -3; x--)
				for (int z = -1; z <= 1; z++)
					if (getWorld().isRemote)
						spawnCrashingParticles(getPos().add(x, -1, z), 0F + ySpikeVel);
		}

		if (facing == EnumFacing.SOUTH) {
			for (int x = -1; x <= 1; x++)
				for (int z = 1; z <= 3; z++)
					if (getWorld().isRemote)
						spawnCrashingParticles(getPos().add(x, -1, z), 0F + ySpikeVel);
		}

		if (facing == EnumFacing.NORTH) {
			for (int x = -1; x <= 1; x++)
				for (int z = -1; z >= -3; z--)
					if (getWorld().isRemote)
						spawnCrashingParticles(getPos().add(x, -1, z), 0F + ySpikeVel);
		}

		List<EntityLivingBase> list = getWorld().getEntitiesWithinAABB(EntityLivingBase.class, hitBox);
		for (int i = 0; i < list.size(); i++) {
			Entity entity = list.get(i);
			if (entity != null)
				if (entity instanceof EntityLivingBase && !(entity instanceof EntityBarrishee))
					if (!getWorld().isRemote)
						entity.attackEntityFrom(DamageSource.FALLING_BLOCK, 10F); // dunno what damage to do yet...
		}
	}

	//TODO shrink all this in to 1 method and eventually in to BL particles
	private void spawnSinkingParticles(BlockPos pos, float ySpikeVel) {
		if (getWorld().isRemote) {
			double px = pos.getX() + 0.5D;
			double py = pos.getY() + 0.0625D;
			double pz = pos.getZ() + 0.5D;
			for (int i = 0, amount = 2 + getWorld().rand.nextInt(2); i < amount; i++) {
				double ox = getWorld().rand.nextDouble() * 0.1F - 0.05F;
				double oz = getWorld().rand.nextDouble() * 0.1F - 0.05F;
				double motionX = getWorld().rand.nextDouble() * 0.2F - 0.1F;
				double motionY = getWorld().rand.nextDouble() * 0.1F + 0.075F + ySpikeVel;
				double motionZ = getWorld().rand.nextDouble() * 0.2F - 0.1F;
				world.spawnParticle(EnumParticleTypes.BLOCK_DUST, px + ox, py, pz + oz, motionX, motionY, motionZ, Block.getStateId(BlockRegistry.MUD_TILES.getDefaultState()));
			}
		}
	}

	private void spawnCrashingParticles(BlockPos pos, float ySpikeVel) {
		if (getWorld().isRemote) {
			double px = pos.getX() + 0.5D;
			double py = pos.getY() + 0.0625D;
			double pz = pos.getZ() + 0.5D;
			for (int i = 0, amount = 2 + getWorld().rand.nextInt(2); i < amount; i++) {
				double ox = getWorld().rand.nextDouble() * 0.1F - 0.05F;
				double oz = getWorld().rand.nextDouble() * 0.1F - 0.05F;
				double motionX = getWorld().rand.nextDouble() * 0.2F - 0.1F;
				double motionY = getWorld().rand.nextDouble() * 0.025F + 0.075F;
				double motionZ = getWorld().rand.nextDouble() * 0.2F - 0.1F;
				world.spawnParticle(EnumParticleTypes.BLOCK_DUST, px + ox, py, pz + oz, motionX, motionY + ySpikeVel, motionZ, Block.getStateId(BlockRegistry.MUD_BRICKS.getDefaultState()));
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, px + ox, py, pz + oz, motionX, 0D, motionZ);
			}
		}
	}

	@Override
	public void update() {
		renderTicks++;

		lastTickTopRotate = top_rotate;
		lastTickMidRotate = mid_rotate;
		lastTickBottomRotate = bottom_rotate;

		last_tick_slate_1_rotate = slate_1_rotate;
		last_tick_slate_2_rotate = slate_2_rotate;
		last_tick_slate_3_rotate = slate_3_rotate;

		last_tick_recess_pos = recess_pos;
		
		last_tick_recess_pos_tile_1 = tile_1_recess_pos;
		last_tick_recess_pos_tile_2 = tile_2_recess_pos;
		last_tick_recess_pos_tile_3 = tile_3_recess_pos;

		if (top_state_prev != top_state) {
			top_rotate += 4;
			if (top_rotate > 90) {
				lastTickTopRotate = top_rotate = 0;
				top_state_prev = top_state;
			}
		}

		if (mid_state_prev != mid_state) {
			mid_rotate += 4;
			if (mid_rotate > 90) {
				lastTickMidRotate = mid_rotate = 0;
				mid_state_prev = mid_state;
			}
		}

		if (bottom_state_prev != bottom_state) {
			bottom_rotate += 4;
			if (bottom_rotate > 90) {
				lastTickBottomRotate = bottom_rotate = 0;
				bottom_state_prev = bottom_state;
			}
		}

		if (animate_open_recess) {
			if (recess_pos <= 0)
				if (!getWorld().isRemote)
					playOpenRecessSound(true);
			shake(240);
			recess_pos += 1;
			int limit = 30;
			if (recess_pos > limit) {
				last_tick_recess_pos = recess_pos = limit;
				if (!getWorld().isRemote) {
					animate_open_recess = false;
					if(!animate_open) {
						animate_open = true;
						getWorld().notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 3);
					}
				}
			}
		}

		if (animate_tile_recess) {
			if (!mimic) {
				if(tile_1_recess_pos <= 0)
					if (!getWorld().isRemote)
						playOpenRecessSound(false);
				if(tile_2_recess_pos == 2)
					if (!getWorld().isRemote)
						playOpenRecessSound(false);
				if(tile_3_recess_pos == 2)
					if (!getWorld().isRemote)
						playOpenRecessSound(false);
				tile_1_recess_pos += 2;
				if(tile_1_recess_pos >= 20)
					tile_2_recess_pos += 2;
				if(tile_2_recess_pos >= 20)
					tile_3_recess_pos += 2;
			}
			int limit = 60;	
			if (tile_1_recess_pos >= limit)
				last_tick_recess_pos_tile_1 = tile_1_recess_pos = limit;

			if (tile_2_recess_pos >= limit)
				last_tick_recess_pos_tile_2 = tile_2_recess_pos = limit;

			if (tile_3_recess_pos >= limit) {
				last_tick_recess_pos_tile_3 = tile_3_recess_pos = limit;
				if (!getWorld().isRemote) {
					break_blocks = true;
					getWorld().notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 3);
				}
			}
		}

		if (animate_open) {
			if (mimic) {
				if (slate_1_rotate <= 0)
					if (!getWorld().isRemote) {
						playTrapFallingSound();

						IBlockState state = getWorld().getBlockState(getPos());
						EnumFacing facing = state.getValue(BlockDungeonDoorRunes.FACING);

						//TODO make this shit work properly
						BlockPos offsetPos = getPos().offset(facing.getOpposite());
						EntityBarrishee entity = new EntityBarrishee(getWorld()); // door golem here :P
						entity.setLocationAndAngles(offsetPos.getX() + 0.5D, offsetPos.down().getY(), offsetPos.getZ() + 0.5D, 0F, 0.0F);
						entity.rotationYawHead = entity.rotationYaw;
						entity.renderYawOffset = entity.rotationYaw;
						entity.setIsAmbushSpawn(true);
						entity.setIsScreaming(true);
						entity.setScreamTimer(0);
						getWorld().spawnEntity(entity);
						
					}
				slate_1_rotate += 4 + (last_tick_slate_1_rotate < 8 ? 0 : last_tick_slate_1_rotate / 8);
				slate_2_rotate += 2 + (last_tick_slate_2_rotate < 6 ? 0 : last_tick_slate_2_rotate / 6);
				slate_3_rotate += 2 + (last_tick_slate_3_rotate < 8 ? 0 : last_tick_slate_3_rotate / 8);
				hide_lock = true;
				hide_back_wall = true;
			}
			if (!mimic) {
				if (slate_1_rotate == 0)
					if (!getWorld().isRemote) {
						playOpenSinkingSound();
						if(is_gate_entrance) {
							lightTowerBuild.destroyGateBeamLenses(getWorld(), getPos());
							lightTowerBuild.destroyTowerBeamLenses(getWorld(), getPos().add(-15, -2, -14)); // centre stone of tower  bottom floor
						}
					}
				slate_1_rotate += 4;
				slate_2_rotate += 3;
				slate_3_rotate += 3;
			}
			int limit = mimic ? 90 : 360;
			if(!mimic)
				if (slate_3_rotate < limit - 6)
					sinkingParticles(0F);
				else
					sinkingParticles(0.25F);	
			if (slate_1_rotate >= limit) {
				if (mimic) {
					falling_shake = true;
					crashingParticles(0.125F);
					hide_slate_1 = true;
				}
				last_tick_slate_1_rotate = slate_1_rotate = limit;
			}
			if (slate_2_rotate >= limit) {
				if (mimic) {
					crashingParticles(0.125F);
					hide_slate_2 = true;
				}
				last_tick_slate_2_rotate = slate_2_rotate = limit;
			}
			if (slate_3_rotate >= limit) {
				if (mimic) {
					crashingParticles(0.125F);
					hide_slate_3 = true;
				}
				if(!mimic) {
					hide_slate_1 = true;
					hide_slate_2 = true;
					hide_slate_3 = true;
					hide_lock = true;
					hide_back_wall = true;
				}
				last_tick_slate_3_rotate = slate_3_rotate = limit;
				if (!getWorld().isRemote) {
					if(mimic)
						break_blocks = true;
					if(!mimic)
						if(is_in_dungeon)
							animate_tile_recess = true;
						else
							break_blocks = true;
					animate_open = false;
					getWorld().notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 3);
				}
			}
			if (falling_shake) {
				shake(10);
				falling_shake = shaking;
			}
		}

		if (!getWorld().isRemote) {
			IBlockState state = getWorld().getBlockState(getPos());
			EnumFacing facing = state.getValue(BlockDungeonDoorRunes.FACING);
			if (top_state_prev == top_code && mid_state_prev == mid_code && bottom_state_prev == bottom_code) {
				if(!mimic) {
					if (!animate_open_recess) {
						animate_open_recess = true;
						getWorld().notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()),getWorld().getBlockState(getPos()), 3);
					}
				}
				else {
					if (!animate_open) {
						animate_open = true;
						getWorld().notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()),getWorld().getBlockState(getPos()), 3);
					}
				}
			}
			if (break_blocks) {
				if (!mimic)
					if(is_in_dungeon)
						breakAllDoorBlocks(state, facing, true, false);
					else
						breakAllDoorBlocks(state, facing, false, false);
				else {
					breakAllDoorBlocks(state, facing, false, false);
				}
			}

			if (getWorld().getTotalWorldTime() % 5 == 0)
				checkComplete(state, facing);
		}
	}

	public void shake(int shakeTimerMax) {
		SHAKING_TIMER_MAX = shakeTimerMax;
		prev_shake_timer = shake_timer;
		if(shake_timer == 0) {
			shaking = true;
			shake_timer = 1;
		}
		if(shake_timer > 0)
			shake_timer++;

		if(shake_timer >= SHAKING_TIMER_MAX)
			shaking = false;
		else
			shaking = true;
	}

	private void checkComplete(IBlockState state, EnumFacing facing) {
		if (facing == EnumFacing.WEST || facing == EnumFacing.EAST) {
			for (int z = -1; z <= 1; z++)
				for (int y = -1; y <= 1; y++)
					if (!(getWorld().getBlockState(getPos().add(0, y, z)).getBlock() instanceof BlockDungeonDoorRunes))
						breakAllDoorBlocks(state, facing, false, true);
		}

		if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
			for (int x = -1; x <= 1; x++)
				for (int y = -1; y <= 1; y++)
					if (!(getWorld().getBlockState(getPos().add(x, y, 0)).getBlock() instanceof BlockDungeonDoorRunes))
						breakAllDoorBlocks(state, facing, false, true);
		}
	}

	public void breakAllDoorBlocks(IBlockState state, EnumFacing facing,  boolean breakFloorBelow, boolean particles) {
		if (facing == EnumFacing.WEST || facing == EnumFacing.EAST) {
			for (int z = -1; z <= 1; z++)
				for (int y = breakFloorBelow ? -2 : -1; y <= 1; y++)
					if (particles) {
						getWorld().destroyBlock(getPos().add(0, y, z), false);
						getWorld().removeTileEntity(getPos());
					}
					else {
						getWorld().setBlockState(getPos().add(0, y, z), Blocks.AIR.getDefaultState(), 3);
						getWorld().removeTileEntity(getPos());
					}
		}

		if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) {
			for (int x = -1; x <= 1; x++)
				for (int y = breakFloorBelow ? -2 : -1; y <= 1; y++)
					if (particles) {
						getWorld().destroyBlock(getPos().add(x, y, 0), false);
						getWorld().removeTileEntity(getPos());
					}
					else {
						getWorld().setBlockState(getPos().add(x, y, 0), Blocks.AIR.getDefaultState(), 3);
						getWorld().removeTileEntity(getPos());
					}
		}
	}

	public void cycleTopState() {
		top_state_prev = top_state;
		top_state++;
		if (top_state > 7)
			top_state = 0;
		this.markDirty();
		playLockSound();
	}

	public void cycleMidState() {
		mid_state_prev = mid_state;
		mid_state++;
		if (mid_state > 7)
			mid_state = 0;
		this.markDirty();
		playLockSound();
	}

	public void cycleBottomState() {
		bottom_state_prev = bottom_state;
		bottom_state++;
		if (bottom_state > 7)
			bottom_state = 0;
		this.markDirty();
		playLockSound();
	}
	
	public void enterLockCode() {
		top_code = top_state;
		mid_code = mid_state;
		bottom_code = bottom_state;
		top_state_prev = top_state = 0;
		mid_state_prev = mid_state = 0;
		bottom_state_prev = bottom_state = 0;
		getWorld().playSound(null, getPos(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 1F, 1.0F);
		this.markDirty();
	}

	private void playLockSound() {
		getWorld().playSound(null, getPos(), SoundRegistry.MUD_DOOR_LOCK, SoundCategory.BLOCKS, 1F, 1.0F);
	}

	private void playOpenRecessSound(boolean isBigDoor) {
		getWorld().playSound(null, getPos(), SoundRegistry.MUD_DOOR_1, SoundCategory.BLOCKS, isBigDoor ? 1F : 0.5F, 1.0F);
	}

	private void playOpenSinkingSound() {
		getWorld().playSound(null, getPos(), SoundRegistry.MUD_DOOR_2, SoundCategory.BLOCKS, 1F, 0.9F);
	}

	private void playTrapFallingSound() {
		getWorld().playSound(null, getPos(), SoundRegistry.MUD_DOOR_TRAP, SoundCategory.BLOCKS, 1F, 1.25F);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		top_code = nbt.getInteger("top_code");
		mid_code = nbt.getInteger("mid_code");
		bottom_code = nbt.getInteger("bottom_code");
		top_state = nbt.getInteger("top_state");
		mid_state = nbt.getInteger("mid_state");
		bottom_state = nbt.getInteger("bottom_state");
		top_state_prev = nbt.getInteger("top_state_prev");
		mid_state_prev = nbt.getInteger("mid_state_prev");
		bottom_state_prev = nbt.getInteger("bottom_state_prev");
		mimic = nbt.getBoolean("mimic");
		animate_open = nbt.getBoolean("animate_open");
		animate_open_recess = nbt.getBoolean("animate_open_recess");
		animate_tile_recess = nbt.getBoolean("animate_tile_recess");
		break_blocks = nbt.getBoolean("break_blocks");
		hide_slate_1 = nbt.getBoolean("hide_slate_1");
		hide_slate_2 = nbt.getBoolean("hide_slate_2");
		hide_slate_3 = nbt.getBoolean("hide_slate_3");
		hide_lock = nbt.getBoolean("hide_lock");
		hide_back_wall = nbt.getBoolean("hide_back_wall");
		is_in_dungeon = nbt.getBoolean("is_in_dungeon");
		is_gate_entrance = nbt.getBoolean("is_gate_entrance");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("top_code", top_code);
		nbt.setInteger("mid_code", mid_code);
		nbt.setInteger("bottom_code", bottom_code);
		nbt.setInteger("top_state", top_state);
		nbt.setInteger("mid_state", mid_state);
		nbt.setInteger("bottom_state", bottom_state);
		nbt.setInteger("top_state_prev", top_state_prev);
		nbt.setInteger("mid_state_prev", mid_state_prev);
		nbt.setInteger("bottom_state_prev", bottom_state_prev);
		nbt.setBoolean("mimic", mimic);
		nbt.setBoolean("animate_open", animate_open);
		nbt.setBoolean("animate_open_recess", animate_open_recess);
		nbt.setBoolean("animate_tile_recess", animate_tile_recess);
		nbt.setBoolean("break_blocks", break_blocks);
		nbt.setBoolean("hide_slate_1", hide_slate_1);
		nbt.setBoolean("hide_slate_2", hide_slate_2);
		nbt.setBoolean("hide_slate_3", hide_slate_3);
		nbt.setBoolean("hide_lock", hide_lock);
		nbt.setBoolean("hide_back_wall", hide_back_wall);
		nbt.setBoolean("is_in_dungeon", is_in_dungeon);
		nbt.setBoolean("is_gate_entrance", is_gate_entrance);
		return nbt;
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

	@Override
	public float getShakeIntensity(Entity viewer, float partialTicks) {
		if(isShaking()) {
			double dist = getDistance(viewer);
			float shakeMult = (float) (1.0F - dist / 10.0F);
			if(dist >= 10.0F) {
				return 0.0F;
			}
			return (float) ((Math.sin(getShakingProgress(partialTicks) * Math.PI) + 0.1F) * 0.075F * shakeMult);
		} else {
			return 0.0F;
		}
	}

    public float getDistance(Entity entity) {
        float distX = (float)(getPos().getX() - entity.getPosition().getX());
        float distY = (float)(getPos().getY() - entity.getPosition().getY());
        float distZ = (float)(getPos().getZ() - entity.getPosition().getZ());
        return MathHelper.sqrt(distX  * distX  + distY * distY + distZ * distZ);
    }

	public boolean isShaking() {
		return shaking;
	}

	public float getShakingProgress(float delta) {
		return 1.0F / SHAKING_TIMER_MAX * (prev_shake_timer + (shake_timer - prev_shake_timer) * delta);
	}

	public boolean isMimic() {
		return this.mimic;
	}

	public ItemStack cachedStack() {
		return renderStack;
	}
}
