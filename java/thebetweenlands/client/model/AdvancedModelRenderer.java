package thebetweenlands.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.TextureOffset;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;
import thebetweenlands.utils.MathUtils;
import thebetweenlands.utils.RotationOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Paul Fulham
 *
 */
public class AdvancedModelRenderer extends MowzieModelRenderer {
	protected int textureOffsetX;
	protected int textureOffsetY;

	protected boolean compiled;

	protected int displayList;

	protected ModelBase modelBase;

	protected float scaleX;
	protected float scaleY;
	protected float scaleZ;

	protected RotationOrder rotationOrder;

	protected List<IModelRenderCallback> callbacks = new ArrayList<IModelRenderCallback>();

	public AdvancedModelRenderer(ModelBase modelBase) {
		this(modelBase, (String) null);
	}

	public AdvancedModelRenderer(ModelBase modelBase, int textureOffsetX, int textureOffsetY) {
		this(modelBase);
		setTextureOffset(textureOffsetX, textureOffsetY);
	}

	public AdvancedModelRenderer(ModelBase modelBase, String name) {
		super(modelBase, name);
		this.modelBase = modelBase;
		setTextureSize(modelBase.textureWidth, modelBase.textureHeight);
		scaleX = scaleY = scaleZ = 1;
		rotationOrder = RotationOrder.ZYX;
	}

	@Override
	public AdvancedModelRenderer addBox(float posX, float posY, float posZ, int width, int height, int depth) {
		cubeList.add(new ModelBox(this, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height, depth, 0));
		return this;
	}

	@Override
	public void addBox(float posX, float posY, float posZ, int width, int height, int depth, float scale) {
		cubeList.add(new ModelBox(this, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height, depth, scale));
	}

	public AdvancedModelRenderer appendBox(float posX, float posY, float posZ, int width, int height, int depth, float scale) {
		cubeList.add(new ModelBox(this, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height, depth, scale));
		return this;
	}

	@Override
	public AdvancedModelRenderer addBox(String name, float posX, float posY, float posZ, int width, int height, int depth) {
		name = boxName + "." + name;
		TextureOffset textureoffset = modelBase.getTextureOffset(name);
		setTextureOffset(textureoffset.textureOffsetX, textureoffset.textureOffsetY);
		cubeList.add(new ModelBox(this, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height, depth, 0).func_78244_a(name));
		return this;
	}

	public AdvancedModelRenderer add3DTexture(float posX, float posY, float posZ, int width, int height) {
		cubeList.add(new Model3DTexture(this, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height));
		return this;
	}

	public AdvancedModelRenderer appendChild(ModelRenderer modelRenderer) {
		super.addChild(modelRenderer);
		return this;
	}

	public void addCallback(IModelRenderCallback callback) {
		callbacks.add(callback);
	}

	protected void compileDisplayList(float scale) {
		displayList = GLAllocation.generateDisplayLists(1);
		GL11.glNewList(displayList, GL11.GL_COMPILE);
		Tessellator tessellator = Tessellator.instance;

		for (int i = 0; i < cubeList.size(); i++) {
			((ModelBox) cubeList.get(i)).render(tessellator, scale);
		}

		GL11.glEndList();
		compiled = true;
	}

