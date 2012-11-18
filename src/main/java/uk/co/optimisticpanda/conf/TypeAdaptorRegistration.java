package uk.co.optimisticpanda.conf;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

public abstract class TypeAdaptorRegistration<D> extends TypeAdapter<D> {

	public abstract <T> boolean supplies(TypeToken<T> type);
}