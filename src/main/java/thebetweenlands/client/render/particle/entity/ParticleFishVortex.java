package thebetweenlands.client.render.particle.entity;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;

public class ParticleFishVortex extends ParticleEntitySwirl {

	public ParticleFishVortex(World world, double x, double y, double z, int maxAge, float scale, float progress, Entity target) {
		super(world, x, y, z, maxAge, scale, progress, target);
		this.setOffset(0, -1.5D, 0).setTargetOffset(0, 1.0D, 0).setRotationSpeed(3.0D).setRotate3D(true);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		IBlockState state = this.world.getBlockState(new BlockPos((int)Math.floor(this.posX), (int)Math.floor(this.posY), (int)Math.floor(this.posZ)));
		if(state.getMaterial() != Material.WATER) {
			this.setExpired();
		}
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleFishVortex> {
		public Factory() {
			super(ParticleFishVortex.class, ParticleTextureStitcher.create(ParticleFishVortex.class, new ResourceLocation("thebetweenlands:particle/fish_swirl")).setSplitAnimations(true));
		}

		@Override
		public ParticleFishVortex createParticle(ImmutableParticleArgs args) {
			return new ParticleFishVortex(args.world, args.x, args.y, args.z, args.data.getInt(0), args.scale, args.data.getFloat(1), args.data.getObject(Entity.class, 2));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(400, 0.0F, null);
		}
	}

}
