package net.minecraft.data.models.model;

import com.google.gson.JsonElement;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class TexturedModel {
   public static final TexturedModel.Provider CUBE = createDefault(TextureMapping::cube, ModelTemplates.CUBE_ALL);
   public static final TexturedModel.Provider CUBE_MIRRORED = createDefault(TextureMapping::cube, ModelTemplates.CUBE_MIRRORED_ALL);
   public static final TexturedModel.Provider COLUMN = createDefault(TextureMapping::column, ModelTemplates.CUBE_COLUMN);
   public static final TexturedModel.Provider COLUMN_HORIZONTAL = createDefault(TextureMapping::column, ModelTemplates.CUBE_COLUMN_HORIZONTAL);
   public static final TexturedModel.Provider CUBE_TOP_BOTTOM = createDefault(TextureMapping::cubeBottomTop, ModelTemplates.CUBE_BOTTOM_TOP);
   public static final TexturedModel.Provider CUBE_TOP = createDefault(TextureMapping::cubeTop, ModelTemplates.CUBE_TOP);
   public static final TexturedModel.Provider ORIENTABLE_ONLY_TOP = createDefault(TextureMapping::orientableCubeOnlyTop, ModelTemplates.CUBE_ORIENTABLE);
   public static final TexturedModel.Provider ORIENTABLE = createDefault(TextureMapping::orientableCube, ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM);
   public static final TexturedModel.Provider CARPET = createDefault(TextureMapping::wool, ModelTemplates.CARPET);
   public static final TexturedModel.Provider GLAZED_TERRACOTTA = createDefault(TextureMapping::pattern, ModelTemplates.GLAZED_TERRACOTTA);
   public static final TexturedModel.Provider CORAL_FAN = createDefault(TextureMapping::fan, ModelTemplates.CORAL_FAN);
   public static final TexturedModel.Provider PARTICLE_ONLY = createDefault(TextureMapping::particle, ModelTemplates.PARTICLE_ONLY);
   public static final TexturedModel.Provider ANVIL = createDefault(TextureMapping::top, ModelTemplates.ANVIL);
   public static final TexturedModel.Provider LEAVES = createDefault(TextureMapping::cube, ModelTemplates.LEAVES);
   public static final TexturedModel.Provider LANTERN = createDefault(TextureMapping::lantern, ModelTemplates.LANTERN);
   public static final TexturedModel.Provider HANGING_LANTERN = createDefault(TextureMapping::lantern, ModelTemplates.HANGING_LANTERN);
   public static final TexturedModel.Provider SEAGRASS = createDefault(TextureMapping::defaultTexture, ModelTemplates.SEAGRASS);
   public static final TexturedModel.Provider COLUMN_ALT = createDefault(TextureMapping::logColumn, ModelTemplates.CUBE_COLUMN);
   public static final TexturedModel.Provider COLUMN_HORIZONTAL_ALT = createDefault(TextureMapping::logColumn, ModelTemplates.CUBE_COLUMN_HORIZONTAL);
   public static final TexturedModel.Provider TOP_BOTTOM_WITH_WALL = createDefault(TextureMapping::cubeBottomTopWithWall, ModelTemplates.CUBE_BOTTOM_TOP);
   public static final TexturedModel.Provider COLUMN_WITH_WALL = createDefault(TextureMapping::columnWithWall, ModelTemplates.CUBE_COLUMN);
   private final TextureMapping mapping;
   private final ModelTemplate template;

   private TexturedModel(TextureMapping p_125930_, ModelTemplate p_125931_) {
      this.mapping = p_125930_;
      this.template = p_125931_;
   }

   public ModelTemplate getTemplate() {
      return this.template;
   }

   public TextureMapping getMapping() {
      return this.mapping;
   }

   public TexturedModel updateTextures(Consumer<TextureMapping> p_125941_) {
      p_125941_.accept(this.mapping);
      return this;
   }

   public ResourceLocation create(Block p_125938_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_125939_) {
      return this.template.create(p_125938_, this.mapping, p_125939_);
   }

   public ResourceLocation createWithSuffix(Block p_125934_, String p_125935_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_125936_) {
      return this.template.createWithSuffix(p_125934_, p_125935_, this.mapping, p_125936_);
   }

   private static TexturedModel.Provider createDefault(Function<Block, TextureMapping> p_125943_, ModelTemplate p_125944_) {
      return (p_125948_) -> {
         return new TexturedModel(p_125943_.apply(p_125948_), p_125944_);
      };
   }

   public static TexturedModel createAllSame(ResourceLocation p_125950_) {
      return new TexturedModel(TextureMapping.cube(p_125950_), ModelTemplates.CUBE_ALL);
   }

   @FunctionalInterface
   public interface Provider {
      TexturedModel get(Block p_125965_);

      default ResourceLocation create(Block p_125957_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_125958_) {
         return this.get(p_125957_).create(p_125957_, p_125958_);
      }

      default ResourceLocation createWithSuffix(Block p_125953_, String p_125954_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_125955_) {
         return this.get(p_125953_).createWithSuffix(p_125953_, p_125954_, p_125955_);
      }

      default TexturedModel.Provider updateTexture(Consumer<TextureMapping> p_125960_) {
         return (p_125963_) -> {
            return this.get(p_125963_).updateTextures(p_125960_);
         };
      }
   }
}