package thebetweenlands.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import thebetweenlands.entities.EntityBLArrow;
import thebetweenlands.utils.DecayableItemHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemWeedwoodBow extends ItemBow implements IDecayable {
	public static final int ANIMATION_LENGTH = 3;

	private IIcon[] iconArray;

	private IIcon[][] decayIcons;

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
			return decayIcons[time < 6 ? 1 : time < 10 ? 2 : 3][DecayableItemHelper.getDecayStage(usingItem)];
		}
		return decayIcons[0][DecayableItemHelper.getDecayStage(stack)];
	}

	@Override
	public IIcon getIconIndex(ItemStack stack) {
		return decayIcons[0][DecayableItemHelper.getDecayStage(stack)];
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
		boolean anglerToothArrow = player.inventory.hasItem(BLItemRegistry.anglerToothArrow);
		boolean poisonedAnglerToothArrow = player.inventory.hasItem(BLItemRegistry.poisonedAnglerToothArrow);
		boolean octineArrow = player.inventory.hasItem(BLItemRegistry.octineArrow);
		boolean basiliskArrow = player.inventory.hasItem(BLItemRegistry.basiliskArrow);

		if (canShoot || anglerToothArrow || poisonedAnglerToothArrow || octineArrow || basiliskArrow) {
			float power = maxUseDuration / 10.0F;
			power = (power * power + power * 2.0F) / 2.0F;

			power *= (DecayableItemHelper.getModifier(stack) - 0.5F) * 2 + 0.15F;

			if (power < 0.1F)
				return;

			if (power > 1.0F)
				power = 1.0F;

			EntityBLArrow entityarrow = new EntityBLArrow(world, player, power * 2.0f);
			if (!world.isRemote) {
				if (poisonedAnglerToothArrow)
					entityarrow.getDataWatcher().updateObject(17, 1);
				else if (octineArrow)
					entityarrow.getDataWatcher().updateObject(17, 2);
				else if (basiliskArrow)
					entityarrow.getDataWatcher().updateObject(17, 3);
			}
			if (power == 1.0F)
				entityarrow.setIsCritical(true);

			int powerEnchant = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);

			if (powerEnchant > 0)
				entityarrow.setDamage(entityarrow.getDamage() + (double) powerEnchant * 0.5D + 0.5D);

			int punchEnchant = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);

			if (punchEnchant > 0)
				entityarrow.setKnockbackStrength(punchEnchant);

			if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0)
				entityarrow.setFire(100);

			stack.damageItem(1, player);
			world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + power * 0.5F);

			if (canShoot)
				entityarrow.canBePickedUp = 2;
			else if (poisonedAnglerToothArrow)
				player.inventory.consumeInventoryItem(BLItemRegistry.poisonedAnglerToothArrow);
			else if (octineArrow)
				player.inventory.consumeInventoryItem(BLItemRegistry.octineArrow);
			else if (basiliskArrow)
				player.inventory.consumeInventoryItem(BLItemRegistry.basiliskArrow);
			else
				player.inventory.consumeInventoryItem(BLItemRegistry.anglerToothArrow);

			if (!world.isRemote)
				world.spawnEntityInWorld(entityarrow);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onUpdateFOV(FOVUpdateEvent event) {
		float fov = event.fov;
		if (event.entity.isUsingItem() && event.entity.getItemInUse().getItem() == this) {
			int duration = event.entity.getItemInUseDuration();
			float multiplier = duration / 10.0F;
			if (multiplier > 1.0F) {
				multiplier = 1.0F;
			} else {
				multiplier *= multiplier;
			}
			fov = 1.0F - multiplier * 0.15F;
		}
		event.newfov = fov;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack item, World world, EntityPlayer player) {
		ArrowNockEvent event = new ArrowNockEvent(player, item);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			return event.result;
		}

		boolean anglerToothArrow = player.inventory.hasItem(BLItemRegistry.anglerToothArrow);
		boolean poisonedAnglerToothArrow = player.inventory.hasItem(BLItemRegistry.poisonedAnglerToothArrow);
		boolean octineArrow = player.inventory.hasItem(BLItemRegistry.octineArrow);
		boolean basiliskArrow = player.inventory.hasItem(BLItemRegistry.basiliskArrow);
		if (player.capabilities.isCreativeMode || anglerToothArrow || poisonedAnglerToothArrow || octineArrow || basiliskArrow) {
			player.setItemInUse(item, this.getMaxItemUseDuration(item));
		}

		return item;
	}

	@Override
	public IIcon[] getIcons() {
		return iconArray;
	}

	@Override
	public void setDecayIcons(IIcon[][] decayIcons) {
		this.decayIcons = decayIcons;
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity holder, int slot, boolean isHeldItem) {
		DecayableItemHelper.onUpdate(itemStack, world, holder, slot, isHeldItem);
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List lines, boolean advancedItemTooltips) {
		DecayableItemHelper.addInformation(itemStack, player, lines, advancedItemTooltips);
	}
}
