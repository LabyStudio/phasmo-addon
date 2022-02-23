package de.labystudio.desktopmodules.phasmo;

import de.labystudio.desktopmodules.core.addon.Addon;
import de.labystudio.desktopmodules.phasmo.manager.EvidenceManager;
import de.labystudio.desktopmodules.phasmo.modules.EvidenceModule;

public class PhasmoAddon extends Addon {

    private final EvidenceManager evidenceManager = new EvidenceManager();

    @Override
    public void onInitialize() throws Exception {
        // Register modules
        this.registerModule(EvidenceModule.class);

        // Save config to create default values
        this.saveConfig();
    }

    @Override
    public void onEnable() throws Exception {
        this.evidenceManager.onEnable();
    }

    @Override
    public void onDisable() throws Exception {
        this.evidenceManager.onDisable();
    }

    public EvidenceManager getEvidenceManager() {
        return this.evidenceManager;
    }
}
