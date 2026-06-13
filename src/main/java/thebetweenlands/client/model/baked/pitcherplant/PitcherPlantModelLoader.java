package thebetweenlands.client.model.baked.pitcherplant;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class PitcherPlantModelLoader implements IGeometryLoader<UnbakedPitcherPlantModel> {
	public static final PitcherPlantModelLoader INSTANCE = new PitcherPlantModelLoader();

	@Override
	public UnbakedPitcherPlantModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedPitcherPlantModel();
	}
}

