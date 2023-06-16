INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');

INSERT INTO user_profiles (email, user_name, password, full_name, phone, info, user_status) VALUES ('admin@gmail.com','admin','$2a$12$.1zE7l.WdGRd8CqDiFkILuxyP.xHHGVsthhEfaRZV/gZwWsycuV7S', 'админ', '+7954439324', '-', 'none');
INSERT INTO user_profiles (email, user_name, password, full_name, phone, info, user_status) VALUES ('moderator@mail.ru','moderator','$2a$12$Ciow2WbWPWCv1ICIiga7ZOmfzDY4Qaey8.GzWwceTnm2/.0zkvC7W', 'модератор', '+7954549324', '-', 'none');
INSERT INTO user_profiles (email, user_name, password, full_name, phone, info, user_status) VALUES ('testuser@gmail.com','testuser','$2a$12$d1lleq2uZCemgyNCJ3BfR.cU8S6WKnCG7lj.JDRmzg14FC1odx6Ee', 'тестовый пользователь', '+79867439324', 'Это тестовый пользователь', 'invited');

INSERT INTO user_roles (user_id, role_id) VALUES ('1','3');
INSERT INTO user_roles (user_id, role_id) VALUES ('2','2');
INSERT INTO user_roles (user_id, role_id) VALUES ('3','1');

INSERT INTO topics(name) VALUES ('Языки программирования');
INSERT INTO topics(name) VALUES ('Алгоритмы');

INSERT INTO task_difficulties(name) VALUES ('джун');
INSERT INTO task_difficulties(name) VALUES ('мидл');
INSERT INTO task_difficulties(name) VALUES ('сениор');

INSERT INTO user_profiles (email, user_name, password, full_name, phone, info, user_status) VALUES ('yak88@mail.ru','ERT5fgd','$2a$12$d1lleq2uZCemgyNCJ3BfR.cU8S6WKnCG7lj.JDRmzg14FC1odx6Ee', 'Яковлев Борис Геннадьевич', '+79843564364', 'JS разработчик, опыт 1 год', 'invited');
INSERT INTO user_profiles (email, user_name, password, full_name, phone, info, user_status) VALUES ('shanna93@gmail.com','dfgtfgg','$2a$12$d1lleq2uZCemgyNCJ3BfR.cU8S6WKnCG7lj.JDRmzg14FC1odx6Ee', 'Снежинская Жанна Олеговна', '+793537355', 'Закончила курсы Skillbox по Java, без опыта работы', 'invited');
INSERT INTO user_profiles (email, user_name, password, full_name, phone, info, user_status) VALUES ('petrov_AI_79@rambler.ru','sdrhwer','$2a$12$d1lleq2uZCemgyNCJ3BfR.cU8S6WKnCG7lj.JDRmzg14FC1odx6Ee', 'Петров Антон Игоречив', '+798583454', 'Программист Python, опыт 2 года', 'started');
INSERT INTO user_profiles (email, user_name, password, full_name, phone, info, user_status, last_activity) VALUES ('nesterov95@mail.ru','iertrf','$2a$12$d1lleq2uZCemgyNCJ3BfR.cU8S6WKnCG7lj.JDRmzg14FC1odx6Ee', 'Нестеров Илья Владимирович', '+79234234', 'SQL разработчик, опыт работы 2 года', 'submitted', '2022-06-22 12:10:25-07');
INSERT INTO user_profiles (email, user_name, password, full_name, phone, info, user_status, last_activity) VALUES ('natasha99@yandex.ru','wfwnff','$2a$12$d1lleq2uZCemgyNCJ3BfR.cU8S6WKnCG7lj.JDRmzg14FC1odx6Ee', 'Щербакова Наталья Викторовна', '+798327458', 'Программист C++ , студенка 3го курса ЛЭТИ, без опыта работы', 'submitted','2022-05-13 21:10:25-07');
INSERT INTO user_profiles (email, user_name, password, full_name, phone, info, user_status, last_activity) VALUES ('EM_panchenko@gmail.com','lwerjtwe','$2a$12$d1lleq2uZCemgyNCJ3BfR.cU8S6WKnCG7lj.JDRmzg14FC1odx6Ee', 'Панченко Екатерина Михайловна', '+79048523', 'Программист C#, опыт 2 года', 'submitted', '2022-06-22 21:10:25-07');
INSERT INTO user_profiles (email, user_name, password, full_name, phone, info, user_status, last_activity) VALUES ('maLmixail91@mail.ru','retjref','$2a$12$d1lleq2uZCemgyNCJ3BfR.cU8S6WKnCG7lj.JDRmzg14FC1odx6Ee', 'Ардеев Михаил Антонович', '+7993482347', 'Java разработчик, диплом бакалавра СПбПУ, опыта работы нет', 'submitted', '2022-07-22 19:10:25-07' );
INSERT INTO user_profiles (email, user_name, password, full_name, phone, info, user_status) VALUES ('eduard85@mail.ru','wqebhwqe','$2a$12$d1lleq2uZCemgyNCJ3BfR.cU8S6WKnCG7lj.JDRmzg14FC1odx6Ee', 'Шепелев Эдуард Владимирович', '+7923875532', 'JavaScript разработчик, удаленная работа, диплом бакалавра ТЮМГУ, опыта работы нет', 'started');
INSERT INTO user_profiles (email, user_name, password, full_name, phone, info, user_status, last_activity) VALUES ('tamara_shmakova@yandex.ru','weuirfg','$2a$12$d1lleq2uZCemgyNCJ3BfR.cU8S6WKnCG7lj.JDRmzg14FC1odx6Ee', 'Шмакова Тамара Владимировна', '+7953765365', 'React JS, опыт 5 лет', 'approved', '2021-06-22 19:10:25-07');
INSERT INTO user_profiles (email, user_name, password, full_name, phone, info, user_status, last_activity) VALUES ('ardeev98@gmail.com','vmdfgjdfhg','$2a$12$d1lleq2uZCemgyNCJ3BfR.cU8S6WKnCG7lj.JDRmzg14FC1odx6Ee', 'Михалков Сергей Романович', '+72345345', 'Закончил курсы Нетология по Python, без опыта работы', 'rejected', '2021-07-22 19:10:25-07');

