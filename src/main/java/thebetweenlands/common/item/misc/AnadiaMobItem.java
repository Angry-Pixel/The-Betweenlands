package thebetweenlands.common.item.misc;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.component.entity.RotSmellData;
import thebetweenlands.common.entity.fishing.anadia.Anadia;
import thebetweenlands.common.entity.monster.SwampHag;
import thebetweenlands.common.entity.Seat;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.List;

public class AnadiaMobItem extends MobItem<Anadia> {
	public AnadiaMobItem(Properties properties) {
		super(properties, 5.0D, EntityRegistry.ANADIA.get(), null);
	}

	public int getDecayTime(ItemStack stack) {
		return 24000; // 20 minutes
	}

	public ItemStack getItemFromEntity(String key, ItemStack stack, Level level) {
		if (!this.isRotten(level, stack) && !this.getEntityData(stack).isEmpty()) {
			CompoundTag tag = this.getEntityData(stack);
			if (tag.contains(key, CompoundTag.TAG_COMPOUND) && !tag.getCompound(key).isEmpty()) {
				return ItemStack.parse(level.registryAccess(), tag.getCompound(key)).orElse(new ItemStack(ItemRegistry.ANADIA_REMAINS.get()));
			}
		}
		return new ItemStack(ItemRegistry.ANADIA_REMAINS.get());
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (!attacker.level().isClientSide()) {
			if (target instanceof SwampHag && !target.isPassenger() && attacker instanceof Player) {
				if (attacker.level().getBlockState(target.blockPosition().below()).blocksMotion()) {
					Seat entitySeat = new Seat(attacker.level(), true);
					entitySeat.setPos(target.getX(), target.getY() - 0.55D, target.getZ());
					attacker.level().addFreshEntity(entitySeat);
					target.startRiding(entitySeat, true);
				}
			}
		}
		return false;
	}

	@Override
	protected InteractionResult spawnCapturedEntity(Player player, Level level, InteractionHand hand, Direction facing, Vec3 hitVec, Entity entity, boolean isNewEntity) {
		if (entity instanceof Anadia anadia && !anadia.getFishColor().isAlive()) {
			return InteractionResult.PASS;
		}
		return super.spawnCapturedEntity(player, level, hand, facing, hitVec, entity, isNewEntity);
	}

	public void setRotten(Level level, ItemStack stack, boolean rotten) {
		if (!this.getEntityData(stack).isEmpty()) {
			if (this.getEntityData(stack).getByte("fish_color") != 0) {
				if (rotten) {
					stack.set(DataComponentRegistry.ROT_TIME, level.getGameTime());
				} else {
					stack.set(DataComponentRegistry.ROT_TIME, level.getGameTime() + this.getDecayTime(stack));
				}
			}
		}
	}

	public boolean isRotten(Level level, ItemStack stack) {
		if (!this.getEntityData(stack).isEmpty()) {
			if (this.getEntityData(stack).getByte("fish_color") != 0) {
				if (stack.has(DataComponentRegistry.ROT_TIME)) {
					return stack.getOrDefault(DataComponentRegistry.ROT_TIME, 0L) - level.getGameTime() <= 0;
				}
			}
		}
		return false;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		if (!this.getEntityData(stack).isEmpty()) {
			byte color = this.getEntityData(stack).getByte("fish_color");
			if (color != 0 && color != 1)
				if (stack.has(DataComponentRegistry.ROT_TIME))
					if (level.getGameTime() >= stack.getOrDefault(DataComponentRegistry.ROT_TIME, 0L))
						CustomData.update(DataComponents.ENTITY_DATA, stack, tag -> tag.putByte("fish_color", (byte) 1));
		}
		if (this.isRotten(level, stack) && entity instanceof Player player) {
			if (!level.isClientSide()) {
				RotSmellData cap = player.getData(AttachmentRegistry.ROT_SMELL);
				if (!cap.isSmellingBad(player)) {
					cap.setSmellingBad(player, Math.max(cap.getRemainingSmellyTicks(player), 24000));
				}
			}
		}
	}

