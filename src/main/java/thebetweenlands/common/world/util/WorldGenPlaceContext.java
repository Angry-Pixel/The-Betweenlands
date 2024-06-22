package thebetweenlands.common.world.util;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;

public class WorldGenPlaceContext extends UseOnContext {
	public WorldGenPlaceContext(Player p_43709_, InteractionHand p_43710_, BlockHitResult p_43711_) {
		super(p_43709_, p_43710_, p_43711_);
	}
}
