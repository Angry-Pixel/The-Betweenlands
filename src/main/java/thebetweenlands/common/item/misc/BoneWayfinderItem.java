package thebetweenlands.common.item.misc;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.block.terrain.WaystoneBlock;
import thebetweenlands.common.handler.PlayerRespawnHandler;
import thebetweenlands.common.item.UnbreakableItem;
import thebetweenlands.common.item.equipment.RingItem;
import thebetweenlands.common.network.clientbound.OpenRenameScreenPacket;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

import javax.annotation.Nullable;
import java.util.List;

public class BoneWayfinderItem extends HoverTextItem implements UnbreakableItem {
	public BoneWayfinderItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return this.getBoundWaystone(stack) != null || super.isFoil(stack);
	}

	@Override
	public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		return false;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		if (this.getBoundWaystone(stack) == null && !UnbreakableItem.isStackBroken(stack)) {
			BlockState state = context.getLevel().getBlockState(context.getClickedPos());
			if (state.is(BlockRegistry.WAYSTONE) && this.activateWaystone(context.getLevel(), context.getClickedPos(), state, stack)) {
				if (!context.getLevel().isClientSide()) {
					this.setBoundWaystone(stack, context.getClickedPos());
				}
				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.isSecondaryUseActive() && hand == InteractionHand.MAIN_HAND) {
			if (level.isClientSide()) {
				return InteractionResultHolder.success(stack);
			} else {
				PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenRenameScreenPacket(stack));
				return InteractionResultHolder.consume(stack);
			}
		} else {
			if (!UnbreakableItem.isStackBroken(stack) && this.getBoundWaystone(stack) != null) {
				player.startUsingItem(hand);
				return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
			}
		}

		return InteractionResultHolder.pass(stack);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (!level.isClientSide() && !UnbreakableItem.isStackBroken(stack)) {
			BlockPos waystone = this.getBoundWaystone(stack);
			if (waystone != null) {
				BlockPos spawnPoint = PlayerRespawnHandler.getSpawnPointNearPos((ServerLevel) level, waystone, 8, false, 4, 0, true);

				if (spawnPoint != null) {
					if (entity.distanceToSqr(spawnPoint.getBottomCenter()) > 24) {
						this.playThunderSounds(level, entity.getX(), entity.getY(), entity.getZ());
					}

					entity.teleportTo(spawnPoint.getX() + 0.5D, spawnPoint.getY(), spawnPoint.getZ() + 0.5D);
					entity.resetFallDistance();

					this.playThunderSounds(level, entity.getX(), entity.getY(), entity.getZ());

					entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 1));

					UnbreakableItem.hurtButDontBreak(stack, 1, entity);
				} else if (entity instanceof Player player) {
					player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".obstructed"), true);
				}
			}
		}
		return stack;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 100;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		if (stack.has(DataComponentRegistry.WAYFINDER_LINK)) {
			tooltip.add(Component.translatable(this.getDescriptionId() + ".linked", stack.get(DataComponentRegistry.WAYFINDER_LINK).toShortString()).withStyle(ChatFormatting.GRAY));
		} else {
			super.appendHoverText(stack, context, tooltip, flag);
		}
	}

	@Override
	public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
		if (!level.isClientSide()) {
			if (remainingUseDuration < 80 && !level.canSeeSky(entity.blockPosition())) {
				if (entity instanceof ServerPlayer player) {
					player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".sky_obstructed"), true);
				}
				entity.stopUsingItem();
				entity.playSound(SoundEvents.FLINTANDSTEEL_USE);
				return;
			}

			if (entity.hurtTime > 0) {
				entity.stopUsingItem();
				entity.playSound(SoundEvents.FLINTANDSTEEL_USE);
			}

			if (entity instanceof Player player && !player.isCreative() && remainingUseDuration < 60 && entity.tickCount % 3 == 0) {
				int removed = RingItem.removeXp(player, 1);
				if (removed == 0) {
					entity.stopUsingItem();
					entity.playSound(SoundEvents.FLINTANDSTEEL_USE);
				}
			}

			if (remainingUseDuration < 90 && remainingUseDuration % 20 == 0) {
				entity.playSound(SoundRegistry.PORTAL_TRAVEL.get(), 0.05F + 0.4F * (float) Mth.clamp(80 - remainingUseDuration, 1, 80) / 80.0F, 0.9F + level.getRandom().nextFloat() * 0.2F);
			}
		} else {
			RandomSource rand = level.getRandom();
			for (int i = 0; i < Mth.clamp(60 - remainingUseDuration, 1, 60); i++) {
				level.addParticle(ParticleTypes.ASH, entity.getX() + (rand.nextBoolean() ? -1 : 1) * Math.pow(rand.nextFloat(), 2) * 6, entity.getY() + rand.nextFloat() * 4 - 2, entity.getZ() + (rand.nextBoolean() ? -1 : 1) * Math.pow(rand.nextFloat(), 2) * 6, 0, 0.2D, 0);
			}
		}
	}

	protected void playThunderSounds(Level level, double x, double y, double z) {
		level.playSound(null, x, y, z, SoundRegistry.RIFT_CREAK, SoundSource.PLAYERS, 2, 1);
		level.playSound(null, x, y, z, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.75F, 0.75F);
	}

	protected boolean activateWaystone(Level level, BlockPos pos, BlockState state, ItemStack stack) {
		WaystoneBlock block = (WaystoneBlock) state.getBlock();
		if (block.isValidWaystone(level, pos, state)) {
			WaystoneBlock.Part part = state.getValue(WaystoneBlock.PART);

			if (!level.isClientSide()) {
				int startY = part == WaystoneBlock.Part.BOTTOM ? 0 : (part == WaystoneBlock.Part.MIDDLE ? -1 : -2);
				for (int yo = startY; yo < startY + 3; yo++) {
					BlockState newState = level.getBlockState(pos.above(yo)).setValue(WaystoneBlock.ACTIVE, true);
					level.setBlockAndUpdate(pos.above(yo), newState);
					level.sendBlockUpdated(pos.above(yo), newState, newState, 2); //why tf is this necessary
				}

				this.playThunderSounds(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

				var storage = BetweenlandsWorldStorage.getNullable(level);
				if (storage != null) {
					List<LocationStorage> waystoneLocations = storage.getLocalStorageHandler()
						.getLocalStorages(LocationStorage.class, new AABB(pos.getX(), pos.getY() + startY, pos.getZ(), pos.getX() + 1, pos.getY() + startY + 3, pos.getZ() + 1), loc -> loc.getType() == EnumLocationType.WAYSTONE);
					if (!waystoneLocations.isEmpty()) {
						LocationStorage location = waystoneLocations.getFirst();

						if (stack.has(DataComponents.CUSTOM_NAME)) {
							location.setName(stack.getHoverName().getString());
							location.setVisible(true);
							location.markDirty();
						} else {
							location.setName("waystone");
							location.setVisible(false);
							location.markDirty();
						}
					}
				}
			} else {
				this.spawnWaystoneParticles(level, pos, part);
			}

			return true;
		}
		return false;
	}

	protected void spawnWaystoneParticles(Level level, BlockPos pos, WaystoneBlock.Part part) {
		int startY = part == WaystoneBlock.Part.BOTTOM ? 0 : (part == WaystoneBlock.Part.MIDDLE ? -1 : -2);
		for (int yo = startY; yo < startY + 3; yo++) {
			for (int i = 0; i < 4; i++) {
				Vec3 dir = new Vec3(level.getRandom().nextFloat() - 0.5F, level.getRandom().nextFloat() - 0.5F + 0.25F, level.getRandom().nextFloat() - 0.5F);
				dir = dir.normalize().scale(2);
				level.addParticle(ParticleRegistry.CORRUPTED.get(), pos.getX() + 0.5D + level.getRandom().nextFloat() / 2.0F - 0.25F, pos.getY() + yo + 0.5D + level.getRandom().nextFloat() / 2.0F - 0.25F, pos.getZ() + 0.5D + level.getRandom().nextFloat() / 2.0F - 0.25F, dir.x, dir.y, dir.z);
			}
		}
	}

	@Nullable
	public BlockPos getBoundWaystone(ItemStack stack) {
		if (stack.has(DataComponentRegistry.WAYFINDER_LINK)) {
			return stack.get(DataComponentRegistry.WAYFINDER_LINK);
		}
		return null;
	}

	public void setBoundWaystone(ItemStack stack, @Nullable BlockPos pos) {
		if (pos == null) {
			stack.remove(DataComponentRegistry.WAYFINDER_LINK);
		} else {
			stack.set(DataComponentRegistry.WAYFINDER_LINK, pos);
		}
	}
}
