package thebetweenlands.common.herblore.book;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.api.aspect.AspectItem;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.herblore.book.widgets.AspectItemSlideShowWidget;
import thebetweenlands.common.herblore.book.widgets.AspectSlideShowWidget;
import thebetweenlands.common.herblore.book.widgets.AspectWidget;
import thebetweenlands.common.herblore.book.widgets.ButtonWidget;
import thebetweenlands.common.herblore.book.widgets.ItemWidget;
import thebetweenlands.common.herblore.book.widgets.ManualWidgetBase;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags;
import thebetweenlands.common.herblore.book.widgets.text.TextContainer;
import thebetweenlands.common.herblore.book.widgets.text.TextWidget;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;
import thebetweenlands.common.herblore.elixir.ElixirRecipes;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;
import thebetweenlands.common.recipe.misc.PestleAndMortarRecipe;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.TranslationHelper;

public class PageCreators {

    /**
     * Creates the button pages for a category
     *
     * @param pages      the pages that need a button
     * @param manualType the type of manual the pages are in
     * @return a array of button pages
     */
    public static ArrayList<Page> pageCreatorButtons(ArrayList<Page> pages, Item manualType) {
        ArrayList<Page> newPages = new ArrayList<>();
        int pageAmount = pages.size();
        int times = 0;
        while (pageAmount > 0) {
            ArrayList<Page> pagesTemp = new ArrayList<>();
            pagesTemp.addAll(pages.subList(7 * times, (pageAmount > 7 ? 7 + 7 * times : 7 * times + pageAmount)));
            int height = 0;
            ArrayList<ManualWidgetBase> widgets = new ArrayList<>();
            for (Page page : pagesTemp) {
                widgets.add(new ButtonWidget(15, 10 + height, page));
                height += 20;
            }
            newPages.add(new Page("index" + times, (ArrayList<ManualWidgetBase>) widgets.clone(), false, manualType));
            widgets.clear();
            pagesTemp.clear();
            pageAmount -= 7;
            times++;
        }

        return newPages;
    }


    /**
     * Creates pages containing text
     *
     * @param x               the x coordinate for starting the text
     * @param y               the y coordinate for starting the text
     * @param unlocalizedName the unlocalized text
     * @param pageName        the page name
     * @param isHidden        whether or not the pages should be hidden until discovery
     * @param manualType      the type of manual they are in
     * @return an array of text pages
     */
    public static ArrayList<Page> TextPages(int x, int y, String unlocalizedName, String pageName, boolean isHidden, Item manualType) {
        ArrayList<Page> newPages = new ArrayList<>();
        String text = TranslationHelper.translateToLocal(unlocalizedName);
        TextContainer textContainer = parseTextContainer(new TextContainer(116, 144, text, Minecraft.getMinecraft().fontRenderer));

        for (int i = 0; i < textContainer.getPages().size(); i++) {
            newPages.add(new Page(pageName, isHidden, manualType, new TextWidget(x, y, unlocalizedName, i)));
        }
        return newPages;
    }


