package thebetweenlands.common.items;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import thebetweenlands.api.item.IExtendedReach;

public class ValoniteGreataxeItem extends AxeItem implements IExtendedReach {

	public ValoniteGreataxeItem(Tier tier, Properties properties) {
		super(tier, properties);
	}

	@Override
	public double getReachModifier(Player player, ItemStack stack) {
		// TODO Range is 5 just for testing
		return 5;
	}

}
