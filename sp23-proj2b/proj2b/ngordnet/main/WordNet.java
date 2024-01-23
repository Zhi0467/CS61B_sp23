package ngordnet.main;

import edu.princeton.cs.algs4.In;

import java.util.*;
import ngordnet.ngrams.NGramMap;
import ngordnet.ngrams.TimeSeries;

public class WordNet {
    private Graph wordNet;
    private Map<Integer, TreeSet<String>> synsets;
    private Map<String, TreeSet<Integer>> inverse;

    public WordNet(String synsetFilename, String hyponymFilename) {
        wordNet = new Graph();
        synsets = new HashMap<>();
        inverse = new HashMap<>();
        // parse through the synset file, create the synsets map and create the vertex set for the graph
        In synsetFile = new In(synsetFilename);
        while (!synsetFile.isEmpty()) {
            String line = synsetFile.readLine();
            String[] synset = line.split(",");
            int id = Integer.parseInt(synset[0]);
            List<String> words = Arrays.asList(synset[1].split(" "));
            for (String word : words) {
                if (inverse.containsKey(word)) {
                    TreeSet<Integer> s = inverse.get(word);
                    s.add(id);
                    inverse.put(word, s);
                } else {
                    TreeSet<Integer> s = new TreeSet<>();
                    s.add(id);
                    inverse.put(word, s);
                }
            }
            TreeSet<String> wordss = new TreeSet<>(words);
            synsets.put(id, wordss);
            wordNet.addVertex(id);
        }
        // parse through the hyponym file, create the edges for the graph
        In hyponymFile = new In(hyponymFilename);
        while (!hyponymFile.isEmpty()) {
            String line = hyponymFile.readLine();
            String[] hyponym = line.split(",");
            int parent = Integer.parseInt(hyponym[0]);
            for (int i = 1; i < hyponym.length; i++) {
                int child = Integer.parseInt(hyponym[i]);
                wordNet.addEdge(parent, child);
            }
        }
    }

    // then some methods to return the hyponyms of a word
    public TreeSet<String> getHyponyms(String word) {
        if (!inverse.containsKey(word)) {
            return new TreeSet<>();
        }
        TreeSet<Integer> ids = inverse.get(word);
        Set<Integer> hyponymIds = new TreeSet<>();
        for (int id : ids) {
            hyponymIds.addAll(wordNet.dfs(id));
        }
        TreeSet<String> hyponyms = new TreeSet<>();
        for (int id : hyponymIds) {
            hyponyms.addAll(synsets.get(id));
        }
        return hyponyms;
    }

    public TreeSet<String> getHyponymsList(List<String> words) {
        TreeSet<String> returned = new TreeSet<>();
        int count = 0;
        for (String word : words) {
            if (count == 0) {
                returned.addAll(getHyponyms(word));
                count++;
            }
            TreeSet<String> temp = getHyponyms(word);
            returned.retainAll(temp);
        }
        return returned;
    }

    public TreeSet<String> getHyponymsListK(List<String> words, int k, int startYear, int endYear, NGramMap ngm) {
        TreeSet<String> initial;
        if (words.size() == 1) {
            initial = getHyponyms(words.get(0));
        } else {
            initial = getHyponymsList(words);
        }
        if (k == 0) {
            return initial;
        }

        TreeMap<Double, List<String>> wordCount = new TreeMap<>(Collections.reverseOrder());
        TreeSet<String> filtered = new TreeSet<>();

        for (String word : initial) {
            TimeSeries ts = ngm.countHistory(word, startYear, endYear);
            double sum = 0;
            if (ts != null) {
                for (int i : ts.years()) {
                    sum += ts.get(i);
                }
                if (sum != 0) {
                    filtered.add(word);
                    if (!wordCount.containsKey(sum)) {
                        List<String> wordList = new ArrayList<>();
                        wordList.add(word);
                        wordCount.put(sum, wordList);
                    } else {
                        List<String> wordList = wordCount.get(sum);
                        wordList.add(word);
                        wordCount.put(sum, wordList);
                    }
                }
            }
        }
        if (filtered.size() <= k) {
            return filtered;
        } else {
            List<Double> topSums = new ArrayList<>(wordCount.keySet());
            List<List<String>> topKs = new ArrayList<>();
            for (int i = 0; i < k && i < filtered.size(); i++) {
                topKs.add(wordCount.get(topSums.get(i)));
            }
            int count = 0;
            int size = 0;
            TreeSet<String> returned = new TreeSet<>();
            while (size < k && size < filtered.size()) {
                for (int i = 0; i < topKs.get(count).size(); i++) {
                    if (size < k && size < filtered.size()) {
                        returned.add(topKs.get(count).get(i));
                    }
                    size++;
                }
                count++;
            }
            return returned;
        }
    }
}
