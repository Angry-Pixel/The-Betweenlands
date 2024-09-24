package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Unit;
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
import thebetweenlands.api.recipes.MortarRecipe;
import thebetweenlands.common.inventory.MortarMenu;
import thebetweenlands.common.item.misc.LifeCrystalItem;
import thebetweenlands.common.registries.*;

import javax.annotation.Nullable;
import java.util.Optional;

public class MortarBlockEntity extends BaseContainerBlockEntity {

	public int progress;
	public boolean manualGrinding = false;
	public float crystalVelocity;
	public float crystalRotation;
	public int itemBob;
	private NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
	public final ContainerData data = new ContainerData() {
		public int get(int index) {
			return MortarBlockEntity.this.progress;
		}

		public void set(int index, int value) {
			if (index == 0) {
				MortarBlockEntity.this.progress = value;
			}
		}

		public int getCount() {
			return 1;
		}
	};
	private final RecipeManager.CachedCheck<SingleRecipeInput, MortarRecipe> quickCheck = RecipeManager.createCheck(RecipeRegistry.MORTAR_RECIPE.get());

	public MortarBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.MORTAR.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, MortarBlockEntity entity) {
		if (level.isClientSide()) {
			if (entity.isCrystalInstalled()) {
				entity.crystalVelocity -= Math.signum(entity.crystalVelocity) * 0.05F;
				entity.crystalRotation += entity.crystalVelocity;
				if (entity.crystalRotation >= 360.0F)
					entity.crystalRotation -= 360.0F;
				else if (entity.crystalRotation <= 360.0F)
					entity.crystalRotation += 360.0F;
				if (Math.abs(entity.crystalVelocity) <= 1.0F && level.getRandom().nextInt(15) == 0)
					entity.crystalVelocity = level.getRandom().nextFloat() * 18.0F - 9.0F;
				entity.itemBob++;
			}

			if (entity.progress > 0 && entity.progress < 84) {
				entity.progress++;
			}

			return;
		}

		boolean validRecipe = false;
		boolean outputFull = entity.outputIsFull();

		if (entity.isPestleInstalled()) {
			SingleRecipeInput input = new SingleRecipeInput(entity.getItem(0));
			Optional<RecipeHolder<MortarRecipe>> holder = entity.quickCheck.getRecipeFor(input, level);

			if (holder.isPresent()) {
				MortarRecipe recipe = holder.get().value();
				ItemStack output = recipe.getOutput(input, entity.getItem(2).copy(), level.registryAccess());
				boolean replacesOutput = recipe.replacesOutput();

				outputFull &= !replacesOutput;

				if ((entity.isCrystalInstalled() || entity.manualGrinding)) {
					if (!output.isEmpty() && (replacesOutput || entity.getItem(2).isEmpty() || (ItemStack.isSameItemSameComponents(entity.getItem(2), output) && entity.getItem(2).getCount() + output.getCount() <= output.getMaxStackSize()))) {
						validRecipe = true;

						entity.progress++;

						if (entity.progress == 1) {
							level.playSound(null, pos, SoundRegistry.GRIND.get(), SoundSource.BLOCKS, 1F, 1F);
						}

						if (entity.progress == 64 || entity.progress == 84) {
							level.playSound(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, SoundEvents.GRASS_BREAK, SoundSource.BLOCKS, 0.3F, 1.0F);
							level.playSound(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 0.3F, 1.0F);
						}

						if (entity.isPestleInstalled())
							entity.getItem(1).set(DataComponentRegistry.PESTLE_ACTIVE, Unit.INSTANCE);

						if (entity.progress > 84) {
							if (!entity.getItem(0).isEmpty())
								if (entity.getItem(0).getCount() - 1 <= 0)
									entity.setItem(0, ItemStack.EMPTY);
								else
									entity.getItem(0).shrink(1);

							if (replacesOutput || entity.getItem(2).isEmpty())
								entity.setItem(2, output.copy());
							else if (ItemStack.isSameItemSameComponents(entity.getItem(2), output))
								entity.getItem(2).grow(output.getCount());

							entity.getItem(1).setDamageValue(entity.getItem(1).getDamageValue() + 1);

							if (!entity.manualGrinding)
								entity.getItem(3).setDamageValue(entity.getItem(1).getDamageValue() + 1);

							entity.progress = 0;
							entity.manualGrinding = false;

							if (entity.getItem(1).getDamageValue() >= entity.getItem(1).getMaxDamage()) {
								entity.setItem(1, ItemStack.EMPTY);
							}

							if (!entity.getItem(1).isEmpty())
								entity.getItem(1).remove(DataComponentRegistry.PESTLE_ACTIVE);

							entity.setChanged();
						}
					}
				}
			}
		}

		if (entity.progress > 0) {
			entity.setChanged();
		}

		if (!validRecipe || entity.getItem(0).isEmpty() || entity.getItem(1).isEmpty() || outputFull) {
			if (!entity.getItem(1).isEmpty())
				entity.getItem(1).remove(DataComponentRegistry.PESTLE_ACTIVE);

			if (entity.progress > 0) {
				entity.progress = 0;
				entity.setChanged();
			}
		}
		if (entity.getItem(3).isEmpty() && entity.progress > 0 && !entity.manualGrinding) {
			if (!entity.getItem(1).isEmpty())
				entity.getItem(1).remove(DataComponentRegistry.PESTLE_ACTIVE);
			entity.progress = 0;
			entity.setChanged();
		}
	}

	public boolean isPestleInstalled() {
		return this.getItem(1).is(ItemRegistry.PESTLE);
	}

	public boolean isCrystalInstalled() {
		return this.getItem(3).getItem() instanceof LifeCrystalItem && this.getItem(3).getDamageValue() <= this.getItem(3).getMaxDamage();
	}

	private boolean outputIsFull() {
		return this.getItem(2).getCount() >= this.getMaxStackSize();
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.thebetweenlands.mortar");
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
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return new MortarMenu(containerId, inventory, this, this.data);
	}

	@Override
	public int getContainerSize() {
		return 4;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ContainerHelper.saveAllItems(tag, this.items, registries);
		tag.putInt("progress", this.progress);
		tag.putBoolean("manual_grinding", this.manualGrinding);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items, registries);
		this.progress = tag.getInt("progress");
		this.manualGrinding = tag.getBoolean("manual_grinding");
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (this.getLevel() != null) {
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
		}
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
