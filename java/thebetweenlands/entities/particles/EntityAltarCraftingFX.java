package thebetweenlands.entities.particles;

import javax.vecmath.Vector3d;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.client.render.tileentity.TileEntityDruidAltarRenderer;
import thebetweenlands.tileentities.TileEntityDruidAltar;

public class EntityAltarCraftingFX extends EntityFX {
	private TileEntityDruidAltar target;
	private final Vector3d startPoint;
	private final Vector3d endPoint;

	public EntityAltarCraftingFX(World world, double x, double y, double z, float scale, TileEntityDruidAltar target) {
		super(world, x, y, z, 0, 0, 0);
		this.motionX = this.motionY = this.motionZ = 0;
		this.target = target;
		this.startPoint = new Vector3d(x, y, z);
		this.endPoint = new Vector3d(target.xCoord + 0.5D, target.yCoord + TileEntityDruidAltar.FINAL_HEIGHT + 1.05D, target.zCoord + 0.5D);
		float colorMulti = this.rand.nextFloat() * 0.3F;
        this.particleScale = 1.0f;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F * colorMulti;
        this.particleMaxAge = TileEntityDruidAltar.CRAFTING_TIME + 200000;
        this.noClip = true;
        this.setParticleTextureIndex((int) (Math.random() * 26.0D + 1.0D + 224.0D));
	}

    @Override
    public void onUpdate() {
    	//TODO I have no fucking idea why it only works like this, for some reason this.target != te
    	TileEntity tileEntity = this.worldObj.getTileEntity(this.target.xCoord, this.target.yCoord, this.target.zCoord);
    	double craftingProgress = 0;
    	if(tileEntity instanceof TileEntityDruidAltar) {
    		craftingProgress = ((TileEntityDruidAltar) tileEntity).craftingProgress;
    	}
    	if(this.particleAge++ >= this.particleMaxAge || craftingProgress == 0) {
    		this.setDead();
    	}
    	craftingProgress /= TileEntityDruidAltar.CRAFTING_TIME;
    	Vector3d xzDiff = new Vector3d(this.endPoint.x, this.endPoint.y, this.endPoint.z);
    	xzDiff.sub(new Vector3d(this.startPoint.x, this.endPoint.y, this.startPoint.z));
    	Vector3d yDiff = new Vector3d(this.endPoint.x, this.endPoint.y, this.endPoint.z);
    	yDiff.sub(new Vector3d(this.endPoint.x, this.startPoint.y, this.endPoint.z));
    	xzDiff.scale(craftingProgress);
    	yDiff.scale(Math.pow(craftingProgress, 6));
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
    	this.setPosition(this.startPoint.x + xzDiff.x, this.startPoint.y + yDiff.y, this.startPoint.z + xzDiff.z);
    	particleGreen = (float) (1F - craftingProgress);
    	particleBlue = (float) (1F - craftingProgress);
    }
}
