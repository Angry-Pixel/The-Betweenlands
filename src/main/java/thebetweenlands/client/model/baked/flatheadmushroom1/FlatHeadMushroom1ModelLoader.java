package thebetweenlands.client.model.baked.flatheadmushroom1;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class FlatHeadMushroom1ModelLoader implements IGeometryLoader<UnbakedFlatHeadMushroom1Model> {
	public static final FlatHeadMushroom1ModelLoader INSTANCE = new FlatHeadMushroom1ModelLoader();

	@Override
	public UnbakedFlatHeadMushroom1Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedFlatHeadMushroom1Model();
	}
}

