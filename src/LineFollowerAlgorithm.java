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
//
        if (sensorsOverLine > 1) {
            //int mid = sensorCount/2;
            //int point = detectedSensorsOverLine.get(0);//getMidpoint(sensorsOverLine, detectedSensorsOverLine);
            double sum = 0;

            for (int i = 0; i < Math.min(sensorsOverLine, (sensorCount*2-1)); i++) {
                int point = detectedSensorsOverLine.get(i);
                sum += point;
            }

            //System.out.printf("%nSENSOR OVER LINE COUNT: %s, SENSORS: %s, POINT: %s%n", sensorsOverLine, detectedSensorsOverLine, sum/2.0);
            int divisor = 100/(sensorCount-1);
            return (double) Math.min(((sum/2.0) * divisor)/100, 1);
        } else if (sensorsOverLine == 1) {
            int divisor = 100/(sensorCount-1);
            return (double) Math.min((detectedSensorsOverLine.get(0) * divisor)/100, 1);
        }

        return -1.0;
    }

    public double GetLinePositionSplitInversion(double position) {
        return (-position*2+1)*-1;
    }

    public double GetAngle(double percentage, double minAngle, double maxAngle, double rate, double straightOffset) {
        if (percentage == 0) { return straightOffset; }
        double angle = Math.min(Math.abs(((minAngle / maxAngle) + Math.pow(percentage, 2))*rate), maxAngle);
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

    public double GetSpeed(double percentage, double rate) {
        return Math.abs(1+(Math.pow(Math.abs(percentage), 4)/rate)*-1)*100;
    }
}
