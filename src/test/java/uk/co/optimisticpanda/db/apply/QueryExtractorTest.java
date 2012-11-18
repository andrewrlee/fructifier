package uk.co.optimisticpanda.db.apply;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.co.optimisticpanda.db.apply.QueryExtractor;
import uk.co.optimisticpanda.db.apply.QueryExtractor.SeparatorLocation;

public class QueryExtractorTest {
    private QueryExtractor extractor;
    private static final String LINE_ENDING = System.getProperty("line.separator");
    
    public static final String platform  = LINE_ENDING;
    public static final String cr = "\r"; 
    public static final String crlf = "\r\n";
    public static final String lf = "\n"; 

    @Before
    public void setUp() throws Exception {
        extractor = new QueryExtractor(";", LINE_ENDING, null);
    }

    @Test
    public void shouldNotSplitStatementsThatHaveNoDelimter() throws Exception {
        List<String> result = extractor.getQueries("SELECT 1");
        assertThat(result).contains("SELECT 1");
        assertThat(result).hasSize(1);
    }

    @Test
    public void shouldIgnoreSemicolonsInTheMiddleOfALine() throws Exception {
        List<String> result = extractor.getQueries("SELECT ';'");
        assertThat(result).contains("SELECT ';'");
        assertThat(result).hasSize(1);
    }

    @Test
    public void shouldSplitStatementsOnASemicolonAtTheEndOfALine() throws Exception {
        List<String> result = extractor.getQueries("SELECT 1;\nSELECT 2;");
        assertThat(result).contains("SELECT 1", "SELECT 2");
        assertThat(result).hasSize(2);
    }

    @Test
    public void shouldSplitStatementsOnASemicolonAtTheEndOfALineEvenWithWindowsLineEndings() throws Exception {
        List<String> result = extractor.getQueries("SELECT 1;\r\nSELECT 2;");
        assertThat(result).contains("SELECT 1", "SELECT 2");
        assertThat(result).hasSize(2);
    }

    @Test
    public void shouldSplitStatementsOnASemicolonAtTheEndOfALineIgnoringWhitespace() throws Exception {
        List<String> result = extractor.getQueries("SELECT 1;  \nSELECT 2;  ");
        assertThat(result).contains("SELECT 1", "SELECT 2");
        assertThat(result).hasSize(2);
    }

    @Test
    public void shouldLeaveLineBreaksAlone() throws Exception {
        assertThat(extractor.getQueries("SELECT\n1")).contains("SELECT" + LINE_ENDING + "1");
        assertThat(extractor.getQueries("SELECT\r\n1")).contains("SELECT" + LINE_ENDING + "1");
    }

    @Test
    public void shouldSupportRowStyleTerminators() throws Exception {
        extractor = new QueryExtractor("/", null, SeparatorLocation.ON_OWN_LINE);

        List<String> result = extractor.getQueries("SHOULD IGNORE /\nAT THE END OF A LINE\n/\nSELECT BLAH FROM DUAL");
        assertThat(result).contains("SHOULD IGNORE /" + LINE_ENDING + "AT THE END OF A LINE" + LINE_ENDING , "SELECT BLAH FROM DUAL");
        assertThat(result).hasSize(2);
    }

	@Test
	public void shouldSupportDefinedNewLineCharacters() throws Exception {
		extractor = new QueryExtractor(null, crlf, null);
		assertThat(extractor.getQueries("SELECT\n1")).contains("SELECT\r\n1");
		assertThat(extractor.getQueries("SELECT\r\n1")).contains("SELECT\r\n1");

		extractor = new QueryExtractor(null, cr, null);
		assertThat(extractor.getQueries("SELECT\n1")).contains("SELECT\r1");
		assertThat(extractor.getQueries("SELECT\r\n1")).contains("SELECT\r1");

		extractor = new QueryExtractor(null, lf, null);
		assertThat(extractor.getQueries("SELECT\n1")).contains("SELECT\n1");
		assertThat(extractor.getQueries("SELECT\r\n1")).contains("SELECT\n1");

		extractor = new QueryExtractor(null, platform, null);
		assertThat(extractor.getQueries("SELECT\n1")).contains("SELECT" + LINE_ENDING + "1");
		assertThat(extractor.getQueries("SELECT\r\n1")).contains("SELECT" + LINE_ENDING + "1");
	}

}
