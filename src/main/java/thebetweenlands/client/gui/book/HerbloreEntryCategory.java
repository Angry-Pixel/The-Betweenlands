package thebetweenlands.client.gui.book;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.aspect.registry.AspectItem;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.client.gui.book.widget.ButtonWidget;
import thebetweenlands.client.gui.book.widget.ImageWidget;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.item.ElixirContents;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.ArrayList;

public class HerbloreEntryCategory {

	public static ArrayList<ManualCategory> CATEGORIES = new ArrayList<>();
	public static ManualCategory aspectCategory;
	public static ArrayList<Page> aspectPages = new ArrayList<>();
	public static ArrayList<Page> itemPages = new ArrayList<>();
	public static ArrayList<Page> elixirPages = new ArrayList<>();

	public static ManualCategory elixirCategory;

	public static void init(HolderLookup.Provider registries) {
		CATEGORIES.clear();
		initAspectEntries(registries);
		initElixirEntries(registries);
	}

	public static void initAspectEntries(HolderLookup.Provider registries) {
		int indexPages = 3;
		aspectPages.clear();
		itemPages.clear();
		ArrayList<Page> temp = new ArrayList<>();
		ArrayList<Page> entryPages = new ArrayList<>();
		aspectPages.add(new Page("aspect_info", false, new ImageWidget(16, 12, 112, 150, TheBetweenlands.prefix("textures/gui/manual/aspect_info_page.png"))));
		for (Holder<AspectType> aspect : registries.lookupOrThrow(BLRegistries.Keys.ASPECT_TYPES).listElements().toList()) {
			aspectPages.addAll(PageCreators.makeAspectPage(aspect, registries));
		}

		entryPages.add(new Page("aspect_list", false, new ImageWidget(16, 12, 112, 150, TheBetweenlands.prefix("textures/gui/manual/aspects_page.png"))));
		int pageNumber = 1;
		for (Page page : aspectPages) {
			page.setPageNumber(pageNumber);
			temp.add(page);
			pageNumber++;
		}
		entryPages.addAll(PageCreators.pageCreatorButtons(temp));
		indexPages += PageCreators.pageCreatorButtons(temp).size();

		for (Holder<AspectItem> aspect : registries.lookupOrThrow(BLRegistries.Keys.ASPECT_ITEMS).listElements().toList()) {
			itemPages.addAll(PageCreators.makeAspectItemPage(aspect));
		}

		ArrayList<Page> tempItems = new ArrayList<>();
		int tempNum = pageNumber;
		pageNumber++;
		while (!itemPages.isEmpty()) {
			Page currentFirst = null;
			for (Page page : itemPages) {
				if (currentFirst == null)
					currentFirst = page;
				else {
					String pageName = page.pageName.getString().toLowerCase();
					char[] characters = pageName.toCharArray();
					String pageNameFirst = currentFirst.pageName.getString().toLowerCase();
					char[] charactersFirst = pageNameFirst.toCharArray();
					for (int i = 0; i < characters.length; i++) {
						if (charactersFirst.length > i) {
							if (characters[i] > charactersFirst[i]) {
								break;
							} else if (characters[i] < charactersFirst[i]) {
								currentFirst = page;
								break;
							}
						}
					}
				}
			}
			itemPages.remove(currentFirst);
			if (currentFirst != null)
				currentFirst.setPageNumber(pageNumber);
			pageNumber++;
			tempItems.add(currentFirst);
		}
		tempItems.addFirst(new Page("ingredient_info", false, new ImageWidget(16, 12, 112, 150, TheBetweenlands.prefix("textures/gui/manual/ingredient_info_page.png"))).setPageNumber(tempNum));
		entryPages.add(new Page("ingredient_list", false, new ImageWidget(16, 12, 112, 150, TheBetweenlands.prefix("textures/gui/manual/ingredients_page.png"))));
		ArrayList<Page> buttons = PageCreators.pageCreatorButtons(tempItems);
		indexPages += buttons.size();
		entryPages.addAll(buttons);
		entryPages.addAll(temp);
		entryPages.addAll(tempItems);
		entryPages.addFirst(new Page("intro_1", false, new ImageWidget(16, 12, 112, 150, TheBetweenlands.prefix("textures/gui/manual/index_page.png")), new ButtonWidget(31, 47, 93, 10, 6, false), new ButtonWidget(31, 66, 70, 10, 1, true), new ButtonWidget(31, 84, 80, 10, tempNum, true)));
		aspectCategory = new ManualCategory(entryPages, 1, "aspect_category", true, indexPages);
		CATEGORIES.add(aspectCategory);
	}

