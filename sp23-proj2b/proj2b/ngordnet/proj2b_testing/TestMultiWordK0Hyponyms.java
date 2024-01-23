package ngordnet.proj2b_testing;

import ngordnet.browser.NgordnetQuery;
import ngordnet.browser.NgordnetQueryHandler;
import ngordnet.main.WordNet;
import ngordnet.ngrams.NGramMap;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.TreeSet;

import static com.google.common.truth.Truth.assertThat;

/** Tests the case where the list of words is length greater than 1, but k is still zero. */
public class TestMultiWordK0Hyponyms {
    // this case doesn't use the NGrams dataset at all, so the choice of files is irrelevant
    public static final String WORDS_FILE = "data/ngrams/very_short.csv";
    public static final String TOTAL_COUNTS_FILE = "data/ngrams/total_counts.csv";
    public static final String SMALL_SYNSET_FILE = "data/wordnet/synsets16.txt";
    public static final String SMALL_HYPONYM_FILE = "data/wordnet/hyponyms16.txt";
    public static final String LARGE_SYNSET_FILE = "data/wordnet/synsets.txt";
    public static final String LARGE_HYPONYM_FILE = "data/wordnet/hyponyms.txt";

    /** This is an example from the spec.*/
    @Test
    public void testOccurrenceAndChangeK0() {
        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymHandler(
                WORDS_FILE, TOTAL_COUNTS_FILE, SMALL_SYNSET_FILE, SMALL_HYPONYM_FILE);
        List<String> words = List.of("occurrence", "change");

        NgordnetQuery nq = new NgordnetQuery(words, 0, 0, 0);
        String actual = studentHandler.handle(nq);
        String expected = "[alteration, change, increase, jump, leap, modification, saltation, transition]";
        assertThat(actual).isEqualTo(expected);
    }

    // TODO: Add more unit tests (including edge case tests) here.
    @Test
    public void testK() {
        WordNet wn = new WordNet(LARGE_SYNSET_FILE, LARGE_HYPONYM_FILE);
        NGramMap ngm = new NGramMap("./data/ngrams/top_14377_words.csv", "./data/ngrams/total_counts.csv");
        List<String> words = List.of("food", "cake");
        TreeSet<String> actual = wn.getHyponymsListK(words, 5, 1950, 1990, ngm);
        assertThat(actual).containsExactly("cake", "cookie", "kiss", "snap", "wafer").inOrder();
    }

    @Test
    public void testMulti() {
        WordNet wn = new WordNet(LARGE_SYNSET_FILE, LARGE_HYPONYM_FILE);
        NGramMap ngm = new NGramMap("./data/ngrams/top_14377_words.csv", "./data/ngrams/total_counts.csv");
        /**
        List<String> words1 = List.of("cat", "dog");
        TreeSet<String> actual1 = wn.getHyponymsListK(words1, 0, 1950, 1990, ngm);
        assertThat(actual1).containsExactly("Maltese").inOrder();

        List<String> words2 = List.of("meat", "fish");
        TreeSet<String> actual2 = wn.getHyponymsListK(words2, 0, 1950, 1990, ngm);
        assertThat(actual2).isEmpty();

        List<String> words3 = List.of("dog", "animal");
        TreeSet<String> actual3 = wn.getHyponymsListK(words3, 5, 2000, 2020, ngm);
        assertThat(actual3).containsExactly("dog", "pointer", "puppy", "toy").inOrder();



        List<String> words4 = List.of("walk", "action");
        TreeSet<String> actual4 = wn.getHyponymsListK(words4, 7, 1950, 2020, ngm);
        assertThat(actual4).containsExactly("constitutional", "foot", "pass", "roll", "turn", "walk", "walking").inOrder();
         */

        List<String> word = List.of("entity");
        TreeSet<String> ac = wn.getHyponymsListK(word, 6, 1470, 2019, ngm);
        assertThat(ac).containsExactly("are", "at", "have", "he", "in", "one").inOrder();
    }
    // TODO: Create similar unit test files for the k != 0 cases.
}
