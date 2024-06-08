package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.nbt.visitors.SkipFields;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.DirectoryLock;
import net.minecraft.util.MemoryReserve;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import org.slf4j.Logger;

public class LevelStorageSource {
   static final Logger LOGGER = LogUtils.getLogger();
   static final DateTimeFormatter FORMATTER = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
   private static final String ICON_FILENAME = "icon.png";
   private static final ImmutableList<String> OLD_SETTINGS_KEYS = ImmutableList.of("RandomSeed", "generatorName", "generatorOptions", "generatorVersion", "legacy_custom_options", "MapFeatures", "BonusChest");
   private static final String TAG_DATA = "Data";
   final Path baseDir;
   private final Path backupDir;
   final DataFixer fixerUpper;

   public LevelStorageSource(Path p_78199_, Path p_78200_, DataFixer p_78201_) {
      this.fixerUpper = p_78201_;

      try {
         Files.createDirectories(Files.exists(p_78199_) ? p_78199_.toRealPath() : p_78199_);
      } catch (IOException ioexception) {
         throw new RuntimeException(ioexception);
      }

      this.baseDir = p_78199_;
      this.backupDir = p_78200_;
   }

   public static LevelStorageSource createDefault(Path p_78243_) {
      return new LevelStorageSource(p_78243_, p_78243_.resolve("../backups"), DataFixers.getDataFixer());
   }

   private static <T> Pair<WorldGenSettings, Lifecycle> readWorldGenSettings(Dynamic<T> p_78205_, DataFixer p_78206_, int p_78207_) {
      Dynamic<T> dynamic = p_78205_.get("WorldGenSettings").orElseEmptyMap();

      for(String s : OLD_SETTINGS_KEYS) {
         Optional<? extends Dynamic<?>> optional = p_78205_.get(s).result();
         if (optional.isPresent()) {
            dynamic = dynamic.set(s, optional.get());
         }
      }

      Dynamic<T> dynamic1 = p_78206_.update(References.WORLD_GEN_SETTINGS, dynamic, p_78207_, SharedConstants.getCurrentVersion().getWorldVersion());
      DataResult<WorldGenSettings> dataresult = WorldGenSettings.CODEC.parse(dynamic1);
      return Pair.of(dataresult.resultOrPartial(Util.prefix("WorldGenSettings: ", LOGGER::error)).orElseGet(() -> {
         RegistryAccess registryaccess = RegistryAccess.readFromDisk(dynamic1);
         return WorldGenSettings.makeDefault(registryaccess);
      }), dataresult.lifecycle());
   }

   private static DataPackConfig readDataPackConfig(Dynamic<?> p_78203_) {
      return DataPackConfig.CODEC.parse(p_78203_).resultOrPartial(LOGGER::error).orElse(DataPackConfig.DEFAULT);
   }

   public String getName() {
      return "Anvil";
   }

   public List<LevelSummary> getLevelList() throws LevelStorageException {
      if (!Files.isDirectory(this.baseDir)) {
         throw new LevelStorageException((new TranslatableComponent("selectWorld.load_folder_access")).getString());
      } else {
         List<LevelSummary> list = Lists.newArrayList();
         File[] afile = this.baseDir.toFile().listFiles();

         for(File file1 : afile) {
            if (file1.isDirectory()) {
               boolean flag;
               try {
                  flag = DirectoryLock.isLocked(file1.toPath());
               } catch (Exception exception) {
                  LOGGER.warn("Failed to read {} lock", file1, exception);
                  continue;
               }

               try {
                  LevelSummary levelsummary = this.readLevelData(file1, this.levelSummaryReader(file1, flag));
                  if (levelsummary != null) {
                     list.add(levelsummary);
                  }
               } catch (OutOfMemoryError outofmemoryerror) {
                  MemoryReserve.release();
                  System.gc();
                  LOGGER.error(LogUtils.FATAL_MARKER, "Ran out of memory trying to read summary of {}", (Object)file1);
                  throw outofmemoryerror;
               } catch (StackOverflowError stackoverflowerror) {
                  LOGGER.error(LogUtils.FATAL_MARKER, "Ran out of stack trying to read summary of {}. Assuming corruption; attempting to restore from from level.dat_old.", (Object)file1);
                  File file2 = new File(file1, "level.dat");
                  File file3 = new File(file1, "level.dat_old");
                  File file4 = new File(file1, "level.dat_corrupted_" + LocalDateTime.now().format(FORMATTER));
                  Util.safeReplaceOrMoveFile(file2, file3, file4, true);
                  throw stackoverflowerror;
               }
            }
         }

         return list;
      }
   }

