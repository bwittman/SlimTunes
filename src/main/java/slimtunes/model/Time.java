package slimtunes.model;
public class Time implements Comparable<Time> {
    private int milliseconds;

    public Time(int milliseconds) {
        this.milliseconds = milliseconds;
    }

    public String toString() {
        int seconds = milliseconds / 1000;
        int hours = seconds / 3600;
        int minutes = seconds % 3600 / 60;
        seconds = seconds % 60;
        if (hours > 0)
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public int compareTo(Time other) {
        return milliseconds - other.milliseconds;
    }
}