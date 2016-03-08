package thebetweenlands.manual;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.herblore.aspects.IAspectType;
import thebetweenlands.herblore.elixirs.ElixirRecipe;
import thebetweenlands.herblore.elixirs.ElixirRecipes;
import thebetweenlands.herblore.elixirs.effects.ElixirEffect;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.manual.widgets.*;
import thebetweenlands.manual.widgets.text.FormatTags;
import thebetweenlands.manual.widgets.text.TextContainer;
import thebetweenlands.manual.widgets.text.TextWidget;
import thebetweenlands.recipes.PestleAndMortarRecipe;

import java.util.ArrayList;

/**
 * Created by Bart on 23/11/2015.
 */
public class PageCreators {

    public static int craftingRecipeHeight = CraftingRecipeWidget.height + 5;
    public static int smeltingRecipeHeight = SmeltingRecipeWidget.height + 5;
    public static int compostRecipeHeight = CompostRecipeWidget.height + 5;
    public static int pestleAndMortarRecipeHeight = PestleAndMortarRecipeWidget.height + 5;
    public static int purifierRecipeHeight = PurifierRecipeWidget.height + 5;
    public static int druidAltarRecipeHeight = DruidAltarWidget.height + 5;
    public static int rubberTabRecipeHeight = RubberTabWidget.height + 5;
    public static int animatorRecipeHeight = AnimatorRecipeWidget.height + 5;

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
            ArrayList<ManualWidgetsBase> widgets = new ArrayList<>();
            for (Page page : pagesTemp) {
                widgets.add(new ButtonWidget(15, 10 + height, page));
                height += 20;
            }
            newPages.add(new Page("index" + times, (ArrayList<ManualWidgetsBase>) widgets.clone(), false, manualType));
            widgets.clear();
            pagesTemp.clear();
            pageAmount -= 7;
            times++;
        }

        return newPages;
    }

    /**
     * Creates the pages for a entity entry
     *
     * @param entity     the entity to make the entry for
     * @param isHidden   whether or not the pages should be hidden until discovery
     * @param manualType the type of manual they are in
     * @return an array for the entry
     */
    public static ArrayList<Page> pageCreatorEntities(IManualEntryEntity entity, boolean isHidden, Item manualType) {
        ArrayList<Page> newPages = new ArrayList<>();
        String title = entity.pageName();
        newPages.add(new Page(title, isHidden, manualType, new TextWidget(15, 10, "manual." + entity.pageName() + ".title"), new PictureWidget(74 - (entity.pictureWidth() / 2), 15, entity.manualPictureLocation(), entity.pictureWidth(), entity.pictureHeight(), entity.manualStats(), 128.0D, 128.0D)).setParent().setEntity(entity));
        newPages.addAll(TextPages(15, 10, "manual." + entity.pageName() + ".description", title, isHidden, manualType));
        return newPages;
    }

    /**
     * Creates the pages for a machine entry
     *
     * @param entryName     the entry name
     * @param machine       the machine it is for
     * @param imageLocation the location for the explanation texture
     * @param width         the width of the texture
     * @param height        the height of the texture
     * @param isHidden      whether or not the pages should be hidden until discovery
     * @param manualType    the type of manual they are in
     * @return an array for the entry
     */
    public static ArrayList<Page> pageCreatorMachines(String entryName, ItemStack machine, String imageLocation, int width, int height, boolean isHidden, Item manualType) {
        ArrayList<Page> newPages = new ArrayList<>();
        newPages.add(new Page(entryName, isHidden, manualType, new TextWidget(15, 10, "manual." + entryName + ".title", 1.5f), new ItemWidget(73 - 24, 77, machine, 3)).addItem(machine).setParent());
        newPages.add(new Page(entryName, isHidden, manualType, new PictureWidget(73 - width / 2, 70 - height / 2, imageLocation, width, height, 256.0D, 256.0D)));
        newPages.addAll(TextPages(16, 10, "manual." + entryName + ".description", entryName, isHidden, manualType));
        return newPages;
    }

    /**
     * Creates the pages for a item entry
     *
     * @param item       the item for the entry
     * @param isHidden   whether or not the pages should be hidden until discovery
     * @param manualType the type of manual they are in
     * @return an array for the entry
     */
    public static ArrayList<Page> pageCreatorItems(IManualEntryItem item, boolean isHidden, Item manualType) {
        ArrayList<Page> newPages = new ArrayList<>();
        String title = item.manualName(0);
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (int i = 0; i <= item.metas(); i++)
            items.add(new ItemStack(item.getItem(), 1, i));
        newPages.add(new Page(title, isHidden, manualType, new TextWidget(18, 12, "manual." + item.manualName(0) + ".title", 1.5f), new ItemWidget(49, 77, item, 3)).addItems(items).setParent());
        newPages.addAll(TextPages(16, 10, "manual." + item.manualName(0) + ".description", title, isHidden, manualType));
        ArrayList<IManualEntryItem> manualItem = new ArrayList<>();
        manualItem.add(item);
        newPages.addAll(RecipePages(manualItem, title, isHidden, manualType));
        return newPages;
    }

    /**
     * Creates the pages for a item entry
     *
     * @param name        the name of the entry
     * @param manualItems an array for the items in the entry
     * @param isHidden    whether or not the pages should be hidden until discovery
     * @param manualType  the type of manual they are in
     * @return an array for the entry
     */
    public static ArrayList<Page> pageCreatorItems(String name, ArrayList<IManualEntryItem> manualItems, boolean isHidden, Item manualType) {
        ArrayList<Page> newPages = new ArrayList<>();
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (IManualEntryItem item : manualItems) {
            for (int i = 0; i <= item.metas(); i++)
                items.add(new ItemStack(item.getItem(), 1, i));
        }
        newPages.add(new Page(name, false, manualType, new TextWidget(15, 10, "manual." + name + ".title", 1.5f), new ItemWidget(49, 77, items, 3)).addItems(items).setParent());
        newPages.addAll(TextPages(16, 10, "manual." + name + ".description", name, isHidden, manualType));
        newPages.addAll(RecipePages(manualItems, name, isHidden, manualType));
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
        String text = StatCollector.translateToLocal(unlocalizedName);
        TextContainer textContainer = parseTextContainer(new TextContainer(116, 144, text, Minecraft.getMinecraft().fontRenderer));

        for (int i = 0; i < textContainer.getPages().size(); i++) {
            newPages.add(new Page(pageName, isHidden, manualType, new TextWidget(x, y, unlocalizedName, i)));
        }
        return newPages;
    }

    /**
     * Creates pages containing recipes
     *
     * @param items      an array of items to display the recipies for
     * @param pageName   the page name
     * @param isHidden   whether or not the pages should be hidden until discovery
     * @param manualType the type of manual they are in
     * @return an array of recipe pages
     */
    public static ArrayList<Page> RecipePages(ArrayList<IManualEntryItem> items, String pageName, boolean isHidden, Item manualType) {
        ArrayList<Page> newPages = new ArrayList<>();
        int height = 10;
        int type = 0;
        ArrayList<ManualWidgetsBase> widgets = new ArrayList<>();
        while (type <= 8) {
            ArrayList<ItemStack> itemStacks = new ArrayList<>();
            for (IManualEntryItem item : items) {
                for (int i : item.recipeType(0)) {
                    for (int j = 0; j <= item.metas(); j++)
                        if (i == type)
                            itemStacks.add(new ItemStack(item.getItem(), 1, j));
                }
            }

            if (itemStacks.size() > 0) {
                int latestAdded = 0;
                switch (type) {
                    case 1:
                        widgets.add(new SmeltingRecipeWidget(itemStacks, 15, height));
                        height += smeltingRecipeHeight;
                        latestAdded += smeltingRecipeHeight;
                        break;
                    case 2:
                        widgets.add(new CraftingRecipeWidget(itemStacks, 15, height));
                        height += craftingRecipeHeight;
                        latestAdded += craftingRecipeHeight;
                        break;
                    case 3:
                        widgets.add(new PestleAndMortarRecipeWidget(itemStacks, 15, height));
                        height += pestleAndMortarRecipeHeight;
                        latestAdded += pestleAndMortarRecipeHeight;
                        break;
                    case 4:
                        widgets.add(new CompostRecipeWidget(15, height));
                        height += compostRecipeHeight;
                        latestAdded += compostRecipeHeight;
                        break;
                    case 5:
                        widgets.add(new PurifierRecipeWidget(itemStacks, 15, height));
                        height += purifierRecipeHeight;
                        latestAdded += purifierRecipeHeight;
                        break;
                    case 6:
                        widgets.add(new RubberTabWidget(15, height));
                        height += rubberTabRecipeHeight;
                        latestAdded += rubberTabRecipeHeight;
                        break;
                    case 7:
                        widgets.add(new DruidAltarWidget(itemStacks, 15, height));
                        height += druidAltarRecipeHeight;
                        latestAdded += druidAltarRecipeHeight;
                        break;
                    case 8:
                        widgets.add(new AnimatorRecipeWidget(itemStacks, 15, height));
                        height += animatorRecipeHeight;
                        latestAdded += animatorRecipeHeight;
                        break;
                }
                if (height >= 152) {
                    ManualWidgetsBase temp = widgets.get(widgets.size() - 1);
                    widgets.remove(widgets.size() - 1);
                    Page page = new Page(pageName, (ArrayList<ManualWidgetsBase>) widgets.clone(), isHidden, manualType);
                    newPages.add(page);
                    widgets.clear();
                    temp.changeYStart(10);
                    widgets.add(temp);
                    height = 10 + latestAdded;
                }
            }
            type++;
        }
        if (widgets.size() > 0) {
            Page page = new Page(pageName, (ArrayList<ManualWidgetsBase>) widgets.clone(), isHidden, manualType);
            newPages.add(page);
            widgets.clear();
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
        ArrayList<ManualWidgetsBase> widgets = new ArrayList<>();
        widgets.add(new AspectWidget(18, 12, aspect, 1f));
        widgets.add(new TextWidget(38, 14, "<font:custom>" + aspect.getName() + "</font>", true));
        height += 24;
        widgets.add(new TextWidget(18, 12 + height, "manual." + aspect.getName().toLowerCase() + ".description"));
        TextContainer textContainer = parseTextContainer(new TextContainer(116, 144, StatCollector.translateToLocal("manual." + aspect.getName().toLowerCase() + ".description"), Minecraft.getMinecraft().fontRenderer));

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
                items.add(BLItemRegistry.elixir.getElixirItem(recipe.positiveElixir, recipe.baseDuration, 1, 0));
                items.add(BLItemRegistry.elixir.getElixirItem(recipe.negativeElixir, recipe.baseDuration, 1, 1));
            }
            widgets.add(new AspectItemSlideShowWidget(18, 12 + height, items));
            height += 18;
        } else {
            if (newPages.size() > 0)
                newPages.add(new Page(aspect.getName().toLowerCase(), widgets, false, manualType).setAspect(aspect).setLocalizedPageName(aspect.getName()));
            else
                newPages.add(new Page(aspect.getName().toLowerCase(), widgets, false, manualType).setParent().setAspect(aspect).setLocalizedPageName(aspect.getName()));
            widgets.add(new TextWidget(18, 12 + height, "manual.aspect.found.in"));
            height += 10;
            ArrayList<ItemStack> items = new ArrayList<>();
            for (ElixirRecipe recipe : ElixirRecipes.getFromAspect(aspect)) {
                items.add(BLItemRegistry.elixir.getElixirItem(recipe.positiveElixir, recipe.baseDuration, 1, 0));
                items.add(BLItemRegistry.elixir.getElixirItem(recipe.negativeElixir, recipe.baseDuration, 1, 1));
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
    public static ArrayList<Page> AspectItemPages(AspectManager.AspectItem item, Item manualType) {
        ArrayList<Page> newPages = new ArrayList<>();
        int height = 0;
        ItemStack itemStack = new ItemStack(item.item, 1, item.damage);
        ArrayList<ManualWidgetsBase> widgets = new ArrayList<>();
        widgets.add(new ItemWidget(18, 12, itemStack, 1f));
        widgets.add(new ItemWidget(118, 12, PestleAndMortarRecipe.getInput(itemStack), 1f));
        widgets.add((new TextWidget(38, 16, itemStack.getDisplayName(), true)).setWidth(70));
        height += 28;
        widgets.add(new TextWidget(18, 12 + height, "manual." + itemStack.getDisplayName().toLowerCase().replace(" ", "") + ".description"));
        TextContainer textContainer = parseTextContainer(new TextContainer(116, 144, StatCollector.translateToLocal("manual." + itemStack.getDisplayName().toLowerCase().replace(" ", "") + ".description"), Minecraft.getMinecraft().fontRenderer));

        height += 18 + textContainer.getPages().get(0).getSegments().get(textContainer.getPages().get(0).getSegments().size() - 1).y;
        widgets.add(new TextWidget(18, 12 + height, "manual.has.aspects"));
        height += 18;
        widgets.add(new AspectSlideShowWidget(18, 12 + height, itemStack));
        newPages.add(new Page(itemStack.getDisplayName().toLowerCase().replace(" ", ""), widgets, true, manualType).setParent().addItem(itemStack).addItem(PestleAndMortarRecipe.getInput(itemStack)).setLocalizedPageName(itemStack.getDisplayName()));
        return newPages;
    }

    public static ArrayList<Page> elixirPages(ItemStack item, Item manualType, ElixirEffect effect) {
        ArrayList<Page> newPages = new ArrayList<>();
        int height = 0;
        ArrayList<ManualWidgetsBase> widgets = new ArrayList<>();
        widgets.add(new ItemWidget(18, 12, item, 1f));
        widgets.add(new TextWidget(38, 14, item.getDisplayName(), true));
        height += 32;
        TextContainer textContainer = new TextContainer(114, 130, StatCollector.translateToLocal("manual." + item.getDisplayName().replace(" ", "").replace("'", "") + ".description"), Minecraft.getMinecraft().fontRenderer);
        textContainer = parseTextContainer(textContainer);
        Page temp = null;
        if (textContainer.getPages().size() > 1) {
            widgets.add(new TextWidget(15, height, "manual." + item.getDisplayName().replace(" ", "").replace("'", "") + ".description", 0, 114, 130));
            newPages.add(new Page(item.getDisplayName().toLowerCase().replace(" ", ""), (ArrayList<ManualWidgetsBase>) widgets.clone(), false, manualType).setParent().setLocalizedPageName(item.getDisplayName()).addItem(item));
            widgets.clear();
            widgets.add(new TextWidget(15, 14, "manual." + item.getDisplayName().replace(" ", "").replace("'", "") + ".description", 1, 114, 130));
            if (ElixirRecipes.getFromEffect(effect) != null && ElixirRecipes.getFromEffect(effect).aspects != null) {
                if (textContainer.getPages().get(1).getSegments().get(textContainer.getPages().get(1).getSegments().size() - 1).y + 38 < 142)
                    widgets.add(new AspectSlideShowWidget(18, (int) (textContainer.getPages().get(1).getSegments().get(textContainer.getPages().get(1).getSegments().size() - 1).y + 22), ElixirRecipes.getFromEffect(effect).aspects));
                else
                    temp = new Page(item.getDisplayName().toLowerCase().replace(" ", ""), false, manualType, new AspectSlideShowWidget(15, 12, ElixirRecipes.getFromEffect(effect).aspects));
            }
            newPages.add(new Page(item.getDisplayName().toLowerCase().replace(" ", ""), widgets, false, manualType).setLocalizedPageName(item.getDisplayName()).addItem(item));
            if (temp != null)
                newPages.add(temp);
        } else {
            widgets.add(new TextWidget(15, height, "manual." + item.getDisplayName().replace(" ", "").replace("'", "") + ".description"));
            if (ElixirRecipes.getFromEffect(effect) != null && ElixirRecipes.getFromEffect(effect).aspects != null) {
                if (height + 24 < 142)
                    widgets.add(new AspectSlideShowWidget(18, height + (int) (textContainer.getPages().get(0).getSegments().get(textContainer.getPages().get(0).getSegments().size() - 1).y + 8), ElixirRecipes.getFromEffect(effect).aspects));
                else
                    temp = new Page(item.getDisplayName().toLowerCase().replace(" ", ""), false, manualType, new AspectSlideShowWidget(15, 12, ElixirRecipes.getFromEffect(effect).aspects));
            }
            newPages.add(new Page(item.getDisplayName().toLowerCase().replace(" ", ""), widgets, false, manualType).setParent().setLocalizedPageName(item.getDisplayName()).addItem(item));
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
        textContainer.setCurrentScale(1.0f).setCurrentColor(0x808080);
        textContainer.registerTag(new FormatTags.TagNewLine());
        textContainer.registerTag(new FormatTags.TagScale(1.0F));
        textContainer.registerTag(new FormatTags.TagColor(0x808080));
        textContainer.registerTag(new FormatTags.TagTooltip("N/A"));
        textContainer.registerTag(new FormatTags.TagSimple("bold", EnumChatFormatting.BOLD));
        textContainer.registerTag(new FormatTags.TagSimple("obfuscated", EnumChatFormatting.OBFUSCATED));
        textContainer.registerTag(new FormatTags.TagSimple("italic", EnumChatFormatting.ITALIC));
        textContainer.registerTag(new FormatTags.TagSimple("strikethrough", EnumChatFormatting.STRIKETHROUGH));
        textContainer.registerTag(new FormatTags.TagSimple("underline", EnumChatFormatting.UNDERLINE));
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
