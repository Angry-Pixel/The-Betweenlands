package thebetweenlands.common.entity;


import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

	public EntityBigPuffshroom(World world) {
		super(world);
		setSize(3F, 1.8F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
	}

	@Override
	public void onUpdate() {
		
		prev_animation_1 = animation_1;
		prev_animation_2 = animation_2;
		prev_animation_3 = animation_3;
		prev_animation_4 = animation_4;
		prev_renderTicks = renderTicks;

		if (/*!getEntityWorld().isRemote && */cooldown <= 0 && getEntityWorld().getTotalWorldTime()%5 == 0)
			findEnemyToAttack();

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
			if (active_4) {
				if (animation_4 <= 1)
					getEntityWorld().playSound((EntityPlayer) null, getPosition().getX() + 0.5D, getPosition().getY() + 1D, getPosition().getZ() + 0.5D, SoundRegistry.PUFF_SHROOM, SoundCategory.BLOCKS, 0.5F, 0.95F + getEntityWorld().rand.nextFloat() * 0.2F);
				if (animation_4 == 10) {
					createDebris(getPosition());
					//System.out.println("Shooting Spore Bombs");
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

	protected Entity findEnemyToAttack() {
		if(!active_1 && animation_1 == 0) {
			List<EntityPlayer> list = getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(getPosition()).grow(5D, 2D, 5D));
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
	
	private void createDebris(BlockPos pos) {
		for (int x = 0; x < 12; x++) {
			double angle = Math.toRadians(x * 30D);
			double offSetX = Math.floor(-Math.sin(angle) * 2D);
			double offSetZ = Math.floor(Math.cos(angle) * 2D);

			if(getEntityWorld().isAirBlock(pos.add(offSetX, 1, offSetZ))) {
				IBlockState placedBlock = BlockRegistry.MOULDY_SOIL.getDefaultState();
				getEntityWorld().setBlockState(pos.add(offSetX, 2, offSetZ), placedBlock);
				EntityFallingBlock debris = new EntityFallingBlock(getEntityWorld(), Math.floor(pos.getX() + 0.5D + offSetX), pos.getY() + 2D, Math.floor(pos.getZ() + 0.5D + offSetZ), placedBlock);
				Vec3d vector3d = new Vec3d(this.posX, this.posY, this.posZ);
				double velX = pos.getX() + offSetX * 3D - debris.posX;
				double velY = pos.getY() + 6D - debris.posY;
				double velZ = pos.getZ() + offSetZ * 3D - debris.posZ;
				double distanceSqRt = (double) MathHelper.sqrt(velX * velX + velY * velY + velZ * velZ);
				double accelerationX = velX / distanceSqRt * 0.3D + rand.nextDouble() * 0.2D;
				double accelerationY = velY / distanceSqRt * 0.5D + rand.nextDouble() * 0.5D;
				double accelerationZ = velZ / distanceSqRt * 0.3D + rand.nextDouble() * 0.2D;
				vector3d.add(accelerationX, accelerationY, accelerationZ).scale((double) 0.9F);
				debris.addVelocity(accelerationX, accelerationY, accelerationZ);
				getEntityWorld().spawnEntity(debris);
			}
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
		return false;
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
		return true;
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
					return true;
				}
			}
			return true;
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