   private int getStorageVersion() {
      return 19133;
   }

   @Nullable
   <T> T readLevelData(File p_78230_, BiFunction<File, DataFixer, T> p_78231_) {
      if (!p_78230_.exists()) {
         return (T)null;
      } else {
         File file1 = new File(p_78230_, "level.dat");
         if (file1.exists()) {
            T t = p_78231_.apply(file1, this.fixerUpper);
            if (t != null) {
               return t;
            }
         }

         file1 = new File(p_78230_, "level.dat_old");
         return (T)(file1.exists() ? p_78231_.apply(file1, this.fixerUpper) : null);
      }
   }

   @Nullable
   private static DataPackConfig getDataPacks(File p_78253_, DataFixer p_78254_) {
      try {
         Tag tag = readLightweightData(p_78253_);
         if (tag instanceof CompoundTag) {
            CompoundTag compoundtag = (CompoundTag)tag;
            CompoundTag compoundtag1 = compoundtag.getCompound("Data");
            int i = compoundtag1.contains("DataVersion", 99) ? compoundtag1.getInt("DataVersion") : -1;
            Dynamic<Tag> dynamic = p_78254_.update(DataFixTypes.LEVEL.getType(), new Dynamic<>(NbtOps.INSTANCE, compoundtag1), i, SharedConstants.getCurrentVersion().getWorldVersion());
            return dynamic.get("DataPacks").result().map(LevelStorageSource::readDataPackConfig).orElse(DataPackConfig.DEFAULT);
         }
      } catch (Exception exception) {
         LOGGER.error("Exception reading {}", p_78253_, exception);
      }

      return null;
   }

   static BiFunction<File, DataFixer, PrimaryLevelData> getLevelData(DynamicOps<Tag> p_211738_, DataPackConfig p_211739_, Lifecycle p_211740_) {
      return (p_211745_, p_211746_) -> {
         try {
            CompoundTag compoundtag = NbtIo.readCompressed(p_211745_);
            CompoundTag compoundtag1 = compoundtag.getCompound("Data");
            CompoundTag compoundtag2 = compoundtag1.contains("Player", 10) ? compoundtag1.getCompound("Player") : null;
            compoundtag1.remove("Player");
            int i = compoundtag1.contains("DataVersion", 99) ? compoundtag1.getInt("DataVersion") : -1;
            Dynamic<Tag> dynamic = p_211746_.update(DataFixTypes.LEVEL.getType(), new Dynamic<>(p_211738_, compoundtag1), i, SharedConstants.getCurrentVersion().getWorldVersion());
            Pair<WorldGenSettings, Lifecycle> pair = readWorldGenSettings(dynamic, p_211746_, i);
            LevelVersion levelversion = LevelVersion.parse(dynamic);
            LevelSettings levelsettings = LevelSettings.parse(dynamic, p_211739_);
            Lifecycle lifecycle = pair.getSecond().add(p_211740_);
            return PrimaryLevelData.parse(dynamic, p_211746_, i, compoundtag2, levelsettings, levelversion, pair.getFirst(), lifecycle);
         } catch (Exception exception) {
            LOGGER.error("Exception reading {}", p_211745_, exception);
            return null;
         }
      };
   }

   BiFunction<File, DataFixer, LevelSummary> levelSummaryReader(File p_78233_, boolean p_78234_) {
      return (p_193015_, p_193016_) -> {
         try {
            Tag tag = readLightweightData(p_193015_);
            if (tag instanceof CompoundTag) {
               CompoundTag compoundtag = (CompoundTag)tag;
               CompoundTag compoundtag1 = compoundtag.getCompound("Data");
               int i = compoundtag1.contains("DataVersion", 99) ? compoundtag1.getInt("DataVersion") : -1;
               Dynamic<Tag> dynamic = p_193016_.update(DataFixTypes.LEVEL.getType(), new Dynamic<>(NbtOps.INSTANCE, compoundtag1), i, SharedConstants.getCurrentVersion().getWorldVersion());
               LevelVersion levelversion = LevelVersion.parse(dynamic);
               int j = levelversion.levelDataVersion();
               if (j == 19132 || j == 19133) {
                  boolean flag = j != this.getStorageVersion();
                  File file1 = new File(p_78233_, "icon.png");
                  DataPackConfig datapackconfig = dynamic.get("DataPacks").result().map(LevelStorageSource::readDataPackConfig).orElse(DataPackConfig.DEFAULT);
                  LevelSettings levelsettings = LevelSettings.parse(dynamic, datapackconfig);
                  return new LevelSummary(levelsettings, levelversion, p_78233_.getName(), flag, p_78234_, file1);
               }
            } else {
               LOGGER.warn("Invalid root tag in {}", (Object)p_193015_);
            }

            return null;
         } catch (Exception exception) {
            LOGGER.error("Exception reading {}", p_193015_, exception);
            return null;
         }
      };
   }

