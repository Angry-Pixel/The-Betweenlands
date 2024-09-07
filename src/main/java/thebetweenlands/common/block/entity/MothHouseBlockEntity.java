package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import thebetweenlands.common.block.BLLanternBlock;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;
import java.util.UUID;

public class MothHouseBlockEntity extends NoMenuContainerBlockEntity {

	public static final int SLOT_GRUBS = 0;
	public static final int SLOT_SILK = 1;

	public static final int MAX_GRUBS = 6;
	public static final int MAX_SILK_PER_GRUB = 3;
	public static final int BASE_TICKS_PER_SILK = 160;

	private int productionTime = 0;
	private float productionEfficiency = 0;
	private boolean isWorking = false;
	@Nullable
	private Player placer;
	@Nullable
	private UUID placerUUID;

	private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

	public MothHouseBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.MOTH_HOUSE.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, MothHouseBlockEntity entity) {
		if (level.getGameTime() % 20 == 0) {
			if (entity.isWorking) {
				if (level.isClientSide() && level.getRandom().nextInt(3) == 0) {
					double px = (double) pos.getX() + 0.5D;
					double py = (double) pos.getY() + 0.3D;
					double pz = (double) pos.getZ() + 0.5D;

//					BLParticles.SILK_MOTH.spawn(level, px, py, pz);
				}

				entity.updateEfficiency(level, pos);
			}
		}

		if (!level.isClientSide()) {
			// because the player is always null unless the world is loaded but block NBT is loaded before grrrrr
			if (entity.placerUUID != null && entity.getPlacer() == null && level.getGameTime() % 20 == 0) {
				if (entity.updatePlacerFromUUID(level)) {
					entity.setChanged();
				}
			}

			ItemStack grubs = entity.getItem(SLOT_GRUBS);

			boolean wasWorking = entity.isWorking;

			// don't work if no grubs are available or silk stack is full
			if (grubs == ItemStack.EMPTY || grubs.getCount() == 0 || entity.getItem(SLOT_SILK).getCount() == entity.getItem(SLOT_SILK).getMaxStackSize() || entity.getItem(SLOT_SILK).getCount() >= entity.getItem(SLOT_GRUBS).getCount() * MAX_SILK_PER_GRUB) {
				entity.isWorking = false;
			} else {
				if (!entity.isWorking) {
					entity.updateEfficiency(level, pos);

					entity.resetProductionTime();
				}

				entity.productionTime--;

				entity.isWorking = true;

				if (entity.productionTime <= 0) {
					ItemStack silkStack = entity.getItem(SLOT_SILK);

					if (silkStack == ItemStack.EMPTY) {
						silkStack = new ItemStack(ItemRegistry.SILK_THREAD.get());
						entity.setItem(SLOT_SILK, silkStack);
					} else if (silkStack.is(ItemRegistry.SILK_THREAD.get())) {
						silkStack.grow(1);
					}
					entity.resetProductionTime();
					entity.setChanged();
				}
			}

			if (wasWorking != entity.isWorking) {
				entity.setChanged();
			}
		}
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (this.getLevel() != null) {
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
		}
	}

	@Override
	public void setLevel(Level level) {
		super.setLevel(level);
		this.updatePlacerFromUUID(level);
	}

	private boolean updatePlacerFromUUID(Level level) {
		if (this.placerUUID != null) {
			Player player = level.getPlayerByUUID(this.placerUUID);
			if (player != null && player != this.getPlacer()) {
				this.setPlacer(player);
				return true;
			}
		}
		return false;
	}

	protected void resetProductionTime() {
		float efficiencyMultiplier = 1.0f / (0.1f + this.productionEfficiency * 0.9f);

		// Triple production time with each stage
		this.productionTime = (int) (BASE_TICKS_PER_SILK * Math.pow(3, this.getSilkProductionStage()) * efficiencyMultiplier);
	}

	public int getSilkProductionStage() {
		// Silk stage starts at 0 and increments by 1 whenever all grubs have produced one more silk
		ItemStack grubStack = this.getItem(SLOT_GRUBS);
		ItemStack silkStack = this.getItem(SLOT_SILK);
		return grubStack.getCount() > 0 ? silkStack.getCount() / grubStack.getCount() : 0;
	}

	public boolean isSilkProductionFinished() {
		ItemStack grubStack = this.getItem(SLOT_GRUBS);
		ItemStack silkStack = this.getItem(SLOT_SILK);
		return silkStack.getCount() >= Math.min(MAX_GRUBS, grubStack.getCount()) * MAX_SILK_PER_GRUB;
	}

	public int getSilkRenderStage() {
		ItemStack silkStack = this.getItem(SLOT_SILK);
		return Mth.ceil(Math.min(1.0f, silkStack.getCount() / (float) Math.min(64, MAX_GRUBS * MAX_SILK_PER_GRUB)) * 3);
	}

	private void updateEfficiency(Level level, BlockPos pos) {
		int lanternsNearby = 0;
		int maxLanterns = 3;

		for (BlockPos checkPos : BlockPos.betweenClosedStream(new AABB(pos).inflate(3.0D)).toList()) {
			BlockState state = level.getBlockState(checkPos);
			if (lanternsNearby < maxLanterns && state.getBlock() instanceof BLLanternBlock) {
				if (!level.isClientSide()) {
					lanternsNearby++;

					if (this.getPlacer() instanceof ServerPlayer player && lanternsNearby == maxLanterns) {
						AdvancementCriteriaRegistry.MOTH_HOUSE_MAXED.get().trigger(player);
					}
				} else if (level.getRandom().nextInt(16) == 0) {
					double px = (double) checkPos.getX() + 0.5D;
					double py = (double) checkPos.getY() + 0.7D;
					double pz = (double) checkPos.getZ() + 0.5D;

//					BLParticles.SILK_MOTH.spawn(level, px, py, pz);
				}
			}
			// add other boosters?
		}

		this.productionEfficiency = lanternsNearby / 3.0f;
	}

	public void onSilkRemoved(int count) {
		int grubsToRemove = Mth.ceil(count / (float) MAX_SILK_PER_GRUB);

		ItemStack grubsStack = this.getItem(SLOT_GRUBS);

		grubsStack.shrink(grubsToRemove);

		this.setChanged();
	}

	public void setPlacer(Player player) {
		this.placer = player;
	}

	@Nullable
	private Player getPlacer() {
		return this.placer;
	}

	@Override
	public NonNullList<ItemStack> getItems() {
		return this.items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.items = items;
	}

	@Override
	public int getContainerSize() {
		return 2;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		if(index == SLOT_SILK) {
			ItemStack prevStack = this.getItem(index).copy();

			super.setItem(index, stack);

			ItemStack newStack = this.getItem(index);

			if(newStack.getCount() < prevStack.getCount()) {
				this.onSilkRemoved(prevStack.getCount() - newStack.getCount());
			}
		} else {
			super.setItem(index, stack);
		}
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return slot == SLOT_GRUBS && stack.is(ItemRegistry.SILK_GRUB);
	}

	@Override
	public boolean canTakeItem(Container target, int slot, ItemStack stack) {
		return slot == SLOT_SILK;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ContainerHelper.saveAllItems(tag, this.items, registries);
		if (this.getPlacer() != null) {
			tag.putUUID("owner_uuid", this.getPlacer().getUUID());
		}
		tag.putInt("production_time", this.productionTime);
		tag.putFloat("production_efficiency", this.productionEfficiency);
		tag.putBoolean("working", this.isWorking);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items, registries);
		if (tag.contains("owner_uuid", Tag.TAG_INT_ARRAY)) {
			this.placerUUID = tag.getUUID("owner_uuid");
		}
		this.productionTime = tag.getInt("production_time");
		this.productionEfficiency = tag.getFloat("production_efficiency");
		this.isWorking = tag.getBoolean("working");
	}
}
