package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.baked.ModelBlank;
import thebetweenlands.client.render.model.baked.ModelCombined;
import thebetweenlands.client.render.model.baked.ModelEventSelection;
import thebetweenlands.client.render.model.loader.CustomModelManager;
import thebetweenlands.common.lib.ModInfo;

public class ModelRegistry {
	private ModelRegistry() { }

	//Generic
	public static final IUnbakedModel BLANK = new ModelBlank();
	public static final IUnbakedModel MODEL_COMBINED = new ModelCombined();
	//public static final IUnbakedModel CONNECTED_TEXTURE = new ModelConnectedTexture();
	//public static final IUnbakedModel LAYER_SELECTION = new ModelLayerSelection();
	public static final ModelEventSelection SPOOK_EVENT = new ModelEventSelection();
	public static final ModelEventSelection WINTER_EVENT = new ModelEventSelection();

	//Plant models
	//public static final IUnbakedModel PITCHER_PLANT = new ModelFromModelBase(new ModelPitcherPlant(), new ResourceLocation("thebetweenlands:blocks/pitcher_plant"), 128, 128);
	//public static final IUnbakedModel BLACK_HAT_MUSHROOM_1 = new ModelFromModelBase(new ModelBlackHatMushroom1(), new ResourceLocation("thebetweenlands:blocks/black_hat_mushroom_1"), 64, 64);
	//public static final IUnbakedModel BLACK_HAT_MUSHROOM_2 = new ModelFromModelBase(new ModelBlackHatMushroom2(), new ResourceLocation("thebetweenlands:blocks/black_hat_mushroom_2"), 64, 64);
	//public static final IUnbakedModel BLACK_HAT_MUSHROOM_3 = new ModelFromModelBase(new ModelBlackHatMushroom3(), new ResourceLocation("thebetweenlands:blocks/black_hat_mushroom_3"), 64, 64);
	//public static final IUnbakedModel BULB_CAPPED_MUSHROOM = new ModelFromModelBase(new ModelBulbCappedMushroom(), new ResourceLocation("thebetweenlands:blocks/bulb_capped_mushroom"), 64, 64);
	//public static final IUnbakedModel FLAT_HEAD_MUSHROOM_1 = new ModelFromModelBase(new ModelFlatHeadMushroom1(), new ResourceLocation("thebetweenlands:blocks/flat_head_mushroom_1"), 64, 64);
	//public static final IUnbakedModel FLAT_HEAD_MUSHROOM_2 = new ModelFromModelBase(new ModelFlatHeadMushroom2(), new ResourceLocation("thebetweenlands:blocks/flat_head_mushroom_2"), 64, 64);
	//public static final IUnbakedModel SUNDEW = new ModelFromModelBase(new ModelSundew(), new ResourceLocation("thebetweenlands:blocks/sundew"), 128, 128);
	//public static final IUnbakedModel VENUS_FLY_TRAP = new ModelFromModelBase(new ModelVenusFlyTrap(), new ResourceLocation("thebetweenlands:blocks/venus_fly_trap"), 64, 64);
	//public static final IUnbakedModel VENUS_FLY_TRAP_BLOOMING = new ModelFromModelBase(new ModelVenusFlyTrap(), new ResourceLocation("thebetweenlands:blocks/venus_fly_trap_blooming"), 64, 64);
	//public static final IUnbakedModel VOLARPAD_1 = new ModelFromModelBase(new ModelVolarpad(), new ResourceLocation("thebetweenlands:blocks/volarpad_1"), 256, 256).setAmbientOcclusion(false);
	//public static final IUnbakedModel VOLARPAD_2 = new ModelFromModelBase(new ModelVolarpad(), new ResourceLocation("thebetweenlands:blocks/volarpad_2"), 256, 256).setAmbientOcclusion(false);
	//public static final IUnbakedModel VOLARPAD_3 = new ModelFromModelBase(new ModelVolarpad(), new ResourceLocation("thebetweenlands:blocks/volarpad_3"), 256, 256).setAmbientOcclusion(false);
	//public static final IUnbakedModel WEEPING_BLUE = new ModelFromModelBase(new ModelWeepingBlue(), new ResourceLocation("thebetweenlands:blocks/weeping_blue"), 64, 64);
	//public static final IUnbakedModel SWAMP_PLANT = new ModelFromModelBase(new ModelSwampPlant(), new ResourceLocation("thebetweenlands:blocks/swamp_plant"), 64, 64);
	//public static final IUnbakedModel WEEDWOOD_BUSH = new ModelWeedwoodBush();

