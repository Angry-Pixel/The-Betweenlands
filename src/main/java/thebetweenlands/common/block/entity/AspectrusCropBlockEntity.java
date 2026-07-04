package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.common.registries.BlockEntityRegistry;

import javax.annotation.Nullable;

public class AspectrusCropBlockEntity extends SyncedBlockEntity implements IAspectBlockEntity {

	public int glowTicks = 0;

    @Nullable
    private Aspect seedAspect;
    private boolean hasSource;
	@Nullable
	private BlockState fence;

	public AspectrusCropBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.ASPECTRUS_CROP.get(), pos, state);
    }

	public static void tick(Level level, BlockPos pos, BlockState state, AspectrusCropBlockEntity entity) {
		entity.glowTicks++;
	}

    public void setAspect(@Nullable Aspect aspect) {
        this.seedAspect = aspect;
        this.setChanged();
    }

    @Nullable
    public Aspect getAspect() {
        return this.seedAspect;
    }

    public void setHasSource(boolean source) {
        this.hasSource = source;
        this.setChanged();
    }

    public boolean hasSource() {
        return this.hasSource;
    }

	public void setFence(@Nullable BlockState fence) {
		this.fence = fence;
		this.setChanged();
	}

	@Nullable
	public BlockState getFence() {
		return this.fence;
	}

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.seedAspect != null) {
            this.seedAspect.writeToNBT(tag, registries);
        }
        tag.putBoolean("hasSource", this.hasSource);
		if (this.fence != null) {
			tag.put("fence", NbtUtils.writeBlockState(this.fence));
		}
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.seedAspect = Aspect.readFromNBT(tag, registries);
        this.hasSource = tag.getBoolean("hasSource");

		if (tag.contains("fence", CompoundTag.TAG_COMPOUND)) {
			this.fence = NbtUtils.readBlockState(registries.lookupOrThrow(Registries.BLOCK), tag.getCompound("fence"));
		}
    }
}