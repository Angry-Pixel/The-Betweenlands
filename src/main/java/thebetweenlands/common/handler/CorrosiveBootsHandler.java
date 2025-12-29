package thebetweenlands.common.handler;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thebetweenlands.common.entity.TriggeredFallingBlock;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.EntityRegistry;

public class CorrosiveBootsHandler {
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		if (!event.getEntity().level().isClientSide() && !(event.getEntity() instanceof FakePlayer)) {
			Level level = event.getEntity().level();
			if (level != null && level.getDifficulty() != Difficulty.PEACEFUL && level.random.nextInt(20) == 0) {
				ItemStack boots = event.getEntity().getItemBySlot(EquipmentSlot.FEET);
				BlockState stateUnder = level.getBlockState(event.getEntity().blockPosition().below());
					if (!boots.isEmpty() && boots.has(DataComponentRegistry.CORROSIVE.get())) {
						if (boots.get(DataComponentRegistry.CORROSIVE.get())) {
							if (event.getEntity().isInWaterOrRain())
								boots.set(DataComponentRegistry.CORROSIVE.get(), false);
							else if (stateUnder.getDestroyProgress(event.getEntity(), level, event.getEntity().blockPosition().below()) > 0.0001) {
								if (!level.isEmptyBlock(event.getEntity().blockPosition().below()) && !level.getBlockState(event.getEntity().blockPosition().below()).hasBlockEntity() && level.isEmptyBlock(event.getEntity().blockPosition().below(2))) {
									TriggeredFallingBlock falling_block = new TriggeredFallingBlock(EntityRegistry.TRIGGERED_FALLING_BLOCK.get(), level);
									falling_block.setPos(event.getEntity().blockPosition().below().getCenter());
									falling_block.setWalkway(true);
									falling_block.setTemporary(true);
									level.addFreshEntity(falling_block);
								}
							}
						}
					}
			}
		}
	}
}
