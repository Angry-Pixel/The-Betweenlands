package thebetweenlands.client.gui.book;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ManualCategory {

	private final List<Page> pages = new ArrayList<>();
	private final List<Page> visiblePages = new ArrayList<>();

	private final LimitedList<Integer> lastPages = new LimitedList<>(100);

	private final Page blankPage = new Page("blank", false);
	@Nullable
	private Page currentPageLeft = null;
	@Nullable
	private Page currentPageRight = null;
	private int currentPage = 1;
	private final int indexPages;
	private final int number;
	private final String name;

	public ManualCategory(ArrayList<Page> pages, int number, String name) {
		int pageNumber = 1;
		ArrayList<Page> buttonPages = new ArrayList<>();
		ArrayList<Page> tempPages = new ArrayList<>();
		for (Page page : pages) {
			page.setPageNumber(pageNumber);
			if (page.isParent)
				buttonPages.add(page);
			tempPages.add(page);
			pageNumber++;
		}
		ArrayList<Page> buttonPagesNew;
		buttonPagesNew = PageCreators.pageCreatorButtons(buttonPages);
		this.indexPages = buttonPagesNew.size();
		this.pages.addAll(buttonPagesNew);
		this.pages.addAll(tempPages);
		this.number = number;
		this.name = name;
	}

	public ManualCategory(ArrayList<Page> pages, int number, String name, boolean customPageInitializing, int indexPages) {
		ArrayList<Page> buttonPagesNew;
		if (!customPageInitializing) {
			int pageNumber = 1;
			ArrayList<Page> buttonPages = new ArrayList<>();
			ArrayList<Page> tempPages = new ArrayList<>();
			for (Page page : pages) {
				page.setPageNumber(pageNumber);
				if (page.isParent)
					buttonPages.add(page);
				tempPages.add(page);
				pageNumber++;
			}
			buttonPagesNew = PageCreators.pageCreatorButtons(buttonPages);
			this.pages.addAll(buttonPagesNew);
			this.pages.addAll(tempPages);
			this.indexPages = buttonPagesNew.size();
		} else {
			this.pages.addAll(pages);
			this.indexPages = indexPages;
		}
		this.number = number;
		this.name = name;
	}

	public void init(HerbloreManualScreen screen, boolean force) {
		if (this.currentPageLeft == null || this.currentPageRight == null || force) {
			this.visiblePages.clear();
			for (Page page : this.pages)
				if (!page.isHidden || ManualManager.hasFoundPage(page.unlocalizedPageName, screen.getAspectManager(), screen.getRegistryLookup(), screen.getManual())) {
					this.visiblePages.add(page);
				}
			if (!this.visiblePages.isEmpty()) {
				this.currentPageLeft = this.visiblePages.get(0);
				this.currentPageLeft.setPageToLeft();
				if (this.visiblePages.size() > 1) {
					this.currentPageRight = this.visiblePages.get(1);
				} else {
					this.currentPageRight = blankPage;
				}
				this.currentPageRight.setPageToRight();
			}
		}
		if (this.currentPageLeft != null)
			this.currentPageLeft.init(screen);
		if (this.currentPageRight != null) {
			this.currentPageRight.init(screen);
		}
	}

	public int getCurrentPage() {
		return this.currentPage;
	}

	public int getCategoryNumber() {
		return this.number;
	}

	public int getIndexPages() {
		return this.indexPages;
	}

	public String getName() {
		return this.name;
	}

	public List<Page> getVisiblePages() {
		return this.visiblePages;
	}

	public void setPage(int pageNumber, HerbloreManualScreen screen, boolean rebuild) {
		if (this.currentPageLeft != null && this.currentPageRight != null) {
			this.lastPages.add(this.currentPage);
			if (pageNumber % 2 == 0) {
				pageNumber--;
			}
			if (pageNumber <= this.visiblePages.size() && pageNumber > 0) {
				this.currentPageLeft = this.visiblePages.get(pageNumber - 1);
				if (this.visiblePages.size() >= pageNumber + 1) {
					this.currentPageRight = this.visiblePages.get(pageNumber);
				} else {
					this.currentPageRight = this.blankPage;
				}
				this.currentPage = pageNumber;
			}
			if (rebuild) screen.rebuildWidgets();
			this.currentPageLeft.init(screen);
			this.currentPageLeft.setPageToLeft();
			this.currentPageRight.init(screen);
			this.currentPageRight.setPageToRight();
		}
	}

	public void nextPage(HerbloreManualScreen screen) {
		if (this.currentPage + 2 <= this.visiblePages.size()) {
			this.setPage(this.currentPage + 2, screen, true);
		}
	}

	public void refreshPage(HerbloreManualScreen screen) {
		if (this.currentPage + 2 <= this.visiblePages.size()) {
			this.setPage(this.currentPage, screen, true);
		}
	}

	public void previousPage(HerbloreManualScreen screen) {
		if (this.currentPage - 2 >= 1) {
			this.setPage(this.currentPage - 2, screen, true);
		}
	}

	public void previousOpenPage(HerbloreManualScreen screen) {
		if(!this.lastPages.isEmpty()) {
			int prevPage = this.lastPages.getLast();
			this.lastPages.removeLast();
			this.setPage(prevPage, screen, true);
			this.lastPages.removeLast();
		}
	}

	public void tick() {
		if (this.currentPageLeft != null) {
			this.currentPageLeft.tick();
		}
		if (this.currentPageRight != null) {
			this.currentPageRight.tick();
		}
	}

	@SuppressWarnings("serial")
	static class LimitedList<E> extends LinkedList<E> {
		private final int limit;

		public LimitedList(int limit) {
			this.limit = limit;
		}

		@Override
		public boolean add(E o) {
			boolean added = super.add(o);
			while (added && this.size() > this.limit) {
				super.remove();
			}
			return added;
		}
	}
}
