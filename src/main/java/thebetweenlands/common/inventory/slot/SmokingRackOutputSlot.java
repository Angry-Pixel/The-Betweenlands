package thebetweenlands.common.inventory.slot;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;

public class SmokingRackOutputSlot extends Slot {

	public SmokingRackOutputSlot(Container container, int slot, int x, int y) {
		super(container, slot, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}

	@Override
	public void onTake(Player player, ItemStack stack) {
		if(player instanceof ServerPlayer sp) {
			AdvancementCriteriaRegistry.SMOKE_ITEM.get().trigger(sp);
		}
		super.onTake(player, stack);
	}
}
