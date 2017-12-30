package thebetweenlands.client.render.particle.entity;

import javax.vecmath.Vector3d;

import net.minecraft.client.particle.Particle;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.common.tile.TileEntityDruidAltar;

public class ParticleAltarCrafting extends Particle {
	private TileEntityDruidAltar target;
	private final Vector3d startPoint;
	private final Vector3d endPoint;

	public ParticleAltarCrafting(World world, double x, double y, double z, float scale, TileEntityDruidAltar target) {
		super(world, x, y, z, 0, 0, 0);
		this.motionX = this.motionY = this.motionZ = 0;
		this.target = target;
		this.startPoint = new Vector3d(x, y, z);
		BlockPos pos = target.getPos();
		this.endPoint = new Vector3d(pos.getX() + 0.5D, pos.getY() + TileEntityDruidAltar.FINAL_HEIGHT + 1.05D, pos.getZ() + 0.5D);
		float colorMulti = this.rand.nextFloat() * 0.3F;
		this.particleScale = scale;
		this.particleRed = this.particleGreen = this.particleBlue = 1.0F * colorMulti;
		this.particleMaxAge = TileEntityDruidAltar.CRAFTING_TIME + 200000;
		this.setParticleTextureIndex((int) (Math.random() * 26.0D + 1.0D + 224.0D));
	}

	@Override
	public void onUpdate() {
		TileEntity tileEntity = this.world.getTileEntity(this.target.getPos());
		double craftingProgress = 0;
		if(tileEntity instanceof TileEntityDruidAltar) {
			craftingProgress = ((TileEntityDruidAltar) tileEntity).craftingProgress;
		}
		if(this.particleAge++ >= this.particleMaxAge || craftingProgress == 0) {
			this.setExpired();
		}
		craftingProgress /= TileEntityDruidAltar.CRAFTING_TIME;
		Vector3d xzDiff = new Vector3d(this.endPoint.x, this.endPoint.y, this.endPoint.z);
		xzDiff.sub(new Vector3d(this.startPoint.x, this.endPoint.y, this.startPoint.z));
		Vector3d yDiff = new Vector3d(this.endPoint.x, this.endPoint.y, this.endPoint.z);
		yDiff.sub(new Vector3d(this.endPoint.x, this.startPoint.y, this.endPoint.z));
		xzDiff.scale(craftingProgress);
		yDiff.scale(Math.pow(craftingProgress, 0.5F));
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.setPosition(this.startPoint.x + xzDiff.x, this.startPoint.y + yDiff.y, this.startPoint.z + xzDiff.z);
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleAltarCrafting> {
		public Factory() {
			super(ParticleAltarCrafting.class);
		}

		@Override
		public ParticleAltarCrafting createParticle(ImmutableParticleArgs args) {
			return new ParticleAltarCrafting(args.world, args.x, args.y, args.z, args.scale, args.data.getObject(TileEntityDruidAltar.class, 0));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData((TileEntityDruidAltar)null);
		}
	}
}
