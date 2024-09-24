package thebetweenlands.api.item.amphibious;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ToggleableAmphibiousArmorUpgrade extends AmphibiousArmorUpgrade {

	boolean onToggle(Level level, Player player, ItemStack heldStack);
}