   @Nullable
   private static Tag readLightweightData(File p_202313_) throws IOException {
      SkipFields skipfields = new SkipFields(new FieldSelector("Data", CompoundTag.TYPE, "Player"), new FieldSelector("Data", CompoundTag.TYPE, "WorldGenSettings"));
      NbtIo.parseCompressed(p_202313_, skipfields);
      return skipfields.getResult();
   }

   public boolean isNewLevelIdAcceptable(String p_78241_) {
      try {
         Path path = this.baseDir.resolve(p_78241_);
         Files.createDirectory(path);
         Files.deleteIfExists(path);
         return true;
      } catch (IOException ioexception) {
         return false;
      }
   }

   public boolean levelExists(String p_78256_) {
      return Files.isDirectory(this.baseDir.resolve(p_78256_));
   }

   public Path getBaseDir() {
      return this.baseDir;
   }

   public Path getBackupPath() {
      return this.backupDir;
   }

   public LevelStorageSource.LevelStorageAccess createAccess(String p_78261_) throws IOException {
      return new LevelStorageSource.LevelStorageAccess(p_78261_);
   }

   public class LevelStorageAccess implements AutoCloseable {
      final DirectoryLock lock;
      final Path levelPath;
      private final String levelId;
      private final Map<LevelResource, Path> resources = Maps.newHashMap();

      public LevelStorageAccess(String p_78276_) throws IOException {
         this.levelId = p_78276_;
         this.levelPath = LevelStorageSource.this.baseDir.resolve(p_78276_);
         this.lock = DirectoryLock.create(this.levelPath);
      }

      public String getLevelId() {
         return this.levelId;
      }

      public Path getLevelPath(LevelResource p_78284_) {
         return this.resources.computeIfAbsent(p_78284_, (p_78303_) -> {
            return this.levelPath.resolve(p_78303_.getId());
         });
      }

      public Path getDimensionPath(ResourceKey<Level> p_197395_) {
         return DimensionType.getStorageFolder(p_197395_, this.levelPath);
      }

      private void checkLock() {
         if (!this.lock.isValid()) {
            throw new IllegalStateException("Lock is no longer valid");
         }
      }

      public PlayerDataStorage createPlayerStorage() {
         this.checkLock();
         return new PlayerDataStorage(this, LevelStorageSource.this.fixerUpper);
      }

      @Nullable
      public LevelSummary getSummary() {
         this.checkLock();
         return LevelStorageSource.this.readLevelData(this.levelPath.toFile(), LevelStorageSource.this.levelSummaryReader(this.levelPath.toFile(), false));
      }

      @Nullable
      public WorldData getDataTag(DynamicOps<Tag> p_211748_, DataPackConfig p_211749_, Lifecycle p_211750_) {
         this.checkLock();
         return LevelStorageSource.this.readLevelData(this.levelPath.toFile(), LevelStorageSource.getLevelData(p_211748_, p_211749_, p_211750_));
      }

      public void readAdditionalLevelSaveData() {
         checkLock();
         LevelStorageSource.this.readLevelData(this.levelPath.toFile(), (file, dataFixer) -> {
            try {
               CompoundTag compoundTag = NbtIo.readCompressed(file);
               net.minecraftforge.common.ForgeHooks.readAdditionalLevelSaveData(compoundTag, this.levelPath);
            } catch (Exception e) {
                LOGGER.error("Exception reading {}", file, e);
            }
            return ""; // Return non-null to prevent level.dat-old inject
         });
      }

      @Nullable
      public DataPackConfig getDataPacks() {
         this.checkLock();
         return LevelStorageSource.this.readLevelData(this.levelPath.toFile(), LevelStorageSource::getDataPacks);
      }

      public void saveDataTag(RegistryAccess p_78288_, WorldData p_78289_) {
         this.saveDataTag(p_78288_, p_78289_, (CompoundTag)null);
      }

      public void saveDataTag(RegistryAccess p_78291_, WorldData p_78292_, @Nullable CompoundTag p_78293_) {
         File file1 = this.levelPath.toFile();
         CompoundTag compoundtag = p_78292_.createTag(p_78291_, p_78293_);
         CompoundTag compoundtag1 = new CompoundTag();
         compoundtag1.put("Data", compoundtag);

         net.minecraftforge.common.ForgeHooks.writeAdditionalLevelSaveData(p_78292_, compoundtag1);

         try {
            File file2 = File.createTempFile("level", ".dat", file1);
            NbtIo.writeCompressed(compoundtag1, file2);
            File file3 = new File(file1, "level.dat_old");
            File file4 = new File(file1, "level.dat");
            Util.safeReplaceFile(file4, file2, file3);
         } catch (Exception exception) {
            LevelStorageSource.LOGGER.error("Failed to save level {}", file1, exception);
         }

      }

