package thebetweenlands.client.model.baked.bush;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class BushModelLoader implements IGeometryLoader<UnbakedBushModel> {
	public static final BushModelLoader INSTANCE = new BushModelLoader();

	@Override
	public UnbakedBushModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedBushModel();
	}
}
