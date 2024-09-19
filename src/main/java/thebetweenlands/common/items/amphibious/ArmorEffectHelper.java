package thebetweenlands.common.items.amphibious;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.Tags;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.item.amphibious.AmphibiousArmorUpgrade;
import thebetweenlands.common.component.item.AmphibiousUpgrades;
import thebetweenlands.common.entities.ElectricShock;
import thebetweenlands.common.entities.FishVortex;
import thebetweenlands.common.entities.UrchinSpike;
import thebetweenlands.common.registries.AmphibiousArmorUpgradeRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.EntityRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static thebetweenlands.common.items.amphibious.AmphibiousArmorItem.NBT_ELECTRIC_COOLDOWN;
import static thebetweenlands.common.items.amphibious.AmphibiousArmorItem.NBT_URCHIN_AOE_COOLDOWN;

public class ArmorEffectHelper {

	public static AABB proximityBox(Player player, double xSize, double ySize, double zSize) {
		return new AABB(player.blockPosition()).inflate(xSize, ySize, zSize);
	}

	public static boolean activateFishVortex(Level level, Player player, int vortexCount) {
		List<LivingEntity> list = findNearbyEntities(level, proximityBox(player, 8D, 4D, 8D));
		boolean activated = false;
		for (int entityCount = 0; entityCount < Math.min(vortexCount, list.size()); entityCount++) {
			LivingEntity entity = pickRandomEntityFromList(list);
			if (entity != null && !entity.getType().is(Tags.EntityTypes.BOSSES)) {
				if (!(entity instanceof Player)) {
					spawnFishVortex(level, entity);
					list.removeFirst();
					activated = true;
				}
			}
		}
		return activated;
	}

	public static boolean activateUrchinSpikes(Level level, Player player, int urchinCount) {
		List<LivingEntity> list = findNearbyEntities(level, proximityBox(player, 2D, 2D, 2D));
		if (!list.isEmpty()) {
			LivingEntity entity = list.getFirst();
			if (entity != null) {
				if (!(entity instanceof Player)) {
					spawnUrchinSpikes(level, player, urchinCount);
					player.getPersistentData().putLong(NBT_URCHIN_AOE_COOLDOWN, level.getGameTime() + 50);
					return true;
				}
			}
		}
		return false;
	}

	public static boolean activateElectricEntity(Level level, Player player, int electricCount) {
		if (player.getLastHurtByMob() != null) {
			if (!(player.getLastHurtByMob() instanceof Player)) {
				spawnElectricEntity(level, player, player.getLastHurtByMob(), electricCount);
				player.getPersistentData().putLong(NBT_ELECTRIC_COOLDOWN, level.getGameTime() + 50);
				return true;
			}
		}
		return false;
	}

	public static void spawnFishVortex(Level level, LivingEntity entity) {
		FishVortex vortex = new FishVortex(EntityRegistry.FISH_VORTEX.get(), level);
		vortex.setPos(entity.getX(), entity.getY() + 0.25D, entity.getZ());
		level.addFreshEntity(vortex);
		entity.startRiding(vortex, true);
	}

	public static void spawnUrchinSpikes(Level level, Player player, int damage) {
		UrchinSpike urchinSpikes = new UrchinSpike(level, player, damage);
		urchinSpikes.setPos(player.getX(), player.getY() + player.getBbHeight() * 0.5D, player.getZ());
		level.addFreshEntity(urchinSpikes);
		urchinSpikes.shootSpikes();
	}

	public static void spawnElectricEntity(Level level, Player player, LivingEntity entity, int electricCount) {
		ElectricShock electric = new ElectricShock(level, player, entity, electricCount, entity.isInWaterOrRain());
		level.addFreshEntity(electric);
	}

	public static List<LivingEntity> findNearbyEntities(Level level, AABB box) {
		return level.getEntitiesOfClass(LivingEntity.class, box, e -> e instanceof Enemy);
	}

	public static LivingEntity pickRandomEntityFromList(List<LivingEntity> list) {
		Collections.shuffle(list);
		return list.getFirst();
	}

	public static Holder<AmphibiousArmorUpgrade> getUpgrade(EquipmentSlot armorType, ItemStack stack) {
		for (Holder<AmphibiousArmorUpgrade> upgrade : BLRegistries.AMPHIBIOUS_ARMOR_UPGRADES.holders().toList()) {
			if (upgrade.value().matches(armorType, stack)) {
				return upgrade;
			}
		}
		return AmphibiousArmorUpgradeRegistry.NONE;
	}

	public static void updateAttributes(ItemStack stack) {
		if (stack.getItem() instanceof ArmorItem armor) {
			ItemAttributeModifiers oldMods = stack.getAttributeModifiers();
			AmphibiousUpgrades upgrades = stack.getOrDefault(DataComponentRegistry.AMPHIBIOUS_UPGRADES, AmphibiousUpgrades.EMPTY);
			ArrayList<ItemAttributeModifiers.Entry> modifiers = new ArrayList<>(oldMods.modifiers().stream().filter(entry -> !entry.modifier().id().getPath().contains("amphibious")).toList());
			for (var entry : upgrades.getAllUniqueUpgradesWithCounts().object2IntEntrySet()) {
				entry.getKey().value().applyAttributeModifiers(armor.getType(), stack, entry.getIntValue(), modifiers);
			}
			stack.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(modifiers, oldMods.showInTooltip()));
		}
	}
}
