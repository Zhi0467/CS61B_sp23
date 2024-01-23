package ngordnet.main;

import ngordnet.browser.NgordnetQuery;
import ngordnet.browser.NgordnetQueryHandler;
import ngordnet.ngrams.NGramMap;

import java.util.List;

public class HyponymsHandler extends NgordnetQueryHandler {
    private WordNet wordNet;
    private NGramMap ngm;
    public HyponymsHandler(WordNet wordNet, NGramMap ngm) {
        this.wordNet = wordNet;
        this.ngm = ngm;
    }
    @Override
    public String handle(NgordnetQuery q) {
        List<String> words = q.words();
        int startYear = q.startYear();
        int endYear = q.endYear();
        int k = q.k();
        if (words.size() == 1 && k == 0) {
            return wordNet.getHyponyms(words.get(0)).toString();
        } else {
            return wordNet.getHyponymsListK(words, k, startYear, endYear, this.ngm).toString();
        }
    }
}
