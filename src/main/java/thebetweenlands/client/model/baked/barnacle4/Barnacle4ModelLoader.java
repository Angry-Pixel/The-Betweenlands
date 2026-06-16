package thebetweenlands.client.model.baked.barnacle4;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class Barnacle4ModelLoader implements IGeometryLoader<UnbakedBarnacle4Model> {
	public static final Barnacle4ModelLoader INSTANCE = new Barnacle4ModelLoader();

	@Override
	public UnbakedBarnacle4Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedBarnacle4Model();
	}
}

