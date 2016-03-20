package thebetweenlands.tileentities;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import thebetweenlands.entities.mobs.EntityWight;
import thebetweenlands.entities.mobs.boss.IBossBL;

public class TileEntityRepeller extends TileEntity {
	private static final float MAX_FUEL = 10.0F;
	private static final float MAX_RADIUS = 6.0F;
	private static final int DEPLOY_TIME = 40;

	private boolean hasShimmerstone = false;
	private float fuel = 0.0F;
	private boolean prevRunning = false;
	private boolean running = false;
	private float lastRadius = 0.0F;
	private float radius = 0.0F;
	private int deployTicks = 0;

	public int renderTicks = 0;

	public void addShimmerstone() {
		this.hasShimmerstone = true;
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}

	public boolean hasShimmerstone() {
		return this.hasShimmerstone;
	}

	public void removeShimmerstone() {
		this.hasShimmerstone = false;
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}

	public boolean addFuel(float amount) {
		if(this.fuel + amount > MAX_FUEL)
			return false;
		this.fuel += amount;
		return true;
	}

	public float getFuel() {
		return this.fuel;
	}

	public void emptyFuel() {
		this.fuel = 0.0F;
	}

	public boolean isRunning() {
		return this.running || this.radius > 0.0F;
	}

	public float getRadius(float partialTicks) {
		return this.lastRadius + (this.radius - this.lastRadius) * partialTicks;
	}

	@Override
	public void updateEntity() {
		if(!this.worldObj.isRemote) {
			if(this.fuel > 0) {
				this.fuel -= 0.000005F;
				if(this.fuel <= 0.0F) {
					this.fuel = 0.0F;
				}
			}
			if(this.fuel > 0.0F && this.hasShimmerstone) {
				if(!this.running) {
					this.running = true;
					this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
				}
			} else if(this.fuel <= 0.0F || !this.hasShimmerstone) {
				if(this.running) {
					this.running = false;
					this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
				}
			}

			int meta = this.getBlockMetadata();
			double xOff = Math.sin(meta / 2.0F * Math.PI) * 0.12F;
			double zOff = Math.cos(meta / 2.0F * Math.PI) * 0.12F;
			double centerX = this.xCoord + 0.5F + xOff;
			double centerY = this.yCoord + 1.15F;
			double centerZ = this.zCoord + 0.5F - zOff;
			AxisAlignedBB affectedBB = AxisAlignedBB.getBoundingBox(this.xCoord - this.radius - 5.0F, this.yCoord - this.radius - 5.0F, this.zCoord - this.radius - 5.0F, this.xCoord + this.radius + 5.0F, this.yCoord + this.radius + 5.0F, this.zCoord + this.radius + 5.0F);
			List<EntityMob> affectedEntities = this.worldObj.getEntitiesWithinAABB(EntityMob.class, affectedBB);
			for(EntityMob entity : affectedEntities) {
				if(entity instanceof EntityWight == false && entity instanceof IBossBL == false) {
					Vec3 closestPoint = this.getClosestAABBCorner(entity.boundingBox, centerX, centerY, centerZ);
					if(closestPoint.squareDistanceTo(centerX, centerY, centerZ) < this.radius*this.radius) {
						double diffX = closestPoint.xCoord - centerX;
						double diffY = closestPoint.yCoord - centerY;
						double diffZ = closestPoint.zCoord - centerZ;
						double len = Math.sqrt(diffX*diffX + diffY*diffY + diffZ*diffZ);
						entity.moveEntity(diffX*0.1F, 0.0F, diffZ*0.1F);
						entity.motionX = (float)(diffX / len) * 0.1F;
						entity.motionZ = (float)(diffX / len) * 0.1F;
						entity.setInWeb();
						this.fuel -= 0.0035F;
					}
				}
			}
		} else {
			this.renderTicks++;
		}

		if(this.prevRunning != this.running) {
			this.deployTicks = 0;
		}
		this.prevRunning = this.running;

		this.lastRadius = this.radius;
		if(this.running && this.radius < MAX_RADIUS && this.deployTicks < DEPLOY_TIME) {
			this.deployTicks++;
			this.radius = (float) this.easeInOut(this.deployTicks, 0.0F, MAX_RADIUS, DEPLOY_TIME);
			if(this.radius > MAX_RADIUS) {
				this.radius = MAX_RADIUS;
			}
		} else if(!this.running && this.radius > 0.0F) {
			this.deployTicks++;
			this.radius = (float) this.easeInOut(this.deployTicks, MAX_RADIUS, -MAX_RADIUS, DEPLOY_TIME);
			if(this.radius < 0.0F) {
				this.radius = 0.0F;
			}
		}
	}

	private Vec3 getClosestAABBCorner(AxisAlignedBB bb, double centerX, double centerY, double centerZ) {
		Vec3 center = Vec3.createVectorHelper(centerX, centerY, centerZ);
		Vec3 closest = null;
		for(int bcx = 0; bcx <= 1; bcx++) {
			for(int bcy = 0; bcy <= 1; bcy++) {
				for(int bcz = 0; bcz <= 1; bcz++) {
					double cx = bcx == 1 ? bb.maxX : bb.minX;
					double cy = bcy == 1 ? bb.maxY : bb.minY;
					double cz = bcz == 1 ? bb.maxZ : bb.minZ;
					Vec3 current = Vec3.createVectorHelper(cx, cy, cz);
					if(closest == null || current.distanceTo(center) < closest.distanceTo(center)) {
						closest = current;
					}
				}
			}
		}
		return closest;
	}

	private double easeInOut(float t, float b, float c, float d) {
		t /= d/2;
		if (t < 1) return c/2*t*t*t*t*t + b;
		t -= 2;
		return c/2*(t*t*t*t*t + 2) + b;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		double radius = this.getRadius(0.0F);
		return AxisAlignedBB.getBoundingBox(this.xCoord - radius, this.yCoord - radius, this.zCoord - radius, this.xCoord + 1.0D + radius, this.yCoord + 2.0D + radius, this.zCoord + 1.0D + radius);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.fuel = nbt.getFloat("fuel");
		this.hasShimmerstone = nbt.getBoolean("hasShimmerstone");
		this.deployTicks = nbt.getInteger("deployTicks");
		this.radius = nbt.getFloat("radius");
		this.running = nbt.getBoolean("running");
		this.prevRunning = this.running;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setFloat("fuel", this.fuel);
		nbt.setBoolean("hasShimmerstone", this.hasShimmerstone);
		nbt.setInteger("deployTicks", this.deployTicks);
		nbt.setFloat("radius", this.radius);
		nbt.setBoolean("running", this.running);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("hasShimmerstone", this.hasShimmerstone);
		nbt.setBoolean("running", this.running);
		nbt.setInteger("deployTicks", this.deployTicks);
		nbt.setFloat("radius", this.radius);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		NBTTagCompound nbt = packet.func_148857_g();
		this.hasShimmerstone = nbt.getBoolean("hasShimmerstone");
		this.deployTicks = nbt.getInteger("deployTicks");
		this.radius = nbt.getFloat("radius");
		this.running = nbt.getBoolean("running");
		this.prevRunning = this.running;
	}
}
