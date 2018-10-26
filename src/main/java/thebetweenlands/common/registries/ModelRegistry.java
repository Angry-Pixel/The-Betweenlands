package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.model.ModelChest;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
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
import thebetweenlands.client.render.model.baked.ModelStalactite;
import thebetweenlands.client.render.model.baked.ModelSlant;
import thebetweenlands.client.render.model.baked.ModelWalkway;
import thebetweenlands.client.render.model.baked.ModelWeedwoodBush;
import thebetweenlands.client.render.model.baked.ModelWeedwoodShieldBurning;
import thebetweenlands.client.render.model.baked.modelbase.ModelBlackHatMushroom1;
import thebetweenlands.client.render.model.baked.modelbase.ModelBlackHatMushroom2;
import thebetweenlands.client.render.model.baked.modelbase.ModelBlackHatMushroom3;
import thebetweenlands.client.render.model.baked.modelbase.ModelBulbCappedMushroom;
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
import thebetweenlands.client.render.model.baked.modelbase.shields.ModelBoneShield;
import thebetweenlands.client.render.model.baked.modelbase.shields.ModelDentrothystShield;
import thebetweenlands.client.render.model.baked.modelbase.shields.ModelLurkerSkinShield;
import thebetweenlands.client.render.model.baked.modelbase.shields.ModelOctineShield;
import thebetweenlands.client.render.model.baked.modelbase.shields.ModelSyrmoriteShield;
import thebetweenlands.client.render.model.baked.modelbase.shields.ModelValoniteShield;
import thebetweenlands.client.render.model.baked.modelbase.shields.ModelWeedwoodShield;
import thebetweenlands.client.render.model.entity.ModelMireSnailEgg;
import thebetweenlands.client.render.model.loader.CustomModelManager;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.ModelConverter.Box;
import thebetweenlands.util.ModelConverter.Quad;
import thebetweenlands.util.QuadBuilder;
import thebetweenlands.util.Vec3UV;

public class ModelRegistry {
	private ModelRegistry() { }

	//Generic
	public static final IModel BLANK = new ModelBlank();
	public static final IModel MODEL_COMBINED = new ModelCombined();
	public static final IModel CONNECTED_TEXTURE = new ModelConnectedTexture();
	public static final IModel LAYER_SELECTION = new ModelLayerSelection();
	public static final ModelEventSelection SPOOK_EVENT = new ModelEventSelection();
	public static final ModelEventSelection WINTER_EVENT = new ModelEventSelection();

