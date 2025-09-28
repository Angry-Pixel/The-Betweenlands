package thebetweenlands.common.item.equipment;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.item.RadialMenuEquippable;
import thebetweenlands.client.BetweenlandsKeybinds;
import thebetweenlands.common.component.entity.CircleGemData;
import thebetweenlands.common.component.entity.PuppetData;
import thebetweenlands.common.component.entity.circlegem.CircleGem;
import thebetweenlands.common.component.entity.circlegem.CircleGemType;
import thebetweenlands.common.component.entity.equipment.EquipmentHelper;
import thebetweenlands.common.component.entity.equipment.EquipmentInventoryType;
import thebetweenlands.common.component.item.AmuletData;
import thebetweenlands.common.entity.creature.Emberling;
import thebetweenlands.common.entity.creature.Tarminion;
import thebetweenlands.common.entity.monster.chiromaw.TameChiromaw;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AmuletItem extends Item implements RadialMenuEquippable {

	public final CircleGemType type;

	public static final Set<Class<? extends LivingEntity>> SUPPORTED_ENTITIES = new HashSet<>();

	static {
		SUPPORTED_ENTITIES.add(Tarminion.class);
		//SUPPORTED_ENTITIES.add(EntityGiantToad.class);
		//SUPPORTED_ENTITIES.add(EntityTamedSpiritTreeFace.class);
		SUPPORTED_ENTITIES.add(Emberling.class);
		SUPPORTED_ENTITIES.add(TameChiromaw.class);
	}

	public AmuletItem(CircleGemType type, Properties properties) {
		super(properties.component(DataComponentRegistry.CIRCLE_GEM, type));
		this.type = type;
	}


	public static boolean canPlayerAddAmulet(Player player, Entity target) {
		PuppetData cap = target.getData(AttachmentRegistry.PUPPET);
		return SUPPORTED_ENTITIES.contains(target.getClass()) || cap.hasPuppeteer() && cap.getPuppeteer(target) == player;
	}

	public static boolean addAmulet(Holder<Item> amulet, Entity entity, boolean canUnequip, boolean canDrop) {
		ItemStack stack = new ItemStack(amulet);

		stack.set(DataComponentRegistry.AMULET_DATA, new AmuletData(canUnequip, canDrop));

		ItemStack result = EquipmentHelper.equipItem(null, entity, stack, false);

		return result.isEmpty() || result.getCount() != stack.getCount();

	}

	@Override
	public EquipmentInventoryType getEquipmentCategory(ItemStack stack) {
		return EquipmentInventoryType.AMULET;
	}

	@Override
	public boolean canEquipOnRightClick(ItemStack stack, Player player, Entity target) {
		return true;
	}

	@Override
	public boolean canEquip(ItemStack stack, @Nullable Player player, Entity target) {
		if (this.type == CircleGemType.NONE) {
			return false;
		}

		return target instanceof Player || player == null || canPlayerAddAmulet(player, target);
	}

	@Override
	public boolean canUnequip(ItemStack stack, @Nullable Player player, Entity target, Container inventory) {
		return target == player || !stack.has(DataComponentRegistry.AMULET_DATA) || stack.get(DataComponentRegistry.AMULET_DATA).canUnequip();
	}

	@Override
	public boolean canDrop(ItemStack stack, Entity entity, Container inventory) {
		return !stack.has(DataComponentRegistry.AMULET_DATA) || stack.get(DataComponentRegistry.AMULET_DATA).canDrop();
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, Container inventory) {
		CircleGemData data = entity.getData(AttachmentRegistry.CIRCLE_GEM);
		data.addGem(new CircleGem(this.type, CircleGem.CombatType.BOTH));
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, Container inventory) {
		CircleGemData data = entity.getData(AttachmentRegistry.CIRCLE_GEM);
		List<CircleGem> gems = data.getGems();

		for (CircleGem gem : gems) {
			if (gem.combatType() == CircleGem.CombatType.BOTH && gem.gemType() == this.type) {
				data.removeGem(gem);
				break;
			}
		}
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity, Container inventory) {

	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> component, TooltipFlag flag) {
		component.add(Component.empty());
		component.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
		if(this.type != CircleGemType.NONE) {
			if (flag.hasShiftDown()) {
				component.add(Component.translatable("item.thebetweenlands.amulet.usage", BetweenlandsKeybinds.RADIAL_MENU.getKey().getDisplayName(), Minecraft.getInstance().options.keyUse.getKey().getDisplayName()).withStyle(ChatFormatting.GRAY));
			} else {
				component.add(Component.translatable("item.thebetweenlands.hold_shift").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
			}
		}
	}
}
