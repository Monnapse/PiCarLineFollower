import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class LineFollowerAlgorithm {
    int MidPoint;

    public LineFollowerAlgorithm (int midPoint) {
        this.MidPoint = midPoint;
    }
    private boolean IsLine(int sensorNumber) {
        //System.out.println(sensorNumber);
        return sensorNumber < MidPoint;
    }
    public double GetLinePosition(int[] sensorData) {
        //System.out.println(Arrays.toString(sensorData));
        int sensorCount = sensorData.length;
        ArrayList<Integer> detectedSensorsOverLine = new ArrayList<>();
        for (int i = 0; i < sensorCount; i++) {
            //System.out.println(sensorData[i]);
            boolean isOverLine = this.IsLine(sensorData[i]);
            if (isOverLine) {
                //int divisor = 100/(sensorCount-1);
                //return (double) (i * divisor)/100;
                detectedSensorsOverLine.add(i);
            }
        }

        int sensorsOverLine = detectedSensorsOverLine.size();
        double divisor = 100.0/(sensorCount-1);

        if (sensorsOverLine > 1) {
            //int mid = sensorCount/2;
            //int point = detectedSensorsOverLine.get(0);//getMidpoint(sensorsOverLine, detectedSensorsOverLine);
            double sum = 0;

            for (int i = 0; i < 2; i++) {
                int point = detectedSensorsOverLine.get(i);
                //System.out.println(point);
                sum += point;
            }

            //System.out.printf("%nSENSOR OVER LINE COUNT: %s, SENSORS: %s, POINT: %s%n", sensorsOverLine, detectedSensorsOverLine, sum/2.0);

            return (double) Math.min(((sum/2.0) * divisor)/100.0, 1);
        } else if (sensorsOverLine == 1) {
            //System.out.printf("%nSENSOR OVER LINE COUNT: %s, SENSOR: %s, SENSOR VALUE: %s%n", sensorsOverLine, detectedSensorsOverLine, detectedSensorsOverLine.get(0));
            return (double) Math.min((detectedSensorsOverLine.get(0) * divisor)/100.0, 1);
        }

        return -1.0;
    }

    public double GetLinePositionSplitInversion(double position) {
        return (-position*2+1)*-1;
    }

    public double GetAngleV1(double percentage, double m1, double m2, double maxAngle, double rate, double straightOffset) {
        if (percentage == 0) { return straightOffset; }
        double angle = Math.min(Math.abs(((m1 / m2) + Math.pow(percentage, 2))*rate), maxAngle);
        if (percentage > 0 && straightOffset > 0) {
            angle += straightOffset;
        } else if (percentage < 0) {
            angle *= -1;
            if (straightOffset < 0) {
                angle += straightOffset;
            }
        }
        return angle;
    }
    public double GetAngleV2(double percentage, double rate, double maxAngle) {
        double x = Math.abs(percentage);
        if (x == 0) { return 0; }
        //double angle = Math.min(Math.sqrt(1-Math.pow(x - 1, 2) * rate), 1);
        double angle = Math.min(1-(1-x)*(1-x)*rate, 1);
        if (percentage < 0) {
            angle *= -1;
        }
        return angle * maxAngle;
    }

    public double GetSpeed(double percentage, double rate) {
        return Math.abs(1+(Math.pow(Math.abs(percentage), 4)/rate)*-1)*100;
    }
}
