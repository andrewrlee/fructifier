package uk.co.optimisticpanda.conf.serializing;

import java.io.IOException;
import java.util.LinkedHashMap;

import uk.co.optimisticpanda.conf.Phase;
import uk.co.optimisticpanda.conf.PhaseCollection;
import uk.co.optimisticpanda.conf.TypeAdaptorRegistration;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class PhaseCollectionTypeAdaptor extends TypeAdaptorRegistration<PhaseCollection> {

	/**
	 * We ignore the phases wrapper class and just write the main map using this
	 * adapter factory letting {@link PhaseTypeAdaptor} write out each phase.
	 */
	@Override
	public void write(JsonWriter out, PhaseCollection value) throws IOException {
		TypeToken<LinkedHashMap<String, Phase>> typeToken = new TypeToken<LinkedHashMap<String, Phase>>() {
		};
		gson.getAdapter(typeToken).write(out, value.getPhases());
	}

	@Override
	public PhaseCollection read(JsonReader in) throws IOException {
		PhaseCollection phases = new PhaseCollection();
		in.beginObject();
		while (in.hasNext() && in.peek() != JsonToken.END_OBJECT) {
			addPhase(in, phases);
		}
		in.endObject();

		return phases;
	}

	public void addPhase(JsonReader in, PhaseCollection phases)
			throws IOException {
		String phaseName = in.nextName();
		JsonElement element = gson.fromJson(in, JsonElement.class);
		if (!element.isJsonObject()) {
			throw new AssertionError(phaseName + " is not an object?:" + element);
		}
		JsonObject object = (JsonObject) element;
		object.addProperty("name", phaseName);
		Phase phase = gson.getAdapter(Phase.class).fromJsonTree(object);
		phases.put(phaseName, phase);
	}

	@Override
	public <T> boolean supplies(TypeToken<T> type) {
		return PhaseCollection.class.isAssignableFrom(type.getRawType());
	}

}