package thebetweenlands.client.model.baked.sundew;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class SundewModelLoader implements IGeometryLoader<UnbakedSundewModel> {
	public static final SundewModelLoader INSTANCE = new SundewModelLoader();

	@Override
	public UnbakedSundewModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedSundewModel();
	}
}

