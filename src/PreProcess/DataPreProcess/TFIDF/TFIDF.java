package PreProcess.DataPreProcess.TFIDF;

import PreProcess.DataPreProcess.Segment.EnglishSegment;
import PreProcess.DataPreProcess.Segment.Segment;

import static PreProcess.DataPreProcess.TFIDF.FigureIDF.figureIDF;

public class TFIDF {
    public static void tfidf(int TypeCode,Segment seg){
        switch (TypeCode){
            case PreCut.YOUTUBE:
                break;
            case PreCut.TWITTER:
                __twitter__(seg);
                break;
            case PreCut.FLICKR:
                break;
            case PreCut.TUMBLR:
                break;
            default:
                break;
        }
    }

    public static void tfidf(int TypeCode, Segment seg, String Collections){
        switch (TypeCode){
            case PreCut.TWITTER:
                __twitter__(Collections, seg);
                break;
            case PreCut.YOUTUBE:
                break;
            case PreCut.FLICKR:
                break;
            case PreCut.TUMBLR:
                break;

            default:
                break;
        }
    }
    private static void __twitter__(Segment seg){
        PreCut.preCut(PreCut.TWITTER,seg);
        figureIDF(PreCut.TWITTER);
        FigureTFIDF.figureTF(PreCut.TWITTER);
        SaveData.Save2DB(PreCut.TWITTER);
    }
    private static void __twitter__(String Collections, Segment seg){
        PreCut.preCut(PreCut.TWITTER,seg,Collections);
        figureIDF(PreCut.TWITTER);
        FigureTFIDF.figureTF(PreCut.TWITTER);
        SaveData.Save2DB(PreCut.TWITTER, Collections);
    }

    public static void main(String[] args){
        //建议调用该函数时先对整个数据集进行操作
        //tfidf(PreCut.TWITTER);
        tfidf(PreCut.TWITTER,new EnglishSegment(),"twitter_20171212132632_data");
    }
}