	//Crops
	//public static final IUnbakedModel FUNGUS_CROP_1 = new ModelFromModelBase(new ModelFungusCrop1(), new ResourceLocation("thebetweenlands:blocks/fungus_crop_1"), 32, 32);
	//public static final IUnbakedModel FUNGUS_CROP_2 = new ModelFromModelBase(new ModelFungusCrop2(), new ResourceLocation("thebetweenlands:blocks/fungus_crop_2"), 32, 32);
	//public static final IUnbakedModel FUNGUS_CROP_3 = new ModelFromModelBase(new ModelFungusCrop3(), new ResourceLocation("thebetweenlands:blocks/fungus_crop_3"), 64, 64);
	//public static final IUnbakedModel FUNGUS_CROP_4 = new ModelFromModelBase(new ModelFungusCrop4(), new ResourceLocation("thebetweenlands:blocks/fungus_crop_4"), 64, 64);
	//public static final IUnbakedModel FUNGUS_CROP_4_DECAYED = new ModelFromModelBase(new ModelFungusCrop4Decayed(), new ResourceLocation("thebetweenlands:blocks/fungus_crop_4_decayed"), 64, 64);
	//public static final IUnbakedModel WHITE_PEAR_CROP_1 = new ModelFromModelBase(new ModelWhitePearCrop1(), new ResourceLocation("thebetweenlands:blocks/white_pear_crop_1"), 16, 16);
	//public static final IUnbakedModel WHITE_PEAR_CROP_2 = new ModelFromModelBase(new ModelWhitePearCrop2(), new ResourceLocation("thebetweenlands:blocks/white_pear_crop_2"), 32, 32);
	//public static final IUnbakedModel WHITE_PEAR_CROP_3 = new ModelFromModelBase(new ModelWhitePearCrop3(), new ResourceLocation("thebetweenlands:blocks/white_pear_crop_3"), 64, 64);
	//public static final IUnbakedModel WHITE_PEAR_CROP_4 = new ModelFromModelBase(new ModelWhitePearCrop4(), new ResourceLocation("thebetweenlands:blocks/white_pear_crop_4"), 64, 64);
	//public static final IUnbakedModel WHITE_PEAR_CROP_5 = new ModelFromModelBase(new ModelWhitePearCrop5(), new ResourceLocation("thebetweenlands:blocks/white_pear_crop_5"), 64, 64);
	//public static final IUnbakedModel WHITE_PEAR_CROP_6 = new ModelFromModelBase(new ModelWhitePearCrop6(), new ResourceLocation("thebetweenlands:blocks/white_pear_crop_6"), 64, 64);
	//public static final IUnbakedModel WHITE_PEAR_CROP_6_DECAYED = new ModelFromModelBase(new ModelWhitePearCrop6Decayed(), new ResourceLocation("thebetweenlands:blocks/white_pear_crop_6_decayed"), 64, 64);

