package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.properties.StructureMode;

public class ServerboundSetStructureBlockPacket implements Packet<ServerGamePacketListener> {
   private static final int FLAG_IGNORE_ENTITIES = 1;
   private static final int FLAG_SHOW_AIR = 2;
   private static final int FLAG_SHOW_BOUNDING_BOX = 4;
   private final BlockPos pos;
   private final StructureBlockEntity.UpdateType updateType;
   private final StructureMode mode;
   private final String name;
   private final BlockPos offset;
   private final Vec3i size;
   private final Mirror mirror;
   private final Rotation rotation;
   private final String data;
   private final boolean ignoreEntities;
   private final boolean showAir;
   private final boolean showBoundingBox;
   private final float integrity;
   private final long seed;

   public ServerboundSetStructureBlockPacket(BlockPos p_179771_, StructureBlockEntity.UpdateType p_179772_, StructureMode p_179773_, String p_179774_, BlockPos p_179775_, Vec3i p_179776_, Mirror p_179777_, Rotation p_179778_, String p_179779_, boolean p_179780_, boolean p_179781_, boolean p_179782_, float p_179783_, long p_179784_) {
      this.pos = p_179771_;
      this.updateType = p_179772_;
      this.mode = p_179773_;
      this.name = p_179774_;
      this.offset = p_179775_;
      this.size = p_179776_;
      this.mirror = p_179777_;
      this.rotation = p_179778_;
      this.data = p_179779_;
      this.ignoreEntities = p_179780_;
      this.showAir = p_179781_;
      this.showBoundingBox = p_179782_;
      this.integrity = p_179783_;
      this.seed = p_179784_;
   }

   public ServerboundSetStructureBlockPacket(FriendlyByteBuf p_179786_) {
      this.pos = p_179786_.readBlockPos();
      this.updateType = p_179786_.readEnum(StructureBlockEntity.UpdateType.class);
      this.mode = p_179786_.readEnum(StructureMode.class);
      this.name = p_179786_.readUtf();
      int i = 48;
      this.offset = new BlockPos(Mth.clamp(p_179786_.readByte(), -48, 48), Mth.clamp(p_179786_.readByte(), -48, 48), Mth.clamp(p_179786_.readByte(), -48, 48));
      int j = 48;
      this.size = new Vec3i(Mth.clamp(p_179786_.readByte(), 0, 48), Mth.clamp(p_179786_.readByte(), 0, 48), Mth.clamp(p_179786_.readByte(), 0, 48));
      this.mirror = p_179786_.readEnum(Mirror.class);
      this.rotation = p_179786_.readEnum(Rotation.class);
      this.data = p_179786_.readUtf(128);
      this.integrity = Mth.clamp(p_179786_.readFloat(), 0.0F, 1.0F);
      this.seed = p_179786_.readVarLong();
      int k = p_179786_.readByte();
      this.ignoreEntities = (k & 1) != 0;
      this.showAir = (k & 2) != 0;
      this.showBoundingBox = (k & 4) != 0;
   }

   public void write(FriendlyByteBuf p_134631_) {
      p_134631_.writeBlockPos(this.pos);
      p_134631_.writeEnum(this.updateType);
      p_134631_.writeEnum(this.mode);
      p_134631_.writeUtf(this.name);
      p_134631_.writeByte(this.offset.getX());
      p_134631_.writeByte(this.offset.getY());
      p_134631_.writeByte(this.offset.getZ());
      p_134631_.writeByte(this.size.getX());
      p_134631_.writeByte(this.size.getY());
      p_134631_.writeByte(this.size.getZ());
      p_134631_.writeEnum(this.mirror);
      p_134631_.writeEnum(this.rotation);
      p_134631_.writeUtf(this.data);
      p_134631_.writeFloat(this.integrity);
      p_134631_.writeVarLong(this.seed);
      int i = 0;
      if (this.ignoreEntities) {
         i |= 1;
      }

      if (this.showAir) {
         i |= 2;
      }

      if (this.showBoundingBox) {
         i |= 4;
      }

      p_134631_.writeByte(i);
   }

   public void handle(ServerGamePacketListener p_134628_) {
      p_134628_.handleSetStructureBlock(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public StructureBlockEntity.UpdateType getUpdateType() {
      return this.updateType;
   }

   public StructureMode getMode() {
      return this.mode;
   }

   public String getName() {
      return this.name;
   }

   public BlockPos getOffset() {
      return this.offset;
   }

   public Vec3i getSize() {
      return this.size;
   }

   public Mirror getMirror() {
      return this.mirror;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public String getData() {
      return this.data;
   }

   public boolean isIgnoreEntities() {
      return this.ignoreEntities;
   }

   public boolean isShowAir() {
      return this.showAir;
   }

   public boolean isShowBoundingBox() {
      return this.showBoundingBox;
   }

   public float getIntegrity() {
      return this.integrity;
   }

   public long getSeed() {
      return this.seed;
   }
}