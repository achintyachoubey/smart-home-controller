package com.smarthome.model;

import com.smarthome.exception.CapacityExceededException;
import com.smarthome.exception.DeviceNotFoundException;
import com.smarthome.exception.InvalidInputException;
import com.smarthome.util.InputHelper;

/**
 * A named automation, such as "Night Mode", made of up to
 * {@link #MAX_ACTIONS} steps that are run in order.
 *
 * <p>OOP concepts: <b>composition</b> (a scene <i>has</i> scene actions),
 * <b>arrays</b> with add / remove-with-shift / copy, and <b>exception
 * handling</b>: {@link #run(Home)} catches the exception of each step
 * separately, so one failing step never stops the remaining steps.</p>
 */
public class Scene {

    /** Maximum number of steps in one scene. */
    public static final int MAX_ACTIONS = 15;

    private final String name;
    private final SceneAction[] actions = new SceneAction[MAX_ACTIONS];
    private int actionCount;
    private int lastSuccessCount;

    /**
     * Creates an empty scene.
     *
     * @param name scene name (not empty, at most 30 characters, no '|')
     * @throws InvalidInputException if the name is invalid
     */
    public Scene(String name) throws InvalidInputException {
        this.name = InputHelper.validateName(name);
    }

    /** @return the scene name */
    public String getName() {
        return name;
    }

    /**
     * Appends a step.
     *
     * @param a the step to add
     * @throws CapacityExceededException if the scene already has 15 steps
     */
    public void addAction(SceneAction a) throws CapacityExceededException {
        if (actionCount == MAX_ACTIONS) {
            throw new CapacityExceededException("Scene '" + name + "' already has the maximum of "
                    + MAX_ACTIONS + " steps.");
        }
        actions[actionCount] = a;
        actionCount++;
    }

    /**
     * Removes a step and moves the later steps up.
     *
     * @param index position of the step, starting at 0
     * @return the removed step
     * @throws InvalidInputException if there is no step at that position
     */
    public SceneAction removeAction(int index) throws InvalidInputException {
        if (index < 0 || index >= actionCount) {
            throw new InvalidInputException("Scene '" + name + "' has no step number " + (index + 1) + ".");
        }
        SceneAction removed = actions[index];
        // shift later steps left so the order of the remaining steps is kept
        for (int i = index; i < actionCount - 1; i++) {
            actions[i] = actions[i + 1];
        }
        actions[actionCount - 1] = null;
        actionCount--;
        return removed;
    }

    /** @return a copy of the steps, trimmed to the number actually stored */
    public SceneAction[] getActions() {
        SceneAction[] copy = new SceneAction[actionCount];
        for (int i = 0; i < actionCount; i++) {
            copy[i] = actions[i];
        }
        return copy;
    }

    /** @return how many steps the scene has */
    public int getActionCount() {
        return actionCount;
    }

    /** @return how many steps succeeded the last time {@link #run(Home)} was called */
    public int getLastSuccessCount() {
        return lastSuccessCount;
    }

    /**
     * Runs every step in order. A step that fails (missing device, wrong device
     * type, value out of range, lock powered off) is reported and skipped.
     *
     * @param home the home whose devices the steps refer to
     * @return a report with one "[OK]" or "[FAILED] reason" line per step and a summary line
     */
    public String run(Home home) {
        StringBuilder report = new StringBuilder();
        int succeeded = 0;
        for (int i = 0; i < actionCount; i++) {
            report.append(String.format("  %2d. ", i + 1));
            try {
                Device d = home.findDeviceAnywhere(actions[i].getDeviceId());
                report.append("[OK] ").append(actions[i].execute(d));
                succeeded++;
            } catch (DeviceNotFoundException | InvalidInputException e) {
                report.append("[FAILED] ").append(actions[i].getDeviceId()).append(" ")
                        .append(actions[i].describe()).append(": ").append(e.getMessage());
            }
            report.append(System.lineSeparator());
        }
        lastSuccessCount = succeeded;
        report.append(name).append(" finished: ").append(succeeded).append(" of ")
                .append(actionCount).append(" steps succeeded.");
        return report.toString();
    }
}
