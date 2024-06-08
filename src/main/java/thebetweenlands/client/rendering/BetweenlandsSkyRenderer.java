package thebetweenlands.client.rendering;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL40;

import net.minecraft.client.Minecraft;
import thebetweenlands.TheBetweenlands;

// This is to make super sure optifine and other mods do not atempt to overwrite this render sequance
// unless they actualy intend to
// Issues: this method may be more preformance expencive then others

// TODO: alow data packs to disable this renderer to alow for betweenlands compatable shaders to draw the sky instead
//		 which in theory should be more efficent then this method
//		 this method is being used for shader and optifine compat
public class BetweenlandsSkyRenderer {
	
	int vertexArrayID = -1;
	int vertexBufferID = -1;
	
	public boolean setup = false;
	
	IntBuffer skyboxModel;
	
	// use to prep the renderer to draw the sky sphere
	public void init() {
		
		setup = true;
		
		skyboxModel = this.Model();
		
		// Set up vertex buffer
		vertexBufferID = GL40.glGenBuffers();
		GL40.glBindBuffer(GL40.GL_ARRAY_BUFFER, vertexBufferID);
		GL40.glBufferData(GL40.GL_ARRAY_BUFFER, skyboxModel, GL40.GL_STATIC_DRAW);
		GL40.glBufferSubData(GL40.GL_ARRAY_BUFFER, 0, skyboxModel);
		
		// Set up vertex array
		vertexArrayID = GL40.glGenVertexArrays();
		GL40.glBindVertexArray(vertexArrayID);
		GL40.glVertexAttribPointer(0, 3, GL40.GL_FLOAT, false, 3, 0);
		
		// reset for saftey
		GL40.glBindVertexArray(0);
		GL40.glBindBuffer(GL40.GL_ARRAY_BUFFER, 0);
	}
	
