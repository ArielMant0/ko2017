package tabusearch;

public class Move extends Attribute {

    private int index;
    private int value;

    public Move() {
        this(0, 0);
    }

    public Move(int index, int value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(Attribute other) {
        if (other instanceof Move)
            return index - ((Move) other).getIndex() + value - ((Move) other).getValue();
        else
            return -1;
    }
}