package net.minecraft.network;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FriendlyByteBuf extends ByteBuf implements net.minecraftforge.common.extensions.IForgeFriendlyByteBuf {
   private static final int MAX_VARINT_SIZE = 5;
   private static final int MAX_VARLONG_SIZE = 10;
   private static final int DEFAULT_NBT_QUOTA = 2097152;
   private final ByteBuf source;
   public static final short MAX_STRING_LENGTH = 32767;
   public static final int MAX_COMPONENT_STRING_LENGTH = 262144;

   public FriendlyByteBuf(ByteBuf p_130051_) {
      this.source = p_130051_;
   }

   public static int getVarIntSize(int p_130054_) {
      for(int i = 1; i < 5; ++i) {
         if ((p_130054_ & -1 << i * 7) == 0) {
            return i;
         }
      }

      return 5;
   }

   public static int getVarLongSize(long p_178340_) {
      for(int i = 1; i < 10; ++i) {
         if ((p_178340_ & -1L << i * 7) == 0L) {
            return i;
         }
      }

      return 10;
   }

   public <T> T readWithCodec(Codec<T> p_130058_) {
      CompoundTag compoundtag = this.readAnySizeNbt();
      DataResult<T> dataresult = p_130058_.parse(NbtOps.INSTANCE, compoundtag);
      dataresult.error().ifPresent((p_178380_) -> {
         throw new EncoderException("Failed to decode: " + p_178380_.message() + " " + compoundtag);
      });
      return dataresult.result().get();
   }

   public <T> void writeWithCodec(Codec<T> p_130060_, T p_130061_) {
      DataResult<Tag> dataresult = p_130060_.encodeStart(NbtOps.INSTANCE, p_130061_);
      dataresult.error().ifPresent((p_178349_) -> {
         throw new EncoderException("Failed to encode: " + p_178349_.message() + " " + p_130061_);
      });
      this.writeNbt((CompoundTag)dataresult.result().get());
   }

   public static <T> IntFunction<T> limitValue(IntFunction<T> p_182696_, int p_182697_) {
      return (p_182686_) -> {
         if (p_182686_ > p_182697_) {
            throw new DecoderException("Value " + p_182686_ + " is larger than limit " + p_182697_);
         } else {
            return p_182696_.apply(p_182686_);
         }
      };
   }

   public <T, C extends Collection<T>> C readCollection(IntFunction<C> p_178372_, Function<FriendlyByteBuf, T> p_178373_) {
      int i = this.readVarInt();
      C c = p_178372_.apply(i);

      for(int j = 0; j < i; ++j) {
         c.add(p_178373_.apply(this));
      }

      return c;
   }

   public <T> void writeCollection(Collection<T> p_178353_, BiConsumer<FriendlyByteBuf, T> p_178354_) {
      this.writeVarInt(p_178353_.size());

      for(T t : p_178353_) {
         p_178354_.accept(this, t);
      }

   }

   public <T> List<T> readList(Function<FriendlyByteBuf, T> p_178367_) {
      return this.readCollection(Lists::newArrayListWithCapacity, p_178367_);
   }

   public IntList readIntIdList() {
      int i = this.readVarInt();
      IntList intlist = new IntArrayList();

      for(int j = 0; j < i; ++j) {
         intlist.add(this.readVarInt());
      }

      return intlist;
   }

   public void writeIntIdList(IntList p_178346_) {
      this.writeVarInt(p_178346_.size());
      p_178346_.forEach((java.util.function.IntConsumer)this::writeVarInt);
   }

   public <K, V, M extends Map<K, V>> M readMap(IntFunction<M> p_178375_, Function<FriendlyByteBuf, K> p_178376_, Function<FriendlyByteBuf, V> p_178377_) {
      int i = this.readVarInt();
      M m = p_178375_.apply(i);

      for(int j = 0; j < i; ++j) {
         K k = p_178376_.apply(this);
         V v = p_178377_.apply(this);
         m.put(k, v);
      }

      return m;
   }

   public <K, V> Map<K, V> readMap(Function<FriendlyByteBuf, K> p_178369_, Function<FriendlyByteBuf, V> p_178370_) {
      return this.readMap(Maps::newHashMapWithExpectedSize, p_178369_, p_178370_);
   }

   public <K, V> void writeMap(Map<K, V> p_178356_, BiConsumer<FriendlyByteBuf, K> p_178357_, BiConsumer<FriendlyByteBuf, V> p_178358_) {
      this.writeVarInt(p_178356_.size());
      p_178356_.forEach((p_178362_, p_178363_) -> {
         p_178357_.accept(this, p_178362_);
         p_178358_.accept(this, p_178363_);
      });
   }

   public void readWithCount(Consumer<FriendlyByteBuf> p_178365_) {
      int i = this.readVarInt();

      for(int j = 0; j < i; ++j) {
         p_178365_.accept(this);
      }

   }

   public <T> void writeOptional(Optional<T> p_182688_, BiConsumer<FriendlyByteBuf, T> p_182689_) {
      if (p_182688_.isPresent()) {
         this.writeBoolean(true);
         p_182689_.accept(this, p_182688_.get());
      } else {
         this.writeBoolean(false);
      }

   }

   public <T> Optional<T> readOptional(Function<FriendlyByteBuf, T> p_182699_) {
      return this.readBoolean() ? Optional.of(p_182699_.apply(this)) : Optional.empty();
   }

   public byte[] readByteArray() {
      return this.readByteArray(this.readableBytes());
   }

   public FriendlyByteBuf writeByteArray(byte[] p_130088_) {
      this.writeVarInt(p_130088_.length);
      this.writeBytes(p_130088_);
      return this;
   }

   public byte[] readByteArray(int p_130102_) {
      int i = this.readVarInt();
      if (i > p_130102_) {
         throw new DecoderException("ByteArray with size " + i + " is bigger than allowed " + p_130102_);
      } else {
         byte[] abyte = new byte[i];
         this.readBytes(abyte);
         return abyte;
      }
   }

   public FriendlyByteBuf writeVarIntArray(int[] p_130090_) {
      this.writeVarInt(p_130090_.length);

      for(int i : p_130090_) {
         this.writeVarInt(i);
      }

      return this;
   }

   public int[] readVarIntArray() {
      return this.readVarIntArray(this.readableBytes());
   }

   public int[] readVarIntArray(int p_130117_) {
      int i = this.readVarInt();
      if (i > p_130117_) {
         throw new DecoderException("VarIntArray with size " + i + " is bigger than allowed " + p_130117_);
      } else {
         int[] aint = new int[i];

         for(int j = 0; j < aint.length; ++j) {
            aint[j] = this.readVarInt();
         }

         return aint;
      }
   }

   public FriendlyByteBuf writeLongArray(long[] p_130092_) {
      this.writeVarInt(p_130092_.length);

      for(long i : p_130092_) {
         this.writeLong(i);
      }

      return this;
   }

   public long[] readLongArray() {
      return this.readLongArray((long[])null);
   }

   public long[] readLongArray(@Nullable long[] p_130106_) {
      return this.readLongArray(p_130106_, this.readableBytes() / 8);
   }

   public long[] readLongArray(@Nullable long[] p_130094_, int p_130095_) {
      int i = this.readVarInt();
      if (p_130094_ == null || p_130094_.length != i) {
         if (i > p_130095_) {
            throw new DecoderException("LongArray with size " + i + " is bigger than allowed " + p_130095_);
         }

         p_130094_ = new long[i];
      }

      for(int j = 0; j < p_130094_.length; ++j) {
         p_130094_[j] = this.readLong();
      }

      return p_130094_;
   }

   @VisibleForTesting
   public byte[] accessByteBufWithCorrectSize() {
      int i = this.writerIndex();
      byte[] abyte = new byte[i];
      this.getBytes(0, abyte);
      return abyte;
   }

   public BlockPos readBlockPos() {
      return BlockPos.of(this.readLong());
   }

   public FriendlyByteBuf writeBlockPos(BlockPos p_130065_) {
      this.writeLong(p_130065_.asLong());
      return this;
   }

   public ChunkPos readChunkPos() {
      return new ChunkPos(this.readLong());
   }

   public FriendlyByteBuf writeChunkPos(ChunkPos p_178342_) {
      this.writeLong(p_178342_.toLong());
      return this;
   }

   public SectionPos readSectionPos() {
      return SectionPos.of(this.readLong());
   }

   public FriendlyByteBuf writeSectionPos(SectionPos p_178344_) {
      this.writeLong(p_178344_.asLong());
      return this;
   }

   public Component readComponent() {
      return Component.Serializer.fromJson(this.readUtf(262144));
   }

   public FriendlyByteBuf writeComponent(Component p_130084_) {
      return this.writeUtf(Component.Serializer.toJson(p_130084_), 262144);
   }

   public <T extends Enum<T>> T readEnum(Class<T> p_130067_) {
      return (p_130067_.getEnumConstants())[this.readVarInt()];
   }

   public FriendlyByteBuf writeEnum(Enum<?> p_130069_) {
      return this.writeVarInt(p_130069_.ordinal());
   }

   public int readVarInt() {
      int i = 0;
      int j = 0;

      byte b0;
      do {
         b0 = this.readByte();
         i |= (b0 & 127) << j++ * 7;
         if (j > 5) {
            throw new RuntimeException("VarInt too big");
         }
      } while((b0 & 128) == 128);

      return i;
   }

   public long readVarLong() {
      long i = 0L;
      int j = 0;

      byte b0;
      do {
         b0 = this.readByte();
         i |= (long)(b0 & 127) << j++ * 7;
         if (j > 10) {
            throw new RuntimeException("VarLong too big");
         }
      } while((b0 & 128) == 128);

      return i;
   }

   public FriendlyByteBuf writeUUID(UUID p_130078_) {
      this.writeLong(p_130078_.getMostSignificantBits());
      this.writeLong(p_130078_.getLeastSignificantBits());
      return this;
   }

   public UUID readUUID() {
      return new UUID(this.readLong(), this.readLong());
   }

   public FriendlyByteBuf writeVarInt(int p_130131_) {
      while((p_130131_ & -128) != 0) {
         this.writeByte(p_130131_ & 127 | 128);
         p_130131_ >>>= 7;
      }

      this.writeByte(p_130131_);
      return this;
   }

   public FriendlyByteBuf writeVarLong(long p_130104_) {
      while((p_130104_ & -128L) != 0L) {
         this.writeByte((int)(p_130104_ & 127L) | 128);
         p_130104_ >>>= 7;
      }

      this.writeByte((int)p_130104_);
      return this;
   }

   public FriendlyByteBuf writeNbt(@Nullable CompoundTag p_130080_) {
      if (p_130080_ == null) {
         this.writeByte(0);
      } else {
         try {
            NbtIo.write(p_130080_, new ByteBufOutputStream(this));
         } catch (IOException ioexception) {
            throw new EncoderException(ioexception);
         }
      }

      return this;
   }

   @Nullable
   public CompoundTag readNbt() {
      return this.readNbt(new NbtAccounter(2097152L));
   }

   @Nullable
   public CompoundTag readAnySizeNbt() {
      return this.readNbt(NbtAccounter.UNLIMITED);
   }

   @Nullable
   public CompoundTag readNbt(NbtAccounter p_130082_) {
      int i = this.readerIndex();
      byte b0 = this.readByte();
      if (b0 == 0) {
         return null;
      } else {
         this.readerIndex(i);

         try {
            return NbtIo.read(new ByteBufInputStream(this), p_130082_);
         } catch (IOException ioexception) {
            throw new EncoderException(ioexception);
         }
      }
   }

   public FriendlyByteBuf writeItem(ItemStack p_130056_) {
      return writeItemStack(p_130056_, true);
   }

   /**
    * Most ItemStack serialization is Server to Client,and doesn't need to know the FULL tag details.
    * One exception is items from the creative menu, which must be sent from Client to Server with their full NBT.
    * If you want to send the FULL tag set limitedTag to false
    */
   public FriendlyByteBuf writeItemStack(ItemStack p_130056_, boolean limitedTag) {
      if (p_130056_.isEmpty()) {
         this.writeBoolean(false);
      } else {
         this.writeBoolean(true);
         Item item = p_130056_.getItem();
         this.writeVarInt(Item.getId(item));
         this.writeByte(p_130056_.getCount());
         CompoundTag compoundtag = null;
         if (item.isDamageable(p_130056_) || item.shouldOverrideMultiplayerNbt()) {
            compoundtag = limitedTag ? p_130056_.getShareTag() : p_130056_.getTag();
         }

         this.writeNbt(compoundtag);
      }

      return this;
   }

   public ItemStack readItem() {
      if (!this.readBoolean()) {
         return ItemStack.EMPTY;
      } else {
         int i = this.readVarInt();
         int j = this.readByte();
         ItemStack itemstack = new ItemStack(Item.byId(i), j);
         itemstack.readShareTag(this.readNbt());
         return itemstack;
      }
   }

   public String readUtf() {
      return this.readUtf(32767);
   }

   public String readUtf(int p_130137_) {
      int i = this.readVarInt();
      if (i > p_130137_ * 4) {
         throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + p_130137_ * 4 + ")");
      } else if (i < 0) {
         throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
      } else {
         String s = this.toString(this.readerIndex(), i, StandardCharsets.UTF_8);
         this.readerIndex(this.readerIndex() + i);
         if (s.length() > p_130137_) {
            throw new DecoderException("The received string length is longer than maximum allowed (" + i + " > " + p_130137_ + ")");
         } else {
            return s;
         }
      }
   }

   public FriendlyByteBuf writeUtf(String p_130071_) {
      return this.writeUtf(p_130071_, 32767);
   }

   public FriendlyByteBuf writeUtf(String p_130073_, int p_130074_) {
      byte[] abyte = p_130073_.getBytes(StandardCharsets.UTF_8);
      if (abyte.length > p_130074_) {
         throw new EncoderException("String too big (was " + abyte.length + " bytes encoded, max " + p_130074_ + ")");
      } else {
         this.writeVarInt(abyte.length);
         this.writeBytes(abyte);
         return this;
      }
   }

   public ResourceLocation readResourceLocation() {
      return new ResourceLocation(this.readUtf(32767));
   }

   public FriendlyByteBuf writeResourceLocation(ResourceLocation p_130086_) {
      this.writeUtf(p_130086_.toString());
      return this;
   }

   public Date readDate() {
      return new Date(this.readLong());
   }

   public FriendlyByteBuf writeDate(Date p_130076_) {
      this.writeLong(p_130076_.getTime());
      return this;
   }

   public BlockHitResult readBlockHitResult() {
      BlockPos blockpos = this.readBlockPos();
      Direction direction = this.readEnum(Direction.class);
      float f = this.readFloat();
      float f1 = this.readFloat();
      float f2 = this.readFloat();
      boolean flag = this.readBoolean();
      return new BlockHitResult(new Vec3((double)blockpos.getX() + (double)f, (double)blockpos.getY() + (double)f1, (double)blockpos.getZ() + (double)f2), direction, blockpos, flag);
   }

   public void writeBlockHitResult(BlockHitResult p_130063_) {
      BlockPos blockpos = p_130063_.getBlockPos();
      this.writeBlockPos(blockpos);
      this.writeEnum(p_130063_.getDirection());
      Vec3 vec3 = p_130063_.getLocation();
      this.writeFloat((float)(vec3.x - (double)blockpos.getX()));
      this.writeFloat((float)(vec3.y - (double)blockpos.getY()));
      this.writeFloat((float)(vec3.z - (double)blockpos.getZ()));
      this.writeBoolean(p_130063_.isInside());
   }

   public BitSet readBitSet() {
      return BitSet.valueOf(this.readLongArray());
   }

   public void writeBitSet(BitSet p_178351_) {
      this.writeLongArray(p_178351_.toLongArray());
   }

   public int capacity() {
      return this.source.capacity();
   }

   public ByteBuf capacity(int p_130120_) {
      return this.source.capacity(p_130120_);
   }

   public int maxCapacity() {
      return this.source.maxCapacity();
   }

   public ByteBufAllocator alloc() {
      return this.source.alloc();
   }

   public ByteOrder order() {
      return this.source.order();
   }

   public ByteBuf order(ByteOrder p_130280_) {
      return this.source.order(p_130280_);
   }

   public ByteBuf unwrap() {
      return this.source.unwrap();
   }

   public boolean isDirect() {
      return this.source.isDirect();
   }

   public boolean isReadOnly() {
      return this.source.isReadOnly();
   }

   public ByteBuf asReadOnly() {
      return this.source.asReadOnly();
   }

   public int readerIndex() {
      return this.source.readerIndex();
   }

   public ByteBuf readerIndex(int p_130343_) {
      return this.source.readerIndex(p_130343_);
   }

   public int writerIndex() {
      return this.source.writerIndex();
   }

   public ByteBuf writerIndex(int p_130527_) {
      return this.source.writerIndex(p_130527_);
   }

   public ByteBuf setIndex(int p_130417_, int p_130418_) {
      return this.source.setIndex(p_130417_, p_130418_);
   }

   public int readableBytes() {
      return this.source.readableBytes();
   }

   public int writableBytes() {
      return this.source.writableBytes();
   }

   public int maxWritableBytes() {
      return this.source.maxWritableBytes();
   }

   public boolean isReadable() {
      return this.source.isReadable();
   }

   public boolean isReadable(int p_130254_) {
      return this.source.isReadable(p_130254_);
   }

   public boolean isWritable() {
      return this.source.isWritable();
   }

   public boolean isWritable(int p_130257_) {
      return this.source.isWritable(p_130257_);
   }

   public ByteBuf clear() {
      return this.source.clear();
   }

   public ByteBuf markReaderIndex() {
      return this.source.markReaderIndex();
   }

   public ByteBuf resetReaderIndex() {
      return this.source.resetReaderIndex();
   }

   public ByteBuf markWriterIndex() {
      return this.source.markWriterIndex();
   }

   public ByteBuf resetWriterIndex() {
      return this.source.resetWriterIndex();
   }

   public ByteBuf discardReadBytes() {
      return this.source.discardReadBytes();
   }

   public ByteBuf discardSomeReadBytes() {
      return this.source.discardSomeReadBytes();
   }

   public ByteBuf ensureWritable(int p_130139_) {
      return this.source.ensureWritable(p_130139_);
   }

   public int ensureWritable(int p_130141_, boolean p_130142_) {
      return this.source.ensureWritable(p_130141_, p_130142_);
   }

   public boolean getBoolean(int p_130159_) {
      return this.source.getBoolean(p_130159_);
   }

   public byte getByte(int p_130161_) {
      return this.source.getByte(p_130161_);
   }

   public short getUnsignedByte(int p_130225_) {
      return this.source.getUnsignedByte(p_130225_);
   }

   public short getShort(int p_130221_) {
      return this.source.getShort(p_130221_);
   }

   public short getShortLE(int p_130223_) {
      return this.source.getShortLE(p_130223_);
   }

   public int getUnsignedShort(int p_130235_) {
      return this.source.getUnsignedShort(p_130235_);
   }

   public int getUnsignedShortLE(int p_130237_) {
      return this.source.getUnsignedShortLE(p_130237_);
   }

   public int getMedium(int p_130217_) {
      return this.source.getMedium(p_130217_);
   }

   public int getMediumLE(int p_130219_) {
      return this.source.getMediumLE(p_130219_);
   }

   public int getUnsignedMedium(int p_130231_) {
      return this.source.getUnsignedMedium(p_130231_);
   }

   public int getUnsignedMediumLE(int p_130233_) {
      return this.source.getUnsignedMediumLE(p_130233_);
   }

   public int getInt(int p_130209_) {
      return this.source.getInt(p_130209_);
   }

   public int getIntLE(int p_130211_) {
      return this.source.getIntLE(p_130211_);
   }

   public long getUnsignedInt(int p_130227_) {
      return this.source.getUnsignedInt(p_130227_);
   }

   public long getUnsignedIntLE(int p_130229_) {
      return this.source.getUnsignedIntLE(p_130229_);
   }

   public long getLong(int p_130213_) {
      return this.source.getLong(p_130213_);
   }

   public long getLongLE(int p_130215_) {
      return this.source.getLongLE(p_130215_);
   }

   public char getChar(int p_130199_) {
      return this.source.getChar(p_130199_);
   }

   public float getFloat(int p_130207_) {
      return this.source.getFloat(p_130207_);
   }

   public double getDouble(int p_130205_) {
      return this.source.getDouble(p_130205_);
   }

   public ByteBuf getBytes(int p_130163_, ByteBuf p_130164_) {
      return this.source.getBytes(p_130163_, p_130164_);
   }

   public ByteBuf getBytes(int p_130166_, ByteBuf p_130167_, int p_130168_) {
      return this.source.getBytes(p_130166_, p_130167_, p_130168_);
   }

   public ByteBuf getBytes(int p_130170_, ByteBuf p_130171_, int p_130172_, int p_130173_) {
      return this.source.getBytes(p_130170_, p_130171_, p_130172_, p_130173_);
   }

   public ByteBuf getBytes(int p_130191_, byte[] p_130192_) {
      return this.source.getBytes(p_130191_, p_130192_);
   }

   public ByteBuf getBytes(int p_130194_, byte[] p_130195_, int p_130196_, int p_130197_) {
      return this.source.getBytes(p_130194_, p_130195_, p_130196_, p_130197_);
   }

   public ByteBuf getBytes(int p_130179_, ByteBuffer p_130180_) {
      return this.source.getBytes(p_130179_, p_130180_);
   }

   public ByteBuf getBytes(int p_130175_, OutputStream p_130176_, int p_130177_) throws IOException {
      return this.source.getBytes(p_130175_, p_130176_, p_130177_);
   }

   public int getBytes(int p_130187_, GatheringByteChannel p_130188_, int p_130189_) throws IOException {
      return this.source.getBytes(p_130187_, p_130188_, p_130189_);
   }

   public int getBytes(int p_130182_, FileChannel p_130183_, long p_130184_, int p_130185_) throws IOException {
      return this.source.getBytes(p_130182_, p_130183_, p_130184_, p_130185_);
   }

   public CharSequence getCharSequence(int p_130201_, int p_130202_, Charset p_130203_) {
      return this.source.getCharSequence(p_130201_, p_130202_, p_130203_);
   }

   public ByteBuf setBoolean(int p_130362_, boolean p_130363_) {
      return this.source.setBoolean(p_130362_, p_130363_);
   }

   public ByteBuf setByte(int p_130365_, int p_130366_) {
      return this.source.setByte(p_130365_, p_130366_);
   }

   public ByteBuf setShort(int p_130438_, int p_130439_) {
      return this.source.setShort(p_130438_, p_130439_);
   }

   public ByteBuf setShortLE(int p_130441_, int p_130442_) {
      return this.source.setShortLE(p_130441_, p_130442_);
   }

   public ByteBuf setMedium(int p_130432_, int p_130433_) {
      return this.source.setMedium(p_130432_, p_130433_);
   }

   public ByteBuf setMediumLE(int p_130435_, int p_130436_) {
      return this.source.setMediumLE(p_130435_, p_130436_);
   }

   public ByteBuf setInt(int p_130420_, int p_130421_) {
      return this.source.setInt(p_130420_, p_130421_);
   }

   public ByteBuf setIntLE(int p_130423_, int p_130424_) {
      return this.source.setIntLE(p_130423_, p_130424_);
   }

   public ByteBuf setLong(int p_130426_, long p_130427_) {
      return this.source.setLong(p_130426_, p_130427_);
   }

   public ByteBuf setLongLE(int p_130429_, long p_130430_) {
      return this.source.setLongLE(p_130429_, p_130430_);
   }

   public ByteBuf setChar(int p_130404_, int p_130405_) {
      return this.source.setChar(p_130404_, p_130405_);
   }

   public ByteBuf setFloat(int p_130414_, float p_130415_) {
      return this.source.setFloat(p_130414_, p_130415_);
   }

   public ByteBuf setDouble(int p_130411_, double p_130412_) {
      return this.source.setDouble(p_130411_, p_130412_);
   }

   public ByteBuf setBytes(int p_130368_, ByteBuf p_130369_) {
      return this.source.setBytes(p_130368_, p_130369_);
   }

   public ByteBuf setBytes(int p_130371_, ByteBuf p_130372_, int p_130373_) {
      return this.source.setBytes(p_130371_, p_130372_, p_130373_);
   }

   public ByteBuf setBytes(int p_130375_, ByteBuf p_130376_, int p_130377_, int p_130378_) {
      return this.source.setBytes(p_130375_, p_130376_, p_130377_, p_130378_);
   }

   public ByteBuf setBytes(int p_130396_, byte[] p_130397_) {
      return this.source.setBytes(p_130396_, p_130397_);
   }

   public ByteBuf setBytes(int p_130399_, byte[] p_130400_, int p_130401_, int p_130402_) {
      return this.source.setBytes(p_130399_, p_130400_, p_130401_, p_130402_);
   }

   public ByteBuf setBytes(int p_130384_, ByteBuffer p_130385_) {
      return this.source.setBytes(p_130384_, p_130385_);
   }

   public int setBytes(int p_130380_, InputStream p_130381_, int p_130382_) throws IOException {
      return this.source.setBytes(p_130380_, p_130381_, p_130382_);
   }

   public int setBytes(int p_130392_, ScatteringByteChannel p_130393_, int p_130394_) throws IOException {
      return this.source.setBytes(p_130392_, p_130393_, p_130394_);
   }

   public int setBytes(int p_130387_, FileChannel p_130388_, long p_130389_, int p_130390_) throws IOException {
      return this.source.setBytes(p_130387_, p_130388_, p_130389_, p_130390_);
   }

   public ByteBuf setZero(int p_130444_, int p_130445_) {
      return this.source.setZero(p_130444_, p_130445_);
   }

   public int setCharSequence(int p_130407_, CharSequence p_130408_, Charset p_130409_) {
      return this.source.setCharSequence(p_130407_, p_130408_, p_130409_);
   }

   public boolean readBoolean() {
      return this.source.readBoolean();
   }

   public byte readByte() {
      return this.source.readByte();
   }

   public short readUnsignedByte() {
      return this.source.readUnsignedByte();
   }

   public short readShort() {
      return this.source.readShort();
   }

   public short readShortLE() {
      return this.source.readShortLE();
   }

   public int readUnsignedShort() {
      return this.source.readUnsignedShort();
   }

   public int readUnsignedShortLE() {
      return this.source.readUnsignedShortLE();
   }

   public int readMedium() {
      return this.source.readMedium();
   }

   public int readMediumLE() {
      return this.source.readMediumLE();
   }

   public int readUnsignedMedium() {
      return this.source.readUnsignedMedium();
   }

   public int readUnsignedMediumLE() {
      return this.source.readUnsignedMediumLE();
   }

   public int readInt() {
      return this.source.readInt();
   }

   public int readIntLE() {
      return this.source.readIntLE();
   }

   public long readUnsignedInt() {
      return this.source.readUnsignedInt();
   }

   public long readUnsignedIntLE() {
      return this.source.readUnsignedIntLE();
   }

   public long readLong() {
      return this.source.readLong();
   }

   public long readLongLE() {
      return this.source.readLongLE();
   }

   public char readChar() {
      return this.source.readChar();
   }

   public float readFloat() {
      return this.source.readFloat();
   }

   public double readDouble() {
      return this.source.readDouble();
   }

   public ByteBuf readBytes(int p_130287_) {
      return this.source.readBytes(p_130287_);
   }

   public ByteBuf readSlice(int p_130332_) {
      return this.source.readSlice(p_130332_);
   }

   public ByteBuf readRetainedSlice(int p_130328_) {
      return this.source.readRetainedSlice(p_130328_);
   }

   public ByteBuf readBytes(ByteBuf p_130289_) {
      return this.source.readBytes(p_130289_);
   }

   public ByteBuf readBytes(ByteBuf p_130291_, int p_130292_) {
      return this.source.readBytes(p_130291_, p_130292_);
   }

   public ByteBuf readBytes(ByteBuf p_130294_, int p_130295_, int p_130296_) {
      return this.source.readBytes(p_130294_, p_130295_, p_130296_);
   }

   public ByteBuf readBytes(byte[] p_130310_) {
      return this.source.readBytes(p_130310_);
   }

   public ByteBuf readBytes(byte[] p_130312_, int p_130313_, int p_130314_) {
      return this.source.readBytes(p_130312_, p_130313_, p_130314_);
   }

   public ByteBuf readBytes(ByteBuffer p_130301_) {
      return this.source.readBytes(p_130301_);
   }

   public ByteBuf readBytes(OutputStream p_130298_, int p_130299_) throws IOException {
      return this.source.readBytes(p_130298_, p_130299_);
   }

   public int readBytes(GatheringByteChannel p_130307_, int p_130308_) throws IOException {
      return this.source.readBytes(p_130307_, p_130308_);
   }

   public CharSequence readCharSequence(int p_130317_, Charset p_130318_) {
      return this.source.readCharSequence(p_130317_, p_130318_);
   }

   public int readBytes(FileChannel p_130303_, long p_130304_, int p_130305_) throws IOException {
      return this.source.readBytes(p_130303_, p_130304_, p_130305_);
   }

   public ByteBuf skipBytes(int p_130447_) {
      return this.source.skipBytes(p_130447_);
   }

   public ByteBuf writeBoolean(boolean p_130468_) {
      return this.source.writeBoolean(p_130468_);
   }

   public ByteBuf writeByte(int p_130470_) {
      return this.source.writeByte(p_130470_);
   }

   public ByteBuf writeShort(int p_130520_) {
      return this.source.writeShort(p_130520_);
   }

   public ByteBuf writeShortLE(int p_130522_) {
      return this.source.writeShortLE(p_130522_);
   }

   public ByteBuf writeMedium(int p_130516_) {
      return this.source.writeMedium(p_130516_);
   }

   public ByteBuf writeMediumLE(int p_130518_) {
      return this.source.writeMediumLE(p_130518_);
   }

   public ByteBuf writeInt(int p_130508_) {
      return this.source.writeInt(p_130508_);
   }

   public ByteBuf writeIntLE(int p_130510_) {
      return this.source.writeIntLE(p_130510_);
   }

   public ByteBuf writeLong(long p_130512_) {
      return this.source.writeLong(p_130512_);
   }

   public ByteBuf writeLongLE(long p_130514_) {
      return this.source.writeLongLE(p_130514_);
   }

   public ByteBuf writeChar(int p_130499_) {
      return this.source.writeChar(p_130499_);
   }

   public ByteBuf writeFloat(float p_130506_) {
      return this.source.writeFloat(p_130506_);
   }

   public ByteBuf writeDouble(double p_130504_) {
      return this.source.writeDouble(p_130504_);
   }

   public ByteBuf writeBytes(ByteBuf p_130472_) {
      return this.source.writeBytes(p_130472_);
   }

   public ByteBuf writeBytes(ByteBuf p_130474_, int p_130475_) {
      return this.source.writeBytes(p_130474_, p_130475_);
   }

   public ByteBuf writeBytes(ByteBuf p_130477_, int p_130478_, int p_130479_) {
      return this.source.writeBytes(p_130477_, p_130478_, p_130479_);
   }

   public ByteBuf writeBytes(byte[] p_130493_) {
      return this.source.writeBytes(p_130493_);
   }

   public ByteBuf writeBytes(byte[] p_130495_, int p_130496_, int p_130497_) {
      return this.source.writeBytes(p_130495_, p_130496_, p_130497_);
   }

   public ByteBuf writeBytes(ByteBuffer p_130484_) {
      return this.source.writeBytes(p_130484_);
   }

   public int writeBytes(InputStream p_130481_, int p_130482_) throws IOException {
      return this.source.writeBytes(p_130481_, p_130482_);
   }

   public int writeBytes(ScatteringByteChannel p_130490_, int p_130491_) throws IOException {
      return this.source.writeBytes(p_130490_, p_130491_);
   }

   public int writeBytes(FileChannel p_130486_, long p_130487_, int p_130488_) throws IOException {
      return this.source.writeBytes(p_130486_, p_130487_, p_130488_);
   }

   public ByteBuf writeZero(int p_130524_) {
      return this.source.writeZero(p_130524_);
   }

   public int writeCharSequence(CharSequence p_130501_, Charset p_130502_) {
      return this.source.writeCharSequence(p_130501_, p_130502_);
   }

   public int indexOf(int p_130244_, int p_130245_, byte p_130246_) {
      return this.source.indexOf(p_130244_, p_130245_, p_130246_);
   }

   public int bytesBefore(byte p_130108_) {
      return this.source.bytesBefore(p_130108_);
   }

   public int bytesBefore(int p_130110_, byte p_130111_) {
      return this.source.bytesBefore(p_130110_, p_130111_);
   }

   public int bytesBefore(int p_130113_, int p_130114_, byte p_130115_) {
      return this.source.bytesBefore(p_130113_, p_130114_, p_130115_);
   }

   public int forEachByte(ByteProcessor p_130150_) {
      return this.source.forEachByte(p_130150_);
   }

   public int forEachByte(int p_130146_, int p_130147_, ByteProcessor p_130148_) {
      return this.source.forEachByte(p_130146_, p_130147_, p_130148_);
   }

   public int forEachByteDesc(ByteProcessor p_130156_) {
      return this.source.forEachByteDesc(p_130156_);
   }

   public int forEachByteDesc(int p_130152_, int p_130153_, ByteProcessor p_130154_) {
      return this.source.forEachByteDesc(p_130152_, p_130153_, p_130154_);
   }

   public ByteBuf copy() {
      return this.source.copy();
   }

   public ByteBuf copy(int p_130128_, int p_130129_) {
      return this.source.copy(p_130128_, p_130129_);
   }

   public ByteBuf slice() {
      return this.source.slice();
   }

   public ByteBuf retainedSlice() {
      return this.source.retainedSlice();
   }

   public ByteBuf slice(int p_130450_, int p_130451_) {
      return this.source.slice(p_130450_, p_130451_);
   }

   public ByteBuf retainedSlice(int p_130359_, int p_130360_) {
      return this.source.retainedSlice(p_130359_, p_130360_);
   }

   public ByteBuf duplicate() {
      return this.source.duplicate();
   }

   public ByteBuf retainedDuplicate() {
      return this.source.retainedDuplicate();
   }

   public int nioBufferCount() {
      return this.source.nioBufferCount();
   }

   public ByteBuffer nioBuffer() {
      return this.source.nioBuffer();
   }

   public ByteBuffer nioBuffer(int p_130270_, int p_130271_) {
      return this.source.nioBuffer(p_130270_, p_130271_);
   }

   public ByteBuffer internalNioBuffer(int p_130248_, int p_130249_) {
      return this.source.internalNioBuffer(p_130248_, p_130249_);
   }

   public ByteBuffer[] nioBuffers() {
      return this.source.nioBuffers();
   }

   public ByteBuffer[] nioBuffers(int p_130275_, int p_130276_) {
      return this.source.nioBuffers(p_130275_, p_130276_);
   }

   public boolean hasArray() {
      return this.source.hasArray();
   }

   public byte[] array() {
      return this.source.array();
   }

   public int arrayOffset() {
      return this.source.arrayOffset();
   }

   public boolean hasMemoryAddress() {
      return this.source.hasMemoryAddress();
   }

   public long memoryAddress() {
      return this.source.memoryAddress();
   }

   public String toString(Charset p_130458_) {
      return this.source.toString(p_130458_);
   }

   public String toString(int p_130454_, int p_130455_, Charset p_130456_) {
      return this.source.toString(p_130454_, p_130455_, p_130456_);
   }

   public int hashCode() {
      return this.source.hashCode();
   }

   public boolean equals(Object p_130144_) {
      return this.source.equals(p_130144_);
   }

   public int compareTo(ByteBuf p_130123_) {
      return this.source.compareTo(p_130123_);
   }

   public String toString() {
      return this.source.toString();
   }

   public ByteBuf retain(int p_130353_) {
      return this.source.retain(p_130353_);
   }

   public ByteBuf retain() {
      return this.source.retain();
   }

   public ByteBuf touch() {
      return this.source.touch();
   }

   public ByteBuf touch(Object p_130462_) {
      return this.source.touch(p_130462_);
   }

   public int refCnt() {
      return this.source.refCnt();
   }

   public boolean release() {
      return this.source.release();
   }

   public boolean release(int p_130347_) {
      return this.source.release(p_130347_);
   }
}
