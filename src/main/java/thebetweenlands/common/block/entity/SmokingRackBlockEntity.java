package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.recipes.SmokingRackRecipe;
import thebetweenlands.common.block.container.SmokingRackBlock;
import thebetweenlands.common.inventory.SmokingRackMenu;
import thebetweenlands.common.item.misc.MobItem;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class SmokingRackBlockEntity extends BaseContainerBlockEntity {

	private static final int SMOKING_TIME = 200;
	private int smokeProgress;
	private int slot1Progress;
	private int slot2Progress;
	private int slot3Progress;
	private int slot1Total;
	private int slot2Total;
	private int slot3Total;
	private NonNullList<ItemStack> items = NonNullList.withSize(7, ItemStack.EMPTY);
	public final ContainerData data = new ContainerData() {
		public int get(int index) {
			return switch (index) {
				case 0 -> SmokingRackBlockEntity.this.smokeProgress;
				case 1 -> SmokingRackBlockEntity.this.slot1Progress;
				case 2 -> SmokingRackBlockEntity.this.slot2Progress;
				case 3 -> SmokingRackBlockEntity.this.slot3Progress;
				case 4 -> SmokingRackBlockEntity.this.slot1Total;
				case 5 -> SmokingRackBlockEntity.this.slot2Total;
				case 6 -> SmokingRackBlockEntity.this.slot3Total;
				default -> 0;
			};
		}

		public void set(int index, int value) {
			switch (index) {
				case 0 -> SmokingRackBlockEntity.this.smokeProgress = value;
				case 1 -> SmokingRackBlockEntity.this.slot1Progress = value;
				case 2 -> SmokingRackBlockEntity.this.slot2Progress = value;
				case 3 -> SmokingRackBlockEntity.this.slot3Progress = value;
				case 4 -> SmokingRackBlockEntity.this.slot1Total = value;
				case 5 -> SmokingRackBlockEntity.this.slot2Total = value;
				case 6 -> SmokingRackBlockEntity.this.slot3Total = value;
			}
		}

		public int getCount() {
			return 7;
		}
	};
	private final List<RecipeManager.CachedCheck<SingleRecipeInput, SmokingRackRecipe>> quickChecks = List.of(
		RecipeManager.createCheck(RecipeRegistry.SMOKING_RECIPE.get()),
		RecipeManager.createCheck(RecipeRegistry.SMOKING_RECIPE.get()),
		RecipeManager.createCheck(RecipeRegistry.SMOKING_RECIPE.get()));

	public SmokingRackBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.SMOKING_RACK.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, SmokingRackBlockEntity entity) {
		if (!level.isClientSide()) {
			boolean setChanged = false;
			if (state.getValue(SmokingRackBlock.HEATED)) {
				if (entity.smokeProgress > 0) {
					entity.smokeProgress--;
				}
				boolean setSmokingAlready = false;

				for (int i = 1; i <= 3; i++) {
					SingleRecipeInput input = new SingleRecipeInput(entity.getItem(i));
					RecipeHolder<SmokingRackRecipe> recipe = entity.quickChecks.get(i - 1).getRecipeFor(input, level).orElse(null);

					if (!setSmokingAlready && entity.smokeProgress <= 0 && entity.canSmokeItem(level, i, recipe)) {
						entity.smokeProgress = SMOKING_TIME;
						entity.getItem(0).shrink(1);
						setSmokingAlready = true;
						setChanged = true;
					}

					if (entity.smokeProgress > 0 && entity.canSmokeItem(level, i, recipe)) {
						entity.data.set(i, entity.data.get(i) + 1);
						if (entity.data.get(i) == entity.data.get(i + 3)) {
							entity.data.set(i, 0);
							entity.data.set(i + 3, entity.getTotalSmokeTime(level, i));
							entity.smokeItem(level, i, recipe);
							setChanged = true;
						}
					} else {
						entity.data.set(i, 0);
					}
				}
			} else {
				for (int i = 1; i <= 3; i++) {
					if (entity.data.get(i) > 0) {
						entity.data.set(i, Mth.clamp(entity.data.get(i) - 2, 0, entity.data.get(i + 3)));
					}
				}
			}

			if (setChanged) {
				entity.setChanged();
			}
		}
	}

	private boolean canSmokeItem(Level level, int index, @Nullable RecipeHolder<SmokingRackRecipe> recipe) {
		if (!this.getItem(index).isEmpty() && recipe != null) {
			ItemStack itemstack = recipe.value().assemble(new SingleRecipeInput(this.getItem(index)), level.registryAccess());
			if (itemstack.isEmpty()) {
				return false;
			} else {
				ItemStack itemstack1 = this.getItem(index + 3);
				if (itemstack1.isEmpty()) {
					return true;
				} else if (!ItemStack.isSameItemSameComponents(itemstack1, itemstack)) {
					return false;
				} else {
					return itemstack1.getCount() + itemstack.getCount() <= this.getMaxStackSize() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize() || itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize();
				}
			}
		} else {
			return false;
		}
	}

	private boolean smokeItem(Level level, int index, @Nullable RecipeHolder<SmokingRackRecipe> recipe) {
		if (recipe != null && this.canSmokeItem(level, index, recipe)) {
			ItemStack itemstack = this.getItem(index);
			ItemStack itemstack1 = recipe.value().assemble(new SingleRecipeInput(this.getItem(index)), level.registryAccess());
			ItemStack itemstack2 = this.getItem(index + 3);
			if (itemstack2.isEmpty()) {
				this.setItem(index + 3, itemstack1.copy());
			} else if (ItemStack.isSameItemSameComponents(itemstack2, itemstack1)) {
				itemstack2.grow(itemstack1.getCount());
			}

			itemstack.shrink(1);
			return true;
		} else {
			return false;
		}
	}

	private int getTotalSmokeTime(Level level, int index) {
		SingleRecipeInput input = new SingleRecipeInput(this.getItem(index));
		return this.quickChecks.get(index - 1).getRecipeFor(input, level).map(recipe -> recipe.value().smokingTime()).orElse(200);
	}

	@Nullable
	public Entity getRenderEntity(Level level, int slot) {
		ItemStack stack = this.getItems().get(slot);
		if(!stack.isEmpty() && stack.getItem() instanceof MobItem<?> mob && !mob.getEntityData(stack).isEmpty()) {
			return mob.createCapturedEntity(level, 0, 0, 0, stack, false);
		}
		return null;
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.thebetweenlands.smoking_rack");
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.items = items;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		ItemStack itemstack = this.items.get(slot);
		boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameComponents(itemstack, stack);
		this.items.set(slot, stack);
		stack.limitSize(this.getMaxStackSize(stack));
		if (slot > 0 && slot < 4 && !flag) {
			this.data.set(slot + 3, this.getTotalSmokeTime(this.getLevel(), slot));
			this.data.set(slot, 0);
			this.setChanged();
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
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return new SmokingRackMenu(containerId, inventory, this, this.data);
	}

	@Override
	public int getContainerSize() {
		return 7;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ContainerHelper.saveAllItems(tag, this.items, registries);
		tag.putInt("smoke_progress", this.smokeProgress);
		tag.putInt("slot_1_progress", this.slot1Progress);
		tag.putInt("slot_2_progress", this.slot2Progress);
		tag.putInt("slot_3_progress", this.slot3Progress);
		tag.putInt("slot_1_total", this.slot1Total);
		tag.putInt("slot_2_total", this.slot2Total);
		tag.putInt("slot_3_total", this.slot3Total);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items, registries);
		this.smokeProgress = tag.getInt("smoke_progress");
		this.slot1Progress = tag.getInt("slot_1_progress");
		this.slot2Progress = tag.getInt("slot_2_progress");
		this.slot3Progress = tag.getInt("slot_3_progress");
		this.slot1Total = tag.getInt("slot_1_total");
		this.slot2Total = tag.getInt("slot_2_total");
		this.slot3Total = tag.getInt("slot_3_total");
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return this.saveCustomOnly(registries);
	}
}
