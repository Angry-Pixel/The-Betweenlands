package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.common.network.clientbound.InfestWeedwoodBushPacket;
import thebetweenlands.common.registries.*;

public class GrubHubBlockEntity extends NoMenuContainerBlockEntity implements IFluidHandler {

	public final FluidTank tank = new FluidTank(FluidType.BUCKET_VOLUME, stack -> stack.is(FluidRegistry.PHEROMONE_EXTRACT_STILL));
	private NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

	public int switchTextureCount = 0;

	public GrubHubBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.GRUB_HUB.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, GrubHubBlockEntity entity) {
		if (!level.isClientSide() && level.getGameTime() % 10 == 0)
			entity.checkCanInfestOrHarvest(level, pos);

		if (level.isClientSide && entity.switchTextureCount > 0)
			entity.switchTextureCount--;
	}

	private void checkCanInfestOrHarvest(Level level, BlockPos pos) {
		for (BlockPos checkPos : BlockPos.betweenClosedStream(new AABB(pos.below()).inflate(1.0D, 0.0D, 1.0D)).toList()) {
			BlockState state = level.getBlockState(checkPos);
			if (state.is(BlockRegistry.WEEDWOOD_BUSH) && this.getTankFluidAmount() >= 50) {
				this.infestBush(level, checkPos);
			} else if (state.is(BlockRegistry.GRUB_INFESTED_WEEDWOOD_BUSH) && this.canAddGrub()) {
				this.harvestGrub(level, checkPos);
			}
		}
	}

	private void harvestGrub(Level level, BlockPos pos) {
		level.playSound(null, pos, SoundRegistry.GRUB_HUB_SUCK.get(), SoundSource.BLOCKS, 0.5F, 1F);
		level.setBlockAndUpdate(pos, BlockRegistry.WEEDWOOD_BUSH.get().defaultBlockState());
		ItemStack contents = this.getItem(0);
		ItemStack grub = new ItemStack(ItemRegistry.SILK_GRUB.get());
		if (contents.isEmpty()) {
			this.setItem(0, grub);
		} else {
			contents.grow(1);
		}
		level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
		this.setChanged();
	}

	private boolean canAddGrub() {
		// this check could be used to stop the infestation of the bush beforehand, but atm I'm letting the bush spawn nasties if inventory is full
		ItemStack contents = this.getItem(0);
		return contents.isEmpty() || contents.is(ItemRegistry.SILK_GRUB) && contents.getCount() < this.getMaxStackSize();
	}

	private void infestBush(Level level, BlockPos pos) {
		level.playSound(null, pos, SoundRegistry.GRUB_HUB_MIST.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
		level.setBlockAndUpdate(pos, BlockRegistry.PHEROMONE_INFUSED_WEEDWOOD_BUSH.get().defaultBlockState());
		this.drain(50, FluidAction.EXECUTE);
		level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
		this.setChanged();
		PacketDistributor.sendToPlayersNear((ServerLevel) level, null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 32.0D, new InfestWeedwoodBushPacket(this, pos));
	}

	public int getTankFluidAmount() {
		return this.tank.getFluidAmount();
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
	public int getContainerSize() {
		return 1;
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
	public int fill(FluidStack resource, FluidAction action) {
		return this.tank.fill(resource, action);
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return this.tank.drain(resource, action);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return this.tank.drain(maxDrain, action);
	}
}
