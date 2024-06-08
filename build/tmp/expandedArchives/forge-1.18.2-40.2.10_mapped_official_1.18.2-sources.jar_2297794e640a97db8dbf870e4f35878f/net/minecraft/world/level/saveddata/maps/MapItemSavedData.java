package net.minecraft.world.level.saveddata.maps;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;

public class MapItemSavedData extends SavedData {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAP_SIZE = 128;
   private static final int HALF_MAP_SIZE = 64;
   public static final int MAX_SCALE = 4;
   public static final int TRACKED_DECORATION_LIMIT = 256;
   public final int x;
   public final int z;
   public final ResourceKey<Level> dimension;
   public final boolean trackingPosition;
   public final boolean unlimitedTracking;
   public final byte scale;
   public byte[] colors = new byte[16384];
   public final boolean locked;
   public final List<MapItemSavedData.HoldingPlayer> carriedBy = Lists.newArrayList();
   public final Map<Player, MapItemSavedData.HoldingPlayer> carriedByPlayers = Maps.newHashMap();
   public final Map<String, MapBanner> bannerMarkers = Maps.newHashMap();
   public final Map<String, MapDecoration> decorations = Maps.newLinkedHashMap();
   public final Map<String, MapFrame> frameMarkers = Maps.newHashMap();
   public int trackedDecorationCount;

   public MapItemSavedData(int p_164768_, int p_164769_, byte p_164770_, boolean p_164771_, boolean p_164772_, boolean p_164773_, ResourceKey<Level> p_164774_) {
      this.scale = p_164770_;
      this.x = p_164768_;
      this.z = p_164769_;
      this.dimension = p_164774_;
      this.trackingPosition = p_164771_;
      this.unlimitedTracking = p_164772_;
      this.locked = p_164773_;
      this.setDirty();
   }

   public static MapItemSavedData createFresh(double p_164781_, double p_164782_, byte p_164783_, boolean p_164784_, boolean p_164785_, ResourceKey<Level> p_164786_) {
      int i = 128 * (1 << p_164783_);
      int j = Mth.floor((p_164781_ + 64.0D) / (double)i);
      int k = Mth.floor((p_164782_ + 64.0D) / (double)i);
      int l = j * i + i / 2 - 64;
      int i1 = k * i + i / 2 - 64;
      return new MapItemSavedData(l, i1, p_164783_, p_164784_, p_164785_, false, p_164786_);
   }

   public static MapItemSavedData createForClient(byte p_164777_, boolean p_164778_, ResourceKey<Level> p_164779_) {
      return new MapItemSavedData(0, 0, p_164777_, false, false, p_164778_, p_164779_);
   }

   public static MapItemSavedData load(CompoundTag p_164808_) {
      ResourceKey<Level> resourcekey = DimensionType.parseLegacy(new Dynamic<>(NbtOps.INSTANCE, p_164808_.get("dimension"))).resultOrPartial(LOGGER::error).orElseThrow(() -> {
         return new IllegalArgumentException("Invalid map dimension: " + p_164808_.get("dimension"));
      });
      int i = p_164808_.getInt("xCenter");
      int j = p_164808_.getInt("zCenter");
      byte b0 = (byte)Mth.clamp(p_164808_.getByte("scale"), 0, 4);
      boolean flag = !p_164808_.contains("trackingPosition", 1) || p_164808_.getBoolean("trackingPosition");
      boolean flag1 = p_164808_.getBoolean("unlimitedTracking");
      boolean flag2 = p_164808_.getBoolean("locked");
      MapItemSavedData mapitemsaveddata = new MapItemSavedData(i, j, b0, flag, flag1, flag2, resourcekey);
      byte[] abyte = p_164808_.getByteArray("colors");
      if (abyte.length == 16384) {
         mapitemsaveddata.colors = abyte;
      }

      ListTag listtag = p_164808_.getList("banners", 10);

      for(int k = 0; k < listtag.size(); ++k) {
         MapBanner mapbanner = MapBanner.load(listtag.getCompound(k));
         mapitemsaveddata.bannerMarkers.put(mapbanner.getId(), mapbanner);
         mapitemsaveddata.addDecoration(mapbanner.getDecoration(), (LevelAccessor)null, mapbanner.getId(), (double)mapbanner.getPos().getX(), (double)mapbanner.getPos().getZ(), 180.0D, mapbanner.getName());
      }

      ListTag listtag1 = p_164808_.getList("frames", 10);

      for(int l = 0; l < listtag1.size(); ++l) {
         MapFrame mapframe = MapFrame.load(listtag1.getCompound(l));
         mapitemsaveddata.frameMarkers.put(mapframe.getId(), mapframe);
         mapitemsaveddata.addDecoration(MapDecoration.Type.FRAME, (LevelAccessor)null, "frame-" + mapframe.getEntityId(), (double)mapframe.getPos().getX(), (double)mapframe.getPos().getZ(), (double)mapframe.getRotation(), (Component)null);
      }

      return mapitemsaveddata;
   }

