fructifier
==========

Toy project. Will run a series of phases.

Features:
---------
* Easy to create custom actions (phases)
* Run arbitrary phases in any order or a "profile" - a list of pre-specified phases. 
* Property overrides - using spring style placeholders
* [Spring resources](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/resources.html) allows picking up property files from arbitray locations. Classpath loading allows all-in-one executable jars
* Command line runner.
* Has the following phases pre-packaged:
    * Incremental Database phase
    * Run arbitrary SQL script

Command line options:
--------------------
show profiles  - List each profile and the list of phases it contains. 
show phases    - List the name of each phase and the implementing class
show build     - 
show help

Example config:
---------------

```json
{
    "profiles": {
        "rebuild": [ "createDatabase", "applyDeltas" ],
        "update":  [ "applyDeltas" ]
    },
    "connections": {
        "root": {
            "connectionType": "database",
            "driver": "com.mysql.jdbc.Driver",
            "connectionUrl" : "jdbc:mysql://localhost:3306/", "dbms": "mysql", 
            "user": "root", "password": ""
        },
        "app_user": {
            "connectionType": "database",
            "driver": "com.mysql.jdbc.Driver",
            "connectionUrl" : "jdbc:mysql://localhost:3306/bar", "dbms": "mysql", 
            "user": "foo", "password": "foo",
            "separator" : "\n" , "delimiter" : ";"
        }
    },
    "phases": {
        "createDatabase": {
            "phaseType": "database.single.script.phase",
            "connection" : "root", 
            "script" : "classpath:incremental.database.scripts/${create.script}",
            "data": {
                "app_user": "foo",
                "app_password": "foo",
                "database" : "bar",
                "host" : "localhost"
            }
        },
        "applyDeltas": {
            "phaseType": "database.incremental.phase",
            "connection" : "app_user", 
            "deltaDir" : "classpath:incremental.database.scripts/${deltas}"
        }
    }
}
```

Example extension:
------------------

All RegisterExtensions will be picked up by an initializing bean and registered with the runner.  
All phases in the extension will then be available for use if the phaseType specified in the RegisteredComponent is specified in the config.yml.

```java
@Configuration
public class DatabaseConfiguration {

    @Autowired
    private RunningOrder runningOrder;

    @SuppressWarnings("unchecked")
    @Bean
    public RegisterExtension registerDatabaseExtension() {
        return new RegisterExtension()
                    .connectionTypes(
                        new RegisteredComponent<ConnectionDefinition>("database", DatabaseConnectionDefinition.class)
                    )
                    .typeAdaptors(
                        new DelimiterLocationTypeAdaptor()
                        )
                    .phaseTypes(
                        new RegisteredComponent<Phase>("database.incremental.phase", IncrementalDatabasePhase.class),
                        new RegisteredComponent<Phase>("database.single.script.phase", SingleScriptDatabasePhase.class)
                    );
    }

    @Bean
    public JdbcConnectionProvider jdbcProviders() {
        return new JdbcConnectionProvider(runningOrder.getConnections());
    }

    @Bean
    public ScriptApplier scriptApplier() {
        return new ScriptApplier();
	}
	
	@Bean
	public DatabaseApplier upgradeApplier() {
		return new DatabaseApplier();
	}

	@Bean
	public TemplateApplier templateApplier(){
		return new TemplateApplier();
	} 
	
}
```

