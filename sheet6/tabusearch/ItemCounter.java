package tabusearch;

public class ItemCounter extends Attribute {

    private int count, value;

    public ItemCounter() {
        count = value = 0;
    }

    public ItemCounter(Solution solution) {
        Instance instance = solution.getInstance();
        int objective = solution.getValue();
        for (int i = 0; i < instance.getSize() && value < objective; i++) {
            count += solution.get(i) * 1;
            value += solution.get(i) * instance.getValue(i);
        }
    }

    public int getCount() {
        return count;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(Attribute other) {
        if (other instanceof ItemCounter)
            return count - ((ItemCounter) other).getCount() + value - ((ItemCounter) other).getValue();
        else
            return -1;
    }
}