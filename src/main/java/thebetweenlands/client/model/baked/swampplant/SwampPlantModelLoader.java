package thebetweenlands.client.model.baked.swampplant;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class SwampPlantModelLoader implements IGeometryLoader<UnbakedSwampPlantModel> {
	public static final SwampPlantModelLoader INSTANCE = new SwampPlantModelLoader();

	@Override
	public UnbakedSwampPlantModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedSwampPlantModel();
	}
}
