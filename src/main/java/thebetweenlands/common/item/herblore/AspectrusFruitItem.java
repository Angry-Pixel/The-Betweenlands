package thebetweenlands.common.item.herblore;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.component.item.AspectContents;
import thebetweenlands.common.registries.DataComponentRegistry;

public class AspectrusFruitItem extends Item {
	public AspectrusFruitItem(Properties properties) {
		super(properties);
	}

	@Override
	public Component getName(ItemStack stack) {
		return stack.getOrDefault(DataComponentRegistry.ASPECT_CONTENTS, AspectContents.EMPTY).aspect()
			.map(aspect -> Component.translatable("item.thebetweenlands.apsectrus_fruit.aspect", Component.translatable(Util.makeDescriptionId("aspect", aspect.getKey().location()))))
			.orElse(super.getName(stack).copy());
	}
}
