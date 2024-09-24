package thebetweenlands.api.item.amphibious;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface TickingAmphibiousArmorUpgrade extends AmphibiousArmorUpgrade {

	void onArmorTick(Level level, Player player, ItemStack stack, int upgradeCount, int armorCount);
}
