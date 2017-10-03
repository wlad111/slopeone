import gnu.trove.list.array.TByteArrayList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.map.hash.TIntByteHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TShortByteHashMap;
import gnu.trove.map.hash.TShortObjectHashMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;



public class NetflixPrize
{
    TIntObjectHashMap<TShortByteHashMap> trainingData;
    TShortObjectHashMap<TIntByteHashMap> testData;
    TDoubleArrayList predictions;
    TByteArrayList ratings;
    int numItems;

    public NetflixPrize(TIntObjectHashMap<TShortByteHashMap> d, int n)
    {
        trainingData = d;
        numItems = n;
    }



    public void getTestSet(File input) throws FileNotFoundException
    {
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
                rating = trainingData.get(userID).get(movieID);
                trainingData.get(userID).remove(movieID);
                if (trainingData.get(userID).keySet().isEmpty())
                {
                    trainingData.remove(userID);
                }
                testData.get(movieID).put(userID, rating);
            }
        }
        sc.close();
    }
}
