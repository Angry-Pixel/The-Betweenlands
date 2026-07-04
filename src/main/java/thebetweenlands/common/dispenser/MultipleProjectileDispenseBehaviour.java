package thebetweenlands.common.dispenser;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class MultipleProjectileDispenseBehaviour extends ProjectileDispenseBehavior {
	protected final int minProjectiles;
	protected final int randomProjectiles;
	
	public MultipleProjectileDispenseBehaviour(Item projectile, int minProjectiles, int randomProjectiles) {
		super(projectile);
		this.minProjectiles = minProjectiles;
		this.randomProjectiles = randomProjectiles;
	}

	@Override
	public ItemStack execute(BlockSource blockSource, ItemStack item) {
		Level level = blockSource.level();
		Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
		Position position = this.dispenseConfig.positionFunction().getDispensePosition(blockSource, direction);
		
		RandomSource random = level.getRandom();
		for (int i = 0; i < random.nextInt(randomProjectiles) + minProjectiles; ++i) {
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
