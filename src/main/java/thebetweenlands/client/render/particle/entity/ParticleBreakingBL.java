package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleBreaking;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;

public class ParticleBreakingBL extends ParticleBreaking implements IParticleSpriteReceiver {
	protected ParticleBreakingBL(World worldIn, double posXIn, double posYIn, double posZIn, ItemStack stack, float scale) {
		super(worldIn, posXIn, posYIn, posZIn, stack.getItem(), stack.getMetadata());
		this.setParticleTexture(this.getParticleTexture(stack));
		this.particleScale = scale;
	}

	protected ParticleBreakingBL(World worldIn, double posXIn, double posYIn, double posZIn, double mx, double my, double mz, ItemStack stack, float scale) {
		super(worldIn, posXIn, posYIn, posZIn, stack.getItem(), stack.getMetadata());
		this.setParticleTexture(this.getParticleTexture(stack));
		this.particleScale = scale;
		this.motionX = mx;
        this.motionY = my;
        this.motionZ = mz;
	}
	
	private TextureAtlasSprite getParticleTexture(ItemStack stack) {
		IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
        return model.getOverrides().handleItemState(model, stack, null, null).getParticleTexture();
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
			return new ParticleBreakingBL(args.world, args.x, args.y, args.z, args.data.getObject(ItemStack.class, 0), args.scale);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(Items.SLIME_BALL, 0);
		}
	}
	
	public static final class DefaultFactory extends ParticleFactory<MotionFactory, ParticleBreaking> {
		public DefaultFactory() {
			super(ParticleBreaking.class);
		}

		@Override
		public ParticleBreaking createParticle(ImmutableParticleArgs args) {
			return new ParticleBreakingBL(args.world, args.x, args.y, args.z, args.data.getObject(ItemStack.class, 0), args.scale);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(new ItemStack(Items.SLIME_BALL));
		}
	}
	
	public static final class MotionFactory extends ParticleFactory<MotionFactory, ParticleBreaking> {
		public MotionFactory() {
			super(ParticleBreaking.class);
		}

		@Override
		public ParticleBreaking createParticle(ImmutableParticleArgs args) {
			return new ParticleBreakingBL(args.world, args.x, args.y, args.z, args.motionX, args.motionY, args.motionZ, args.data.getObject(ItemStack.class, 0), args.scale);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(new ItemStack(Items.SLIME_BALL));
		}
	}

	public static final class TexturedFactory extends ParticleFactory<TexturedFactory, ParticleBreakingBL> {
		public TexturedFactory(ResourceLocation texture) {
			super(ParticleBreakingBL.class, ParticleTextureStitcher.create(ParticleBreakingBL.class, texture));
		}

		@Override
		public ParticleBreakingBL createParticle(ImmutableParticleArgs args) {
			return new ParticleBreakingBL(args.world, args.x, args.y, args.z, args.data.getObject(ItemStack.class, 0), args.scale);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(ItemStack.EMPTY);
		}
	}
}
