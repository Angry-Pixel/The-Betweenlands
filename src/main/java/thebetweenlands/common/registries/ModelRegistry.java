package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.TRSRTransformation;
import thebetweenlands.client.render.model.baked.ModelAlcove;
import thebetweenlands.client.render.model.baked.ModelBlank;
import thebetweenlands.client.render.model.baked.ModelCombined;
import thebetweenlands.client.render.model.baked.ModelConnectedTexture;
import thebetweenlands.client.render.model.baked.ModelDynBucketBL;
import thebetweenlands.client.render.model.baked.ModelEventSelection;
import thebetweenlands.client.render.model.baked.ModelFromModelBase;
import thebetweenlands.client.render.model.baked.ModelFromModelBase.IVertexProcessor;
import thebetweenlands.client.render.model.baked.ModelLayerSelection;
import thebetweenlands.client.render.model.baked.ModelLifeCrystalStalactite;
import thebetweenlands.client.render.model.baked.ModelRoot;
import thebetweenlands.client.render.model.baked.ModelRubberTapCombined;
import thebetweenlands.client.render.model.baked.ModelRubberTapLiquid;
import thebetweenlands.client.render.model.baked.ModelSlant;
import thebetweenlands.client.render.model.baked.ModelStalactite;
import thebetweenlands.client.render.model.baked.ModelTransform;
import thebetweenlands.client.render.model.baked.ModelWalkway;
import thebetweenlands.client.render.model.baked.ModelWeedwoodBush;
import thebetweenlands.client.render.model.baked.ModelWeedwoodShieldBurning;
import thebetweenlands.client.render.model.baked.modelbase.ModelBlackHatMushroom1;
import thebetweenlands.client.render.model.baked.modelbase.ModelBlackHatMushroom2;
import thebetweenlands.client.render.model.baked.modelbase.ModelBlackHatMushroom3;
import thebetweenlands.client.render.model.baked.modelbase.ModelBulbCappedMushroom;
import thebetweenlands.client.render.model.baked.modelbase.ModelDungeonWallCandle;
import thebetweenlands.client.render.model.baked.modelbase.ModelFlatHeadMushroom1;
import thebetweenlands.client.render.model.baked.modelbase.ModelFlatHeadMushroom2;
import thebetweenlands.client.render.model.baked.modelbase.ModelFungusCrop1;
import thebetweenlands.client.render.model.baked.modelbase.ModelFungusCrop2;
import thebetweenlands.client.render.model.baked.modelbase.ModelFungusCrop3;
import thebetweenlands.client.render.model.baked.modelbase.ModelFungusCrop4;
import thebetweenlands.client.render.model.baked.modelbase.ModelFungusCrop4Decayed;
import thebetweenlands.client.render.model.baked.modelbase.ModelMossBed;
import thebetweenlands.client.render.model.baked.modelbase.ModelMudFlowerPot;
import thebetweenlands.client.render.model.baked.modelbase.ModelMudFlowerPotCandle;
import thebetweenlands.client.render.model.baked.modelbase.ModelMudTowerBrazier;
import thebetweenlands.client.render.model.baked.modelbase.ModelPitcherPlant;
import thebetweenlands.client.render.model.baked.modelbase.ModelPresent;
import thebetweenlands.client.render.model.baked.modelbase.ModelRubberTapPouring;
import thebetweenlands.client.render.model.baked.modelbase.ModelSundew;
import thebetweenlands.client.render.model.baked.modelbase.ModelSwampPlant;
import thebetweenlands.client.render.model.baked.modelbase.ModelVenusFlyTrap;
import thebetweenlands.client.render.model.baked.modelbase.ModelVolarpad;
import thebetweenlands.client.render.model.baked.modelbase.ModelWeepingBlue;
import thebetweenlands.client.render.model.baked.modelbase.ModelWhitePearCrop1;
import thebetweenlands.client.render.model.baked.modelbase.ModelWhitePearCrop2;
import thebetweenlands.client.render.model.baked.modelbase.ModelWhitePearCrop3;
import thebetweenlands.client.render.model.baked.modelbase.ModelWhitePearCrop4;
import thebetweenlands.client.render.model.baked.modelbase.ModelWhitePearCrop5;
import thebetweenlands.client.render.model.baked.modelbase.ModelWhitePearCrop6;
import thebetweenlands.client.render.model.baked.modelbase.ModelWhitePearCrop6Decayed;
import thebetweenlands.client.render.model.baked.modelbase.ModelWoodSupportBeam1;
import thebetweenlands.client.render.model.baked.modelbase.ModelWoodSupportBeam2;
import thebetweenlands.client.render.model.baked.modelbase.ModelWoodSupportBeam3;
import thebetweenlands.client.render.model.baked.modelbase.shields.ModelBoneShield;
import thebetweenlands.client.render.model.baked.modelbase.shields.ModelDentrothystShield;
import thebetweenlands.client.render.model.baked.modelbase.shields.ModelLurkerSkinShield;
import thebetweenlands.client.render.model.baked.modelbase.shields.ModelOctineShield;
import thebetweenlands.client.render.model.baked.modelbase.shields.ModelSyrmoriteShield;
import thebetweenlands.client.render.model.baked.modelbase.shields.ModelValoniteShield;
import thebetweenlands.client.render.model.baked.modelbase.shields.ModelWeedwoodShield;
import thebetweenlands.client.render.model.entity.ModelMireSnailEgg;
import thebetweenlands.client.render.model.loader.CustomModelManager;
import thebetweenlands.client.render.model.tile.ModelLootPot1;
import thebetweenlands.client.render.model.tile.ModelLootPot2;
import thebetweenlands.client.render.model.tile.ModelLootPot3;
import thebetweenlands.client.render.model.tile.ModelLootUrn1;
import thebetweenlands.client.render.model.tile.ModelLootUrn2;
import thebetweenlands.client.render.model.tile.ModelLootUrn3;
import thebetweenlands.client.render.model.tile.ModelTarBarrel;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.ModelConverter.Box;
import thebetweenlands.util.ModelConverter.Quad;
import thebetweenlands.util.QuadBuilder;
import thebetweenlands.util.TexturePacker;
import thebetweenlands.util.Vec3UV;

