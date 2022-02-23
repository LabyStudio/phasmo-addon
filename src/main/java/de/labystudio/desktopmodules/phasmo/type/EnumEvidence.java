package de.labystudio.desktopmodules.phasmo.type;

public enum EnumEvidence {
    FREEZING_TEMPERATURE("Freezing Temperature", 3655),
    EMF_5("EMF-5", 57419),
    GHOST_ORBS("Ghost Orbs", 3663),
    SPIRIT_BOX("Spirit Box", 57416),
    GHOST_WRITING("Ghost Writing", 57420),
    FINGERPRINTS("Fingerprints", 57424),
    DOTS_PROJECTOR("D.O.T.S", 3657);

    private final String displayName;
    private final int keyCode;

    EnumEvidence(String displayName, int keyCode) {
        this.displayName = displayName;
        this.keyCode = keyCode;
    }

    public static EnumEvidence getByKeyCode(int keyCode) {
        for (EnumEvidence evidence : values()) {
            if (evidence.keyCode == keyCode) {
                return evidence;
            }
        }
        return null;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
