package thebetweenlands.common.item.misc;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.entity.projectile.PyradFlame;

public class PyradFlameItem extends Item implements ProjectileItem {
	private static final float DISPENSER_SPREAD = 0.2F;
	private static final float SPREAD = 0.05F;
	
	public PyradFlameItem(Properties properties) {
		super(properties);
		DispenserBlock.registerBehavior(this, new PyradFlameDispenseBehaviour(this));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if(!level.isClientSide()) {
			level.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.0F);

			Vec3 look = player.getLookAngle();

			for (int i = 0; i < player.getRandom().nextInt(6) + 1; ++i) {
				PyradFlame flame = new PyradFlame(level, player, new Vec3(look.x + player.getRandom().nextGaussian() * SPREAD, look.y, look.z + player.getRandom().nextGaussian() * SPREAD));
				flame.setY(player.getY() + (double)(player.getBbHeight() / 2.0F) + 0.5D);
				level.addFreshEntity(flame);
			}
			stack.consume(1, player);
		}
		return InteractionResultHolder.success(stack);
	}

	@Override
	public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
		RandomSource randomsource = level.getRandom();
		double motionX = randomsource.triangle(direction.getStepX(), DISPENSER_SPREAD);
		double motionY = randomsource.triangle(direction.getStepY(), DISPENSER_SPREAD);
		double motionZ = randomsource.triangle(direction.getStepZ(), DISPENSER_SPREAD);
		Vec3 motion = new Vec3(motionX, motionY, motionZ);
		PyradFlame flame = new PyradFlame(level, pos.x(), pos.y(), pos.z(), motion);
		flame.setItem(stack);
		return flame;
	}
	
	@Override
	public void shoot(Projectile projectile, double x, double y, double z, float velocity, float inaccuracy) {
	}

	@Override
	public ProjectileItem.DispenseConfig createDispenseConfig() {
		return ProjectileItem.DispenseConfig.builder()
			.positionFunction((blockSource, face) -> DispenserBlock.getDispensePosition(blockSource, 1.0, Vec3.ZERO))
			.uncertainty(DISPENSER_SPREAD / 0.0172275F)
			.power(1.0F)
			.overrideDispenseEvent(1018)
			.build();
	}
	
	public static class PyradFlameDispenseBehaviour extends ProjectileDispenseBehavior {
		public PyradFlameDispenseBehaviour(Item projectile) {
			super(projectile);
		}

		@Override
		public ItemStack execute(BlockSource blockSource, ItemStack item) {
			Level level = blockSource.level();
			Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
			Position position = this.dispenseConfig.positionFunction().getDispensePosition(blockSource, direction);
			
			RandomSource random = level.getRandom();
			for (int i = 0; i < random.nextInt(6) + 1; ++i) {
				Projectile projectile = this.projectileItem.asProjectile(level, position, item, direction);
				this.projectileItem
					.shoot(
						projectile,
						(double)direction.getStepX(),
						(double)direction.getStepY(),
						(double)direction.getStepZ(),
						this.dispenseConfig.power(),
						this.dispenseConfig.uncertainty()
					);
				level.addFreshEntity(projectile);
			}
			item.shrink(1);
			return item;
		}

	}
}
