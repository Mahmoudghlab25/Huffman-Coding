import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Statistics {
    private final static int bufferSize=8192;
    public static Map<List<Byte>, Integer> getFrequencyMap(String filePath, int n) throws IOException {
        Map<List<Byte>, Integer> map = new HashMap<>();

        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filePath))) {
            byte[] buffer = new byte[bufferSize - (bufferSize%n)];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                // Process the data in the buffer
                List<Byte> key = new ArrayList<>();
                for (int i = 0; i < bytesRead; i++) {
                    key.add(buffer[i]);
                    if(key.size()==n){
                        if (!map.containsKey(key)) {
                            map.put(key, 1);
                        } else {
                            map.put(key, map.get(key) + 1);
                        }
                        key = new ArrayList<>();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

//    public static void main(String[] args) throws IOException {
//        // Specify the path to the file
//        String filePath = "/home/mahmoud/Downloads/nethunter-2023.3b-oneplus_nord-eleven-kalifs-full/kalifs-arm64-full.tar.xz";
//        Path path = Paths.get(filePath);
//        File file = new File(filePath);
//        long sz = file.length();
//        System.out.println(sz);
//
//    }

}
