package thebetweenlands.client.model.baked.barnacle;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class BarnacleModelLoader implements IGeometryLoader<UnbakedBarnacleModel> {
	public static final BarnacleModelLoader INSTANCE = new BarnacleModelLoader();

	@Override
	public UnbakedBarnacleModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedBarnacleModel();
	}
}

