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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.EntityRootGrabber;
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
				spawnSludgeJet(getPos().getX() + 6D, getPos().getY() + 2.5D, getPos().getZ() - 1.5D);
				spawnSludgeJet(getPos().getX() - 5D, getPos().getY() + 2.5D, getPos().getZ() + 2.5D);
			}

			if (animationTicks == 60 || animationTicks == 240) {
				spawnSludgeJet(getPos().getX() + 2.5D, getPos().getY() + 2.5D, getPos().getZ() - 5D);
				spawnSludgeJet(getPos().getX() - 1.5D, getPos().getY() + 2.5D, getPos().getZ() + 6D);
			}

			if (animationTicks == 105 || animationTicks == 285) {
				spawnSludgeJet(getPos().getX() - 1.5D, getPos().getY() + 2.5D, getPos().getZ() - 5D);
				spawnSludgeJet(getPos().getX() + 2.5D, getPos().getY() + 2.5D, getPos().getZ() + 6D);
			}

			if (animationTicks == 150 || animationTicks == 330) {
				spawnSludgeJet(getPos().getX() - 5D, getPos().getY() + 2.5D, getPos().getZ() - 1.5D);
				spawnSludgeJet(getPos().getX() + 6D, getPos().getY() + 2.5D, getPos().getZ() + 2.5D);
			}

		}

		checkSurfaceCollisions();

	}

	private Entity checkSurfaceCollisions() {
		boolean reverse = false;
		for (Entity entity : getEntityAbove()) {
			if (entity != null && !(entity instanceof EntitySludgeJet) && !(entity instanceof EntityRootGrabber)&& !(entity instanceof IEntityBL)) {
				if(entity instanceof EntityArrow)
					entity.setDead();
				if (getDistance(entity) >= 4.25F - entity.width * 0.5F && getDistance(entity) <= 7.5F + entity.width * 0.5F) {
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
		return new AxisAlignedBB(getPos()).grow(7.5D, 0.0625D, 7.5D).offset(0D, 2D, 0D);
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