INSERT INTO tasks (name, description, difficulty_id, topic_id, language_id, language_name, deleted) VALUES ('Упражнение на полиморфизм 3', 'Напишите программу Java для'
|| 'создания базового класса Animal (семейство животных) с помощью метода Sound(). Создайте два подкласса Bird и Cat. Переопределите метод Sound() в каждом подклассе,'
|| 'чтобы издавать определенный звук для каждого животного.', 1, 1, 1, 'Java', false);
INSERT INTO tasks (name, description, difficulty_id, topic_id, language_id, language_name, deleted) VALUES ('Римские цифры в арабские','Римские цифры представлены семью'
|| 'различными символами: I, V, X, L, C, D и M. Символ: I, V, X, L, C, D, M Значение: 1, 5, 10, 50, 100, 500, 1000 Для Например, 2 записывается как II римскими цифрами,'
|| 'просто две сложенные вместе. 12 записывается как XII, то есть просто X + II. Число 27 записывается как XXVII, то есть XX + V + II. Римские цифры обычно пишутся слева'
|| 'направо от большего к меньшему. Однако цифра четыре не IIII. Вместо этого цифра четыре пишется как IV. Так как единица предшествует пятерке, мы вычитаем ее и получаем'
|| 'четыре. Тот же принцип применим к числу девять, которое пишется как IX. Вычитание используется в шести случаях: I можно поставить перед V (5) и X (10), чтобы получить'
|| '4 и 9. X можно поставить перед L (50) и C (100), чтобы получить 40 и 90. C можно поставить перед D (500) и M (1000), чтобы получить 400 и 900. Дана римская цифра,'
|| 'преобразовать ее в целое число.', 1, 1, 1, 'Java', false);

