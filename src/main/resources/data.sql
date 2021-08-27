/*Default Roles*/
insert ignore into role (name, type, color)
values ('default', 'ADMIN', '');
insert ignore into role (name, type, color)
values ('default', 'RESELLER', '');
insert ignore into role (name, type, color)
values ('default', 'LINE', '');

/* Default Permissions*/
insert ignore into permission (name, description, user_type) values ('dashboard_page', 'See Dashboard Page information', 'ADMIN');

insert ignore into permission (name, description, user_type) values ('admins_manage', 'Manage Admins', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('admins_add', 'Add Admins', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('admins_edit', 'Edit Admins', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('admins_delete', 'Delete Admins', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('admins_batch_edit', 'Batch Edit Admins', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('admins_batch_delete', 'Batch Delete Admins', 'ADMIN');

insert ignore into permission (name, description, user_type) values ('resellers_manage', 'Manage Resellers', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('resellers_add', 'Add Resellers', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('resellers_edit', 'Edit Resellers', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('resellers_delete', 'Delete Resellers', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('resellers_batch_edit', 'Batch Edit Resellers', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('resellers_batch_delete', 'Batch Delete Resellers', 'ADMIN');

insert ignore into permission (name, description, user_type) values ('roles_manage', 'Manage Roles', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('roles_add', 'Add Roles', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('roles_edit', 'Edit Roles', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('roles_delete', 'Delete Roles', 'ADMIN');

insert ignore into permission (name, description, user_type) values ('lines_manage', 'Manage Lines', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('lines_add', 'Add Lines', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('lines_edit', 'Edit Lines', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('lines_delete', 'Delete Lines', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('lines_batch_edit', 'Batch Edit Lines', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('lines_batch_delete', 'Batch Delete Lines', 'ADMIN');

insert ignore into permission (name, description, user_type) values ('channels_manage', 'Manage Channels', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('channels_add', 'Add Channels', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('channels_edit', 'Edit Channels', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('channels_delete', 'Delete Channels', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('channels_batch_edit', 'Batch Edit Channels', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('channels_batch_delete', 'Batch Delete Channels', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('channels_import', 'Import Channels', 'ADMIN');

insert ignore into permission (name, description, user_type) values ('movies_manage', 'Manage Movies', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('movies_add', 'Add Movies', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('movies_edit', 'Edit Movies', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('movies_delete', 'Delete Movies', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('movies_batch_edit', 'Batch Edit Movies', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('movies_batch_delete', 'Batch Delete Movies', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('movies_import', 'Import Movies', 'ADMIN');

insert ignore into permission (name, description, user_type) values ('series_manage', 'Manage Series', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('series_add', 'Add Series', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('series_edit', 'Edit Series', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('series_delete', 'Delete Series', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('series_batch_edit', 'Batch Edit Series', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('series_batch_delete', 'Batch Delete Series', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('series_import', 'Import Series', 'ADMIN');

insert ignore into permission (name, description, user_type) values ('add_channel', 'Add Channel', 'ADMIN');
insert ignore into permission (name, description, user_type) values ('delete_channel', 'Delete Channel', 'ADMIN');

insert ignore into setting (id, value) value ('backup_interval', '')