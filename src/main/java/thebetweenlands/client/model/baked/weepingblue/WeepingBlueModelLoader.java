package thebetweenlands.client.model.baked.weepingblue;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class WeepingBlueModelLoader implements IGeometryLoader<UnbakedWeepingBlueModel> {
	public static final WeepingBlueModelLoader INSTANCE = new WeepingBlueModelLoader();

	@Override
	public UnbakedWeepingBlueModel read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedWeepingBlueModel();
	}
}

