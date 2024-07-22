package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.SmokingRackBlock;
import thebetweenlands.common.items.AnadiaMobItem;
import thebetweenlands.common.items.MobItem;
import thebetweenlands.common.items.recipe.SmokingRackRecipe;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

import javax.annotation.Nullable;
import java.util.Optional;

public class SmokingRackBlockEntity extends BaseContainerBlockEntity {

	public static final int MAX_SMOKING_TIME = 200; // 10 seconds per moss for a 64 stack = over 10.6 min IRL
	public int curingModifier1 = 1;
	public int curingModifier2 = 1;
	public int curingModifier3 = 1;
	public int smokeProgress = 0;
	public int slot1Progress = 0;
	public int slot2Progress = 0;
	public int slot3Progress = 0;
	private NonNullList<ItemStack> items = NonNullList.withSize(7, ItemStack.EMPTY);

	public SmokingRackBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.SMOKING_RACK.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, SmokingRackBlockEntity entity) {

		if (!level.isClientSide()) {
			if (state.getValue(SmokingRackBlock.HEATED)) {
				if (entity.updateFuelState(level, pos, state)) {
					entity.setSmokeProgress(entity.getSmokeProgress() + 1);

					if (entity.getSmokeProgress() % 10 == 0) {
						level.sendBlockUpdated(pos, state, state, 2);
					}

					if (entity.getSmokeProgress() > MAX_SMOKING_TIME) { // not equal because stuff needs to work on 1 fuel item use
						entity.consumeFuel(level, pos, state);
					}
				}

				for (int i = 0; i < 3; i++) {
					Optional<RecipeHolder<SmokingRackRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeRegistry.SMOKING_RECIPE.get(), new SingleRecipeInput(entity.getItem(i + 1)), level);
					if (entity.canSmokeSlots(recipe, level, pos, state, 1 + i, 4 + i)) {
						entity.updateCuringModifier(recipe, level, pos, state, 1 + i);

						entity.setSlotProgress(1 + i, entity.getSlotProgress(1 + i) + 1);

						if (entity.getSlotProgress(1 + i) >= MAX_SMOKING_TIME * entity.curingModifier1) {
							entity.smokeItem(recipe, level, pos, state, 1 + i, 4 + i);
						}
					} else {
						if (entity.getSlotProgress(1 + i) > 0) {
							entity.setSlotProgress(1 + i, 0);
							level.sendBlockUpdated(pos, state, state, 2);
						}
					}
				}
			} else { // just reset all progress if the fuel runs out or inactive I suppose
				if (entity.getSmokeProgress() > 0) {
					entity.setSmokeProgress(0);
					level.sendBlockUpdated(pos, state, state, 2);
				}

				for (int i = 0; i < 3; i++) {
					if (entity.getSlotProgress(1 + i) > 0) {
						entity.setSlotProgress(1 + i, 0);
						level.sendBlockUpdated(pos, state, state, 2);
					}
				}
			}
		}
	}

	private void setSlotProgress(int slot, int counter) {
		switch (slot) {
			case 1 -> this.slot1Progress = counter;
			case 2 -> this.slot2Progress = counter;
			case 3 -> this.slot3Progress = counter;
		}
	}

	private int getSlotProgress(int slot) {
		return switch (slot) {
			case 1 -> this.slot1Progress;
			case 2 -> this.slot2Progress;
			case 3 -> this.slot3Progress;
			default -> 0;
		};
	}

	@Nullable
	public Entity getRenderEntity(Level level, int slot) {
		ItemStack stack = this.getItems().get(slot);
		if(!stack.isEmpty() && stack.getItem() instanceof MobItem mob && mob.hasEntityData(stack)) {
			return mob.createCapturedEntity(level, 0, 0, 0, stack, false);
		}
		return null;
	}

	public void consumeFuel(Level level, BlockPos pos, BlockState state) {
		ItemStack fuelStack = this.getItems().getFirst();
		this.setSmokeProgress(0);
		level.sendBlockUpdated(pos, state, state, 2);
		fuelStack.shrink(1);
	}

	private boolean canSmokeSlots(Optional<RecipeHolder<SmokingRackRecipe>> recipe, Level level, BlockPos pos, BlockState state, int input, int output) {
		if (!state.getValue(SmokingRackBlock.HEATED) || !this.updateFuelState(level, pos, state) || getItems().get(input).isEmpty() || !getItems().get(output).isEmpty())
			return false;
		else {
			if (recipe.isEmpty())
				return false;
			else {
				ItemStack stack = this.getItems().get(input);
				if(!stack.isEmpty() && stack.getItem() instanceof AnadiaMobItem mob && mob.hasEntityData(stack)) {
					CompoundTag entityNbt = mob.getEntityData(stack);

					return entityNbt == null || !entityNbt.contains("fish_color") || (entityNbt.getByte("fish_color") != 0 && entityNbt.getByte("fish_color") != 1);
				}
				return true;
			}
		}
	}

	private int updateCuringModifier(Optional<RecipeHolder<SmokingRackRecipe>> recipe, Level level, BlockPos pos, BlockState state, int slot) {
		int modifier = recipe.map(holder -> holder.value().smokingTime()).orElse(0);
		if(modifier <= 0) // just in case
			modifier = 1;
		switch (slot) {
			case 1 -> {
				if (this.curingModifier1 != modifier) {
					this.curingModifier1 = modifier;
					level.sendBlockUpdated(pos, state, state, 2);
				}
			}
			case 2 -> {
				if (this.curingModifier2 != modifier) {
					this.curingModifier2 = modifier;
					level.sendBlockUpdated(pos, state, state, 2);
				}
			}
			case 3 -> {
				if (this.curingModifier3 != modifier) {
					this.curingModifier3 = modifier;
					level.sendBlockUpdated(pos, state, state, 2);
				}
			}
		}
		return modifier;
	}

	public boolean updateFuelState(Level level, BlockPos pos, BlockState state) {
		ItemStack fuelStack = this.getItems().getFirst();
		if (!fuelStack.isEmpty()) {
			return true;
		} else if (this.getSmokeProgress() > 0) {
			this.setSmokeProgress(0);
			level.sendBlockUpdated(pos, state, state, 2);
		}
		return false;
	}

	public void smokeItem(Optional<RecipeHolder<SmokingRackRecipe>> recipe, Level level, BlockPos pos, BlockState state, int input, int output) {
		if (this.canSmokeSlots(recipe, level, pos, state, input, output)) {
			ItemStack itemstack = this.getItem(input);
			ItemStack result = recipe.map(holder -> holder.value().result()).orElse(ItemStack.EMPTY);
			if (!result.isEmpty()) {
				ItemStack itemstack2 = this.getItem(output);
				if (itemstack2.isEmpty())
					this.setItem(output, result.copy());
				this.setSlotProgress(input, 0);
				level.sendBlockUpdated(pos, state, state, 2);
				itemstack.shrink(1);
			}
		}
	}

	public void setSmokeProgress(int duration) {
		this.smokeProgress = duration;
	}

	private int getSmokeProgress() {
		return this.smokeProgress;
	}

	public int getSmokeProgressScaled(int index, int count) {
		return getSmokeProgress() * count / MAX_SMOKING_TIME;
	}

	public int getItemProgressScaledTop(int index, int count) {
		return getSlotProgress(1) * count / (MAX_SMOKING_TIME * curingModifier1);
	}

	public int getItemProgressScaledMid(int index, int count) {
		return getSlotProgress(2) * count / (MAX_SMOKING_TIME * curingModifier2);
	}

	public int getItemProgressScaledBottom(int index, int count) {
		return getSlotProgress(3) * count / (MAX_SMOKING_TIME * curingModifier3);
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

	//TODO
	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return null;
	}

	@Override
	public int getContainerSize() {
		return 7;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ContainerHelper.saveAllItems(tag, this.items, registries);
		tag.putInt("smoke_progress", this.getSmokeProgress());
		tag.putInt("slot_1_progress", this.slot1Progress);
		tag.putInt("slot_2_progress", this.slot2Progress);
		tag.putInt("slot_3_progress", this.slot3Progress);
		tag.putInt("curing_modifier_1", this.curingModifier1);
		tag.putInt("curing_modifier_2", this.curingModifier2);
		tag.putInt("curing_modifier_3", this.curingModifier3);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items, registries);
		this.setSmokeProgress(tag.getInt("smoke_progress"));
		this.slot1Progress = tag.getInt("slot_1_progress");
		this.slot2Progress = tag.getInt("slot_2_progress");
		this.slot3Progress = tag.getInt("slot_3_progress");
		this.curingModifier1 = tag.getInt("curing_modifier_1");
		this.curingModifier2 = tag.getInt("curing_modifier_2");
		this.curingModifier3 = tag.getInt("curing_modifier_3");
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
		this.loadAdditional(packet.getTag(), registries);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = super.getUpdateTag(registries);
		this.saveAdditional(tag, registries);
		return tag;
	}
}
