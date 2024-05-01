import java.sql.Array;
import java.util.ArrayList;

public class LineFollowerAlgorithm {
    int MidPoint = 40;
    private boolean IsLine(int sensorNumber) {
        //System.out.println(sensorNumber);
        return sensorNumber < MidPoint;
    }
    public double GetLinePosition(int[] sensorData) {
        int sensorCount = sensorData.length;
        ArrayList<Integer> detectedSensorsOverLine = new ArrayList();
        for (int i = 0; i < sensorCount; i++) {
            //System.out.println(sensorData[i]);
            boolean isOverLine = this.IsLine(sensorData[i]);
            if (isOverLine) {
                detectedSensorsOverLine.add(i);
                //System.out.printf("%n%s is over the line", i);
            }
        }

        int sensorsOverLine = detectedSensorsOverLine.size();

        if (sensorsOverLine > 0) {
            int midpoint = 0;
            if (sensorsOverLine > 1) {
                int sum = 0;
                for (int i = 0; i < sensorsOverLine; i++) {
                    int position = (sensorsOverLine - 1) - i;
                    sum += (sensorsOverLine + 1) - (position);
                }
                midpoint = sum/2;
            } else {
                detectedSensorsOverLine.get(0);
            }
            return (double) midpoint /(sensorCount*2-1);
        }

        return -1.0;
    }
}
