package thebetweenlands.common.item.herblore;

import net.minecraft.item.ItemStack;
import thebetweenlands.api.rune.IRuneContainer;
import thebetweenlands.api.rune.IRuneContainerFactory;
import thebetweenlands.api.rune.IRuneGui;
import thebetweenlands.api.rune.RuneMenuType;
import thebetweenlands.client.gui.inventory.runeweavingtable.RuneBranchingGui;
import thebetweenlands.common.herblore.rune.RuneBranching;
import thebetweenlands.common.inventory.container.runeweavingtable.DefaultRuneContainer;

public class ItemRuneBranching extends ItemRune {
	private final Factory factory = new Factory(this);

	public ItemRuneBranching() {
		super(new RuneBranching.Blueprint());
	}

	@Override
	public IRuneContainerFactory getRuneContainerFactory(ItemStack stack) {
		return super.getRuneContainerFactory(stack);
	}

	private static class Factory implements IRuneContainerFactory {
		private ItemRuneBranching item;

		private Factory(ItemRuneBranching item) {
			this.item = item;
		}

		@Override
		public IRuneContainer createContainer() {
			return new DefaultRuneContainer(this.item.getRegistryName(), this.item.blueprint);
		}

		@Override
		public IRuneGui createGui(RuneMenuType menu) {
			return new RuneBranchingGui(menu);
		}
	}
}
