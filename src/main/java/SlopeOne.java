import gnu.trove.list.array.TByteArrayList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.map.hash.*;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TShortProcedure;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static java.lang.Math.sqrt;




public class SlopeOne extends NetflixPrize
{
    TIntObjectHashMap<TShortByteHashMap> trainingData;
    TShortObjectHashMap<TIntByteHashMap> testData;
    TDoubleArrayList predictions;
    TByteArrayList ratings;
    int numItems;

    public SlopeOne(TIntObjectHashMap<TShortByteHashMap> d, int n)
    {
        super(d, n);
    }

    public double predictOne(final int userID, final short itemID)
    {
        final double[] diffs = new double[numItems + 1];
        final int[] freqs = new int[numItems + 1];
        trainingData.forEachKey(new TIntProcedure() {
                    public boolean execute(final int user) {if (trainingData.get(user).containsKey(itemID)) {
                    trainingData.get(user).forEachKey(new TShortProcedure() {
                        public boolean execute(short otherItem) {
                            if (otherItem != itemID) {
                                diffs[otherItem] += trainingData.get(user).get(itemID) - trainingData.get(user).get(otherItem);
                                freqs[otherItem] += 1;
                            }
                            return true;
                        }
                    });
                }
                return true;
            }
        });
        for (int i = 1; i < numItems + 1; i++)
        {
            diffs[i] /= freqs[i];
        }
        final double[] res = {0};
        final int[] sum = {0};
        trainingData.get(userID).forEachKey(new TShortProcedure() {
            public boolean execute(short item) {
                if (item != itemID)
                {
                    res[0] += (diffs[item] + trainingData.get(userID).get(item)) * freqs[item];
                    sum[0] += freqs[item];
                }
                return true;
            }
        });
        return res[0] / sum[0];
    }

    public void predictTestRatings()
    {
        System.out.println("Started prediction");
        predictions = new TDoubleArrayList();
        ratings = new TByteArrayList();
        final int[] k = {0};
        final int[] t = {1};
        testData.forEachKey(new TShortProcedure() {
            public boolean execute(final short itemID) {
                t[0] += 1;
                if (t[0] % 10 == 0)
                {
                    System.out.println("Processing " + t[0] + "movie...");
                }
                final double[] diffs = new double[numItems + 1];
                final int[] freqs = new int[numItems + 1];
                //training differencies line for itemID
                trainingData.forEachKey(new TIntProcedure() {
                    public boolean execute(final int user) {
                        if (trainingData.get(user).containsKey(itemID)) {
                            trainingData.get(user).forEachKey(new TShortProcedure() {
                                public boolean execute(short otherItem) {
                                    if (otherItem != itemID) {
                                        diffs[otherItem] += trainingData.get(user).get(itemID) - trainingData.get(user).get(otherItem);
                                        freqs[otherItem] += 1;
                                    }
                                    return true;
                                }
                            });
                        }
                        return true;
                    }
                });
                for (int i = 1; i < numItems + 1; i++)
                {
                    diffs[i] /= freqs[i];
                }
                testData.get(itemID).forEachKey(new TIntProcedure() {
                    public boolean execute(final int user) {
                        final double[] res = {0};
                        final int[] sum = {0};
                        trainingData.get(user).forEachKey(new TShortProcedure() {
                            public boolean execute(short item) {
                                if (item != itemID)
                                {
                                    res[0] += (diffs[item] + trainingData.get(user).get(item)) * freqs[item];
                                    sum[0] += freqs[item];
                                }
                                return true;
                            }
                        });
                        predictions.add(res[0]/sum[0]);
                        ratings.add(testData.get(itemID).get(user));
                        k[0]+= 1;
                        if(k[0] % 100000 == 0)
                        {
                            System.out.println(predictions.get(k[0] - 1) + " " + ratings.get(k[0] - 1));
                        }
                        return true;
                    }
                });
                return true;
            }
        });


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

    public double countRMSE()
    {
        double sum = 0;
        double delta = 0;
        int n = predictions.size();
        System.out.println("Predictions size " + n);
        System.out.println("Ratings size " + ratings.size());
        for (int i = 0; i < n; i++)
        {
            delta = predictions.get(i) - ratings.get(i);
            if (!Double.isNaN(delta)) {
                sum += (delta * delta);
            }
        }
        System.out.println("sum = " + sum);
        System.out.println("n = " + n);
        return sqrt(sum/n);
    }

}
