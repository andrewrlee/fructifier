package uk.co.optimisticpanda.config.db.apply;


import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.co.optimisticpanda.config.db.apply.QueryExtractor.SeparatorLocation;

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
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		 
        URL[] urls = ((URLClassLoader)cl).getURLs();
 
        for(URL url: urls){
        	System.out.println(url.getFile());
        }
        List<String> result = extractor.getQueries("SELECT 1");
        assertThat(result, hasItem("SELECT 1"));
        assertThat(result.size(), is(1));
    }

    @Test
    public void shouldIgnoreSemicolonsInTheMiddleOfALine() throws Exception {
        List<String> result = extractor.getQueries("SELECT ';'");
        assertThat(result, hasItem("SELECT ';'"));
        assertThat(result.size(), is(1));
    }

    @Test
    public void shouldSplitStatementsOnASemicolonAtTheEndOfALine() throws Exception {
        List<String> result = extractor.getQueries("SELECT 1;\nSELECT 2;");
        assertThat(result, hasItems("SELECT 1", "SELECT 2"));
        assertThat(result.size(), is(2));
    }

    @Test
    public void shouldSplitStatementsOnASemicolonAtTheEndOfALineEvenWithWindowsLineEndings() throws Exception {
        List<String> result = extractor.getQueries("SELECT 1;\r\nSELECT 2;");
        assertThat(result, hasItems("SELECT 1", "SELECT 2"));
        assertThat(result.size(), is(2));
    }

    @Test
    public void shouldSplitStatementsOnASemicolonAtTheEndOfALineIgnoringWhitespace() throws Exception {
        List<String> result = extractor.getQueries("SELECT 1;  \nSELECT 2;  ");
        assertThat(result, hasItems("SELECT 1", "SELECT 2"));
        assertThat(result.size(), is(2));
    }

    @Test
    public void shouldLeaveLineBreaksAlone() throws Exception {
        assertThat(extractor.getQueries("SELECT\n1"), hasItems("SELECT" + LINE_ENDING + "1"));
        assertThat(extractor.getQueries("SELECT\r\n1"), hasItems("SELECT" + LINE_ENDING + "1"));
    }

    @Test
    public void shouldSupportRowStyleTerminators() throws Exception {
        extractor = new QueryExtractor("/", null, SeparatorLocation.ON_OWN_LINE);

        List<String> result = extractor.getQueries("SHOULD IGNORE /\nAT THE END OF A LINE\n/\nSELECT BLAH FROM DUAL");
        assertThat(result, hasItems("SHOULD IGNORE /" + LINE_ENDING + "AT THE END OF A LINE" + LINE_ENDING , "SELECT BLAH FROM DUAL"));
        assertThat(result.size(), is(2));
    }

	@Test
	public void shouldSupportDefinedNewLineCharacters() throws Exception {
		extractor = new QueryExtractor(null, crlf, null);
		assertThat(extractor.getQueries("SELECT\n1"), hasItems("SELECT\r\n1"));
		assertThat(extractor.getQueries("SELECT\r\n1"), hasItems("SELECT\r\n1"));

		extractor = new QueryExtractor(null, cr, null);
		assertThat(extractor.getQueries("SELECT\n1"), hasItems("SELECT\r1"));
		assertThat(extractor.getQueries("SELECT\r\n1"), hasItems("SELECT\r1"));


		extractor = new QueryExtractor(null, lf, null);
		assertThat(extractor.getQueries("SELECT\n1"), hasItems("SELECT\n1"));
		assertThat(extractor.getQueries("SELECT\r\n1"), hasItems("SELECT\n1"));


		extractor = new QueryExtractor(null, platform, null);
		assertThat(extractor.getQueries("SELECT\n1"), hasItems("SELECT" + LINE_ENDING + "1"));
		assertThat(extractor.getQueries("SELECT\r\n1"), hasItems("SELECT" + LINE_ENDING + "1"));
	}


}
