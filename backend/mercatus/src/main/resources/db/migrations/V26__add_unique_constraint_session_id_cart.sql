alter table cart
add constraint uq_session_id unique (session_id);