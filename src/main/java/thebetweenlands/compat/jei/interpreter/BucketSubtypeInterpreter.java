package thebetweenlands.compat.jei.interpreter;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.component.item.AspectContents;
import thebetweenlands.common.registries.DataComponentRegistry;

public class BucketSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
	public static final BucketSubtypeInterpreter INSTANCE = new BucketSubtypeInterpreter();

	private BucketSubtypeInterpreter() {
	}

	@Override
	@Nullable
	public Object getSubtypeData(ItemStack ingredient, UidContext context) {
		SimpleFluidContent contents = ingredient.get(DataComponentRegistry.STORED_FLUID);
		if (contents == null) {
			return null;
		}
		return contents.getFluid();
	}

	@Override
	public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
		return getStringName(ingredient);
	}

	public String getStringName(ItemStack itemStack) {
		if (itemStack.getComponentsPatch().isEmpty()) {
			return "";
		}
		SimpleFluidContent contents = itemStack.getOrDefault(DataComponentRegistry.STORED_FLUID, SimpleFluidContent.EMPTY);
		String itemDescriptionId = itemStack.getItem().getDescriptionId();
		String liquid = contents.getFluidHolder().getRegisteredName();
		return itemDescriptionId + ".fluid." + liquid;
	}
}
