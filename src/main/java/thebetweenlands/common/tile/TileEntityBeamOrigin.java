package thebetweenlands.common.tile;

import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.block.structure.BlockBeamLensSupport;
import thebetweenlands.common.block.structure.BlockBeamOrigin;
import thebetweenlands.common.block.structure.BlockBeamRelay;
import thebetweenlands.common.block.structure.BlockBeamTube;
import thebetweenlands.common.block.structure.BlockDiagonalEnergyBarrier;
import thebetweenlands.common.block.structure.BlockEnergyBarrierMud;
import thebetweenlands.common.registries.BlockRegistry;

public class TileEntityBeamOrigin extends TileEntity implements ITickable {
	public boolean active;

	public float prevVisibility = 0.0f;
	public float visibility = 0.0f;

	public float prevRotation = (float)Math.PI / 4;
	public float rotation = (float)Math.PI / 4;

	public TileEntityBeamOrigin() {
		super();
	}

	@Override
	public void update() {
		int litBraziers = this.checkForLitBraziers();
		
		if (getWorld().getBlockState(getPos()).getBlock() != null) {
			if (litBraziers == 4) {
				if (!active)
					setActive(true);
			} else {
				if (active)
					setActive(false);
			}
		}

		if (world.isRemote) {
			if (getWorld().getTotalWorldTime() % 20 == 0) {
				if (checkForLitBrazier(getPos().add(3, -1, 3))) {
					spawnBrazierParticles(new Vec3d(3, -1, 3));
				}

				if (checkForLitBrazier(getPos().add(3, -1, -3))) {
					spawnBrazierParticles(new Vec3d(3, -1, -3));
				}

				if (checkForLitBrazier(getPos().add(-3, -1, 3)))
					spawnBrazierParticles(new Vec3d(-3, -1, 3));

				if (checkForLitBrazier(getPos().add(-3, -1, -3)))
					spawnBrazierParticles(new Vec3d(-3, -1, -3));
			}
		}

		this.prevVisibility = this.visibility;
		this.prevRotation = this.rotation;
		
		this.rotation += litBraziers * 0.0025f;
		
		float targetVisibility = 0.2f + 0.8f * litBraziers / 4.0f;
		
		if(this.visibility < targetVisibility) {
			this.visibility += 0.02f;
			if(this.visibility > targetVisibility) {
				this.visibility = targetVisibility;
			}
		} else if(this.visibility > targetVisibility) {
			this.visibility -= 0.02f;
			if(this.visibility < targetVisibility) {
				this.visibility = targetVisibility;
			}
		}
		
		if (active) {
			activateBlock();
		} else {
			deactivateBlock();
		}
	}

	public int checkForLitBraziers() {
		int braziers = 0;
		if(checkForLitBrazier(getPos().add(3, -1, 3))) braziers++;
		if(checkForLitBrazier(getPos().add(3, -1, -3))) braziers++;
		if(checkForLitBrazier(getPos().add(-3, -1, 3))) braziers++;
		if(checkForLitBrazier(getPos().add(-3, -1, -3))) braziers++;
		return braziers;
	}

	public boolean checkForLitBrazier(BlockPos targetPos) {
		IBlockState flame = getWorld().getBlockState(targetPos);
		if(flame.getBlock() instanceof BlockFire)
			return true;
		return false;
	}

