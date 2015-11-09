package thebetweenlands.manual.gui.pages;

import java.util.ArrayList;
import java.util.Collections;

import thebetweenlands.manual.gui.entries.ManualEntry;
import thebetweenlands.manual.gui.widgets.ManualWidgetsBase;

/**
 * Created by Bart on 11-8-2015.
 */
public class ManualPage {
	public int x;
	public int y;
	public int pageNumber;

	public ArrayList<ManualWidgetsBase> widgets = new ArrayList<>();

	boolean rightPage = false;

	public ManualEntry entry;

	public ManualPage(ArrayList<ManualWidgetsBase> widgets) {
		this.widgets = widgets;
	}

	public ManualPage(ManualWidgetsBase... widgets) {
		Collections.addAll(this.widgets, widgets);
	}

	public ArrayList<ManualPage> setManualEntry(ManualEntry entry) {
		ArrayList<ManualPage> pages = new ArrayList<>();
		this.entry = entry;
		return pages;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
		rightPage = pageNumber % 2 == 0;

		for (ManualWidgetsBase widget : widgets)
			if (rightPage)
				widget.setPageToRight();
	}



	public void draw(int mouseX, int mouseY) {
		for (ManualWidgetsBase widget : widgets)
			widget.draw(mouseX, mouseY);
	}

	public void keyTyped(char c, int key) {
		for (ManualWidgetsBase widget : widgets)
			widget.keyTyped(c, key);
	}

	public void mouseClicked(int x, int y, int button) {
		for (ManualWidgetsBase widget : widgets)
			widget.mouseClicked(x, y, button);
	}

	public void updateScreen() {
		for (ManualWidgetsBase widget : widgets)
			widget.updateScreen();
	}
}
