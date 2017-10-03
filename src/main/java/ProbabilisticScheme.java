import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TShortByteHashMap;
import gnu.trove.procedure.TIntProcedure;

public class ProbabilisticScheme extends NetflixPrize
{
    public ProbabilisticScheme(TIntObjectHashMap<TShortByteHashMap> d, int n)
    {
        super(d, n);
    }

    public double countProb(final short asset, final byte rating, final short condAsset, final byte condRating)
    {
        final double[] freq = {0};
        final double[] total = {0};
        trainingData.forEachKey(new TIntProcedure()
        {
            public boolean execute(int user)
            {
                if ((trainingData.get(user).get(condAsset) == condRating) && (trainingData.get(user).containsKey(condAsset)))
                {
                    total[0] += trainingData.size();
                    if ((trainingData.get(user).containsKey(asset)) && (trainingData.get(user).get(asset) == rating))
                    {
                        freq[0]++;
                    }
                }
                return true;
            }
        });
        return freq[0]/total[0];
    }

}
