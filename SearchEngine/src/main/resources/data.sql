INSERT INTO field (id, name, selector, weight) values (1, "head", "head", 1.0) on duplicate key update name = "head";
INSERT INTO field (id, name, selector, weight) values (2, "body", "body", 0.8) on duplicate key update name = "body";
