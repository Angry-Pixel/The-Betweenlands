package net.minecraft.world.item;

import com.mojang.logging.LogUtils;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;

public class CompassItem extends Item implements Vanishable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String TAG_LODESTONE_POS = "LodestonePos";
   public static final String TAG_LODESTONE_DIMENSION = "LodestoneDimension";
   public static final String TAG_LODESTONE_TRACKED = "LodestoneTracked";

   public CompassItem(Item.Properties p_40718_) {
      super(p_40718_);
   }

   public static boolean isLodestoneCompass(ItemStack p_40737_) {
      CompoundTag compoundtag = p_40737_.getTag();
      return compoundtag != null && (compoundtag.contains("LodestoneDimension") || compoundtag.contains("LodestonePos"));
   }

   public boolean isFoil(ItemStack p_40739_) {
      return isLodestoneCompass(p_40739_) || super.isFoil(p_40739_);
   }

   public static Optional<ResourceKey<Level>> getLodestoneDimension(CompoundTag p_40728_) {
      return Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, p_40728_.get("LodestoneDimension")).result();
   }

   public void inventoryTick(ItemStack p_40720_, Level p_40721_, Entity p_40722_, int p_40723_, boolean p_40724_) {
      if (!p_40721_.isClientSide) {
         if (isLodestoneCompass(p_40720_)) {
            CompoundTag compoundtag = p_40720_.getOrCreateTag();
            if (compoundtag.contains("LodestoneTracked") && !compoundtag.getBoolean("LodestoneTracked")) {
               return;
            }

            Optional<ResourceKey<Level>> optional = getLodestoneDimension(compoundtag);
            if (optional.isPresent() && optional.get() == p_40721_.dimension() && compoundtag.contains("LodestonePos")) {
               BlockPos blockpos = NbtUtils.readBlockPos(compoundtag.getCompound("LodestonePos"));
               if (!p_40721_.isInWorldBounds(blockpos) || !((ServerLevel)p_40721_).getPoiManager().existsAtPosition(PoiType.LODESTONE, blockpos)) {
                  compoundtag.remove("LodestonePos");
               }
            }
         }

      }
   }

   public InteractionResult useOn(UseOnContext p_40726_) {
      BlockPos blockpos = p_40726_.getClickedPos();
      Level level = p_40726_.getLevel();
      if (!level.getBlockState(blockpos).is(Blocks.LODESTONE)) {
         return super.useOn(p_40726_);
      } else {
         level.playSound((Player)null, blockpos, SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
         Player player = p_40726_.getPlayer();
         ItemStack itemstack = p_40726_.getItemInHand();
         boolean flag = !player.getAbilities().instabuild && itemstack.getCount() == 1;
         if (flag) {
            this.addLodestoneTags(level.dimension(), blockpos, itemstack.getOrCreateTag());
         } else {
            ItemStack itemstack1 = new ItemStack(Items.COMPASS, 1);
            CompoundTag compoundtag = itemstack.hasTag() ? itemstack.getTag().copy() : new CompoundTag();
            itemstack1.setTag(compoundtag);
            if (!player.getAbilities().instabuild) {
               itemstack.shrink(1);
            }

            this.addLodestoneTags(level.dimension(), blockpos, compoundtag);
            if (!player.getInventory().add(itemstack1)) {
               player.drop(itemstack1, false);
            }
         }

         return InteractionResult.sidedSuccess(level.isClientSide);
      }
   }

   private void addLodestoneTags(ResourceKey<Level> p_40733_, BlockPos p_40734_, CompoundTag p_40735_) {
      p_40735_.put("LodestonePos", NbtUtils.writeBlockPos(p_40734_));
      Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, p_40733_).resultOrPartial(LOGGER::error).ifPresent((p_40731_) -> {
         p_40735_.put("LodestoneDimension", p_40731_);
      });
      p_40735_.putBoolean("LodestoneTracked", true);
   }

   public String getDescriptionId(ItemStack p_40741_) {
      return isLodestoneCompass(p_40741_) ? "item.minecraft.lodestone_compass" : super.getDescriptionId(p_40741_);
   }
}