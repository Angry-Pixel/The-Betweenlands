package thebetweenlands.compat.jei.interpreter;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.component.item.AspectContents;
import thebetweenlands.common.registries.DataComponentRegistry;

public class AspectSubtypeInterpreter  implements ISubtypeInterpreter<ItemStack> {
	public static final AspectSubtypeInterpreter INSTANCE = new AspectSubtypeInterpreter();

	private AspectSubtypeInterpreter() {
	}

	@Override
	@Nullable
	public Object getSubtypeData(ItemStack ingredient, UidContext context) {
		AspectContents contents = ingredient.get(DataComponentRegistry.ASPECT_CONTENTS);
		if (contents == null) {
			return null;
		}
		return contents.aspect()
			.orElse(null);
	}

	@Override
	public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
		return getStringName(ingredient);
	}

	public String getStringName(ItemStack itemStack) {
		if (itemStack.getComponentsPatch().isEmpty()) {
			return "";
		}
		AspectContents contents = itemStack.getOrDefault(DataComponentRegistry.ASPECT_CONTENTS, AspectContents.EMPTY);
		String itemDescriptionId = itemStack.getItem().getDescriptionId();
		String aspectId = contents.aspect().map(Holder::getRegisteredName).orElse("none");
		return itemDescriptionId + ".aspect." + aspectId;
	}
}
