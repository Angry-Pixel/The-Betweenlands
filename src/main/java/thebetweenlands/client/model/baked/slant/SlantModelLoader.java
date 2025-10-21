package thebetweenlands.client.model.baked.slant;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class SlantModelLoader implements IGeometryLoader<UnbakedSlantModel> {
	public static final SlantModelLoader INSTANCE = new SlantModelLoader();

	@Override
	public UnbakedSlantModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedSlantModel();
	}
}
