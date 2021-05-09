/*Default Roles*/
insert ignore into role (name, type, color)
values ('default', 'ADMIN', '');
insert ignore into role (name, type, color)
values ('default', 'RESELLER', '');
insert ignore into role (name, type, color)
values ('default', 'LINE', '');

/* Default Permissions*/
insert ignore into permission (name, description, user_type)
values ('manage_lines', 'Manage Lines', 'ADMIN');
insert ignore into permission (name, description, user_type)
values ('add_channel', 'Add Channel', 'ADMIN');
insert ignore into permission (name, description, user_type)
values ('delete_channel', 'Delete Channel', 'ADMIN');