      public Optional<Path> getIconFile() {
         return !this.lock.isValid() ? Optional.empty() : Optional.of(this.levelPath.resolve("icon.png"));
      }

      public Path getWorldDir() {
         return levelPath;
      }

      public void deleteLevel() throws IOException {
         this.checkLock();
         final Path path = this.levelPath.resolve("session.lock");
         LevelStorageSource.LOGGER.info("Deleting level {}", (Object)this.levelId);

         for(int i = 1; i <= 5; ++i) {
            LevelStorageSource.LOGGER.info("Attempt {}...", (int)i);

            try {
               Files.walkFileTree(this.levelPath, new SimpleFileVisitor<Path>() {
                  public FileVisitResult visitFile(Path p_78323_, BasicFileAttributes p_78324_) throws IOException {
                     if (!p_78323_.equals(path)) {
                        LevelStorageSource.LOGGER.debug("Deleting {}", (Object)p_78323_);
                        Files.delete(p_78323_);
                     }

                     return FileVisitResult.CONTINUE;
                  }

                  public FileVisitResult postVisitDirectory(Path p_78320_, IOException p_78321_) throws IOException {
                     if (p_78321_ != null) {
                        throw p_78321_;
                     } else {
                        if (p_78320_.equals(LevelStorageAccess.this.levelPath)) {
                           LevelStorageAccess.this.lock.close();
                           Files.deleteIfExists(path);
                        }

                        Files.delete(p_78320_);
                        return FileVisitResult.CONTINUE;
                     }
                  }
               });
               break;
            } catch (IOException ioexception) {
               if (i >= 5) {
                  throw ioexception;
               }

               LevelStorageSource.LOGGER.warn("Failed to delete {}", this.levelPath, ioexception);

               try {
                  Thread.sleep(500L);
               } catch (InterruptedException interruptedexception) {
               }
            }
         }

      }

      public void renameLevel(String p_78298_) throws IOException {
         this.checkLock();
         File file1 = new File(LevelStorageSource.this.baseDir.toFile(), this.levelId);
         if (file1.exists()) {
            File file2 = new File(file1, "level.dat");
            if (file2.exists()) {
               CompoundTag compoundtag = NbtIo.readCompressed(file2);
               CompoundTag compoundtag1 = compoundtag.getCompound("Data");
               compoundtag1.putString("LevelName", p_78298_);
               NbtIo.writeCompressed(compoundtag, file2);
            }

         }
      }

      public long makeWorldBackup() throws IOException {
         this.checkLock();
         String s = LocalDateTime.now().format(LevelStorageSource.FORMATTER) + "_" + this.levelId;
         Path path = LevelStorageSource.this.getBackupPath();

         try {
            Files.createDirectories(Files.exists(path) ? path.toRealPath() : path);
         } catch (IOException ioexception) {
            throw new RuntimeException(ioexception);
         }

         Path path1 = path.resolve(FileUtil.findAvailableName(path, s, ".zip"));
         final ZipOutputStream zipoutputstream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path1)));

         try {
            final Path path2 = Paths.get(this.levelId);
            Files.walkFileTree(this.levelPath, new SimpleFileVisitor<Path>() {
               public FileVisitResult visitFile(Path p_78339_, BasicFileAttributes p_78340_) throws IOException {
                  if (p_78339_.endsWith("session.lock")) {
                     return FileVisitResult.CONTINUE;
                  } else {
                     String s1 = path2.resolve(LevelStorageAccess.this.levelPath.relativize(p_78339_)).toString().replace('\\', '/');
                     ZipEntry zipentry = new ZipEntry(s1);
                     zipoutputstream.putNextEntry(zipentry);
                     com.google.common.io.Files.asByteSource(p_78339_.toFile()).copyTo(zipoutputstream);
                     zipoutputstream.closeEntry();
                     return FileVisitResult.CONTINUE;
                  }
               }
            });
         } catch (Throwable throwable1) {
            try {
               zipoutputstream.close();
            } catch (Throwable throwable) {
               throwable1.addSuppressed(throwable);
            }

            throw throwable1;
         }

         zipoutputstream.close();
         return Files.size(path1);
      }

      public void close() throws IOException {
         this.lock.close();
      }
   }
}
