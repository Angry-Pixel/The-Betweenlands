package thebetweenlands.common.item.armor.amphibious;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.item.IAmphibiousArmorUpgrade;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;

public class ItemAmphibiousArmourUpgradeTrigger extends Item {

	AmphibiousArmorEffectsHelper armorEffectsHelper = new AmphibiousArmorEffectsHelper();
	public final Map<IAmphibiousArmorUpgrade, Boolean> ALLOWED_UPGRADES = new HashMap<IAmphibiousArmorUpgrade, Boolean>();

	public ItemAmphibiousArmourUpgradeTrigger() {
		this.maxStackSize = 1;
		this.setMaxDamage(600);
		this.setCreativeTab(BLCreativeTabs.GEARS);
		initAllowedUpgradeMap();

		this.addPropertyOverride(new ResourceLocation("selected_effect"), (stack, world, entity) -> {
			if (entity instanceof EntityPlayer) {
				ItemStack chest = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

				List<IAmphibiousArmorUpgrade> upgradeListChest = new ArrayList<>();

				if (chest.getItem() instanceof ItemAmphibiousArmor && chest.getItem() == ItemRegistry.AMPHIBIOUS_CHESTPLATE)
					if (!getUpgradeList(chest, EntityEquipmentSlot.CHEST).isEmpty())
						for (IAmphibiousArmorUpgrade upgrade : getUpgradeList(chest, EntityEquipmentSlot.CHEST))
							if (isValidUpgrade(upgrade))
								upgradeListChest.add(upgrade);

				if (!upgradeListChest.isEmpty() && stack.hasTagCompound() && stack.getTagCompound().hasKey("scrollPos") && stack.getTagCompound().getInteger("scrollPos") != -1) {
					int scrollPos = stack.getTagCompound().getInteger("scrollPos");

					scrollPos = MathHelper.clamp(scrollPos, 0, upgradeListChest.size() - 1);

					if (upgradeListChest.get(scrollPos) == AmphibiousArmorUpgrades.URCHIN)
						return 1;

					if (upgradeListChest.get(scrollPos) == AmphibiousArmorUpgrades.FISH_VORTEX)
						return 2;

					if (upgradeListChest.get(scrollPos) == AmphibiousArmorUpgrades.ELECTRIC)
						return 3;
				}
				return 0;
			}
			return 0;
		});
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.addAll(ItemTooltipHandler.splitTooltip(I18n.format("tooltip.bl.amphibious_ability.trigger"), 0));
	}

	public boolean isValidUpgrade(IAmphibiousArmorUpgrade upgradeIn) {
		return ALLOWED_UPGRADES.get(upgradeIn) != null;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
		ItemStack stack = player.getHeldItem(hand);
		ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		NBTTagCompound nbt = player.getEntityData();
		List<IAmphibiousArmorUpgrade> upgradeListChest = new ArrayList<>();

		if (chest.getItem() instanceof ItemAmphibiousArmor && chest.getItem() == ItemRegistry.AMPHIBIOUS_CHESTPLATE)
			if (!getUpgradeList(chest, EntityEquipmentSlot.CHEST).isEmpty())
				for (IAmphibiousArmorUpgrade upgrade : getUpgradeList(chest, EntityEquipmentSlot.CHEST))
					if (isValidUpgrade(upgrade))
						upgradeListChest.add(upgrade);

        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setInteger("scrollPos", -1);
            }

