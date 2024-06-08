package net.minecraft.world.level.border;

import com.google.common.collect.Lists;
import com.mojang.serialization.DynamicLike;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WorldBorder {
   public static final double MAX_SIZE = 5.9999968E7D;
   public static final double MAX_CENTER_COORDINATE = 2.9999984E7D;
   private final List<BorderChangeListener> listeners = Lists.newArrayList();
   private double damagePerBlock = 0.2D;
   private double damageSafeZone = 5.0D;
   private int warningTime = 15;
   private int warningBlocks = 5;
   private double centerX;
   private double centerZ;
   int absoluteMaxSize = 29999984;
   private WorldBorder.BorderExtent extent = new WorldBorder.StaticBorderExtent(5.9999968E7D);
   public static final WorldBorder.Settings DEFAULT_SETTINGS = new WorldBorder.Settings(0.0D, 0.0D, 0.2D, 5.0D, 5, 15, 5.9999968E7D, 0L, 0.0D);

   public boolean isWithinBounds(BlockPos p_61938_) {
      return (double)(p_61938_.getX() + 1) > this.getMinX() && (double)p_61938_.getX() < this.getMaxX() && (double)(p_61938_.getZ() + 1) > this.getMinZ() && (double)p_61938_.getZ() < this.getMaxZ();
   }

   public boolean isWithinBounds(ChunkPos p_61928_) {
      return (double)p_61928_.getMaxBlockX() > this.getMinX() && (double)p_61928_.getMinBlockX() < this.getMaxX() && (double)p_61928_.getMaxBlockZ() > this.getMinZ() && (double)p_61928_.getMinBlockZ() < this.getMaxZ();
   }

   public boolean isWithinBounds(double p_156094_, double p_156095_) {
      return p_156094_ > this.getMinX() && p_156094_ < this.getMaxX() && p_156095_ > this.getMinZ() && p_156095_ < this.getMaxZ();
   }

   public boolean isWithinBounds(double p_187563_, double p_187564_, double p_187565_) {
      return p_187563_ > this.getMinX() - p_187565_ && p_187563_ < this.getMaxX() + p_187565_ && p_187564_ > this.getMinZ() - p_187565_ && p_187564_ < this.getMaxZ() + p_187565_;
   }

   public boolean isWithinBounds(AABB p_61936_) {
      return p_61936_.maxX > this.getMinX() && p_61936_.minX < this.getMaxX() && p_61936_.maxZ > this.getMinZ() && p_61936_.minZ < this.getMaxZ();
   }

   public BlockPos clampToBounds(double p_187570_, double p_187571_, double p_187572_) {
      return new BlockPos(Mth.clamp(p_187570_, this.getMinX(), this.getMaxX()), p_187571_, Mth.clamp(p_187572_, this.getMinZ(), this.getMaxZ()));
   }

   public double getDistanceToBorder(Entity p_61926_) {
      return this.getDistanceToBorder(p_61926_.getX(), p_61926_.getZ());
   }

   public VoxelShape getCollisionShape() {
      return this.extent.getCollisionShape();
   }

   public double getDistanceToBorder(double p_61942_, double p_61943_) {
      double d0 = p_61943_ - this.getMinZ();
      double d1 = this.getMaxZ() - p_61943_;
      double d2 = p_61942_ - this.getMinX();
      double d3 = this.getMaxX() - p_61942_;
      double d4 = Math.min(d2, d3);
      d4 = Math.min(d4, d0);
      return Math.min(d4, d1);
   }

   public boolean isInsideCloseToBorder(Entity p_187567_, AABB p_187568_) {
      double d0 = Math.max(Mth.absMax(p_187568_.getXsize(), p_187568_.getZsize()), 1.0D);
      return this.getDistanceToBorder(p_187567_) < d0 * 2.0D && this.isWithinBounds(p_187567_.getX(), p_187567_.getZ(), d0);
   }

   public BorderStatus getStatus() {
      return this.extent.getStatus();
   }

   public double getMinX() {
      return this.extent.getMinX();
   }

   public double getMinZ() {
      return this.extent.getMinZ();
   }

   public double getMaxX() {
      return this.extent.getMaxX();
   }

   public double getMaxZ() {
      return this.extent.getMaxZ();
   }

   public double getCenterX() {
      return this.centerX;
   }

   public double getCenterZ() {
      return this.centerZ;
   }

   public void setCenter(double p_61950_, double p_61951_) {
      this.centerX = p_61950_;
      this.centerZ = p_61951_;
      this.extent.onCenterChange();

      for(BorderChangeListener borderchangelistener : this.getListeners()) {
         borderchangelistener.onBorderCenterSet(this, p_61950_, p_61951_);
      }

   }

   public double getSize() {
      return this.extent.getSize();
   }

   public long getLerpRemainingTime() {
      return this.extent.getLerpRemainingTime();
   }

   public double getLerpTarget() {
      return this.extent.getLerpTarget();
   }

   public void setSize(double p_61918_) {
      this.extent = new WorldBorder.StaticBorderExtent(p_61918_);

      for(BorderChangeListener borderchangelistener : this.getListeners()) {
         borderchangelistener.onBorderSizeSet(this, p_61918_);
      }

   }

   public void lerpSizeBetween(double p_61920_, double p_61921_, long p_61922_) {
      this.extent = (WorldBorder.BorderExtent)(p_61920_ == p_61921_ ? new WorldBorder.StaticBorderExtent(p_61921_) : new WorldBorder.MovingBorderExtent(p_61920_, p_61921_, p_61922_));

      for(BorderChangeListener borderchangelistener : this.getListeners()) {
         borderchangelistener.onBorderSizeLerping(this, p_61920_, p_61921_, p_61922_);
      }

   }

   protected List<BorderChangeListener> getListeners() {
      return Lists.newArrayList(this.listeners);
   }

   public void addListener(BorderChangeListener p_61930_) {
      this.listeners.add(p_61930_);
   }

   public void removeListener(BorderChangeListener p_156097_) {
      this.listeners.remove(p_156097_);
   }

   public void setAbsoluteMaxSize(int p_61924_) {
      this.absoluteMaxSize = p_61924_;
      this.extent.onAbsoluteMaxSizeChange();
   }

   public int getAbsoluteMaxSize() {
      return this.absoluteMaxSize;
   }

   public double getDamageSafeZone() {
      return this.damageSafeZone;
   }

   public void setDamageSafeZone(double p_61940_) {
      this.damageSafeZone = p_61940_;

      for(BorderChangeListener borderchangelistener : this.getListeners()) {
         borderchangelistener.onBorderSetDamageSafeZOne(this, p_61940_);
      }

   }

   public double getDamagePerBlock() {
      return this.damagePerBlock;
   }

   public void setDamagePerBlock(double p_61948_) {
      this.damagePerBlock = p_61948_;

      for(BorderChangeListener borderchangelistener : this.getListeners()) {
         borderchangelistener.onBorderSetDamagePerBlock(this, p_61948_);
      }

   }

   public double getLerpSpeed() {
      return this.extent.getLerpSpeed();
   }

   public int getWarningTime() {
      return this.warningTime;
   }

   public void setWarningTime(int p_61945_) {
      this.warningTime = p_61945_;

      for(BorderChangeListener borderchangelistener : this.getListeners()) {
         borderchangelistener.onBorderSetWarningTime(this, p_61945_);
      }

   }

   public int getWarningBlocks() {
      return this.warningBlocks;
   }

   public void setWarningBlocks(int p_61953_) {
      this.warningBlocks = p_61953_;

      for(BorderChangeListener borderchangelistener : this.getListeners()) {
         borderchangelistener.onBorderSetWarningBlocks(this, p_61953_);
      }

   }

   public void tick() {
      this.extent = this.extent.update();
   }

   public WorldBorder.Settings createSettings() {
      return new WorldBorder.Settings(this);
   }

   public void applySettings(WorldBorder.Settings p_61932_) {
      this.setCenter(p_61932_.getCenterX(), p_61932_.getCenterZ());
      this.setDamagePerBlock(p_61932_.getDamagePerBlock());
      this.setDamageSafeZone(p_61932_.getSafeZone());
      this.setWarningBlocks(p_61932_.getWarningBlocks());
      this.setWarningTime(p_61932_.getWarningTime());
      if (p_61932_.getSizeLerpTime() > 0L) {
         this.lerpSizeBetween(p_61932_.getSize(), p_61932_.getSizeLerpTarget(), p_61932_.getSizeLerpTime());
      } else {
         this.setSize(p_61932_.getSize());
      }

   }

   interface BorderExtent {
      double getMinX();

      double getMaxX();

      double getMinZ();

      double getMaxZ();

      double getSize();

      double getLerpSpeed();

      long getLerpRemainingTime();

      double getLerpTarget();

      BorderStatus getStatus();

      void onAbsoluteMaxSizeChange();

      void onCenterChange();

      WorldBorder.BorderExtent update();

      VoxelShape getCollisionShape();
   }

   class MovingBorderExtent implements WorldBorder.BorderExtent {
      private final double from;
      private final double to;
      private final long lerpEnd;
      private final long lerpBegin;
      private final double lerpDuration;

      MovingBorderExtent(double p_61979_, double p_61980_, long p_61981_) {
         this.from = p_61979_;
         this.to = p_61980_;
         this.lerpDuration = (double)p_61981_;
         this.lerpBegin = Util.getMillis();
         this.lerpEnd = this.lerpBegin + p_61981_;
      }

      public double getMinX() {
         return Mth.clamp(WorldBorder.this.getCenterX() - this.getSize() / 2.0D, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
      }

      public double getMinZ() {
         return Mth.clamp(WorldBorder.this.getCenterZ() - this.getSize() / 2.0D, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
      }

      public double getMaxX() {
         return Mth.clamp(WorldBorder.this.getCenterX() + this.getSize() / 2.0D, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
      }

      public double getMaxZ() {
         return Mth.clamp(WorldBorder.this.getCenterZ() + this.getSize() / 2.0D, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
      }

      public double getSize() {
         double d0 = (double)(Util.getMillis() - this.lerpBegin) / this.lerpDuration;
         return d0 < 1.0D ? Mth.lerp(d0, this.from, this.to) : this.to;
      }

      public double getLerpSpeed() {
         return Math.abs(this.from - this.to) / (double)(this.lerpEnd - this.lerpBegin);
      }

      public long getLerpRemainingTime() {
         return this.lerpEnd - Util.getMillis();
      }

      public double getLerpTarget() {
         return this.to;
      }

      public BorderStatus getStatus() {
         return this.to < this.from ? BorderStatus.SHRINKING : BorderStatus.GROWING;
      }

      public void onCenterChange() {
      }

      public void onAbsoluteMaxSizeChange() {
      }

      public WorldBorder.BorderExtent update() {
         return (WorldBorder.BorderExtent)(this.getLerpRemainingTime() <= 0L ? WorldBorder.this.new StaticBorderExtent(this.to) : this);
      }

      public VoxelShape getCollisionShape() {
         return Shapes.join(Shapes.INFINITY, Shapes.box(Math.floor(this.getMinX()), Double.NEGATIVE_INFINITY, Math.floor(this.getMinZ()), Math.ceil(this.getMaxX()), Double.POSITIVE_INFINITY, Math.ceil(this.getMaxZ())), BooleanOp.ONLY_FIRST);
      }
   }

   public static class Settings {
      private final double centerX;
      private final double centerZ;
      private final double damagePerBlock;
      private final double safeZone;
      private final int warningBlocks;
      private final int warningTime;
      private final double size;
      private final long sizeLerpTime;
      private final double sizeLerpTarget;

      Settings(double p_62011_, double p_62012_, double p_62013_, double p_62014_, int p_62015_, int p_62016_, double p_62017_, long p_62018_, double p_62019_) {
         this.centerX = p_62011_;
         this.centerZ = p_62012_;
         this.damagePerBlock = p_62013_;
         this.safeZone = p_62014_;
         this.warningBlocks = p_62015_;
         this.warningTime = p_62016_;
         this.size = p_62017_;
         this.sizeLerpTime = p_62018_;
         this.sizeLerpTarget = p_62019_;
      }

      Settings(WorldBorder p_62032_) {
         this.centerX = p_62032_.getCenterX();
         this.centerZ = p_62032_.getCenterZ();
         this.damagePerBlock = p_62032_.getDamagePerBlock();
         this.safeZone = p_62032_.getDamageSafeZone();
         this.warningBlocks = p_62032_.getWarningBlocks();
         this.warningTime = p_62032_.getWarningTime();
         this.size = p_62032_.getSize();
         this.sizeLerpTime = p_62032_.getLerpRemainingTime();
         this.sizeLerpTarget = p_62032_.getLerpTarget();
      }

      public double getCenterX() {
         return this.centerX;
      }

      public double getCenterZ() {
         return this.centerZ;
      }

      public double getDamagePerBlock() {
         return this.damagePerBlock;
      }

      public double getSafeZone() {
         return this.safeZone;
      }

      public int getWarningBlocks() {
         return this.warningBlocks;
      }

      public int getWarningTime() {
         return this.warningTime;
      }

      public double getSize() {
         return this.size;
      }

      public long getSizeLerpTime() {
         return this.sizeLerpTime;
      }

      public double getSizeLerpTarget() {
         return this.sizeLerpTarget;
      }

      public static WorldBorder.Settings read(DynamicLike<?> p_62038_, WorldBorder.Settings p_62039_) {
         double d0 = Mth.clamp(p_62038_.get("BorderCenterX").asDouble(p_62039_.centerX), -2.9999984E7D, 2.9999984E7D);
         double d1 = Mth.clamp(p_62038_.get("BorderCenterZ").asDouble(p_62039_.centerZ), -2.9999984E7D, 2.9999984E7D);
         double d2 = p_62038_.get("BorderSize").asDouble(p_62039_.size);
         long i = p_62038_.get("BorderSizeLerpTime").asLong(p_62039_.sizeLerpTime);
         double d3 = p_62038_.get("BorderSizeLerpTarget").asDouble(p_62039_.sizeLerpTarget);
         double d4 = p_62038_.get("BorderSafeZone").asDouble(p_62039_.safeZone);
         double d5 = p_62038_.get("BorderDamagePerBlock").asDouble(p_62039_.damagePerBlock);
         int j = p_62038_.get("BorderWarningBlocks").asInt(p_62039_.warningBlocks);
         int k = p_62038_.get("BorderWarningTime").asInt(p_62039_.warningTime);
         return new WorldBorder.Settings(d0, d1, d5, d4, j, k, d2, i, d3);
      }

      public void write(CompoundTag p_62041_) {
         p_62041_.putDouble("BorderCenterX", this.centerX);
         p_62041_.putDouble("BorderCenterZ", this.centerZ);
         p_62041_.putDouble("BorderSize", this.size);
         p_62041_.putLong("BorderSizeLerpTime", this.sizeLerpTime);
         p_62041_.putDouble("BorderSafeZone", this.safeZone);
         p_62041_.putDouble("BorderDamagePerBlock", this.damagePerBlock);
         p_62041_.putDouble("BorderSizeLerpTarget", this.sizeLerpTarget);
         p_62041_.putDouble("BorderWarningBlocks", (double)this.warningBlocks);
         p_62041_.putDouble("BorderWarningTime", (double)this.warningTime);
      }
   }

   class StaticBorderExtent implements WorldBorder.BorderExtent {
      private final double size;
      private double minX;
      private double minZ;
      private double maxX;
      private double maxZ;
      private VoxelShape shape;

      public StaticBorderExtent(double p_62059_) {
         this.size = p_62059_;
         this.updateBox();
      }

      public double getMinX() {
         return this.minX;
      }

      public double getMaxX() {
         return this.maxX;
      }

      public double getMinZ() {
         return this.minZ;
      }

      public double getMaxZ() {
         return this.maxZ;
      }

      public double getSize() {
         return this.size;
      }

      public BorderStatus getStatus() {
         return BorderStatus.STATIONARY;
      }

      public double getLerpSpeed() {
         return 0.0D;
      }

      public long getLerpRemainingTime() {
         return 0L;
      }

      public double getLerpTarget() {
         return this.size;
      }

      private void updateBox() {
         this.minX = Mth.clamp(WorldBorder.this.getCenterX() - this.size / 2.0D, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
         this.minZ = Mth.clamp(WorldBorder.this.getCenterZ() - this.size / 2.0D, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
         this.maxX = Mth.clamp(WorldBorder.this.getCenterX() + this.size / 2.0D, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
         this.maxZ = Mth.clamp(WorldBorder.this.getCenterZ() + this.size / 2.0D, (double)(-WorldBorder.this.absoluteMaxSize), (double)WorldBorder.this.absoluteMaxSize);
         this.shape = Shapes.join(Shapes.INFINITY, Shapes.box(Math.floor(this.getMinX()), Double.NEGATIVE_INFINITY, Math.floor(this.getMinZ()), Math.ceil(this.getMaxX()), Double.POSITIVE_INFINITY, Math.ceil(this.getMaxZ())), BooleanOp.ONLY_FIRST);
      }

      public void onAbsoluteMaxSizeChange() {
         this.updateBox();
      }

      public void onCenterChange() {
         this.updateBox();
      }

      public WorldBorder.BorderExtent update() {
         return this;
      }

      public VoxelShape getCollisionShape() {
         return this.shape;
      }
   }
}