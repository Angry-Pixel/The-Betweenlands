package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Sheets {
   public static final ResourceLocation SHULKER_SHEET = new ResourceLocation("textures/atlas/shulker_boxes.png");
   public static final ResourceLocation BED_SHEET = new ResourceLocation("textures/atlas/beds.png");
   public static final ResourceLocation BANNER_SHEET = new ResourceLocation("textures/atlas/banner_patterns.png");
   public static final ResourceLocation SHIELD_SHEET = new ResourceLocation("textures/atlas/shield_patterns.png");
   public static final ResourceLocation SIGN_SHEET = new ResourceLocation("textures/atlas/signs.png");
   public static final ResourceLocation CHEST_SHEET = new ResourceLocation("textures/atlas/chest.png");
   private static final RenderType SHULKER_BOX_SHEET_TYPE = RenderType.entityCutoutNoCull(SHULKER_SHEET);
   private static final RenderType BED_SHEET_TYPE = RenderType.entitySolid(BED_SHEET);
   private static final RenderType BANNER_SHEET_TYPE = RenderType.entityNoOutline(BANNER_SHEET);
   private static final RenderType SHIELD_SHEET_TYPE = RenderType.entityNoOutline(SHIELD_SHEET);
   private static final RenderType SIGN_SHEET_TYPE = RenderType.entityCutoutNoCull(SIGN_SHEET);
   private static final RenderType CHEST_SHEET_TYPE = RenderType.entityCutout(CHEST_SHEET);
   private static final RenderType SOLID_BLOCK_SHEET = RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS);
   private static final RenderType CUTOUT_BLOCK_SHEET = RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS);
   private static final RenderType TRANSLUCENT_ITEM_CULL_BLOCK_SHEET = RenderType.itemEntityTranslucentCull(TextureAtlas.LOCATION_BLOCKS);
   private static final RenderType TRANSLUCENT_CULL_BLOCK_SHEET = RenderType.entityTranslucentCull(TextureAtlas.LOCATION_BLOCKS);
   public static final Material DEFAULT_SHULKER_TEXTURE_LOCATION = new Material(SHULKER_SHEET, new ResourceLocation("entity/shulker/shulker"));
   public static final List<Material> SHULKER_TEXTURE_LOCATION = Stream.of("white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black").map((p_110784_) -> {
      return new Material(SHULKER_SHEET, new ResourceLocation("entity/shulker/shulker_" + p_110784_));
   }).collect(ImmutableList.toImmutableList());
   public static final Map<WoodType, Material> SIGN_MATERIALS = WoodType.values().collect(Collectors.toMap(Function.identity(), Sheets::createSignMaterial));
   public static final Map<BannerPattern, Material> BANNER_MATERIALS = Arrays.stream(BannerPattern.values()).collect(Collectors.toMap(Function.identity(), Sheets::createBannerMaterial));
   public static final Map<BannerPattern, Material> SHIELD_MATERIALS = Arrays.stream(BannerPattern.values()).collect(Collectors.toMap(Function.identity(), Sheets::createShieldMaterial));
   public static final Material[] BED_TEXTURES = Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map((p_110766_) -> {
      return new Material(BED_SHEET, new ResourceLocation("entity/bed/" + p_110766_.getName()));
   }).toArray((p_110764_) -> {
      return new Material[p_110764_];
   });
   public static final Material CHEST_TRAP_LOCATION = chestMaterial("trapped");
   public static final Material CHEST_TRAP_LOCATION_LEFT = chestMaterial("trapped_left");
   public static final Material CHEST_TRAP_LOCATION_RIGHT = chestMaterial("trapped_right");
   public static final Material CHEST_XMAS_LOCATION = chestMaterial("christmas");
   public static final Material CHEST_XMAS_LOCATION_LEFT = chestMaterial("christmas_left");
   public static final Material CHEST_XMAS_LOCATION_RIGHT = chestMaterial("christmas_right");
   public static final Material CHEST_LOCATION = chestMaterial("normal");
   public static final Material CHEST_LOCATION_LEFT = chestMaterial("normal_left");
   public static final Material CHEST_LOCATION_RIGHT = chestMaterial("normal_right");
   public static final Material ENDER_CHEST_LOCATION = chestMaterial("ender");

   public static RenderType bannerSheet() {
      return BANNER_SHEET_TYPE;
   }

   public static RenderType shieldSheet() {
      return SHIELD_SHEET_TYPE;
   }

   public static RenderType bedSheet() {
      return BED_SHEET_TYPE;
   }

   public static RenderType shulkerBoxSheet() {
      return SHULKER_BOX_SHEET_TYPE;
   }

   public static RenderType signSheet() {
      return SIGN_SHEET_TYPE;
   }

   public static RenderType chestSheet() {
      return CHEST_SHEET_TYPE;
   }

   public static RenderType solidBlockSheet() {
      return SOLID_BLOCK_SHEET;
   }

   public static RenderType cutoutBlockSheet() {
      return CUTOUT_BLOCK_SHEET;
   }

   public static RenderType translucentItemSheet() {
      return TRANSLUCENT_ITEM_CULL_BLOCK_SHEET;
   }

   public static RenderType translucentCullBlockSheet() {
      return TRANSLUCENT_CULL_BLOCK_SHEET;
   }

   public static void getAllMaterials(Consumer<Material> p_110781_) {
      p_110781_.accept(DEFAULT_SHULKER_TEXTURE_LOCATION);
      SHULKER_TEXTURE_LOCATION.forEach(p_110781_);
      BANNER_MATERIALS.values().forEach(p_110781_);
      SHIELD_MATERIALS.values().forEach(p_110781_);
      SIGN_MATERIALS.values().forEach(p_110781_);

      for(Material material : BED_TEXTURES) {
         p_110781_.accept(material);
      }

      p_110781_.accept(CHEST_TRAP_LOCATION);
      p_110781_.accept(CHEST_TRAP_LOCATION_LEFT);
      p_110781_.accept(CHEST_TRAP_LOCATION_RIGHT);
      p_110781_.accept(CHEST_XMAS_LOCATION);
      p_110781_.accept(CHEST_XMAS_LOCATION_LEFT);
      p_110781_.accept(CHEST_XMAS_LOCATION_RIGHT);
      p_110781_.accept(CHEST_LOCATION);
      p_110781_.accept(CHEST_LOCATION_LEFT);
      p_110781_.accept(CHEST_LOCATION_RIGHT);
      p_110781_.accept(ENDER_CHEST_LOCATION);
   }

   private static Material createSignMaterial(WoodType p_173386_) {
      ResourceLocation location = new ResourceLocation(p_173386_.name());
      return new Material(SIGN_SHEET, new ResourceLocation(location.getNamespace(), "entity/signs/" + location.getPath()));
   }

   public static Material getSignMaterial(WoodType p_173382_) {
      return SIGN_MATERIALS.get(p_173382_);
   }

   private static Material createBannerMaterial(BannerPattern p_173388_) {
      return new Material(BANNER_SHEET, p_173388_.location(true));
   }

   public static Material getBannerMaterial(BannerPattern p_173380_) {
      return BANNER_MATERIALS.get(p_173380_);
   }

   private static Material createShieldMaterial(BannerPattern p_173390_) {
      return new Material(SHIELD_SHEET, p_173390_.location(false));
   }

   public static Material getShieldMaterial(BannerPattern p_173384_) {
      return SHIELD_MATERIALS.get(p_173384_);
   }

   private static Material chestMaterial(String p_110779_) {
      return new Material(CHEST_SHEET, new ResourceLocation("entity/chest/" + p_110779_));
   }

   public static Material chooseMaterial(BlockEntity p_110768_, ChestType p_110769_, boolean p_110770_) {
      if (p_110768_ instanceof EnderChestBlockEntity) {
         return ENDER_CHEST_LOCATION;
      } else if (p_110770_) {
         return chooseMaterial(p_110769_, CHEST_XMAS_LOCATION, CHEST_XMAS_LOCATION_LEFT, CHEST_XMAS_LOCATION_RIGHT);
      } else {
         return p_110768_ instanceof TrappedChestBlockEntity ? chooseMaterial(p_110769_, CHEST_TRAP_LOCATION, CHEST_TRAP_LOCATION_LEFT, CHEST_TRAP_LOCATION_RIGHT) : chooseMaterial(p_110769_, CHEST_LOCATION, CHEST_LOCATION_LEFT, CHEST_LOCATION_RIGHT);
      }
   }

   private static Material chooseMaterial(ChestType p_110772_, Material p_110773_, Material p_110774_, Material p_110775_) {
      switch(p_110772_) {
      case LEFT:
         return p_110774_;
      case RIGHT:
         return p_110775_;
      case SINGLE:
      default:
         return p_110773_;
      }
   }

   /**
    * Not threadsafe. Enqueue it in client setup.
    */
   public static void addWoodType(WoodType woodType) {
      SIGN_MATERIALS.put(woodType, createSignMaterial(woodType));
   }
}
