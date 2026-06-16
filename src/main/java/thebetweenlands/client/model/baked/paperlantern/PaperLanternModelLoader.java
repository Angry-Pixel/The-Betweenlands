package thebetweenlands.client.model.baked.paperlantern;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class PaperLanternModelLoader implements IGeometryLoader<UnbakedPaperLanternModel> {
	public static final PaperLanternModelLoader INSTANCE = new PaperLanternModelLoader();

	@Override
	public UnbakedPaperLanternModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedPaperLanternModel();
	}
}