	//Items
	//public static final IVertexProcessor SHIELD_VERTEX_PROCESSOR = new IVertexProcessor() {
	//	@Override
	//	public Vec3UV process(Vec3UV vertexIn, Quad quad, Box box, QuadBuilder builder) {
	//		return new Vec3UV(-vertexIn.x - 0.5D, vertexIn.y + 1.5D, -vertexIn.z - 0.5D, vertexIn.u, vertexIn.v, vertexIn.uw, vertexIn.vw);
	//	}
	//};
	//public static final IUnbakedModel BONE_SHIELD = new ModelFromModelBase(new ModelBoneShield(), new ResourceLocation("thebetweenlands:items/shields/bone_shield"), 128, 128, SHIELD_VERTEX_PROCESSOR);
	//public static final IUnbakedModel OCTINE_SHIELD = new ModelFromModelBase(new ModelOctineShield(), new ResourceLocation("thebetweenlands:items/shields/octine_shield"), 128, 128, SHIELD_VERTEX_PROCESSOR);
	//public static final IUnbakedModel SYRMORITE_SHIELD = new ModelFromModelBase(new ModelSyrmoriteShield(), new ResourceLocation("thebetweenlands:items/shields/syrmorite_shield"), 128, 128, SHIELD_VERTEX_PROCESSOR);
	//public static final IUnbakedModel VALONITE_SHIELD = new ModelFromModelBase(new ModelValoniteShield(), new ResourceLocation("thebetweenlands:items/shields/valonite_shield"), 128, 128, SHIELD_VERTEX_PROCESSOR);
	//public static final IUnbakedModel WEEDWOOD_SHIELD = new ModelFromModelBase(new ModelWeedwoodShield(), new ResourceLocation("thebetweenlands:items/shields/weedwood_shield"), 64, 64, SHIELD_VERTEX_PROCESSOR);
	//public static final IUnbakedModel WEEDWOOD_SHIELD_BURNING = new ModelWeedwoodShieldBurning();
	//public static final IUnbakedModel DENTROTHYST_SHIELD_GREEN = new ModelFromModelBase(new ModelDentrothystShield(), new ResourceLocation("thebetweenlands:items/shields/dentrothyst_shield_green"), 64, 64, SHIELD_VERTEX_PROCESSOR);
	//public static final IUnbakedModel DENTROTHYST_SHIELD_GREEN_POLISHED = new ModelFromModelBase(new ModelDentrothystShield(), new ResourceLocation("thebetweenlands:items/shields/dentrothyst_shield_green_polished"), 64, 64, SHIELD_VERTEX_PROCESSOR);
	//public static final IUnbakedModel DENTROTHYST_SHIELD_ORANGE = new ModelFromModelBase(new ModelDentrothystShield(), new ResourceLocation("thebetweenlands:items/shields/dentrothyst_shield_orange"), 64, 64, SHIELD_VERTEX_PROCESSOR);
	//public static final IUnbakedModel DENTROTHYST_SHIELD_ORANGE_POLISHED = new ModelFromModelBase(new ModelDentrothystShield(), new ResourceLocation("thebetweenlands:items/shields/dentrothyst_shield_orange_polished"), 64, 64, SHIELD_VERTEX_PROCESSOR);
	//public static final IUnbakedModel LURKER_SKIN_SHIELD = new ModelFromModelBase(new ModelLurkerSkinShield(), new ResourceLocation("thebetweenlands:items/shields/lurker_skin_shield"), 128, 128, SHIELD_VERTEX_PROCESSOR);
	//public static final IUnbakedModel BUCKET = new ModelDynBucketBL();
	//public static final IUnbakedModel WEEDWOOD_CHEST = new ModelFromModelBase(new ModelChest(), new ResourceLocation("thebetweenlands:tiles/weedwood_chest"), 64, 32,
	//		new IVertexProcessor() {
	//	@Override
	//	public Vec3UV process(Vec3UV vertexIn, Quad quad, Box box, QuadBuilder builder) {
	//		return new Vec3UV(vertexIn.x - 0.5D, vertexIn.y + 0.5D, -vertexIn.z + 0.5D, vertexIn.u, vertexIn.v, vertexIn.uw, vertexIn.vw);
	//	}
	//});
	//public static final IUnbakedModel MIRE_SNAIL_EGG = new ModelFromModelBase(new ModelMireSnailEgg(), new ResourceLocation("thebetweenlands:items/mire_snail_egg"), 16, 16);

