package thebetweenlands.common.item.misc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.ItemRegistry;

public class ItemLoreScrap extends Item implements ItemRegistry.ISubItemsItem {
	public static final String[] PAGE_NAMES = new String[]{"them", "mutants", "shadows", "ruins", "heads", "tar", "dungeon", "pitstone", "tower", "fort"};

	public ItemLoreScrap() {
		this.maxStackSize = 1;
		this.setCreativeTab(BLCreativeTabs.SPECIALS);

		this.addPropertyOverride(new ResourceLocation("page"), new IItemPropertyGetter() {
			@Override
			public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
				return getPage(stack);
			}
		});
	}

	public static ItemStack createStack(int page) {
		ItemStack stack = new ItemStack(ItemRegistry.LORE_SCRAP);
		setPage(stack, page);
		return stack;
	}

	public static ItemStack createRandomStack(Random random) {
		ItemStack stack = new ItemStack(ItemRegistry.LORE_SCRAP);
		stack = setRandomPage(stack, random);
		return stack;
	}

	public static ItemStack setRandomPage(ItemStack stack, Random random) {
		setPage(stack, random.nextInt(PAGE_NAMES.length));
		return stack;
	}

	public static ItemStack setPage(ItemStack stack, int page) {
		if(page >= 0 && page < PAGE_NAMES.length) {
			stack.setItemDamage(page);
		}
		return stack;
	}

	public static int getPage(ItemStack stack) {
		int dmg = stack.getItemDamage();
		if(dmg >= 0 && dmg < PAGE_NAMES.length) {
			return dmg;
		}
		return -1;
	}

	@Nullable
	public static String getPageName(ItemStack stack) {
		int page = getPage(stack);
		if(page != -1) {
			return PAGE_NAMES[page];
		}
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		for(int i = 0; i < PAGE_NAMES.length; i++) {
			ItemStack stack = new ItemStack(item);
			setPage(stack, i);
			subItems.add(stack);
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		String pageName = getPageName(stack);
		if(pageName != null) {
			return super.getUnlocalizedName() + "." + pageName;
		}
		return super.getUnlocalizedName();
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack itemStackIn = playerIn.getHeldItem(hand);
		if (getPage(itemStackIn) != -1) {
			playerIn.openGui(TheBetweenlands.INSTANCE, CommonProxy.GUI_LORE, worldIn, hand == EnumHand.MAIN_HAND ? 0 : 1, 0, 0);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
	}

	@Override
	public Map<Integer, ResourceLocation> getModels() {
		Map<Integer, ResourceLocation> map = new HashMap<Integer, ResourceLocation>();
		for(int i = 0; i < PAGE_NAMES.length; i++) {
			map.put(i, this.getRegistryName());
		}
		return map;
	}
}
