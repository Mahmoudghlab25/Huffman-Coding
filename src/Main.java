import java.io.*;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    private static final int bufferSize = 8192;


    public static Node getTree(Map<List<Byte>, Integer> freqMap) {
        PriorityQueue<Node> pq = new PriorityQueue<>(Node::compareTo);
        int sz = freqMap.size();
        for (List<Byte> st : freqMap.keySet()) {
            Node node = new Leaf(st, freqMap.get(st));
            pq.add(node);
        }
        //create the root node
        for (int i = 1; i < sz; i++) {
            Node x = pq.poll();
            Node y = pq.poll();

            Node z = new Node(x.getFrequency() + y.getFrequency());
            z.setLeft(x);
            z.setRight(y);

            pq.add(z);
        }
        return pq.poll();
    }


    public static void HuffmanTree(Node root, String prefix, Map<List<Byte>, String> m) {
        if (root != null) {
            if (root instanceof Leaf) {
                m.put(((Leaf) root).getData(), prefix);
            }
            HuffmanTree(root.getLeft(), prefix + "0", m);
            HuffmanTree(root.getRight(), prefix + "1", m);
        }
    }

    public static void encode(String filePath, int n) throws IOException {
        File file = new File(filePath);
        FileInputStream reader = new FileInputStream(file);
        String outpath = file.getParent() + "/20011811." + n + "." + file.getName() + ".hc";
        Map<List<Byte>, String> map = new HashMap<>();
        Map<List<Byte>, Integer> freqmap = Statistics.getFrequencyMap(filePath, n);
        Node root = getTree(freqmap);
        HuffmanTree(root, "", map);//map is created

        FileOutputStream outputStream = new FileOutputStream(outpath);
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        //write header
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        oos.writeObject(root);
        long fileSize = 0;
        for (Map.Entry<List<Byte>, Integer> entry : freqmap.entrySet()) {
            List<Byte> data = entry.getKey();
            int frequency = entry.getValue();
            int codeLength = map.get(data).length();
            fileSize += (long) frequency * codeLength;
        }
        int remByteLen = (int) (fileSize % 8);
        dataOutputStream.writeLong((long) Math.ceil(fileSize / 8.0));
        outputStream.write(remByteLen);

        byte[] block = new byte[bufferSize - (bufferSize % n)];
        int bytesRead;
        StringBuilder s = new StringBuilder();
        while ((bytesRead = reader.read(block)) != -1) {
            List<Byte> bytes = new ArrayList<>();
            for (int i = 0; i < bytesRead; i++) {
                bytes.add(block[i]);
                if (bytes.size() == n) {
                    String huffmanCode = map.get(bytes);
                    if (huffmanCode == null) {
                        break;
                    }
                    s.append(huffmanCode);
                    bytes = new ArrayList<>();
                }
            }
            byte[] writtenBytes = new byte[s.length()];
            int x = 0;
            while (s.length() >= 8) {
                String byteStr = s.substring(0, 8);
                int byteValue = Integer.parseInt(byteStr, 2);
                writtenBytes[x++] = (byte) byteValue;
                s.delete(0, 8);
            }
            outputStream.write(writtenBytes, 0, x);
        }
        if (!s.isEmpty()) {
            while (s.length() < 8) {
                s.append('0');
            }
            String byteStr = s.substring(0, 8);
            int byteValue = Integer.parseInt(byteStr, 2);
            outputStream.write((byte) byteValue);
        }
        oos.close();
        dataOutputStream.close();
        outputStream.close();
        reader.close();
        long  file1size = new File(filePath).length();
        long  file2size = new File(outpath).length();
        System.out.println("The compression ratio = " + ((double) file2size / file1size));
    }


    public static void decode(String filePath) {
        File file = new File(filePath);
        String outPath = file.getParent() + "/extracted." + file.getName().replaceFirst("\\.hc$", "");
        long bytesread = 0;
        try (FileInputStream in = new FileInputStream(file);
             FileOutputStream out = new FileOutputStream(outPath);
             DataInputStream in2 = new DataInputStream(in);
             ObjectInputStream ois = new ObjectInputStream(in)) {

            Node root = (Node) ois.readObject();
            long totalbytes = in2.readLong();
            int finalBytesize = in.read();
            Node current = root;

            byte[] bytes = new byte[bufferSize];
            int bytesRead;
            StringBuilder s = new StringBuilder();
            while ((bytesRead = in.read(bytes)) != -1) {
                for (byte aByte : bytes) {
                    s.append(String.format("%8s", Integer.toBinaryString(aByte & 0xFF)).replace(' ', '0'));
                    bytesread++;
                    if (bytesread == totalbytes) {
                        List<Byte> arr = new ArrayList<>();
                        for (int i = 0; i < finalBytesize; i++) {
                            char bit = s.charAt(i);
                            if (bit == '0') {
                                current = current.getLeft();
                            } else if (bit == '1') {
                                current = current.getRight();
                            }

                            if (current instanceof Leaf) {
                                List<Byte> dataList = ((Leaf) current).getData();
                                arr.addAll(dataList);
                                current = root;
                            }
                        }
                        byte[] b = new byte[arr.size()];
                        for (int i = 0; i < b.length; i++) {
                            b[i] = arr.get(i);
                        }
                        out.write(b);
                        break;
                    } else {
                        List<Byte> arr = new ArrayList<>();
                        for (int i = 0; i < s.length(); i++) {
                            char bit = s.charAt(i);
                            if (bit == '0') {
                                current = current.getLeft();
                            } else if (bit == '1') {
                                current = current.getRight();
                            }
                            if (current instanceof Leaf) {
                                List<Byte> dataList = ((Leaf) current).getData();
                                arr.addAll(dataList);
                                current = root;
                            }
                        }
                        byte[] b = new byte[arr.size()];
                        for (int i = 0; i < b.length; i++) {
                            b[i] = arr.get(i);
                        }
                        out.write(b);
                    }
                    s.delete(0, s.length());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        char choice = args[0].charAt(0);
        String fp = args[1];
        if (choice == 'c') {
            int n = Integer.parseInt(args[2]);
            long startTime, endTime;
            startTime = System.currentTimeMillis();
            encode(fp, n);
            endTime = System.currentTimeMillis();
            System.out.println("Total time to compress = " + (endTime - startTime) + " ms");
            //compress
        } else if (choice == 'd') {
            long startTime, endTime;
            startTime = System.currentTimeMillis();
            decode(fp);
            endTime = System.currentTimeMillis();
            System.out.println("Total time to decompress = " + (endTime - startTime) + " ms");            //decompress
        } else {
            System.out.println("no such choice");
        }
    }
}
