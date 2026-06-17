package thebetweenlands.client.model.baked.walkway;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class WalkwayModelLoader implements IGeometryLoader<UnbakedWalkwayModel> {
	public static final WalkwayModelLoader INSTANCE = new WalkwayModelLoader();

	@Override
	public UnbakedWalkwayModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedWalkwayModel();
	}
}

