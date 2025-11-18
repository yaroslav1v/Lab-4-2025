package functions;

public class FunctionPoint {
    private double x;
    private double y;

    // Конструктор с заданными координатами
    public FunctionPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Конструктор копирования
    public FunctionPoint(FunctionPoint point) {
        this.x = point.x;
        this.y = point.y;
    }

    // Конструктор по умолчанию
    public FunctionPoint() {
        this(0, 0);
    }
    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
}
