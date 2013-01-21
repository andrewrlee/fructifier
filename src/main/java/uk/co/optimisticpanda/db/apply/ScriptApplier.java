package uk.co.optimisticpanda.db.apply;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;

import uk.co.optimisticpanda.db.apply.QueryExtractor.QueryVisitor;
import uk.co.optimisticpanda.db.conf.JdbcConnectionProvider.JdbcConnection;
import uk.co.optimisticpanda.util.ReaderWriterProvider;
import uk.co.optimisticpanda.util.TemplateApplier;

import com.google.common.base.Optional;
import com.google.common.io.Closeables;

public class ScriptApplier{

	private static final Logger logger = Logger.getLogger(ScriptApplier.class);

	@Autowired
	private TemplateApplier applier;

	public void applyScript(final JdbcConnection connection, Map<String, Object> model, Resource scriptTemplate, Optional<File> outputFile) {

		applier.addTemplate(scriptTemplate.getFilename(), scriptTemplate);

		logger.info("Applying model: " + model + ", to template: " + scriptTemplate.getFilename());
		ReaderWriterProvider provider = new ReaderWriterProvider(outputFile);
		applier.apply(scriptTemplate.getFilename(), provider.getWriter(), model);
		Closeables.closeQuietly(provider.getWriter());
		
		connection.visitScript(scriptTemplate, provider.getReader(), new QueryVisitor() {
			public void visit(final int count, final String query) {
				connection.execute(queryCallback(count, query));
			}
		});
	}

	private ConnectionCallback<Void> queryCallback(final int count, final String query) {
		return new ConnectionCallback<Void>() {
			public Void doInConnection(Connection con) throws SQLException, DataAccessException {
				logger.info("Executing statement: " + count);
				logger.debug("statement: " + count + ", contents:" + query);
				con.createStatement().execute(query);
				return null;
			}
		};
	}
	
}