	//Plant models
	public static final IModel PITCHER_PLANT = new ModelFromModelBase(new ModelPitcherPlant(), new ResourceLocation("thebetweenlands:blocks/pitcher_plant"), 128, 128);
	public static final IModel BLACK_HAT_MUSHROOM_1 = new ModelFromModelBase(new ModelBlackHatMushroom1(), new ResourceLocation("thebetweenlands:blocks/black_hat_mushroom_1"), 64, 64);
	public static final IModel BLACK_HAT_MUSHROOM_2 = new ModelFromModelBase(new ModelBlackHatMushroom2(), new ResourceLocation("thebetweenlands:blocks/black_hat_mushroom_2"), 64, 64);
	public static final IModel BLACK_HAT_MUSHROOM_3 = new ModelFromModelBase(new ModelBlackHatMushroom3(), new ResourceLocation("thebetweenlands:blocks/black_hat_mushroom_3"), 64, 64);
	public static final IModel BULB_CAPPED_MUSHROOM = new ModelFromModelBase(new ModelBulbCappedMushroom(), new ResourceLocation("thebetweenlands:blocks/bulb_capped_mushroom"), 64, 64);
	public static final IModel FLAT_HEAD_MUSHROOM_1 = new ModelFromModelBase(new ModelFlatHeadMushroom1(), new ResourceLocation("thebetweenlands:blocks/flat_head_mushroom_1"), 64, 64);
	public static final IModel FLAT_HEAD_MUSHROOM_2 = new ModelFromModelBase(new ModelFlatHeadMushroom2(), new ResourceLocation("thebetweenlands:blocks/flat_head_mushroom_2"), 64, 64);
	public static final IModel SUNDEW = new ModelFromModelBase(new ModelSundew(), new ResourceLocation("thebetweenlands:blocks/sundew"), 128, 128);
	public static final IModel VENUS_FLY_TRAP = new ModelFromModelBase(new ModelVenusFlyTrap(), new ResourceLocation("thebetweenlands:blocks/venus_fly_trap"), 64, 64);
	public static final IModel VENUS_FLY_TRAP_BLOOMING = new ModelFromModelBase(new ModelVenusFlyTrap(), new ResourceLocation("thebetweenlands:blocks/venus_fly_trap_blooming"), 64, 64);
	public static final IModel VOLARPAD_1 = new ModelFromModelBase(new ModelVolarpad(), new ResourceLocation("thebetweenlands:blocks/volarpad_1"), 256, 256).setAmbientOcclusion(false);
	public static final IModel VOLARPAD_2 = new ModelFromModelBase(new ModelVolarpad(), new ResourceLocation("thebetweenlands:blocks/volarpad_2"), 256, 256).setAmbientOcclusion(false);
	public static final IModel VOLARPAD_3 = new ModelFromModelBase(new ModelVolarpad(), new ResourceLocation("thebetweenlands:blocks/volarpad_3"), 256, 256).setAmbientOcclusion(false);
	public static final IModel WEEPING_BLUE = new ModelFromModelBase(new ModelWeepingBlue(), new ResourceLocation("thebetweenlands:blocks/weeping_blue"), 64, 64);
	public static final IModel SWAMP_PLANT = new ModelFromModelBase(new ModelSwampPlant(), new ResourceLocation("thebetweenlands:blocks/swamp_plant"), 64, 64);
	public static final IModel WEEDWOOD_BUSH = new ModelWeedwoodBush();

	//Crops
	public static final IModel FUNGUS_CROP_1 = new ModelFromModelBase(new ModelFungusCrop1(), new ResourceLocation("thebetweenlands:blocks/fungus_crop_1"), 32, 32);
	public static final IModel FUNGUS_CROP_2 = new ModelFromModelBase(new ModelFungusCrop2(), new ResourceLocation("thebetweenlands:blocks/fungus_crop_2"), 32, 32);
	public static final IModel FUNGUS_CROP_3 = new ModelFromModelBase(new ModelFungusCrop3(), new ResourceLocation("thebetweenlands:blocks/fungus_crop_3"), 64, 64);
	public static final IModel FUNGUS_CROP_4 = new ModelFromModelBase(new ModelFungusCrop4(), new ResourceLocation("thebetweenlands:blocks/fungus_crop_4"), 64, 64);
	public static final IModel FUNGUS_CROP_4_DECAYED = new ModelFromModelBase(new ModelFungusCrop4Decayed(), new ResourceLocation("thebetweenlands:blocks/fungus_crop_4_decayed"), 64, 64);
	public static final IModel WHITE_PEAR_CROP_1 = new ModelFromModelBase(new ModelWhitePearCrop1(), new ResourceLocation("thebetweenlands:blocks/white_pear_crop_1"), 16, 16);
	public static final IModel WHITE_PEAR_CROP_2 = new ModelFromModelBase(new ModelWhitePearCrop2(), new ResourceLocation("thebetweenlands:blocks/white_pear_crop_2"), 32, 32);
	public static final IModel WHITE_PEAR_CROP_3 = new ModelFromModelBase(new ModelWhitePearCrop3(), new ResourceLocation("thebetweenlands:blocks/white_pear_crop_3"), 64, 64);
	public static final IModel WHITE_PEAR_CROP_4 = new ModelFromModelBase(new ModelWhitePearCrop4(), new ResourceLocation("thebetweenlands:blocks/white_pear_crop_4"), 64, 64);
	public static final IModel WHITE_PEAR_CROP_5 = new ModelFromModelBase(new ModelWhitePearCrop5(), new ResourceLocation("thebetweenlands:blocks/white_pear_crop_5"), 64, 64);
	public static final IModel WHITE_PEAR_CROP_6 = new ModelFromModelBase(new ModelWhitePearCrop6(), new ResourceLocation("thebetweenlands:blocks/white_pear_crop_6"), 64, 64);
	public static final IModel WHITE_PEAR_CROP_6_DECAYED = new ModelFromModelBase(new ModelWhitePearCrop6Decayed(), new ResourceLocation("thebetweenlands:blocks/white_pear_crop_6_decayed"), 64, 64);

