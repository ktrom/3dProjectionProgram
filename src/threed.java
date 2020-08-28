import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

public class threed {
    public static void main(String[] args){
        Vector viewpoint = new Vector<Double>();
        Vector viewplane = new Vector<Double>();
        fillVectors(viewpoint, viewplane);
        int input = 0;
        ArrayList<double[]> points = new ArrayList<double[]>();
        Scanner scan = new Scanner(System.in);
        while(input != -1){
            double[] point = new double[4];
            System.out.println("Enter point x");
            point[0] = scan.nextDouble();
            System.out.println("Enter point y");
            point[1] = scan.nextDouble();
            System.out.println("Enter point z");
            point[2] = scan.nextDouble();
            point[3] = 1;
            points.add(point);
            System.out.println("Enter 1 to enter another point or -1 to skip");
            input = scan.nextInt();
        }
        scan.close();
        projectionMatrixFinder matrixFinder = new projectionMatrixFinder(viewpoint, viewplane);
        matrixFinder.calculateMatrix();
        double[][] projectionMatrix = matrixFinder.getProjectionMatrix();
        System.out.println();
        System.out.println("Projection Matrix:");
        for(int i =0; i< 4; i++)
        {
            for(int j = 0; j < 4; j++){
                System.out.print(projectionMatrix[i][j]+ " ");
            }
            System.out.println();
        }
        double[][] transformedPoints = new double[3][points.size()];
        for(int i = 0; i < points.size(); i++){
              double[] transformedPoint = matrixFinder.projectedPoint(points.get(i));
              transformedPoints[0][i] = transformedPoint[0];
              transformedPoints[1][i] = transformedPoint[1];
              transformedPoints[2][i] = transformedPoint[2];
        }

        System.out.println();
        System.out.println("Resultant Points Matrix:");
        for(int j = 0; j < transformedPoints.length; j++){
        for(int i = 0; i < transformedPoints[0].length; i++){
                System.out.print(transformedPoints[j][i] + " ");
            }
            System.out.println();
        }
    }

    public static void fillVectors(Vector viewpoint, Vector viewplane){
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter viewpoint x");
        viewpoint.add(scan.nextDouble());
        System.out.println("Enter viewpoint y");
        viewpoint.add(scan.nextDouble());
        System.out.println("Enter viewpoint z");
        viewpoint.add(scan.nextDouble());
        System.out.println("Enter viewplane x");
        viewplane.add(scan.nextDouble());
        System.out.println("Enter viewplane y");
        viewplane.add(scan.nextDouble());
        System.out.println("Enter viewplane z");
        viewplane.add(scan.nextDouble());
        System.out.println("Enter viewplane w");
        viewplane.add(scan.nextDouble());
    }
}

class projectionMatrixFinder{
    private Vector<Double> viewpoint;
    private Vector<Double> viewplane;
    private double[][] projectionMatrix;
    public projectionMatrixFinder(Vector<Double> viewpoint, Vector<Double> viewplane){
        this.viewpoint = viewpoint;
        viewpoint.add(1.0);
        this.viewplane = viewplane;
        projectionMatrix = new double[4][4];
    }

    public void calculateMatrix(){
        double[][] identityMatrix = new double[4][4];
        double[] viewpointDouble = {viewpoint.get(0), viewpoint.get(1), viewpoint.get(2), viewpoint.get(3)};
        double[] viewplaneDouble = {viewplane.get(0), viewplane.get(1), viewplane.get(2), viewplane.get(3)};
        identityMatrix[0][0] = identityMatrix[1][1] = identityMatrix[2][2] = identityMatrix[3][3] = 1;

        double[][] leftOperation = new double[4][4];
        for(int i = 0; i < 16; i++)
        {
            int column = i/4;
            int row = i%4;
                leftOperation[column][row] = viewpointDouble[column]*viewplaneDouble[row];
        }

        double rightOperationScalar = vectorOperations.dotprod(viewpointDouble,viewplaneDouble);
        for (int i = 0; i <4; i++) {
            for(int j = 0; j< 4; j++) {
                identityMatrix[i][j] *= rightOperationScalar;
            }
        }

        projectionMatrix= vectorOperations.matrixSubtract(leftOperation, identityMatrix);
    }