	@Override
	public void onCapturedByPlayer(Player player, InteractionHand hand, ItemStack captured, LivingEntity entity) {
		if (!player.level().isClientSide()) {
			if (captured.get(DataComponents.ENTITY_DATA) != null)
				captured.set(DataComponentRegistry.ROT_TIME, player.level().getGameTime() + this.getDecayTime(captured));
			if (entity instanceof Anadia anadia && anadia.isVehicle()) {
				anadia.ejectPassengers();
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		CompoundTag entityNbt = this.getEntityData(stack);

		if (!entityNbt.isEmpty()) {
			tooltip.add(Anadia.createName(entityNbt.getByte("head_type"), entityNbt.getByte("body_type"), entityNbt.getByte("tail_type")).copy().withStyle(ChatFormatting.GRAY));

			if (entityNbt.getByte("fish_color") != 0) {
				Level level = context.level();
				if (stack.has(DataComponentRegistry.ROT_TIME) && level != null) {
					long rottingTime = stack.getOrDefault(DataComponentRegistry.ROT_TIME, 0L);
					if (rottingTime - level.getGameTime() > 19200)
						tooltip.add(Component.translatable("item.thebetweenlands.anadia.status").append(Component.translatable("item.thebetweenlands.anadia.rotting_1")).withStyle(ChatFormatting.GRAY));
					else if (rottingTime - level.getGameTime() <= 19200 && rottingTime - level.getGameTime() > 14400)
						tooltip.add(Component.translatable("item.thebetweenlands.anadia.status").append(Component.translatable("item.thebetweenlands.anadia.rotting_2")).withStyle(ChatFormatting.GRAY));
					else if (rottingTime - level.getGameTime() <= 14400 && rottingTime - level.getGameTime() > 9600)
						tooltip.add(Component.translatable("item.thebetweenlands.anadia.status").append(Component.translatable("item.thebetweenlands.anadia.rotting_3")).withStyle(ChatFormatting.GRAY));
					else if (rottingTime - level.getGameTime() <= 9600 && rottingTime - level.getGameTime() > 4800)
						tooltip.add(Component.translatable("item.thebetweenlands.anadia.status").append(Component.translatable("item.thebetweenlands.anadia.rotting_4")).withStyle(ChatFormatting.GRAY));
					else if (rottingTime - level.getGameTime() <= 4800 && rottingTime - level.getGameTime() > 0)
						tooltip.add(Component.translatable("item.thebetweenlands.anadia.status").append(Component.translatable("item.thebetweenlands.anadia.rotting_5")).withStyle(ChatFormatting.GRAY));
					else if (rottingTime - level.getGameTime() <= 0)
						tooltip.add(Component.translatable("item.thebetweenlands.anadia.status").append(Component.translatable("item.thebetweenlands.anadia.rotten")).withStyle(ChatFormatting.GRAY));
				}
			} else {
				tooltip.add(Component.translatable("item.thebetweenlands.anadia.status").append(Component.translatable("item.thebetweenlands.anadia.smoked")).withStyle(ChatFormatting.GRAY));
			}

			tooltip.add(Component.translatable("item.thebetweenlands.anadia.health", Mth.ceil(this.getMobHealth(stack)), Mth.ceil(this.getMobAttribute(Attributes.MAX_HEALTH, 5.0D, entityNbt) + entityNbt.getFloat(Anadia.HEALTH_MOD))).withStyle(ChatFormatting.GRAY));
			tooltip.add(Component.translatable("item.thebetweenlands.anadia.size", entityNbt.getFloat("fish_size")).withStyle(ChatFormatting.GRAY));
			tooltip.add(Component.translatable("item.thebetweenlands.anadia.speed", this.getMobAttribute(Attributes.MOVEMENT_SPEED, 0.2D, entityNbt) + entityNbt.getFloat(Anadia.SPEED_MOD)).withStyle(ChatFormatting.GRAY));
			tooltip.add(Component.translatable("item.thebetweenlands.anadia.strength", entityNbt.getFloat(Anadia.STRENGTH_MOD)).withStyle(ChatFormatting.GRAY));
			tooltip.add(Component.translatable("item.thebetweenlands.anadia.stamina", entityNbt.getFloat(Anadia.STAMINA_MOD)).withStyle(ChatFormatting.GRAY));
		} else {
			super.appendHoverText(stack, context, tooltip, flag);
		}
	}
}
