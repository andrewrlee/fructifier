package uk.co.optimisticpanda.config.db.apply;

import static org.springframework.util.StringUtils.trimTrailingWhitespace;
import static uk.co.optimisticpanda.config.db.apply.QueryExtractor.SeparatorLocation.END_OF_LINE;
import static uk.co.optimisticpanda.config.db.apply.QueryExtractor.SeparatorLocation.ON_OWN_LINE;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.common.base.Optional;

public class QueryExtractor {
	private Optional<String> delimiter;
	private Optional<String> separator;
	private Optional<SeparatorLocation> separatorLocation;

	public static enum SeparatorLocation {
		END_OF_LINE, ON_OWN_LINE;
	}

	public QueryExtractor(String delimiter, String separator, SeparatorLocation separatorLocation) {
		super();
		this.delimiter = Optional.fromNullable(delimiter);
		this.separator = Optional.fromNullable(separator);
		this.separatorLocation = Optional.fromNullable(separatorLocation);
	}


	public List<String> getQueries(String input) {
		List<String> statements = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		Scanner scanner = new Scanner(input);
		while (scanner.hasNextLine()) {
			String line = trimTrailingWhitespace(scanner.nextLine());

			if (builder.length() > 0) {
				builder.append(getSeparator());
			}
			builder.append(line);

			if (endStatementEol(line) || endStatementOol(line)) {
				statements.add(withoutTrailingDelimiter(builder));
				builder = new StringBuilder();
			}
		}
		if (builder.length() > 0) {
			statements.add(builder.toString());
		}
		return statements;
	}

	private String withoutTrailingDelimiter(StringBuilder builder) {
		return builder.toString().substring(0, builder.length() - getDelimiter().length());
	}

	private boolean endStatementEol(String line) {
		return getSeparatorLocation() == END_OF_LINE && line.endsWith(getDelimiter());
	}

	private boolean endStatementOol(String line) {
		return getSeparatorLocation() == ON_OWN_LINE && line.equals(getDelimiter());
	}

	public String getDelimiter() {
		return delimiter.or(";");
	}

	public String getSeparator() {
		return separator.or(System.getProperty("line.separator"));
	}
	
	public SeparatorLocation getSeparatorLocation() {
		return separatorLocation.or(SeparatorLocation.END_OF_LINE);
	}
}
