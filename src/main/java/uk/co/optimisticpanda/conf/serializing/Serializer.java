package uk.co.optimisticpanda.conf.serializing;

import java.util.List;

import org.springframework.core.io.ResourceLoader;

import uk.co.optimisticpanda.conf.RunningOrder;
import uk.co.optimisticpanda.conf.TypeAdaptorRegistration;
import uk.co.optimisticpanda.runner.RegisteredExtensionsGatherer;

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
public class Serializer implements TypeAdapterFactory {

	private final Gson gson;
	private final List<TypeAdaptorRegistration<?>> adapters = Lists.newArrayList();

	public Serializer(ResourceLoader resourceLoader, RegisteredExtensionsGatherer registeredExtensions) {
		adapters.addAll(registeredExtensions.getAdaptorList());
		adapters.addAll(defaultTypeAdaptors(resourceLoader, registeredExtensions));
		this.gson = new GsonBuilder().registerTypeAdapterFactory(this).create();
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

	public <D> D parse(String json, Class<D> clazz) {
		return gson.fromJson(json, clazz);
	}

	public <D> String toString(D object) {
		return gson.toJson(object);
	}
	
	@SuppressWarnings("unchecked")
	private List<TypeAdaptorRegistration<?>> defaultTypeAdaptors(ResourceLoader resourceLoader, RegisteredExtensionsGatherer registeredExtensions) {
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