public class ModelRegistry {
	private ModelRegistry() { }

	public static final TexturePacker MODEL_TEXTURE_PACKER = new TexturePacker(new ResourceLocation(ModInfo.ID, "packed_model_texture"));
	
	//Generic
	public static final IModel BLANK = new ModelBlank();
	public static final IModel MODEL_COMBINED = new ModelCombined();
	public static final IModel MODEL_TRANSFORM = new ModelTransform(BLANK, TRSRTransformation.identity());
	public static final IModel CONNECTED_TEXTURE = new ModelConnectedTexture();
	public static final IModel LAYER_SELECTION = new ModelLayerSelection();
	public static final ModelEventSelection SPOOK_EVENT = new ModelEventSelection();
	public static final ModelEventSelection WINTER_EVENT = new ModelEventSelection();

	//Plant models
	public static final IModel PITCHER_PLANT = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelPitcherPlant(), new ResourceLocation("thebetweenlands:blocks/pitcher_plant"), new ResourceLocation("thebetweenlands:particle/block/pitcher_plant_particle"), 128, 128);
	public static final IModel BLACK_HAT_MUSHROOM_1 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelBlackHatMushroom1(), new ResourceLocation("thebetweenlands:blocks/black_hat_mushroom_1"), new ResourceLocation("thebetweenlands:particle/block/black_hat_mushroom_particle"), 64, 64);
	public static final IModel BLACK_HAT_MUSHROOM_2 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelBlackHatMushroom2(), new ResourceLocation("thebetweenlands:blocks/black_hat_mushroom_2"), new ResourceLocation("thebetweenlands:particle/block/black_hat_mushroom_particle"), 64, 64);
	public static final IModel BLACK_HAT_MUSHROOM_3 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelBlackHatMushroom3(), new ResourceLocation("thebetweenlands:blocks/black_hat_mushroom_3"), new ResourceLocation("thebetweenlands:particle/block/black_hat_mushroom_particle"), 64, 64);
	public static final IModel BULB_CAPPED_MUSHROOM = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelBulbCappedMushroom(), new ResourceLocation("thebetweenlands:blocks/bulb_capped_mushroom"), new ResourceLocation("thebetweenlands:particle/block/bulb_capped_mushroom_particle"), 64, 64);
	public static final IModel FLAT_HEAD_MUSHROOM_1 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelFlatHeadMushroom1(), new ResourceLocation("thebetweenlands:blocks/flat_head_mushroom_1"), new ResourceLocation("thebetweenlands:particle/block/flat_head_mushroom_particle"), 64, 64);
	public static final IModel FLAT_HEAD_MUSHROOM_2 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelFlatHeadMushroom2(), new ResourceLocation("thebetweenlands:blocks/flat_head_mushroom_2"), new ResourceLocation("thebetweenlands:particle/block/flat_head_mushroom_particle"), 64, 64);
	public static final IModel SUNDEW = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelSundew(), new ResourceLocation("thebetweenlands:blocks/sundew"), new ResourceLocation("thebetweenlands:particle/block/sundew_particle"), 128, 128);
	public static final IModel VENUS_FLY_TRAP = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelVenusFlyTrap(), new ResourceLocation("thebetweenlands:blocks/venus_fly_trap"), new ResourceLocation("thebetweenlands:particle/block/venus_fly_trap_particle"), 64, 64);
	public static final IModel VENUS_FLY_TRAP_BLOOMING = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelVenusFlyTrap(), new ResourceLocation("thebetweenlands:blocks/venus_fly_trap_blooming"), new ResourceLocation("thebetweenlands:particle/block/venus_fly_trap_blooming_particle"), 64, 64);
	public static final IModel VOLARPAD_1 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelVolarpad(), new ResourceLocation("thebetweenlands:blocks/volarpad_1"), new ResourceLocation("thebetweenlands:particle/block/volarpad_particle"), 256, 256).setAmbientOcclusion(false);
	public static final IModel VOLARPAD_2 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelVolarpad(), new ResourceLocation("thebetweenlands:blocks/volarpad_2"), new ResourceLocation("thebetweenlands:particle/block/volarpad_particle"), 256, 256).setAmbientOcclusion(false);
	public static final IModel VOLARPAD_3 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelVolarpad(), new ResourceLocation("thebetweenlands:blocks/volarpad_3"), new ResourceLocation("thebetweenlands:particle/block/volarpad_particle"), 256, 256).setAmbientOcclusion(false);
	public static final IModel WEEPING_BLUE = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelWeepingBlue(), new ResourceLocation("thebetweenlands:blocks/weeping_blue"), new ResourceLocation("thebetweenlands:particle/block/weeping_blue_particle"), 64, 64);
	public static final IModel SWAMP_PLANT = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelSwampPlant(), new ResourceLocation("thebetweenlands:blocks/swamp_plant"), new ResourceLocation("thebetweenlands:particle/block/swamp_plant_particle"), 64, 64);
	public static final IModel WEEDWOOD_BUSH = new ModelWeedwoodBush();

	//Crops
	public static final IModel FUNGUS_CROP_1 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelFungusCrop1(), new ResourceLocation("thebetweenlands:blocks/fungus_crop_1"), new ResourceLocation("thebetweenlands:particle/block/fungus_crop_particle"), 32, 32);
	public static final IModel FUNGUS_CROP_2 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelFungusCrop2(), new ResourceLocation("thebetweenlands:blocks/fungus_crop_2"), new ResourceLocation("thebetweenlands:particle/block/fungus_crop_particle"), 32, 32);
	public static final IModel FUNGUS_CROP_3 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelFungusCrop3(), new ResourceLocation("thebetweenlands:blocks/fungus_crop_3"), new ResourceLocation("thebetweenlands:particle/block/fungus_crop_particle"), 64, 64);
	public static final IModel FUNGUS_CROP_4 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelFungusCrop4(), new ResourceLocation("thebetweenlands:blocks/fungus_crop_4"), new ResourceLocation("thebetweenlands:particle/block/fungus_crop_particle"), 64, 64);
	public static final IModel FUNGUS_CROP_4_DECAYED = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelFungusCrop4Decayed(), new ResourceLocation("thebetweenlands:blocks/fungus_crop_4_decayed"), new ResourceLocation("thebetweenlands:particle/block/fungus_crop_decayed_particle"), 64, 64);
	public static final IModel WHITE_PEAR_CROP_1 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelWhitePearCrop1(), new ResourceLocation("thebetweenlands:blocks/white_pear_crop_1"), new ResourceLocation("thebetweenlands:particle/block/white_pear_crop_particle"), 16, 16);
	public static final IModel WHITE_PEAR_CROP_2 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelWhitePearCrop2(), new ResourceLocation("thebetweenlands:blocks/white_pear_crop_2"), new ResourceLocation("thebetweenlands:particle/block/white_pear_crop_particle"), 32, 32);
	public static final IModel WHITE_PEAR_CROP_3 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelWhitePearCrop3(), new ResourceLocation("thebetweenlands:blocks/white_pear_crop_3"), new ResourceLocation("thebetweenlands:particle/block/white_pear_crop_particle"), 64, 64);
	public static final IModel WHITE_PEAR_CROP_4 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelWhitePearCrop4(), new ResourceLocation("thebetweenlands:blocks/white_pear_crop_4"), new ResourceLocation("thebetweenlands:particle/block/white_pear_crop_particle"), 64, 64);
	public static final IModel WHITE_PEAR_CROP_5 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelWhitePearCrop5(), new ResourceLocation("thebetweenlands:blocks/white_pear_crop_5"), new ResourceLocation("thebetweenlands:particle/block/white_pear_crop_particle"), 64, 64);
	public static final IModel WHITE_PEAR_CROP_6 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelWhitePearCrop6(), new ResourceLocation("thebetweenlands:blocks/white_pear_crop_6"), new ResourceLocation("thebetweenlands:particle/block/white_pear_crop_particle"), 64, 64);
	public static final IModel WHITE_PEAR_CROP_6_DECAYED = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelWhitePearCrop6Decayed(), new ResourceLocation("thebetweenlands:blocks/white_pear_crop_6_decayed"), new ResourceLocation("thebetweenlands:particle/block/white_pear_crop_decayed_particle"), 64, 64);

	//Items
	public static final IVertexProcessor SHIELD_VERTEX_PROCESSOR = new IVertexProcessor() {
		@Override
		public Vec3UV process(Vec3UV vertexIn, Quad quad, Box box, QuadBuilder builder) {
			return new Vec3UV(-vertexIn.x - 0.5D, vertexIn.y + 1.5D, -vertexIn.z - 0.5D, vertexIn.u, vertexIn.v, vertexIn.uw, vertexIn.vw);
		}
	};
	public static final IModel BONE_SHIELD = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelBoneShield(), new ResourceLocation("thebetweenlands:items/shields/bone_shield"), new ResourceLocation("thebetweenlands:particle/item/bone_shield_particle"), 128, 128, SHIELD_VERTEX_PROCESSOR);
	public static final IModel OCTINE_SHIELD = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelOctineShield(), new ResourceLocation("thebetweenlands:items/shields/octine_shield"), new ResourceLocation("thebetweenlands:particle/item/octine_shield_particle"), 128, 128, SHIELD_VERTEX_PROCESSOR);
	public static final IModel SYRMORITE_SHIELD = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelSyrmoriteShield(), new ResourceLocation("thebetweenlands:items/shields/syrmorite_shield"), new ResourceLocation("thebetweenlands:particle/item/syrmorite_shield_particle"), 128, 128, SHIELD_VERTEX_PROCESSOR);
	public static final IModel VALONITE_SHIELD = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelValoniteShield(), new ResourceLocation("thebetweenlands:items/shields/valonite_shield"), new ResourceLocation("thebetweenlands:particle/item/valonite_shield_particle"), 128, 128, SHIELD_VERTEX_PROCESSOR);
	public static final IModel WEEDWOOD_SHIELD = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelWeedwoodShield(), new ResourceLocation("thebetweenlands:items/shields/weedwood_shield"), new ResourceLocation("thebetweenlands:particle/item/weedwood_shield_particle"), 64, 64, SHIELD_VERTEX_PROCESSOR);
	public static final IModel WEEDWOOD_SHIELD_BURNING = new ModelWeedwoodShieldBurning(MODEL_TEXTURE_PACKER);
	public static final IModel DENTROTHYST_SHIELD_GREEN = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelDentrothystShield(), new ResourceLocation("thebetweenlands:items/shields/dentrothyst_shield_green"), new ResourceLocation("thebetweenlands:particle/item/dentrothyst_shield_green_particle"), 64, 64, SHIELD_VERTEX_PROCESSOR);
	public static final IModel DENTROTHYST_SHIELD_GREEN_POLISHED = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelDentrothystShield(), new ResourceLocation("thebetweenlands:items/shields/dentrothyst_shield_green_polished"), new ResourceLocation("thebetweenlands:particle/item/dentrothyst_shield_green_polished_particle"), 64, 64, SHIELD_VERTEX_PROCESSOR);
	public static final IModel DENTROTHYST_SHIELD_ORANGE = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelDentrothystShield(), new ResourceLocation("thebetweenlands:items/shields/dentrothyst_shield_orange"), new ResourceLocation("thebetweenlands:particle/item/dentrothyst_shield_orange_particle"), 64, 64, SHIELD_VERTEX_PROCESSOR);
	public static final IModel DENTROTHYST_SHIELD_ORANGE_POLISHED = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelDentrothystShield(), new ResourceLocation("thebetweenlands:items/shields/dentrothyst_shield_orange_polished"), new ResourceLocation("thebetweenlands:particle/item/dentrothyst_shield_orange_polished_particle"), 64, 64, SHIELD_VERTEX_PROCESSOR);
	public static final IModel LURKER_SKIN_SHIELD = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelLurkerSkinShield(), new ResourceLocation("thebetweenlands:items/shields/lurker_skin_shield"), new ResourceLocation("thebetweenlands:particle/item/lurker_skin_shield_particle"), 128, 128, SHIELD_VERTEX_PROCESSOR);
	public static final IModel BUCKET = new ModelDynBucketBL();
	public static final IModel MIRE_SNAIL_EGG = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelMireSnailEgg(), new ResourceLocation("thebetweenlands:items/mire_snail_egg"), 16, 16);

	//Misc
	public static final IModel LIFE_CRYSTAL_STALACTITE = new ModelLifeCrystalStalactite();
	public static final IModel STALACTITE = new ModelStalactite();
	public static final IModel ROOT = new ModelRoot(new ResourceLocation(ModInfo.ID, "blocks/root_top"), new ResourceLocation(ModInfo.ID, "blocks/root_middle"), new ResourceLocation(ModInfo.ID, "blocks/root_bottom"));
	public static final IModel ROOT_SPOOK = new ModelRoot(new ResourceLocation(ModInfo.ID, "blocks/root_top_spook"), new ResourceLocation(ModInfo.ID, "blocks/root_middle_spook"), new ResourceLocation(ModInfo.ID, "blocks/root_bottom_spook"));
	public static final IModel RUBBER_TAP_LIQUID = new ModelRubberTapLiquid(null, 0);
	public static final IModel RUBBER_TAP_POURING = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelRubberTapPouring(), new ResourceLocation(ModInfo.ID, "fluids/rubber_flowing"), 64, 64);
	public static final IModel WEEDWOOD_RUBBER_TAP = new ModelRubberTapCombined(MODEL_TEXTURE_PACKER, new ResourceLocation("thebetweenlands:blocks/weedwood_rubber_tap"), new ResourceLocation("thebetweenlands:particle/block/weedwood_rubber_tap_particle"));
	public static final IModel SYRMORITE_RUBBER_TAP = new ModelRubberTapCombined(MODEL_TEXTURE_PACKER, new ResourceLocation("thebetweenlands:blocks/syrmorite_rubber_tap"), new ResourceLocation("thebetweenlands:particle/block/syrmorite_rubber_tap_particle"));
	public static final IModel MUD_FLOWER_POT_BASE = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelMudFlowerPot(), new ResourceLocation("thebetweenlands:blocks/mud_flower_pot"), 32, 32);
	public static final IModel MUD_FLOWER_POT_CANDLE = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelMudFlowerPotCandle(), new ResourceLocation("thebetweenlands:blocks/mud_flower_pot_candle"), 32, 32);
	public static final IModel MOSS_BED = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelMossBed(), new ResourceLocation("thebetweenlands:blocks/moss_bed"), new ResourceLocation("thebetweenlands:particle/block/moss_bed_particle"), 128, 128);
	public static final IModel WALKWAY = new ModelWalkway(MODEL_TEXTURE_PACKER, true);
	public static final IModel WALKWAY_NO_STANDS = new ModelWalkway(MODEL_TEXTURE_PACKER, false);
	public static final IModel THATCH_ROOF = new ModelSlant(new ResourceLocation(ModInfo.ID, "blocks/thatch"));
	public static final IModel MUD_BRICK_ROOF = new ModelSlant(new ResourceLocation(ModInfo.ID, "blocks/mud_brick_roof"));
	public static final IModel COMPACTED_MUD_SLOPE = new ModelSlant(new ResourceLocation(ModInfo.ID, "blocks/compacted_mud"));
	public static final IModel PRESENT = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelPresent(), new ResourceLocation("thebetweenlands:blocks/present"), 64, 64, new IVertexProcessor() {
		@Override
		public Vec3UV process(Vec3UV vertexIn, Quad quad, Box box, QuadBuilder builder) {
			builder.setTintIndex(0);
			return vertexIn;
		}
	});

	public static final IModel DUNGEON_WALL_CANDLE = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelDungeonWallCandle(), new ResourceLocation("thebetweenlands:blocks/dungeon_wall_candle"), new ResourceLocation("thebetweenlands:particle/block/dungeon_wall_candle_particle"), 32, 32);
	public static final IModel WOODEN_SUPPORT_BEAM_ROTTEN_1 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelWoodSupportBeam1(), new ResourceLocation("thebetweenlands:blocks/wooden_support_beam_rotten_1"), new ResourceLocation("thebetweenlands:particle/block/wood_support_beam_particle"), 64, 64);
	public static final IModel WOODEN_SUPPORT_BEAM_ROTTEN_2 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelWoodSupportBeam2(), new ResourceLocation("thebetweenlands:blocks/wooden_support_beam_rotten_2"), new ResourceLocation("thebetweenlands:particle/block/wood_support_beam_particle"), 64, 64);
	public static final IModel WOODEN_SUPPORT_BEAM_ROTTEN_3 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelWoodSupportBeam3(), new ResourceLocation("thebetweenlands:blocks/wooden_support_beam_rotten_3"), new ResourceLocation("thebetweenlands:particle/block/wood_support_beam_particle"), 64, 64);
	public static final IModel MUD_TOWER_BRAZIER = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelMudTowerBrazier(), new ResourceLocation("thebetweenlands:blocks/mud_tower_brazier"), new ResourceLocation("thebetweenlands:particle/block/mud_tower_brazier_particle"), 128, 128);
	public static final IModel MUD_BRICK_ALCOVE = new ModelAlcove(MODEL_TEXTURE_PACKER, new ResourceLocation[] {
			new ResourceLocation("thebetweenlands:blocks/mud_brick_alcove_0"),
			new ResourceLocation("thebetweenlands:blocks/mud_brick_alcove_1"),
			new ResourceLocation("thebetweenlands:blocks/mud_brick_alcove_2"),
			new ResourceLocation("thebetweenlands:blocks/mud_brick_alcove_3"),
			new ResourceLocation("thebetweenlands:blocks/mud_brick_alcove_4")
			}, new ResourceLocation("thebetweenlands:particle/block/mud_brick_alcove_particle"), 128, 128);
	public static final ModelFromModelBase LOOT_URN_1 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelLootUrn1(), new ResourceLocation("thebetweenlands:blocks/loot_urn_1"), new ResourceLocation("thebetweenlands:particle/block/loot_urn_particle"), 64, 32);
	public static final ModelFromModelBase LOOT_URN_2 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelLootUrn2(), new ResourceLocation("thebetweenlands:blocks/loot_urn_2"), new ResourceLocation("thebetweenlands:particle/block/loot_urn_particle"), 64, 32);
	public static final ModelFromModelBase LOOT_URN_3 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelLootUrn3(), new ResourceLocation("thebetweenlands:blocks/loot_urn_3"), new ResourceLocation("thebetweenlands:particle/block/loot_urn_particle"), 64, 32);
	public static final ModelFromModelBase LOOT_POT_1 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelLootPot1(), new ResourceLocation("thebetweenlands:blocks/loot_pot_1"), new ResourceLocation("thebetweenlands:particle/block/loot_pot_1_particle"), 64, 32);
	public static final ModelFromModelBase LOOT_POT_2 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelLootPot2(), new ResourceLocation("thebetweenlands:blocks/loot_pot_2"), new ResourceLocation("thebetweenlands:particle/block/loot_pot_2_particle"), 64, 32);
	public static final ModelFromModelBase LOOT_POT_3 = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelLootPot3(), new ResourceLocation("thebetweenlands:blocks/loot_pot_3"), new ResourceLocation("thebetweenlands:particle/block/loot_pot_3_particle"), 64, 32);
	public static final ModelFromModelBase TAR_BARREL = new ModelFromModelBase(MODEL_TEXTURE_PACKER, new ModelTarBarrel(), new ResourceLocation("thebetweenlands:blocks/tar_barrel"), new ResourceLocation("thebetweenlands:blocks/weedwood"), 64, 32);
	
	
	
	public final static List<IModel> MODELS = new ArrayList<IModel>();

	private static final ICustomRegistrar DEFAULT_REGISTRAR = new DefaultRegistrar(CustomModelManager.INSTANCE);

	public static interface ICustomRegistrar {
		/**
		 * Called when this model is registered.
		 * Can be used to register additional models or
		 * to override the default registering.
		 * @param model Model to register
		 * @param location Resource location of the model
		 * @return Return true to cancel default registering
		 */
		default boolean registerModel(IModel model, ResourceLocation location) {
			return false;
		}
	}

	public static final class DefaultRegistrar implements ICustomRegistrar {
		private CustomModelManager manager;

		private DefaultRegistrar(CustomModelManager manager) {
			this.manager = manager;
		}

		@Override
		public boolean registerModel(IModel model, ResourceLocation location) {
			this.manager.registerModel(location, model);
			return true;
		}
	}
	
	public static void preInit() {
		try {
			for (Field field : ModelRegistry.class.getDeclaredFields()) {
				if (IModel.class.isAssignableFrom(field.getType())) {
					IModel model = (IModel) field.get(null);
					MODELS.add(model);
					ResourceLocation blockLocation = new ResourceLocation(ModInfo.ID, "models/block/internal/" + field.getName().toLowerCase(Locale.ENGLISH));
					ResourceLocation itemLocation = new ResourceLocation(ModInfo.ID, "models/item/internal/" + field.getName().toLowerCase(Locale.ENGLISH));
					registerModel(model, blockLocation);
					registerModel(model, itemLocation);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void registerModel(IModel model, ResourceLocation location) {
		if(model instanceof ICustomRegistrar == false || !((ICustomRegistrar)model).registerModel(model, location)) {
			DEFAULT_REGISTRAR.registerModel(model, location);
		}
	}
}
