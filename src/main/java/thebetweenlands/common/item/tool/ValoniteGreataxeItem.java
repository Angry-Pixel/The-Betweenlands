package thebetweenlands.common.item.tool;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import thebetweenlands.api.item.ExtendedReach;

public class ValoniteGreataxeItem extends AxeItem implements ExtendedReach {

	public ValoniteGreataxeItem(Tier tier, Properties properties) {
		super(tier, properties);
	}

	@Override
	public double getReachModifier(Player player, ItemStack stack) {
		// TODO Range is 5 just for testing
		return 5;
	}

}
