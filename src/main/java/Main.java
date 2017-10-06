import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TShortByteHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TIntProcedure;

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
        NetflixPrizeJMLL np = new NetflixPrizeJMLL(dataDirectory, probeSet, NUMBEROFMOVIES);


        /*
        while (true)
        {
            System.out.println("Enter asset, rating, conditional asset, conditional rating");
            Scanner sc = new Scanner(System.in);
            short asset = sc.nextShort();
            byte rating = sc.nextByte();
            short condAsset = sc.nextShort();
            byte condRating = sc.nextByte();
            long start = System.nanoTime();
            double prob = np.countProb(asset, rating, condAsset, condRating);
            long elapsedTime = System.nanoTime() - start;
            System.out.println("Counted probability: "  + prob);
            System.out.println("Time elapsed: " + elapsedTime);
        }
        */
        while (true) {
            System.out.println("Enter conditional asset and conditional rating to check the sum");
            Scanner sc = new Scanner(System.in);
            short condAsset = sc.nextShort();
            byte condRating = sc.nextByte();
            long start = System.nanoTime();
            double sum = 0;
            for (short i = 1; i <= np.numItems; i++) {
                for (byte j = 1; j <= 5; j++) {
                    sum = np.countProb(i, j, condAsset, condRating);
                    if (i % 1000 == 0) {
                        System.out.println(i);
                    }
                }
            }
            long elapsedTime = System.nanoTime() - start;
            System.out.println("Time elapsed: " + elapsedTime);
            System.out.println(sum);
        }
    }

  /*  public static TIntObjectHashMap<TShortByteHashMap> processFilesFromFolder(File folder) throws FileNotFoundException
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
    */
}
