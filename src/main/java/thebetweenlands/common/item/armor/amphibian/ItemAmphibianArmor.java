package thebetweenlands.common.item.armor.amphibian;

import com.google.common.collect.Multimap;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.item.IAmphibianArmorUpgrade;
import thebetweenlands.client.render.model.armor.ModelAmphibianArmor;
import thebetweenlands.client.render.model.armor.ModelBodyAttachment;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.armor.Item3DArmor;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.util.NBTHelper;

public class ItemAmphibianArmor extends Item3DArmor {

	private static final String NBT_UPGRADE_MAP_KEY = "thebetweenlands.amphibian_armor_upgrades";

	public ItemAmphibianArmor(EntityEquipmentSlot slot) {
		super(BLMaterialRegistry.ARMOR_AMPHIBIAN, 3, slot, "amphibian");

		this.setGemArmorTextureOverride(CircleGemType.AQUA, "amphibian_aqua");
		this.setGemArmorTextureOverride(CircleGemType.CRIMSON, "amphibian_crimson");
		this.setGemArmorTextureOverride(CircleGemType.GREEN, "amphibian_green");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ModelBodyAttachment createModel() {
		return new ModelAmphibianArmor();
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
		ItemStack stack = player.getHeldItem(hand);

		//TODO Testing
		for(IAmphibianArmorUpgrade upgrade : AmphibianArmorUpgrades.values()) {
			System.out.println(upgrade.getId() + ": " + this.getUpgradeCount(stack, upgrade));
		}

		if (player.isSneaking()) {
			player.openGui(TheBetweenlands.instance, CommonProxy.GUI_AMPHIBIAN_ARMOR, world, 0, 0, 0);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		} else {
			return super.onItemRightClick(world, player, hand);
		}
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		Multimap<String, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);

		if(slot == this.armorType) {
			for(IAmphibianArmorUpgrade upgrade : AmphibianArmorUpgrades.getUpgrades(this.armorType)) {
				int count = this.getUpgradeCount(stack, upgrade);

				if(count > 0) {
					upgrade.applyAttributeModifiers(this.armorType, stack, count, modifiers);
				}
			}
		}

		return modifiers;
	}

	public void setUpgradeCounts(ItemStack stack, IInventory inv) {
		NBTTagCompound upgradesMap = new NBTTagCompound();

		for(int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack upgradeItem = inv.getStackInSlot(i);

			if(!upgradeItem.isEmpty()) {
				IAmphibianArmorUpgrade upgrade = AmphibianArmorUpgrades.getUpgrade(this.armorType, upgradeItem);

				if(upgrade != null) {
					String idStr = upgrade.getId().toString();
					upgradesMap.setInteger(idStr, upgradesMap.getInteger(idStr) + 1);
				}
			}
		}

		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);
		nbt.setTag(NBT_UPGRADE_MAP_KEY, upgradesMap);
	}

	public int getUpgradeCount(ItemStack stack, IAmphibianArmorUpgrade upgrade) {
		NBTTagCompound nbt = stack.getTagCompound();

		if(nbt != null) {
			return nbt.getCompoundTag(NBT_UPGRADE_MAP_KEY).getInteger(upgrade.getId().toString());
		}

		return 0;
	}
}
