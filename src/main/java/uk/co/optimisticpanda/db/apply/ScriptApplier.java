package uk.co.optimisticpanda.db.apply;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;

import uk.co.optimisticpanda.db.conf.DatabasePhase;
import uk.co.optimisticpanda.util.TemplateApplier;

import com.google.common.collect.Maps;

public class ScriptApplier {

	@Autowired
	private TemplateApplier applier;

	public void applyScript(DatabasePhase phase, Resource resource) {

		applier.addTemplate(resource.getFilename(), resource);

		HashMap<String, Object> model = Maps.newHashMap();
		model.put("phase", phase);

		StringWriter writer = new StringWriter();
		applier.apply(resource.getFilename(), writer, model);

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
