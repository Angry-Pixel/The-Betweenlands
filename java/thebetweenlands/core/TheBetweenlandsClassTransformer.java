package thebetweenlands.core;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class TheBetweenlandsClassTransformer implements IClassTransformer {
	public static final String SLEEP_PER_TICK = "sleepPerTick";

	public long sleepPerTick;

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if ("net.minecraft.server.MinecraftServer".equals(name)) {
			sleepPerTick = 50L;
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(basicClass);
			classReader.accept(classNode, 0);
			FieldNode sleepPerTickField = new FieldNode(Opcodes.ACC_PUBLIC, SLEEP_PER_TICK, "J", null, null);
			classNode.fields.add(sleepPerTickField);
			boolean needsRun = true, needsInit = true; 
			for (MethodNode method : classNode.methods) {
				if (needsInit && "<init>".equals(method.name)) {
					needsInit = false;
					for (int i = 0; i < method.instructions.size(); i++) {
						AbstractInsnNode insnNode = method.instructions.get(i);
						if (insnNode.getOpcode() == Opcodes.RETURN) {
							InsnList initSleepPerTickInsns = new InsnList();
							initSleepPerTickInsns.add(new VarInsnNode(Opcodes.ALOAD, 0));
							initSleepPerTickInsns.add(new LdcInsnNode(50L));
							initSleepPerTickInsns.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/server/MinecraftServer", SLEEP_PER_TICK, "J"));
							method.instructions.insertBefore(insnNode, initSleepPerTickInsns);
							break;
						}
					}
				}
				if (needsRun && "run".equals(method.name)) {
					needsRun = false;
					Long fifty = Long.valueOf(50);
					for (int i = 0; i < method.instructions.size(); i++) {
						AbstractInsnNode insnNode = method.instructions.get(i);
						if (insnNode.getType() == AbstractInsnNode.LDC_INSN && fifty.equals(((LdcInsnNode) insnNode).cst)) {
							InsnList accessSleepPerTickInsns = new InsnList();
							accessSleepPerTickInsns.add(new VarInsnNode(Opcodes.ALOAD, 0));
							accessSleepPerTickInsns.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/server/MinecraftServer", SLEEP_PER_TICK, "J"));
							method.instructions.insert(insnNode, accessSleepPerTickInsns);
							method.instructions.remove(insnNode);
							break;
						}
					}
				}
				if (!needsRun && !needsInit) {
					break;
				}
			}
			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			classNode.accept(classWriter);
			return classWriter.toByteArray();
		}
		return basicClass;
	}
}
