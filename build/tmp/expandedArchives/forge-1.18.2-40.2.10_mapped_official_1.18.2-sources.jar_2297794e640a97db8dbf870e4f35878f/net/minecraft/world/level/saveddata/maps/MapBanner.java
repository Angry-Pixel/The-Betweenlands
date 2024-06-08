package net.minecraft.world.level.saveddata.maps;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MapBanner {
   private final BlockPos pos;
   private final DyeColor color;
   @Nullable
   private final Component name;

   public MapBanner(BlockPos p_77770_, DyeColor p_77771_, @Nullable Component p_77772_) {
      this.pos = p_77770_;
      this.color = p_77771_;
      this.name = p_77772_;
   }

   public static MapBanner load(CompoundTag p_77778_) {
      BlockPos blockpos = NbtUtils.readBlockPos(p_77778_.getCompound("Pos"));
      DyeColor dyecolor = DyeColor.byName(p_77778_.getString("Color"), DyeColor.WHITE);
      Component component = p_77778_.contains("Name") ? Component.Serializer.fromJson(p_77778_.getString("Name")) : null;
      return new MapBanner(blockpos, dyecolor, component);
   }

   @Nullable
   public static MapBanner fromWorld(BlockGetter p_77775_, BlockPos p_77776_) {
      BlockEntity blockentity = p_77775_.getBlockEntity(p_77776_);
      if (blockentity instanceof BannerBlockEntity) {
         BannerBlockEntity bannerblockentity = (BannerBlockEntity)blockentity;
         DyeColor dyecolor = bannerblockentity.getBaseColor();
         Component component = bannerblockentity.hasCustomName() ? bannerblockentity.getCustomName() : null;
         return new MapBanner(p_77776_, dyecolor, component);
      } else {
         return null;
      }
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public DyeColor getColor() {
      return this.color;
   }

   public MapDecoration.Type getDecoration() {
      switch(this.color) {
      case WHITE:
         return MapDecoration.Type.BANNER_WHITE;
      case ORANGE:
         return MapDecoration.Type.BANNER_ORANGE;
      case MAGENTA:
         return MapDecoration.Type.BANNER_MAGENTA;
      case LIGHT_BLUE:
         return MapDecoration.Type.BANNER_LIGHT_BLUE;
      case YELLOW:
         return MapDecoration.Type.BANNER_YELLOW;
      case LIME:
         return MapDecoration.Type.BANNER_LIME;
      case PINK:
         return MapDecoration.Type.BANNER_PINK;
      case GRAY:
         return MapDecoration.Type.BANNER_GRAY;
      case LIGHT_GRAY:
         return MapDecoration.Type.BANNER_LIGHT_GRAY;
      case CYAN:
         return MapDecoration.Type.BANNER_CYAN;
      case PURPLE:
         return MapDecoration.Type.BANNER_PURPLE;
      case BLUE:
         return MapDecoration.Type.BANNER_BLUE;
      case BROWN:
         return MapDecoration.Type.BANNER_BROWN;
      case GREEN:
         return MapDecoration.Type.BANNER_GREEN;
      case RED:
         return MapDecoration.Type.BANNER_RED;
      case BLACK:
      default:
         return MapDecoration.Type.BANNER_BLACK;
      }
   }

   @Nullable
   public Component getName() {
      return this.name;
   }

   public boolean equals(Object p_77786_) {
      if (this == p_77786_) {
         return true;
      } else if (p_77786_ != null && this.getClass() == p_77786_.getClass()) {
         MapBanner mapbanner = (MapBanner)p_77786_;
         return Objects.equals(this.pos, mapbanner.pos) && this.color == mapbanner.color && Objects.equals(this.name, mapbanner.name);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(this.pos, this.color, this.name);
   }

   public CompoundTag save() {
      CompoundTag compoundtag = new CompoundTag();
      compoundtag.put("Pos", NbtUtils.writeBlockPos(this.pos));
      compoundtag.putString("Color", this.color.getName());
      if (this.name != null) {
         compoundtag.putString("Name", Component.Serializer.toJson(this.name));
      }

      return compoundtag;
   }

   public String getId() {
      return "banner-" + this.pos.getX() + "," + this.pos.getY() + "," + this.pos.getZ();
   }
}