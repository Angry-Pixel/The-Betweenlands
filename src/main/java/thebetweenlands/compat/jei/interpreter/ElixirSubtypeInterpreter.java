package thebetweenlands.compat.jei.interpreter;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.component.item.ElixirContents;
import thebetweenlands.common.registries.DataComponentRegistry;

public class ElixirSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
	public static final ElixirSubtypeInterpreter INSTANCE = new ElixirSubtypeInterpreter();

	private ElixirSubtypeInterpreter() {
	}

	@Override
	@Nullable
	public Object getSubtypeData(ItemStack ingredient, UidContext context) {
		ElixirContents contents = ingredient.get(DataComponentRegistry.ELIXIR_CONTENTS);
		if (contents == null) {
			return null;
		}
		return contents.elixir()
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
		ElixirContents contents = itemStack.getOrDefault(DataComponentRegistry.ELIXIR_CONTENTS, ElixirContents.EMPTY);
		String itemDescriptionId = itemStack.getItem().getDescriptionId();
		String elixirId = contents.elixir().map(Holder::getRegisteredName).orElse("none");
		return itemDescriptionId + ".elixir." + elixirId;
	}
}
