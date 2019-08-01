package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.entity.ParticleGasCloud;
import thebetweenlands.common.entity.EntityRootGrabber;
import thebetweenlands.common.entity.EntityTriggeredSludgeWallJet;
import thebetweenlands.common.entity.mobs.EntitySludgeJet;

public class TileEntityDecayPitControl extends TileEntity implements ITickable {

	public float animationTicks = 0;
	public float animationTicksPrev = 0;


	@Override
	public void update() {
		animationTicksPrev = animationTicks;

		animationTicks += 1F;
		if (animationTicks >= 360F)
			animationTicks = animationTicksPrev = 0;

		if (!getWorld().isRemote) {

			if (animationTicks == 15 || animationTicks == 195) {
				spawnSludgeJet(getPos().getX() + 5.5D, getPos().getY() + 3D, getPos().getZ() - 1.5D);
				spawnSludgeJet(getPos().getX() - 4.5D, getPos().getY() + 3D, getPos().getZ() + 2.5D);
			}

			if (animationTicks == 60 || animationTicks == 240) {
				spawnSludgeJet(getPos().getX() + 2.5D, getPos().getY() + 3D, getPos().getZ() - 4.5D);
				spawnSludgeJet(getPos().getX() - 1.5D, getPos().getY() + 3D, getPos().getZ() + 5.5D);
			}

			if (animationTicks == 105 || animationTicks == 285) {
				spawnSludgeJet(getPos().getX() - 1.5D, getPos().getY() + 3D, getPos().getZ() - 4.5D);
				spawnSludgeJet(getPos().getX() + 2.5D, getPos().getY() + 3D, getPos().getZ() + 5.5D);
			}

			if (animationTicks == 150 || animationTicks == 330) {
				spawnSludgeJet(getPos().getX() - 4.5D, getPos().getY() + 3D, getPos().getZ() - 1.5D);
				spawnSludgeJet(getPos().getX() + 5.5D, getPos().getY() + 3D, getPos().getZ() + 2.5D);
			}

			//TODO remove ghetto syncing
			if(getWorld().getTotalWorldTime() % 20 == 0)
				updateBlock();

			if(getWorld().getTotalWorldTime() % 1200 == 0) { //once a minute
				//S
				checkTurretSpawn(4, 12, 11);
				checkTurretSpawn(-4, 12, 11);
				//E
				checkTurretSpawn(11, 12, 4);
				checkTurretSpawn(11, 12, -4);
				//N
				checkTurretSpawn(4, 12, -11);
				checkTurretSpawn(-4, 12, -11);
				//W
				checkTurretSpawn(-11, 12, -4);
				checkTurretSpawn(-11, 12, 4);
			}
		} else {
			this.spawnAmbientParticles();
		}
		checkSurfaceCollisions();
	}

	private void checkTurretSpawn(int x, int y, int z) {
		BlockPos checkPos = getPos().add(x, y, z);
		AxisAlignedBB checkBox = new AxisAlignedBB(checkPos);
		List<EntityTriggeredSludgeWallJet> entityList = getWorld().getEntitiesWithinAABB(EntityTriggeredSludgeWallJet.class, checkBox);
		for (EntityTriggeredSludgeWallJet entity : entityList) {
			if (entity instanceof EntityTriggeredSludgeWallJet) {
				break;
			}
		}
		if (entityList.isEmpty()) {
			EntityTriggeredSludgeWallJet jet = new EntityTriggeredSludgeWallJet(getWorld());
			jet.setPosition(checkPos.getX() + 0.5D, checkPos.getY(), checkPos.getZ() + 0.5D);
			getWorld().spawnEntity(jet);
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnAmbientParticles() {
		BlockPos pos = this.getPos();
		
		double x = pos.getX() + 0.5D + (this.world.rand.nextFloat() - 0.5F) / 2.0F;
		double y = pos.getY() + 1.5D;
		double z = pos.getZ() + 0.5D + (this.world.rand.nextFloat() - 0.5F) / 2.0F;
		double mx = (this.world.rand.nextFloat() - 0.5F) * 0.08F;
		double my = this.world.rand.nextFloat() * 0.175F;
		double mz = (this.world.rand.nextFloat() - 0.5F) * 0.08F;
		int[] color = {100, 70, 0, 255};

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

	private void updateBlock() {
		getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 3);
	}

	private Entity checkSurfaceCollisions() {
		boolean reverse = false;
		for (Entity entity : getEntityAbove()) {
			if (entity != null && !(entity instanceof EntitySludgeJet) && !(entity instanceof EntityRootGrabber)&& !(entity instanceof IEntityBL)) {
				if(entity instanceof EntityArrow)
					entity.setDead();
				if (getDistance(entity) >= 4.25F - entity.width * 0.5F && getDistance(entity) <= 7F + entity.width * 0.5F) {
					reverse = false;
					if (entity.posY <= getPos().getY() + 3D) {
						entity.motionX = 0D;
						entity.motionY = 0.1D;
						entity.motionZ = 0D;
					} else if (entity.motionY < 0) {
						entity.motionY = 0;
						checkJumpOnTopOfAABB(entity);
					}
				}

				if (getDistance(entity) < 4.25F - entity.width * 0.5F && getDistance(entity) >= 2.5F + entity.width * 0.5F) {
					if (entity.posY <= getPos().getY() + 2D + 0.0625D) {
					reverse = true;
					checkJumpOnTopOfAABB(entity);
					}
				}

				if (getDistance(entity) >= 2.5F + entity.width * 0.5F) {
					Vec3d center = new Vec3d(getPos().getX() + 0.5D, 0, getPos().getZ() + 0.5D);
					Vec3d entityOffset = new Vec3d(entity.posX, 0, entity.posZ);

					double dist = entityOffset.distanceTo(center);
					double circumference = 2 * Math.PI * dist;
					double speed = circumference / 360 * (reverse ? 1F : 0.75F) /* angle per tick */;

					Vec3d push = new Vec3d(0, 1, 0).crossProduct(entityOffset.subtract(center).normalize()).normalize().scale(reverse ? -speed : speed);

					if (!entity.world.isRemote || entity instanceof EntityPlayer) {
						entity.move(MoverType.SELF, push.x, 0, push.z);
					}
				}
			}
		}
		return null;
	}
	
    public float getDistance(Entity entityIn)
    {
        float f = (float)(getPos().getX() + 0.5D - entityIn.posX);
        float f1 = (float)(getPos().getY() + 2D - entityIn.posY);
        float f2 = (float)(getPos().getZ() + 0.5D - entityIn.posZ);
        return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
    }

	public void checkJumpOnTopOfAABB(Entity entity) {
		if (entity.getEntityWorld().isRemote && entity instanceof EntityPlayer) {
			boolean jump = Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
			if (jump)
				((EntityPlayer) entity).jump();
		}
	}

	public List<Entity> getEntityAbove() {
		return getWorld().<Entity>getEntitiesWithinAABB(Entity.class, getFloorEntityBoundingBox(), EntitySelectors.IS_ALIVE);
    }

	private AxisAlignedBB getFloorEntityBoundingBox() {
		return new AxisAlignedBB(getPos()).grow(7D, 0.0625D, 7D).offset(0D, 2D, 0D);
	}

	private void spawnSludgeJet(double posX, double posY, double posZ) {
		EntitySludgeJet jet = new EntitySludgeJet(getWorld());
		jet.setPosition(posX, posY, posZ);
		getWorld().spawnEntity(jet);
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setFloat("animationTicks", animationTicks);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		animationTicks = nbt.getFloat("animationTicks");
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
