package thebetweenlands.items;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import thebetweenlands.herblore.effects.ElixirEffect;
import thebetweenlands.utils.PotionHelper;

public class ItemElixir extends Item {
	public final List<ElixirEffect> effects = new ArrayList<ElixirEffect>();

	public static final ElixirEffect TEST_EFFECT = new ElixirEffect("test");
	public static final ElixirEffect TEST_EFFECT2 = new ElixirEffect("test2");

	public ItemElixir() {
		this.setUnlocalizedName("elixir");

		this.effects.add(TEST_EFFECT);
		this.effects.add(TEST_EFFECT2);

		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < this.effects.size(); i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		try {
			return "item.thebetweenlands.elixir." + this.effects.get(stack.getItemDamage()).getEffectName();
		} catch (Exception e) {
			return "item.thebetweenlands.unknown";
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.drink;
	}

	public ItemStack getElixir(ElixirEffect effect) {
		return new ItemStack(this, 1, this.effects.indexOf(effect));
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
		return stack;
	}

	@Override
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
		if (!player.capabilities.isCreativeMode) {
			--stack.stackSize;
		}

		if (!world.isRemote) {
			ElixirEffect effect = this.effects.get(stack.getItemDamage());
			player.addPotionEffect(new PotionEffect(effect.getId(), effect.getDuration(), effect.getStrength()));
		}

		//Add empty dentrothyst vial
		/*if (!player.capabilities.isCreativeMode) {
			if (stack.stackSize <= 0) {
				return new ItemStack(Items.glass_bottle);
			}
			player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
		}*/

		return stack;
	}
}
