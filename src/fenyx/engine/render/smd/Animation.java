package fenyx.engine.render.smd;

import java.util.ArrayList;

/**
 *
 * @author KiQDominaN
 */
public class Animation {

    public String name;
    public ArrayList<Frame> frames;
    private float fps = 10f;
    private long last_time;
    private int current_frame = 0;

    public Animation() {
        frames = new ArrayList<>();
    }

    public void setFps(float fps) {
        this.fps = fps;
    }

    public Frame getFirstFrame() {
        return frames.get(0);
    }

    public Frame getPrevFrame() {
        if (current_frame == 0)
            return frames.get(frames.size() - 1);
        else
            return frames.get(current_frame - 1);
    }

    public Frame getCurrentFrame() {
        return checkSequence();
    }

    public Frame getNextFrame() {
        if (current_frame == frames.size())
            return frames.get(0);
        else
            return frames.get(current_frame + 1);
    }

    public void setSequencePos(int pos) {
        current_frame = pos;
    }

    public int getSequencePos() {
        return current_frame;
    }

    public void end() {
        setSequencePos(frames.size() - 1);
    }

    public boolean ended() {
        return current_frame == frames.size() - 1;
    }

    private Frame checkSequence() {
        if (fps <= 0)
            return frames.get(current_frame);

        long current_time = System.currentTimeMillis();
        if (current_time - last_time > 1000f / fps) {
            current_frame++;
            if (current_frame >= frames.size()) current_frame = 0;

            last_time = current_time;
        }

        return frames.get(current_frame);
    }

    public void resetSequence() {
        current_frame = 0;
    }

}
