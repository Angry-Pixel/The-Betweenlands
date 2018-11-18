package thebetweenlands.common.item.herblore;

import java.util.Collection;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.item.IRuneItem;
import thebetweenlands.api.rune.gui.IGuiRuneMark;
import thebetweenlands.api.rune.gui.IRuneContainer;
import thebetweenlands.api.rune.gui.IRuneContainerContext;
import thebetweenlands.api.rune.gui.IRuneGui;
import thebetweenlands.api.rune.gui.IRuneLink;
import thebetweenlands.api.rune.gui.IRuneMenuFactory;
import thebetweenlands.api.rune.gui.RuneMenuType;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.client.tab.BLCreativeTabs;

public class ItemRune extends Item implements IRuneItem {
	private AbstractRune.Blueprint<?> blueprint;

	public ItemRune(AbstractRune.Blueprint<?> blueprint) {
		this.setMaxStackSize(1);
		this.setMaxDamage(30);
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
		this.blueprint = blueprint;
	}

	@Override
	public AbstractRune.Blueprint<?> getRuneBlueprint(ItemStack stack, NBTTagCompound data) {
		return this.blueprint;
	}

	@Override
	public IRuneMenuFactory getRuneMenuFactory(ItemStack stack) {
		return new Factory(this);
	}

	private static class Factory implements IRuneMenuFactory {
		private ItemRune item;

		private Factory(ItemRune item) {
			this.item = item;
		}

		@Override
		public IRuneContainer createContainer() {
			return new IRuneContainer() {

				@Override
				public void init(IRuneContainerContext context) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onMarkLinked(int input, IRuneLink link) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onMarkUnlinked(int input, IRuneLink link) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onLinksMoved(int fromRuneIndex, int toRuneIndex) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onRuneShifted(int fromRuneIndex, int toRuneIndex) {
					// TODO Auto-generated method stub

				}

				@Override
				public ResourceLocation getId() {
					return item.getRegistryName();
				}

			};
		}

		@Override
		public IRuneGui createGui(RuneMenuType type) {
			return new IRuneGui() {

				private IRuneContainer container;

				@Override
				public void init(IRuneContainerContext context, IRuneContainer container) {
					// TODO Auto-generated method stub
					this.container = container;
				}

				@Override
				public IRuneContainer getContainer() {
					// TODO Auto-generated method stub
					return this.container;
				}

				@Override
				public void close() {
					// TODO Auto-generated method stub

				}

				@Override
				public void update() {
					// TODO Auto-generated method stub

				}

				@Override
				public void draw(int mouseX, int mouseY) {
					// TODO Auto-generated method stub

				}

				@Override
				public void drawMarkConnection(IGuiRuneMark mark, int targetX, int targetY, boolean linked) {
					// TODO Auto-generated method stub

				}

				@Override
				public boolean onKeyTyped(char typedChar, int keyCode) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean onMouseReleased(int mouseX, int mouseY, int state) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public void onParentResized(int w, int h) {
					// TODO Auto-generated method stub

				}

				@Override
				public Collection<IGuiRuneMark> getInteractableMarks() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public int getMinX() {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public int getMinY() {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public int getMaxX() {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public int getMaxY() {
					// TODO Auto-generated method stub
					return 0;
				}

			};
		}
	}

	/*@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 0.5D;
	}*/
}