INSERT INTO task_unit_tests (task_id, code) VALUES (1,'aW1wb3J0IHN0YXRpYyBvcmcuanVuaXQuanVwaXRlci5hcGkuQXNzZXJ0aW9ucy5hc3NlcnRUcnVlOw0KaW1wb3J0IHN0YXRpYyBvcmcuanVuaXQua'
|| 'nVwaXRlci5hcGkuQXNzZXJ0aW9ucy5hc3NlcnRUaW1lb3V0UHJlZW1wdGl2ZWx5Ow0KDQppbXBvcnQgamF2YS50aW1lLkR1cmF0aW9uOw0KaW1wb3J0IG9yZy5qdW5pdC5qdXBpdGVyLmFwaS5UZXN0Ow0KDQoNCnB1Y'
|| 'mxpYyBjbGFzcyBUZXN0MSB7DQoJDQogICAgQFRlc3QNCiAgICBwdWJsaWMgdm9pZCBzaG91bGRBbnN3ZXJXaXRoVHJ1ZSgpDQogICAgew0KICAgCQ0KICAgICAgICBBbmltYWwgYW5pbWFsID0gbmV3IEFuaW1hbCgpO'
|| 'w0KICAgICAgICBCaXJkIGJpcmQgPSBuZXcgQmlyZCgpOw0KICAgICAgICBDYXQgY2F0ID0gbmV3IENhdCgpOw0KDQogICAgICAgIGFzc2VydFRpbWVvdXRQcmVlbXB0aXZlbHkoRHVyYXRpb24ub2ZTZWNvbmRzKDMpL'
|| 'CAoKSAtPiB7DQogICAgICAgICAgICBhc3NlcnRUcnVlKGFuaW1hbC5tYWtlU291bmQoKT09IlRoZSBhbmltYWwgbWFrZXMgYSBzb3VuZCIpOw0KICAgICAgICAgICAgYXNzZXJ0VHJ1ZShiaXJkLm1ha2VTb3VuZCgpP'
|| 'T0iVGhlIGJpcmQgY2hpcnBzIik7DQogICAgICAgICAgICBhc3NlcnRUcnVlKGNhdC5tYWtlU291bmQoKT09IlRoZSBjYXQgbWVvd3MiKTsNCiAgICAgICAgfSk7DQogICAgICAgIA0KICAgIH0NCg0KfQ==');
INSERT INTO task_unit_tests (task_id, code) VALUES (1,'aW1wb3J0IG9yZy5qdW5pdC5qdXBpdGVyLmFwaS5UZXN0Ow0KDQppbXBvcnQgc3RhdGljIG9yZy5qdW5pdC5qdXBpdGVyLmFwaS5Bc3NlcnRpb25zL'
|| 'mFzc2VydFRpbWVvdXRQcmVlbXB0aXZlbHk7DQppbXBvcnQgc3RhdGljIG9yZy5qdW5pdC5qdXBpdGVyLmFwaS5Bc3NlcnRpb25zLmFzc2VydFRydWU7DQoNCmltcG9ydCBqYXZhLnRpbWUuRHVyYXRpb247DQoNCnB1Y'
|| 'mxpYyBjbGFzcyBUZXN0MiB7DQoJDQoJQFRlc3QNCglwdWJsaWMgdm9pZCBzaW1wbGUoKSB7DQoJCWFzc2VydFRpbWVvdXRQcmVlbXB0aXZlbHkoRHVyYXRpb24ub2ZTZWNvbmRzKDMpLCAoKSAtPiB7DQoJCQlhc3Nlc'
|| 'nRUcnVlKHRydWUpOw0KCQl9KTsNCgl9DQoJDQoJQFRlc3QNCglwdWJsaWMgdm9pZCBzaW1wbGUyKCkgewkJDQoJCWFzc2VydFRpbWVvdXRQcmVlbXB0aXZlbHkoRHVyYXRpb24ub2ZTZWNvbmRzKDMpLCAoKSAtPiB7D'
|| 'QoJCQlhc3NlcnRUcnVlKHRydWUpOw0KCQl9KTsNCgl9DQoNCn0=');
INSERT INTO task_unit_tests (task_id, code) VALUES (2, 'aW1wb3J0IHN0YXRpYyBvcmcuanVuaXQuanVwaXRlci5hcGkuQXNzZXJ0aW9ucy5hc3NlcnRUaW1lb3V0UHJlZW1wdGl2ZWx5Ow0KaW1wb3J0IHN0'
|| 'YXRpYyBvcmcuanVuaXQuanVwaXRlci5hcGkuQXNzZXJ0aW9ucy5hc3NlcnRUcnVlOw0KDQppbXBvcnQgamF2YS50aW1lLkR1cmF0aW9uOw0KaW1wb3J0IG9yZy5qdW5pdC5qdXBpdGVyLmFwaS5UZXN0Ow0KDQpwdWJs'
|| 'aWMgY2xhc3MgVGVzdDEgew0KCQ0KICAgIEBUZXN0DQogICAgcHVibGljIHZvaWQgY2hlY2tPbmUoKQ0KICAgIHsNCiAgICAgICAgYXNzZXJ0VGltZW91dFByZWVtcHRpdmVseShEdXJhdGlvbi5vZlNlY29uZHMoMyks'
|| 'ICgpIC0+IHsNCgkgICAgCVNvbHV0aW9uIHNvbCA9IG5ldyBTb2x1dGlvbigpOw0KCSAgICAgICAgYXNzZXJ0VHJ1ZSggc29sLnJvbWFuVG9JbnQoIklJSSIpPT0zICk7DQogICAgICAgIH0pOw0KICAgIH0NCiAgICAN'
|| 'CiAgICBAVGVzdA0KICAgIHB1YmxpYyB2b2lkIGNoZWNrVHdvKCkNCiAgICB7DQogICAgCWFzc2VydFRpbWVvdXRQcmVlbXB0aXZlbHkoRHVyYXRpb24ub2ZTZWNvbmRzKDMpLCAoKSAtPiB7DQoJICAgIAlTb2x1dGlv'
|| 'biBzb2wgPSBuZXcgU29sdXRpb24oKTsNCgkgICAgICAgIGFzc2VydFRydWUoIHNvbC5yb21hblRvSW50KCJMVklJSSIpPT01OCApOw0KICAgIAl9KTsNCiAgICB9DQogICAgDQogICAgQFRlc3QNCiAgICBwdWJsaWMg'
|| 'dm9pZCBjaGVja1RocmVlKCkNCiAgICB7DQogICAgCWFzc2VydFRpbWVvdXRQcmVlbXB0aXZlbHkoRHVyYXRpb24ub2ZTZWNvbmRzKDMpLCAoKSAtPiB7DQoJICAgIAlTb2x1dGlvbiBzb2wgPSBuZXcgU29sdXRpb24o'
|| 'KTsNCgkgICAgICAgIGFzc2VydFRydWUoIHNvbC5yb21hblRvSW50KCJNQ01YQ0lWIik9PTE5OTQgKTsNCiAgICAJfSk7DQogICAgfQ0KDQp9');

