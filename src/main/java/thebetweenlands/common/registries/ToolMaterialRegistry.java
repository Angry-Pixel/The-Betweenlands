package thebetweenlands.common.registries;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import thebetweenlands.common.datagen.tags.BLBlockTagProvider;
import thebetweenlands.common.datagen.tags.BLItemTagProvider;

public class ToolMaterialRegistry {

	public static final Tier WEEDWOOD = new SimpleTier(BLBlockTagProvider.INCORRECT_FOR_WEEDWOOD_TOOL, 80, 2.0F, 0.0F, 0, () -> Ingredient.of(BLItemTagProvider.REPAIRS_WEEDWOOD_TOOLS));
	public static final Tier BONE = new SimpleTier(BLBlockTagProvider.INCORRECT_FOR_BONE_TOOL, 320, 4.0F, 1.0F, 0, () -> Ingredient.of(BLItemTagProvider.REPAIRS_BONE_TOOLS));
	public static final Tier OCTINE = new SimpleTier(BLBlockTagProvider.INCORRECT_FOR_OCTINE_TOOL, 900, 6.0F, 2.0F, 0, () -> Ingredient.of(BLItemTagProvider.REPAIRS_OCTINE_TOOLS));
	public static final Tier VALONITE = new SimpleTier(BLBlockTagProvider.INCORRECT_FOR_VALONITE_TOOL, 2500, 8.0F, 3.0F, 0, () -> Ingredient.of(BLItemTagProvider.REPAIRS_VALONITE_TOOLS));
}
