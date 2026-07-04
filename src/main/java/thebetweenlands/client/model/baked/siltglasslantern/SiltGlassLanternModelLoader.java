package thebetweenlands.client.model.baked.siltglasslantern;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class SiltGlassLanternModelLoader implements IGeometryLoader<UnbakedSiltGlassLanternModel> {
	public static final SiltGlassLanternModelLoader INSTANCE = new SiltGlassLanternModelLoader();

	@Override
	public UnbakedSiltGlassLanternModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedSiltGlassLanternModel();
	}
}

