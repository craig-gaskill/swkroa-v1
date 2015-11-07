CREATE TABLE document (
  document_id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  parent_entity_id       BIGINT UNSIGNED NULL,
  parent_entity_name     VARCHAR(25) NULL,
  document_type          VARCHAR(25) NOT NULL COMMENT 'The type of document: Invoice, Newsletter, etc.',
  document_format        VARCHAR(5) NOT NULL COMMENT 'The format of the content: XML, PDF, etc.',
  document_location      VARCHAR(256) NULL,
  document_content       MEDIUMBLOB NULL,
  document_desc          VARCHAR(150) NULL,
  beg_eff_dt             DATETIME NOT NULL,
  end_eff_dt             DATETIME NULL,
  active_ind             BOOLEAN DEFAULT 1 NOT NULL,
  create_dt_tm           DATETIME NOT NULL,
  create_id              BIGINT UNSIGNED NOT NULL,
  updt_dt_tm             DATETIME NOT NULL,
  updt_id                BIGINT UNSIGNED NOT NULL,
  updt_cnt               INT UNSIGNED DEFAULT 0 NOT NULL,
  CONSTRAINT document_pk PRIMARY KEY (document_id),
  INDEX document_idx1 (beg_eff_dt, document_type),
  INDEX document_idx2 (parent_entity_name, parent_entity_id)
) ENGINE = InnoDB;