	//Items
	public static final IVertexProcessor SHIELD_VERTEX_PROCESSOR = new IVertexProcessor() {
		@Override
		public Vec3UV process(Vec3UV vertexIn, Quad quad, Box box, QuadBuilder builder) {
			return new Vec3UV(-vertexIn.x - 0.5D, vertexIn.y + 1.5D, -vertexIn.z - 0.5D, vertexIn.u, vertexIn.v, vertexIn.uw, vertexIn.vw);
		}
	};
	public static final IModel BONE_SHIELD = new ModelFromModelBase(new ModelBoneShield(), new ResourceLocation("thebetweenlands:items/shields/bone_shield"), 128, 128, SHIELD_VERTEX_PROCESSOR);
	public static final IModel OCTINE_SHIELD = new ModelFromModelBase(new ModelOctineShield(), new ResourceLocation("thebetweenlands:items/shields/octine_shield"), 128, 128, SHIELD_VERTEX_PROCESSOR);
	public static final IModel SYRMORITE_SHIELD = new ModelFromModelBase(new ModelSyrmoriteShield(), new ResourceLocation("thebetweenlands:items/shields/syrmorite_shield"), 128, 128, SHIELD_VERTEX_PROCESSOR);
	public static final IModel VALONITE_SHIELD = new ModelFromModelBase(new ModelValoniteShield(), new ResourceLocation("thebetweenlands:items/shields/valonite_shield"), 128, 128, SHIELD_VERTEX_PROCESSOR);
	public static final IModel WEEDWOOD_SHIELD = new ModelFromModelBase(new ModelWeedwoodShield(), new ResourceLocation("thebetweenlands:items/shields/weedwood_shield"), 64, 64, SHIELD_VERTEX_PROCESSOR);
	public static final IModel WEEDWOOD_SHIELD_BURNING = new ModelWeedwoodShieldBurning();
	public static final IModel DENTROTHYST_SHIELD_GREEN = new ModelFromModelBase(new ModelDentrothystShield(), new ResourceLocation("thebetweenlands:items/shields/dentrothyst_shield_green"), 64, 64, SHIELD_VERTEX_PROCESSOR);
	public static final IModel DENTROTHYST_SHIELD_GREEN_POLISHED = new ModelFromModelBase(new ModelDentrothystShield(), new ResourceLocation("thebetweenlands:items/shields/dentrothyst_shield_green_polished"), 64, 64, SHIELD_VERTEX_PROCESSOR);
	public static final IModel DENTROTHYST_SHIELD_ORANGE = new ModelFromModelBase(new ModelDentrothystShield(), new ResourceLocation("thebetweenlands:items/shields/dentrothyst_shield_orange"), 64, 64, SHIELD_VERTEX_PROCESSOR);
	public static final IModel DENTROTHYST_SHIELD_ORANGE_POLISHED = new ModelFromModelBase(new ModelDentrothystShield(), new ResourceLocation("thebetweenlands:items/shields/dentrothyst_shield_orange_polished"), 64, 64, SHIELD_VERTEX_PROCESSOR);
	public static final IModel LURKER_SKIN_SHIELD = new ModelFromModelBase(new ModelLurkerSkinShield(), new ResourceLocation("thebetweenlands:items/shields/lurker_skin_shield"), 128, 128, SHIELD_VERTEX_PROCESSOR);
	public static final IModel BUCKET = new ModelDynBucketBL();
	public static final IModel WEEDWOOD_CHEST = new ModelFromModelBase(new ModelChest(), new ResourceLocation("thebetweenlands:tiles/weedwood_chest"), 64, 32,
			new IVertexProcessor() {
		@Override
		public Vec3UV process(Vec3UV vertexIn, Quad quad, Box box, QuadBuilder builder) {
			return new Vec3UV(vertexIn.x - 0.5D, vertexIn.y + 0.5D, -vertexIn.z + 0.5D, vertexIn.u, vertexIn.v, vertexIn.uw, vertexIn.vw);
		}
	});
	public static final IModel MIRE_SNAIL_EGG = new ModelFromModelBase(new ModelMireSnailEgg(), new ResourceLocation("thebetweenlands:items/mire_snail_egg"), 16, 16);

