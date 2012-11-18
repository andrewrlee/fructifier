package uk.co.optimisticpanda.conf.serializing;

import java.util.List;

import org.springframework.core.io.ResourceLoader;

import uk.co.optimisticpanda.conf.PhaseCollection;
import uk.co.optimisticpanda.conf.RunningOrder;
import uk.co.optimisticpanda.conf.TypeAdaptorRegistration;
import uk.co.optimisticpanda.db.conf.DatabasePhase;
import uk.co.optimisticpanda.runner.RegisteredExtensions;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

/**
 * The main serializer that is responsible for parsing a json configuration into
 * a {@link RunningOrder}.
 */
public class Serializer {

	private final Gson gson;

	public Serializer(ResourceLoader resourceLoader, RegisteredExtensions registeredExtensions) {
		this.gson = new GsonBuilder().registerTypeAdapterFactory(new AdapterFactory(resourceLoader, registeredExtensions)).create();
	}

	public RunningOrder parseRunningOrder(String json) {
		return gson.fromJson(json, RunningOrder.class);
	}

	public PhaseCollection parsePhases(String json) {
		return gson.fromJson(json, PhaseCollection.class);
	}

	public String toString(DatabasePhase phase) {
		return gson.toJson(phase);
	}

	public String toString(PhaseCollection phases) {
		return gson.toJson(phases);
	}

	public String toString(RunningOrder config) {
		return gson.toJson(config);
	}

	public static class AdapterFactory implements TypeAdapterFactory {

		private final List<TypeAdaptorRegistration<?>> adapters = Lists.newArrayList();

		public AdapterFactory(ResourceLoader resourceLoader, RegisteredExtensions registeredExtensions) {
			adapters.addAll(registeredExtensions.getAdaptorList());
			adapters.addAll(defaultTypeAdaptors(resourceLoader, registeredExtensions));
		}

		@SuppressWarnings("unchecked")
		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
			for (TypeAdaptorRegistration<?> typeAdaptor : adapters) {
				if (typeAdaptor.supplies(type)) {
					return (TypeAdapter<T>) typeAdaptor.preparedAdapter(gson, this);
				}
			}
			return null;
		}
		
		@SuppressWarnings("unchecked")
		private List<TypeAdaptorRegistration<?>> defaultTypeAdaptors(ResourceLoader resourceLoader, RegisteredExtensions registeredExtensions){
			return Lists.newArrayList( //
					new PhaseTypeAdaptor(registeredExtensions), //
					new PhaseCollectionTypeAdaptor(), //
					new SimpleTypeAdaptors.OptionalTypeAdaptor(), //
					new SimpleTypeAdaptors.OptionalFileTypeAdaptor(), //
					new SimpleTypeAdaptors.FileTypeAdaptor(),//
					new SimpleTypeAdaptors.ResourceTypeAdaptor(resourceLoader),//
					new SimpleTypeAdaptors.OptionalResourceTypeAdaptor(resourceLoader),//
					new ConnectionCollectionTypeAdaptor(registeredExtensions));
		}
	}
	
}
