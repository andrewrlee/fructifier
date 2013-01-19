package uk.co.optimisticpanda.db.apply;

import static org.springframework.util.StringUtils.trimTrailingWhitespace;
import static uk.co.optimisticpanda.db.apply.QueryExtractor.DelimiterLocation.END_OF_LINE;
import static uk.co.optimisticpanda.db.apply.QueryExtractor.DelimiterLocation.ON_OWN_LINE;

import java.util.Scanner;

import com.google.common.base.Optional;

public class QueryExtractor {
	private Optional<String> delimiter;
	private Optional<String> separator;
	private Optional<DelimiterLocation> delimiterLocation;

	public static enum DelimiterLocation {
		END_OF_LINE, ON_OWN_LINE;
	}

	public QueryExtractor(String delimiter, String separator, DelimiterLocation separatorLocation) {
		super();
		this.delimiter = Optional.fromNullable(delimiter);
		this.separator = Optional.fromNullable(separator);
		this.delimiterLocation = Optional.fromNullable(separatorLocation);
	}

	public void visitQueries(String input, QueryVisitor visitor) {
		int count = 0; 
		StringBuilder builder = new StringBuilder();
		Scanner scanner = new Scanner(input);
		while (scanner.hasNextLine()) {
			String line = trimTrailingWhitespace(scanner.nextLine());

			if (builder.length() > 0) {
				builder.append(getSeparator());
			}
			builder.append(line);

			if (statementEnds(line)) {
				visitor.visit(++count, withoutTrailingDelimiter(builder));
				builder = new StringBuilder();
			}
		}
		if (builder.length() > 0) {
			visitor.visit(++count, builder.toString());
		}
	}

	private String withoutTrailingDelimiter(StringBuilder builder) {
		return builder.toString().substring(0, builder.length() - getDelimiter().length());
	}

	private boolean statementEnds(String line) {
		return 	getDelimiterLocation() == END_OF_LINE && line.endsWith(getDelimiter())
			|| 
				getDelimiterLocation() == ON_OWN_LINE && line.equals(getDelimiter());
	}


	public String getDelimiter() {
		return delimiter.or(";");
	}

	public String getSeparator() {
		return separator.or(System.getProperty("line.separator"));
	}
	
	public DelimiterLocation getDelimiterLocation() {
		return delimiterLocation.or(DelimiterLocation.END_OF_LINE);
	}
	
	public static interface QueryVisitor{
		
		public void visit(int count, String query);
		
	}
	
}