	@SideOnly(Side.CLIENT)
	private void spawnBrazierParticles(Vec3d target) {
		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, BLParticles.PUZZLE_BEAM_2.create(world, this.pos.getX() + 0.5 + target.x, this.pos.getY() + 0.5 + target.y, this.pos.getZ() + 0.5 + target.z, ParticleArgs.get().withMotion(0, 0, 0).withColor(255F, 102F, 0F, 1F).withScale(1.5F).withData(30, target.scale(-1))));
		for(int i = 0; i < 2; i++) {
			float offsetLen = this.world.rand.nextFloat();
			Vec3d offset = new Vec3d(target.x * offsetLen + world.rand.nextFloat() * 0.2f - 0.1f, target.y * offsetLen + world.rand.nextFloat() * 0.2f - 0.1f, target.z * offsetLen + world.rand.nextFloat() * 0.2f - 0.1f);
			float vx = (world.rand.nextFloat() * 2f - 1) * 0.0025f;
			float vy = (world.rand.nextFloat() * 2f - 1) * 0.0025f + 0.008f;
			float vz = (world.rand.nextFloat() * 2f - 1) * 0.0025f;
			float scale = 0.5f + world.rand.nextFloat();
			if(ShaderHelper.INSTANCE.canUseShaders() && world.rand.nextBoolean()) {
				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.HEAT_HAZE_BLOCK_ATLAS, BLParticles.SMOOTH_SMOKE.create(world, this.pos.getX() + 0.5 + offset.x, this.pos.getY() + 0.5 + offset.y, this.pos.getZ() + 0.5 + offset.z, ParticleArgs.get().withMotion(vx, vy, vz).withColor(1, 1, 1, 0.2F).withScale(scale * 8).withData(80, true, 0.0F, true)));
			} else {
				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, this.pos.getX() + 0.5 + offset.x, this.pos.getY() + 0.5 + offset.y, this.pos.getZ() + 0.5 + offset.z, ParticleArgs.get().withMotion(vx, vy, vz).withColor(255F, 102F, 0F, 1F).withScale(scale).withData(100)));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnBeamParticles(Vec3d target) {
		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, BLParticles.PUZZLE_BEAM_2.create(world, this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5, ParticleArgs.get().withMotion(0, 0, 0).withColor(40F, 220F, 130F, 1F).withScale(2.5F).withData(30, target)));
		for(int i = 0; i < 3; i++) {
			float offsetLen = this.world.rand.nextFloat();
			Vec3d offset = new Vec3d(target.x * offsetLen + world.rand.nextFloat() * 0.2f - 0.1f, target.y * offsetLen + world.rand.nextFloat() * 0.2f - 0.1f, target.z * offsetLen + world.rand.nextFloat() * 0.2f - 0.1f);
			float vx = (world.rand.nextFloat() * 2f - 1) * 0.0025f;
			float vy = (world.rand.nextFloat() * 2f - 1) * 0.0025f + 0.008f;
			float vz = (world.rand.nextFloat() * 2f - 1) * 0.0025f;
			float scale = 0.5f + world.rand.nextFloat();
			if(ShaderHelper.INSTANCE.canUseShaders() && world.rand.nextBoolean()) {
				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.HEAT_HAZE_BLOCK_ATLAS, BLParticles.SMOOTH_SMOKE.create(world, this.pos.getX() + 0.5 + offset.x, this.pos.getY() + 0.5 + offset.y, this.pos.getZ() + 0.5 + offset.z, ParticleArgs.get().withMotion(vx, vy, vz).withColor(1, 1, 1, 0.2F).withScale(scale * 8).withData(80, true, 0.0F, true)));
			} else {
				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.PUZZLE_BEAM.create(world, this.pos.getX() + 0.5 + offset.x, this.pos.getY() + 0.5 + offset.y, this.pos.getZ() + 0.5 + offset.z, ParticleArgs.get().withMotion(vx, vy, vz).withColor(40F, 220F, 130F, 1F).withScale(scale).withData(100)));
			}
		}
	}

	public void activateBlock() {
		if(!world.isRemote) {
			IBlockState state = getWorld().getBlockState(getPos());
			if(!state.getValue(BlockBeamOrigin.POWERED))
				getWorld().setBlockState(getPos(), BlockRegistry.MUD_TOWER_BEAM_ORIGIN.getDefaultState().withProperty(BlockBeamOrigin.POWERED, true));
		}

		EnumFacing facing = EnumFacing.DOWN;
		BlockPos targetPos = getPos().offset(facing, getDistanceToObstruction(facing));

		if(world.isRemote) {
			if (getWorld().getTotalWorldTime() % 20 == 0) {
				spawnBeamParticles(new Vec3d(targetPos.getX() - pos.getX(), targetPos.getY() - pos.getY(), targetPos.getZ() - pos.getZ()));
			}
		} else {
			IBlockState stateofTarget = getWorld().getBlockState(targetPos);

			if (stateofTarget.getBlock() instanceof BlockBeamRelay) {
				if (getWorld().getTileEntity(targetPos) instanceof TileEntityBeamRelay) {
					TileEntityBeamRelay targetTile = (TileEntityBeamRelay) getWorld().getTileEntity(targetPos);
					targetTile.setTargetIncomingBeam(facing.getOpposite(), true);
					if (!getWorld().getBlockState(targetPos).getValue(BlockBeamRelay.POWERED)) {
						stateofTarget = stateofTarget.cycleProperty(BlockBeamRelay.POWERED);
						getWorld().setBlockState(targetPos, stateofTarget, 3);
					}
				}
			}
		}
	}

	public void deactivateBlock() {
		IBlockState state = getWorld().getBlockState(getPos());
		if (state.getValue(BlockBeamOrigin.POWERED)) {
			getWorld().setBlockState(getPos(), BlockRegistry.MUD_TOWER_BEAM_ORIGIN.getDefaultState().withProperty(BlockBeamOrigin.POWERED, false));

			EnumFacing facing = EnumFacing.DOWN;
			BlockPos targetPos = getPos().offset(facing, getDistanceToObstruction(facing));
			IBlockState stateofTarget = getWorld().getBlockState(targetPos);

			if (stateofTarget.getBlock() instanceof BlockBeamRelay) {
				if (getWorld().getTileEntity(targetPos) instanceof TileEntityBeamRelay) {
					TileEntityBeamRelay targetTile = (TileEntityBeamRelay) getWorld().getTileEntity(targetPos);
					targetTile.setTargetIncomingBeam(facing.getOpposite(), false);
					if (!targetTile.isGettingBeamed())
						if (getWorld().getBlockState(targetPos).getValue(BlockBeamRelay.POWERED)) {
							stateofTarget = stateofTarget.cycleProperty(BlockBeamRelay.POWERED);
							getWorld().setBlockState(targetPos, stateofTarget, 3);
						}
				}
			}
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public void setActive(boolean isActive) {
		active = isActive;
		getWorld().notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 3);
	}

	public int getDistanceToObstruction(EnumFacing facing) {
		int distance = 0;
		for (distance = 1; distance < 14; distance++) {
			IBlockState state = getWorld().getBlockState(getPos().offset(facing, distance));
			if (state != Blocks.AIR.getDefaultState()
					&& !(state.getBlock() instanceof BlockDiagonalEnergyBarrier) 
					&& !(state.getBlock() instanceof BlockEnergyBarrierMud)
					&& !(state.getBlock() instanceof BlockBeamLensSupport)
					&& !isValidBeamTubeLens(state, facing))
				break;
		}
		return distance;
	}

	private boolean isValidBeamTubeLens(IBlockState state, EnumFacing facing) {
		if(!(state.getBlock() instanceof BlockBeamTube))
			return false;
		if(state.getValue(BlockBeamTube.FACING) == facing)
			return true;
		if(state.getValue(BlockBeamTube.FACING) == facing.getOpposite())
			return true;
		return false;
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
		return new SPacketUpdateTileEntity(getPos(), 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return super.getRenderBoundingBox().grow(10);
	}
}
