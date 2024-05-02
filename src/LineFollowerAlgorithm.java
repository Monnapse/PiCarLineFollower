import java.sql.Array;
import java.util.ArrayList;

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
        int sensorCount = sensorData.length;
        //ArrayList<Integer> detectedSensorsOverLine = new ArrayList<>();
        for (int i = 0; i < sensorCount; i++) {
            //System.out.println(sensorData[i]);
            boolean isOverLine = this.IsLine(sensorData[i]);
            if (isOverLine) {
                int divisor = 100/(sensorCount-1);
                return (double) (i * divisor)/100;
            }
        }

        //int sensorsOverLine = detectedSensorsOverLine.size();
//
        //if (sensorsOverLine > 0) {
        //    //int mid = sensorCount/2;
        //    int point = detectedSensorsOverLine.get(0);//getMidpoint(sensorsOverLine, detectedSensorsOverLine);
//
        //    //System.out.printf("%nSENSOR COUNT: %d", sensorCount);
        //    int divisor = 100/(sensorCount-1);
        //    return (double) (point * divisor)/100;
        //}

        return -1.0;
    }

    public double GetLinePositionSplitInversion(double position) {
        if (position == 0.5) { return 0; }
        return -position*2+1;
    }

    public double GetAngle(double percentage, double minAngle, double maxAngle, double rate) {
        double angle = ((minAngle / maxAngle) + Math.pow(percentage, 2))*rate;
        return Math.min(angle, maxAngle);
    }
}
