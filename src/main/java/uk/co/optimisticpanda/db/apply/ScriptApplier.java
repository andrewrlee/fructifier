package uk.co.optimisticpanda.db.apply;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;

import com.google.common.base.CharMatcher;

import uk.co.optimisticpanda.db.apply.QueryExtractor.QueryVisitor;
import uk.co.optimisticpanda.db.conf.JdbcConnectionProvider.JdbcConnection;
import uk.co.optimisticpanda.util.TemplateApplier;

public class ScriptApplier {

	private static final Logger logger = Logger.getLogger(ScriptApplier.class);
	
	@Autowired
	private TemplateApplier applier;

	public void applyScript(final JdbcConnection connection, Map<String,Object> model, Resource scriptTemplate) {

		applier.addTemplate(scriptTemplate.getFilename(), scriptTemplate);

		StringWriter writer = new StringWriter();
		logger.info("Applying model: " + model + ", to template: " + scriptTemplate.getFilename());
		applier.apply(scriptTemplate.getFilename(), writer, model);
		logger.debug("Replaced file:" + writer.toString());

		final AtomicInteger counter = new AtomicInteger(1);
		connection.visitScript(writer.toString(), new QueryVisitor() {
			public void visit(final int count, final String query) {
				connection.execute(queryCallback(count, query));
				counter.incrementAndGet();
			}
		});
		logger.info("Executed " + counter.get() + " statements");
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