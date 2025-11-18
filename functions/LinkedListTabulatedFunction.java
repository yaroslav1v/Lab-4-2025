package functions;

public class LinkedListTabulatedFunction implements TabulatedFunction {

    // Внутренний класс для элемента списка
    private class FunctionNode {
        private FunctionPoint point;
        private FunctionNode prev;
        private FunctionNode next;

        public FunctionNode(FunctionPoint point) {
            this.point = point;
            this.prev = null;
            this.next = null;
        }

        public FunctionNode(FunctionPoint point, FunctionNode prev, FunctionNode next) {
            this.point = point;
            this.prev = prev;
            this.next = next;
        }

        // Геттеры и сеттеры
        public FunctionPoint getPoint() {
            return point;
        }

        public void setPoint(FunctionPoint point) {
            this.point = point;
        }

        public FunctionNode getPrev() {
            return prev;
        }

        public void setPrev(FunctionNode prev) {
            this.prev = prev;
        }

        public FunctionNode getNext() {
            return next;
        }

        public void setNext(FunctionNode next) {
            this.next = next;
        }
    }

    // Поля основного класса
    private FunctionNode head; // голова списка
    private FunctionNode lastAccessedNode; // кэш для быстроты доступа
    private int lastAccessedIndex; // индекс последнего доступного элемента
    private int size; // количество значащих элементов

