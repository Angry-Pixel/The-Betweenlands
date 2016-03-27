package thebetweenlands.event.item;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesFood;
import thebetweenlands.event.player.PlayerItemEventHandler.Sickness;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.items.ICorrodible;
import thebetweenlands.items.IEquippable;
import thebetweenlands.recipes.misc.CompostRecipe;

/**
 * This class allows for the prevention of held item tooltips
 * from displayed such as they would from modification of
 * item stack NBT while being held by the player.
 * 
 * <p>Exclusions are a list of strings that the equality
 * test between NBT compounds tags uses to ignore certain
 * tags. Exclusions can be registered though use of the following
 * methods:
 * {@link #registerExclusion(Item, List)},
 * {@link #registerExclusion(Class, List)}, and
 * {@link #registerExclusion(Item, ExclusionEntry)}.
 * 
 * @author Paul Fulham
 */
public class ItemTooltipHandler {
	public static final ItemTooltipHandler INSTANCE = new ItemTooltipHandler();

	private static final Map<Item, ExclusionEntry> ITEM_EXCLUSIONS = new HashMap<Item, ExclusionEntry>();
	private static final List<ExclusionEntryClass> CLASS_EXCLUSIONS = new ArrayList<ExclusionEntryClass>();
	private static final List<String> NULL_EXCLUSIONS = new ArrayList<String>(0);
	static {
		registerExclusion(ICorrodible.class, Arrays.asList("Corrosion"));
	}

	/**
	 * Local reference to the Minecraft instance
	 */
	private Minecraft mc;

	/**
	 * The field object for {@link net.minecraft.client.gui.GuiIngame#remainingHighlightTicks}
	 * @see #overrideRemainingHighlightTicks()
	 */
	private Field remainingHighlightTicksField;

	/**
	 * Local version of {@link net.minecraft.client.gui.GuiIngame#highlightingItemStack}
	 * @see #onClientTickEvent(cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent)
	 */
	private ItemStack highlightingItemStack;

	/**
	 * Local version of {@link net.minecraft.client.gui.GuiIngame#remainingHighlightTicks}
	 * @see #onClientTickEvent(cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent)
	 */
	private int remainingHighlightTicks = 0;

	public ItemTooltipHandler() {
		mc = Minecraft.getMinecraft();
		remainingHighlightTicksField = ReflectionHelper.findField(GuiIngame.class, new String[] { "remainingHighlightTicks", "field_92017_k", "r" });
	}

