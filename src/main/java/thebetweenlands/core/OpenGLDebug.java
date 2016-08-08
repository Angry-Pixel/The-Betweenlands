package thebetweenlands.core;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class OpenGLDebug implements IClassTransformer {
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if(name != null) {
			return writeClass(transformClass(readClass(basicClass)));
		}
		return basicClass;
	}

	private ClassNode readClass(byte[] classBytes) {
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(classBytes);
		classReader.accept(classNode, 0);
		return classNode;
	}

	private byte[] writeClass(ClassNode classNode) {
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(classWriter);
		return classWriter.toByteArray();
	}

	//Any methods that can be called inside a glBegin/End block
	private static final String[] checkExclusions = new String[] {
			"glBegin", "glEnd", "glVertex", "glColor", "glSecondaryColor", "glIndex", 
			"glNormal", "glFogCoord", "glTexCoord", "glMultiTexCoord", "glVertexAttrib", 
			"glEvalCoord", "glEvalPoint", "glArrayElement", "glMaterial", "glEdgeFlag"
	};

	private ClassNode transformClass(ClassNode classNode) {
		for (MethodNode method : classNode.methods) {
			for (int i = 0; i < method.instructions.size(); i++) {
				AbstractInsnNode insnNode = method.instructions.get(i);
				if (insnNode.getOpcode() == Opcodes.INVOKESTATIC) {
					MethodInsnNode methodInsn = (MethodInsnNode) insnNode;
					if(!this.isExcluded(methodInsn.name) && methodInsn.owner.startsWith("org/lwjgl/opengl")) {
						InsnList checkErrorInsns = new InsnList();
						method.instructions.insert(methodInsn, new MethodInsnNode(INVOKESTATIC, OpenGLDebug.class.getCanonicalName().replace(".", "/"), "checkThrowError", "()V", false));
						System.out.println(String.format("[OpenGLDebug] Hooked method '%s' at '%s' (%s)", classNode.name + "#" + method.name, methodInsn.name + methodInsn.desc, i));
					}
				}
			}
		}
		return classNode;
	}

	private boolean isExcluded(String method) {
		for(String exclusion : checkExclusions)
			if(method.startsWith(exclusion))
				return true;
		return false;
	}

	public static class OpenGLError extends Error {
		public OpenGLError(String errors) {
			super("An OpenGL Error has occurred: " + errors);
		}
	}

	private static final List<Integer> foundErrors = new ArrayList<Integer>();

	public static void checkThrowError() {
		if(checkGlContext()) {
			int glErrorId;
			while((glErrorId = GL11.glGetError()) != 0) {
				foundErrors.add(glErrorId);
			}
			if (!foundErrors.isEmpty()) {
				StringBuilder errors = new StringBuilder();
				for(int i = 0; i < foundErrors.size(); i++) {
					errors.append(GLU.gluErrorString(foundErrors.get(i))).append(" (").append(foundErrors.get(i)).append(")");
					if(i < foundErrors.size() - 1)
						errors.append(", ");
				}
				foundErrors.clear();
				new OpenGLError(errors.toString()).printStackTrace();
			}
		}
	}

	public static boolean checkGlContext() {
		try {
			ContextCapabilities capabilities = GLContext.getCapabilities();
			return capabilities != null;
		} catch(Exception ex) { 
			return false;
		}
	}
}
