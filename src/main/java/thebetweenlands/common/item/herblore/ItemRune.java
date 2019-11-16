package thebetweenlands.common.item.herblore;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.rune.IRuneContainer;
import thebetweenlands.api.rune.IRuneContainerFactory;
import thebetweenlands.api.rune.IRuneGui;
import thebetweenlands.api.rune.RuneMenuType;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.client.gui.inventory.runechainaltar.DefaultRuneGui;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.inventory.container.runechainaltar.DefaultRuneContainer;

public class ItemRune extends Item {
	private final Factory factory = new Factory(this);
	protected AbstractRune.Blueprint<?> blueprint;

	public ItemRune() {
		
	}
	
	public ItemRune(AbstractRune.Blueprint<?> blueprint) {
		this.setMaxStackSize(1);
		this.setMaxDamage(30);
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
		this.blueprint = blueprint;
	}

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
