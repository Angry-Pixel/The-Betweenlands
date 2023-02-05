package thebetweenlands.common.entity.mobs;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.entity.ParticleGasCloud;
import thebetweenlands.common.entity.EntityFragSpore;
import thebetweenlands.common.entity.EntityShockwaveBlock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityBigPuffshroom extends EntityLiving {
	
	public int animation_1 = 0, prev_animation_1 = 0, cooldown = 0;
	public int animation_2 = 0, prev_animation_2 = 0;
	public int animation_3 = 0, prev_animation_3 = 0;
	public int animation_4 = 0, prev_animation_4 = 0;
	public boolean active_1 = false, active_2 = false, active_3 = false, active_4 = false, active_5 = false, pause = true;
	public int renderTicks = 0, prev_renderTicks = 0, pause_count = 30;
	
	private static final DataParameter<Boolean> SLAM_ATTACK = EntityDataManager.createKey(EntityBigPuffshroom.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> MOVE = EntityDataManager.createKey(EntityBigPuffshroom.class, DataSerializers.BOOLEAN);

	public EntityBigPuffshroom(World world) {
		super(world);
		setSize(3F, 1.8F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SLAM_ATTACK, false);
		dataManager.register(MOVE, false);
	}
	
	public void setSlam(boolean state) {
		dataManager.set(SLAM_ATTACK, state);
	}

	public boolean getSlam() {
		return dataManager.get(SLAM_ATTACK);
	}
	
	public void setMove(boolean state) {
		dataManager.set(MOVE, state);
	}

	public boolean getMove() {
		return dataManager.get(MOVE);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
	}

	@Override
	public void onUpdate() {
		
		prev_animation_1 = animation_1;
		prev_animation_2 = animation_2;
		prev_animation_3 = animation_3;
		prev_animation_4 = animation_4;
		prev_renderTicks = renderTicks;

		if (/*!getEntityWorld().isRemote && */cooldown <= 0 && getEntityWorld().getTotalWorldTime()%5 == 0) {
			findEnemyToAttack();
		}
			

		if (active_1 || active_5) {
			if (getEntityWorld().isRemote) {
				if (animation_1 < 3) {
					double px = getPosition().getX() + 0.5D;
					double py = getPosition().getY() + 0.0625D;
					double pz = getPosition().getZ() + 0.5D;
					for (int i = 0, amount = 10 + getEntityWorld().rand.nextInt(4); i < amount; i++) {
						double ox = getEntityWorld().rand.nextDouble() * 2F - 1F;
						double oz = getEntityWorld().rand.nextDouble() * 2F - 1F;
						double motionX = getEntityWorld().rand.nextDouble() * 0.4F - 0.2F;
						double motionY = getEntityWorld().rand.nextDouble() * 0.3F + 0.075F;
						double motionZ = getEntityWorld().rand.nextDouble() * 0.4F - 0.2F;
						world.spawnParticle(EnumParticleTypes.BLOCK_DUST, px + ox, py, pz + oz, motionX, motionY, motionZ, Block.getStateId(BlockRegistry.MOULDY_SOIL.getDefaultState()));

					}
				}
			}
		}

		if (!getEntityWorld().isRemote) {
			//TODO Set a boolean state so the attack uses the right animations
			
			if (active_4) {
				if (getRangeToPlayer() > 5) {
					setSlam(false);
					//if (animation_4 <= 1)
					//	getEntityWorld().playSound(null, getPosition().getX() + 0.5D, getPosition().getY() + 1D, getPosition().getZ() + 0.5D, SoundRegistry.PUFF_SHROOM, SoundCategory.BLOCKS, 0.5F, 0.95F + getEntityWorld().rand.nextFloat() * 0.2F);
					if (animation_4 == 10) {
						if (rand.nextBoolean())
							spawnFragSpores(getPosition());
						else
							spawnSporeMinions(world, getPosition());
						getEntityWorld().playSound(null, getPosition().getX() + 0.5D, getPosition().getY() + 1D, getPosition().getZ() + 0.5D, SoundRegistry.PUFF_SHROOM, SoundCategory.BLOCKS, 0.5F, 0.95F + getEntityWorld().rand.nextFloat() * 0.2F);
					}
				}

				if (getRangeToPlayer() <= 5) {
					setSlam(true);
					//if (animation_4 <= 1)
					//	getEntityWorld().playSound(null, getPosition().getX() + 0.5D, getPosition().getY() + 1D, getPosition().getZ() + 0.5D, SoundRegistry.PUFF_SHROOM, SoundCategory.BLOCKS, 0.5F, 0.95F + getEntityWorld().rand.nextFloat() * 0.2F);
					if (animation_4 == 10) {
						getEntityWorld().playSound(null, getPosition(), SoundRegistry.WALL_SLAM, SoundCategory.HOSTILE, 1F, 1F);
						slamAttack(world, getPosition());
						
					}
				}
			}

			if (!active_5)
				if (getMove()) {
					BlockPos moveTo = new BlockPos(findNewSurfaceLocation(world, getPosition().down()));
					setPositionAndUpdate(moveTo.getX() + 0.5D, moveTo.getY() + 1, moveTo.getZ() + 0.5D);
					setMove(false);
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
				setMove(true);
			}
		}

		if (cooldown >= 0)
			cooldown--;
		if (cooldown < 0)
			cooldown = 0;

		renderTicks++;
		super.onUpdate();
	}

	private BlockPos findNewSurfaceLocation(World world, BlockPos pos) {
		List<BlockPos> allViableBlocks = new ArrayList<BlockPos>();
		List<BlockPos> posRandom = new ArrayList<BlockPos>();
		IBlockState mouldySoil = BlockRegistry.MOULDY_SOIL.getDefaultState();
		for (int x = -16; x <= 16; x++)
			for (int z = -16; z <= 16; z++)
				if (world.getBlockState(pos.add(x, 0, z)).getBlock() == mouldySoil.getBlock())
					allViableBlocks.add(pos.add(x, 0, z));

		if (!allViableBlocks.isEmpty())
			for (BlockPos goodBlocks : allViableBlocks)
				if (checkForAdjacentMouldyDirt(world, goodBlocks, 1, 1, mouldySoil))
					posRandom.add(goodBlocks);

		if (!posRandom.isEmpty()) {
			Collections.shuffle(posRandom);
			return posRandom.get(0);
		}
		return pos;
	}

	private boolean checkForAdjacentMouldyDirt(World world, BlockPos pos, int offSetXIn, int offSetZIn, IBlockState state) {
		int count = 0;
		for(int offsetX = -offSetXIn; offsetX <= offSetXIn; offsetX++)
			for(int offsetZ = -offSetZIn; offsetZ <= offSetZIn; offsetZ++)
				if (isMouldyDirtAdjacent(world.getBlockState(pos.add(offsetX, 0, offsetZ)), state)) {
					count ++;
				}
		return count >= 8;
	}

	private boolean isMouldyDirtAdjacent(IBlockState posState, IBlockState stateChecked) {
        return posState.getBlock() == stateChecked.getBlock();
	}

	private void slamAttack(World world, BlockPos pos) {
		for (int x = 0; x < 12; x++) {
			for (int d = 0; d < 6; d++) {
			double angle = Math.toRadians(x * 30D);
			double offSetX = Math.floor(-Math.sin(angle) * (2D + d));
			double offSetZ = Math.floor(Math.cos(angle) * (2D + d));

			BlockPos origin = new BlockPos(pos.add(offSetX, -1, offSetZ));
			IBlockState block = world.getBlockState(origin);

			if (block.isNormalCube()
					&& !block.getBlock().hasTileEntity(block)
					&& block.getBlockHardness(world, origin) <= 5.0F && block.getBlockHardness(world, origin) >= 0.0F
					&& world.getBlockState(origin).isOpaqueCube()) {

				EntityShockwaveBlock shockwaveBlock = new EntityShockwaveBlock(world);
				shockwaveBlock.setOrigin(origin, 5 + d * 2, origin.getX() + 0.5D, origin.getZ() + 0.5D, this);
				shockwaveBlock.setLocationAndAngles(origin.getX() + 0.5D, origin.getY(), origin.getZ() + 0.5D, 0.0F, 0.0F);
				shockwaveBlock.setBlock(Block.getBlockById(Block.getIdFromBlock(world.getBlockState(origin).getBlock())), world.getBlockState(origin).getBlock().getMetaFromState(world.getBlockState(origin)));
				world.spawnEntity(shockwaveBlock);
			}
		}
		}
	}

	private float getRangeToPlayer() {
		List<EntityPlayer> list = getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(getPosition()).grow(8D, 2D, 8D));
		for (EntityPlayer player : list)
			if (/* !player.isCreative() && */ !player.isSpectator())
				return getDistance(player);
		return 5F;
	}
	
	private void spawnSporeMinions(World world, BlockPos pos) {
		for (int x = 0; x < 4; x++) {
			double angle = Math.toRadians(x * 90D);
			double offSetX = Math.floor(-Math.sin(angle) * 1D);
			double offSetZ = Math.floor(Math.cos(angle) * 1D);

			if(getEntityWorld().isAirBlock(pos.add(offSetX, 1, offSetZ))) {
				EntitySporeMinion spore = new EntitySporeMinion(world);
				spore.setLocationAndAngles(posX + offSetX, posY + 1.8D, posZ + offSetZ, world.rand.nextFloat() * 360, 0);
				Vec3d vector3d = new Vec3d(this.posX, this.posY, this.posZ);
				double velX = pos.getX() + offSetX * 4D - spore.posX;
				double velY = pos.getY() + 6D - spore.posY;
				double velZ = pos.getZ() + offSetZ * 4D - spore.posZ;
				double distanceSqRt = (double) MathHelper.sqrt(velX * velX + velY * velY + velZ * velZ);
				double accelerationX = velX / distanceSqRt * 0.3D + rand.nextDouble() * 0.2D;
				double accelerationY = velY / distanceSqRt * 0.5D + rand.nextDouble() * 0.5D;
				double accelerationZ = velZ / distanceSqRt * 0.3D + rand.nextDouble() * 0.2D;
				vector3d.add(accelerationX, accelerationY, accelerationZ).scale((double) 0.9F);
				spore.addVelocity(accelerationX, accelerationY, accelerationZ);
				world.spawnEntity(spore);
			}
		}
	}

	protected Entity findEnemyToAttack() {
		if(!active_1 && animation_1 == 0) {
			List<EntityPlayer> list = getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(getPosition()).grow(8D, 2D, 8D));
			for(EntityPlayer player : list) {
				if (/*!player.isCreative() &&*/ !player.isSpectator()) {
					active_1 = true;
					cooldown = 120;
					pause = true;
					return player;
				}
			}
		}
		return null;
	}
	
	private void spawnFragSpores(BlockPos pos) {
		for (int x = 0; x < 6; x++) {
			double angle = Math.toRadians(x * 60D);
			double offSetX = Math.floor(-Math.sin(angle) * 2D);
			double offSetZ = Math.floor(Math.cos(angle) * 2D);
			EntityFragSpore fragSpore = new EntityFragSpore(getEntityWorld());
			fragSpore.setPosition(Math.floor(pos.getX() + 0.5D + offSetX), pos.getY() + 2D, Math.floor(pos.getZ() + 0.5D + offSetZ));
			Vec3d vector3d = new Vec3d(this.posX, this.posY, this.posZ);
			double velX = pos.getX() + offSetX * 3D - fragSpore.posX;
			double velY = pos.getY() + 6D - fragSpore.posY;
			double velZ = pos.getZ() + offSetZ * 3D - fragSpore.posZ;
			double distanceSqRt = (double) MathHelper.sqrt(velX * velX + velY * velY + velZ * velZ);
			double accelerationX = velX / distanceSqRt * 0.3D + rand.nextDouble() * 0.2D;
			double accelerationY = velY / distanceSqRt * 0.5D + rand.nextDouble() * 0.5D;
			double accelerationZ = velZ / distanceSqRt * 0.3D + rand.nextDouble() * 0.2D;
			vector3d.add(accelerationX, accelerationY, accelerationZ).scale((double) 0.9F);
			fragSpore.addVelocity(accelerationX, accelerationY, accelerationZ);
			getEntityWorld().spawnEntity(fragSpore);
		}
	}

    public boolean isWearingSilkMask(EntityLivingBase entity) {
    	if(entity instanceof EntityPlayer) {
        	ItemStack helmet = ((EntityPlayer)entity).getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        	if(!helmet.isEmpty() && helmet.getItem() == ItemRegistry.SILK_MASK) {
        		return true;
        	}
        }
    	return false;
    }

	@Override
	protected boolean isMovementBlocked() {
		return true;
	}

	@Override
    public boolean canBePushed() {
        return false;
    }

	@Override
    public boolean canBeCollidedWith() {
        return true;
    }

	@Override
	public void addVelocity(double x, double y, double z) {
		motionX = 0;
		motionY += y;
		motionZ = 0;
	}

	@Override
	public boolean getIsInvulnerable() {
		return false;
	}

	@Override
	public void onKillCommand() {
		this.setDead();
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if(source == DamageSource.OUT_OF_WORLD) {
			return true;
		}
		if (source instanceof EntityDamageSource) {
			Entity sourceEntity = ((EntityDamageSource) source).getTrueSource();
			if (sourceEntity instanceof EntityPlayer) {
				if (!getEntityWorld().isRemote) {
					return super.attackEntityFrom(source, damage);
				}
			}
		}
		return false;
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

	@SideOnly(Side.CLIENT)
	private void spawnCloudParticle() {
		double x = this.posX + (this.world.rand.nextFloat() - 0.5F) / 2.0F;
		double y = this.posY + 0.1D;
		double z = this.posZ + (this.world.rand.nextFloat() - 0.5F) / 2.0F;
		double mx = (this.world.rand.nextFloat() - 0.5F) / 12.0F;
		double my = (this.world.rand.nextFloat() - 0.5F) / 16.0F * 0.1F;
		double mz = (this.world.rand.nextFloat() - 0.5F) / 12.0F;
		int[] color = {100, 100, 0, 255};

		ParticleGasCloud hazeParticle = (ParticleGasCloud) BLParticles.GAS_CLOUD
				.create(this.world, x, y, z, ParticleFactory.ParticleArgs.get()
						.withData(null)
						.withMotion(mx, my, mz)
						.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F)
						.withScale(8f));
		
		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE, hazeParticle);
		
		ParticleGasCloud particle = (ParticleGasCloud) BLParticles.GAS_CLOUD
				.create(this.world, x, y, z, ParticleFactory.ParticleArgs.get()
						.withData(null)
						.withMotion(mx, my, mz)
						.withColor(color[0] / 255.0F, color[1] / 255.0F, color[2] / 255.0F, color[3] / 255.0F)
						.withScale(4f));

		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_TEXTURED, particle);
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 0;
	}
}