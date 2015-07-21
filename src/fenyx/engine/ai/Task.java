package fenyx.engine.ai;

import java.util.HashSet;

/**
 *
 * @author KiQDominaN
 */
public abstract class Task {

    public boolean initialized = false;
    public HashSet<Condition> choose_conditions = new HashSet<>();
    public HashSet<Condition> done_conditions = new HashSet<>();
    public HashSet<Condition> interrupt_conditions = new HashSet<>();
    private long next_update;
    private long last_update = System.currentTimeMillis();

    public abstract void init();

    public void update() {
    }

    public boolean done() {
        boolean done = true;

        for (Condition c : done_conditions) if (!c.satisfied()) done = false;
        for (Condition c : interrupt_conditions) if (c.satisfied()) done = true;

        return done;
    }

    public void addCondition(Condition c) {
        if (interrupt_conditions.contains(c)) interrupt_conditions.remove(c);

        choose_conditions.add(c);
        interrupt_conditions.add(c.invert()); //Prevent bad logic
    }

    public void addDoneCondition(Condition c) {
        done_conditions.add(c);
    }

    public void addInterrupt(Condition c) {
        if (choose_conditions.contains(c)) choose_conditions.remove(c);
        interrupt_conditions.add(c);
    }

    public void addInterrupt(Task t) {
        for (Condition c : t.choose_conditions) addInterrupt(c);
    }

    public void setNextUpdate(float time) {
        next_update = (long) (time * 1000);
    }

    public boolean canUpdate() {
        if (System.currentTimeMillis() - last_update >= next_update) {
            last_update = System.currentTimeMillis();

            return true;
        }

        return false;
    }

}
