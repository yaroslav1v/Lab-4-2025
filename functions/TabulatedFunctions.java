package functions;

import java.io.*;
import java.util.*;

public final class TabulatedFunctions {

    // Приватный конструктор для запрета создания объектов
    private TabulatedFunctions() {
    }

    // Табулирует функцию на заданном отрезке с заданным количеством точек
    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException(
                    "Отрезок табулирования [" + leftX + ", " + rightX + "] " +
                            "выходит за область определения функции [" +
                            function.getLeftDomainBorder() + ", " + function.getRightDomainBorder() + "]"
            );
        }

        // Проверка корректности количества точек
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }

        // Создание массива значений функции
        double[] values = new double[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);

        // Заполнение массива значений
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            values[i] = function.getFunctionValue(x);
        }

        // Возврат табулированной функции (убрал лишние {})
        return new ArrayTabulatedFunction(leftX, rightX, values);
    }

    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) throws IOException {
        PrintWriter writer = new PrintWriter(out);
        writer.println(function.getPointsCount());
        for (int i = 0; i < function.getPointsCount(); i++) {
            writer.println(function.getPointX(i) + " " + function.getPointY(i));
        }
        writer.flush();
    }

    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(in);

        tokenizer.nextToken(); // читаем количество точек
        int pointsCount = (int)tokenizer.nval;

        FunctionPoint[] points = new FunctionPoint[pointsCount];

        for (int i = 0; i < pointsCount; i++) {
            tokenizer.nextToken(); // читаем X
            double x = tokenizer.nval;
            tokenizer.nextToken(); // читаем Y
            double y = tokenizer.nval;
            points[i] = new FunctionPoint(x, y);
        }

        return new ArrayTabulatedFunction(points);
    }

    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeInt(function.getPointsCount());
        for (int i = 0; i < function.getPointsCount(); i++) {
            dos.writeDouble(function.getPointX(i));
            dos.writeDouble(function.getPointY(i));
        }
        dos.flush();
    }

    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in);
        int pointsCount = dis.readInt();
        FunctionPoint[] points = new FunctionPoint[pointsCount];

        for (int i = 0; i < pointsCount; i++) {
            double x = dis.readDouble();
            double y = dis.readDouble();
            points[i] = new FunctionPoint(x, y);
        }

        return new ArrayTabulatedFunction(points);
    }
}