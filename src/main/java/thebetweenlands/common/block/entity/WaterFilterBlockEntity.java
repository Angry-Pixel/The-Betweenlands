package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import thebetweenlands.common.registries.*;

import java.util.Collections;
import java.util.List;

public class WaterFilterBlockEntity extends NoMenuContainerBlockEntity implements IFluidHandler {

	public boolean showFluidAnimation;
	public FluidTank tank = new FluidTank(FluidType.BUCKET_VOLUME * 4);
	private NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);

	public WaterFilterBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.WATER_FILTER.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, WaterFilterBlockEntity entity) {
		if (!level.isClientSide() && level.getGameTime() % 10 == 0) {
			IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos.below(), Direction.UP);

			if (handler != null) {
				for (int i = 0; i < handler.getTanks(); i++) {
					if (handler.isFluidValid(i, entity.getResultFluid(entity.tank.getFluid()))) {
						FluidStack tankFluid = entity.tank.drain(20, FluidAction.SIMULATE);
						if (!tankFluid.isEmpty()) {
							int filled = handler.fill(entity.getResultFluid(tankFluid), FluidAction.EXECUTE);

							FluidStack drained = entity.tank.drain(filled, FluidAction.EXECUTE);

							if (!drained.isEmpty() && entity.hasFilter()) {
								if (drained.getAmount() >= 20) {
									entity.addByProductRandom(level, drained);
								}

								entity.damageFilter(level, pos, 1);
							}

							if (!entity.getFluidAnimation())
								entity.setFluidAnimation(true);

							level.sendBlockUpdated(pos, state, state, 2);
						} else if (tankFluid.getAmount() == 0 && entity.getFluidAnimation()) {
							entity.setFluidAnimation(false);
							level.sendBlockUpdated(pos, state, state, 2);
						}
					}
				}
			}
		}

		if (!level.isClientSide() && entity.tank.isEmpty() && entity.getFluidAnimation()) { //instead of filter check - check for can empty stuff and change rendering
			entity.setFluidAnimation(false);
			level.sendBlockUpdated(pos, state, state, 2);
		}
	}

	public FluidStack getResultFluid(FluidStack fluid) {
		if(this.hasFilter()) {
			if (fluid.is(FluidRegistry.STAGNANT_WATER_STILL)) {
				return new FluidStack(FluidRegistry.SWAMP_WATER_STILL, fluid.getAmount());
			}
			if (fluid.is(FluidRegistry.SWAMP_WATER_STILL)) {
				return new FluidStack(FluidRegistry.CLEAN_WATER_STILL, fluid.getAmount());
			}
		}
		return fluid.copy();
	}

	public void setFluidAnimation(boolean showFluid) {
		this.showFluidAnimation = showFluid;
	}

	public boolean getFluidAnimation() {
		return this.showFluidAnimation;
	}

	private void addByProductRandom(Level level, FluidStack fluid) {
		if (level.getRandom().nextInt(50) == 0) {
			ItemStack stack = ItemStack.EMPTY;
			if (fluid.is(FluidRegistry.STAGNANT_WATER_STILL))
				stack = this.chooseRandomItemFromLootTable(level, this.getStagnantWaterLootTable());

			if (fluid.is(FluidRegistry.SWAMP_WATER_STILL))
				stack = this.chooseRandomItemFromLootTable(level, this.getSwampWaterLootTable());

			for (int slot = 1; slot < 5; slot++) {
//				if (this.insertItem(slot, stack, false).isEmpty()) {
//					break;
//				}
			}
		}
	}

	protected ResourceKey<LootTable> getStagnantWaterLootTable() {
		return LootTableRegistry.FILTERED_STAGNANT_WATER;
	}

	protected ResourceKey<LootTable> getSwampWaterLootTable() {
		return LootTableRegistry.FILTERED_SWAMP_WATER;
	}

	public ItemStack chooseRandomItemFromLootTable(Level level, ResourceKey<LootTable> resourceLocation) {
		if (level.getServer() != null) {
			LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(resourceLocation);
			LootParams.Builder lootBuilder = new LootParams.Builder((ServerLevel) level)
				.withParameter(LootContextParams.BLOCK_ENTITY, this)
				.withParameter(LootContextParams.BLOCK_STATE, this.getBlockState())
				.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.getBlockPos()));
			List<ItemStack> loot = lootTable.getRandomItems(lootBuilder.create(LootContextParamSets.EMPTY), level.getRandom());
			if (!loot.isEmpty()) {
				Collections.shuffle(loot);
				return loot.getFirst();
			}
		}
		return new ItemStack(ItemRegistry.GLUE.get()); // to stop null;
	}

	private void damageFilter(Level level, BlockPos pos, int damage) {
		ItemStack mesh = this.getItem(0);
		if(!mesh.isEmpty()) {
			mesh.setDamageValue(mesh.getDamageValue() +damage);
			if(mesh.getDamageValue() > mesh.getMaxDamage()) {
				mesh.shrink(1);
				level.levelEvent(2001, pos, Block.getId(BlockRegistry.WEEDWOOD_PLANKS.get().defaultBlockState()));
			}
		}
	}

	public boolean hasFilter() {
		return this.hasMossFilter() || this.hasSilkFilter();
	}

	public boolean hasMossFilter() {
		return this.getItem(0).is(ItemRegistry.MOSS_FILTER);
	}

	public boolean hasSilkFilter() {
		return this.getItem(0).is(ItemRegistry.SILK_FILTER);
	}

	public int getTankFluidAmount() {
		return this.tank.getFluidAmount();
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
		return 5;
	}

	@Override
	public int getTanks() {
		return this.tank.getTanks();
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return this.tank.getFluidInTank(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		return this.tank.getTankCapacity(tank);
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return this.tank.isFluidValid(tank, stack);
	}

	@Override
	public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
		return this.tank.fill(resource, action);
	}

	@Override
	public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
		return this.tank.drain(resource, action);
	}

	@Override
	public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
		return this.tank.drain(maxDrain, action);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ContainerHelper.saveAllItems(tag, this.items, registries);
		this.tank.writeToNBT(registries, tag);
		tag.putBoolean("show_fluid_animation", this.showFluidAnimation);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items, registries);
		this.tank.readFromNBT(registries, tag);
		this.showFluidAnimation = tag.getBoolean("show_fluid_animation");
	}
}
