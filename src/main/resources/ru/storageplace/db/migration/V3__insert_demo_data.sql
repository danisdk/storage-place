INSERT INTO warehouse (name, address, description)
VALUES
    ('Основной склад', 'г. Москва, ул. Складская, д. 1', 'Демонстрационный склад для проверки работы программы');

INSERT INTO product (name, article, length_cm, width_cm, height_cm, weight_kg)
VALUES
    ('Картонная коробка', 'BOX-001', 40.000, 30.000, 20.000, 2.500)
    ON CONFLICT (article) DO NOTHING;

INSERT INTO storage_place (
    warehouse_id,
    type_id,
    number,
    length_cm,
    width_cm,
    height_cm,
    max_weight_kg
)
SELECT
    w.id,
    t.id,
    '1',
    120.000,
    80.000,
    100.000,
    500.000
FROM warehouse w
         JOIN storage_place_type t ON t.code = 'P'
WHERE w.name = 'Основной склад'
    ON CONFLICT (warehouse_id, type_id, number) DO NOTHING;

INSERT INTO storage_place (
    warehouse_id,
    type_id,
    number,
    length_cm,
    width_cm,
    height_cm,
    max_weight_kg
)
SELECT
    w.id,
    t.id,
    '2',
    120.000,
    80.000,
    100.000,
    500.000
FROM warehouse w
         JOIN storage_place_type t ON t.code = 'P'
WHERE w.name = 'Основной склад'
    ON CONFLICT (warehouse_id, type_id, number) DO NOTHING;

INSERT INTO storage_place_state (storage_place_id)
SELECT sp.id
FROM storage_place sp
         JOIN warehouse w ON w.id = sp.warehouse_id
         JOIN storage_place_type t ON t.id = sp.type_id
WHERE w.name = 'Основной склад'
  AND t.code = 'P'
  AND sp.number IN ('1', '2')
    ON CONFLICT (storage_place_id) DO NOTHING;