	//Misc
	//public static final IUnbakedModel LIFE_CRYSTAL_STALACTITE = new ModelLifeCrystalStalactite();
	//public static final IUnbakedModel STALACTITE = new ModelStalactite();
	//public static final IUnbakedModel ROOT = new ModelRoot(new ResourceLocation(ModInfo.ID, "blocks/root_top"), new ResourceLocation(ModInfo.ID, "blocks/root_middle"), new ResourceLocation(ModInfo.ID, "blocks/root_bottom"));
	//public static final IUnbakedModel ROOT_SPOOK = new ModelRoot(new ResourceLocation(ModInfo.ID, "blocks/root_top_spook"), new ResourceLocation(ModInfo.ID, "blocks/root_middle_spook"), new ResourceLocation(ModInfo.ID, "blocks/root_bottom_spook"));
	//public static final IUnbakedModel RUBBER_TAP_LIQUID = new ModelRubberTapLiquid(null, 0);
	//public static final IUnbakedModel RUBBER_TAP_POURING = new ModelFromModelBase(new ModelRubberTapPouring(), new ResourceLocation(ModInfo.ID, "fluids/rubber_flowing"), 64, 64);
	//public static final IUnbakedModel WEEDWOOD_RUBBER_TAP = new ModelRubberTapCombined(new ResourceLocation("thebetweenlands:blocks/weedwood_rubber_tap"));
	//public static final IUnbakedModel SYRMORITE_RUBBER_TAP = new ModelRubberTapCombined(new ResourceLocation("thebetweenlands:blocks/syrmorite_rubber_tap"));
	//public static final IUnbakedModel MUD_FLOWER_POT_BASE = new ModelFromModelBase(new ModelMudFlowerPot(), new ResourceLocation("thebetweenlands:blocks/mud_flower_pot"), 32, 32);
	//public static final IUnbakedModel MUD_FLOWER_POT_CANDLE = new ModelFromModelBase(new ModelMudFlowerPotCandle(), new ResourceLocation("thebetweenlands:blocks/mud_flower_pot_candle"), 32, 32);
	//public static final IUnbakedModel MOSS_BED = new ModelFromModelBase(new ModelMossBed(), new ResourceLocation("thebetweenlands:blocks/moss_bed"), 128, 128);
	//public static final IUnbakedModel WALKWAY = new ModelWalkway(true);
	//public static final IUnbakedModel WALKWAY_NO_STANDS = new ModelWalkway(false);
	//public static final IUnbakedModel THATCH_ROOF = new ModelSlant(new ResourceLocation(ModInfo.ID, "blocks/thatch"));
	//public static final IUnbakedModel MUD_BRICK_ROOF = new ModelSlant(new ResourceLocation(ModInfo.ID, "blocks/mud_brick_roof"));
	//public static final IUnbakedModel PRESENT = new ModelFromModelBase(new ModelPresent(), new ResourceLocation("thebetweenlands:blocks/present"), 64, 64, new IVertexProcessor() {
	//	@Override
	//	public Vec3UV process(Vec3UV vertexIn, Quad quad, Box box, QuadBuilder builder) {
	//		builder.setTintIndex(0);
	//		return vertexIn;
	//	}
	//});

	public final static List<IUnbakedModel> MODELS = new ArrayList<IUnbakedModel>();

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
		default boolean registerModel(IUnbakedModel model, ResourceLocation location) {
			return false;
		}
	}

	public static final class DefaultRegistrar implements ICustomRegistrar {
		private CustomModelManager manager;

		private DefaultRegistrar(CustomModelManager manager) {
			this.manager = manager;
		}

		@Override
		public boolean registerModel(IUnbakedModel model, ResourceLocation location) {
			this.manager.registerModel(location, model);
			return true;
		}
	}

	public static void registerModels() {
		try {
			for (Field field : ModelRegistry.class.getDeclaredFields()) {
				if (IUnbakedModel.class.isAssignableFrom(field.getType())) {
					IUnbakedModel model = (IUnbakedModel) field.get(null);
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

	private static void registerModel(IUnbakedModel model, ResourceLocation location) {
		if(model instanceof ICustomRegistrar == false || !((ICustomRegistrar)model).registerModel(model, location)) {
			DEFAULT_REGISTRAR.registerModel(model, location);
		}
	}
}
