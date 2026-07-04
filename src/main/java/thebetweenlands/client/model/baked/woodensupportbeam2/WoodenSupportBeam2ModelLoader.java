package thebetweenlands.client.model.baked.woodensupportbeam2;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class WoodenSupportBeam2ModelLoader implements IGeometryLoader<UnbakedWoodenSupportBeam2Model> {
	public static final WoodenSupportBeam2ModelLoader INSTANCE = new WoodenSupportBeam2ModelLoader();

	@Override
	public UnbakedWoodenSupportBeam2Model read(JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		return new UnbakedWoodenSupportBeam2Model();
	}
}

