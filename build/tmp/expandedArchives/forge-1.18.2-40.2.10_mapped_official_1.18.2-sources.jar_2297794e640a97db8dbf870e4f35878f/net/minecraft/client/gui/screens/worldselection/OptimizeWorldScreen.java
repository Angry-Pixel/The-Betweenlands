package net.minecraft.client.gui.screens.worldselection;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.WorldStem;
import net.minecraft.util.Mth;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class OptimizeWorldScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Object2IntMap<ResourceKey<Level>> DIMENSION_COLORS = Util.make(new Object2IntOpenCustomHashMap<>(Util.identityStrategy()), (p_101324_) -> {
      p_101324_.put(Level.OVERWORLD, -13408734);
      p_101324_.put(Level.NETHER, -10075085);
      p_101324_.put(Level.END, -8943531);
      p_101324_.defaultReturnValue(-2236963);
   });
   private final BooleanConsumer callback;
   private final WorldUpgrader upgrader;

   @Nullable
   public static OptimizeWorldScreen create(Minecraft p_101316_, BooleanConsumer p_101317_, DataFixer p_101318_, LevelStorageSource.LevelStorageAccess p_101319_, boolean p_101320_) {
      try {
         WorldStem worldstem = p_101316_.makeWorldStem(p_101319_, false);

         OptimizeWorldScreen optimizeworldscreen;
         try {
            WorldData worlddata = worldstem.worldData();
            p_101319_.saveDataTag(worldstem.registryAccess(), worlddata);
            optimizeworldscreen = new OptimizeWorldScreen(p_101317_, p_101318_, p_101319_, worlddata.getLevelSettings(), p_101320_, worlddata.worldGenSettings());
         } catch (Throwable throwable1) {
            if (worldstem != null) {
               try {
                  worldstem.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }
            }

            throw throwable1;
         }

         if (worldstem != null) {
            worldstem.close();
         }

         return optimizeworldscreen;
      } catch (Exception exception) {
         LOGGER.warn("Failed to load datapacks, can't optimize world", (Throwable)exception);
         return null;
      }
   }

   private OptimizeWorldScreen(BooleanConsumer p_194064_, DataFixer p_194065_, LevelStorageSource.LevelStorageAccess p_194066_, LevelSettings p_194067_, boolean p_194068_, WorldGenSettings p_194069_) {
      super(new TranslatableComponent("optimizeWorld.title", p_194067_.levelName()));
      this.callback = p_194064_;
      this.upgrader = new WorldUpgrader(p_194066_, p_194065_, p_194069_, p_194068_);
   }

   protected void init() {
      super.init();
      this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 150, 200, 20, CommonComponents.GUI_CANCEL, (p_101322_) -> {
         this.upgrader.cancel();
         this.callback.accept(false);
      }));
   }

   public void tick() {
      if (this.upgrader.isFinished()) {
         this.callback.accept(true);
      }

   }

   public void onClose() {
      this.callback.accept(false);
   }

   public void removed() {
      this.upgrader.cancel();
   }

   public void render(PoseStack p_101311_, int p_101312_, int p_101313_, float p_101314_) {
      this.renderBackground(p_101311_);
      drawCenteredString(p_101311_, this.font, this.title, this.width / 2, 20, 16777215);
      int i = this.width / 2 - 150;
      int j = this.width / 2 + 150;
      int k = this.height / 4 + 100;
      int l = k + 10;
      drawCenteredString(p_101311_, this.font, this.upgrader.getStatus(), this.width / 2, k - 9 - 2, 10526880);
      if (this.upgrader.getTotalChunks() > 0) {
         fill(p_101311_, i - 1, k - 1, j + 1, l + 1, -16777216);
         drawString(p_101311_, this.font, new TranslatableComponent("optimizeWorld.info.converted", this.upgrader.getConverted()), i, 40, 10526880);
         drawString(p_101311_, this.font, new TranslatableComponent("optimizeWorld.info.skipped", this.upgrader.getSkipped()), i, 40 + 9 + 3, 10526880);
         drawString(p_101311_, this.font, new TranslatableComponent("optimizeWorld.info.total", this.upgrader.getTotalChunks()), i, 40 + (9 + 3) * 2, 10526880);
         int i1 = 0;

         for(ResourceKey<Level> resourcekey : this.upgrader.levels()) {
            int j1 = Mth.floor(this.upgrader.dimensionProgress(resourcekey) * (float)(j - i));
            fill(p_101311_, i + i1, k, i + i1 + j1, l, DIMENSION_COLORS.getInt(resourcekey));
            i1 += j1;
         }

         int k1 = this.upgrader.getConverted() + this.upgrader.getSkipped();
         drawCenteredString(p_101311_, this.font, k1 + " / " + this.upgrader.getTotalChunks(), this.width / 2, k + 2 * 9 + 2, 10526880);
         drawCenteredString(p_101311_, this.font, Mth.floor(this.upgrader.getProgress() * 100.0F) + "%", this.width / 2, k + (l - k) / 2 - 9 / 2, 10526880);
      }

      super.render(p_101311_, p_101312_, p_101313_, p_101314_);
   }
}