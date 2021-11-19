DELETE FROM tmp_order;

INSERT INTO tmp_order (user_id, product_code, count, money) VALUES
('Jone', '11111110', 1, 11.11),
('Jack', '11111110', 4, 44.44),
('Tom', '11111111', 5, 55.00),
('Sandy', '11111112', 2, 22.44),
('Billie', '11111113', 2, 44.88);