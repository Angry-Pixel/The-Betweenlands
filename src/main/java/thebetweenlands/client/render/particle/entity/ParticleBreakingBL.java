package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.ParticleBreaking;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;

public class ParticleBreakingBL extends ParticleBreaking implements IParticleSpriteReceiver {
	protected ParticleBreakingBL(World worldIn, double posXIn, double posYIn, double posZIn, Item itemIn, int meta, float scale) {
		super(worldIn, posXIn, posYIn, posZIn, itemIn, meta);
		this.particleScale = scale;
	}

	protected ParticleBreakingBL(World worldIn, double posXIn, double posYIn, double posZIn, double mx, double my, double mz, Item itemIn, int meta, float scale) {
		super(worldIn, posXIn, posYIn, posZIn, itemIn, meta);
		this.particleScale = scale;
		this.motionX = mx;
        this.motionY = my;
        this.motionZ = mz;
	}
	
	@Override
	public int getFXLayer() {
		if(this.particleTexture != null) {
			return 1;
		}
		return super.getFXLayer();
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleBreaking> {
		public Factory() {
			super(ParticleBreaking.class);
		}

		@Override
		public ParticleBreaking createParticle(ImmutableParticleArgs args) {
			return new ParticleBreakingBL(args.world, args.x, args.y, args.z, args.data.getObject(Item.class, 0), args.data.getInt(1), args.scale);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(Items.SLIME_BALL, 0);
		}
	}
	
	public static final class MotionFactory extends ParticleFactory<MotionFactory, ParticleBreaking> {
		public MotionFactory() {
			super(ParticleBreaking.class);
		}

		@Override
		public ParticleBreaking createParticle(ImmutableParticleArgs args) {
			return new ParticleBreakingBL(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getObject(Item.class, 0), args.data.getInt(1), args.scale);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(Items.SLIME_BALL, 0);
		}
	}

	public static final class TexturedFactory extends ParticleFactory<TexturedFactory, ParticleBreakingBL> {
		public TexturedFactory(ResourceLocation texture) {
			super(ParticleBreakingBL.class, ParticleTextureStitcher.create(ParticleBreakingBL.class, texture));
		}

		@Override
		public ParticleBreakingBL createParticle(ImmutableParticleArgs args) {
			return new ParticleBreakingBL(args.world, args.x, args.y, args.z, args.data.getObject(Item.class, 0), args.data.getInt(1), args.scale);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(Items.AIR, 0);
		}
	}
}
