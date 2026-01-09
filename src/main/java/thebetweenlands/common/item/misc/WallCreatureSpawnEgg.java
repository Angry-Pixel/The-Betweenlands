package thebetweenlands.common.item.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import thebetweenlands.common.entity.monster.wall.AbstractWallCreature;
import thebetweenlands.common.registries.EntityRegistry;

import java.util.Objects;
import java.util.function.Supplier;

public class WallCreatureSpawnEgg extends DeferredSpawnEggItem {

	public WallCreatureSpawnEgg(Supplier<? extends EntityType<? extends Mob>> type, int primaryColor, int secondaryColor, Properties properties) {
		super(type, primaryColor, secondaryColor, properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		return InteractionResultHolder.pass(player.getItemInHand(hand));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		if (!(level instanceof ServerLevel serverLevel)) {
			return InteractionResult.SUCCESS;
		} else {
			ItemStack itemstack = context.getItemInHand();
			Direction direction = context.getClickedFace();
			BlockPos blockpos = context.getClickedPos();
			Entity entity = this.getType(itemstack).create(serverLevel);

			if (!(entity instanceof AbstractWallCreature creature)) return InteractionResult.PASS;

			if (!creature.canResideInBlock(blockpos, direction, Direction.UP)) {
				return InteractionResult.PASS;
			}
			creature.setPositionToAnchor(blockpos, direction, Direction.UP);
			level.addFreshEntity(creature);
			itemstack.consume(1, context.getPlayer());
			level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
			return InteractionResult.CONSUME;
		}
	}
}
