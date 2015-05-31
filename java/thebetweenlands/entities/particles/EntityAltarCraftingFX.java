package thebetweenlands.entities.particles;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
import thebetweenlands.client.render.tileentity.TileEntityDruidAltarRenderer;
import thebetweenlands.tileentities.TileEntityDruidAltar;

import javax.vecmath.Vector3d;

public class EntityAltarCraftingFX extends EntityFX {
	private TileEntityDruidAltar target;
	private final Vector3d startPoint;
	private final Vector3d endPoint;
	
	public EntityAltarCraftingFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ, float scale, TileEntityDruidAltar target) {
		super(world, x, y, z, motionX, motionY, motionZ);
		this.target = target;
		this.startPoint = new Vector3d(x, y, z);
		this.endPoint = new Vector3d(target.xCoord + 0.5D, target.yCoord + TileEntityDruidAltarRenderer.FINAL_HEIGHT + 1.05D, target.zCoord + 0.5D);
		float colorMulti = this.rand.nextFloat() * 0.3F;
        this.particleScale = 1.0f;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F * colorMulti;
        this.particleMaxAge = TileEntityDruidAltar.CRAFTING_TIME + 200000;
        this.noClip = true;
        this.setParticleTextureIndex((int) (Math.random() * 26.0D + 1.0D + 224.0D));
	}

    @Override
    public void onUpdate() {
    	if(this.target == null) {
    		this.setDead();
    	}
    	//TODO I have no fucking idea why it only works like this, for some reason this.target != te
    	TileEntityDruidAltar te = (TileEntityDruidAltar)this.worldObj.getTileEntity(this.target.xCoord, this.target.yCoord, this.target.zCoord);
    	double relProgress = (double) te.craftingProgress / (double) TileEntityDruidAltar.CRAFTING_TIME;
    	if(relProgress == 1.0D || te.craftingProgress == 0) {
    		this.setDead();
    	}
    	Vector3d xzDiff = new Vector3d(this.endPoint.x, this.endPoint.y, this.endPoint.z);
    	xzDiff.sub(new Vector3d(this.startPoint.x, this.endPoint.y, this.startPoint.z));
    	Vector3d yDiff = new Vector3d(this.endPoint.x, this.endPoint.y, this.endPoint.z);
    	yDiff.sub(new Vector3d(this.endPoint.x, this.startPoint.y, this.endPoint.z));
    	xzDiff.scale(relProgress);
    	yDiff.scale(Math.pow(relProgress, 6));
    	this.prevPosX = this.posX;
    	this.prevPosY = this.posY;
    	this.prevPosZ = this.posZ;
    	this.setPosition(this.startPoint.x + xzDiff.x, this.startPoint.y + yDiff.y, this.startPoint.z + xzDiff.z);
    	particleGreen = (float) (1F - relProgress);
    	particleBlue = (float) (1F - relProgress);
    	super.onUpdate();
    }
}
