package uk.co.optimisticpanda.conf.serializing;

import java.io.File;
import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import uk.co.optimisticpanda.conf.TypeAdaptorRegistration;

import com.google.common.base.Optional;
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
			return type.equals(new TypeToken<Optional<String>>() {});
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
			return type.equals(new TypeToken<Optional<File>>() {});
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

	public static class ResourceTypeAdaptor extends TypeAdaptorRegistration<Resource> {

		private ResourceLoader loader;
		
		public ResourceTypeAdaptor(ResourceLoader resourceLoader) {
			loader = resourceLoader;
		}

		@Override
		public void write(JsonWriter out, Resource value) throws IOException {
			if (value != null) {
				out.value(value.getDescription());
			} else {
				out.nullValue();
			}
		}

		@Override
		public Resource read(JsonReader in) throws IOException {
			String nextString = in.nextString();
			return loader.getResource(nextString);
		}

		@Override
		public <T> boolean supplies(TypeToken<T> type) {
			return Resource.class.isAssignableFrom(type.getRawType());
		}
	}
	
	public static class OptionalResourceTypeAdaptor extends TypeAdaptorRegistration<Optional<Resource>> {
		
		private ResourceLoader loader;
		
		public OptionalResourceTypeAdaptor(ResourceLoader resourceLoader) {
			loader = resourceLoader;
		}

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
		
		@Override
		public <T> boolean supplies(TypeToken<T> type) {
			return type.equals(new TypeToken<Optional<Resource>>() {});
		}
	}

}