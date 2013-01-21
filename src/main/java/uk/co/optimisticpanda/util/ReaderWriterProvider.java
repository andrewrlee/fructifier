package uk.co.optimisticpanda.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;

public class ReaderWriterProvider {

	private final Optional<File> output;
	private StringWriter stringWriter;

	public ReaderWriterProvider (Optional<File> output) {
		this.output = output;
		this.stringWriter = new StringWriter();
	}

	public Reader getReader() {
		if (output.isPresent()) {
			try {
				return new BufferedReader(new FileReader(output.get()));
			} catch (IOException e) {
				throw Throwables.propagate(e);
			}
		}
		return new StringReader(stringWriter.toString());
	}

	public Writer getWriter() {
		if (output.isPresent()) {
			try {
				return new BufferedWriter(new FileWriter(output.get()));
			} catch (IOException e) {
				throw Throwables.propagate(e);
			}
		}
		return stringWriter;
	}

}
