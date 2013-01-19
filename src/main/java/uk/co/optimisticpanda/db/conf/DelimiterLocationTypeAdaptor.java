package uk.co.optimisticpanda.db.conf;

import java.io.IOException;

import uk.co.optimisticpanda.conf.TypeAdaptorRegistration;
import uk.co.optimisticpanda.db.apply.QueryExtractor.DelimiterLocation;

import com.google.common.base.Optional;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class DelimiterLocationTypeAdaptor extends TypeAdaptorRegistration<Optional<DelimiterLocation>> {

		@Override
		public <T> boolean supplies(TypeToken<T> type) {
			TypeToken<Optional<DelimiterLocation>> typeToken = new TypeToken<Optional<DelimiterLocation>>() {
			};
			return type.equals(typeToken);
		}

		@Override
		public void write(JsonWriter out, Optional<DelimiterLocation> value) throws IOException {
			if (value != null && value.isPresent()) {
				out.value(value.get().name());
			} else {
				out.nullValue();
			}
		}

		@Override
		public Optional<DelimiterLocation> read(JsonReader in) throws IOException {
			String nextString = in.nextString();
			return Optional.fromNullable(DelimiterLocation.valueOf(nextString));
		}

	}