	@Override
	public void postRender(float scale) {
		if (!isHidden) {
			if (showModel) {
				if (!compiled) {
					compileDisplayList(scale);
				}

				if (rotateAngleX == 0 && rotateAngleY == 0 && rotateAngleZ == 0) {
					if (rotationPointX != 0 || rotationPointY != 0 || rotationPointZ != 0F) {
						GL11.glTranslatef(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
					}
				} else {
					GL11.glTranslatef(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);

					rotationOrder.rotate(rotateAngleX * MathUtils.RAD_TO_DEG, rotateAngleY * MathUtils.RAD_TO_DEG, rotateAngleZ * MathUtils.RAD_TO_DEG);
				}
			}
		}
	}

	private void callback(float scale) {
		for (IModelRenderCallback callback : callbacks) {
			callback.render(this, scale);
		}
	}

	@Override
	public void render(float scale) {
		if (!isHidden) {
			if (showModel) {
				if (!compiled) {
					compileDisplayList(scale);
				}

				GL11.glTranslatef(offsetX, offsetY, offsetZ);

				if (rotateAngleX == 0 && rotateAngleY == 0 && rotateAngleZ == 0) {
					if (rotationPointX == 0 && rotationPointY == 0 && rotationPointZ == 0) {
						if (scaleX == 1 && scaleY == 1 && scaleZ == 1) {
							GL11.glCallList(displayList);
							callback(scale);

							if (childModels != null) {
								for (int i = 0; i < childModels.size(); i++) {
									((ModelRenderer) childModels.get(i)).render(scale);
								}
							}
						} else {
							GL11.glPushMatrix();
							GL11.glScalef(scaleX, scaleY, scaleZ);

							GL11.glCallList(displayList);
							callback(scale);

							if (childModels != null) {
								for (int i = 0; i < childModels.size(); ++i) {
									((ModelRenderer) childModels.get(i)).render(scale);
								}
							}

							GL11.glPopMatrix();
						}
					} else {
						GL11.glPushMatrix();
						GL11.glTranslatef(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
						GL11.glScalef(scaleX, scaleY, scaleZ);

						GL11.glCallList(displayList);
						callback(scale);

						if (childModels != null) {
							for (int i = 0; i < childModels.size(); ++i) {
								((ModelRenderer) childModels.get(i)).render(scale);
							}
						}

						GL11.glPopMatrix();
					}
				} else {
					GL11.glPushMatrix();
					GL11.glTranslatef(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);

					rotationOrder.rotate(rotateAngleX * MathUtils.RAD_TO_DEG, rotateAngleY * MathUtils.RAD_TO_DEG, rotateAngleZ * MathUtils.RAD_TO_DEG);

					GL11.glScalef(scaleX, scaleY, scaleZ);

					GL11.glCallList(displayList);
					callback(scale);

					if (childModels != null) {
						for (int i = 0; i < childModels.size(); i++) {
							((ModelRenderer) childModels.get(i)).render(scale);
						}
					}

					GL11.glPopMatrix();
				}

				GL11.glTranslatef(-offsetX, -offsetY, -offsetZ);
			}
		}
	}

	@Override
	public void renderWithRotation(float scale) {
		if (!isHidden) {
			if (showModel) {
				if (!compiled) {
					compileDisplayList(scale);
				}

				GL11.glPushMatrix();
				GL11.glTranslatef(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);

				rotationOrder.rotate(rotateAngleX * MathUtils.RAD_TO_DEG, rotateAngleY * MathUtils.RAD_TO_DEG, rotateAngleZ * MathUtils.RAD_TO_DEG);

				GL11.glScalef(scaleX, scaleY, scaleZ);

				GL11.glCallList(displayList);
				callback(scale);
				GL11.glPopMatrix();
			}
		}
	}

	@Override
	public void setRotationPoint(float rotationPointX, float rotationPointY, float rotationPointZ) {
		this.rotationPointX = rotationPointX;
		this.rotationPointY = rotationPointY;
		this.rotationPointZ = rotationPointZ;
	}

	public AdvancedModelRenderer setRotPoint(float rotationPointX, float rotationPointY, float rotationPointZ) {
		setRotationPoint(rotationPointX, rotationPointY, rotationPointZ);
		return this;
	}

	@Override
	public AdvancedModelRenderer setTextureOffset(int textureOffsetX, int textureOffsetY) {
		this.textureOffsetX = textureOffsetX;
		this.textureOffsetY = textureOffsetY;
		return this;
	}

	@Override
	public AdvancedModelRenderer setTextureSize(int textureWidth, int textureHeight) {
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		return this;
	}

	public void setRotationOrder(RotationOrder rotationOrder) {
		this.rotationOrder = rotationOrder;
	}

	public RotationOrder getRotationOrder() {
		return rotationOrder;
	}

	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	public void setScaleZ(float scaleZ) {
		this.scaleZ = scaleZ;
	}

	public void setScale(float scale) {
		scaleX = scaleY = scaleZ = scale;
	}
}