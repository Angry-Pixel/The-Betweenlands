package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import thebetweenlands.client.render.models.block.ModelCombined;
import thebetweenlands.client.render.models.block.ModelFromModelBase;
import thebetweenlands.client.render.models.block.ModelLifeCrystalStalactite;
import thebetweenlands.client.render.models.block.modelbase.ModelPitcherPlant;
import thebetweenlands.client.render.models.loader.CustomModelManager;
import thebetweenlands.common.lib.ModInfo;

public class ModelRegistry {
	public static final IModel MODEL_COMBINED = new ModelCombined();
	public static final IModel LIFE_CRYSTAL_STALACTITE = new ModelLifeCrystalStalactite();
	public static final IModel PITCHER_PLANT = new ModelFromModelBase(new ModelPitcherPlant(), new ResourceLocation("thebetweenlands:blocks/pitcher_plant"), 128, 128);


	public final static List<IModel> MODELS = new ArrayList<IModel>();

	public void preInit() {
		try {
			for (Field field : this.getClass().getDeclaredFields()) {
				if (field.getType().isAssignableFrom(IModel.class)) {
					IModel model = (IModel) field.get(this);
					MODELS.add(model);
					CustomModelManager.INSTANCE.registerModel(new ResourceLocation(ModInfo.ID, "models/block/internal/" + field.getName().toLowerCase(Locale.ENGLISH)), model);
					CustomModelManager.INSTANCE.registerModel(new ResourceLocation(ModInfo.ID, "models/item/internal/" + field.getName().toLowerCase(Locale.ENGLISH)), model);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
