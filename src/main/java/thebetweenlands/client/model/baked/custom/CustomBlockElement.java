package thebetweenlands.client.model.baked.custom;

import java.lang.reflect.Type;
import java.util.Locale;

import javax.annotation.Nullable;

import org.joml.Vector3f;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

public record CustomBlockElement(BlockElement baseBlockElement, CustomElementRotation customRotation) {

	public static final class Deserializer implements JsonDeserializer<CustomBlockElement> {

		@Override
		public CustomBlockElement deserialize(JsonElement json, Type type, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();
			
			CustomElementRotation customRotation = CustomElementRotation.ZERO;
			
			// Parse the custom rotation
			if (jsonObject.has("rotation")) {
                JsonObject rotationObject = GsonHelper.getAsJsonObject(jsonObject, "rotation");
                
                // Get custom rotation from rotation object
                CustomElementRotation rotation = getRotation(rotationObject);
                
				if (rotation != null) {
					// Remove the rotation field so the vanilla parser can still parse it
					jsonObject.remove("rotation");
					customRotation = rotation;
				}
				// If null, then the rotation can be parsed by the vanilla parser
			}
			
			// Parse the vanilla BlockElement
			BlockElement blockElement = context.deserialize(json, BlockElement.class);
			
			// Return the custom block element
			return new CustomBlockElement(blockElement, customRotation);
		}

		// Takes the rotation json object, and maybe gives a custom rotation out
        @Nullable
		private CustomElementRotation getRotation(JsonObject json) {
            Vector3f origin = this.getVector3f(json, "origin");
            origin.mul(0.0625F);
            boolean rescale = GsonHelper.getAsBoolean(json, "rescale", false);
            
        	if (json.has("axis") && json.has("angle")) {
        		return this.getSingleRotation(json, origin, rescale);
        	} else if (json.has("x") || json.has("y") || json.has("z")) {
        		return this.getMultiRotation(json, origin, rescale);
        	}
        	
			return null;
		}

		// Takes the rotation json object, and maybe gives a single axis rotation out
        @Nullable
		private CustomElementRotation getMultiRotation(JsonObject json, Vector3f origin, boolean rescale) {
        	float x = this.getRotation(json, "x");
        	float y = this.getRotation(json, "y");
        	float z = this.getRotation(json, "z");
        	
			return new CustomElementRotation(origin, x, y, z, rescale);
		}
        
		// Takes the rotation json object, and maybe gives a single axis rotation out
        @Nullable
        private CustomElementRotation getSingleRotation(JsonObject json, Vector3f origin, boolean rescale) {
        	// Get axis
            String axisName = GsonHelper.getAsString(json, "axis");
            Direction.Axis axis = Direction.Axis.byName(axisName.toLowerCase(Locale.ROOT));
            if(axis == null) {
                throw new JsonParseException("Invalid rotation axis: " + axis);
            }

            // Get angle
            float angle = GsonHelper.getAsFloat(json, "angle");
            if (angle == 0.0F || Mth.abs(angle) == 22.5F || Mth.abs(angle) == 45.0F) {
            	// This element's rotation can be parsed by the vanilla element model,
            	// so we return null to let the vanilla element model take over.
            	return null;
            }
            
            return switch (axis) {
            	case X -> new CustomElementRotation(origin, angle, 0.0f,  0.0f,  rescale);
            	case Y -> new CustomElementRotation(origin, 0.0f,  angle, 0.0f,  rescale);
            	case Z -> new CustomElementRotation(origin, 0.0f,  0.0f,  angle, rescale);
            };
        }

        private float getRotation(JsonObject json, String memberName) {
        	// No element, no rotation
        	if (!json.has(memberName)) {
            	return 0.0f;
        	}
        	
        	// Get the angle in the range [-90, 90]
    		float angle = GsonHelper.getAsFloat(json, memberName);
    		if (angle < -90.0f || angle > 90.0f) {
    			throw new JsonParseException("Expected rotation between -90.0 and 90.0, got " + angle);
    		}
    		
    		return angle;
        }
        
        private Vector3f getVector3f(JsonObject json, String memberName) {
            JsonArray jsonarray = GsonHelper.getAsJsonArray(json, memberName);
            if (jsonarray.size() != 3) {
                throw new JsonParseException("Expected 3 " + memberName + " values, found: " + jsonarray.size());
            } else {
                float[] afloat = new float[3];

                for (int i = 0; i < afloat.length; i++) {
                    afloat[i] = GsonHelper.convertToFloat(jsonarray.get(i), memberName + "[" + i + "]");
                }

                return new Vector3f(afloat[0], afloat[1], afloat[2]);
            }
        }
	}
	
}
