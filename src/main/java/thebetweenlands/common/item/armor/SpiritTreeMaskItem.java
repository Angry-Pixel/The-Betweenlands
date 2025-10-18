package thebetweenlands.common.item.armor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.client.renderer.entity.SpiritTreeFaceMaskRenderer;
import thebetweenlands.common.entity.SpiritTreeFaceMask;
import thebetweenlands.common.registries.ArmorMaterialRegistry;

public class SpiritTreeMaskItem extends ArmorItem {

	private final SpiritTreeFaceMask.MaskType type;

	public SpiritTreeMaskItem(SpiritTreeFaceMask.MaskType type, Properties properties) {
		super(ArmorMaterialRegistry.SPIRIT_TREE_MASK, Type.HELMET, properties);
		this.type = type;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		BlockPos blockpos = context.getClickedPos();
		Direction direction = context.getClickedFace();
		BlockPos blockpos1 = blockpos.relative(direction);
		Player player = context.getPlayer();
		ItemStack itemstack = context.getItemInHand();
		if (player != null && !this.mayPlace(player, direction, itemstack, blockpos1)) {
			return InteractionResult.FAIL;
		} else {
			Level level = context.getLevel();
			SpiritTreeFaceMask mask = new SpiritTreeFaceMask(level, blockpos1, direction, this.type);

			CustomData customdata = itemstack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
			if (!customdata.isEmpty()) {
				EntityType.updateCustomEntityTag(level, player, mask, customdata);
			}

			if (mask.survives()) {
				if (!level.isClientSide()) {
					mask.playPlacementSound();
					level.gameEvent(player, GameEvent.ENTITY_PLACE, mask.position());
					level.addFreshEntity(mask);
				}

				itemstack.consume(1, player);
				return InteractionResult.sidedSuccess(level.isClientSide());
			} else {
				return InteractionResult.CONSUME;
			}
		}
	}

	protected boolean mayPlace(Player player, Direction direction, ItemStack hangingEntityStack, BlockPos pos) {
		return !direction.getAxis().isVertical() && player.mayUseItemAt(pos, direction, hangingEntityStack);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		super.inventoryTick(stack, level, entity, slotId, isSelected);
		if (entity instanceof Player player && slotId > Inventory.INVENTORY_SIZE && slotId < Inventory.SLOT_OFFHAND) {
			if (this.type == SpiritTreeFaceMask.MaskType.LARGE) {
				player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 0, true, false, false));
			}
		}
	}

	@Override
	public @Nullable ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
		return this.type == SpiritTreeFaceMask.MaskType.LARGE ? SpiritTreeFaceMaskRenderer.TEXTURE_LARGE : SpiritTreeFaceMaskRenderer.TEXTURE_SMALL;
	}
}
