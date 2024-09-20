package thebetweenlands.common.item.armor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;

public class MarshRunnerBootsItem extends RubberBootsItem {

	private static final int MAX_WALK_TICKS = 30;

	public MarshRunnerBootsItem(Properties properties) {
		super(properties);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
		if (entity instanceof Player player && slot >= 36 && slot <= 39) {
			int walkTicksLeft = stack.getOrDefault(DataComponentRegistry.WALK_TICKS, 0);
			BlockState blockBelowPlayer = level.getBlockState(player.blockPosition().below());

			if (player.onGround() && blockBelowPlayer.is(BlockRegistry.SWAMP_WATER)) {
				player.setDeltaMovement(player.getDeltaMovement().multiply(1.0D / MAX_WALK_TICKS * walkTicksLeft, 1.0D, 1.0D / MAX_WALK_TICKS * walkTicksLeft));
			}

			if (!level.isClientSide()) {
				boolean playerOnGround = player.onGround() && !player.isInWater() && !blockBelowPlayer.is(BlockRegistry.SWAMP_WATER);
				if (walkTicksLeft == 0 || playerOnGround) {
					stack.set(DataComponentRegistry.WALK_TICKS, MAX_WALK_TICKS);
				} else {
					if (walkTicksLeft > 1) {
						stack.set(DataComponentRegistry.WALK_TICKS, --walkTicksLeft);
					}
				}
			}
		}
	}

	public static boolean checkPlayerWalkOnWater(Player player) {
		if (player.isShiftKeyDown() || ElixirEffectRegistry.EFFECT_HEAVYWEIGHT.get().isActive(player)) return false;
		ItemStack boots = player.getInventory().getArmor(0);
		if (!boots.isEmpty() && boots.getItem() instanceof MarshRunnerBootsItem) {
			return boots.getOrDefault(DataComponentRegistry.WALK_TICKS, 0) > 1;
		}
		return false;
	}

	public static double getWalkPercentage(Player player) {
		ItemStack boots = player.getInventory().getArmor(0);
		if (!boots.isEmpty() && boots.getItem() instanceof MarshRunnerBootsItem) {
			if (boots.has(DataComponentRegistry.WALK_TICKS)) {
				return (double) boots.get(DataComponentRegistry.WALK_TICKS) / (double) MAX_WALK_TICKS;
			}
		}
		return 0.0D;
	}
}
