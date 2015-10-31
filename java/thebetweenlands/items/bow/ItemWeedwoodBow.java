package thebetweenlands.items.bow;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import thebetweenlands.entities.projectiles.EntityBLArrow;
import thebetweenlands.items.ICorrodible;
import thebetweenlands.utils.CorrodibleItemHelper;

public class ItemWeedwoodBow extends ItemBow implements ICorrodible {
	public static final int ANIMATION_LENGTH = 3;

	private IIcon[] iconArray;

	private IIcon[][] corrosionIcons;

	public ItemWeedwoodBow() {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister icon) {
		itemIcon = icon.registerIcon(iconString);
		iconArray = new IIcon[ANIMATION_LENGTH + 1];
		iconArray[0] = itemIcon;
		for (int iconIndex = 1; iconIndex < iconArray.length; iconIndex++) {
			iconArray[iconIndex] = icon.registerIcon(iconString + (iconIndex - 1));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
		if (usingItem != null) {
			int time = 36000 - useRemaining;
			return corrosionIcons[time < 6 ? 1 : time < 10 ? 2 : 3][CorrodibleItemHelper.getCorrosionStage(usingItem)];
		}
		return corrosionIcons[0][CorrodibleItemHelper.getCorrosionStage(stack)];
	}

	@Override
	public IIcon getIconIndex(ItemStack stack) {
		return corrosionIcons[0][CorrodibleItemHelper.getCorrosionStage(stack)];
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return getIconIndex(stack);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 100000;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int inUseCount) {
		int maxUseDuration = getMaxItemUseDuration(stack) - inUseCount;

		ArrowLooseEvent event = new ArrowLooseEvent(player, stack, maxUseDuration);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled())
			return;
		maxUseDuration = event.charge;

		boolean canShoot = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;

		EnumArrowType type = null;
		ItemBLArrow arrowItem = null;
		for(int i = 0; i < player.inventory.mainInventory.length; i++) {
			ItemStack currentStack = player.inventory.mainInventory[i];
			if(currentStack != null && currentStack.getItem() instanceof ItemBLArrow) {
				arrowItem =  ((ItemBLArrow)currentStack.getItem());
				type = arrowItem.getType();
				break;
			}
		}

		if (canShoot || type != null) {
			float power = maxUseDuration / 10.0F;
			power = (power * power + power * 2.0F) / 2.0F;

			power *= (CorrodibleItemHelper.getModifier(stack) - 0.5F) * 2 + 0.15F;

			if (power < 0.1F)
				return;

			if (power > 1.0F)
				power = 1.0F;

			EntityBLArrow entityarrow = new EntityBLArrow(world, player, power * 2.0f);
			if (!world.isRemote) {
				entityarrow.setArrowType(type != null ? type : EnumArrowType.DEFAULT);
			}
			if (power == 1.0F) {
				entityarrow.setIsCritical(true);
			}

			int powerEnchant = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);

			if (powerEnchant > 0) {
				entityarrow.setDamage(entityarrow.getDamage() + (double) powerEnchant * 0.5D + 0.5D);
			}

			int punchEnchant = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);

			if (punchEnchant > 0) {
				entityarrow.setKnockbackStrength(punchEnchant);
			}

			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0) {
				entityarrow.setFire(100);
			}

			stack.damageItem(1, player);
			world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + power * 0.5F);

			if (player.capabilities.isCreativeMode) {
				entityarrow.canBePickedUp = 2;
			} else if (arrowItem != null) {
				player.inventory.consumeInventoryItem(arrowItem);
			}

			if (!world.isRemote) {
				world.spawnEntityInWorld(entityarrow);
			}
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack item, World world, EntityPlayer player) {
		ArrowNockEvent event = new ArrowNockEvent(player, item);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			return event.result;
		}

		EnumArrowType type = null;
		for(int i = 0; i < player.inventory.mainInventory.length; i++) {
			ItemStack currentStack = player.inventory.mainInventory[i];
			if(currentStack != null && currentStack.getItem() instanceof ItemBLArrow) {
				type = ((ItemBLArrow)currentStack.getItem()).getType();
				break;
			}
		}

		if (player.capabilities.isCreativeMode || type != null) {
			player.setItemInUse(item, this.getMaxItemUseDuration(item));
		}

		return item;
	}

	@Override
	public IIcon[] getIcons() {
		return iconArray;
	}

	@Override
	public void setCorrosionIcons(IIcon[][] corrosionIcons) {
		this.corrosionIcons = corrosionIcons;
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity holder, int slot, boolean isHeldItem) {
		CorrodibleItemHelper.onUpdate(itemStack, world, holder, slot, isHeldItem);
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List lines, boolean advancedItemTooltips) {
		CorrodibleItemHelper.addInformation(itemStack, player, lines, advancedItemTooltips);
	}
}
