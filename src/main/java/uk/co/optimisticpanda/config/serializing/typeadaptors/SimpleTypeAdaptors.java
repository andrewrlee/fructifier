package uk.co.optimisticpanda.config.serializing.typeadaptors;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import uk.co.optimisticpanda.conf.TypeAdaptorRegistration;

import com.google.common.base.Optional;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class SimpleTypeAdaptors {

	public static class OptionalTypeAdaptor extends TypeAdaptorRegistration<Optional<String>> {

		@Override
		public void write(JsonWriter out, Optional<String> optional) throws IOException {
			if (optional != null && optional.isPresent()) {
				out.value(optional.get());
			} else {
				out.nullValue();
			}
		}

		@Override
		public Optional<String> read(JsonReader in) throws IOException {
			String nextString = in.nextString();
			return Optional.fromNullable(nextString);
		}

		@Override
		public <T> boolean supplies(TypeToken<T> type) {
			TypeToken<Optional<String>> optionalStringToken = new TypeToken<Optional<String>>() {
			};
			return type.equals(optionalStringToken);
		}
	}

	public static class OptionalFileTypeAdaptor extends TypeAdaptorRegistration<Optional<File>> {

		@Override
		public void write(JsonWriter out, Optional<File> optional) throws IOException {
			if (optional != null && optional.isPresent()) {
				out.value(optional.get().getAbsolutePath());
			} else {
				out.nullValue();
			}
		}

		@Override
		public Optional<File> read(JsonReader in) throws IOException {
			String nextString = in.nextString();
			return Optional.fromNullable(nextString == null ? null : new File(nextString));
		}

		@Override
		public <T> boolean supplies(TypeToken<T> type) {
			TypeToken<Optional<File>> optionalFileToken = new TypeToken<Optional<File>>() {};
			return type.equals(optionalFileToken);
		}
	}

	public static class FileTypeAdaptor extends TypeAdaptorRegistration<File> {

		@Override
		public void write(JsonWriter out, File file) throws IOException {
			if (file != null) {
				out.value(file.getAbsolutePath());
			} else {
				out.nullValue();
			}
		}

		@Override
		public File read(JsonReader in) throws IOException {
			String nextString = in.nextString();
			return nextString == null ? null : new File(nextString);
		}

		@Override
		public <T> boolean supplies(TypeToken<T> type) {
			return File.class.isAssignableFrom(type.getRawType());
		}
	}

	public static class ResourceTypeAdaptor extends TypeAdapter<Optional<Resource>> {

		@Autowired
		ResourceLoader loader;
		
		@Override
		public void write(JsonWriter out, Optional<Resource> value) throws IOException {
			if (value != null && value.isPresent()) {
				out.value(value.get().getDescription());
			} else {
				out.nullValue();
			}
		}

		@Override
		public Optional<Resource> read(JsonReader in) throws IOException {
			String nextString = in.nextString();
			return Optional.fromNullable(loader.getResource(nextString));
		}
	}

}