	//Misc
	public static final IModel LIFE_CRYSTAL_STALACTITE = new ModelLifeCrystalStalactite();
	public static final IModel STALACTITE = new ModelStalactite();
	public static final IModel ROOT = new ModelRoot(new ResourceLocation(ModInfo.ID, "blocks/root_top"), new ResourceLocation(ModInfo.ID, "blocks/root_middle"), new ResourceLocation(ModInfo.ID, "blocks/root_bottom"));
	public static final IModel ROOT_SPOOK = new ModelRoot(new ResourceLocation(ModInfo.ID, "blocks/root_top_spook"), new ResourceLocation(ModInfo.ID, "blocks/root_middle_spook"), new ResourceLocation(ModInfo.ID, "blocks/root_bottom_spook"));
	public static final IModel RUBBER_TAP_LIQUID = new ModelRubberTapLiquid(null, 0);
	public static final IModel RUBBER_TAP_POURING = new ModelFromModelBase(new ModelRubberTapPouring(), new ResourceLocation(ModInfo.ID, "fluids/rubber_flowing"), 64, 64);
	public static final IModel WEEDWOOD_RUBBER_TAP = new ModelRubberTapCombined(new ResourceLocation("thebetweenlands:blocks/weedwood_rubber_tap"));
	public static final IModel SYRMORITE_RUBBER_TAP = new ModelRubberTapCombined(new ResourceLocation("thebetweenlands:blocks/syrmorite_rubber_tap"));
	public static final IModel MUD_FLOWER_POT_BASE = new ModelFromModelBase(new ModelMudFlowerPot(), new ResourceLocation("thebetweenlands:blocks/mud_flower_pot"), 32, 32);
	public static final IModel MUD_FLOWER_POT_CANDLE = new ModelFromModelBase(new ModelMudFlowerPotCandle(), new ResourceLocation("thebetweenlands:blocks/mud_flower_pot_candle"), 32, 32);
	public static final IModel MOSS_BED = new ModelFromModelBase(new ModelMossBed(), new ResourceLocation("thebetweenlands:blocks/moss_bed"), 128, 128);
	public static final IModel WALKWAY = new ModelWalkway(true);
	public static final IModel WALKWAY_NO_STANDS = new ModelWalkway(false);
	public static final IModel THATCH_ROOF = new ModelSlant(new ResourceLocation(ModInfo.ID, "blocks/thatch"));
	public static final IModel MUD_BRICK_ROOF = new ModelSlant(new ResourceLocation(ModInfo.ID, "blocks/mud_brick_roof"));
	public static final IModel PRESENT = new ModelFromModelBase(new ModelPresent(), new ResourceLocation("thebetweenlands:blocks/present"), 64, 64, new IVertexProcessor() {
		@Override
		public Vec3UV process(Vec3UV vertexIn, Quad quad, Box box, QuadBuilder builder) {
			builder.setTintIndex(0);
			return vertexIn;
		}
	});

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
