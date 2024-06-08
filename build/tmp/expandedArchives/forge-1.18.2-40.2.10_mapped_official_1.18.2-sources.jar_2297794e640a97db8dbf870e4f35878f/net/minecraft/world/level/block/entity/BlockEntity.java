package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public abstract class BlockEntity extends net.minecraftforge.common.capabilities.CapabilityProvider<BlockEntity> implements net.minecraftforge.common.extensions.IForgeBlockEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final BlockEntityType<?> type;
   @Nullable
   protected Level level;
   protected final BlockPos worldPosition;
   protected boolean remove;
   private BlockState blockState;
   private CompoundTag customTileData;

   public BlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
      super(BlockEntity.class);
      this.type = p_155228_;
      this.worldPosition = p_155229_.immutable();
      this.blockState = p_155230_;
      this.gatherCapabilities();
   }

   public static BlockPos getPosFromTag(CompoundTag p_187473_) {
      return new BlockPos(p_187473_.getInt("x"), p_187473_.getInt("y"), p_187473_.getInt("z"));
   }

   @Nullable
   public Level getLevel() {
      return this.level;
   }

   public void setLevel(Level p_155231_) {
      this.level = p_155231_;
   }

   public boolean hasLevel() {
      return this.level != null;
   }

   public void load(CompoundTag p_155245_) {
      if (p_155245_.contains("ForgeData")) this.customTileData = p_155245_.getCompound("ForgeData");
      if (getCapabilities() != null && p_155245_.contains("ForgeCaps")) deserializeCaps(p_155245_.getCompound("ForgeCaps"));
   }

   protected void saveAdditional(CompoundTag p_187471_) {
      if (this.customTileData != null) p_187471_.put("ForgeData", this.customTileData.copy());
      if (getCapabilities() != null) p_187471_.put("ForgeCaps", serializeCaps());
   }

   public final CompoundTag saveWithFullMetadata() {
      CompoundTag compoundtag = this.saveWithoutMetadata();
      this.saveMetadata(compoundtag);
      return compoundtag;
   }

   public final CompoundTag saveWithId() {
      CompoundTag compoundtag = this.saveWithoutMetadata();
      this.saveId(compoundtag);
      return compoundtag;
   }

   public final CompoundTag saveWithoutMetadata() {
      CompoundTag compoundtag = new CompoundTag();
      this.saveAdditional(compoundtag);
      return compoundtag;
   }

   private void saveId(CompoundTag p_187475_) {
      ResourceLocation resourcelocation = BlockEntityType.getKey(this.getType());
      if (resourcelocation == null) {
         throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
      } else {
         p_187475_.putString("id", resourcelocation.toString());
      }
   }

   public static void addEntityType(CompoundTag p_187469_, BlockEntityType<?> p_187470_) {
      p_187469_.putString("id", BlockEntityType.getKey(p_187470_).toString());
   }

   public void saveToItem(ItemStack p_187477_) {
      BlockItem.setBlockEntityData(p_187477_, this.getType(), this.saveWithoutMetadata());
   }

   private void saveMetadata(CompoundTag p_187479_) {
      this.saveId(p_187479_);
      p_187479_.putInt("x", this.worldPosition.getX());
      p_187479_.putInt("y", this.worldPosition.getY());
      p_187479_.putInt("z", this.worldPosition.getZ());
   }

   @Nullable
   public static BlockEntity loadStatic(BlockPos p_155242_, BlockState p_155243_, CompoundTag p_155244_) {
      String s = p_155244_.getString("id");
      ResourceLocation resourcelocation = ResourceLocation.tryParse(s);
      if (resourcelocation == null) {
         LOGGER.error("Block entity has invalid type: {}", (Object)s);
         return null;
      } else {
         return Registry.BLOCK_ENTITY_TYPE.getOptional(resourcelocation).map((p_155240_) -> {
            try {
               return p_155240_.create(p_155242_, p_155243_);
            } catch (Throwable throwable) {
               LOGGER.error("Failed to create block entity {}", s, throwable);
               return null;
            }
         }).map((p_155249_) -> {
            try {
               p_155249_.load(p_155244_);
               return p_155249_;
            } catch (Throwable throwable) {
               LOGGER.error("Failed to load data for block entity {}", s, throwable);
               return null;
            }
         }).orElseGet(() -> {
            LOGGER.warn("Skipping BlockEntity with id {}", (Object)s);
            return null;
         });
      }
   }

   public void setChanged() {
      if (this.level != null) {
         setChanged(this.level, this.worldPosition, this.blockState);
      }

   }

   protected static void setChanged(Level p_155233_, BlockPos p_155234_, BlockState p_155235_) {
      p_155233_.blockEntityChangedWithoutNeighborUpdates(p_155234_);
      if (!p_155235_.isAir()) {
         p_155233_.updateNeighbourForOutputSignal(p_155234_, p_155235_.getBlock());
      }

   }

   public BlockPos getBlockPos() {
      return this.worldPosition;
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return null;
   }

   public CompoundTag getUpdateTag() {
      return new CompoundTag();
   }

   public boolean isRemoved() {
      return this.remove;
   }

   public void setRemoved() {
      this.remove = true;
      this.invalidateCaps();
      requestModelDataUpdate();
   }

   @Override
   public void onChunkUnloaded() {
      this.invalidateCaps();
   }

   public void clearRemoved() {
      this.remove = false;
   }

   public boolean triggerEvent(int p_58889_, int p_58890_) {
      return false;
   }

   public void fillCrashReportCategory(CrashReportCategory p_58887_) {
      p_58887_.setDetail("Name", () -> {
         return Registry.BLOCK_ENTITY_TYPE.getKey(this.getType()) + " // " + this.getClass().getCanonicalName();
      });
      if (this.level != null) {
         CrashReportCategory.populateBlockDetails(p_58887_, this.level, this.worldPosition, this.getBlockState());
         CrashReportCategory.populateBlockDetails(p_58887_, this.level, this.worldPosition, this.level.getBlockState(this.worldPosition));
      }
   }

   public boolean onlyOpCanSetNbt() {
      return false;
   }

   public BlockEntityType<?> getType() {
      return this.type;
   }

   @Override
   public CompoundTag getTileData() {
      if (this.customTileData == null)
         this.customTileData = new CompoundTag();
      return this.customTileData;
   }

   /** @deprecated */
   @Deprecated
   public void setBlockState(BlockState p_155251_) {
      this.blockState = p_155251_;
   }
}
