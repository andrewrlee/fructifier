package uk.co.optimisticpanda.conf;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

public abstract class TypeAdaptorRegistration<D> extends TypeAdapter<D> {

	protected Gson gson;
	protected TypeAdapterFactory parent;

	public abstract <T> boolean supplies(TypeToken<T> type);
	
	public TypeAdaptorRegistration<D> preparedAdapter(Gson gson, TypeAdapterFactory parent){
		this.gson = gson;
		this.parent = parent;
		return this;
	}
}