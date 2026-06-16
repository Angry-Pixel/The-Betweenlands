package thebetweenlands.client.model.baked.barnacle3;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class Barnacle3ModelLoader implements IGeometryLoader<UnbakedBarnacle3Model> {
	public static final Barnacle3ModelLoader INSTANCE = new Barnacle3ModelLoader();

	@Override
	public UnbakedBarnacle3Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedBarnacle3Model();
	}
}

