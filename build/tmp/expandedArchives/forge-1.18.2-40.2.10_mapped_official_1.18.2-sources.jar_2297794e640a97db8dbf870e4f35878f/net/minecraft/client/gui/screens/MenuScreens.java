package net.minecraft.client.gui.screens;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.client.gui.screens.inventory.BlastFurnaceScreen;
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen;
import net.minecraft.client.gui.screens.inventory.CartographyTableScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.DispenserScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.client.gui.screens.inventory.FurnaceScreen;
import net.minecraft.client.gui.screens.inventory.GrindstoneScreen;
import net.minecraft.client.gui.screens.inventory.HopperScreen;
import net.minecraft.client.gui.screens.inventory.LecternScreen;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.client.gui.screens.inventory.SmokerScreen;
import net.minecraft.client.gui.screens.inventory.StonecutterScreen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class MenuScreens {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<MenuType<?>, MenuScreens.ScreenConstructor<?, ?>> SCREENS = Maps.newHashMap();

   public static <T extends AbstractContainerMenu> void create(@Nullable MenuType<T> p_96202_, Minecraft p_96203_, int p_96204_, Component p_96205_) {
      getScreenFactory(p_96202_, p_96203_, p_96204_, p_96205_).ifPresent(f -> f.fromPacket(p_96205_, p_96202_, p_96203_, p_96204_));
   }

   public static <T extends AbstractContainerMenu> java.util.Optional<ScreenConstructor<T, ?>> getScreenFactory(@Nullable MenuType<T> p_96202_, Minecraft p_96203_, int p_96204_, Component p_96205_) {
      if (p_96202_ == null) {
         LOGGER.warn("Trying to open invalid screen with name: {}", (Object)p_96205_.getString());
      } else {
         MenuScreens.ScreenConstructor<T, ?> screenconstructor = getConstructor(p_96202_);
         if (screenconstructor == null) {
            LOGGER.warn("Failed to create screen for menu type: {}", (Object)Registry.MENU.getKey(p_96202_));
         } else {
            return java.util.Optional.of(screenconstructor);
         }
      }
      return java.util.Optional.empty();
   }

   @Nullable
   private static <T extends AbstractContainerMenu> MenuScreens.ScreenConstructor<T, ?> getConstructor(MenuType<T> p_96200_) {
      return (MenuScreens.ScreenConstructor<T, ?>)SCREENS.get(p_96200_);
   }

   public static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> p_96207_, MenuScreens.ScreenConstructor<M, U> p_96208_) {
      MenuScreens.ScreenConstructor<?, ?> screenconstructor = SCREENS.put(p_96207_, p_96208_);
      if (screenconstructor != null) {
         throw new IllegalStateException("Duplicate registration for " + Registry.MENU.getKey(p_96207_));
      }
   }

   public static boolean selfTest() {
      boolean flag = false;

      for(MenuType<?> menutype : Registry.MENU) {
         if (!SCREENS.containsKey(menutype)) {
            LOGGER.debug("Menu {} has no matching screen", (Object)Registry.MENU.getKey(menutype));
            flag = true;
         }
      }

      return flag;
   }

   static {
      register(MenuType.GENERIC_9x1, ContainerScreen::new);
      register(MenuType.GENERIC_9x2, ContainerScreen::new);
      register(MenuType.GENERIC_9x3, ContainerScreen::new);
      register(MenuType.GENERIC_9x4, ContainerScreen::new);
      register(MenuType.GENERIC_9x5, ContainerScreen::new);
      register(MenuType.GENERIC_9x6, ContainerScreen::new);
      register(MenuType.GENERIC_3x3, DispenserScreen::new);
      register(MenuType.ANVIL, AnvilScreen::new);
      register(MenuType.BEACON, BeaconScreen::new);
      register(MenuType.BLAST_FURNACE, BlastFurnaceScreen::new);
      register(MenuType.BREWING_STAND, BrewingStandScreen::new);
      register(MenuType.CRAFTING, CraftingScreen::new);
      register(MenuType.ENCHANTMENT, EnchantmentScreen::new);
      register(MenuType.FURNACE, FurnaceScreen::new);
      register(MenuType.GRINDSTONE, GrindstoneScreen::new);
      register(MenuType.HOPPER, HopperScreen::new);
      register(MenuType.LECTERN, LecternScreen::new);
      register(MenuType.LOOM, LoomScreen::new);
      register(MenuType.MERCHANT, MerchantScreen::new);
      register(MenuType.SHULKER_BOX, ShulkerBoxScreen::new);
      register(MenuType.SMITHING, SmithingScreen::new);
      register(MenuType.SMOKER, SmokerScreen::new);
      register(MenuType.CARTOGRAPHY_TABLE, CartographyTableScreen::new);
      register(MenuType.STONECUTTER, StonecutterScreen::new);
   }

   @OnlyIn(Dist.CLIENT)
   public interface ScreenConstructor<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> {
      default void fromPacket(Component p_96210_, MenuType<T> p_96211_, Minecraft p_96212_, int p_96213_) {
         U u = this.create(p_96211_.create(p_96213_, p_96212_.player.getInventory()), p_96212_.player.getInventory(), p_96210_);
         p_96212_.player.containerMenu = u.getMenu();
         p_96212_.setScreen(u);
      }

      U create(T p_96215_, Inventory p_96216_, Component p_96217_);
   }
}
