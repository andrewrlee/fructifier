package uk.co.optimisticpanda.config.db.apply;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;

import uk.co.optimisticpanda.config.db.DatabasePhase;
import uk.co.optimisticpanda.util.TemplateApplier;

import com.google.common.collect.Maps;

public class ScriptApplier {

	@Autowired
	private ResourceLoader resourceLoader;

	private final TemplateApplier applier;

	public ScriptApplier() {
		this.applier = new TemplateApplier();
	}

	public void applyScript(DatabasePhase phase, String resourceName) {
		Resource resource = resourceLoader.getResource(resourceName);

		applier.addTemplate(resourceName, resource);

		HashMap<String, Object> model = Maps.newHashMap();
		model.put("phase", phase);

		StringWriter writer = new StringWriter();
		applier.apply(resourceName, writer, model);

		for (final String query : phase.getQueryExtractor().getQueries(writer.toString())) {
			phase.getJdbcTemplate().execute(new ConnectionCallback<Void>() {
				public Void doInConnection(Connection con) throws SQLException, DataAccessException {
					Statement statement = con.createStatement();
					statement.execute(query);
					return null;
				}
			});
		}
	}

}
