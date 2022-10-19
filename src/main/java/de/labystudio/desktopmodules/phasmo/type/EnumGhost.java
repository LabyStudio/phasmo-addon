package de.labystudio.desktopmodules.phasmo.type;

import java.util.ArrayList;
import java.util.List;

import static de.labystudio.desktopmodules.phasmo.type.EnumEvidence.DOTS_PROJECTOR;
import static de.labystudio.desktopmodules.phasmo.type.EnumEvidence.EMF_5;
import static de.labystudio.desktopmodules.phasmo.type.EnumEvidence.FINGERPRINTS;
import static de.labystudio.desktopmodules.phasmo.type.EnumEvidence.FREEZING_TEMPERATURE;
import static de.labystudio.desktopmodules.phasmo.type.EnumEvidence.GHOST_ORBS;
import static de.labystudio.desktopmodules.phasmo.type.EnumEvidence.GHOST_WRITING;
import static de.labystudio.desktopmodules.phasmo.type.EnumEvidence.SPIRIT_BOX;

public enum EnumGhost {
    SPIRIT("Spirit", EMF_5, SPIRIT_BOX, GHOST_WRITING),
    WRAITH("Wraith", EMF_5, SPIRIT_BOX, DOTS_PROJECTOR),
    PHANTOM("Phantom", SPIRIT_BOX, FINGERPRINTS, DOTS_PROJECTOR),
    POLTERGEIST("Poltergeist", SPIRIT_BOX, FINGERPRINTS, GHOST_WRITING),
    BANSHEE("Banshee", GHOST_ORBS, FINGERPRINTS, DOTS_PROJECTOR),
    JINN("Jinn", EMF_5, FREEZING_TEMPERATURE, FINGERPRINTS),
    MARE("Mare", GHOST_ORBS, SPIRIT_BOX, GHOST_WRITING),
    REVENANT("Revenant", GHOST_ORBS, FREEZING_TEMPERATURE, GHOST_WRITING),
    SHADE("Shade", EMF_5, FREEZING_TEMPERATURE, GHOST_WRITING),
    DEMON("Demon", FREEZING_TEMPERATURE, FINGERPRINTS, GHOST_WRITING),
    YUREI("Yurei", GHOST_ORBS, FREEZING_TEMPERATURE, DOTS_PROJECTOR),
    ONI("Oni", EMF_5, FREEZING_TEMPERATURE, DOTS_PROJECTOR),
    YOKAI("Yokai", GHOST_ORBS, SPIRIT_BOX, DOTS_PROJECTOR),
    HANTU("Hantu", GHOST_ORBS, FREEZING_TEMPERATURE, FINGERPRINTS),
    GORYO("Goryo", EMF_5, FINGERPRINTS, DOTS_PROJECTOR),
    MYLING("Myling", EMF_5, FINGERPRINTS, GHOST_WRITING),
    ONRYO("Onryo", SPIRIT_BOX, GHOST_ORBS, FREEZING_TEMPERATURE),
    THE_TWINS("The Twins", EMF_5, SPIRIT_BOX, FREEZING_TEMPERATURE),
    RAIJU("Raiju", EMF_5, GHOST_ORBS, DOTS_PROJECTOR),
    OBAKE("Obake", EMF_5, FINGERPRINTS, GHOST_ORBS),
    THE_MIMIC("The Mimic", SPIRIT_BOX, FINGERPRINTS, FREEZING_TEMPERATURE),
    MOROI("Moroi", SPIRIT_BOX, FREEZING_TEMPERATURE, GHOST_WRITING),
    DEOGEN("Deogen", SPIRIT_BOX, GHOST_WRITING, DOTS_PROJECTOR),
    THAYE("Thaye", GHOST_ORBS, GHOST_WRITING, DOTS_PROJECTOR);

    private final String displayName;
    private final EnumEvidence[] evidences;

    EnumGhost(String displayName, EnumEvidence... evidences) {
        this.displayName = displayName;
        this.evidences = evidences;
    }

    public static List<EnumGhost> getByEvidences(List<EnumEvidence> evidences) {
        List<EnumGhost> ghosts = new ArrayList<>();
        for (EnumGhost ghost : EnumGhost.values()) {
            if (ghost.hasMatchingEvidences(evidences)) {
                ghosts.add(ghost);
            }
        }
        return ghosts;
    }

    private boolean hasMatchingEvidences(List<EnumEvidence> evidences) {
        for (EnumEvidence evidence : evidences) {
            if (!this.hasEvidence(evidence)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasEvidence(EnumEvidence evidence) {
        for (EnumEvidence enumEvidence : this.evidences) {
            if (enumEvidence == evidence) {
                return true;
            }
        }
        return false;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public EnumEvidence[] getEvidences() {
        return this.evidences;
    }
}
