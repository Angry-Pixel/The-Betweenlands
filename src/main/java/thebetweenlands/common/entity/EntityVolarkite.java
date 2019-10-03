package thebetweenlands.common.entity;

import java.util.Iterator;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.item.misc.ItemVolarkite;
import thebetweenlands.common.registries.BlockRegistry;

public class EntityVolarkite extends Entity {
	public float prevRotationRoll;
	public float rotationRoll;

	protected int updraftTicks = 0;
	protected int downdraftTicks = 0;
	protected int draftSourcePos = 0;

	public EntityVolarkite(World world) {
		super(world);
		this.setSize(0.6F, 1.8F);
	}

	@Override
	protected void entityInit() {
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
	}

	@Override
	public double getMountedYOffset() {
		return 0.01D + (this.getControllingPassenger() != null ? -this.getControllingPassenger().getYOffset() : 0);
	}

	@Override
	public void onEntityUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.prevRotationPitch = this.rotationPitch;
		this.prevRotationYaw = this.rotationYaw;
		this.prevRotationRoll = this.rotationRoll;

		double targetMotionY = -0.04D;

		this.motionY = targetMotionY + (this.motionY - targetMotionY) * 0.92D;

		this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		this.handleWaterMovement();

		float invFriction = 1.0F;

		if(this.onGround) {
			invFriction *= 0.8F;
		}
		if(this.isInWater() || this.isInLava()) {
			invFriction *= 0.8F;
			if(this.world.getBlockState(new BlockPos(this.posX, this.posY + this.height + 0.75D, this.posZ)).getMaterial().isLiquid()) {
				invFriction *= 0.5F; 
			}
		}

		this.motionX *= invFriction;
		this.motionY *= invFriction;
		this.motionZ *= invFriction;

		Entity controller = this.getControllingPassenger();

		Vec3d kiteDir = new Vec3d(Math.cos(Math.toRadians(this.rotationYaw + 90)), 0, Math.sin(Math.toRadians(this.rotationYaw + 90)));

		double rotIncr = 0;

		boolean hasValidUser = false;

		if(controller != null) {
			controller.fallDistance = 0;

			if(this.motionY < 0 && !this.onGround) {
				double speedBoost = -this.motionY * 0.1D + MathHelper.clamp(Math.sin(Math.toRadians(this.rotationPitch)) * 0.5F, -0.02D, 0.02D);

				this.motionX += kiteDir.x * (speedBoost + 0.01D);
				this.motionZ += kiteDir.z * (speedBoost + 0.01D);

				this.velocityChanged = true;
			}

			Vec3d controllerDir = new Vec3d(Math.cos(Math.toRadians(controller.rotationYaw + 90)), 0, Math.sin(Math.toRadians(controller.rotationYaw + 90)));
			double rotDiff = Math.toDegrees(Math.acos(kiteDir.dotProduct(controllerDir))) * -Math.signum(kiteDir.crossProduct(controllerDir).y);
			rotIncr = MathHelper.clamp(rotDiff * 0.05D, -1.0D, 1.0D);
			this.rotationYaw += rotIncr;

			if(!this.onGround && controller instanceof EntityLivingBase) {
				float forward = ((EntityLivingBase) controller).moveForward;
				if(forward > 0.1F) {
					this.rotationPitch = 20.0F + (this.rotationPitch - 20.0F) * 0.9F;
					this.motionY -= 0.01D;
				} else if(forward < -0.1F) {
					this.rotationPitch = -20.0F + (this.rotationPitch + 20.0F) * 0.9F;
				}
			}

			Iterator<ItemStack> it = controller.getHeldEquipment().iterator();
			while(it.hasNext()) {
				ItemStack stack = it.next();
				if(!stack.isEmpty() && stack.getItem() instanceof ItemVolarkite && ((ItemVolarkite) stack.getItem()).canRideKite(stack, controller)) {
					hasValidUser = true;
					break;
				}
			}
		}

		if(!this.onGround && Math.abs(rotIncr) > 0.1D) {
			this.rotationRoll = (float) (rotIncr * 15 + (this.rotationRoll - rotIncr * 15) * 0.9D);
		} else {
			this.rotationRoll *= 0.9F;
		}

		this.rotationPitch *= 0.9F;

		this.updateUpdraft();

		double speed = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

