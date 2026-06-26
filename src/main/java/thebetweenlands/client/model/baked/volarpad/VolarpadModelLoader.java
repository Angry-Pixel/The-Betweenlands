package thebetweenlands.client.model.baked.volarpad;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class VolarpadModelLoader implements IGeometryLoader<UnbakedVolarpadModel> {
	public static final VolarpadModelLoader INSTANCE = new VolarpadModelLoader();

	@Override
	public UnbakedVolarpadModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedVolarpadModel();
	}
}

