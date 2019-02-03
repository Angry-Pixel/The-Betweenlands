package thebetweenlands.common.item.herblore;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.item.IRuneItem;
import thebetweenlands.api.rune.gui.IRuneContainer;
import thebetweenlands.api.rune.gui.IRuneGui;
import thebetweenlands.api.rune.gui.IRuneContainerFactory;
import thebetweenlands.api.rune.gui.RuneMenuType;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.client.gui.inventory.runechainaltar.DefaultRuneGui;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.inventory.container.runechainaltar.DefaultRuneContainer;

public class ItemRune extends Item implements IRuneItem {
	private final Factory factory = new Factory(this);
	private AbstractRune.Blueprint<?> blueprint;

	public ItemRune(AbstractRune.Blueprint<?> blueprint) {
		this.setMaxStackSize(1);
		this.setMaxDamage(30);
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
		this.blueprint = blueprint;
	}

	@Override
	public IRuneContainerFactory getRuneContainerFactory(ItemStack stack) {
		return this.factory;
	}

	private static class Factory implements IRuneContainerFactory {
		private ItemRune item;

		private Factory(ItemRune item) {
			this.item = item;
		}

		@Override
		public IRuneContainer createContainer() {
			return new DefaultRuneContainer(this.item.getRegistryName(), this.item.blueprint);
		}

		@Override
		public IRuneGui createGui(RuneMenuType menu) {
			return new DefaultRuneGui(menu);
		}
	}
}
