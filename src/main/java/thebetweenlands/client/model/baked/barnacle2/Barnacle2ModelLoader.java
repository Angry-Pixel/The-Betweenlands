package thebetweenlands.client.model.baked.barnacle2;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class Barnacle2ModelLoader implements IGeometryLoader<UnbakedBarnacle2Model> {
	public static final Barnacle2ModelLoader INSTANCE = new Barnacle2ModelLoader();

	@Override
	public UnbakedBarnacle2Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedBarnacle2Model();
	}
}