	public static void initElixirEntries(HolderLookup.Provider registries) {
		elixirPages.clear();
		ArrayList<Page> infusionPages = new ArrayList<>();
		ArrayList<Page> antiInfusionPages = new ArrayList<>();

		for (Holder<ElixirEffect> effect : BLRegistries.ELIXIR_EFFECTS.holders().toList()) {
			if (effect.value().shouldShowInBook()) {
				if (effect.value().isAntiInfusion()) {
					antiInfusionPages.addAll(PageCreators.makeElixirPage(ElixirContents.createItemStack(ItemRegistry.GREEN_ELIXIR.get(), effect, 1, 1), effect, registries));
				} else {
					infusionPages.addAll(PageCreators.makeElixirPage(ElixirContents.createItemStack(ItemRegistry.GREEN_ELIXIR.get(), effect, 1, 1), effect, registries));
				}
			}
		}

		ArrayList<Page> temp = new ArrayList<>();
		while (!infusionPages.isEmpty()) {
			Page currentFirst = null;
			for (Page page : infusionPages) {
				if (currentFirst == null)
					currentFirst = page;
				else {
					String pageName = page.pageName.getString().toLowerCase();
					char[] characters = pageName.toCharArray();
					String pageNameFirst = currentFirst.pageName.getString().toLowerCase();
					char[] charactersFirst = pageNameFirst.toCharArray();
					for (int i = 0; i < characters.length; i++) {
						if (charactersFirst.length > i) {
							if (characters[i] > charactersFirst[i]) {
								break;
							} else if (characters[i] < charactersFirst[i]) {
								currentFirst = page;
								break;
							}
						}
					}
				}
			}
			infusionPages.remove(currentFirst);
			temp.add(currentFirst);
		}
		infusionPages.addAll(temp);
		infusionPages.addFirst(new Page("infusions", false, new ImageWidget(16, 12, 112, 150, TheBetweenlands.prefix("textures/gui/manual/infusions_page.png"))).setPageNumber(1));
		temp.clear();
		while (!antiInfusionPages.isEmpty()) {
			Page currentFirst = null;
			for (Page page : antiInfusionPages) {
				if (currentFirst == null)
					currentFirst = page;
				else {
					String pageName = page.pageName.getString().toLowerCase();
					char[] characters = pageName.toCharArray();
					String pageNameFirst = currentFirst.pageName.getString().toLowerCase();
					char[] charactersFirst = pageNameFirst.toCharArray();
					for (int i = 0; i < characters.length; i++) {
						if (charactersFirst.length > i) {
							if (characters[i] > charactersFirst[i]) {
								break;
							} else if (characters[i] < charactersFirst[i]) {
								currentFirst = page;
								break;
							}
						}
					}
				}
			}
			antiInfusionPages.remove(currentFirst);
			temp.add(currentFirst);
		}
		antiInfusionPages.addAll(temp);
		antiInfusionPages.addFirst(new Page("infusions", false, new ImageWidget(16, 12, 112, 150, TheBetweenlands.prefix("textures/gui/manual/anti_infusions_page.png"))).setPageNumber(infusionPages.size()));
		elixirPages.clear();
		elixirPages.addAll(infusionPages);
		elixirPages.addAll(antiInfusionPages);
		elixirCategory = new ManualCategory(elixirPages, 2, "elixir_category");
		CATEGORIES.add(elixirCategory);
	}
}
