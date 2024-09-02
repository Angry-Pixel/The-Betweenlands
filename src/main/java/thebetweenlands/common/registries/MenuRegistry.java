package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagRegistry;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.client.gui.screen.MortarScreen;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.inventory.*;

public class MenuRegistry {

	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, TheBetweenlands.ID);

	public static final DeferredHolder<MenuType<?>, MenuType<AnimatorMenu>> ANIMATOR = MENUS.register("animator", () -> IMenuTypeExtension.create(AnimatorMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<DruidAltarMenu>> DRUID_ALTAR = MENUS.register("druid_altar", () -> IMenuTypeExtension.create(DruidAltarMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<FishTrimmingTableMenu>> FISH_TRIMMING_TABLE = MENUS.register("fish_trimming_table", () -> IMenuTypeExtension.create(FishTrimmingTableMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<MortarMenu>> MORTAR = MENUS.register("mortar", () -> new MenuType<>(MortarMenu::new, FeatureFlags.REGISTRY.allFlags()));
	public static final DeferredHolder<MenuType<?>, MenuType<WeedwoodCraftingMenu>> WEEDWOOD_CRAFTING_TABLE = MENUS.register("weedwood_crafting_table", () -> IMenuTypeExtension.create(WeedwoodCraftingMenu::new));
}
