package thebetweenlands.client.model.baked.brazier;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class BrazierModelLoader implements IGeometryLoader<UnbakedBrazierModel> {
	public static final BrazierModelLoader INSTANCE = new BrazierModelLoader();

	@Override
	public UnbakedBrazierModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedBrazierModel();
	}
}

