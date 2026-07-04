package thebetweenlands.common.item.herblore;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IItemExtension;

public class DentrothystVialItem extends Item implements IItemExtension {

	private final Holder<Item> fullAspectBottle;
	private final Holder<Item> fullElixirBottle;

	public DentrothystVialItem(Holder<Item> fullAspectBottle, Holder<Item> fullElixirBottle, Properties properties) {
		super(properties);
		this.fullAspectBottle = fullAspectBottle;
		this.fullElixirBottle = fullElixirBottle;
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, net.minecraft.world.level.LevelReader level, BlockPos pos, Player player) {
        return true;
    }

	public Holder<Item> getFullAspectBottle() {
		return this.fullAspectBottle;
	}

	public Holder<Item> getFullElixirBottle() {
		return this.fullElixirBottle;
	}
}
