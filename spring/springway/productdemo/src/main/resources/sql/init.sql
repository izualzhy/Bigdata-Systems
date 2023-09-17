delete from t_product;
delete from T_PURCHASE_RECORD;
INSERT INTO T_Product (id, product_name, stock, price, version, note) VALUES (1, 'Product A', 100, 10.99, 1, 'Note A');
SELECT * FROM T_Product;