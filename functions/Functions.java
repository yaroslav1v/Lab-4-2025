package functions;

import functions.meta.*;

public final class Functions {

    private Functions() {}

    // Возвращает функцию, полученную из исходной сдвигом вдоль осей
    public static Function shift(Function f, double shiftX, double shiftY) {
        return new Shift(f, shiftX, shiftY);
    }

    // Возвращает функцию, полученную из исходной масштабированием вдоль осей
    public static Function scale(Function f, double scaleX, double scaleY) {
        return new Scale(f, scaleX, scaleY);
    }

    // Возвращает функцию, являющуюся заданной степенью исходной
    public static Function power(Function f, double power) {
        return new Power(f, power);
    }

    // Возвращает функцию, являющуюся суммой двух исходных функций
    public static Function sum(Function f1, Function f2) {
        return new Sum(f1, f2);
    }

    // Возвращает функцию, являющуюся произведением двух исходных функций
    public static Function mult(Function f1, Function f2) {
        return new Mult(f1, f2);
    }

    // Возвращает функцию, являющуюся композицией двух исходных функций
    public static Function composition(Function f1, Function f2) {
        return new Composition(f1, f2);
    }
}