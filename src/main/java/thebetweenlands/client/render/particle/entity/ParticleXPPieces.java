package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleTextureStitcher;

public class ParticleXPPieces extends ParticleBreakingBL {
	protected int xpColor;

	protected ParticleXPPieces(World worldIn, double posXIn, double posYIn, double posZIn, ItemStack stack, float scale) {
		super(worldIn, posXIn, posYIn, posZIn, stack, scale);
		this.setAlphaF(0.5F);
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		float colorTicks = ((float)this.xpColor + partialTicks) / 2.0F;
		int red = (int)((MathHelper.sin(colorTicks + 0.0F) + 1.0F) * 0.5F * 255.0F);
		int blue = (int)((MathHelper.sin(colorTicks + 4.1887903F) + 1.0F) * 0.1F * 255.0F);
		this.particleRed = red / 255.0F;
		this.particleGreen = 1.0F;
		this.particleBlue = blue / 255.0F;
		super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
	}

	@Override
	public void onUpdate() {
		this.xpColor++;

		super.onUpdate();
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleXPPieces> {
		public Factory() {
			super(ParticleXPPieces.class, ParticleTextureStitcher.create(ParticleXPPieces.class, new ResourceLocation("entity/experience_orb")));
		}

		@Override
		public ParticleXPPieces createParticle(ImmutableParticleArgs args) {
			return new ParticleXPPieces(args.world, args.x, args.y, args.z, args.data.getObject(ItemStack.class, 0), args.scale);
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(ItemStack.EMPTY);
		}
	}
}
