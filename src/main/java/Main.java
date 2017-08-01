import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TShortByteHashMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static final int NUMBEROFMOVIES = 17770;

    public static void main(String[] args) throws FileNotFoundException {
        File dataDirectory = new File("D:\\Downloads\\nf_prize_dataset\\download\\training_set\\training_set");
        File probeSet = new File("D:\\Downloads\\nf_prize_dataset\\download\\probe.txt");
        long heapSize = Runtime.getRuntime().maxMemory();
        //heapSize = Runtime.getRuntime().freeMemory();
        System.out.println("Heap Size = " + heapSize);
        SlopeOne so = new SlopeOne(processFilesFromFolder(dataDirectory), 17770);
        so.getTestSet(probeSet);
        so.predictTestRatings();
        System.out.println("RMSE = " + so.countRMSE());
        while (true)
        {
            System.out.println("Enter userID and itemID to predict");
            Scanner sc = new Scanner(System.in);
            int userID = sc.nextInt();
            short itemID = sc.nextShort();
            System.out.println(so.predictOne(userID, itemID) + " " + so.trainingData.get(userID).get(itemID));
        }

    }

    public static TIntObjectHashMap<TShortByteHashMap> processFilesFromFolder(File folder) throws FileNotFoundException
    {
        short movieID;
        int userID;
        byte rating;
        TIntObjectHashMap<TShortByteHashMap> dataSet = new TIntObjectHashMap<TShortByteHashMap>();
        File[] folderEntries = folder.listFiles();
        int i = 0;
        for (File entry : folderEntries) {
            i++;
            if(i % 100 == 0) {
                System.out.println(i/17771.0);
            }
            Scanner sc = new Scanner(entry).useDelimiter("\\D");
            movieID = sc.nextShort();
            sc.nextLine();
            while (sc.hasNext()) {
                userID = sc.nextInt();
                rating = sc.nextByte();
                sc.nextLine();
                if (!dataSet.containsKey(userID))
                {
                    dataSet.put(userID, new TShortByteHashMap());
                }
                dataSet.get(userID).put(movieID, rating);
            }
        }
        return dataSet;
    }
}
