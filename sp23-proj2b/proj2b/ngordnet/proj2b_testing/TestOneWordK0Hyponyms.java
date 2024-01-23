package ngordnet.proj2b_testing;

import ngordnet.browser.NgordnetQuery;
import ngordnet.browser.NgordnetQueryHandler;
import ngordnet.main.WordNet;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/** Tests the most basic case for Hyponyms where the list of words is one word long, and k = 0.*/
public class TestOneWordK0Hyponyms {
    // this case doesn't use the NGrams dataset at all, so the choice of files is irrelevant
    public static final String WORDS_FILE = "data/ngrams/very_short.csv";
    public static final String TOTAL_COUNTS_FILE = "data/ngrams/total_counts.csv";
    public static final String SMALL_SYNSET_FILE = "data/wordnet/synsets16.txt";
    public static final String SMALL_HYPONYM_FILE = "data/wordnet/hyponyms16.txt";

    @Test
    public void testActK0() {
        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymHandler(
                WORDS_FILE, TOTAL_COUNTS_FILE, SMALL_SYNSET_FILE, SMALL_HYPONYM_FILE);
        List<String> words = List.of("act");

        NgordnetQuery nq = new NgordnetQuery(words, 0, 0, 0);
        String actual = studentHandler.handle(nq);
        String expected = "[act, action, change, demotion, human_action, human_activity, variation]";
        assertThat(actual).isEqualTo(expected);
    }

    // TODO: Add more unit tests (including edge case tests) here.
    @Test
    public void testWordNet_small() {
        WordNet wn = new WordNet("data/wordnet/synsets11.txt", "data/wordnet/hyponyms11.txt");
        assertThat(wn.getHyponyms("action")).containsExactly("action","change", "demotion").inOrder();
        assertThat(wn.getHyponyms("change")).containsExactly("change", "demotion").inOrder();
        assertThat(wn.getHyponyms("demotion")).containsExactly("demotion").inOrder();
        assertThat(wn.getHyponyms("descent")).containsExactly("descent", "jump", "parachuting").inOrder();
        assertThat(wn.getHyponyms("jump")).containsExactly("jump","leap", "parachuting").inOrder();
        assertThat(wn.getHyponyms("penis")).isEmpty();
        List<String> test = new ArrayList<>();
        test.add("jump");
        test.add("penis");
        assertThat(wn.getHyponymsList(test)).isEmpty();
    }
    @Test
    public void testWordNet_large() {
        WordNet wn = new WordNet("data/wordnet/synsets.txt", "data/wordnet/hyponyms.txt");
        assertThat(wn.getHyponyms("watermelon")).containsExactly("Citrullus_vulgaris", "watermelon", "watermelon_vine").inOrder();
    }
}
