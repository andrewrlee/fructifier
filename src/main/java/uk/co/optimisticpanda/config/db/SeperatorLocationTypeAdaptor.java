package uk.co.optimisticpanda.config.db;

import java.io.IOException;

import uk.co.optimisticpanda.conf.TypeAdaptorRegistration;
import uk.co.optimisticpanda.config.db.apply.QueryExtractor.SeparatorLocation;

import com.google.common.base.Optional;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class SeperatorLocationTypeAdaptor extends TypeAdaptorRegistration<Optional<SeparatorLocation>> {

		@Override
		public <T> boolean supplies(TypeToken<T> type) {
			TypeToken<Optional<SeparatorLocation>> typeToken = new TypeToken<Optional<SeparatorLocation>>() {
			};
			return type.equals(typeToken);
		}

		@Override
		public void write(JsonWriter out, Optional<SeparatorLocation> value) throws IOException {
			if (value != null && value.isPresent()) {
				out.value(value.get().name());
			} else {
				out.nullValue();
			}
		}

		@Override
		public Optional<SeparatorLocation> read(JsonReader in) throws IOException {
			String nextString = in.nextString();
			return Optional.fromNullable(SeparatorLocation.valueOf(nextString));
		}

	}
