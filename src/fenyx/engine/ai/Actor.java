package fenyx.engine.ai;

import java.util.HashSet;

import fenyx.engine.geom.Shape;
import fenyx.engine.world.PointObject;

/**
 *
 * @author KiQDominaN
 */
public abstract class Actor extends PointObject {

    protected Shape.Rect search_zone;
    protected HashSet<Task> possible_tasks;
    protected Task current_task;
    public boolean override_ai = false;

    public void overrideAI(boolean override) {
        override_ai = override;
    }

    public void runAI() {
        if (current_task != null) {
            if (current_task.canUpdate()) current_task.update();

            if (current_task.done()) chooseTask();
        } else
            chooseTask();
    }

    public void addTask(Task t) {
        if (possible_tasks == null) possible_tasks = new HashSet<>();
        possible_tasks.add(t);
    }

    public void chooseTask() {
        if (override_ai) return;

        boolean can_choose;
        for (Task t : possible_tasks) {
            can_choose = true;

            for (Condition c : t.choose_conditions) if (!c.satisfied()) can_choose = false;
            for (Condition c : t.interrupt_conditions) if (c.satisfied()) can_choose = false;

            if (can_choose) setTask(t);
        }
    }

    public void setTask(Task task) {
        if (current_task != null) current_task.initialized = false;

        if (!task.initialized) task.init();
        current_task = task;
    }

    public Task getTask() {
        return current_task;
    }

    public void attack() {
    }

    public void update() {
        runAI();
    }

//    protected boolean checkVisibilityOf(PointObject obj) {
//        return (Shape.intersects(search_zone, obj.getBBOX()));
//    }
}
