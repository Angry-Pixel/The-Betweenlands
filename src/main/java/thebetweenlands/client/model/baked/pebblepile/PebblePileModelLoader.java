package thebetweenlands.client.model.baked.pebblepile;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class PebblePileModelLoader implements IGeometryLoader<UnbakedPebblePileModel> {
	public static final PebblePileModelLoader INSTANCE = new PebblePileModelLoader();

	@Override
	public UnbakedPebblePileModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedPebblePileModel();
	}
}

