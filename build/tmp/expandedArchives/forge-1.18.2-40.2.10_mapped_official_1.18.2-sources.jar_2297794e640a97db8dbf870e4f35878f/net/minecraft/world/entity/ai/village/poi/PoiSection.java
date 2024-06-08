package net.minecraft.world.entity.ai.village.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.VisibleForDebug;
import org.slf4j.Logger;

public class PoiSection {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Short2ObjectMap<PoiRecord> records = new Short2ObjectOpenHashMap<>();
   private final Map<PoiType, Set<PoiRecord>> byType = Maps.newHashMap();
   private final Runnable setDirty;
   private boolean isValid;

   public static Codec<PoiSection> codec(Runnable p_27296_) {
      return RecordCodecBuilder.<PoiSection>create((p_27299_) -> {
         return p_27299_.group(RecordCodecBuilder.point(p_27296_), Codec.BOOL.optionalFieldOf("Valid", Boolean.valueOf(false)).forGetter((p_148681_) -> {
            return p_148681_.isValid;
         }), PoiRecord.codec(p_27296_).listOf().fieldOf("Records").forGetter((p_148675_) -> {
            return ImmutableList.copyOf(p_148675_.records.values());
         })).apply(p_27299_, PoiSection::new);
      }).orElseGet(Util.prefix("Failed to read POI section: ", LOGGER::error), () -> {
         return new PoiSection(p_27296_, false, ImmutableList.of());
      });
   }

   public PoiSection(Runnable p_27267_) {
      this(p_27267_, true, ImmutableList.of());
   }

   private PoiSection(Runnable p_27269_, boolean p_27270_, List<PoiRecord> p_27271_) {
      this.setDirty = p_27269_;
      this.isValid = p_27270_;
      p_27271_.forEach(this::add);
   }

   public Stream<PoiRecord> getRecords(Predicate<PoiType> p_27305_, PoiManager.Occupancy p_27306_) {
      return this.byType.entrySet().stream().filter((p_27309_) -> {
         return p_27305_.test(p_27309_.getKey());
      }).flatMap((p_27301_) -> {
         return p_27301_.getValue().stream();
      }).filter(p_27306_.getTest());
   }

   public void add(BlockPos p_27282_, PoiType p_27283_) {
      if (this.add(new PoiRecord(p_27282_, p_27283_, this.setDirty))) {
         LOGGER.debug("Added POI of type {} @ {}", p_27283_, p_27282_);
         this.setDirty.run();
      }

   }

   private boolean add(PoiRecord p_27274_) {
      BlockPos blockpos = p_27274_.getPos();
      PoiType poitype = p_27274_.getPoiType();
      short short1 = SectionPos.sectionRelativePos(blockpos);
      PoiRecord poirecord = this.records.get(short1);
      if (poirecord != null) {
         if (poitype.equals(poirecord.getPoiType())) {
            return false;
         }

         Util.logAndPauseIfInIde("POI data mismatch: already registered at " + blockpos);
      }

      this.records.put(short1, p_27274_);
      this.byType.computeIfAbsent(poitype, (p_201961_) -> {
         return Sets.newHashSet();
      }).add(p_27274_);
      return true;
   }

   public void remove(BlockPos p_27280_) {
      PoiRecord poirecord = this.records.remove(SectionPos.sectionRelativePos(p_27280_));
      if (poirecord == null) {
         LOGGER.error("POI data mismatch: never registered at {}", (Object)p_27280_);
      } else {
         this.byType.get(poirecord.getPoiType()).remove(poirecord);
         LOGGER.debug("Removed POI of type {} @ {}", LogUtils.defer(poirecord::getPoiType), LogUtils.defer(poirecord::getPos));
         this.setDirty.run();
      }
   }

   /** @deprecated */
   @Deprecated
   @VisibleForDebug
   public int getFreeTickets(BlockPos p_148683_) {
      return this.getPoiRecord(p_148683_).map(PoiRecord::getFreeTickets).orElse(0);
   }

   public boolean release(BlockPos p_27318_) {
      PoiRecord poirecord = this.records.get(SectionPos.sectionRelativePos(p_27318_));
      if (poirecord == null) {
         throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("POI never registered at " + p_27318_));
      } else {
         boolean flag = poirecord.releaseTicket();
         this.setDirty.run();
         return flag;
      }
   }

   public boolean exists(BlockPos p_27289_, Predicate<PoiType> p_27290_) {
      return this.getType(p_27289_).filter(p_27290_).isPresent();
   }

   public Optional<PoiType> getType(BlockPos p_27320_) {
      return this.getPoiRecord(p_27320_).map(PoiRecord::getPoiType);
   }

   private Optional<PoiRecord> getPoiRecord(BlockPos p_148685_) {
      return Optional.ofNullable(this.records.get(SectionPos.sectionRelativePos(p_148685_)));
   }

   public void refresh(Consumer<BiConsumer<BlockPos, PoiType>> p_27303_) {
      if (!this.isValid) {
         Short2ObjectMap<PoiRecord> short2objectmap = new Short2ObjectOpenHashMap<>(this.records);
         this.clear();
         p_27303_.accept((p_201968_, p_201969_) -> {
            short short1 = SectionPos.sectionRelativePos(p_201968_);
            PoiRecord poirecord = short2objectmap.computeIfAbsent(short1, (p_201965_) -> {
               return new PoiRecord(p_201968_, p_201969_, this.setDirty);
            });
            this.add(poirecord);
         });
         this.isValid = true;
         this.setDirty.run();
      }

   }

   private void clear() {
      this.records.clear();
      this.byType.clear();
   }

   boolean isValid() {
      return this.isValid;
   }
}