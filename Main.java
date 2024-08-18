import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out))) {
            int pointsNumber = Integer.parseInt(reader.readLine());
            String[] xString = reader.readLine().split(" ");
            String[] yString = reader.readLine().split(" ");
            List<Point> points = new ArrayList<>();
            double x_temp, y_temp;
            for (int i = 0; i < pointsNumber; i++) {
                x_temp = Double.parseDouble(xString[i]);
                y_temp = Double.parseDouble(yString[i]);
                points.add(new Point(x_temp, y_temp));
            }
            RangeTree_2D rangeTree = new RangeTree_2D(points);
            points = null;
            List<Point> answer;
            String[] number;
            String resultX = "", resultY = "";
            int numberOfLine = Integer.parseInt(reader.readLine());
            for (int i = 0; i < numberOfLine; i++) {
                number = reader.readLine().split(" ");
                answer = rangeTree.searchForRec(Double.parseDouble(number[0]), Double.parseDouble(number[1])
                        , Double.parseDouble(number[2]), Double.parseDouble(number[3]));
                if (answer.size() == 0) {
                    writer.write("None" + "\n");
                    writer.flush();
                    continue;
                }
                resultX = "";
                resultY = "";
                for (int j = 0; j < answer.size(); j++) {
                    if (answer.get(j).X == (int) answer.get(j).X) {
                        resultX += (int) answer.get(j).X + " ";
                    } else {
                        resultX += answer.get(j).X + " ";
                    }
                    if (answer.get(j).Y == (int) answer.get(j).Y) {
                        resultY += (int) answer.get(j).Y + " ";
                    } else {
                        resultY += answer.get(j).Y + " ";
                    }
                }
                writer.write(resultX + "\n" + resultY + "\n");
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Point {
    double X;
    double Y;

    public Point(double x, double y) {
        this.X = x;
        this.Y = y;
    }


    public boolean isBetween(double x1, double y1, double x2, double y2) {
        if (this.X <= x2 && this.X >= x1 && this.Y <= y2 && this.Y >= y1)
            return true;
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return X == point.X &&
                Y == point.Y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(X, Y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "X=" + X +
                ", Y=" + Y +
                '}';
    }


}


class RangeTree_2D {
    Node root;


    public RangeTree_2D(List<Point> points) {
        points.sort(new Comparator<Point>() {
            @Override
            public int compare(Point point, Point t1) {
                return Double.compare(point.X, t1.X);
            }
        });
        this.root = makeTree(points, null);
    }

    private Node makeTree(List<Point> list, Node parent) {
        int mid = list.size() / 2;
        if (list.size() == 1) {
            return new Node(parent, list.get(0));
        }
        Node node;
        if (list.size() % 2 == 0) {
            node = new Node(parent, list.get(mid - 1).X);
            node.left_c = makeTree(list.subList(0, mid), node);
            node.right_c = makeTree(list.subList(mid, list.size()), node);
        } else {
            node = new Node(parent, list.get(mid).X);
            node.left_c = makeTree(list.subList(0, mid + 1), node);
            node.right_c = makeTree(list.subList(mid + 1, list.size()), node);
        }
        list.sort(new Comparator<Point>() {
            @Override
            public int compare(Point point, Point t1) {
                return Double.compare(point.Y, t1.Y);
            }
        });
        node.y_root = yTree(list, null);
        return node;
    }

    private Node yTree(List<Point> list, Node parent) {
        int mid = list.size() / 2;
        if (list.size() == 1) {
            return new Node(parent, list.get(0));
        }
        Node node;
        if (list.size() % 2 == 0) {
            node = new Node(parent, list.get(mid - 1).Y);
            node.left_c = yTree(list.subList(0, mid), node);
            node.right_c = yTree(list.subList(mid, list.size()), node);
        } else {
            node = new Node(parent, list.get(mid).Y);
            node.left_c = yTree(list.subList(0, mid + 1), node);
            node.right_c = yTree(list.subList(mid + 1, list.size()), node);
        }
        return node;
    }


    public List<Point> searchForRec(double x1, double y1, double x2, double y2) {
        List<Point> points = new ArrayList<>();
        xSearch(points, root, x1, y1, x2, y2);
        points.sort(new Comparator<Point>() {
            @Override
            public int compare(Point point, Point t1) {
                return Double.compare(point.Y, t1.Y);
            }
        });
        return points;
    }


    private void xSearch(List<Point> points, Node node, double x1, double y1, double x2, double y2) {
        while (!node.isLeave && (node.value >= x2 || node.value < x1)) {
            if (node.value >= x2)
                node = node.left_c;
            else
                node = node.right_c;
        }
        if (node.isLeave) {
            if (node.point.isBetween(x1, y1, x2, y2))
                points.add(node.point);
        } else {
            Node node1 = node.left_c;
            while (!node1.isLeave) {
                if (node1.value >= x1) {
                    if (node1.right_c.y_root != null) {
                        ySearch(points, node1.right_c.y_root, x1, y1, x2, y2);
                    } else {
                        if (node1.right_c.point.isBetween(x1, y1, x2, y2))
                            points.add(node1.right_c.point);
                    }
                    node1 = node1.left_c;
                } else {
                    node1 = node1.right_c;
                }
            }
            if (node1.point.isBetween(x1, y1, x2, y2))
                points.add(node1.point);
            node1 = node.right_c;
            while (!node1.isLeave) {
                if (node1.value <= x2) {
                    if (node1.left_c.y_root != null) {
                        ySearch(points, node1.left_c.y_root, x1, y1, x2, y2);
                    } else {
                        if (node1.left_c.point.isBetween(x1, y1, x2, y2))
                            points.add(node1.left_c.point);
                    }
                    node1 = node1.right_c;
                } else {
                    node1 = node1.left_c;
                }
            }
            if (node1.isLeave) {
                if (node1.point.isBetween(x1, y1, x2, y2))
                    points.add(node1.point);
            }
        }
    }


    private List<Point> AllLeaves(Node node) {
        List<Point> points = new ArrayList<>();
        if (node.isLeave) {
            points.add(node.point);
            return points;
        }
        points.addAll(AllLeaves(node.right_c));
        points.addAll(AllLeaves(node.left_c));
        return points;
    }


    private void ySearch(List<Point> points, Node node, double x1, double y1, double x2, double y2) {
        while (!node.isLeave && (node.value >= y2 || node.value < y1)) {
            if (node.value >= y2)
                node = node.left_c;
            else
                node = node.right_c;
        }
        if (node.isLeave) {
            if (node.point.isBetween(x1, y1, x2, y2))
                points.add(node.point);
        } else {
            Node node1 = node.left_c;
            while (!node1.isLeave) {
                if (node1.value >= y1) {
                    points.addAll(AllLeaves(node1.right_c));
                    node1 = node1.left_c;
                } else {
                    node1 = node1.right_c;
                }
            }
                if (node1.point.isBetween(x1, y1, x2, y2))
                    points.add(node1.point);
            node1 = node.right_c;
            while (!node1.isLeave) {
                if (node1.value <= y2) {
                    points.addAll(AllLeaves(node1.left_c));
                    node1 = node1.right_c;
                } else {
                    node1 = node1.left_c;
                }
            }
                if (node1.point.isBetween(x1, y1, x2, y2))
                    points.add(node1.point);
        }
    }

    private String print(Node node) {
        if (node == null)
            return "";
        return node.toString() + "\n" + "&&" + print(node.left_c) + "^^" + print(node.right_c);
    }

    @Override
    public String toString() {
        return "RangeTree_2D{" +
                print(root) +
                '}';
    }
}


class Node {
    Point point;
    Node parent;
    Node right_c;
    Node left_c;
    Node y_root;
    double value;
    boolean isLeave;


    public Node(Node parent, double value) {
        this.value = value;
        this.parent = parent;
        this.isLeave = false;
    }


    public Node(Node parent, Point point) {
        this.isLeave = true;
        this.point = point;
        this.parent = parent;
    }

    @Override
    public String toString() {
        String s;
        if (point != null) {
            s = "Node{" +
                    "point=" + point.toString() +
                    ", value=" + value +
                    '}';
        } else {
            s = "Node{" +
                    ", value=" + value +
                    '}';
        }
        return s;
    }
}


/**
 * a new data structure which is very similar to RangeTree but it has a better
 * time complexity & it also gets 100% in quera (even before adding memory Limit & time limit)
 * & it uses much less memory (in order n)
 */



/**
 import java.io.*;
 import java.util.*;

 public class Main {

 public static void main(String[] args) {
 try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out))) {
 int pointsNumber = Integer.parseInt(reader.readLine());
 String[] xString = reader.readLine().split(" ");
 String[] yString = reader.readLine().split(" ");
 Point[] points =new Point[pointsNumber];
 double x_temp, y_temp;
 for (int i = 0; i < pointsNumber; i++) {
 x_temp = Double.parseDouble(xString[i]);
 y_temp = Double.parseDouble(yString[i]);
 points[i] =new Point(x_temp , y_temp);
 }
 RangeTree_2D rangeTree = new RangeTree_2D(points);
 for (int i = 0; i < points.length; i++) {
 rangeTree.addY(points[i]);
 }
 points=null;
 rangeTree.set1D_tree();
 ArrayList<Point> answer;
 String[] number;
 String resultX = "", resultY = "";
 int numberOfLine = Integer.parseInt(reader.readLine());
 for (int i = 0; i < numberOfLine; i++) {
 number = reader.readLine().split(" ");
 answer = rangeTree.searchForRec(Double.parseDouble(number[0]), Double.parseDouble(number[1])
 , Double.parseDouble(number[2]), Double.parseDouble(number[3]));
 if (answer.size() == 0) {
 writer.write("None" + "\n");
 writer.flush();
 continue;
 }
 resultX = "";
 resultY = "";
 for (int j = 0; j < answer.size(); j++) {
 if (answer.get(j).X == (int) answer.get(j).X) {
 resultX += (int) answer.get(j).X + " ";
 } else {
 resultX += answer.get(j).X + " ";
 }
 if (answer.get(j).Y == (int) answer.get(j).Y) {
 resultY += (int) answer.get(j).Y + " ";
 } else {
 resultY += answer.get(j).Y + " ";
 }
 }
 writer.write(resultX + "\n" + resultY + "\n");
 writer.flush();
 }
 } catch (Exception e) {
 e.printStackTrace();
 }
 }
 }

 class Point {
 double X;
 double Y;

 public Point(double x, double y) {
 this.X = x;
 this.Y = y;
 }

 @Override
 public boolean equals(Object o) {
 if (this == o) return true;
 if (o == null || getClass() != o.getClass()) return false;
 Point point = (Point) o;
 return X == point.X &&
 Y == point.Y;
 }

 @Override
 public int hashCode() {
 return Objects.hash(X, Y);
 }

 @Override
 public String toString() {
 return "Point{" +
 "X=" + X +
 ", Y=" + Y +
 '}';
 }
 }


 class RangeTree_2D {
 Node root;

 public RangeTree_2D(Point[] points) {
 Arrays.sort(points ,new Comparator<Point>() {
 @Override
 public int compare(Point point, Point t1) {
 if (point.X < t1.X)
 return -1;
 return 1;
 }
 });
 this.setRoot(points);
 }

 private void setRoot(Point[] points) {
 this.root = makeTheTree(points, null, 0, points.length - 1);
 }

 private Node makeTheTree(Point[] points, Node parent, int first, int last) {
 if (first > last)
 return null;
 Node node = new Node(points[(first + last) / 2].X);
 node.parent = parent;
 node.left_c = makeTheTree(points, node, first, ((first + last) / 2) - 1);
 node.right_c = makeTheTree(points, node, ((first + last) / 2) + 1, last);
 return node;
 }

 private void setYTreeForAll(Node node) {
 node.rangeTree_1D = new RangeTree_1D(node.y);
 if (node.left_c != null)
 setYTreeForAll(node.left_c);
 if (node.right_c != null)
 setYTreeForAll(node.right_c);
 }

 public void set1D_tree() {
 setYTreeForAll(this.root);
 }

 public void addY(Point point) {
 Node node = this.root;
 while (node.value != point.X) {

 if (node.value > point.X)
 node = node.left_c;
 else
 node = node.right_c;
 }
 node.y.add(point.Y);
 }



 public ArrayList<Point> searchForRec(double x1, double y1 , double x2 , double y2){
 return getX(this.root , x1 , y1 , x2 , y2);
 }

 private ArrayList<Point> getX(Node node , double x1 , double y1 , double x2 , double y2 ){
 ArrayList<Point> points =new ArrayList<>();
 if (node == null)
 return points;
 if (node.value > x2)
 return getX(node.left_c , x1 , y1 , x2 , y2);
 if (node.value < x1)
 return getX(node.right_c , x1 , y1 , x2 , y2);
 points.addAll(node.rangeTree_1D.getY(node.value , y1 , y2));
 points.addAll(getX(node.right_c , x1 , y1 , x2 , y2));
 points.addAll(getX(node.left_c , x1 , y1 , x2 , y2));
 points.sort(new Comparator<Point>() {
 @Override
 public int compare(Point point, Point t1) {
 if (point.Y < t1.Y)
 return -1;
 return 1;
 }
 });
 return points;
 }


 private class Node {
 Node parent;
 Node right_c;
 Node left_c;
 RangeTree_1D rangeTree_1D;
 ArrayList<Double> y;
 double value;

 public Node(double x) {
 this.value = x;
 y = new ArrayList<>();
 }

 @Override
 public String toString() {
 return "Node2D{" +
 " value=" + value + ", 1D_RangeTree :^^^^^ " +
 rangeTree_1D.toString() +
 "^^^^}" + "\n";
 }
 }

 @Override
 public String toString() {
 return print(root, 0);
 }

 private String print(Node node, int level) {
 String s = node.toString();
 if (node.left_c != null)
 s += print(node.left_c, level + 1);
 if (node.right_c != null)
 s += print(node.right_c, level + 1);
 return s;
 }
 }


 class RangeTree_1D {
 private Node root;

 public RangeTree_1D(ArrayList<Double> number) {
 number.sort(Double::compareTo);
 this.setRoot(number);
 }

 private void setRoot(ArrayList<Double> number) {
 this.root = makeTheTree(number, null, 0, number.size() - 1);
 }

 private Node makeTheTree(ArrayList<Double> number, Node parent, int first, int last) {
 if (first > last)
 return null;
 Node node = new Node(number.get((first + last) / 2));
 node.parent = parent;
 node.left_C = makeTheTree(number, node, first, ((first + last) / 2) - 1);
 node.right_C = makeTheTree(number, node, ((first + last) / 2) + 1, last);
 return node;
 }

 private ArrayList<Point> searchForY(Node node,double x, double y1, double y2){
 ArrayList<Point> points =new ArrayList<>();
 if (node==null)
 return points;
 if (node.value > y2){
 return searchForY(node.left_C,x,y1,y2);
 }
 if (node.value < y1){
 return searchForY(node.right_C,x,y1,y2);
 }
 points.add(new Point(x , node.value));
 points.addAll(searchForY(node.left_C , x, y1 , y2));
 points.addAll(searchForY(node.right_C,x,y1,y2));
 return points;
 }

 public ArrayList<Point> getY(double x ,double y1 ,double y2){
 return searchForY(this.root , x , y1 , y2);
 }


 @Override
 public String toString() {
 return print(root, 0);
 }

 private String print(Node node, int level) {
 String s = node.toString() + level;
 if (node.left_C != null)
 s += print(node.left_C, level + 1);
 if (node.right_C != null)
 s += print(node.right_C, level + 1);
 return s;
 }

 private class Node {
 double value;

 Node parent;
 Node left_C;
 Node right_C;

 public Node(double t) {
 this.value = t;
 }

 public boolean isRightChild() {
 if (parent == null)
 return false;
 if (parent.right_C.equals(this))
 return true;
 return false;
 }

 public boolean isLeftChild() {
 if (parent == null)
 return false;
 if (parent.left_C.equals(this))
 return true;
 return false;
 }

 @Override
 public String toString() {
 return "Node1D{" +
 "value=" + value +
 '}';
 }
 }
 }
 */