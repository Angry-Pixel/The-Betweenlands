package thebetweenlands.client.model.baked.aspectruscrop;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class AspectrusCropModelLoader implements IGeometryLoader<UnbakedAspectrusCropModel> {
	public static final AspectrusCropModelLoader INSTANCE = new AspectrusCropModelLoader();

	@Override
	public UnbakedAspectrusCropModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedAspectrusCropModel();
	}
}

