package uk.co.optimisticpanda.config.serializing;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uk.co.optimisticpanda.conf.ConnectionCollection;
import uk.co.optimisticpanda.conf.Phase;
import uk.co.optimisticpanda.conf.PhaseCollection;
import uk.co.optimisticpanda.conf.RunningOrder;
import uk.co.optimisticpanda.conf.TypeAdaptorRegistration;
import uk.co.optimisticpanda.config.db.DatabasePhase;
import uk.co.optimisticpanda.config.serializing.typeadaptors.ConnectionCollectionTypeAdaptor;
import uk.co.optimisticpanda.config.serializing.typeadaptors.PhaseCollectionTypeAdaptor;
import uk.co.optimisticpanda.config.serializing.typeadaptors.PhaseTypeAdaptor;
import uk.co.optimisticpanda.config.serializing.typeadaptors.SimpleTypeAdaptors;
import uk.co.optimisticpanda.config.serializing.typeadaptors.SimpleTypeAdaptors.FileTypeAdaptor;
import uk.co.optimisticpanda.runner.RegisteredExtensions;

import com.google.common.base.Optional;
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

	public Serializer(RegisteredExtensions registeredExtensions) {
		this.gson = new GsonBuilder().registerTypeAdapterFactory(new AdapterFactory(registeredExtensions)).create();
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

	private class AdapterFactory implements TypeAdapterFactory {

		private final RegisteredExtensions registeredExtensions;

		public AdapterFactory(RegisteredExtensions registeredExtensions) {
			this.registeredExtensions = registeredExtensions;
		}

		@SuppressWarnings("unchecked")
		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
			Class<? super T> t = type.getRawType();

			for (TypeAdaptorRegistration<?> typeAdaptor : registeredExtensions.getAdaptorList()) {
				if (typeAdaptor.supplies(type)) {
					return (TypeAdapter<T>) typeAdaptor;
				}
			}

			//DONE
			if (Phase.class.isAssignableFrom(t)) {
				return (TypeAdapter<T>) new PhaseTypeAdaptor(gson, this, registeredExtensions);
			}
			
			//DONE
			if (PhaseCollection.class.isAssignableFrom(t)) {
				return (TypeAdapter<T>) new PhaseCollectionTypeAdaptor(gson);
			}

//			//DONE
//			TypeToken<Optional<String>> optionalStringToken = new TypeToken<Optional<String>>() {
//			};
//			if (type.equals(optionalStringToken)) {
//				return (TypeAdapter<T>) new SimpleTypeAdaptors.OptionalTypeAdaptor();
//			}

//			//DONE
//			TypeToken<Optional<File>> optionalFileToken = new TypeToken<Optional<File>>() {
//			};
//			if (type.equals(optionalFileToken)) {
//				return (TypeAdapter<T>) new SimpleTypeAdaptors.OptionalFileTypeAdaptor();
//			}
//			//DONE
//			if (File.class.isAssignableFrom(t)) {
//				return (TypeAdapter<T>) new SimpleTypeAdaptors.FileTypeAdaptor();
//			}

			//DONE
			if (ConnectionCollection.class.isAssignableFrom(t)) {
				return (TypeAdapter<T>) new ConnectionCollectionTypeAdaptor(gson, this, registeredExtensions);
			}
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<TypeAdaptorRegistration<? extends Serializable>> defaultTypeAdaptors(){
		return Lists.newArrayList( //
				new SimpleTypeAdaptors.FileTypeAdaptor(),//
				new SimpleTypeAdaptors.OptionalTypeAdaptor(), //
				new SimpleTypeAdaptors.OptionalFileTypeAdaptor());
	}
	
}