		if (player.isSneaking()) {
			if (chest.getItem() instanceof ItemAmphibiousArmor && chest.getItem() == ItemRegistry.AMPHIBIOUS_CHESTPLATE) {
				int scrollSize = upgradeListChest.size();
				if (!upgradeListChest.isEmpty()) {
					if (stack.getTagCompound().getInteger("scrollPos") < scrollSize)
						stack.getTagCompound().setInteger("scrollPos", stack.getTagCompound().getInteger("scrollPos") + 1);
					if (stack.getTagCompound().getInteger("scrollPos") >= scrollSize)
						stack.getTagCompound().setInteger("scrollPos", 0);
					player.sendStatusMessage(new TextComponentTranslation("chat.aa_trigger.selected_effect", getUpgradeName((AmphibiousArmorUpgrades) upgradeListChest.get(stack.getTagCompound().getInteger("scrollPos")))), true);
				}
			}

			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);

		} else {
			//activate effect based on armour upgrade and counter selection
			if (chest.getItem() instanceof ItemAmphibiousArmor && chest.getItem() == ItemRegistry.AMPHIBIOUS_CHESTPLATE) {

				if (stack.getTagCompound().getInteger("scrollPos") != -1 && !upgradeListChest.isEmpty()) {

					if (upgradeListChest.get(stack.getTagCompound().getInteger("scrollPos")) == AmphibiousArmorUpgrades.URCHIN) {
						int urchinCount = ((ItemAmphibiousArmor) chest.getItem()).getUpgradeCount(chest, AmphibiousArmorUpgrades.URCHIN);
						long urchinAOECooldown = nbt.getLong(armorEffectsHelper.NBT_URCHIN_AOE_COOLDOWN);
						if (urchinCount >= 1) {
							if (world.getTotalWorldTime() >= urchinAOECooldown) {
								if (!world.isRemote && world.getDifficulty() != EnumDifficulty.PEACEFUL) {
									armorEffectsHelper.spawnUrchinSpikes(world, player, urchinCount);
									nbt.setLong(armorEffectsHelper.NBT_URCHIN_AOE_COOLDOWN, world.getTotalWorldTime() + 50);
									((ItemAmphibiousArmor)chest.getItem()).damageUpgrade(chest, AmphibiousArmorUpgrades.URCHIN, 1, IAmphibiousArmorUpgrade.DamageEvent.ON_USE, false);
								}
							}
						}
					}

					if (upgradeListChest.get(stack.getTagCompound().getInteger("scrollPos")) == AmphibiousArmorUpgrades.FISH_VORTEX) {
						int vortexCount = ((ItemAmphibiousArmor) chest.getItem()).getUpgradeCount(chest, AmphibiousArmorUpgrades.FISH_VORTEX);
						if (vortexCount >= 1)
							if (!world.isRemote && world.getDifficulty() != EnumDifficulty.PEACEFUL) {
								armorEffectsHelper.activateFishVortex(world, player, vortexCount);
								((ItemAmphibiousArmor)chest.getItem()).damageUpgrade(chest, AmphibiousArmorUpgrades.FISH_VORTEX, 1, IAmphibiousArmorUpgrade.DamageEvent.ON_USE, false);
							}
					}

					if (upgradeListChest.get(stack.getTagCompound().getInteger("scrollPos")) == AmphibiousArmorUpgrades.ELECTRIC) {
						int electricCount = ((ItemAmphibiousArmor) chest.getItem()).getUpgradeCount(chest, AmphibiousArmorUpgrades.ELECTRIC);
						long electricCooldown = nbt.getLong(armorEffectsHelper.NBT_ELECTRIC_COOLDOWN);
						if (electricCount >= 1) {
							if (world.getTotalWorldTime() >= electricCooldown) {
								if (!world.isRemote && world.getDifficulty() != EnumDifficulty.PEACEFUL) {
									armorEffectsHelper.spawnElectricEntity(world, player, player, electricCount);
									nbt.setLong(armorEffectsHelper.NBT_ELECTRIC_COOLDOWN, world.getTotalWorldTime() + 50);
									((ItemAmphibiousArmor)chest.getItem()).damageUpgrade(chest, AmphibiousArmorUpgrades.ELECTRIC, 1, IAmphibiousArmorUpgrade.DamageEvent.ON_USE, false);
								}
							}
						}
					}

					// OTHER STUFFS HERE?
				}
			}

			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
	}

	public List<IAmphibiousArmorUpgrade> getUpgradeList(ItemStack stack, EntityEquipmentSlot slotType) {
		List<IAmphibiousArmorUpgrade> nameList = new ArrayList<>();
		for (IAmphibiousArmorUpgrade upgrade : AmphibiousArmorUpgrades.getUpgrades(slotType)) {
			int count = ((ItemAmphibiousArmor) stack.getItem()).getUpgradeCount(stack, upgrade);
			if (count > 0)
				nameList.add(upgrade);
		}
		return nameList;
	}

    private TextComponentTranslation getUpgradeName(AmphibiousArmorUpgrades upgrade) {
    	switch (upgrade) {
		case AQUA_GEM:
		case BREATHING:
		case BUOYANCY:
		case CRIMSON_GEM:
		case DECAY_DECREASE:
		case GREEN_GEM:
		case KNOCKBACK_RESISTANCE:
		case MINING_SPEED:
		case MOVEMENT_SPEED:
		case THORNS:
		case TOUGHNESS:
		case VISIBILITY:
		case ASCENT_BOOST:
		case GLIDE:
		break;
		case ELECTRIC:
			return new TextComponentTranslation("chat.aa_item.electric");
		case FISH_VORTEX:
			return new TextComponentTranslation("chat.aa_item.vortex");
		case URCHIN:
			return new TextComponentTranslation("chat.aa_item.urchin_spikes");
		default:
			break;
    	}
		return new TextComponentTranslation("");
    }

    private boolean hasTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
            return false;
        }
        return true;
    }

	private void initAllowedUpgradeMap() {
		if (ALLOWED_UPGRADES.isEmpty()) {
			ALLOWED_UPGRADES.put(AmphibiousArmorUpgrades.FISH_VORTEX.getUpgrade(EntityEquipmentSlot.CHEST, new ItemStack(ItemRegistry.AA_UPGRADE_VORTEX)), true);
			ALLOWED_UPGRADES.put(AmphibiousArmorUpgrades.URCHIN.getUpgrade(EntityEquipmentSlot.CHEST, new ItemStack(ItemRegistry.AA_UPGRADE_URCHIN)), true);
			ALLOWED_UPGRADES.put(AmphibiousArmorUpgrades.ELECTRIC.getUpgrade(EntityEquipmentSlot.CHEST, new ItemStack(ItemRegistry.AA_UPGRADE_ELECTRIC)), true);
		}
	}
}