	// generates the model to place in the skybox
	public IntBuffer Model() {
		
		// Generate the model
		IntBuffer out = BufferUtils.createIntBuffer(6912);
		
		// Model
		int HorzNodes = 24;
		int VertNodes = 24;
		
		float xmul = 1;
		float ymul = 1;
		float zmul = 1;
		
		float PlaceHorzAng = (float)(360 / HorzNodes);
		float PlaceVertAng = (float)(180 / VertNodes);
		float UVHorzAng = (float)(360 / HorzNodes);
		
		int HorzNodesHalf = HorzNodes/2;
		int VertNodesHalf = VertNodes/2;
		
		for (int loopx = 0; loopx < HorzNodes; loopx++)
		{
			
			if (loopx != 0) {
				continue;
			}
			
			for (int loopy = 0; loopy <= VertNodes+1; loopy++)
			{
				float uvalue;
				float zvalue;
				float modeY;
				float distance = 0;
				float angle = 0;
				
				modeY = (float) Math.cos(Math.toRadians((PlaceVertAng*(loopy+1)) + 90.0f));
				
				if (loopy == 0) {
					uvalue = 0.5f + ((float)(Math.cos(Math.toRadians((PlaceHorzAng*(loopx)))) * modeY)*0.35f);
					zvalue = 0.5f + ((float)(Math.sin(Math.toRadians((PlaceHorzAng*(loopx)))) * modeY)*0.35f);
				}
				else {
					distance = 0;
					if (loopy != 0) {
						distance = ((float)(loopy+1) / (float)VertNodes);
					}
					angle = ((float)PlaceHorzAng * (float)loopx) + 180.0f;
					uvalue = 0.5f + (float) (Math.cos(Math.toRadians(angle)) * distance);
					zvalue = 0.5f + (float) (Math.sin(Math.toRadians(angle)) * distance);
				}
				
				// color unused
				out.put(Float.floatToRawIntBits((float) (Math.cos(Math.toRadians(PlaceHorzAng*(loopx))) * Math.cos(Math.toRadians((PlaceVertAng*(loopy+1)) + 90.0f)) * xmul)));	// x
				out.put(Float.floatToRawIntBits((float) (Math.sin(Math.toRadians((PlaceVertAng*(loopy+1)) + 90.0f)) * ymul)));	// y
				out.put(Float.floatToRawIntBits((float) (Math.sin(Math.toRadians(PlaceHorzAng*(loopx))) * Math.cos(Math.toRadians((PlaceVertAng*(loopy+1)) + 90.0f))) * zmul));	// z
				//out.put(Float.floatToRawIntBits(uvalue));	// u
				//out.put(Float.floatToRawIntBits(zvalue));	// v
				
				// second point
				modeY = (float) Math.cos(Math.toRadians((PlaceVertAng*(loopy+1)) + 90.0f));
				if (loopy == 0) {
					uvalue = 0.5f + ((float)(Math.cos(Math.toRadians((PlaceHorzAng*(loopx+1)))) * modeY)*0.35f);
					zvalue = 0.5f + ((float)(Math.sin(Math.toRadians((PlaceHorzAng*(loopx+1)))) * modeY)*0.35f);
				}
				else {
					distance = 0;
					if (loopy != 0) {
						distance = ((float)(loopy+1) / (float)VertNodes);
					}
					angle = ((float)PlaceHorzAng * (float)(loopx+1)) + 180.0f;
					uvalue = 0.5f + (float) (Math.cos(Math.toRadians(angle)) * distance);
					zvalue = 0.5f + (float) (Math.sin(Math.toRadians(angle)) * distance);
				}
				
				out.put(Float.floatToRawIntBits((float) (Math.cos(Math.toRadians(PlaceHorzAng*(loopx+1))) * Math.cos(Math.toRadians((PlaceVertAng*(loopy+1)) + 90.0f)) * xmul)));	// x
				out.put(Float.floatToRawIntBits((float) (Math.sin(Math.toRadians((PlaceVertAng*(loopy+1)) + 90.0f)) * ymul)));	// y
				out.put(Float.floatToRawIntBits((float) (Math.sin(Math.toRadians(PlaceHorzAng*(loopx+1))) * Math.cos(Math.toRadians((PlaceVertAng*(loopy+1)) + 90.0f))) * zmul));	// z
				//out.put(Float.floatToRawIntBits(uvalue));	// u
				//out.put(Float.floatToRawIntBits(zvalue));	// v
				
				// third point
				modeY = (float) Math.cos(Math.toRadians((PlaceVertAng*(loopy)) + 90.0f));
				if (loopy == 0) {
					uvalue = 0.5f + ((float)(Math.cos(Math.toRadians((PlaceHorzAng*(loopx+1)))) * modeY)*0.35f);
					zvalue = 0.5f + ((float)(Math.sin(Math.toRadians((PlaceHorzAng*(loopx+1)))) * modeY)*0.35f);
				}
				else {
					distance = 0;
					if (loopy != 0) {
						distance = ((float)(loopy) / (float)VertNodes);
					}
					angle = ((float)PlaceHorzAng * (float)(loopx+1)) + 180.0f;
					uvalue = 0.5f + (float) (Math.cos(Math.toRadians(angle)) * distance);
					zvalue = 0.5f + (float) (Math.sin(Math.toRadians(angle)) * distance);
				}
				
				out.put(Float.floatToRawIntBits((float) (Math.cos(Math.toRadians(PlaceHorzAng*(loopx+1))) * Math.cos(Math.toRadians((PlaceVertAng*(loopy)) + 90.0f)) * xmul)));	// x
				out.put(Float.floatToRawIntBits((float) (Math.sin(Math.toRadians((PlaceVertAng*(loopy)) + 90.0f)) * ymul)));	// y
				out.put(Float.floatToRawIntBits((float) (Math.sin(Math.toRadians(PlaceHorzAng*(loopx+1))) * Math.cos(Math.toRadians((PlaceVertAng*(loopy)) + 90.0f))) * zmul));	// z
				//out.put(Float.floatToRawIntBits(uvalue));	// u
				//out.put(Float.floatToRawIntBits(zvalue));	// v
				
				// forth point
				modeY = (float) Math.cos(Math.toRadians((PlaceVertAng*(loopy)) + 90.0f));
				if (loopy == 0) {
					uvalue = 0.5f + ((float)(Math.cos(Math.toRadians((PlaceHorzAng*(loopx)))) * modeY)*0.35f);
					zvalue = 0.5f + ((float)(Math.sin(Math.toRadians((PlaceHorzAng*(loopx)))) * modeY)*0.35f);
				}
				else {
					distance = 0;
					if (loopy != 0) {
						distance = ((float)(loopy) / (float)VertNodes);
					}
					angle = ((float)PlaceHorzAng * (float)loopx) + 180.0f;
					uvalue = 0.5f + (float) (Math.cos(Math.toRadians(angle)) * distance);
					zvalue = 0.5f + (float) (Math.sin(Math.toRadians(angle)) * distance);
				}
				
				out.put(Float.floatToRawIntBits((float) (Math.cos(Math.toRadians(PlaceHorzAng*(loopx))) * Math.cos(Math.toRadians((PlaceVertAng*(loopy)) + 90.0f)) * xmul)));	// x
				out.put(Float.floatToRawIntBits((float) (Math.sin(Math.toRadians((PlaceVertAng*(loopy)) + 90.0f)) * ymul)));	// y
				out.put(Float.floatToRawIntBits((float) (Math.sin(Math.toRadians(PlaceHorzAng*(loopx))) * Math.cos(Math.toRadians((PlaceVertAng*(loopy)) + 90.0f))) * zmul));	// z
				//out.put(Float.floatToRawIntBits(uvalue));	// u
				//out.put(Float.floatToRawIntBits(zvalue));	// v
				
			}
		}
		
		// Rewind and send
		return out.rewind();
	}
	
	// Sets the shader prodram to use on the skybox model
	public void setShader() {
		
	}
	
	// Standard draw function, uses defalt settings
	public void draw() {
		
		// Update sky atlas if needed
    	if (!TheBetweenlands.skyTextureHandler.isLoaded) {
    		TheBetweenlands.skyTextureHandler.setup(Minecraft.getInstance().getResourceManager());
    	}
		
		GL40.glBindBuffer(GL40.GL_ARRAY_BUFFER, vertexBufferID);
		//TheBetweenlands.LOGGER.info("0");
		GL40.glBindVertexArray(vertexArrayID);
		//TheBetweenlands.LOGGER.info("1");
		GL40.glEnableVertexAttribArray(0);
		//TheBetweenlands.LOGGER.info("2");
		GL40.glActiveTexture(TheBetweenlands.skyTextureHandler.skyAtlas.getId());
		//TheBetweenlands.LOGGER.info("3");
		GL40.glDrawArrays(GL40.GL_TRIANGLES, 0, 10);
		//TheBetweenlands.LOGGER.info("4");
		
		GL40.glBindBuffer(GL40.GL_ARRAY_BUFFER, 0);
	}
	
	// Extended draw function, all settings customisable (used primeraly by resorce packs)
	public void drawEx() {
		
	}
}
