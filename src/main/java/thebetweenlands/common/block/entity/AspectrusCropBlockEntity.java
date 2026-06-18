package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.common.registries.BlockEntityRegistry;

import javax.annotation.Nullable;

public class AspectrusCropBlockEntity extends SyncedBlockEntity implements EntityBlock {

    @Nullable
    private Aspect seedAspect;
    private boolean hasSource;

    public AspectrusCropBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.ASPECTRUS_CROP.get(), pos, state);
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AspectrusCropBlockEntity(pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.seedAspect != null) {
            this.seedAspect.writeToNBT(tag, registries);
        }
        tag.putBoolean("hasSource", this.hasSource);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.seedAspect = Aspect.readFromNBT(tag, registries);
        this.hasSource = tag.getBoolean("hasSource");
    }
}