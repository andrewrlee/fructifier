package uk.co.optimisticpanda.conf.serializing;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Set;

import uk.co.optimisticpanda.conf.Connection;
import uk.co.optimisticpanda.conf.ConnectionCollection;
import uk.co.optimisticpanda.conf.TypeAdaptorRegistration;
import uk.co.optimisticpanda.runner.RegisteredExtensions;
import uk.co.optimisticpanda.util.ConfigurationException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class ConnectionCollectionTypeAdaptor extends TypeAdaptorRegistration<ConnectionCollection> {

	private final RegisteredExtensions registeredExtensions;

	public ConnectionCollectionTypeAdaptor(RegisteredExtensions registeredExtensions) {
		this.registeredExtensions = registeredExtensions;
	}

	/**
	 * We ignore the phases wrapper class and just write the main map.
	 */
	@Override
	public void write(JsonWriter out, ConnectionCollection details) throws IOException {
		TypeToken<LinkedHashMap<String, Connection>> typeToken = new TypeToken<LinkedHashMap<String, Connection>>() {
		};
		gson.getDelegateAdapter(parent, typeToken).write(out, details.getConnectionDetails());
	}

	@Override
	public ConnectionCollection read(JsonReader in) throws IOException {
		ConnectionCollection connectionDetails = new ConnectionCollection();
		in.beginObject();
		while (in.hasNext() && in.peek() != JsonToken.END_OBJECT) {
			addDetails(in, connectionDetails);
		}
		in.endObject();

		return connectionDetails;
	}

	public void addDetails(JsonReader in, ConnectionCollection collection) throws IOException {
		String detailsName = in.nextName();
		in.beginObject();
		JsonObject object = new JsonObject();
		while (in.hasNext() && in.peek() != JsonToken.END_OBJECT) {
			String name = in.nextName();
			object.addProperty(name, in.nextString());
		}
		String connectionType = object.get("connectionType").getAsString();
		Connection details = create(registeredExtensions.getConnectionNames(), detailsName, connectionType,
				registeredExtensions.getConnectionTypeForName(connectionType), object);
		details.setConnectionType(connectionType);
		details.setName(detailsName);
		in.endObject();
		collection.put(detailsName, details);
	}
	
	private Connection create(Set<String> availableDetails, String connectionName,
			String connectionType, Class<? extends Connection> clazz, JsonElement element) {
		if (clazz == null) {
			throw new ConfigurationException("Do not know what type of connection: "
					+ connectionType + " is, for connection:" + connectionName
					+ ". Possible connection types are: " + availableDetails);
		}
		return gson.fromJson(element, clazz);
	}

	@Override
	public <T> boolean supplies(TypeToken<T> type) {
		return ConnectionCollection.class.isAssignableFrom(type.getRawType());
	}

	
}