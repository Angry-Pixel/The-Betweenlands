package thebetweenlands.api.rune.gui;

import java.util.Collection;

import javax.annotation.Nullable;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IRuneSubMenuFactory {
	public static enum MenuType {
		PRIMARY, SECONDARY
	}

	public static interface IRuneAltarContainer {
		//TODO Methods to get rune items, get/set links, add slots etc.

		public ItemStack getRuneItemStack(int runeIndex);

		public void addSlot(Slot slot);
	}

	public static interface IRuneAltarGui {

	}

	public static interface IRuneSubMenu {
		public IRuneAltarContainer getContainer();

		@Nullable
		public IRuneAltarGui getGui();

		public int getRuneIndex();

		public NBTTagCompound getData();
	}

	public static interface IGuiRuneMark {
		public boolean isInside(int mouseX, int mouseY);

		public int getMarkIndex();

		public int getCenterX();

		public int getCenterY();
	}

	public static interface IRuneSubGui {
		public void init(IRuneSubMenu menu, IRuneSubContainer container);

		public void close();

		public void update();

		public void draw(int mouseX, int mouseY);

		public void drawMarkConnection(IGuiRuneMark mark, int targetX, int targetY, boolean linked);

		public boolean onKeyTyped(char typedChar, int keyCode);

		public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton);

		public boolean onMouseReleased(int mouseX, int mouseY, int state);

		public void onParentResized(int w, int h);

		public boolean onLinkMark(IGuiRuneMark mark, int outputRune, int outputMark);

		public Collection<IGuiRuneMark> getInteractableMarks();

		public int getMinX();

		public int getMinY();

		public int getMaxX();

		public int getMaxY();
	}

	public static interface IRuneSubContainer {
		public void init(IRuneSubMenu menu);

		public void close();
	}

	public IRuneSubContainer createSubContainer(MenuType type);

	public IRuneAltarGui createSubGui(MenuType type);
}
