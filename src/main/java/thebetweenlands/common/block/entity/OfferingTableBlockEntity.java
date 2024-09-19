package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.ticks.ContainerSingleItem;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.*;

public class OfferingTableBlockEntity extends SyncedBlockEntity implements ContainerSingleItem.BlockContainerSingleItem {

	private final Map<UUID, Integer> teleportTicks = new WeakHashMap<>();
	private ItemStack item = ItemStack.EMPTY;

	public OfferingTableBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.OFFERING_TABLE.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, OfferingTableBlockEntity entity) {
		ItemStack stack = entity.getTheItem();
		if (!stack.isEmpty() && stack.is(ItemRegistry.BONE_WAYFINDER)) {
			double radius = 2.5D;

			AABB aabb = new AABB(pos).inflate(radius);

			Set<Player> teleportingPlayers = null;
			for (Player player : level.getEntitiesOfClass(Player.class, aabb, p -> p.isShiftKeyDown() && p.distanceToSqr(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f) <= radius * radius)) {
				if (teleportingPlayers == null) {
					teleportingPlayers = new HashSet<>();
				}
				teleportingPlayers.add(player);

				int ticks = entity.teleportTicks.getOrDefault(player.getUUID(), 0);

				if (ticks >= 0 && !entity.updateTeleport(player, level, pos, ticks, stack)) {
					entity.teleportTicks.put(player.getUUID(), -100);
				} else {
					entity.teleportTicks.put(player.getUUID(), ticks + 1);
				}

				entity.setTheItem(stack);
			}

			if (teleportingPlayers == null) {
				entity.teleportTicks.clear();
			} else if (!entity.teleportTicks.isEmpty()) {
				Iterator<UUID> it = entity.teleportTicks.keySet().iterator();
				while (it.hasNext()) {
					Player player = level.getPlayerByUUID(it.next());
					if (player != null && !teleportingPlayers.contains(player)) {
						it.remove();
					}
				}
			}
		} else {
			entity.teleportTicks.clear();
		}
	}

	private boolean updateTeleport(Player player, Level level, BlockPos pos, int ticks, ItemStack stack) {
		if (ticks >= 100) {
			if (!level.isClientSide() && stack.getDamageValue() < stack.getMaxDamage()) {
//				BlockPos waystone = ((BoneWayfinderItem) stack.getItem()).getBoundWaystone(stack);
//				if (waystone != null) {
//					BlockPos spawnPoint = PlayerRespawnHandler.getSpawnPointNearPos(level, waystone, 8, false, 4, 0);
//
//					if (spawnPoint != null) {
//						if (player.distanceToSqr(Vec3.atCenterOf(spawnPoint)) > 24) {
//							this.playThunderSounds(level, player.blockPosition());
//						}
//
//						PlayerUtil.teleport(player, spawnPoint.getX() + 0.5D, spawnPoint.getY(), spawnPoint.getZ() + 0.5D);
//
//						this.playThunderSounds(level, player.blockPosition());
//
//						player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 1));
//
//						stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
//					} else if (player instanceof ServerPlayer) {
//						player.displayClientMessage(Component.translatable("chat.waystone.obstructed"), true);
//					}
//				}
			}
		} else {
			if (stack.getDamageValue() < stack.getMaxDamage()) {
				if (!level.isClientSide()) {
					if (ticks >= 40) {
						int count = ticks - 40;

						if (player.hurtTime > 0) {
							level.playSound(null, player.blockPosition(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1, 1);
						}

						if (player instanceof Player && !player.isCreative() && count < 60 && player.tickCount % 3 == 0) {
//							int removed = RingItem.removeXp(player, 1);
//							if (removed == 0) {
//								level.playSound(null, player.blockPosition(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1, 1);
//								return false;
//							}
						}
					}

					if (ticks >= 15 && ticks < 90 && (ticks - 15) % 20 == 0) {
						level.playSound(null, player.blockPosition(), SoundRegistry.PORTAL_TRAVEL.get(), SoundSource.PLAYERS, 0.05F + 0.4F * (float) Mth.clamp(80 - ticks, 1, 80) / 80.0F, 0.9F + level.getRandom().nextFloat() * 0.2F);
					}
				} else {
					if (ticks >= 15 && ticks % 4 == 0) {
						this.spawnChargingParticles(level, level.getGameTime() * 0.035f, pos.getX() + 0.5f, pos.getY() + 0.4f, pos.getZ() + 0.5f);
					}

					RandomSource random = level.getRandom();
					for (int i = 0; i < Mth.clamp(60 - ticks, 1, 60); i++) {
						level.addParticle(ParticleTypes.ASH, player.getX() + (random.nextBoolean() ? -1 : 1) * Math.pow(random.nextFloat(), 2) * 6, player.getY() + random.nextFloat() * 4 - 2, player.getZ() + (random.nextBoolean() ? -1 : 1) * Math.pow(random.nextFloat(), 2) * 6, 0, 0.2D, 0);
					}
				}
			} else if (!level.isClientSide()) {
				level.playSound(null, player.blockPosition(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1, 1);
				return false;
			}
		}

		return true;
	}

	private void playThunderSounds(Level level, BlockPos pos) {
		level.playSound(null, pos, SoundRegistry.RIFT_CREAK.get(), SoundSource.PLAYERS, 2, 1);
		level.playSound(null, pos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.75F, 0.75F);
	}

	private void spawnChargingParticles(Level level, float rot, float x, float y, float z) {
		float step = (float) Math.PI * 2 / 20;
		for (int i = 0; i < 20; i++) {
			float dx = (float) Math.cos(rot + step * i);
			float dz = (float) Math.sin(rot + step * i);

//			BLParticles.CORRUPTED.spawn(level, x, y, z, ParticleArgs.get().withMotion(dx * 0.075f, 0.15f, dz * 0.075f).withData(80, true, 0.1f, true));
		}
	}

	@Override
	public BlockEntity getContainerBlockEntity() {
		return this;
	}

	@Override
	public ItemStack getTheItem() {
		return this.item;
	}

	@Override
	public void setTheItem(ItemStack item) {
		this.item = item;
		this.setChanged();
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if (!this.getTheItem().isEmpty()) {
			tag.put("item", this.getTheItem().save(registries));
		}
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		if (tag.contains("item", Tag.TAG_COMPOUND)) {
			this.item = ItemStack.parse(registries, tag.getCompound("item")).orElse(ItemStack.EMPTY);
		} else {
			this.item = ItemStack.EMPTY;
		}
	}
}
