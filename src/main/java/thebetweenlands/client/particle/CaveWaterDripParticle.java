package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;

//TODO: Figure this out properly. It seems to change sprite, but it might be this needs to be split into multiple
public class CaveWaterDripParticle extends TextureSheetParticle {

	private int bobTimer;

	protected CaveWaterDripParticle(ClientLevel level, double x, double y, double z) {
		super(level, x, y, z, 0, 0, 0);
		xd = yd = zd = 0;
		rCol = 0.2F;
		gCol = 0.3F;
		bCol = 1.0F;
		//index 112
		setSize(0.01F, 0.01F);
		this.bobTimer = 10;
		gravity = 0.06F;
		lifetime = (int) (64 / (Math.random() * 0.8F + 0.2));
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE; //TODO: Verify
	}

	@Override
	public void tick() {
		//prev x = x
		//prev y = y
		//prev z = z
		yd -= gravity;

		if (this.bobTimer-- > 0) {
			this.xd *= 0.02D;
			this.yd *= 0.02D;
			this.zd *= 0.02D;
			//index 113
		} else {
			//index 112
		}

		move(xd, yd, zd);
		this.xd *= 0.98D;
		this.yd *= 0.98D;
		this.zd *= 0.98D;
		if (lifetime-- <= 0) {
			this.remove();
		}
		if (this.onGround) {
			this.remove();
			this.level.addParticle(ParticleTypes.SPLASH, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
			xd *= 0.7D;
			zd *= 0.7D;
		}
		BlockPos pos = BlockPos.containing(x, y, z);
		BlockState state = level.getBlockState(pos);
		//TODO: can no longer check material, verify this works
		if (!state.isAir()) {
			double d0 = 0.0D;
			if (!state.getFluidState().isEmpty()) {
				d0 = state.getFluidState().getAmount();
			}
			double dy = (double) (Mth.floor(this.y) + 1) - d0;
			if (this.y < dy) {
				this.remove();
			}
		}
	}

	public static final class Factory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public Factory(SpriteSet sprite) {
			this.spriteSet = sprite;
		}

		@Nullable
		@Override
		public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
			CaveWaterDripParticle particle = new CaveWaterDripParticle(pLevel, pX, pY, pZ);
			particle.pickSprite(spriteSet);
			return particle;
		}
	}
}
