package thebetweenlands.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;
import thebetweenlands.common.TheBetweenlands;

public class BLRecipeCategory extends EmiRecipeCategory {
	public final String name;
	private final Component title;
	private final EmiStack icon;

	public BLRecipeCategory(String name, ItemLike icon) {
		super(TheBetweenlands.prefix(name), EmiStack.of(icon));
		this.name = name;
		this.title = Component.translatable("jei.thebetweenlands.recipe." + name);
		this.icon = EmiStack.of(icon);
	}

	@Override
	public Component getName() {
		return this.title;
	}

	public EmiStack getIcon() {
		return this.icon;
	}
}
