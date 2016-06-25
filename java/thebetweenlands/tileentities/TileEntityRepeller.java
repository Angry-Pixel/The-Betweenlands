package thebetweenlands.tileentities;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import thebetweenlands.entities.mobs.EntityWight;
import thebetweenlands.entities.mobs.boss.IBossBL;

public class TileEntityRepeller extends TileEntity {
	private static final float MAX_FUEL = 10.0F;
	private static final float MAX_RADIUS = 18.0F;
	private static final int DEPLOY_TIME = 80;

	private boolean hasShimmerstone = false;
	private float fuel = 0.0F;
	private boolean prevRunning = false;
	private boolean running = false;
	private float lastRadius = 0.0F;
	private float radius = 0.0F;
	private int deployTicks = 0;
	private int radiusState = 0;

	public int renderTicks = 0;

	public void setRadiusState(int state) {
		if(this.running && state % 4 != this.radiusState)
			this.deployTicks = 0;
		this.radiusState = state % 4;
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}

	public void cycleRadiusState() {
		this.radiusState = (this.radiusState + 1) % 4;
		if(this.running)
			this.deployTicks = 0;
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}

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

	public float addFuel(float amount) {
		float canAdd = MAX_FUEL - this.fuel;
		if(canAdd > 0.0F) {
			float added = Math.min(canAdd, amount);
			this.fuel += added;
			return added;
		}
		return 0.0F;
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
			if(this.fuel <= 0.0F) {
				this.fuel = 0.0F;
			} else {
				float fuelCost = 0.000004F * (this.radiusState + 1);
				int meta = this.getBlockMetadata();
				double centerX = this.xCoord + 0.5F;
				double centerY = this.yCoord + 1.15F;
				double centerZ = this.zCoord + 0.5F;
				AxisAlignedBB affectedBB = AxisAlignedBB.getBoundingBox(this.xCoord - this.radius - 5.0F, this.yCoord - this.radius - 5.0F, this.zCoord - this.radius - 5.0F, this.xCoord + this.radius + 5.0F, this.yCoord + this.radius + 5.0F, this.zCoord + this.radius + 5.0F);
				List<Entity> affectedEntities = this.worldObj.getEntitiesWithinAABB(IMob.class, affectedBB);
				for(Entity entity : affectedEntities) {
					if(entity instanceof EntityWight == false && entity instanceof IBossBL == false) {
						Vec3 closestPoint = this.getClosestAABBCorner(entity.boundingBox, centerX, centerY, centerZ);
						if(closestPoint.squareDistanceTo(centerX, centerY, centerZ) < this.radius*this.radius) {
							double diffX = entity.posX - centerX;
							double diffY = entity.posY - centerY;
							double diffZ = entity.posZ - centerZ;
							entity.moveEntity(diffX*0.1F, 0.0F, diffZ*0.1F);
							double len = Math.sqrt(diffX*diffX + diffY*diffY + diffZ*diffZ);
							double speed = (this.radius - len) / this.radius * 1.5F + 0.5F;
							entity.motionX = (float)(diffX / len) * speed;
							entity.motionZ = (float)(diffZ / len) * speed;
							if(entity instanceof EntityLivingBase) {
								((EntityLivingBase)entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 10, 6));
							}
							if(!entity.isCollidedHorizontally) {
								fuelCost += 0.00028F;
							}
						}
					}
				}
				List<Entity> affectedProjectiles = this.worldObj.getEntitiesWithinAABB(IProjectile.class, affectedBB);
				for(Entity entity : affectedProjectiles) {
					Vec3 closestPoint = this.getClosestAABBCorner(entity.boundingBox, centerX, centerY, centerZ);
					if(closestPoint.squareDistanceTo(centerX, centerY, centerZ) < this.radius*this.radius) {
						double velocity = Math.sqrt(entity.motionX*entity.motionX + entity.motionY*entity.motionY + entity.motionZ*entity.motionZ);
						double diffX = entity.posX - centerX;
						double diffY = entity.posY - centerY;
						double diffZ = entity.posZ - centerZ;
						double len = Math.sqrt(diffX*diffX + diffY*diffY + diffZ*diffZ);
						entity.moveEntity(diffX*0.1F, 0.0F, diffZ*0.1F);
						entity.motionX = (float)(diffX / len) * velocity;
						entity.motionY = (float)(diffY / len) * velocity;
						entity.motionZ = (float)(diffZ / len) * velocity;
						((IProjectile)entity).setThrowableHeading(diffX / len, diffY / len, diffZ / len, 1.0F, 1.0F);
						entity.velocityChanged = true;
						if(!entity.isCollidedHorizontally && !entity.isCollidedHorizontally && !entity.onGround) {
							fuelCost += 0.0004F;
						}
					}
				}

				//Limit fuel cost per tick
				fuelCost = Math.min(fuelCost, 0.00125F);
				this.fuel -= fuelCost;
			}
		} else {
			this.renderTicks++;
		}

		if(this.prevRunning != this.running) {
			this.deployTicks = 0;
		}
		this.prevRunning = this.running;

		float desiredRadius = MAX_RADIUS / 4.0F * (this.radiusState + 1);
		this.lastRadius = this.radius;
		if(this.running && this.radius < desiredRadius && this.deployTicks < DEPLOY_TIME) {
			this.deployTicks++;
			this.radius = (float) this.easeInOut(this.deployTicks, this.radius, desiredRadius, DEPLOY_TIME);
			if(this.radius > desiredRadius) {
				this.radius = desiredRadius;
			}
		} else if(!this.running && this.radius > 0.0F || this.radius > desiredRadius) {
			this.deployTicks++;
			this.radius = (float) this.easeInOut(this.deployTicks, desiredRadius, -desiredRadius, DEPLOY_TIME);
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
		this.radiusState = nbt.getInteger("radiusState");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setFloat("fuel", this.fuel);
		nbt.setBoolean("hasShimmerstone", this.hasShimmerstone);
		nbt.setInteger("deployTicks", this.deployTicks);
		nbt.setFloat("radius", this.radius);
		nbt.setBoolean("running", this.running);
		nbt.setInteger("radiusState", this.radiusState);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("hasShimmerstone", this.hasShimmerstone);
		nbt.setBoolean("running", this.running);
		nbt.setInteger("deployTicks", this.deployTicks);
		nbt.setFloat("radius", this.radius);
		nbt.setInteger("radiusState", this.radiusState);
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
		this.radiusState = nbt.getInteger("radiusState");
	}
}