    // Конструкторы
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница " + leftX + " >= правой границы " + rightX);
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек " + pointsCount + " < 2");
        }

        initializeList();
        double intervalLength = (rightX - leftX) / (pointsCount - 1);

        for (int i = 0; i < pointsCount; i++) {
            addNodeToTail().setPoint(new FunctionPoint(leftX + intervalLength * i, 0));
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница " + leftX + " >= правой границы " + rightX);
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек " + values.length + " < 2");
        }

        initializeList();
        double intervalLength = (rightX - leftX) / (values.length - 1);

        for (int i = 0; i < values.length; i++) {
            addNodeToTail().setPoint(new FunctionPoint(leftX + intervalLength * i, values[i]));
        }
    }

    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        // Проверка количества точек
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек " + points.length + " < 2");
        }

        // Проверка упорядоченности по X
        for (int i = 0; i < points.length - 1; i++) {
            if (points[i].getX() >= points[i + 1].getX()) {
                throw new IllegalArgumentException("Точки не упорядочены по X. Индексы " + i + " и " + (i + 1));
            }
        }

        // Инициализация списка
        initializeList();

        // Добавление точек в список
        for (int i = 0; i < points.length; i++) {
            addNodeToTail().setPoint(new FunctionPoint(points[i]));
        }
    }


    // Инициализация пустого списка с головой
    private void initializeList() {
        head = new FunctionNode(null);
        head.setPrev(head);
        head.setNext(head);
        size = 0;
        lastAccessedNode = head;
        lastAccessedIndex = -1;
    }


    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (size-1) + "]");
        }

        // начинаем поиск с последнего доступного элемента
        FunctionNode current;
        int startIndex;

        if (lastAccessedIndex != -1 && Math.abs(index - lastAccessedIndex) < Math.min(index, size - index - 1)) {
            current = lastAccessedNode;
            startIndex = lastAccessedIndex;
        } else {
            current = head.getNext();
            startIndex = 0;
        }

        // Движение вперед или назад в зависимости от позиции
        if (index > startIndex) {
            for (int i = startIndex; i < index; i++) {
                current = current.getNext();
            }
        } else if (index < startIndex) {
            for (int i = startIndex; i > index; i--) {
                current = current.getPrev();
            }
        }

        // Сохраняем для будущих обращений
        lastAccessedNode = current;
        lastAccessedIndex = index;

        return current;
    }

    // Добавление узла в конец списка
    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode(null, head.getPrev(), head);
        head.getPrev().setNext(newNode);
        head.setPrev(newNode);
        size++;

        // Сбрасываем кэш, т.к. структура изменилась
        lastAccessedNode = head;
        lastAccessedIndex = -1;

        return newNode;
    }

    // Добавление узла по индексу
    private FunctionNode addNodeByIndex(int index) {
        if (index < 0 || index > size) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + size + "]");
        }

        if (index == size) {
            return addNodeToTail();
        }

        FunctionNode nodeAtIndex = getNodeByIndex(index);
        FunctionNode newNode = new FunctionNode(null, nodeAtIndex.getPrev(), nodeAtIndex);

        nodeAtIndex.getPrev().setNext(newNode);
        nodeAtIndex.setPrev(newNode);
        size++;

        // Сбрасываем кэш
        lastAccessedNode = head;
        lastAccessedIndex = -1;

        return newNode;
    }

    // Удаление узла по индексу
    private FunctionNode deleteNodeByIndex(int index) {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " выходит за границы [0, " + (size-1) + "]");
        }
        if (size <= 3) {
            throw new IllegalStateException("Нельзя удалить точку - останется меньше 3 точек. Текущее количество: " + size);
        }

        FunctionNode nodeToDelete = getNodeByIndex(index);

        nodeToDelete.getPrev().setNext(nodeToDelete.getNext());
        nodeToDelete.getNext().setPrev(nodeToDelete.getPrev());
        size--;


        lastAccessedNode = head;
        lastAccessedIndex = -1;

        return nodeToDelete;
    }

    // Методы интерфейса TabulatedFunction
    public double getLeftDomainBorder() {
        if (size == 0) return Double.NaN;
        return head.getNext().getPoint().getX();
    }

    public double getRightDomainBorder() {
        if (size == 0) return Double.NaN;
        return head.getPrev().getPoint().getX();
    }

    public double getFunctionValue(double x) {
        // Поиск интервала, содержащего x
        for (int i = 0; i < size - 1; i++) {
            FunctionNode node1 = getNodeByIndex(i);
            FunctionNode node2 = getNodeByIndex(i + 1);

            if (node1.getPoint().getX() <= x && node2.getPoint().getX() >= x) {

                if (node1.getPoint().getX() == x) {
                    return node1.getPoint().getY();
                }
                if (node2.getPoint().getX() == x) {
                    return node2.getPoint().getY();
                }

                // Линейная интерполяция
                double x1 = node1.getPoint().getX();
                double y1 = node1.getPoint().getY();
                double x2 = node2.getPoint().getX();
                double y2 = node2.getPoint().getY();

                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }

        return Double.NaN;
    }

    public int getPointsCount() {
        return size;
    }

    public FunctionPoint getPoint(int index) {
        FunctionNode node = getNodeByIndex(index);
        return new FunctionPoint(node.getPoint());
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);

        // Проверка упорядоченности
        double newX = point.getX();
        double prevX;
        if (index > 0) {
            prevX = getNodeByIndex(index - 1).getPoint().getX();
        } else {
            prevX = Double.NEGATIVE_INFINITY;
        }

        double nextX;
        if (index < size - 1) {
            nextX = getNodeByIndex(index + 1).getPoint().getX();
        } else {
            nextX = Double.POSITIVE_INFINITY;
        }

        if (newX <= prevX || newX >= nextX) {
            throw new InappropriateFunctionPointException(
                    "X=" + newX + " должен быть строго между " + prevX + " и " + nextX);
        }

        node.setPoint(new FunctionPoint(point));
    }

    public double getPointX(int index) {
        return getNodeByIndex(index).getPoint().getX();
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);

        // Проверка упорядоченности
        double prevX;
        if (index > 0) {
            prevX = getNodeByIndex(index - 1).getPoint().getX();
        } else {
            prevX = Double.NEGATIVE_INFINITY;
        }

        double nextX;
        if (index < size - 1) {
            nextX = getNodeByIndex(index + 1).getPoint().getX();
        } else {
            nextX = Double.POSITIVE_INFINITY;
        }

        if (x <= prevX || x >= nextX) {
            throw new InappropriateFunctionPointException(
                    "X=" + x + " должен быть строго между " + prevX + " и " + nextX);
        }

        node.setPoint(new FunctionPoint(x, node.getPoint().getY()));
    }

    public double getPointY(int index) {
        return getNodeByIndex(index).getPoint().getY();
    }

    public void setPointY(int index, double y) {
        FunctionNode node = getNodeByIndex(index);
        node.setPoint(new FunctionPoint(node.getPoint().getX(), y));
    }

    public void deletePoint(int index) {
        deleteNodeByIndex(index);
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // Проверка на дублирование X
        for (int i = 0; i < size; i++) {
            if (TabulatedFunction.compareDouble(getPointX(i), point.getX())) {
                throw new InappropriateFunctionPointException("Точка с X=" + point.getX() + " уже существует");
            }
        }

        // Поиск позиции для вставки
        int insertIndex = size;
        for (int i = 0; i < size; i++) {
            if (point.getX() < getPointX(i)) {
                insertIndex = i;
                break;
            }
        }

        FunctionNode newNode = addNodeByIndex(insertIndex);
        newNode.setPoint(new FunctionPoint(point));
    }
}
