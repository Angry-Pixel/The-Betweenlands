package net.minecraft.client.gui.screens;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class PresetFlatWorldScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int SLOT_TEX_SIZE = 128;
   private static final int SLOT_BG_SIZE = 18;
   private static final int SLOT_STAT_HEIGHT = 20;
   private static final int SLOT_BG_X = 1;
   private static final int SLOT_BG_Y = 1;
   private static final int SLOT_FG_X = 2;
   private static final int SLOT_FG_Y = 2;
   static final List<PresetFlatWorldScreen.PresetInfo> PRESETS = Lists.newArrayList();
   private static final ResourceKey<Biome> DEFAULT_BIOME = Biomes.PLAINS;
   final CreateFlatWorldScreen parent;
   private Component shareText;
   private Component listText;
   private PresetFlatWorldScreen.PresetsList list;
   private Button selectButton;
   EditBox export;
   FlatLevelGeneratorSettings settings;

   public PresetFlatWorldScreen(CreateFlatWorldScreen p_96379_) {
      super(new TranslatableComponent("createWorld.customize.presets.title"));
      this.parent = p_96379_;
   }

   @Nullable
   private static FlatLayerInfo getLayerInfoFromString(String p_96414_, int p_96415_) {
      String[] astring = p_96414_.split("\\*", 2);
      int i;
      if (astring.length == 2) {
         try {
            i = Math.max(Integer.parseInt(astring[0]), 0);
         } catch (NumberFormatException numberformatexception) {
            LOGGER.error("Error while parsing flat world string => {}", (Object)numberformatexception.getMessage());
            return null;
         }
      } else {
         i = 1;
      }

      int j = Math.min(p_96415_ + i, DimensionType.Y_SIZE);
      int k = j - p_96415_;
      String s = astring[astring.length - 1];

      Block block;
      try {
         block = Registry.BLOCK.getOptional(new ResourceLocation(s)).orElse((Block)null);
      } catch (Exception exception) {
         LOGGER.error("Error while parsing flat world string => {}", (Object)exception.getMessage());
         return null;
      }

      if (block == null) {
         LOGGER.error("Error while parsing flat world string => Unknown block, {}", (Object)s);
         return null;
      } else {
         return new FlatLayerInfo(k, block);
      }
   }

   private static List<FlatLayerInfo> getLayersInfoFromString(String p_96446_) {
      List<FlatLayerInfo> list = Lists.newArrayList();
      String[] astring = p_96446_.split(",");
      int i = 0;

      for(String s : astring) {
         FlatLayerInfo flatlayerinfo = getLayerInfoFromString(s, i);
         if (flatlayerinfo == null) {
            return Collections.emptyList();
         }

         list.add(flatlayerinfo);
         i += flatlayerinfo.getHeight();
      }

      return list;
   }

   public static FlatLevelGeneratorSettings fromString(Registry<Biome> p_211772_, Registry<StructureSet> p_211773_, String p_211774_, FlatLevelGeneratorSettings p_211775_) {
      Iterator<String> iterator = Splitter.on(';').split(p_211774_).iterator();
      if (!iterator.hasNext()) {
         return FlatLevelGeneratorSettings.getDefault(p_211772_, p_211773_);
      } else {
         List<FlatLayerInfo> list = getLayersInfoFromString(iterator.next());
         if (list.isEmpty()) {
            return FlatLevelGeneratorSettings.getDefault(p_211772_, p_211773_);
         } else {
            FlatLevelGeneratorSettings flatlevelgeneratorsettings = p_211775_.withLayers(list, p_211775_.structureOverrides());
            ResourceKey<Biome> resourcekey = DEFAULT_BIOME;
            if (iterator.hasNext()) {
               try {
                  ResourceLocation resourcelocation = new ResourceLocation(iterator.next());
                  resourcekey = ResourceKey.create(Registry.BIOME_REGISTRY, resourcelocation);
                  p_211772_.getOptional(resourcekey).orElseThrow(() -> {
                     return new IllegalArgumentException("Invalid Biome: " + resourcelocation);
                  });
               } catch (Exception exception) {
                  LOGGER.error("Error while parsing flat world string => {}", (Object)exception.getMessage());
                  resourcekey = DEFAULT_BIOME;
               }
            }

            flatlevelgeneratorsettings.setBiome(p_211772_.getOrCreateHolder(resourcekey));
            return flatlevelgeneratorsettings;
         }
      }
   }

   static String save(FlatLevelGeneratorSettings p_205394_) {
      StringBuilder stringbuilder = new StringBuilder();

      for(int i = 0; i < p_205394_.getLayersInfo().size(); ++i) {
         if (i > 0) {
            stringbuilder.append(",");
         }

         stringbuilder.append(p_205394_.getLayersInfo().get(i));
      }

      stringbuilder.append(";");
      stringbuilder.append(p_205394_.getBiome().unwrapKey().map(ResourceKey::location).orElseThrow(() -> {
         return new IllegalStateException("Biome not registered");
      }));
      return stringbuilder.toString();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.shareText = new TranslatableComponent("createWorld.customize.presets.share");
      this.listText = new TranslatableComponent("createWorld.customize.presets.list");
      this.export = new EditBox(this.font, 50, 40, this.width - 100, 20, this.shareText);
      this.export.setMaxLength(1230);
      RegistryAccess registryaccess = this.parent.parent.worldGenSettingsComponent.registryHolder();
      Registry<Biome> registry = registryaccess.registryOrThrow(Registry.BIOME_REGISTRY);
      Registry<StructureSet> registry1 = registryaccess.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY);
      this.export.setValue(save(this.parent.settings()));
      this.settings = this.parent.settings();
      this.addWidget(this.export);
      this.list = new PresetFlatWorldScreen.PresetsList();
      this.addWidget(this.list);
      this.selectButton = this.addRenderableWidget(new Button(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableComponent("createWorld.customize.presets.select"), (p_211770_) -> {
         FlatLevelGeneratorSettings flatlevelgeneratorsettings = fromString(registry, registry1, this.export.getValue(), this.settings);
         this.parent.setConfig(flatlevelgeneratorsettings);
         this.minecraft.setScreen(this.parent);
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 5, this.height - 28, 150, 20, CommonComponents.GUI_CANCEL, (p_96394_) -> {
         this.minecraft.setScreen(this.parent);
      }));
      this.updateButtonValidity(this.list.getSelected() != null);
   }

   public boolean mouseScrolled(double p_96381_, double p_96382_, double p_96383_) {
      return this.list.mouseScrolled(p_96381_, p_96382_, p_96383_);
   }

   public void resize(Minecraft p_96390_, int p_96391_, int p_96392_) {
      String s = this.export.getValue();
      this.init(p_96390_, p_96391_, p_96392_);
      this.export.setValue(s);
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public void render(PoseStack p_96385_, int p_96386_, int p_96387_, float p_96388_) {
      this.renderBackground(p_96385_);
      this.list.render(p_96385_, p_96386_, p_96387_, p_96388_);
      p_96385_.pushPose();
      p_96385_.translate(0.0D, 0.0D, 400.0D);
      drawCenteredString(p_96385_, this.font, this.title, this.width / 2, 8, 16777215);
      drawString(p_96385_, this.font, this.shareText, 50, 30, 10526880);
      drawString(p_96385_, this.font, this.listText, 50, 70, 10526880);
      p_96385_.popPose();
      this.export.render(p_96385_, p_96386_, p_96387_, p_96388_);
      super.render(p_96385_, p_96386_, p_96387_, p_96388_);
   }

   public void tick() {
      this.export.tick();
      super.tick();
   }

   public void updateButtonValidity(boolean p_96450_) {
      this.selectButton.active = p_96450_ || this.export.getValue().length() > 1;
   }

   private static void preset(Component p_210850_, ItemLike p_210851_, ResourceKey<Biome> p_210852_, Set<ResourceKey<StructureSet>> p_210853_, boolean p_210854_, boolean p_210855_, FlatLayerInfo... p_210856_) {
      PRESETS.add(new PresetFlatWorldScreen.PresetInfo(p_210851_.asItem(), p_210850_, (p_210848_) -> {
         Registry<Biome> registry = p_210848_.registryOrThrow(Registry.BIOME_REGISTRY);
         Registry<StructureSet> registry1 = p_210848_.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY);
         HolderSet.Direct<StructureSet> direct = HolderSet.direct(p_210853_.stream().flatMap((p_210841_) -> {
            return registry1.getHolder(p_210841_).stream();
         }).collect(Collectors.toList()));
         FlatLevelGeneratorSettings flatlevelgeneratorsettings = new FlatLevelGeneratorSettings(Optional.of(direct), registry);
         if (p_210854_) {
            flatlevelgeneratorsettings.setDecoration();
         }

         if (p_210855_) {
            flatlevelgeneratorsettings.setAddLakes();
         }

         for(int i = p_210856_.length - 1; i >= 0; --i) {
            flatlevelgeneratorsettings.getLayersInfo().add(p_210856_[i]);
         }

         flatlevelgeneratorsettings.setBiome(registry.getOrCreateHolder(p_210852_));
         flatlevelgeneratorsettings.updateLayers();
         return flatlevelgeneratorsettings;
      }));
   }

   static {
      preset(new TranslatableComponent("createWorld.customize.preset.classic_flat"), Blocks.GRASS_BLOCK, Biomes.PLAINS, Set.of(BuiltinStructureSets.VILLAGES), false, false, new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(2, Blocks.DIRT), new FlatLayerInfo(1, Blocks.BEDROCK));
      preset(new TranslatableComponent("createWorld.customize.preset.tunnelers_dream"), Blocks.STONE, Biomes.WINDSWEPT_HILLS, Set.of(BuiltinStructureSets.MINESHAFTS, BuiltinStructureSets.STRONGHOLDS), true, false, new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(230, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      preset(new TranslatableComponent("createWorld.customize.preset.water_world"), Items.WATER_BUCKET, Biomes.DEEP_OCEAN, Set.of(BuiltinStructureSets.OCEAN_RUINS, BuiltinStructureSets.SHIPWRECKS, BuiltinStructureSets.OCEAN_MONUMENTS), false, false, new FlatLayerInfo(90, Blocks.WATER), new FlatLayerInfo(5, Blocks.GRAVEL), new FlatLayerInfo(5, Blocks.DIRT), new FlatLayerInfo(5, Blocks.STONE), new FlatLayerInfo(64, Blocks.DEEPSLATE), new FlatLayerInfo(1, Blocks.BEDROCK));
      preset(new TranslatableComponent("createWorld.customize.preset.overworld"), Blocks.GRASS, Biomes.PLAINS, Set.of(BuiltinStructureSets.VILLAGES, BuiltinStructureSets.MINESHAFTS, BuiltinStructureSets.PILLAGER_OUTPOSTS, BuiltinStructureSets.RUINED_PORTALS, BuiltinStructureSets.STRONGHOLDS), true, true, new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      preset(new TranslatableComponent("createWorld.customize.preset.snowy_kingdom"), Blocks.SNOW, Biomes.SNOWY_PLAINS, Set.of(BuiltinStructureSets.VILLAGES, BuiltinStructureSets.IGLOOS), false, false, new FlatLayerInfo(1, Blocks.SNOW), new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(59, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      preset(new TranslatableComponent("createWorld.customize.preset.bottomless_pit"), Items.FEATHER, Biomes.PLAINS, Set.of(BuiltinStructureSets.VILLAGES), false, false, new FlatLayerInfo(1, Blocks.GRASS_BLOCK), new FlatLayerInfo(3, Blocks.DIRT), new FlatLayerInfo(2, Blocks.COBBLESTONE));
      preset(new TranslatableComponent("createWorld.customize.preset.desert"), Blocks.SAND, Biomes.DESERT, Set.of(BuiltinStructureSets.VILLAGES, BuiltinStructureSets.DESERT_PYRAMIDS, BuiltinStructureSets.MINESHAFTS, BuiltinStructureSets.STRONGHOLDS), true, false, new FlatLayerInfo(8, Blocks.SAND), new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      preset(new TranslatableComponent("createWorld.customize.preset.redstone_ready"), Items.REDSTONE, Biomes.DESERT, Set.of(), false, false, new FlatLayerInfo(116, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));
      preset(new TranslatableComponent("createWorld.customize.preset.the_void"), Blocks.BARRIER, Biomes.THE_VOID, Set.of(), true, false, new FlatLayerInfo(1, Blocks.AIR));
   }

   @OnlyIn(Dist.CLIENT)
   static class PresetInfo {
      public final Item icon;
      public final Component name;
      public final Function<RegistryAccess, FlatLevelGeneratorSettings> settings;

      public PresetInfo(Item p_96458_, Component p_96459_, Function<RegistryAccess, FlatLevelGeneratorSettings> p_96460_) {
         this.icon = p_96458_;
         this.name = p_96459_;
         this.settings = p_96460_;
      }

      public Component getName() {
         return this.name;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class PresetsList extends ObjectSelectionList<PresetFlatWorldScreen.PresetsList.Entry> {
      public PresetsList() {
         super(PresetFlatWorldScreen.this.minecraft, PresetFlatWorldScreen.this.width, PresetFlatWorldScreen.this.height, 80, PresetFlatWorldScreen.this.height - 37, 24);

         for(PresetFlatWorldScreen.PresetInfo presetflatworldscreen$presetinfo : PresetFlatWorldScreen.PRESETS) {
            this.addEntry(new PresetFlatWorldScreen.PresetsList.Entry(presetflatworldscreen$presetinfo));
         }

      }

      public void setSelected(@Nullable PresetFlatWorldScreen.PresetsList.Entry p_96472_) {
         super.setSelected(p_96472_);
         PresetFlatWorldScreen.this.updateButtonValidity(p_96472_ != null);
      }

      protected boolean isFocused() {
         return PresetFlatWorldScreen.this.getFocused() == this;
      }

      public boolean keyPressed(int p_96466_, int p_96467_, int p_96468_) {
         if (super.keyPressed(p_96466_, p_96467_, p_96468_)) {
            return true;
         } else {
            if ((p_96466_ == 257 || p_96466_ == 335) && this.getSelected() != null) {
               this.getSelected().select();
            }

            return false;
         }
      }

      @OnlyIn(Dist.CLIENT)
      public class Entry extends ObjectSelectionList.Entry<PresetFlatWorldScreen.PresetsList.Entry> {
         private final PresetFlatWorldScreen.PresetInfo preset;

         public Entry(PresetFlatWorldScreen.PresetInfo p_169360_) {
            this.preset = p_169360_;
         }

         public void render(PoseStack p_96489_, int p_96490_, int p_96491_, int p_96492_, int p_96493_, int p_96494_, int p_96495_, int p_96496_, boolean p_96497_, float p_96498_) {
            this.blitSlot(p_96489_, p_96492_, p_96491_, this.preset.icon);
            PresetFlatWorldScreen.this.font.draw(p_96489_, this.preset.name, (float)(p_96492_ + 18 + 5), (float)(p_96491_ + 6), 16777215);
         }

         public boolean mouseClicked(double p_96481_, double p_96482_, int p_96483_) {
            if (p_96483_ == 0) {
               this.select();
            }

            return false;
         }

         void select() {
            PresetsList.this.setSelected(this);
            PresetFlatWorldScreen.this.settings = this.preset.settings.apply(PresetFlatWorldScreen.this.parent.parent.worldGenSettingsComponent.registryHolder());
            PresetFlatWorldScreen.this.export.setValue(PresetFlatWorldScreen.save(PresetFlatWorldScreen.this.settings));
            PresetFlatWorldScreen.this.export.moveCursorToStart();
         }

         private void blitSlot(PoseStack p_96500_, int p_96501_, int p_96502_, Item p_96503_) {
            this.blitSlotBg(p_96500_, p_96501_ + 1, p_96502_ + 1);
            PresetFlatWorldScreen.this.itemRenderer.renderGuiItem(new ItemStack(p_96503_), p_96501_ + 2, p_96502_ + 2);
         }

         private void blitSlotBg(PoseStack p_96485_, int p_96486_, int p_96487_) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, GuiComponent.STATS_ICON_LOCATION);
            GuiComponent.blit(p_96485_, p_96486_, p_96487_, PresetFlatWorldScreen.this.getBlitOffset(), 0.0F, 0.0F, 18, 18, 128, 128);
         }

         public Component getNarration() {
            return new TranslatableComponent("narrator.select", this.preset.getName());
         }
      }
   }
}