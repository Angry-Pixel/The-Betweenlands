package thebetweenlands.client.model.baked.temp;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class TempModelLoader implements IGeometryLoader<UnbakedTempModel> {
	public static final TempModelLoader INSTANCE = new TempModelLoader();

	@Override
	public UnbakedTempModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedTempModel();
	}
}
