package net.wheel.cutils.impl.patch;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import net.wheel.cutils.api.event.gui.EventBookPage;
import net.wheel.cutils.api.event.gui.EventBookTitle;
import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.api.patch.MethodPatch;
import net.wheel.cutils.crack;
import net.wheel.cutils.impl.management.PatchManager;

public final class GuiScreenBookPatch extends ClassPatch {

    public GuiScreenBookPatch() {
        super("net.minecraft.client.gui.GuiScreenBook", "bmj");
    }

    public static String pageInsertIntoCurrentHook(String page) {

        final EventBookPage event = new EventBookPage(page);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.getPage();
    }

    public static String keyTypedInTitleHook(String title) {

        final EventBookTitle event = new EventBookTitle(title);
        crack.INSTANCE.getEventManager().dispatchEvent(event);

        return event.getTitle();
    }

    @MethodPatch(mcpName = "pageInsertIntoCurrent", notchName = "b", mcpDesc = "(Ljava/lang/String;)V")
    public void pageInsertIntoCurrent(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new VarInsnNode(ALOAD, 1));

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()),
                "pageInsertIntoCurrentHook", "(Ljava/lang/String;)Ljava/lang/String;", false));

        insnList.add(new VarInsnNode(ASTORE, 1));

        methodNode.instructions.insert(insnList);
    }

    @MethodPatch(mcpName = "keyTypedInTitle", notchName = "c", mcpDesc = "(CI)V")
    public void keyTypedInTitle(MethodNode methodNode, PatchManager.Environment env) {

        final InsnList insnList = new InsnList();

        insnList.add(new VarInsnNode(ALOAD, 0));
        insnList.add(new VarInsnNode(ALOAD, 0));

        insnList.add(new FieldInsnNode(GETFIELD,
                env == PatchManager.Environment.IDE ? "net/minecraft/client/gui/GuiScreenBook" : "bmj",
                env == PatchManager.Environment.IDE ? "bookTitle" : "A", "Ljava/lang/String;"));

        insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(this.getClass()), "keyTypedInTitleHook",
                "(Ljava/lang/String;)Ljava/lang/String;", false));

        insnList.add(new FieldInsnNode(PUTFIELD,
                env == PatchManager.Environment.IDE ? "net/minecraft/client/gui/GuiScreenBook" : "bmj",
                env == PatchManager.Environment.IDE ? "bookTitle" : "A", "Ljava/lang/String;"));

        methodNode.instructions.insert(insnList);
    }

}
