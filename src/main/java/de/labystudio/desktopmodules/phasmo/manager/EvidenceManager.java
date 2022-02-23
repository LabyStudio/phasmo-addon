package de.labystudio.desktopmodules.phasmo.manager;

import de.labystudio.desktopmodules.phasmo.type.EnumEvidence;
import de.labystudio.desktopmodules.phasmo.type.EnumEvidenceState;
import de.labystudio.desktopmodules.phasmo.type.EnumGhost;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyAdapter;
import org.jnativehook.keyboard.NativeKeyEvent;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EvidenceManager extends NativeKeyAdapter {

    public static final int MAX_EVIDENCE = 3;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private final Map<EnumEvidence, EnumEvidenceState> states = new HashMap<>();

    private EnumEvidence pressedEvidence = null;
    private boolean lastEvidenceNotPossible = false;

    private Robot robot;

    private ScheduledFuture<?> task;

    public EvidenceManager() {
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);

        GlobalScreen.addNativeKeyListener(this);

        for (EnumEvidence evidence : EnumEvidence.values()) {
            this.states.put(evidence, EnumEvidenceState.UNKNOWN);
        }

        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent event) {
        EnumEvidence evidence = EnumEvidence.getByKeyCode(event.getKeyCode());
        if (evidence != null) {
            if (evidence == this.pressedEvidence) {
                if (this.hasReachedMaxNotPossible()) {
                    this.states.put(evidence, EnumEvidenceState.NOT_POSSIBLE);
                }
                this.lastEvidenceNotPossible = true;
            } else {
                this.lastEvidenceNotPossible = false;
            }

            this.pressedEvidence = evidence;
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent event) {
        EnumEvidence evidence = EnumEvidence.getByKeyCode(event.getKeyCode());
        if (this.pressedEvidence != null && evidence == this.pressedEvidence) {
            EnumEvidenceState prevState = this.states.get(evidence);
            if (!this.lastEvidenceNotPossible) {
                if (prevState == EnumEvidenceState.UNKNOWN) {
                    if (!this.hasReachedMaxFound()) {
                        this.states.put(evidence, EnumEvidenceState.FOUND);
                    }
                } else {
                    this.states.put(evidence, EnumEvidenceState.UNKNOWN);
                }
            }

            this.pressedEvidence = null;
            this.lastEvidenceNotPossible = false;
        }

        // DELETE KEY - GUESS FINAL GHOST
        if (event.getKeyCode() == 3667) {
            EnumGhost ghost = this.getFinalGhost();

            if (ghost != null) {
                if (this.task != null) {
                    this.task.cancel(true);
                }

                // TODO support other screen resolutions
                this.task = this.executor.schedule(() -> {
                    // Open book
                    this.robot.keyPress(KeyEvent.VK_J);
                    this.robot.keyRelease(KeyEvent.VK_J);

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Point at evidence tab
                    this.robot.mouseMove(1000, 70);

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Go to evidence tab
                    this.robot.mousePress(1 << 4);
                    this.robot.mouseRelease(1 << 4);

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    int index = ghost.ordinal();

                    int x = index % 3;
                    int y = index / 3;

                    // Point at ghost
                    this.robot.mouseMove(1080 + x * 150, 520 + y * 44);

                    // Select ghost
                    this.robot.mousePress(1 << 4);
                    this.robot.mouseRelease(1 << 4);

                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Close book
                    this.robot.keyPress(KeyEvent.VK_J);
                    this.robot.keyRelease(KeyEvent.VK_J);
                }, 0, TimeUnit.MILLISECONDS);
            }
        }
    }

    public void onEnable() throws Exception {
        GlobalScreen.registerNativeHook();
    }

    public void onDisable() throws Exception {
        GlobalScreen.unregisterNativeHook();
    }

    private boolean hasReachedMaxNotPossible() {
        return this.getEvidencesInState(EnumEvidenceState.NOT_POSSIBLE).size() < EnumEvidence.values().length - MAX_EVIDENCE;
    }

    private boolean hasReachedMaxFound() {
        return this.getFoundEvidences().size() >= MAX_EVIDENCE;
    }

    public EnumEvidence getPressedEvidence() {
        return this.pressedEvidence;
    }

    public EnumEvidenceState getStateOf(EnumEvidence evidence) {
        EnumEvidenceState state = this.states.get(evidence);
        if (state != EnumEvidenceState.UNKNOWN) {
            return state;
        }
        return this.getGhostForEvidence(evidence).isEmpty() ? EnumEvidenceState.NOT_POSSIBLE : EnumEvidenceState.UNKNOWN;
    }

    public List<EnumEvidence> getFoundEvidences() {
        return this.getEvidencesInState(EnumEvidenceState.FOUND);
    }

    public List<EnumEvidence> getEvidencesInState(EnumEvidenceState state) {
        List<EnumEvidence> list = new ArrayList<>();
        for (Map.Entry<EnumEvidence, EnumEvidenceState> entry : this.states.entrySet()) {
            if (entry.getValue() == state) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    public List<EnumGhost> getGhostForEvidence(EnumEvidence evidence) {
        List<EnumEvidence> foundEvidences = this.getFoundEvidences();
        foundEvidences.add(evidence);
        return EnumGhost.getByEvidences(foundEvidences);
    }

    public EnumGhost getGhostForLastEvidence(EnumEvidence lastEvidence) {
        List<EnumGhost> ghosts = this.getGhostForEvidence(lastEvidence);
        return !this.hasReachedMaxFound() && ghosts.size() == 1 ? ghosts.get(0) : null;
    }

    public EnumGhost getFinalGhost() {
        List<EnumGhost> ghosts = EnumGhost.getByEvidences(this.getFoundEvidences());
        return this.hasReachedMaxFound() && ghosts.size() == 1 ? ghosts.get(0) : null;
    }
}