    public double[][] getProjectionMatrix(){
        return projectionMatrix;
    }

    public double[] projectedPoint(double[] point){
        double[] result = new double[3];
        double normalizer = projectionMatrix[3][0]*point[0] + projectionMatrix[3][1]*point[1] + projectionMatrix[3][2]*point[2] + projectionMatrix[3][3]*point[3];

        result[0] = (projectionMatrix[0][0]*point[0] + projectionMatrix[0][1]*point[1] + projectionMatrix[0][2]*point[2] + projectionMatrix[0][3]*point[3])/normalizer;
        result[1] = (projectionMatrix[1][0]*point[0] + projectionMatrix[1][1]*point[1] + projectionMatrix[1][2]*point[2] + projectionMatrix[1][3]*point[3])/normalizer;
        result[2] = (projectionMatrix[2][0]*point[0] + projectionMatrix[2][1]*point[1] + projectionMatrix[2][2]*point[2] + projectionMatrix[2][3]*point[3])/normalizer;

        return result;
    }
}

class vectorOperations{

    public static double[][] matrixSubtract(double[][] m1, double[][] m2){
        double[][] m3 = new double[4][4];
        for(int i=0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                m3[i][j] = m1[i][j] - m2[i][j];
            }
        }
        return m3;
    }
    public static double[] cross(double v1[], double v2[]) {
        double v[] = new double[3];
        v[0] = v1[1] * v2[2] - v1[2] * v2[1];
        v[1] = v1[2] * v2[0] - v1[0] * v2[2];
        v[2] = v1[0] * v2[1] - v1[1] * v2[0];
        return v;
    }

    public static double dotprod(double[] v1, double[] v2) {
        double result = 0.0;
        int size = Math.min(v1.length, v2.length);
        int n = 0;
        for (int i = 0; i < size; i++) {
            result += v1[i] * v2[i];
        }
        return result;
    }

    public static double[] matrix4x4f_Mult(double[] a, double[] b) {
        double[] result = new double[16];
        result[0] = a[0] * b[0] + a[1] * b[4] + a[2] * b[8] + a[3] * b[12];
        result[1] = a[0] * b[1] + a[1] * b[5] + a[2] * b[9] + a[3] * b[13];
        result[2] = a[0] * b[2] + a[1] * b[6] + a[2] * b[10] + a[3] * b[14];
        result[3] = a[0] * b[3] + a[1] * b[7] + a[2] * b[11] + a[3] * b[15];

        result[4] = a[4] * b[0] + a[5] * b[4] + a[6] * b[8] + a[7] * b[12];
        result[5] = a[4] * b[1] + a[5] * b[5] + a[6] * b[9] + a[7] * b[13];
        result[6] = a[4] * b[2] + a[5] * b[6] + a[6] * b[10] + a[7] * b[14];
        result[7] = a[4] * b[3] + a[5] * b[7] + a[6] * b[11] + a[7] * b[15];

        result[8] = a[8] * b[0] + a[9] * b[4] + a[10] * b[8] + a[11] * b[12];
        result[9] = a[8] * b[1] + a[9] * b[5] + a[10] * b[9] + a[11] * b[13];
        result[10] = a[8] * b[2] + a[9] * b[6] + a[10] * b[10] + a[11] * b[14];
        result[11] = a[8] * b[3] + a[9] * b[7] + a[10] * b[11] + a[11] * b[15];

        result[12] = a[12] * b[0] + a[13] * b[4] + a[14] * b[8] + a[15] * b[12];
        result[13] = a[12] * b[1] + a[13] * b[5] + a[14] * b[9] + a[15] * b[13];
        result[14] = a[12] * b[2] + a[13] * b[6] + a[14] * b[10] + a[15] * b[14];
        result[15] = a[12] * b[3] + a[13] * b[7] + a[14] * b[11] + a[15] * b[15];
        return result;
    }
}
