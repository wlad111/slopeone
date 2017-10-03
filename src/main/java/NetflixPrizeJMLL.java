import com.spbsu.commons.math.vectors.impl.iterators.SparseVecNZIterator;
import com.spbsu.commons.math.vectors.impl.vectors.SparseVec;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntByteHashMap;
import gnu.trove.map.hash.TShortObjectHashMap;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class NetflixPrizeJMLL {
    ArrayList<SparseVec> trainingData;
    TShortObjectHashMap<TIntByteHashMap> testData;
    int numItems;

    public NetflixPrizeJMLL(File trainingSet, File probeSet, int n) throws FileNotFoundException {
        getTestSet(probeSet);
        File[] folderEntries = trainingSet.listFiles();
        this.trainingData = new ArrayList<SparseVec>(n + 1);
        trainingData.add(0, null);
        int i = 0;
        int dim;
        short movieID;
        int userID;
        double rating;
        this.numItems = n;

        for (File entry : folderEntries) {
            TDoubleArrayList values = new TDoubleArrayList();
            TIntArrayList indices = new TIntArrayList();
            i++;
            if(i % 100 == 0) {
                System.out.println(i/17771.0);
            }
            Scanner sc = new Scanner(entry).useDelimiter("\\D");
            movieID = sc.nextShort();
            sc.nextLine();
            while (sc.hasNext()) {
                userID = sc.nextInt();
                rating = sc.nextDouble();
                sc.nextLine();
                if ((testData.containsKey(movieID)) && (testData.get(movieID).containsKey(userID))) {
                    testData.get(movieID).put(userID, (byte)rating);
                }
                else {
                    indices.add(userID);
                    values.add(rating);
                }
            }
            dim = indices.size();
            this.trainingData.add(movieID, new SparseVec(dim, indices.toArray(), values.toArray()));
        }
    }


    public double countProb(final short asset, final byte rating, final short condAsset, final byte condRating) {
        double freq = 0;
        double total = 0;
        SparseVecNZIterator svIterCond = new SparseVecNZIterator(trainingData.get(condAsset));
        while (svIterCond.advance()) {
            if ((svIterCond.value() == condRating) && (trainingData.get(asset).get(svIterCond.index()) != 0)) {
                total++;
                if (trainingData.get(asset).get(svIterCond.index()) == rating) {
                    freq++;
                }
            }
        }
        if (total != 0) {
            return freq/total;
        }
        return 0;
    }

    public void getTestSet (File input) throws FileNotFoundException {
        testData = new TShortObjectHashMap<TIntByteHashMap>();
        System.out.println("Getting probe sample...");
        int t = 0;
        short movieID = 0;
        int userID;
        byte rating;
        Scanner sc = new Scanner(input);
        while (sc.hasNext())
        {
            String temp = sc.nextLine();
            if (temp.endsWith(":"))
            {
                if (t % 1000 == 0)
                {
                    System.out.println(t);
                }
                t++;
                movieID = Short.parseShort(temp.substring(0, temp.length() - 1));
                if (!testData.containsKey(movieID))
                {
                    testData.put(movieID, new TIntByteHashMap());
                }
            }
            else
            {
                userID = Integer.parseInt(temp);
                testData.get(movieID).put(userID, (byte)0);
            }
        }
        sc.close();
    }
}
