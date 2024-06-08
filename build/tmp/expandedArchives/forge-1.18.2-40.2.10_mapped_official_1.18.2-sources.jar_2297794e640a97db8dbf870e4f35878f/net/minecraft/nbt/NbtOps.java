package net.minecraft.nbt;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.PeekingIterator;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.RecordBuilder.AbstractStringBuilder;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class NbtOps implements DynamicOps<Tag> {
   public static final NbtOps INSTANCE = new NbtOps();

   protected NbtOps() {
   }

   public Tag empty() {
      return EndTag.INSTANCE;
   }

   public <U> U convertTo(DynamicOps<U> p_128980_, Tag p_128981_) {
      switch(p_128981_.getId()) {
      case 0:
         return p_128980_.empty();
      case 1:
         return p_128980_.createByte(((NumericTag)p_128981_).getAsByte());
      case 2:
         return p_128980_.createShort(((NumericTag)p_128981_).getAsShort());
      case 3:
         return p_128980_.createInt(((NumericTag)p_128981_).getAsInt());
      case 4:
         return p_128980_.createLong(((NumericTag)p_128981_).getAsLong());
      case 5:
         return p_128980_.createFloat(((NumericTag)p_128981_).getAsFloat());
      case 6:
         return p_128980_.createDouble(((NumericTag)p_128981_).getAsDouble());
      case 7:
         return p_128980_.createByteList(ByteBuffer.wrap(((ByteArrayTag)p_128981_).getAsByteArray()));
      case 8:
         return p_128980_.createString(p_128981_.getAsString());
      case 9:
         return this.convertList(p_128980_, p_128981_);
      case 10:
         return this.convertMap(p_128980_, p_128981_);
      case 11:
         return p_128980_.createIntList(Arrays.stream(((IntArrayTag)p_128981_).getAsIntArray()));
      case 12:
         return p_128980_.createLongList(Arrays.stream(((LongArrayTag)p_128981_).getAsLongArray()));
      default:
         throw new IllegalStateException("Unknown tag type: " + p_128981_);
      }
   }

   public DataResult<Number> getNumberValue(Tag p_129030_) {
      return p_129030_ instanceof NumericTag ? DataResult.success(((NumericTag)p_129030_).getAsNumber()) : DataResult.error("Not a number");
   }

   public Tag createNumeric(Number p_128983_) {
      return DoubleTag.valueOf(p_128983_.doubleValue());
   }

   public Tag createByte(byte p_128963_) {
      return ByteTag.valueOf(p_128963_);
   }

   public Tag createShort(short p_129048_) {
      return ShortTag.valueOf(p_129048_);
   }

   public Tag createInt(int p_128976_) {
      return IntTag.valueOf(p_128976_);
   }

   public Tag createLong(long p_128978_) {
      return LongTag.valueOf(p_128978_);
   }

   public Tag createFloat(float p_128974_) {
      return FloatTag.valueOf(p_128974_);
   }

   public Tag createDouble(double p_128972_) {
      return DoubleTag.valueOf(p_128972_);
   }

   public Tag createBoolean(boolean p_129050_) {
      return ByteTag.valueOf(p_129050_);
   }

   public DataResult<String> getStringValue(Tag p_129061_) {
      return p_129061_ instanceof StringTag ? DataResult.success(p_129061_.getAsString()) : DataResult.error("Not a string");
   }

   public Tag createString(String p_128985_) {
      return StringTag.valueOf(p_128985_);
   }

   private static CollectionTag<?> createGenericList(byte p_128965_, byte p_128966_) {
      if (typesMatch(p_128965_, p_128966_, (byte)4)) {
         return new LongArrayTag(new long[0]);
      } else if (typesMatch(p_128965_, p_128966_, (byte)1)) {
         return new ByteArrayTag(new byte[0]);
      } else {
         return (CollectionTag<?>)(typesMatch(p_128965_, p_128966_, (byte)3) ? new IntArrayTag(new int[0]) : new ListTag());
      }
   }

   private static boolean typesMatch(byte p_128968_, byte p_128969_, byte p_128970_) {
      return p_128968_ == p_128970_ && (p_128969_ == p_128970_ || p_128969_ == 0);
   }

   private static <T extends Tag> void fillOne(CollectionTag<T> p_129013_, Tag p_129014_, Tag p_129015_) {
      if (p_129014_ instanceof CollectionTag) {
         CollectionTag<T> collectiontag = (CollectionTag<T>)p_129014_;
         collectiontag.forEach((p_129064_) -> {
            p_129013_.add(p_129064_);
         });
      }

      p_129013_.add((T)p_129015_);
   }

   private static <T extends Tag> void fillMany(CollectionTag<T> p_129009_, Tag p_129010_, List<Tag> p_129011_) {
      if (p_129010_ instanceof CollectionTag) {
         CollectionTag<T> collectiontag = (CollectionTag<T>)p_129010_;
         collectiontag.forEach((p_129055_) -> {
            p_129009_.add(p_129055_);
         });
      }

      ((List<T>)p_129011_).forEach((p_129007_) -> {
         p_129009_.add(p_129007_);
      });
   }

   public DataResult<Tag> mergeToList(Tag p_129041_, Tag p_129042_) {
      if (!(p_129041_ instanceof CollectionTag) && !(p_129041_ instanceof EndTag)) {
         return DataResult.error("mergeToList called with not a list: " + p_129041_, p_129041_);
      } else {
         CollectionTag<?> collectiontag = createGenericList(p_129041_ instanceof CollectionTag ? ((CollectionTag)p_129041_).getElementType() : 0, p_129042_.getId());
         fillOne(collectiontag, p_129041_, p_129042_);
         return DataResult.success(collectiontag);
      }
   }

   public DataResult<Tag> mergeToList(Tag p_129038_, List<Tag> p_129039_) {
      if (!(p_129038_ instanceof CollectionTag) && !(p_129038_ instanceof EndTag)) {
         return DataResult.error("mergeToList called with not a list: " + p_129038_, p_129038_);
      } else {
         CollectionTag<?> collectiontag = createGenericList(p_129038_ instanceof CollectionTag ? ((CollectionTag)p_129038_).getElementType() : 0, p_129039_.stream().findFirst().map(Tag::getId).orElse((byte)0));
         fillMany(collectiontag, p_129038_, p_129039_);
         return DataResult.success(collectiontag);
      }
   }

   public DataResult<Tag> mergeToMap(Tag p_129044_, Tag p_129045_, Tag p_129046_) {
      if (!(p_129044_ instanceof CompoundTag) && !(p_129044_ instanceof EndTag)) {
         return DataResult.error("mergeToMap called with not a map: " + p_129044_, p_129044_);
      } else if (!(p_129045_ instanceof StringTag)) {
         return DataResult.error("key is not a string: " + p_129045_, p_129044_);
      } else {
         CompoundTag compoundtag = new CompoundTag();
         if (p_129044_ instanceof CompoundTag) {
            CompoundTag compoundtag1 = (CompoundTag)p_129044_;
            compoundtag1.getAllKeys().forEach((p_129068_) -> {
               compoundtag.put(p_129068_, compoundtag1.get(p_129068_));
            });
         }

         compoundtag.put(p_129045_.getAsString(), p_129046_);
         return DataResult.success(compoundtag);
      }
   }

   public DataResult<Tag> mergeToMap(Tag p_129032_, MapLike<Tag> p_129033_) {
      if (!(p_129032_ instanceof CompoundTag) && !(p_129032_ instanceof EndTag)) {
         return DataResult.error("mergeToMap called with not a map: " + p_129032_, p_129032_);
      } else {
         CompoundTag compoundtag = new CompoundTag();
         if (p_129032_ instanceof CompoundTag) {
            CompoundTag compoundtag1 = (CompoundTag)p_129032_;
            compoundtag1.getAllKeys().forEach((p_129059_) -> {
               compoundtag.put(p_129059_, compoundtag1.get(p_129059_));
            });
         }

         List<Tag> list = Lists.newArrayList();
         p_129033_.entries().forEach((p_128994_) -> {
            Tag tag = p_128994_.getFirst();
            if (!(tag instanceof StringTag)) {
               list.add(tag);
            } else {
               compoundtag.put(tag.getAsString(), p_128994_.getSecond());
            }
         });
         return !list.isEmpty() ? DataResult.error("some keys are not strings: " + list, compoundtag) : DataResult.success(compoundtag);
      }
   }

   public DataResult<Stream<Pair<Tag, Tag>>> getMapValues(Tag p_129070_) {
      if (!(p_129070_ instanceof CompoundTag)) {
         return DataResult.error("Not a map: " + p_129070_);
      } else {
         CompoundTag compoundtag = (CompoundTag)p_129070_;
         return DataResult.success(compoundtag.getAllKeys().stream().map((p_129021_) -> {
            return Pair.of(this.createString(p_129021_), compoundtag.get(p_129021_));
         }));
      }
   }

   public DataResult<Consumer<BiConsumer<Tag, Tag>>> getMapEntries(Tag p_129103_) {
      if (!(p_129103_ instanceof CompoundTag)) {
         return DataResult.error("Not a map: " + p_129103_);
      } else {
         CompoundTag compoundtag = (CompoundTag)p_129103_;
         return DataResult.success((p_129024_) -> {
            compoundtag.getAllKeys().forEach((p_178006_) -> {
               p_129024_.accept(this.createString(p_178006_), compoundtag.get(p_178006_));
            });
         });
      }
   }

   public DataResult<MapLike<Tag>> getMap(Tag p_129105_) {
      if (!(p_129105_ instanceof CompoundTag)) {
         return DataResult.error("Not a map: " + p_129105_);
      } else {
         final CompoundTag compoundtag = (CompoundTag)p_129105_;
         return DataResult.success(new MapLike<Tag>() {
            @Nullable
            public Tag get(Tag p_129174_) {
               return compoundtag.get(p_129174_.getAsString());
            }

            @Nullable
            public Tag get(String p_129169_) {
               return compoundtag.get(p_129169_);
            }

            public Stream<Pair<Tag, Tag>> entries() {
               return compoundtag.getAllKeys().stream().map((p_129172_) -> {
                  return Pair.of(NbtOps.this.createString(p_129172_), compoundtag.get(p_129172_));
               });
            }

            public String toString() {
               return "MapLike[" + compoundtag + "]";
            }
         });
      }
   }

   public Tag createMap(Stream<Pair<Tag, Tag>> p_129004_) {
      CompoundTag compoundtag = new CompoundTag();
      p_129004_.forEach((p_129018_) -> {
         compoundtag.put(p_129018_.getFirst().getAsString(), p_129018_.getSecond());
      });
      return compoundtag;
   }

   public DataResult<Stream<Tag>> getStream(Tag p_129108_) {
      return p_129108_ instanceof CollectionTag ? DataResult.success(((CollectionTag)p_129108_).stream().map((p_129158_) -> {
         return p_129158_;
      })) : DataResult.error("Not a list");
   }

   public DataResult<Consumer<Consumer<Tag>>> getList(Tag p_129110_) {
      if (p_129110_ instanceof CollectionTag) {
         CollectionTag<?> collectiontag = (CollectionTag)p_129110_;
         return DataResult.success(collectiontag::forEach);
      } else {
         return DataResult.error("Not a list: " + p_129110_);
      }
   }

   public DataResult<ByteBuffer> getByteBuffer(Tag p_129132_) {
      return p_129132_ instanceof ByteArrayTag ? DataResult.success(ByteBuffer.wrap(((ByteArrayTag)p_129132_).getAsByteArray())) : DynamicOps.super.getByteBuffer(p_129132_);
   }

   public Tag createByteList(ByteBuffer p_128990_) {
      return new ByteArrayTag(DataFixUtils.toArray(p_128990_));
   }

   public DataResult<IntStream> getIntStream(Tag p_129134_) {
      return p_129134_ instanceof IntArrayTag ? DataResult.success(Arrays.stream(((IntArrayTag)p_129134_).getAsIntArray())) : DynamicOps.super.getIntStream(p_129134_);
   }

   public Tag createIntList(IntStream p_129000_) {
      return new IntArrayTag(p_129000_.toArray());
   }

   public DataResult<LongStream> getLongStream(Tag p_129136_) {
      return p_129136_ instanceof LongArrayTag ? DataResult.success(Arrays.stream(((LongArrayTag)p_129136_).getAsLongArray())) : DynamicOps.super.getLongStream(p_129136_);
   }

   public Tag createLongList(LongStream p_129002_) {
      return new LongArrayTag(p_129002_.toArray());
   }

   public Tag createList(Stream<Tag> p_129052_) {
      PeekingIterator<Tag> peekingiterator = Iterators.peekingIterator(p_129052_.iterator());
      if (!peekingiterator.hasNext()) {
         return new ListTag();
      } else {
         Tag tag = peekingiterator.peek();
         if (tag instanceof ByteTag) {
            List<Byte> list2 = Lists.newArrayList(Iterators.transform(peekingiterator, (p_129142_) -> {
               return ((ByteTag)p_129142_).getAsByte();
            }));
            return new ByteArrayTag(list2);
         } else if (tag instanceof IntTag) {
            List<Integer> list1 = Lists.newArrayList(Iterators.transform(peekingiterator, (p_129140_) -> {
               return ((IntTag)p_129140_).getAsInt();
            }));
            return new IntArrayTag(list1);
         } else if (tag instanceof LongTag) {
            List<Long> list = Lists.newArrayList(Iterators.transform(peekingiterator, (p_129138_) -> {
               return ((LongTag)p_129138_).getAsLong();
            }));
            return new LongArrayTag(list);
         } else {
            ListTag listtag = new ListTag();

            while(peekingiterator.hasNext()) {
               Tag tag1 = peekingiterator.next();
               if (!(tag1 instanceof EndTag)) {
                  listtag.add(tag1);
               }
            }

            return listtag;
         }
      }
   }

   public Tag remove(Tag p_129035_, String p_129036_) {
      if (p_129035_ instanceof CompoundTag) {
         CompoundTag compoundtag = (CompoundTag)p_129035_;
         CompoundTag compoundtag1 = new CompoundTag();
         compoundtag.getAllKeys().stream().filter((p_128988_) -> {
            return !Objects.equals(p_128988_, p_129036_);
         }).forEach((p_129028_) -> {
            compoundtag1.put(p_129028_, compoundtag.get(p_129028_));
         });
         return compoundtag1;
      } else {
         return p_129035_;
      }
   }

   public String toString() {
      return "NBT";
   }

   public RecordBuilder<Tag> mapBuilder() {
      return new NbtOps.NbtRecordBuilder();
   }

   class NbtRecordBuilder extends AbstractStringBuilder<Tag, CompoundTag> {
      protected NbtRecordBuilder() {
         super(NbtOps.this);
      }

      protected CompoundTag initBuilder() {
         return new CompoundTag();
      }

      protected CompoundTag append(String p_129186_, Tag p_129187_, CompoundTag p_129188_) {
         p_129188_.put(p_129186_, p_129187_);
         return p_129188_;
      }

      protected DataResult<Tag> build(CompoundTag p_129190_, Tag p_129191_) {
         if (p_129191_ != null && p_129191_ != EndTag.INSTANCE) {
            if (!(p_129191_ instanceof CompoundTag)) {
               return DataResult.error("mergeToMap called with not a map: " + p_129191_, p_129191_);
            } else {
               CompoundTag compoundtag = new CompoundTag(Maps.newHashMap(((CompoundTag)p_129191_).entries()));

               for(Entry<String, Tag> entry : p_129190_.entries().entrySet()) {
                  compoundtag.put(entry.getKey(), entry.getValue());
               }

               return DataResult.success(compoundtag);
            }
         } else {
            return DataResult.success(p_129190_);
         }
      }
   }
}