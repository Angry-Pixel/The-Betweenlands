package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
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
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import thebetweenlands.common.block.PurifierBlock;
import thebetweenlands.common.items.recipe.PurifierRecipe;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.RecipeRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;

public class PurifierBlockEntity extends BaseContainerBlockEntity {

	private static final int MAX_TIME = 432;
	public final FluidTank waterTank = new FluidTank(FluidType.BUCKET_VOLUME * 4, fluidStack -> fluidStack.is(FluidRegistry.SWAMP_WATER_STILL.get()));
	public int time = 0;
	protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
	public final ContainerData data = new ContainerData() {
		public int get(int index) {
			return switch (index) {
				case 0 -> PurifierBlockEntity.this.time;
				case 1 -> PurifierBlockEntity.this.waterTank.getFluidAmount();
				case 2 -> PurifierBlockEntity.this.waterTank.getCapacity();
				default -> 0;
			};
		}

		public void set(int index, int value) {
			if (index == 0) {
				PurifierBlockEntity.this.time = value;
			}
		}

		public int getCount() {
			return 3;
		}
	};

	private final RecipeManager.CachedCheck<SingleRecipeInput, PurifierRecipe> quickCheck = RecipeManager.createCheck(RecipeRegistry.PURIFIER_RECIPE.get());

	public PurifierBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.PURIFIER.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, PurifierBlockEntity entity) {
		if (level.isClientSide())
			return;
		ItemStack fuel = entity.getItems().get(1);
		ItemStack input = entity.getItems().get(0);
		if (!fuel.isEmpty() && !input.isEmpty() && !entity.waterTank.isEmpty()) {
			RecipeHolder<PurifierRecipe> recipeholder = entity.quickCheck.getRecipeFor(new SingleRecipeInput(input), level).orElse(null);
			if (entity.canPurify(level.registryAccess(), recipeholder)) {
				entity.time++;
				if (entity.time % 108 == 0)
					level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundRegistry.PURIFIER.get(), SoundSource.BLOCKS, 1.5F, 1.0F);
				entity.updateLitState(level, pos, state, true);
				if (entity.time >= MAX_TIME) {
					if (entity.purify(level.registryAccess(), recipeholder)) {
						entity.time = 0;
						entity.setChanged();
						entity.updateLitState(level, pos, state, entity.canPurify(level.registryAccess(), recipeholder));
					}
				}
			}
		}
		if (entity.time > 0) {
			entity.setChanged();
		}
	}

	private boolean canPurify(RegistryAccess access, @Nullable RecipeHolder<PurifierRecipe> recipe) {
		if (!this.getItems().getFirst().isEmpty() && recipe != null) {
			if (recipe.value().requiredWater().getAmount() > this.waterTank.getFluidAmount()) return false;
			ItemStack itemstack = recipe.value().assemble(new SingleRecipeInput(this.getItem(0)), access);
			if (itemstack.isEmpty()) {
				return false;
			} else {
				ItemStack itemstack1 = this.getItems().get(2);
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

	private boolean purify(RegistryAccess registryAccess, @Nullable RecipeHolder<PurifierRecipe> recipe) {
		if (recipe != null && this.canPurify(registryAccess, recipe)) {
			ItemStack input = this.getItems().getFirst();
			ItemStack recipeResult = recipe.value().assemble(new SingleRecipeInput(this.getItem(0)), registryAccess);
			ItemStack output = this.getItems().get(2);
			if (output.isEmpty()) {
				this.getItems().set(2, recipeResult.copy());
			} else if (ItemStack.isSameItemSameComponents(output, recipeResult)) {
				output.grow(recipeResult.getCount());
			}
			this.waterTank.drain(recipe.value().requiredWater(), IFluidHandler.FluidAction.EXECUTE);
			input.shrink(1);
			this.getItems().get(1).shrink(1);
			return true;
		} else {
			return false;
		}
	}

	private void updateLitState(Level level, BlockPos pos, BlockState state, boolean lit) {
		if (state.hasProperty(PurifierBlock.LIT) && state.getValue(PurifierBlock.LIT) != lit) {
			level.setBlockAndUpdate(pos, state.setValue(PurifierBlock.LIT, lit));
		}
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.thebetweenlands.purifier");
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
		return 3;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ContainerHelper.saveAllItems(tag, this.items, registries);
		tag.putInt("progress", this.time);
		tag.put("tank", this.waterTank.writeToNBT(registries, new CompoundTag()));
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items, registries);
		this.time = tag.getInt("progress");
		this.waterTank.readFromNBT(registries, tag.getCompound("tank"));
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
