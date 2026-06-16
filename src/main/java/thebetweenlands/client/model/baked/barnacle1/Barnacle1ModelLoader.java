package thebetweenlands.client.model.baked.barnacle1;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class Barnacle1ModelLoader implements IGeometryLoader<UnbakedBarnacle1Model> {
	public static final Barnacle1ModelLoader INSTANCE = new Barnacle1ModelLoader();

	@Override
	public UnbakedBarnacle1Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedBarnacle1Model();
	}
}

