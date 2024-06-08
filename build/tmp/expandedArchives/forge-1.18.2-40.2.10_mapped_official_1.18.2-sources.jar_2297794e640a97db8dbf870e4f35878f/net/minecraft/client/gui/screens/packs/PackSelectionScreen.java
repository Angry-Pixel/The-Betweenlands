package net.minecraft.client.gui.screens.packs;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class PackSelectionScreen extends Screen {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int LIST_WIDTH = 200;
   private static final Component DRAG_AND_DROP = (new TranslatableComponent("pack.dropInfo")).withStyle(ChatFormatting.GRAY);
   static final Component DIRECTORY_BUTTON_TOOLTIP = new TranslatableComponent("pack.folderInfo");
   private static final int RELOAD_COOLDOWN = 20;
   private static final ResourceLocation DEFAULT_ICON = new ResourceLocation("textures/misc/unknown_pack.png");
   private final PackSelectionModel model;
   private final Screen lastScreen;
   @Nullable
   private PackSelectionScreen.Watcher watcher;
   private long ticksToReload;
   private TransferableSelectionList availablePackList;
   private TransferableSelectionList selectedPackList;
   private final File packDir;
   private Button doneButton;
   private final Map<String, ResourceLocation> packIcons = Maps.newHashMap();

   public PackSelectionScreen(Screen p_99984_, PackRepository p_99985_, Consumer<PackRepository> p_99986_, File p_99987_, Component p_99988_) {
      super(p_99988_);
      this.lastScreen = p_99984_;
      this.model = new PackSelectionModel(this::populateLists, this::getPackIcon, p_99985_, p_99986_);
      this.packDir = p_99987_;
      this.watcher = PackSelectionScreen.Watcher.create(p_99987_);
   }

   public void onClose() {
      this.model.commit();
      this.minecraft.setScreen(this.lastScreen);
      this.closeWatcher();
   }

   private void closeWatcher() {
      if (this.watcher != null) {
         try {
            this.watcher.close();
            this.watcher = null;
         } catch (Exception exception) {
         }
      }

   }

   protected void init() {
      this.doneButton = this.addRenderableWidget(new Button(this.width / 2 + 4, this.height - 48, 150, 20, CommonComponents.GUI_DONE, (p_100036_) -> {
         this.onClose();
      }));
      this.addRenderableWidget(new Button(this.width / 2 - 154, this.height - 48, 150, 20, new TranslatableComponent("pack.openFolder"), (p_100004_) -> {
         Util.getPlatform().openFile(this.packDir);
      }, new Button.OnTooltip() {
         public void onTooltip(Button p_170019_, PoseStack p_170020_, int p_170021_, int p_170022_) {
            PackSelectionScreen.this.renderTooltip(p_170020_, PackSelectionScreen.DIRECTORY_BUTTON_TOOLTIP, p_170021_, p_170022_);
         }

         public void narrateTooltip(Consumer<Component> p_170017_) {
            p_170017_.accept(PackSelectionScreen.DIRECTORY_BUTTON_TOOLTIP);
         }
      }));
      this.availablePackList = new TransferableSelectionList(this.minecraft, 200, this.height, new TranslatableComponent("pack.available.title"));
      this.availablePackList.setLeftPos(this.width / 2 - 4 - 200);
      this.addWidget(this.availablePackList);
      this.selectedPackList = new TransferableSelectionList(this.minecraft, 200, this.height, new TranslatableComponent("pack.selected.title"));
      this.selectedPackList.setLeftPos(this.width / 2 + 4);
      this.addWidget(this.selectedPackList);
      this.reload();
   }

   public void tick() {
      if (this.watcher != null) {
         try {
            if (this.watcher.pollForChanges()) {
               this.ticksToReload = 20L;
            }
         } catch (IOException ioexception) {
            LOGGER.warn("Failed to poll for directory {} changes, stopping", (Object)this.packDir);
            this.closeWatcher();
         }
      }

      if (this.ticksToReload > 0L && --this.ticksToReload == 0L) {
         this.reload();
      }

   }

   private void populateLists() {
      this.updateList(this.selectedPackList, this.model.getSelected());
      this.updateList(this.availablePackList, this.model.getUnselected());
      this.doneButton.active = !this.selectedPackList.children().isEmpty();
   }

   private void updateList(TransferableSelectionList p_100014_, Stream<PackSelectionModel.Entry> p_100015_) {
      p_100014_.children().clear();
      p_100015_.filter(PackSelectionModel.Entry::notHidden).forEach((p_170000_) -> {
         p_100014_.children().add(new TransferableSelectionList.PackEntry(this.minecraft, p_100014_, this, p_170000_));
      });
   }

   private void reload() {
      this.model.findNewPacks();
      this.populateLists();
      this.ticksToReload = 0L;
      this.packIcons.clear();
   }

   public void render(PoseStack p_99995_, int p_99996_, int p_99997_, float p_99998_) {
      this.renderDirtBackground(0);
      this.availablePackList.render(p_99995_, p_99996_, p_99997_, p_99998_);
      this.selectedPackList.render(p_99995_, p_99996_, p_99997_, p_99998_);
      drawCenteredString(p_99995_, this.font, this.title, this.width / 2, 8, 16777215);
      drawCenteredString(p_99995_, this.font, DRAG_AND_DROP, this.width / 2, 20, 16777215);
      super.render(p_99995_, p_99996_, p_99997_, p_99998_);
   }

   protected static void copyPacks(Minecraft p_100000_, List<Path> p_100001_, Path p_100002_) {
      MutableBoolean mutableboolean = new MutableBoolean();
      p_100001_.forEach((p_170009_) -> {
         try {
            Stream<Path> stream = Files.walk(p_170009_);

            try {
               stream.forEach((p_170005_) -> {
                  try {
                     Util.copyBetweenDirs(p_170009_.getParent(), p_100002_, p_170005_);
                  } catch (IOException ioexception1) {
                     LOGGER.warn("Failed to copy datapack file  from {} to {}", p_170005_, p_100002_, ioexception1);
                     mutableboolean.setTrue();
                  }

               });
            } catch (Throwable throwable1) {
               if (stream != null) {
                  try {
                     stream.close();
                  } catch (Throwable throwable) {
                     throwable1.addSuppressed(throwable);
                  }
               }

               throw throwable1;
            }

            if (stream != null) {
               stream.close();
            }
         } catch (IOException ioexception) {
            LOGGER.warn("Failed to copy datapack file from {} to {}", p_170009_, p_100002_);
            mutableboolean.setTrue();
         }

      });
      if (mutableboolean.isTrue()) {
         SystemToast.onPackCopyFailure(p_100000_, p_100002_.toString());
      }

   }

   public void onFilesDrop(List<Path> p_100029_) {
      String s = p_100029_.stream().map(Path::getFileName).map(Path::toString).collect(Collectors.joining(", "));
      this.minecraft.setScreen(new ConfirmScreen((p_170012_) -> {
         if (p_170012_) {
            copyPacks(this.minecraft, p_100029_, this.packDir.toPath());
            this.reload();
         }

         this.minecraft.setScreen(this);
      }, new TranslatableComponent("pack.dropConfirm"), new TextComponent(s)));
   }

   private ResourceLocation loadPackIcon(TextureManager p_100017_, Pack p_100018_) {
      try {
         PackResources packresources = p_100018_.open();

         ResourceLocation $$4;
         label84: {
            ResourceLocation resourcelocation2;
            try {
               InputStream inputstream = packresources.getRootResource("pack.png");

               label86: {
                  try {
                     if (inputstream != null) {
                        String s = p_100018_.getId();
                        ResourceLocation resourcelocation1 = new ResourceLocation("minecraft", "pack/" + Util.sanitizeName(s, ResourceLocation::validPathChar) + "/" + Hashing.sha1().hashUnencodedChars(s) + "/icon");
                        NativeImage nativeimage = NativeImage.read(inputstream);
                        p_100017_.register(resourcelocation1, new DynamicTexture(nativeimage));
                        resourcelocation2 = resourcelocation1;
                        break label86;
                     }

                     $$4 = DEFAULT_ICON;
                  } catch (Throwable throwable2) {
                     if (inputstream != null) {
                        try {
                           inputstream.close();
                        } catch (Throwable throwable1) {
                           throwable2.addSuppressed(throwable1);
                        }
                     }

                     throw throwable2;
                  }

                  if (inputstream != null) {
                     inputstream.close();
                  }
                  break label84;
               }

               if (inputstream != null) {
                  inputstream.close();
               }
            } catch (Throwable throwable3) {
               if (packresources != null) {
                  try {
                     packresources.close();
                  } catch (Throwable throwable) {
                     throwable3.addSuppressed(throwable);
                  }
               }

               throw throwable3;
            }

            if (packresources != null) {
               packresources.close();
            }

            return resourcelocation2;
         }

         if (packresources != null) {
            packresources.close();
         }

         return $$4;
      } catch (FileNotFoundException filenotfoundexception) {
      } catch (Exception exception) {
         LOGGER.warn("Failed to load icon from pack {}", p_100018_.getId(), exception);
      }

      return DEFAULT_ICON;
   }

   private ResourceLocation getPackIcon(Pack p_99990_) {
      return this.packIcons.computeIfAbsent(p_99990_.getId(), (p_169997_) -> {
         return this.loadPackIcon(this.minecraft.getTextureManager(), p_99990_);
      });
   }

   @OnlyIn(Dist.CLIENT)
   static class Watcher implements AutoCloseable {
      private final WatchService watcher;
      private final Path packPath;

      public Watcher(File p_100045_) throws IOException {
         this.packPath = p_100045_.toPath();
         this.watcher = this.packPath.getFileSystem().newWatchService();

         try {
            this.watchDir(this.packPath);
            DirectoryStream<Path> directorystream = Files.newDirectoryStream(this.packPath);

            try {
               for(Path path : directorystream) {
                  if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
                     this.watchDir(path);
                  }
               }
            } catch (Throwable throwable1) {
               if (directorystream != null) {
                  try {
                     directorystream.close();
                  } catch (Throwable throwable) {
                     throwable1.addSuppressed(throwable);
                  }
               }

               throw throwable1;
            }

            if (directorystream != null) {
               directorystream.close();
            }

         } catch (Exception exception) {
            this.watcher.close();
            throw exception;
         }
      }

      @Nullable
      public static PackSelectionScreen.Watcher create(File p_100048_) {
         try {
            return new PackSelectionScreen.Watcher(p_100048_);
         } catch (IOException ioexception) {
            PackSelectionScreen.LOGGER.warn("Failed to initialize pack directory {} monitoring", p_100048_, ioexception);
            return null;
         }
      }

      private void watchDir(Path p_100050_) throws IOException {
         p_100050_.register(this.watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
      }

      public boolean pollForChanges() throws IOException {
         boolean flag = false;

         WatchKey watchkey;
         while((watchkey = this.watcher.poll()) != null) {
            for(WatchEvent<?> watchevent : watchkey.pollEvents()) {
               flag = true;
               if (watchkey.watchable() == this.packPath && watchevent.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                  Path path = this.packPath.resolve((Path)watchevent.context());
                  if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
                     this.watchDir(path);
                  }
               }
            }

            watchkey.reset();
         }

         return flag;
      }

      public void close() throws IOException {
         this.watcher.close();
      }
   }
}
