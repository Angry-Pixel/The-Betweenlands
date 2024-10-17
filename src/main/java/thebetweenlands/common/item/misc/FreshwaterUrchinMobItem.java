package thebetweenlands.common.item.misc;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.context.UseOnContext;
import thebetweenlands.common.entity.creature.FreshwaterUrchin;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class FreshwaterUrchinMobItem extends MobItem<FreshwaterUrchin> {
	public FreshwaterUrchinMobItem(Properties properties) {
		super(properties, 3.0D, EntityRegistry.FRESHWATER_URCHIN.get(), null);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		var result = JukeboxPlayable.tryInsertIntoJukebox(context.getLevel(), context.getClickedPos(), ItemRegistry.RECORD_DEEP_WATER_THEME.toStack(), context.getPlayer()).result();
		if (result.consumesAction()) {
			context.getItemInHand().consume(1, context.getPlayer());
		}
		return result;
	}
}
