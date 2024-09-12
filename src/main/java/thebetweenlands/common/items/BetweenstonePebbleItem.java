package thebetweenlands.common.items;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import thebetweenlands.common.entities.BetweenstonePebble;
import thebetweenlands.common.registries.BlockRegistry;

public class BetweenstonePebbleItem extends BlockItem implements ProjectileItem {
	public BetweenstonePebbleItem(Properties properties) {
		super(BlockRegistry.BETWEENSTONE_PEBBLE.get(), properties);
	}

	@Override
	public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
		return new BetweenstonePebble(pos.x(), pos.y(), pos.z(), level, stack.copyWithCount(1), null);
	}
}
