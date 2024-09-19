package thebetweenlands.api.item.amphibious;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface TriggerableAmphibiousArmorUpgrade extends AmphibiousArmorUpgrade {

	boolean onTrigger(Level level, Player player, ItemStack heldStack, ItemStack armorStack, int upgradeAmount);
}
