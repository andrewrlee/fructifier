package uk.co.optimisticpanda.db.apply;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import uk.co.optimisticpanda.db.apply.QueryExtractor;
import uk.co.optimisticpanda.db.apply.QueryExtractor.DelimiterLocation;
import uk.co.optimisticpanda.db.apply.QueryExtractor.QueryVisitor;

public class QueryExtractorTest {
	private QueryExtractor extractor;
	private static final String LINE_ENDING = System.getProperty("line.separator");

	public static final String platform = LINE_ENDING;
	public static final String cr = "\r";
	public static final String crlf = "\r\n";
	public static final String lf = "\n";

	@Before
	public void setUp() throws Exception {
		extractor = new QueryExtractor(";", LINE_ENDING, null);
	}


	@Test
	public void shouldNotSplitStatementsThatHaveNoDelimter() throws Exception {
		List<String> result = extract(extractor,"SELECT 1");
		assertThat(result).contains("SELECT 1");
		assertThat(result).hasSize(1);
	}

	@Test
	public void shouldIgnoreSemicolonsInTheMiddleOfALine() throws Exception {
		List<String> result = extract(extractor,"SELECT ';'");
		assertThat(result).contains("SELECT ';'");
		assertThat(result).hasSize(1);
	}

	@Test
	public void shouldSplitStatementsOnASemicolonAtTheEndOfALine() throws Exception {
		List<String> result = extract(extractor,"SELECT 1;\nSELECT 2;");
		assertThat(result).contains("SELECT 1", "SELECT 2");
		assertThat(result).hasSize(2);
	}

	@Test
	public void shouldSplitStatementsOnASemicolonAtTheEndOfALineEvenWithWindowsLineEndings() throws Exception {
		List<String> result = extract(extractor,"SELECT 1;\r\nSELECT 2;");
		assertThat(result).contains("SELECT 1", "SELECT 2");
		assertThat(result).hasSize(2);
	}

	@Test
	public void shouldSplitStatementsOnASemicolonAtTheEndOfALineIgnoringWhitespace() throws Exception {
		List<String> result = extract(extractor,"SELECT 1;  \nSELECT 2;  ");
		assertThat(result).contains("SELECT 1", "SELECT 2");
		assertThat(result).hasSize(2);
	}

	@Test
	public void shouldLeaveLineBreaksAlone() throws Exception {
		assertThat(extract(extractor,"SELECT\n1")).contains("SELECT" + LINE_ENDING + "1");
		assertThat(extract(extractor,"SELECT\r\n1")).contains("SELECT" + LINE_ENDING + "1");
	}

	@Test
	public void shouldSupportRowStyleTerminators() throws Exception {
		extractor = new QueryExtractor("/", null, DelimiterLocation.ON_OWN_LINE);

		List<String> result = extract(extractor,"SHOULD IGNORE /\nAT THE END OF A LINE\n/\nSELECT BLAH FROM DUAL");
		assertThat(result).contains("SHOULD IGNORE /" + LINE_ENDING + "AT THE END OF A LINE" + LINE_ENDING, "SELECT BLAH FROM DUAL");
		assertThat(result).hasSize(2);
	}

	@Test
	public void shouldSupportDefinedNewLineCharacters() throws Exception {
		extractor = new QueryExtractor(null, crlf, null);
		assertThat(extract(extractor,"SELECT\n1")).contains("SELECT\r\n1");
		assertThat(extract(extractor,"SELECT\r\n1")).contains("SELECT\r\n1");

		extractor = new QueryExtractor(null, cr, null);
		assertThat(extract(extractor,"SELECT\n1")).contains("SELECT\r1");
		assertThat(extract(extractor,"SELECT\r\n1")).contains("SELECT\r1");

		extractor = new QueryExtractor(null, lf, null);
		assertThat(extract(extractor,"SELECT\n1")).contains("SELECT\n1");
		assertThat(extract(extractor,"SELECT\r\n1")).contains("SELECT\n1");

		extractor = new QueryExtractor(null, platform, null);
		assertThat(extract(extractor,"SELECT\n1")).contains("SELECT" + LINE_ENDING + "1");
		assertThat(extract(extractor,"SELECT\r\n1")).contains("SELECT" + LINE_ENDING + "1");
	}

	private List<String> extract(QueryExtractor extractor, String text){
		QueryCollection collection = new QueryCollection();
		extractor.visitQueries(text, collection);
		return collection.queries;
	}
	
	private static class QueryCollection implements QueryVisitor {
		private List<String> queries = Lists.newArrayList();

		public void visit(int count, String query) {
			queries.add(query);
		}
	}
}
