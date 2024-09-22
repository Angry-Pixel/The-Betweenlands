package thebetweenlands.common.item.herblore;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.common.component.item.AspectContents;
import thebetweenlands.common.registries.AspectTypeRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;

import java.util.List;

public class AspectVialItem extends Item {

	public AspectVialItem(Properties properties) {
		super(properties);
	}

	@Override
	public Component getName(ItemStack stack) {
		if (stack.has(DataComponentRegistry.ASPECT_CONTENTS)) {
			AspectContents contents = stack.get(DataComponentRegistry.ASPECT_CONTENTS);
			if (contents.aspect().isPresent()) {
				return Component.translatable("item.thebetweenlands.aspect_vial.aspect", Component.translatable(Util.makeDescriptionId("aspect", contents.aspect().get().getKey().location())), Aspect.ASPECT_AMOUNT_FORMAT.format(contents.amount() / 1000.0D));
			}
		}
		return super.getName(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		if (stack.has(DataComponentRegistry.ASPECT_CONTENTS) && stack.get(DataComponentRegistry.ASPECT_CONTENTS).aspect().isPresent()) {
			if (stack.get(DataComponentRegistry.ASPECT_CONTENTS).aspect().get().is(AspectTypeRegistry.BYARIIS)) {
				tooltip.add(Component.translatable("item.thebetweenlands.aspect_vial.byariis"));
			} else if (stack.get(DataComponentRegistry.ASPECT_CONTENTS).aspect().get().is(AspectTypeRegistry.FREIWYNN)) {
				tooltip.add(Component.translatable("item.thebetweenlands.aspect_vial.freiwynn"));
			}
		}
	}
}