		if(speed > 0.1D) {
			double dx = this.motionX / speed;
			double dz = this.motionZ / speed;

			this.motionX = (kiteDir.x + (dx - kiteDir.x) * 0.9D) * speed;
			this.motionZ = (kiteDir.z + (dz - kiteDir.z) * 0.9D) * speed;

			double maxSpeed = 0.6D;
			if(speed > maxSpeed) {
				double targetX = dx * maxSpeed;
				double targetZ = dz * maxSpeed;

				this.motionX = targetX + (this.motionX - targetX) * 0.8D;
				this.motionZ = targetZ + (this.motionZ - targetZ) * 0.8D;
			}

			this.velocityChanged = true;
		}

		if(!this.world.isRemote && !hasValidUser) {
			this.onKillCommand();
		}

		this.firstUpdate = false;
	}

	protected void updateUpdraft() {
		int range = 10;

		PooledMutableBlockPos pos = PooledMutableBlockPos.retain();
		pos.setPos(MathHelper.floor(this.posX), MathHelper.floor(this.posY), MathHelper.floor(this.posZ));

		for(int i = 0; i <= range; i++) {
			IBlockState state = this.world.getBlockState(pos);

			Block block = state.getBlock();

			boolean hasSource = false;
			
			if(block instanceof IFluidBlock) {
				Fluid fluid = ((IFluidBlock) block).getFluid();
				if(fluid.getTemperature() > 373 /*roughly 100°C*/) {
					this.updraftTicks = 25;
					hasSource = true;
				} else if(fluid.getTemperature() < 272.15 /*roughly -1°C*/) {
					this.downdraftTicks = 25;
					hasSource = true;
				}
			} else if(state.getMaterial() == Material.FIRE || state.getMaterial() == Material.LAVA || block instanceof BlockFire || block == BlockRegistry.OCTINE_ORE || block == BlockRegistry.OCTINE_BLOCK) {
				this.updraftTicks = 25;
				hasSource = true;
			} else if(state.getMaterial() == Material.ICE || state.getMaterial() == Material.SNOW || state.getMaterial() == Material.CRAFTED_SNOW || state.getMaterial() == Material.PACKED_ICE) {
				this.downdraftTicks = 25;
				hasSource = true;
			} else if(!block.isAir(state, this.world, pos)) {
				break;
			}

			if(hasSource) {
				this.draftSourcePos = pos.getY();
				break;
			}

			pos.setPos(pos.getX(), pos.getY() - 1, pos.getZ());
		}

		pos.release();

		if(this.updraftTicks > 0 || this.downdraftTicks > 0) {
			if(this.motionY < 1.0D) {
				this.motionY += this.downdraftTicks > 0 ? -0.03D : 0.1D;
			}

			if(this.world.isRemote) {
				for(int i = 0; i < (this.downdraftTicks > 0 ? 2 : 10); i++) {
					float offsetX = this.world.rand.nextFloat() - 0.5F;
					float offsetZ = this.world.rand.nextFloat() - 0.5F;

					float len = (float)Math.sqrt(offsetX*offsetX + offsetZ*offsetZ);

					offsetX /= len;
					offsetZ /= len;
					
					this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + offsetX, this.draftSourcePos + (this.posY + (this.downdraftTicks > 0 ? 2.4D : 1) - this.draftSourcePos) * this.world.rand.nextFloat(), this.posZ + offsetZ, this.motionX, this.motionY + (this.downdraftTicks > 0 ? -0.15D : 0.25D), this.motionZ);
				}
			}
		}

		if(this.updraftTicks > 0) {
			this.updraftTicks--;
		}

		if(this.downdraftTicks > 0) {
			this.downdraftTicks--;
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
			this.prevRotationYaw -= 360.0F;
		}

		while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
			this.prevRotationYaw += 360.0F;
		}
	}

	@Override
	@Nullable
	public Entity getControllingPassenger() {
		return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
	}

	@Override
	public boolean canPassengerSteer() {
		return true;
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	@Override
	protected void removePassenger(Entity passenger) {
		super.removePassenger(passenger);

		passenger.fallDistance = 0;

		passenger.motionX = this.motionX;
		passenger.motionY = this.motionY;
		passenger.motionZ = this.motionZ;
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		//No fall damage to node or rider
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return super.isInRangeToRenderDist(distance) || (this.getControllingPassenger() != null && this.getControllingPassenger().isInRangeToRenderDist(distance));
	}
}