INSERT INTO task_ref_solutions (task_id, code) VALUES (1, 'cHVibGljIGNsYXNzIEFuaW1hbCB7DQoJDQogICAgcHVibGljIFN0cmluZyBtYWtlU291bmQoKSB7DQogICAgICAgIHJldHVybiAiVGhlIG'
|| 'FuaW1hbCBtYWtlcyBhIHNvdW5kIjsNCiAgICB9DQoNCn0=');
INSERT INTO task_ref_solutions (task_id, code) VALUES (1, 'cHVibGljIGNsYXNzIEJpcmQgZXh0ZW5kcyBBbmltYWwgew0KICAgIEBPdmVycmlkZQ0KICAgIHB1YmxpYyBTdHJpbmcgbWFrZVNvdW5kKC'
|| 'kgew0KICAgICAgICByZXR1cm4gIlRoZSBiaXJkIGNoaXJwcyI7DQogICAgfQ0KDQp9');
INSERT INTO task_ref_solutions (task_id, code) VALUES (1, 'cHVibGljIGNsYXNzIENhdCBleHRlbmRzIEFuaW1hbCB7DQoNCiAgICBAT3ZlcnJpZGUNCiAgICBwdWJsaWMgU3RyaW5nIG1ha2VTb3VuZC'
|| 'gpIHsNCiAgICAgICAgcmV0dXJuICJUaGUgY2F0IG1lb3dzIjsNCiAgICB9DQp9');
INSERT INTO task_ref_solutions (task_id, code) VALUES (2, 'cHVibGljIGNsYXNzIFNvbHV0aW9uIHsNCgkNCgkgcHVibGljIGludCByb21hblRvSW50KFN0cmluZyBzKSB7DQogICAgICAgICBpbnQgYW'
|| '5zID0gMCwgbnVtID0gMDsNCiAgICAgICAgZm9yIChpbnQgaSA9IHMubGVuZ3RoKCktMTsgaSA+PSAwOyBpLS0pIHsNCiAgICAgICAgICAgIHN3aXRjaChzLmNoYXJBdChpKSkgew0KICAgICAgICAgICAgICAgIGN'
|| 'hc2UgJ0knOiBudW0gPSAxOyBicmVhazsNCiAgICAgICAgICAgICAgICBjYXNlICdWJzogbnVtID0gNTsgYnJlYWs7DQogICAgICAgICAgICAgICAgY2FzZSAnWCc6IG51bSA9IDEwOyBicmVhazsNCiAgICAgICAg'
|| 'ICAgICAgICBjYXNlICdMJzogbnVtID0gNTA7IGJyZWFrOw0KICAgICAgICAgICAgICAgIGNhc2UgJ0MnOiBudW0gPSAxMDA7IGJyZWFrOw0KICAgICAgICAgICAgICAgIGNhc2UgJ0QnOiBudW0gPSA1MDA7IGJyZ'
|| 'WFrOw0KICAgICAgICAgICAgICAgIGNhc2UgJ00nOiBudW0gPSAxMDAwOyBicmVhazsNCiAgICAgICAgICAgIH0NCiAgICAgICAgICAgIGlmICg0ICogbnVtIDwgYW5zKSBhbnMgLT0gbnVtOw0KICAgICAgICAgIC'
|| 'AgZWxzZSBhbnMgKz0gbnVtOw0KICAgICAgICB9DQogICAgICAgIHJldHVybiBhbnM7DQogICAgfQ0KDQp9');
