package PreProcess.DataPreProcess;

import PreProcess.DataPreProcess.Dictionary.GenerateDic;
import PreProcess.DataPreProcess.Segment.EnglishSegment;
import PreProcess.DataPreProcess.Segment.Segment;
import PreProcess.DataPreProcess.TFIDF.PreCut;
import PreProcess.DataPreProcess.TFIDF.TFIDF;

public class PreProcessCenter {
    public static void main(String[]  args){
        Segment seg = new EnglishSegment();
        GenerateDic.generateDict("TwitterData",seg,"twitter_20171212132632_data");
        TFIDF.tfidf(PreCut.TWITTER,seg,"twitter_20171212132632_data");
    }
}