   public CompoundTag save(CompoundTag p_77956_) {
      ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, this.dimension.location()).resultOrPartial(LOGGER::error).ifPresent((p_77954_) -> {
         p_77956_.put("dimension", p_77954_);
      });
      p_77956_.putInt("xCenter", this.x);
      p_77956_.putInt("zCenter", this.z);
      p_77956_.putByte("scale", this.scale);
      p_77956_.putByteArray("colors", this.colors);
      p_77956_.putBoolean("trackingPosition", this.trackingPosition);
      p_77956_.putBoolean("unlimitedTracking", this.unlimitedTracking);
      p_77956_.putBoolean("locked", this.locked);
      ListTag listtag = new ListTag();

      for(MapBanner mapbanner : this.bannerMarkers.values()) {
         listtag.add(mapbanner.save());
      }

      p_77956_.put("banners", listtag);
      ListTag listtag1 = new ListTag();

      for(MapFrame mapframe : this.frameMarkers.values()) {
         listtag1.add(mapframe.save());
      }

      p_77956_.put("frames", listtag1);
      return p_77956_;
   }

   public MapItemSavedData locked() {
      MapItemSavedData mapitemsaveddata = new MapItemSavedData(this.x, this.z, this.scale, this.trackingPosition, this.unlimitedTracking, true, this.dimension);
      mapitemsaveddata.bannerMarkers.putAll(this.bannerMarkers);
      mapitemsaveddata.decorations.putAll(this.decorations);
      mapitemsaveddata.trackedDecorationCount = this.trackedDecorationCount;
      System.arraycopy(this.colors, 0, mapitemsaveddata.colors, 0, this.colors.length);
      mapitemsaveddata.setDirty();
      return mapitemsaveddata;
   }

   public MapItemSavedData scaled(int p_164788_) {
      return createFresh((double)this.x, (double)this.z, (byte)Mth.clamp(this.scale + p_164788_, 0, 4), this.trackingPosition, this.unlimitedTracking, this.dimension);
   }

   public void tickCarriedBy(Player p_77919_, ItemStack p_77920_) {
      if (!this.carriedByPlayers.containsKey(p_77919_)) {
         MapItemSavedData.HoldingPlayer mapitemsaveddata$holdingplayer = new MapItemSavedData.HoldingPlayer(p_77919_);
         this.carriedByPlayers.put(p_77919_, mapitemsaveddata$holdingplayer);
         this.carriedBy.add(mapitemsaveddata$holdingplayer);
      }

      if (!p_77919_.getInventory().contains(p_77920_)) {
         this.removeDecoration(p_77919_.getName().getString());
      }

      for(int i = 0; i < this.carriedBy.size(); ++i) {
         MapItemSavedData.HoldingPlayer mapitemsaveddata$holdingplayer1 = this.carriedBy.get(i);
         String s = mapitemsaveddata$holdingplayer1.player.getName().getString();
         if (!mapitemsaveddata$holdingplayer1.player.isRemoved() && (mapitemsaveddata$holdingplayer1.player.getInventory().contains(p_77920_) || p_77920_.isFramed())) {
            if (!p_77920_.isFramed() && mapitemsaveddata$holdingplayer1.player.level.dimension() == this.dimension && this.trackingPosition) {
               this.addDecoration(MapDecoration.Type.PLAYER, mapitemsaveddata$holdingplayer1.player.level, s, mapitemsaveddata$holdingplayer1.player.getX(), mapitemsaveddata$holdingplayer1.player.getZ(), (double)mapitemsaveddata$holdingplayer1.player.getYRot(), (Component)null);
            }
         } else {
            this.carriedByPlayers.remove(mapitemsaveddata$holdingplayer1.player);
            this.carriedBy.remove(mapitemsaveddata$holdingplayer1);
            this.removeDecoration(s);
         }
      }

      if (p_77920_.isFramed() && this.trackingPosition) {
         ItemFrame itemframe = p_77920_.getFrame();
         BlockPos blockpos = itemframe.getPos();
         MapFrame mapframe1 = this.frameMarkers.get(MapFrame.frameId(blockpos));
         if (mapframe1 != null && itemframe.getId() != mapframe1.getEntityId() && this.frameMarkers.containsKey(mapframe1.getId())) {
            this.removeDecoration("frame-" + mapframe1.getEntityId());
         }

         MapFrame mapframe = new MapFrame(blockpos, itemframe.getDirection().get2DDataValue() * 90, itemframe.getId());
         this.addDecoration(MapDecoration.Type.FRAME, p_77919_.level, "frame-" + itemframe.getId(), (double)blockpos.getX(), (double)blockpos.getZ(), (double)(itemframe.getDirection().get2DDataValue() * 90), (Component)null);
         this.frameMarkers.put(mapframe.getId(), mapframe);
      }

      CompoundTag compoundtag = p_77920_.getTag();
      if (compoundtag != null && compoundtag.contains("Decorations", 9)) {
         ListTag listtag = compoundtag.getList("Decorations", 10);

         for(int j = 0; j < listtag.size(); ++j) {
            CompoundTag compoundtag1 = listtag.getCompound(j);
            if (!this.decorations.containsKey(compoundtag1.getString("id"))) {
               this.addDecoration(MapDecoration.Type.byIcon(compoundtag1.getByte("type")), p_77919_.level, compoundtag1.getString("id"), compoundtag1.getDouble("x"), compoundtag1.getDouble("z"), compoundtag1.getDouble("rot"), (Component)null);
            }
         }
      }

   }

   public void removeDecoration(String p_164800_) {
      MapDecoration mapdecoration = this.decorations.remove(p_164800_);
      if (mapdecoration != null && mapdecoration.getType().shouldTrackCount()) {
         --this.trackedDecorationCount;
      }

      this.setDecorationsDirty();
   }

   public static void addTargetDecoration(ItemStack p_77926_, BlockPos p_77927_, String p_77928_, MapDecoration.Type p_77929_) {
      ListTag listtag;
      if (p_77926_.hasTag() && p_77926_.getTag().contains("Decorations", 9)) {
         listtag = p_77926_.getTag().getList("Decorations", 10);
      } else {
         listtag = new ListTag();
         p_77926_.addTagElement("Decorations", listtag);
      }

      CompoundTag compoundtag = new CompoundTag();
      compoundtag.putByte("type", p_77929_.getIcon());
      compoundtag.putString("id", p_77928_);
      compoundtag.putDouble("x", (double)p_77927_.getX());
      compoundtag.putDouble("z", (double)p_77927_.getZ());
      compoundtag.putDouble("rot", 180.0D);
      listtag.add(compoundtag);
      if (p_77929_.hasMapColor()) {
         CompoundTag compoundtag1 = p_77926_.getOrCreateTagElement("display");
         compoundtag1.putInt("MapColor", p_77929_.getMapColor());
      }

   }

   public void addDecoration(MapDecoration.Type p_77938_, @Nullable LevelAccessor p_77939_, String p_77940_, double p_77941_, double p_77942_, double p_77943_, @Nullable Component p_77944_) {
      int i = 1 << this.scale;
      float f = (float)(p_77941_ - (double)this.x) / (float)i;
      float f1 = (float)(p_77942_ - (double)this.z) / (float)i;
      byte b0 = (byte)((int)((double)(f * 2.0F) + 0.5D));
      byte b1 = (byte)((int)((double)(f1 * 2.0F) + 0.5D));
      int j = 63;
      byte b2;
      if (f >= -63.0F && f1 >= -63.0F && f <= 63.0F && f1 <= 63.0F) {
         p_77943_ += p_77943_ < 0.0D ? -8.0D : 8.0D;
         b2 = (byte)((int)(p_77943_ * 16.0D / 360.0D));
         if (this.dimension == Level.NETHER && p_77939_ != null) {
            int l = (int)(p_77939_.getLevelData().getDayTime() / 10L);
            b2 = (byte)(l * l * 34187121 + l * 121 >> 15 & 15);
         }
      } else {
         if (p_77938_ != MapDecoration.Type.PLAYER) {
            this.removeDecoration(p_77940_);
            return;
         }

         int k = 320;
         if (Math.abs(f) < 320.0F && Math.abs(f1) < 320.0F) {
            p_77938_ = MapDecoration.Type.PLAYER_OFF_MAP;
         } else {
            if (!this.unlimitedTracking) {
               this.removeDecoration(p_77940_);
               return;
            }

            p_77938_ = MapDecoration.Type.PLAYER_OFF_LIMITS;
         }

         b2 = 0;
         if (f <= -63.0F) {
            b0 = -128;
         }

         if (f1 <= -63.0F) {
            b1 = -128;
         }

         if (f >= 63.0F) {
            b0 = 127;
         }

         if (f1 >= 63.0F) {
            b1 = 127;
         }
      }

      MapDecoration mapdecoration1 = new MapDecoration(p_77938_, b0, b1, b2, p_77944_);
      MapDecoration mapdecoration = this.decorations.put(p_77940_, mapdecoration1);
      if (!mapdecoration1.equals(mapdecoration)) {
         if (mapdecoration != null && mapdecoration.getType().shouldTrackCount()) {
            --this.trackedDecorationCount;
         }

         if (p_77938_.shouldTrackCount()) {
            ++this.trackedDecorationCount;
         }

         this.setDecorationsDirty();
      }

   }

   @Nullable
   public Packet<?> getUpdatePacket(int p_164797_, Player p_164798_) {
      MapItemSavedData.HoldingPlayer mapitemsaveddata$holdingplayer = this.carriedByPlayers.get(p_164798_);
      return mapitemsaveddata$holdingplayer == null ? null : mapitemsaveddata$holdingplayer.nextUpdatePacket(p_164797_);
   }

   private void setColorsDirty(int p_164790_, int p_164791_) {
      this.setDirty();

      for(MapItemSavedData.HoldingPlayer mapitemsaveddata$holdingplayer : this.carriedBy) {
         mapitemsaveddata$holdingplayer.markColorsDirty(p_164790_, p_164791_);
      }

   }

   private void setDecorationsDirty() {
      this.setDirty();
      this.carriedBy.forEach(MapItemSavedData.HoldingPlayer::markDecorationsDirty);
   }

   public MapItemSavedData.HoldingPlayer getHoldingPlayer(Player p_77917_) {
      MapItemSavedData.HoldingPlayer mapitemsaveddata$holdingplayer = this.carriedByPlayers.get(p_77917_);
      if (mapitemsaveddata$holdingplayer == null) {
         mapitemsaveddata$holdingplayer = new MapItemSavedData.HoldingPlayer(p_77917_);
         this.carriedByPlayers.put(p_77917_, mapitemsaveddata$holdingplayer);
         this.carriedBy.add(mapitemsaveddata$holdingplayer);
      }

      return mapitemsaveddata$holdingplayer;
   }

   public boolean toggleBanner(LevelAccessor p_77935_, BlockPos p_77936_) {
      double d0 = (double)p_77936_.getX() + 0.5D;
      double d1 = (double)p_77936_.getZ() + 0.5D;
      int i = 1 << this.scale;
      double d2 = (d0 - (double)this.x) / (double)i;
      double d3 = (d1 - (double)this.z) / (double)i;
      int j = 63;
      if (d2 >= -63.0D && d3 >= -63.0D && d2 <= 63.0D && d3 <= 63.0D) {
         MapBanner mapbanner = MapBanner.fromWorld(p_77935_, p_77936_);
         if (mapbanner == null) {
            return false;
         }

         if (this.bannerMarkers.remove(mapbanner.getId(), mapbanner)) {
            this.removeDecoration(mapbanner.getId());
            return true;
         }

         if (!this.isTrackedCountOverLimit(256)) {
            this.bannerMarkers.put(mapbanner.getId(), mapbanner);
            this.addDecoration(mapbanner.getDecoration(), p_77935_, mapbanner.getId(), d0, d1, 180.0D, mapbanner.getName());
            return true;
         }
      }

      return false;
   }

   public void checkBanners(BlockGetter p_77931_, int p_77932_, int p_77933_) {
      Iterator<MapBanner> iterator = this.bannerMarkers.values().iterator();

      while(iterator.hasNext()) {
         MapBanner mapbanner = iterator.next();
         if (mapbanner.getPos().getX() == p_77932_ && mapbanner.getPos().getZ() == p_77933_) {
            MapBanner mapbanner1 = MapBanner.fromWorld(p_77931_, mapbanner.getPos());
            if (!mapbanner.equals(mapbanner1)) {
               iterator.remove();
               this.removeDecoration(mapbanner.getId());
            }
         }
      }

   }

   public Collection<MapBanner> getBanners() {
      return this.bannerMarkers.values();
   }

   public void removedFromFrame(BlockPos p_77948_, int p_77949_) {
      this.removeDecoration("frame-" + p_77949_);
      this.frameMarkers.remove(MapFrame.frameId(p_77948_));
   }

   public boolean updateColor(int p_164793_, int p_164794_, byte p_164795_) {
      byte b0 = this.colors[p_164793_ + p_164794_ * 128];
      if (b0 != p_164795_) {
         this.setColor(p_164793_, p_164794_, p_164795_);
         return true;
      } else {
         return false;
      }
   }

   public void setColor(int p_164804_, int p_164805_, byte p_164806_) {
      this.colors[p_164804_ + p_164805_ * 128] = p_164806_;
      this.setColorsDirty(p_164804_, p_164805_);
   }

   public boolean isExplorationMap() {
      for(MapDecoration mapdecoration : this.decorations.values()) {
         if (mapdecoration.getType() == MapDecoration.Type.MANSION || mapdecoration.getType() == MapDecoration.Type.MONUMENT) {
            return true;
         }
      }

      return false;
   }

   public void addClientSideDecorations(List<MapDecoration> p_164802_) {
      this.decorations.clear();
      this.trackedDecorationCount = 0;

      for(int i = 0; i < p_164802_.size(); ++i) {
         MapDecoration mapdecoration = p_164802_.get(i);
         this.decorations.put("icon-" + i, mapdecoration);
         if (mapdecoration.getType().shouldTrackCount()) {
            ++this.trackedDecorationCount;
         }
      }

   }

   public Iterable<MapDecoration> getDecorations() {
      return this.decorations.values();
   }

   public boolean isTrackedCountOverLimit(int p_181313_) {
      return this.trackedDecorationCount >= p_181313_;
   }

   public class HoldingPlayer {
      public final Player player;
      public boolean dirtyData = true;
      public int minDirtyX;
      public int minDirtyY;
      public int maxDirtyX = 127;
      public int maxDirtyY = 127;
      public boolean dirtyDecorations = true;
      public int tick;
      public int step;

      public HoldingPlayer(Player p_77970_) {
         this.player = p_77970_;
      }

      private MapItemSavedData.MapPatch createPatch() {
         int i = this.minDirtyX;
         int j = this.minDirtyY;
         int k = this.maxDirtyX + 1 - this.minDirtyX;
         int l = this.maxDirtyY + 1 - this.minDirtyY;
         byte[] abyte = new byte[k * l];

         for(int i1 = 0; i1 < k; ++i1) {
            for(int j1 = 0; j1 < l; ++j1) {
               abyte[i1 + j1 * k] = MapItemSavedData.this.colors[i + i1 + (j + j1) * 128];
            }
         }

         return new MapItemSavedData.MapPatch(i, j, k, l, abyte);
      }

      @Nullable
      public Packet<?> nextUpdatePacket(int p_164816_) {
         MapItemSavedData.MapPatch mapitemsaveddata$mappatch;
         if (this.dirtyData) {
            this.dirtyData = false;
            mapitemsaveddata$mappatch = this.createPatch();
         } else {
            mapitemsaveddata$mappatch = null;
         }

         Collection<MapDecoration> collection;
         if (this.dirtyDecorations && this.tick++ % 5 == 0) {
            this.dirtyDecorations = false;
            collection = MapItemSavedData.this.decorations.values();
         } else {
            collection = null;
         }

         return collection == null && mapitemsaveddata$mappatch == null ? null : new ClientboundMapItemDataPacket(p_164816_, MapItemSavedData.this.scale, MapItemSavedData.this.locked, collection, mapitemsaveddata$mappatch);
      }

      public void markColorsDirty(int p_164818_, int p_164819_) {
         if (this.dirtyData) {
            this.minDirtyX = Math.min(this.minDirtyX, p_164818_);
            this.minDirtyY = Math.min(this.minDirtyY, p_164819_);
            this.maxDirtyX = Math.max(this.maxDirtyX, p_164818_);
            this.maxDirtyY = Math.max(this.maxDirtyY, p_164819_);
         } else {
            this.dirtyData = true;
            this.minDirtyX = p_164818_;
            this.minDirtyY = p_164819_;
            this.maxDirtyX = p_164818_;
            this.maxDirtyY = p_164819_;
         }

      }

      public void markDecorationsDirty() {
         this.dirtyDecorations = true;
      }
   }

   public static class MapPatch {
      public final int startX;
      public final int startY;
      public final int width;
      public final int height;
      public final byte[] mapColors;

      public MapPatch(int p_164827_, int p_164828_, int p_164829_, int p_164830_, byte[] p_164831_) {
         this.startX = p_164827_;
         this.startY = p_164828_;
         this.width = p_164829_;
         this.height = p_164830_;
         this.mapColors = p_164831_;
      }

      public void applyToMap(MapItemSavedData p_164833_) {
         for(int i = 0; i < this.width; ++i) {
            for(int j = 0; j < this.height; ++j) {
               p_164833_.setColor(this.startX + i, this.startY + j, this.mapColors[i + j * this.width]);
            }
         }

      }
   }
}