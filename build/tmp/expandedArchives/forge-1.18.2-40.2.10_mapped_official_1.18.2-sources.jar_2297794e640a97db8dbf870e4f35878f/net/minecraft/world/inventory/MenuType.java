package net.minecraft.world.inventory;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Inventory;

public class MenuType<T extends AbstractContainerMenu> extends net.minecraftforge.registries.ForgeRegistryEntry<MenuType<?>> implements net.minecraftforge.common.extensions.IForgeMenuType<T> {
   public static final MenuType<ChestMenu> GENERIC_9x1 = register("generic_9x1", ChestMenu::oneRow);
   public static final MenuType<ChestMenu> GENERIC_9x2 = register("generic_9x2", ChestMenu::twoRows);
   public static final MenuType<ChestMenu> GENERIC_9x3 = register("generic_9x3", ChestMenu::threeRows);
   public static final MenuType<ChestMenu> GENERIC_9x4 = register("generic_9x4", ChestMenu::fourRows);
   public static final MenuType<ChestMenu> GENERIC_9x5 = register("generic_9x5", ChestMenu::fiveRows);
   public static final MenuType<ChestMenu> GENERIC_9x6 = register("generic_9x6", ChestMenu::sixRows);
   public static final MenuType<DispenserMenu> GENERIC_3x3 = register("generic_3x3", DispenserMenu::new);
   public static final MenuType<AnvilMenu> ANVIL = register("anvil", AnvilMenu::new);
   public static final MenuType<BeaconMenu> BEACON = register("beacon", BeaconMenu::new);
   public static final MenuType<BlastFurnaceMenu> BLAST_FURNACE = register("blast_furnace", BlastFurnaceMenu::new);
   public static final MenuType<BrewingStandMenu> BREWING_STAND = register("brewing_stand", BrewingStandMenu::new);
   public static final MenuType<CraftingMenu> CRAFTING = register("crafting", CraftingMenu::new);
   public static final MenuType<EnchantmentMenu> ENCHANTMENT = register("enchantment", EnchantmentMenu::new);
   public static final MenuType<FurnaceMenu> FURNACE = register("furnace", FurnaceMenu::new);
   public static final MenuType<GrindstoneMenu> GRINDSTONE = register("grindstone", GrindstoneMenu::new);
   public static final MenuType<HopperMenu> HOPPER = register("hopper", HopperMenu::new);
   public static final MenuType<LecternMenu> LECTERN = register("lectern", (p_39992_, p_39993_) -> {
      return new LecternMenu(p_39992_);
   });
   public static final MenuType<LoomMenu> LOOM = register("loom", LoomMenu::new);
   public static final MenuType<MerchantMenu> MERCHANT = register("merchant", MerchantMenu::new);
   public static final MenuType<ShulkerBoxMenu> SHULKER_BOX = register("shulker_box", ShulkerBoxMenu::new);
   public static final MenuType<SmithingMenu> SMITHING = register("smithing", SmithingMenu::new);
   public static final MenuType<SmokerMenu> SMOKER = register("smoker", SmokerMenu::new);
   public static final MenuType<CartographyTableMenu> CARTOGRAPHY_TABLE = register("cartography_table", CartographyTableMenu::new);
   public static final MenuType<StonecutterMenu> STONECUTTER = register("stonecutter", StonecutterMenu::new);
   private final MenuType.MenuSupplier<T> constructor;

   private static <T extends AbstractContainerMenu> MenuType<T> register(String p_39989_, MenuType.MenuSupplier<T> p_39990_) {
      return Registry.register(Registry.MENU, p_39989_, new MenuType<>(p_39990_));
   }

   public MenuType(MenuType.MenuSupplier<T> p_39984_) {
      this.constructor = p_39984_;
   }

   public T create(int p_39986_, Inventory p_39987_) {
      return this.constructor.create(p_39986_, p_39987_);
   }
   
   @Override
   public T create(int windowId, Inventory playerInv, net.minecraft.network.FriendlyByteBuf extraData) {
      if (this.constructor instanceof net.minecraftforge.network.IContainerFactory) {
         return ((net.minecraftforge.network.IContainerFactory<T>) this.constructor).create(windowId, playerInv, extraData);
      }
      return create(windowId, playerInv);
   }

   public interface MenuSupplier<T extends AbstractContainerMenu> {
      T create(int p_39995_, Inventory p_39996_);
   }
}
