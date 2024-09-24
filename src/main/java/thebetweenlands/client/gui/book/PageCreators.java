package thebetweenlands.client.gui.book;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thebetweenlands.api.aspect.registry.AspectItem;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.api.recipes.MortarRecipe;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.client.gui.book.widget.*;
import thebetweenlands.client.gui.book.widget.text.FormatTags;
import thebetweenlands.client.gui.book.widget.text.TextContainer;
import thebetweenlands.client.gui.book.widget.text.TextWidget;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.item.ElixirContents;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class PageCreators {

	public static final Function<String, Component> ENTRY_DESCRIPTION = s -> Component.translatable("manual.thebetweenlands.herblore." + s + ".desc");

	public static ArrayList<Page> pageCreatorButtons(ArrayList<Page> pages) {
		ArrayList<Page> newPages = new ArrayList<>();
		int pageAmount = pages.size();
		int times = 0;
		while (pageAmount > 0) {
			ArrayList<Page> pagesTemp = new ArrayList<>(pages.subList(7 * times, (pageAmount > 7 ? 7 + 7 * times : 7 * times + pageAmount)));
			int height = 0;
			ArrayList<BookWidget> widgets = new ArrayList<>();
			for (Page page : pagesTemp) {
				widgets.add(new ButtonWidget(15, 10 + height, page));
				height += 20;
			}
			newPages.add(new Page("index" + times, (ArrayList<BookWidget>) widgets.clone(), false));
			widgets.clear();
			pagesTemp.clear();
			pageAmount -= 7;
			times++;
		}

		return newPages;
	}

	public static ArrayList<Page> makeAspectPage(Holder<AspectType> aspect, HolderLookup.Provider registries) {
		Component aspectName = AspectType.getAspectName(aspect);
		ArrayList<Page> newPages = new ArrayList<>();
		int height = 0;
		ArrayList<BookWidget> widgets = new ArrayList<>();
		widgets.add(new AspectWidget(18, 12, aspect, 1.0F));
		widgets.add(new TextWidget(38, 14, aspectName, TheBetweenlands.HERBLORE_FONT.getFont()).setTextHeight(22));
		height += 24;
		Component desc = ENTRY_DESCRIPTION.apply(aspect.getKey().location().toString().replace(':', '.'));
		TextContainer textContainer = parseTextContainer(new TextContainer(112, 144, desc));
		int widgetHeight = (int) (18 + textContainer.getPages().getFirst().getSegments().getLast().y);
		widgets.add(new TextWidget(18, 12 + height, desc).setTextHeight(widgetHeight));
		height += widgetHeight;

		if (height + 18 + 16 < 152) {
			widgets.add(new TextWidget(18, 12 + height, Component.translatable("manual.thebetweenlands.herblore.found_in")).setTextHeight(18));
			height += 16;
			widgets.add(new AspectItemSlideshowWidget(18, 12 + height, aspect));
			height += 18;
		} else {
			newPages.add(new Page(aspectName.getString().toLowerCase(), widgets, false).setParent().setAspect(aspect).setLocalizedPageName(aspectName));
			widgets.add(new TextWidget(18, 12 + height, Component.translatable("manual.thebetweenlands.herblore.found_in")).setTextHeight(18));
			height += 16;
			widgets.add(new AspectItemSlideshowWidget(18, 12 + height, aspect));
			height += 18;
		}

		if (height + 10 + 18 < 152) {
			widgets.add(new TextWidget(18, 12 + height, Component.translatable("manual.thebetweenlands.herblore.used_in")).setTextHeight(18));
			height += 10;
			ArrayList<ItemStack> items = new ArrayList<>();
			for (Holder<ElixirRecipe> recipe : ElixirRecipe.getFromAspect(aspect, registries)) {
				items.add(ElixirContents.createItemStack(ItemRegistry.GREEN_ELIXIR.get(), recipe.value().positiveElixir(), recipe.value().baseDuration(), 1));
				items.add(ElixirContents.createItemStack(ItemRegistry.ORANGE_ELIXIR.get(), recipe.value().negativeElixir(), recipe.value().baseDuration(), 1));
			}
			widgets.add(new AspectItemSlideshowWidget(18, 12 + height, items));
		} else {
			if (!newPages.isEmpty())
				newPages.add(new Page(aspect.getRegisteredName().toLowerCase(), widgets, false).setAspect(aspect).setLocalizedPageName(aspectName));
			else
				newPages.add(new Page(aspect.getRegisteredName().toLowerCase(), widgets, false).setParent().setAspect(aspect).setLocalizedPageName(aspectName));
			widgets.add(new TextWidget(18, 12 + height, Component.translatable("manual.thebetweenlands.herblore.used_in")).setTextHeight(18));
			height += 10;
			ArrayList<ItemStack> items = new ArrayList<>();
			for (Holder<ElixirRecipe> recipe : ElixirRecipe.getFromAspect(aspect, registries)) {
				items.add(ElixirContents.createItemStack(ItemRegistry.GREEN_ELIXIR.get(), recipe.value().positiveElixir(), recipe.value().baseDuration(), 1));
				items.add(ElixirContents.createItemStack(ItemRegistry.ORANGE_ELIXIR.get(), recipe.value().negativeElixir(), recipe.value().baseDuration(), 1));
			}
			widgets.add(new AspectItemSlideshowWidget(18, 12 + height, items));
		}

		if (!newPages.isEmpty()) {
			newPages.add(new Page(aspectName.getString().toLowerCase(), widgets, false).setAspect(aspect).setLocalizedPageName(aspectName));
		} else {
			newPages.add(new Page(aspectName.getString().toLowerCase(), widgets, false).setParent().setAspect(aspect).setLocalizedPageName(aspectName));
		}
		return newPages;
	}

	public static List<Page> makeAspectItemPage(Holder<AspectItem> item) {
		List<Page> newPages = new ArrayList<>();
		int height = 0;
		ItemStack itemStack = new ItemStack(item.value().item());
		List<Ingredient> pestleAndMortarInput = MortarRecipe.getInputs(BetweenlandsClient.getClientLevel(), itemStack);
		List<BookWidget> widgets = new ArrayList<>();
		widgets.add(new ItemWidget(18, 12, itemStack, 1f));
		if (!pestleAndMortarInput.isEmpty()) {
			widgets.add(new ItemWidget(118, 12, getStacks(pestleAndMortarInput), 1f));
		}
		widgets.add((new TextWidget(38, 16, itemStack.getHoverName())).setTextWidth(90).setTextHeight(26));
		height += 30;
		Component desc = ENTRY_DESCRIPTION.apply(item.value().item().builtInRegistryHolder().getRegisteredName().replace(':', '.'));
		TextContainer textContainer = parseTextContainer(new TextContainer(112, 144, desc));
		int widgetHeight = (int) (18 + textContainer.getPages().getFirst().getSegments().getLast().y);
		widgets.add(new TextWidget(18, 12 + height, desc).setTextHeight(widgetHeight));
		height += widgetHeight;
		widgets.add(new TextWidget(18, 12 + height, Component.translatable("manual.thebetweenlands.herblore.has_aspects")).setTextHeight(18));
		height += 18;
		widgets.add(new AspectSlideshowWidget(18, 12 + height, itemStack));
		Page itemPage = new Page(item.getKey().location().toString(), widgets, true).setParent().addItem(itemStack).setLocalizedPageName(itemStack.getHoverName());
		if (!pestleAndMortarInput.isEmpty()) {
			itemPage.addItems(getStacks(pestleAndMortarInput));
		}
		newPages.add(itemPage);
		return newPages;
	}

	private static List<ItemStack> getStacks(List<Ingredient> input) {
		List<ItemStack> stacks = new ArrayList<>();
		input.forEach(ingredient -> stacks.addAll(Arrays.stream(ingredient.getItems()).toList()));
		return stacks;
	}

	public static ArrayList<Page> makeElixirPage(ItemStack item, Holder<ElixirEffect> effect, HolderLookup.Provider registries) {
		ArrayList<Page> newPages = new ArrayList<>();
		int height = 0;
		ArrayList<BookWidget> widgets = new ArrayList<>();
		widgets.add(new ItemWidget(18, 12, item, 1f));
		widgets.add(new TextWidget(38, 14, item.getHoverName()).setTextHeight(20));
		height += 35;
		Component desc = ENTRY_DESCRIPTION.apply(effect.getKey().location().toString().replace(':', '.'));
		TextContainer textContainer = parseTextContainer(new TextContainer(115, 130, desc));
		Page temp = null;
		var recipe = ElixirRecipe.getRecipeFor(effect, registries);
		if (textContainer.getPages().size() > 1) {
			widgets.add(new TextWidget(15, height, desc, 0, 115, 130));
			newPages.add(new Page(effect.getRegisteredName(), (ArrayList<BookWidget>) widgets.clone(), false).setParent().setLocalizedPageName(item.getHoverName()).addItem(item));
			widgets.clear();
			widgets.add(new TextWidget(15, 14, desc, 1, 115, 130));
			if (recipe != null && !recipe.value().aspects().isEmpty()) {
				if (textContainer.getPages().get(1).getSegments().getLast().y + 38 < 142)
					widgets.add(new AspectSlideshowWidget(18, (int) (textContainer.getPages().get(1).getSegments().getLast().y + 22), recipe.value().aspects()));
				else
					temp = new Page(effect.getRegisteredName(), false, new AspectSlideshowWidget(15, 12, recipe.value().aspects()));
			}
			newPages.add(new Page(effect.getRegisteredName(), widgets, false).setLocalizedPageName(item.getHoverName()).addItem(item));
			if (temp != null)
				newPages.add(temp);
		} else {
			widgets.add(new TextWidget(15, height, desc, 0, 115, 130));
			if (recipe != null && !recipe.value().aspects().isEmpty()) {
				widgets.add(new AspectSlideshowWidget(18, height + (int) (textContainer.getPages().getFirst().getSegments().getLast().y + 8), recipe.value().aspects()));
			}
			newPages.add(new Page(effect.getRegisteredName(), widgets, false).setParent().setLocalizedPageName(item.getHoverName()).addItem(item));
		}

		return newPages;
	}

	private static TextContainer parseTextContainer(TextContainer textContainer) {
		textContainer.setCurrentScale(1.0f).setCurrentColor(0x606060);
		textContainer.registerTag(new FormatTags.TagNewLine());
		textContainer.registerTag(new FormatTags.TagScale(1.0F));
		textContainer.registerTag(new FormatTags.TagColor(0x606060));
		textContainer.registerTag(new FormatTags.TagTooltip(Component.empty()));
		textContainer.registerTag(new FormatTags.TagSimple("bold", ChatFormatting.BOLD));
		textContainer.registerTag(new FormatTags.TagSimple("obfuscated", ChatFormatting.OBFUSCATED));
		textContainer.registerTag(new FormatTags.TagSimple("italic", ChatFormatting.ITALIC));
		textContainer.registerTag(new FormatTags.TagSimple("strikethrough", ChatFormatting.STRIKETHROUGH));
		textContainer.registerTag(new FormatTags.TagSimple("underline", ChatFormatting.UNDERLINE));
		textContainer.registerTag(new FormatTags.TagPagelink());
		textContainer.registerTag(new FormatTags.TagRainbow());
		try {
			textContainer.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return textContainer;
	}
}
