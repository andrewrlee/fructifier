[#ftl]

[#list scripts as script]

-- START CHANGE SCRIPT ${script}

${script.content}

INSERT INTO ${connectionDefinition.changeLogTableName} (change_number, change_set, complete_dt, applied_by, description)
 VALUES (${script.delta?c}, '${script.changeSet}', CURRENT_TIMESTAMP, USER(), '${script.resource.description}')${connectionDefinition.separator}${connectionDefinition.delimiter}

COMMIT${connectionDefinition.separator}${connectionDefinition.delimiter}

-- END CHANGE SCRIPT ${script}

[/#list]