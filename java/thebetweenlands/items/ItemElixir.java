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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.herblore.elixirs.effects.ElixirEffect;
import thebetweenlands.herblore.elixirs.effects.ElixirRegistry;

public class ItemElixir extends Item {
	//TODO: Make throwable
	
	private final List<ElixirEffect> effects = new ArrayList<ElixirEffect>();

	public ItemElixir() {
		this.setUnlocalizedName("elixir");

		this.effects.addAll(ElixirRegistry.getEffects());

		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}

	private ElixirEffect getElixirByID(int id) {
		for(ElixirEffect effect : this.effects) {
			if(id == effect.getID()) return effect;
		}
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (ElixirEffect effect : this.effects) {
			list.add(new ItemStack(item, 1, effect.getID()));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		try {
			return "item.thebetweenlands.elixir." + this.getElixirByID(stack.getItemDamage()).getEffectName();
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

	public ItemStack getElixirItem(ElixirEffect effect, int duration, int strength) {
		ItemStack elixirStack = new ItemStack(this, 1, effect.getID());
		NBTTagCompound elixirData = new NBTTagCompound();
		elixirData.setInteger("duration", duration);
		elixirData.setInteger("strength", strength);
		if(elixirStack.stackTagCompound == null) elixirStack.stackTagCompound = new NBTTagCompound();
		elixirStack.stackTagCompound.setTag("elixirData", elixirData);
		return elixirStack;
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
			ElixirEffect effect = this.getElixirByID(stack.getItemDamage());
			int duration = this.getElixirDuration(stack);
			int strength = this.getElixirStrength(stack);
			player.addPotionEffect(effect.createEffect(duration == -1 ? 200 : duration, strength == -1 ? 0 : strength));
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

	public int getElixirDuration(ItemStack stack) {
		if(stack.stackTagCompound != null && stack.stackTagCompound.hasKey("elixirData")) {
			NBTTagCompound elixirData = stack.stackTagCompound.getCompoundTag("elixirData");
			return elixirData.getInteger("duration");
		}
		return -1;
	}

	public int getElixirStrength(ItemStack stack) {
		if(stack.stackTagCompound != null && stack.stackTagCompound.hasKey("elixirData")) {
			NBTTagCompound elixirData = stack.stackTagCompound.getCompoundTag("elixirData");
			return elixirData.getInteger("strength");
		}
		return -1;
	}
}