    /**
     * Creates the pages for a aspect entry
     *
     * @param aspect     the aspect to create the pages for
     * @param manualType the type of manual they are in
     * @return an array for the entry
     */
    public static ArrayList<Page> AspectPages(IAspectType aspect, Item manualType) {
        ArrayList<Page> newPages = new ArrayList<>();
        int height = 0;
        ArrayList<ManualWidgetBase> widgets = new ArrayList<>();
        widgets.add(new AspectWidget(18, 12, aspect, 1f));
        widgets.add(new TextWidget(38, 14, "<font:custom>" + aspect.getName() + "</font>", true));
        height += 24;
        widgets.add(new TextWidget(18, 12 + height, "manual." + aspect.getName().toLowerCase() + ".description"));
        TextContainer textContainer = parseTextContainer(new TextContainer(112, 144, TranslationHelper.translateToLocal("manual." + aspect.getName().toLowerCase() + ".description"), Minecraft.getMinecraft().fontRenderer));

        height += textContainer.getPages().get(0).getSegments().get(textContainer.getPages().get(0).getSegments().size() - 1).y + 18;

        if (height + 18 + 16 < 152) {
            widgets.add(new TextWidget(18, 12 + height, "manual.aspect.found.in"));
            height += 16;
            widgets.add(new AspectItemSlideShowWidget(18, 12 + height, aspect));
            height += 18;
        } else {
            newPages.add(new Page(aspect.getName().toLowerCase(), widgets, false, manualType).setParent().setAspect(aspect).setLocalizedPageName(aspect.getName()));
            widgets.add(new TextWidget(18, 12 + height, "manual.aspect.found.in"));
            height += 16;
            widgets.add(new AspectItemSlideShowWidget(18, 12 + height, aspect));
            height += 18;
        }

        if (height + 10 + 18 < 152) {
            widgets.add(new TextWidget(18, 12 + height, "manual.aspect.used.in"));
            height += 10;
            ArrayList<ItemStack> items = new ArrayList<>();
            for (ElixirRecipe recipe : ElixirRecipes.getFromAspect(aspect)) {
                items.add(ItemRegistry.ELIXIR.getElixirItem(recipe.positiveElixir, recipe.baseDuration, 1, 0));
                items.add(ItemRegistry.ELIXIR.getElixirItem(recipe.negativeElixir, recipe.baseDuration, 1, 1));
            }
            widgets.add(new AspectItemSlideShowWidget(18, 12 + height, items));
            height += 18;
        } else {
            if (newPages.size() > 0)
                newPages.add(new Page(aspect.getName().toLowerCase(), widgets, false, manualType).setAspect(aspect).setLocalizedPageName(aspect.getName()));
            else
                newPages.add(new Page(aspect.getName().toLowerCase(), widgets, false, manualType).setParent().setAspect(aspect).setLocalizedPageName(aspect.getName()));
            widgets.add(new TextWidget(18, 12 + height, "manual.aspect.used.in"));
            height += 10;
            ArrayList<ItemStack> items = new ArrayList<>();
            for (ElixirRecipe recipe : ElixirRecipes.getFromAspect(aspect)) {
                items.add(ItemRegistry.ELIXIR.getElixirItem(recipe.positiveElixir, recipe.baseDuration, 1, 0));
                items.add(ItemRegistry.ELIXIR.getElixirItem(recipe.negativeElixir, recipe.baseDuration, 1, 1));
            }
            widgets.add(new AspectItemSlideShowWidget(18, 12 + height, items));
            height += 18;
        }

        if (widgets.size() > 0) {
            if (newPages.size() > 0)
                newPages.add(new Page(aspect.getName().toLowerCase(), widgets, false, manualType).setAspect(aspect).setLocalizedPageName(aspect.getName()));
            else
                newPages.add(new Page(aspect.getName().toLowerCase(), widgets, false, manualType).setParent().setAspect(aspect).setLocalizedPageName(aspect.getName()));
        }
        return newPages;
    }

    /**
     * Creates the pages for the ground times
     *
     * @param item       the item
     * @param manualType the type of manual they are in
     * @return an array for the entry
     */
    public static List<Page> AspectItemPages(AspectItem item, Item manualType) {
        List<Page> newPages = new ArrayList<>();
        int height = 0;
        ItemStack itemStack = item.getOriginal();
        ItemStack pestleAndMortarInput = PestleAndMortarRecipe.getInput(itemStack);
        List<ManualWidgetBase> widgets = new ArrayList<>();
        widgets.add(new ItemWidget(18, 12, itemStack, 1f));
        if(!pestleAndMortarInput.isEmpty()) {
        	widgets.add(new ItemWidget(118, 12, getStacks(pestleAndMortarInput), 1f));
        }
        widgets.add((new TextWidget(38, 16, itemStack.getDisplayName(), true)).setWidth(70));
        height += 28;
        widgets.add(new TextWidget(18, 12 + height, "manual." + itemStack.getTranslationKey() + ".description"));
        TextContainer textContainer = parseTextContainer(new TextContainer(112, 144, TranslationHelper.translateToLocal("manual." + itemStack.getTranslationKey() + ".description"), Minecraft.getMinecraft().fontRenderer));

        height += 18 + textContainer.getPages().get(0).getSegments().get(textContainer.getPages().get(0).getSegments().size() - 1).y;
        widgets.add(new TextWidget(18, 12 + height, "manual.has.aspects"));
        height += 18;
        widgets.add(new AspectSlideShowWidget(18, 12 + height, itemStack));
        Page itemPage = new Page(itemStack.getTranslationKey().toLowerCase().replace(" ", ""), widgets, true, manualType).setParent().addItem(itemStack).setLocalizedPageName(itemStack.getDisplayName());
        if(!pestleAndMortarInput.isEmpty()) {
        	itemPage.addItems(getStacks(pestleAndMortarInput));
        }
        newPages.add(itemPage);
        return newPages;
    }

    private static List<ItemStack> getStacks(ItemStack input) {
        List<ItemStack> stacks = NonNullList.create();
        if (input != null && !input.isEmpty() && input.getMetadata() == OreDictionary.WILDCARD_VALUE) {
            NonNullList<ItemStack> list = NonNullList.create();
            input.getItem().getSubItems(CreativeTabs.SEARCH, list);
            stacks.addAll(list);
        } else {
            stacks.add(input);
        }
        return stacks;
    }