	/**
	 * Performs logic similar to {@link net.minecraft.client.gui.GuiIngame#updateTick()},
	 * allowing for prevention of held item tooltips from displayed such as they would
	 * from modification of item stack NBT while being held by the player.
	 * 
	 * @param event client tick event
	 */
	@SubscribeEvent
	public void onClientTickEvent(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			return;
		}
		if (mc.gameSettings.heldItemTooltips) {
			if (mc.thePlayer != null) {
				ItemStack itemStack = mc.thePlayer.inventory.getCurrentItem();
				if (itemStack == null) {
					remainingHighlightTicks = 0;
				} else if (highlightingItemStack != null && itemStack.getItem() == highlightingItemStack.getItem() && areItemStackTagsEqual(itemStack, highlightingItemStack, getExclusions(itemStack)) && (itemStack.isItemStackDamageable() || itemStack.getItemDamage() == highlightingItemStack.getItemDamage())) {
					if (remainingHighlightTicks > 0) {
						remainingHighlightTicks--;
					}
				} else {
					remainingHighlightTicks = 40;
				}
				highlightingItemStack = itemStack;
				overrideRemainingHighlightTicks();
			}
		}
	}

	/**
	 * Sets {@link net.minecraft.client.gui.GuiIngame#remainingHighlightTicks}
	 * to {@link #remainingHighlightTicks} using reflection.
	 */
	private void overrideRemainingHighlightTicks() {
		try {
			remainingHighlightTicksField.set(mc.ingameGUI, remainingHighlightTicks);
		} catch (IllegalAccessException e) {
		}
	}

	/**
	 * Returns <tt>true</tt> if the two specifies item stacks' NBT
	 * compound tags are <i>equal</i> to one another.
	 * 
	 * @param a one NBT compound tag to be tested for equality
	 * @param b the other NBT compound tag to be tested for equality
	 * @param exclusions a list of tags to be excluded in checking equality
	 * @return <tt>true</tt> if the two NBT compounds tags are equal
	 * @see #areNBTCompoundsEquals(NBTTagCompound, NBTTagCompound, List)
	 */
	public static boolean areItemStackTagsEqual(ItemStack a, ItemStack b, List<String> exclusions) {
		if (a == null && b == null) {
			return true;
		}
		if (a != null && b != null) {
			if (a.stackTagCompound == null && b.stackTagCompound == null) {
				return true;
			}
			if (a.stackTagCompound == null ^ b.stackTagCompound == null) {
				return false;
			}
			return areNBTCompoundsEquals(a.stackTagCompound, b.stackTagCompound, exclusions);
		}
		return false;
	}

	/**
	 * Returns <tt>true</tt> if the two specified NBT compound tags
	 * are <i>equal</i> to one another. Two NBT compound tags are
	 * considered equal if both NBT compounds tags contain all of
	 * the same keys with the same values, while ignoring tags
	 * whose keys are in the exclusions list.
	 * 
	 * @param a one NBT compound tag to be tested for equality
	 * @param b the other NBT compound tag to be tested for equality
	 * @param exclusions a list of tags to be excluded in checking equality
	 * @return <tt>true</tt> if the two NBT compounds tags are equal
	 */
	public static boolean areNBTCompoundsEquals(NBTTagCompound a, NBTTagCompound b, List<String> exclusions) {
		Stack<String> tagOwners = new Stack<String>();
		Stack<NBTTagCompound> aTagCompounds = new Stack<NBTTagCompound>();
		Stack<NBTTagCompound> bTagCompounds = new Stack<NBTTagCompound>();
		tagOwners.push("");
		aTagCompounds.push(a);
		bTagCompounds.push(b);
		while (!aTagCompounds.isEmpty()) {
			String tagOwner = tagOwners.pop();
			NBTTagCompound aCurrentTagCompound = aTagCompounds.pop();
			NBTTagCompound bCurrentTagCompound = bTagCompounds.pop();
			Set<String> aKeys = aCurrentTagCompound.func_150296_c();
			Set<String> bKeys = bCurrentTagCompound.func_150296_c();
			for (String key : bKeys) {
				if (exclusions.contains(key)) {
					continue;
				}
				if (!aKeys.contains(key)) {
					return false;
				}
			}
			for (String key : aKeys) {
				String totalKey = tagOwner == "" ? key : tagOwner + '.' + key;
				if (exclusions.contains(totalKey)) {
					continue;
				}
				NBTBase aTag = aCurrentTagCompound.getTag(key);
				NBTBase bTag = bCurrentTagCompound.getTag(key);
				if (aTag instanceof NBTTagCompound && bTag instanceof NBTTagCompound) {
					tagOwners.push(totalKey);
					aTagCompounds.push((NBTTagCompound) aTag);
					bTagCompounds.push((NBTTagCompound) bTag);
				} else {
					if (!aTag.equals(bTag)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Registers an exclusion for the provided item.
	 * 
	 * @param item item that the exclusion applies to
	 * @param exclusionList list of strings to be used by {@link #areItemStackTagsEqual(ItemStack, ItemStack, List)}
	 * 
	 */
	public static void registerExclusion(Item item, List<String> exclusionList) {
		ITEM_EXCLUSIONS.put(item, new ExclusionEntry(exclusionList));
	}

	/**
	 * Registers an exclusion for items that are an instance of the provided class.
	 * 
	 * @param clazz class that the exclusion applies to
	 * @param exclusionList list of strings to be used by {@link #areItemStackTagsEqual(ItemStack, ItemStack, List)}
	 * 
	 */
	public static void registerExclusion(Class<?> clazz, List<String> exclusionList) {
		CLASS_EXCLUSIONS.add(new ExclusionEntryClass(clazz, exclusionList));
	}

	/**
	 * Registers an exclusion for the provided item
	 * 
	 * @param item item that the exclusion applies to
	 * @param entry exclusion entry object whose
	 * {@link ExclusionEntry#getExclusionList(ItemStack)} method is used
	 * to get the exclusion list that is used by {@link #areItemStackTagsEqual(ItemStack, ItemStack, List)}
	 * 
	 */
	public static void registerExclusion(Item item, ExclusionEntry entry) {
		ITEM_EXCLUSIONS.put(item, entry);
	}

	/**
	 * The exclusion is first attempted to be retrieved from the map
	 * of items to their exclusion entries that were registered through
	 * {@link #registerExclusion(Item, List)} or
	 * {@link #registerExclusion(Item, ExclusionEntry)}.
	 * 
	 * <p>If no mapping was found for the item, then the class
	 * entries are iterated over to find the exclusion that applies to
	 * the item's class. The first found class exclusion that applies
	 * to the item's class is used to get the exclusion list.
	 * 
	 * @param itemStack an item stack to get the exclusions for
	 * @return the exclusion list
	 */
	private static List<String> getExclusions(ItemStack itemStack) {
		Item item = itemStack.getItem();
		if (ITEM_EXCLUSIONS.containsKey(item)) {
			return ITEM_EXCLUSIONS.get(item).getExclusionList(itemStack);
		}
		for (ExclusionEntryClass entry : CLASS_EXCLUSIONS) {
			if (entry.appliesFor(itemStack)) {
				return entry.getExclusionList(itemStack);
			}
		}
		return NULL_EXCLUSIONS;
	}

	/**
	 * Adds the "Compostable" and Gem tooltips to items
	 * @param event
	 */
	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent event) {
		if(CompostRecipe.getCompostRecipe(event.itemStack) != null) {
			event.toolTip.add(StatCollector.translateToLocal("compost.compostable"));
		}
		CircleGem circleGem = CircleGem.getGem(event.itemStack);
		if(circleGem != CircleGem.NONE) {
			String colorPrefix = "";
			switch(circleGem){
			case AQUA:
				colorPrefix = "\u00A79";
				break;
			case CRIMSON:
				colorPrefix = "\u00A7c";
				break;
			case GREEN:
				colorPrefix = "\u00A7a";
				break;
			default:
			}
			event.toolTip.add(colorPrefix + StatCollector.translateToLocal("circlegem." + circleGem.name));
		}
		if(event.itemStack.getItem() instanceof ItemFood) {
			EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();
			if(player != null) {
				EntityPropertiesFood property = BLEntityPropertiesRegistry.HANDLER.getProperties(event.entityPlayer, EntityPropertiesFood.class);
				Sickness sickness = property.getSickness((ItemFood)event.itemStack.getItem());
				event.toolTip.add(StatCollector.translateToLocal("food_sickness.state." + sickness.name().toLowerCase()));
			}
		}
		if(event.itemStack.getItem() instanceof IEquippable) {
			event.toolTip.add(StatCollector.translateToLocal("item.equippable"));
		}
	}

	public static List<String> splitTooltip(String tooltip, int indent) {
		String indentStr = new String(new char[indent]).replace('\0', ' ');
		List<String> lines = new ArrayList<String>();
		String[] splits = tooltip.split("\\\\n");
		for(int i = 0; i < splits.length; i++) {
			splits[i] = indentStr + splits[i];
		}
		lines.addAll(Arrays.asList(splits));
		return lines;
	}
}
