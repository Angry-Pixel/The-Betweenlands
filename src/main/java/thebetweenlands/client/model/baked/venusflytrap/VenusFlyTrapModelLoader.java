package thebetweenlands.client.model.baked.venusflytrap;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class VenusFlyTrapModelLoader implements IGeometryLoader<UnbakedVenusFlyTrapModel> {
	public static final VenusFlyTrapModelLoader INSTANCE = new VenusFlyTrapModelLoader();

	@Override
	public UnbakedVenusFlyTrapModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedVenusFlyTrapModel();
	}
}

