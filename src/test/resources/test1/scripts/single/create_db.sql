
drop database if exists ${phase.data.database}; 

create database ${phase.data.database}; 

-- Drop user (Grant to create user if not exists)
GRANT USAGE ON *.* TO '${phase.data.app_user}'@'${phase.data.host}';
DROP USER '${phase.data.app_user}'@'${phase.data.host}';

create user '${phase.data.app_user}'@'${phase.data.host}' identified by '${phase.data.app_password}'; 

grant all on ${phase.data.database}.* TO '${phase.data.app_user}'@'${phase.data.host}'; 
  
USE ${phase.data.database};

CREATE TABLE ${phase.connection.changeLogTableName}(
  change_number BIGINT NOT NULL,
  change_set VARCHAR(100) NOT NULL,
  complete_dt TIMESTAMP NOT NULL,
  applied_by VARCHAR(100) NOT NULL,
  description VARCHAR(500) NOT NULL
);

ALTER TABLE changelog ADD CONSTRAINT Pkchangelog PRIMARY KEY (change_number);

