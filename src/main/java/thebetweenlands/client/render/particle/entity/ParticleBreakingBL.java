package thebetweenlands.client.render.particle.entity;

import net.minecraft.client.particle.ParticleBreaking;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory;

public class ParticleBreakingBL extends ParticleBreaking {
	protected ParticleBreakingBL(World worldIn, double posXIn, double posYIn, double posZIn, Item itemIn, int meta) {
		super(worldIn, posXIn, posYIn, posZIn, itemIn, meta);
	}

	public static final class Factory extends ParticleFactory<Factory, ParticleBreaking> {
		public Factory() {
			super(ParticleBreaking.class);
		}

		@Override
		public ParticleBreaking createParticle(ImmutableParticleArgs args) {
			return new ParticleBreakingBL(args.world, args.x, args.y, args.z, args.data.getObject(Item.class, 0), args.data.getInt(1));
		}

		@Override
		protected void setBaseArguments(ParticleArgs<?> args) {
			args.withData(Items.SLIME_BALL, 0);
		}
	}
}
