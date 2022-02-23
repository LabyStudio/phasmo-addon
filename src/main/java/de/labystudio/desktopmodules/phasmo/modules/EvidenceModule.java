package de.labystudio.desktopmodules.phasmo.modules;

import de.labystudio.desktopmodules.core.loader.TextureLoader;
import de.labystudio.desktopmodules.core.module.Module;
import de.labystudio.desktopmodules.core.renderer.IRenderContext;
import de.labystudio.desktopmodules.core.renderer.font.Font;
import de.labystudio.desktopmodules.core.renderer.font.FontStyle;
import de.labystudio.desktopmodules.core.renderer.font.StringAlignment;
import de.labystudio.desktopmodules.core.renderer.font.StringEffect;
import de.labystudio.desktopmodules.phasmo.PhasmoAddon;
import de.labystudio.desktopmodules.phasmo.manager.EvidenceManager;
import de.labystudio.desktopmodules.phasmo.type.EnumEvidence;
import de.labystudio.desktopmodules.phasmo.type.EnumEvidenceState;
import de.labystudio.desktopmodules.phasmo.type.EnumGhost;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.util.HashMap;
import java.util.Map;

public class EvidenceModule extends Module<PhasmoAddon> {

    private static final Font FONT_RESULT = new Font("Impact", FontStyle.PLAIN, 18);
    private static final Font FONT_HINT = new Font("Impact", FontStyle.PLAIN, 12);

    private static final int ICON_SIZE = 60;
    private static final int RESULT_HEIGHT = 30;

    private final Map<EnumEvidence, BufferedImage> enabledIcons = new HashMap<>();
    private final Map<EnumEvidence, BufferedImage> disabledIcons = new HashMap<>();

    public EvidenceModule() {
        super(ICON_SIZE * 3, EnumEvidence.values().length / 2 * ICON_SIZE + RESULT_HEIGHT);
    }

    @Override
    public void onRender(IRenderContext context, int width, int height, int mouseX, int mouseY) {
        super.onRender(context, width, height, mouseX, mouseY);

        EvidenceManager manager = this.addon.getEvidenceManager();

        int xIndex = 0;
        int yIndex = 0;
        for (EnumEvidence evidence : EnumEvidence.values()) {
            EnumEvidenceState state = manager.getStateOf(evidence);
            EnumGhost ghost = manager.getGhostForLastEvidence(evidence);

            int x = this.rightBound ? width - ICON_SIZE * 3 + ICON_SIZE * xIndex : ICON_SIZE * xIndex;

            // Render evidence
            this.drawEvidence(context, evidence, state, ghost, x, yIndex * ICON_SIZE + RESULT_HEIGHT);

            yIndex++;
            if (yIndex >= 3) {
                yIndex = 0;
                xIndex++;
            }
        }

        // Render result
        EnumGhost ghost = manager.getFinalGhost();
        if (ghost != null) {
            context.setAlpha(1);
            context.drawString(
                    ghost.getDisplayName(),
                    width,
                    2,
                    RESULT_HEIGHT - 10,
                    this.rightBound,
                    StringEffect.SHADOW,
                    Color.WHITE,
                    FONT_RESULT
            );
        }
    }

    @Override
    public void onTick() {
        super.onTick();

    }

    @Override
    public void loadTextures(TextureLoader textureLoader) {
        BufferedImage noneIcon = textureLoader.load("textures/phasmo/evidence/none.png");

        for (EnumEvidence evidence : EnumEvidence.values()) {
            try {
                BufferedImage enabledIcon = textureLoader.load("textures/phasmo/evidence/" + evidence.name().toLowerCase() + ".png");
                this.enabledIcons.put(evidence, enabledIcon);

                // Apply grey filter
                ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
                ColorConvertOp op = new ColorConvertOp(colorSpace, null);
                BufferedImage disabledIcon = op.filter(enabledIcon, null);

                this.disabledIcons.put(evidence, disabledIcon);
            } catch (Exception e) {
                System.out.println("Could not load icon of " + evidence.getDisplayName());

                this.enabledIcons.put(evidence, noneIcon);
                this.disabledIcons.put(evidence, noneIcon);
            }
        }
    }

    private void drawEvidence(
            IRenderContext context,
            EnumEvidence evidence,
            EnumEvidenceState state,
            EnumGhost ghost,
            int x, int y
    ) {
        boolean notPossible = state == EnumEvidenceState.NOT_POSSIBLE;
        boolean found = state == EnumEvidenceState.FOUND;
        BufferedImage icon = found ? this.enabledIcons.get(evidence) : this.disabledIcons.get(evidence);

        // Render evidence icon
        context.setAlpha(notPossible ? 0.05F : found ? 1.0F : 0.3F);
        context.drawImage(icon, x, y, ICON_SIZE, ICON_SIZE);

        // Render ghost name hint
        if (ghost != null) {
            context.drawString(
                    ghost.getDisplayName(),
                    x + ICON_SIZE / 2F,
                    y + ICON_SIZE - 2,
                    StringAlignment.CENTERED,
                    StringEffect.NONE,
                    Color.WHITE, FONT_HINT
            );
        }
    }

    @Override
    protected String getIconPath() {
        return "textures/phasmo/icon.png";
    }

    @Override
    public String getDisplayName() {
        return "Phasmo Evidence Selector";
    }
}
