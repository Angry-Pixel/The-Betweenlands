package thebetweenlands.manual;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import thebetweenlands.items.BLItemRegistry;

/**
 * Created by Bart on 21/11/2015.
 */
public class ManualManager {
	public static List<String> findablePagesGuideBook = new ArrayList<>();
	public static List<String> findablePagesHL = new ArrayList<>();
	public static List<String> findablePagesAll = new ArrayList<>();

	/**
	 * Adds the nbt for a page to the page manual
	 *
	 * @param player     a player with a manual
	 * @param pageName   a valid page name (one found in the arrays above)
	 * @param itemManual either manualGuideBook or manualHL
	 * @return
	 */
	public static boolean findPage(EntityPlayer player, String pageName, Item itemManual) {
		if (pageName != null && player != null) {
			for (int i = -1; i < 36; i++) {
				ItemStack stack;
				if (i >= 0)
					stack = player.inventory.getStackInSlot(i);
				else
					stack = player.getCurrentEquippedItem();
				if (stack != null && stack.getItem() == itemManual) {
					NBTTagCompound nbt = stack.getTagCompound();
					if (nbt == null)
						nbt = new NBTTagCompound();
					ArrayList<String> foundPages = getFoundPages(player, itemManual);
					if (foundPages != null && !foundPages.contains(pageName)) {
						NBTTagList pages = new NBTTagList();
						for (String string : foundPages) {
							NBTTagCompound data = new NBTTagCompound();
							data.setString("page", string);
							pages.appendTag(data);
						}
						NBTTagCompound data = new NBTTagCompound();
						data.setString("page", pageName);
						pages.appendTag(data);
						nbt.setTag("pages", pages);
					} else {
						NBTTagList pages = new NBTTagList();
						NBTTagCompound data = new NBTTagCompound();
						data.setString("page", pageName);
						pages.appendTag(data);
						nbt.setTag("pages", pages);
					}
					player.inventory.getStackInSlot(i).setTagCompound(nbt);
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * Gets all the found pages on a manual
	 *
	 * @param player     a player with a manual
	 * @param itemManual either manualGuideBook or manualHL
	 * @return
	 */
	public static ArrayList<String> getFoundPages(EntityPlayer player, Item itemManual) {
		if (player != null) {
			ArrayList<String> foundPages = new ArrayList<>();
			for (int i = -1; i < 36; i++) {
				ItemStack stack;
				if (i >= 0)
					stack = player.inventory.getStackInSlot(i);
				else
					stack = player.getCurrentEquippedItem();
				if (stack != null && stack.getItem() == itemManual) {
					NBTTagCompound nbt = stack.getTagCompound();
					if (nbt != null) {
						NBTTagList tag = nbt.getTagList("pages", 10);
						if (tag != null) {
							for (int j = 0; j < tag.tagCount(); j++) {
								NBTTagCompound data = tag.getCompoundTagAt(j);
								foundPages.add(data.getString("page"));
							}
						}
						return foundPages;
					}
				}
			}
		}
		return null;
	}

	/**
	 * checks if the manual in the inventory has found a specific page
	 *
	 * @param player     a player with a manual
	 * @param page       a valid page name (one found in the arrays above)
	 * @param itemManual either manualGuideBook or manualHL
	 * @return
	 */
	public static boolean hasFoundPage(EntityPlayer player, String page, Item itemManual) {
		return player != null && page != null && getFoundPages(player, itemManual) != null && getFoundPages(player, itemManual).contains(page.toLowerCase());
	}

	/**
	 * Adds NBT to the book to allow a page to be opened
	 *
	 * @param player     a player with a manual
	 * @param name       a valid page name (one found in the arrays above)
	 * @param itemManual either manualGuideBook or manualHL
	 */
	public static void playerDiscoverPage(EntityPlayer player, String name, Item itemManual) {
		if (!ManualManager.hasFoundPage(player, name, itemManual) && player != null && player.inventory.hasItem(itemManual) && !player.worldObj.isRemote) {
			if (ManualManager.findPage(player, name, itemManual))
				player.addChatMessage(new ChatComponentTranslation("chat.manual.discover.page", new ChatComponentTranslation("manual." + name + ".title")));
		}
	}

	/**
	 * Sets the page you will be on when you open the book
	 *
	 * @param category   the category the player is on
	 * @param pageNumber the number of the page the player is on
	 * @param itemManual either manualGuideBook or manualHL
	 * @param player     a player with a manual
	 */
	public static void setCurrentPage(String category, int pageNumber, Item itemManual, EntityPlayer player) {
		if (player != null && player.getHeldItem() != null && player.getHeldItem().getItem() == itemManual && category != null) {
			if (player.getHeldItem().stackTagCompound == null)
				player.getHeldItem().stackTagCompound = new NBTTagCompound();
			player.getHeldItem().stackTagCompound.setInteger("pageNumber", pageNumber);
			player.getHeldItem().stackTagCompound.setString("category", category);
		}
	}

	/**
	 * Gets the pagenumber you where on last time you closed the manual
	 *
	 * @param itemManual either manualGuideBook or manualHL
	 * @param player     a player with a manual
	 * @return
	 */
	public static int getCurrentPageNumber(Item itemManual, EntityPlayer player) {
		player = player.worldObj.getClosestPlayerToEntity(player, 20);
		if (player != null && player.getHeldItem() != null && player.getHeldItem().getItem() == itemManual)
			if (player.getHeldItem().stackTagCompound != null && player.getHeldItem().stackTagCompound.hasKey("pageNumber"))
				return player.getHeldItem().stackTagCompound.getInteger("pageNumber");
		return 1;
	}

	/**
	 * Gets the category you where on last time you closed the manual
	 *
	 * @param itemManual either manualGuideBook or manualHL
	 * @param player     a player with a manual
	 * @return returns a category
	 */
	public static ManualCategory getCurrentCategory(Item itemManual, EntityPlayer player) {
		player = player.worldObj.getClosestPlayerToEntity(player, 20);
		if (player != null && player.getHeldItem() != null && player.getHeldItem().getItem() == itemManual) {
			NBTTagCompound nbt = player.getHeldItem().getTagCompound();
			if (nbt != null && nbt.hasKey("category") && getCategoryFromString(nbt.getString("category"), itemManual) != null)
				return getCategoryFromString(nbt.getString("category"), itemManual);
			else
				return GuideBookEntryRegistry.itemsCategory;
		}
		return null;
	}

	/**
	 * gets a category from a string
	 *
	 * @param categoryName the name of a category
	 * @param itemManual   either manualGuideBook or manualHL
	 * @return category
	 */
	public static ManualCategory getCategoryFromString(String categoryName, Item itemManual) {
		if (itemManual == BLItemRegistry.manualGuideBook && categoryName != null)
			for (ManualCategory category : GuideBookEntryRegistry.CATEGORIES)
				if (category.name.equals(categoryName))
					return category;
		if (itemManual == BLItemRegistry.manualHL && categoryName != null)
			for (ManualCategory category : HLEntryRegistry.CATEGORIES)
				if (category.name.equals(categoryName))
					return category;
		return null;
	}
}
