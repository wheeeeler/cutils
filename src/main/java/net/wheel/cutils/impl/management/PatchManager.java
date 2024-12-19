package net.wheel.cutils.impl.management;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.wheel.cutils.api.patch.ClassPatch;
import net.wheel.cutils.impl.patch.*;

@Getter
@Setter
public final class PatchManager {

    private List<ClassPatch> patchList = new ArrayList<ClassPatch>();

    private Environment env;

    public PatchManager(final boolean devEnv) {

        this.setEnv(devEnv ? Environment.IDE : Environment.RELEASE);

        this.patchList.add(new MinecraftPatch());
        this.patchList.add(new EntityRendererPatch());
        this.patchList.add(new WorldPatch());
        this.patchList.add(new NetworkManagerPatch());
        this.patchList.add(new PlayerControllerMPPatch());
        this.patchList.add(new VisGraphPatch());
        this.patchList.add(new EntityPlayerSPPatch());
        this.patchList.add(new EntityPlayerPatch());
        this.patchList.add(new GuiScreenBookPatch());
        this.patchList.add(new GuiIngameForgePatch());
        this.patchList.add(new ItemRendererPatch());
        this.patchList.add(new RenderManagerPatch());
        this.patchList.add(new RenderLivingBasePatch());
        this.patchList.add(new EntityPigPatch());
        this.patchList.add(new EntityLlamaPatch());
        this.patchList.add(new AbstractHorsePatch());
        this.patchList.add(new BlockRendererDispatcherPatch());
        this.patchList.add(new BlockPatch());
        this.patchList.add(new BlockSoulSandPatch());
        this.patchList.add(new KeyBindingPatch());
        this.patchList.add(new ActiveRenderInfoPatch());
        this.patchList.add(new BlockSlimePatch());
        this.patchList.add(new BlockLiquidPatch());
        this.patchList.add(new EntityPatch());
        this.patchList.add(new AbstractClientPlayerPatch());
        this.patchList.add(new BiomeColorHelperPatch());
        this.patchList.add(new GuiBossOverlayPatch());
        this.patchList.add(new NetHandlerPlayClientPatch());
        this.patchList.add(new ChunkPatch());
        this.patchList.add(new GuiScreenPatch());
        this.patchList.add(new RenderGlobalPatch());
        this.patchList.add(new GuiChatPatch());
        this.patchList.add(new ParticleManagerPatch());
        this.patchList.add(new GuiPlayerTabOverlayPatch());
        this.patchList.add(new GuiToastPatch());

    }

    public ClassPatch findClassPatch(String name) {
        for (ClassPatch patch : this.patchList) {
            if (patch != null) {
                String patchName = patch.getMcpName();

                if (this.env == Environment.RELEASE) {
                    if (patch.getNotchName() != null && !patch.getNotchName().isEmpty()) {
                        patchName = patch.getNotchName();
                    }
                }

                if (name.equals(patchName)) {
                    return patch;
                }
            }
        }
        return null;
    }

    public enum Environment {
        IDE, RELEASE
    }

}
