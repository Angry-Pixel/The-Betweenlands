package thebetweenlands.items.herblore;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.entities.projectiles.EntityElixir;
import thebetweenlands.herblore.elixirs.ElixirRecipe;
import thebetweenlands.herblore.elixirs.ElixirRecipes;
import thebetweenlands.herblore.elixirs.ElixirRegistry;
import thebetweenlands.herblore.elixirs.effects.ElixirEffect;
import thebetweenlands.items.BLItemRegistry;

public class ItemElixir extends Item {
	//TODO: Make throwable

	@SideOnly(Side.CLIENT)
	private IIcon iconLiquid, iconVialOrange;

	private final List<ElixirEffect> effects = new ArrayList<ElixirEffect>();

	public ItemElixir() {
		this.setUnlocalizedName("item.thebetweenlands.elixir");

		this.effects.addAll(ElixirRegistry.getEffects());

		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);

		this.setTextureName("thebetweenlands:strictlyHerblore/misc/vialGreen");
	}

	private ElixirEffect getElixirByID(int id) {
		for(ElixirEffect effect : this.effects) {
			if(id == effect.getID()) return effect;
		}
		return null;
	}

	private ElixirEffect getElixirFromItem(ItemStack stack) {
		return this.getElixirByID(stack.getItemDamage() / 2);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		super.registerIcons(reg);
		this.iconLiquid = reg.registerIcon("thebetweenlands:strictlyHerblore/misc/vialLiquid");
		this.iconVialOrange = reg.registerIcon("thebetweenlands:strictlyHerblore/misc/vialOrange");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		if(pass == 0) {
			ElixirEffect effect = this.getElixirFromItem(stack);
			if(effect != null) {
				ElixirRecipe recipe = ElixirRecipes.getFromEffect(effect);
				if(recipe != null) {
					return recipe.infusionFinishedColor;
				}
			}
		}
		return 0xFFFFFFFF;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
		return pass == 0 ? this.iconLiquid : super.getIconFromDamageForRenderPass(damage, pass);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage) {
		return damage % 2 == 0 ? this.itemIcon : this.iconVialOrange;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (ElixirEffect effect : this.effects) {
			list.add(new ItemStack(item, 1, effect.getID() * 2));
			list.add(new ItemStack(item, 1, effect.getID() * 2 + 1));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		try {
			return "item.thebetweenlands.elixir." + this.getElixirFromItem(stack).getEffectName();
		} catch (Exception e) {
			return "item.thebetweenlands.unknown";
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		if(stack.stackTagCompound != null && stack.stackTagCompound.hasKey("throwing") && stack.stackTagCompound.getBoolean("throwing")) {
			return EnumAction.bow;
		}
		return EnumAction.drink;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int inUseCount) {
		if(stack.stackTagCompound != null && stack.stackTagCompound.hasKey("throwing") && stack.stackTagCompound.getBoolean("throwing")) {
			if (!player.capabilities.isCreativeMode) {
				--stack.stackSize;
				if(stack.stackSize <= 0) {
					player.setCurrentItemOrArmor(0, null);
				}
			}
			world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			if (!world.isRemote) {
				int useCount = this.getMaxItemUseDuration(stack) - inUseCount;
				float strength = Math.min(0.4F + useCount / 15.0F, 2.0F);
				world.spawnEntityInWorld(new EntityElixir(world, player, stack, strength));
			}
		}
	}
	
	/**
	 * Creates an item stack with the specified effect, duration, strength and vial type.
	 * Vial types: 0 = green, 1 = orange
	 * @param effect
	 * @param duration
	 * @param strength
	 * @param vialType
	 * @return
	 */
	public ItemStack getElixirItem(ElixirEffect effect, int duration, int strength, int vialType) {
		ItemStack elixirStack = new ItemStack(this, 1, effect.getID() * 2 + vialType);
		NBTTagCompound elixirData = new NBTTagCompound();
		elixirData.setInteger("duration", duration);
		elixirData.setInteger("strength", strength);
		if(elixirStack.stackTagCompound == null) elixirStack.stackTagCompound = new NBTTagCompound();
		elixirStack.stackTagCompound.setTag("elixirData", elixirData);
		return elixirStack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		if(stack.stackTagCompound != null && stack.stackTagCompound.hasKey("throwing") && stack.stackTagCompound.getBoolean("throwing")) {
			return 100000;
		}
		return 32;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(stack.stackTagCompound == null) {
			stack.stackTagCompound = new NBTTagCompound();
		}
		stack.stackTagCompound.setBoolean("throwing", player.isSneaking());
		player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
		return stack;
	}

	@Override
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
		if (!player.capabilities.isCreativeMode) {
			--stack.stackSize;
		}

		if (!world.isRemote) {
			ElixirEffect effect = this.getElixirFromItem(stack);
			int duration = this.getElixirDuration(stack);
			int strength = this.getElixirStrength(stack);
			player.addPotionEffect(effect.createEffect(duration == -1 ? 1200 : duration, strength == -1 ? 0 : strength));
		}

		//Add empty dentrothyst vial
		if (!player.capabilities.isCreativeMode) {
			if (stack.stackSize <= 0) {
				return BLItemRegistry.dentrothystVial.createStack(stack.getItemDamage() % 2 == 0 ? 1 : 2);
			}
			player.inventory.addItemStackToInventory(BLItemRegistry.dentrothystVial.createStack(stack.getItemDamage() % 2 == 0 ? 1 : 2));
		}

		return stack;
	}

	public void applyEffect(ItemStack stack, EntityLivingBase entity, double modifier) {
		ElixirEffect effect = this.getElixirFromItem(stack);
		int strength = this.getElixirStrength(stack);
		int duration = this.getElixirDuration(stack);
		entity.addPotionEffect(effect.createEffect(duration == -1 ? (int)(1200 * modifier) : (int)(duration * modifier), strength == -1 ? 0 : strength));
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