    public static ArrayList<Page> elixirPages(ItemStack item, Item manualType, ElixirEffect effect) {
        ArrayList<Page> newPages = new ArrayList<>();
        int height = 0;
        ArrayList<ManualWidgetBase> widgets = new ArrayList<>();
        widgets.add(new ItemWidget(18, 12, item, 1f));
        widgets.add(new TextWidget(38, 14, item.getDisplayName(), true));
        height += 32;
        TextContainer textContainer = new TextContainer(115, 130, TranslationHelper.translateToLocal("manual." + item.getTranslationKey() + ".description"), Minecraft.getMinecraft().fontRenderer);
        textContainer = parseTextContainer(textContainer);
        Page temp = null;
        if (textContainer.getPages().size() > 1) {
            widgets.add(new TextWidget(15, height, "manual." + item.getTranslationKey() + ".description", 0, 115, 130));
            newPages.add(new Page(item.getTranslationKey().toLowerCase().replace(" ", ""), (ArrayList<ManualWidgetBase>) widgets.clone(), false, manualType).setParent().setLocalizedPageName(item.getDisplayName()).addItem(item));
            widgets.clear();
            widgets.add(new TextWidget(15, 14, "manual." + item.getTranslationKey() + ".description", 1, 115, 130));
            if (ElixirRecipes.getFromEffect(effect) != null && ElixirRecipes.getFromEffect(effect).aspects != null) {
                if (textContainer.getPages().get(1).getSegments().get(textContainer.getPages().get(1).getSegments().size() - 1).y + 38 < 142)
                    widgets.add(new AspectSlideShowWidget(18, (int) (textContainer.getPages().get(1).getSegments().get(textContainer.getPages().get(1).getSegments().size() - 1).y + 22), ElixirRecipes.getFromEffect(effect).aspects));
                else
                    temp = new Page(item.getTranslationKey().toLowerCase().replace(" ", ""), false, manualType, new AspectSlideShowWidget(15, 12, ElixirRecipes.getFromEffect(effect).aspects));
            }
            newPages.add(new Page(item.getTranslationKey().toLowerCase().replace(" ", ""), widgets, false, manualType).setLocalizedPageName(item.getDisplayName()).addItem(item));
            if (temp != null)
                newPages.add(temp);
        } else {
            widgets.add(new TextWidget(15, height, "manual." + item.getTranslationKey() + ".description", 0, 115, 1130));
            if (ElixirRecipes.getFromEffect(effect) != null && ElixirRecipes.getFromEffect(effect).aspects != null) {
                if (height + 24 < 142)
                    widgets.add(new AspectSlideShowWidget(18, height + (int) (textContainer.getPages().get(0).getSegments().get(textContainer.getPages().get(0).getSegments().size() - 1).y + 8), ElixirRecipes.getFromEffect(effect).aspects));
                else
                    temp = new Page(item.getTranslationKey().toLowerCase().replace(" ", ""), false, manualType, new AspectSlideShowWidget(15, 12, ElixirRecipes.getFromEffect(effect).aspects));
            }
            newPages.add(new Page(item.getTranslationKey().toLowerCase().replace(" ", ""), widgets, false, manualType).setParent().setLocalizedPageName(item.getDisplayName()).addItem(item));
            if (temp != null)
                newPages.add(temp);
        }

        return newPages;
    }

    /**
     * Parses the text container. Used to get the right width and height of the container
     *
     * @param textContainer a unparsed text container
     * @return a pars
     * ed text container
     */
    private static TextContainer parseTextContainer(TextContainer textContainer) {
        textContainer.setCurrentScale(1.0f).setCurrentColor(0x606060);
        textContainer.registerTag(new FormatTags.TagNewLine());
        textContainer.registerTag(new FormatTags.TagScale(1.0F));
        textContainer.registerTag(new FormatTags.TagColor(0x606060));
        textContainer.registerTag(new FormatTags.TagTooltip("N/A"));
        textContainer.registerTag(new FormatTags.TagSimple("bold", TextFormatting.BOLD));
        textContainer.registerTag(new FormatTags.TagSimple("obfuscated", TextFormatting.OBFUSCATED));
        textContainer.registerTag(new FormatTags.TagSimple("italic", TextFormatting.ITALIC));
        textContainer.registerTag(new FormatTags.TagSimple("strikethrough", TextFormatting.STRIKETHROUGH));
        textContainer.registerTag(new FormatTags.TagSimple("underline", TextFormatting.UNDERLINE));
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
